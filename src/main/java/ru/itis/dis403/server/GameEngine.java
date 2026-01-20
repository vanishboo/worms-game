package ru.itis.dis403.server;

import lombok.Getter;
import ru.itis.dis403.common.protocol.MessageType;
import ru.itis.dis403.common.protocol.Packet;
import ru.itis.dis403.common.protocol.PacketIO;
import ru.itis.dis403.server.manager.*;
import ru.itis.dis403.server.model.*;
import ru.itis.dis403.server.physics.*;
import ru.itis.dis403.server.storage.PlayerStatsManager;
import ru.itis.dis403.server.weapons.ExplosionHandler;

import java.util.List;


@Getter
public class GameEngine {

    // ============ СОСТОЯНИЕ ИГРЫ ============
    private final ServerGameState state;
    private final List<ClientHandler> clients;
    private boolean gameStarted = false;
    private boolean gameRunning = false;

    // ============ СИСТЕМЫ (МЕНЕДЖЕРЫ) ============
    private final PlayerPhysics playerPhysics;
    private final ProjectilePhysics projectilePhysics;
    private final BulletPhysics bulletPhysics;
    private final ExplosionHandler explosionHandler;
    private final TurnManager turnManager;
    private final InputHandler inputHandler;
    private final StateSerializer stateSerializer;
    private final MapVotingManager mapVotingManager;
    private final PlayerStatsManager statsManager;


    public GameEngine(ServerGameState state, List<ClientHandler> clients,
                      PlayerStatsManager statsManager) {
        this.state = state;
        this.clients = clients;
        this.statsManager = statsManager;

        // Инициализация всех систем с существующей ареной
        this.playerPhysics = new PlayerPhysics(state.arena);
        this.projectilePhysics = new ProjectilePhysics(state.arena);
        this.bulletPhysics = new BulletPhysics(state.arena);
        this.explosionHandler = new ExplosionHandler(state.arena);
        this.turnManager = new TurnManager(state, statsManager);
        this.inputHandler = new InputHandler(state, statsManager);
        this.stateSerializer = new StateSerializer(state);
        this.mapVotingManager = new MapVotingManager(state.arena, this::broadcast);
    }


    public void startGame() {
        gameStarted = true;
        gameRunning = true;


        // Создаём игроков
        state.initPlayers();

        // Начинаем первый ход
        turnManager.startGame();
        // Отправляем клиентам начальные данные
        broadcastStartTurn();
        sendMap();
        sendObjects();

        System.out.println("Игра запущена!");
    }


    public void handle(ClientHandler from, Packet packet) {

        if (packet.type == MessageType.LEADERBOARD_REQUEST) {
            try {
                String json = statsManager.getAllPlayersJson();
                PacketIO.write(from.out, new Packet(MessageType.LEADERBOARD_RESPONSE, json));
                System.out.println("Отправлена таблица лидеров игроку " + from.playerId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        int id = from.playerId;

        // Регистрация игрока
        if (packet.type == MessageType.REGISTER_PLAYER) {
            state.setPlayerNickname(id, packet.payload);
            statsManager.getOrCreatePlayer(packet.payload);
            System.out.println("Игрок " + id + " зарегистрирован как: " + packet.payload);
            return;
        }

        // Готовность игрока
        if (packet.type == MessageType.PLAYER_READY) {
            GameServer.setPlayerReady(id);
            return;
        }

        if (packet.type == MessageType.VOTE_MAP) {
            mapVotingManager.handleVote(id, packet.payload);
        }

        // Игровые команды (только во время игры и только для текущего игрока)
        if (!gameStarted) return;
        if (id != state.currentTurn) return;

        switch (packet.type) {
            case MessageType.MOVE:
                inputHandler.handleMove(id, packet.payload);
                break;

            case MessageType.MOVE_STOP:
                inputHandler.handleMoveStop(id);
                break;

            case MessageType.JUMP:
                inputHandler.handleJump(id);
                break;

            case MessageType.SELECT_WEAPON:
                inputHandler.handleSelectWeapon(id, packet.payload);
                break;

            case MessageType.SHOOT:
                handleShot(id, packet.payload);
                break;
        }
    }


    private void handleShot(int id, String data) {
        try {
            String[] parts = data.split(" ");
            double dx = Double.parseDouble(parts[0]);
            double dy = Double.parseDouble(parts[1]);
            double power = Double.parseDouble(parts[2]);

            inputHandler.handleShot(id, dx, dy, power);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Главный игровой цикл - обновляет все системы.

    public void update() {
        synchronized (this) {
            if (!gameStarted) return;
            if (!gameRunning) return;

            // конец хода по времени
            if (state.isTurnExpired()) {
                System.out.println("⏰ Время хода истекло! Принудительная смена хода");
                endTurn();
                return;
            }
            // Обработка очереди выстрелов (автоматическое оружие)
            boolean firingComplete = inputHandler.updateFiring();
            if (firingComplete) {
                endTurn();
                return;
            }


            // Физика снарядов (ракеты, гранаты)
            updateProjectile();

            // Физика пуль
            updateBullets();

            // Физика игроков
            playerPhysics.update(state.p1);
            playerPhysics.update(state.p2);



            // Проверка конца игры
            checkGameOver();
        }
    }



    private void updateProjectile() {
        if (!state.projectile.active) return;

        ProjectilePhysics.CollisionResult collision = projectilePhysics.update(
                state.projectile, state.p1, state.p2);

        if (collision != null) {
            // Попадание в игрока - записываем попадание
            if (collision.type == ProjectilePhysics.CollisionType.PLAYER) {
                statsManager.recordShot(
                        state.getPlayerNickname(state.projectile.ownerId), true);
            }

            // Создаём взрыв
            Explosion explosion = explosionHandler.explode(
                    state.projectile, collision.x, collision.y, state.p1, state.p2);
            state.explosionX = (int) explosion.x;
            state.explosionY = (int) explosion.y;
            state.explosionRadius = explosion.radius;
            state.explosionTime = System.currentTimeMillis();

            // Деактивируем снаряд
            state.projectile.deactivate();

            // Отправляем обновления
            sendObjectsUpdate();
            sendMapUpdate();

            // Заканчиваем ход
            endTurn();
        }
    }


    private void updateBullets() {
        List<BulletPhysics.BulletHitEvent> hitEvents = bulletPhysics.update(
                state.bullets, state.p1, state.p2);

        for (BulletPhysics.BulletHitEvent event : hitEvents) {
            switch (event.type) {
                case GROUND:
                    break;

                case PLATFORM:
                    Platform platform = (Platform) event.target;
                    break;

                case PLAYER:
                    statsManager.recordShot(
                            state.getPlayerNickname(event.bullet.ownerId), true);
                    Player target = (Player) event.target;
                    System.out.println("Попадание в игрока " + target.id +
                            "! Урон: " + event.bullet.damage + " HP");
                    break;
            }
        }
    }



    private void checkGameOver() {
        int winnerId = turnManager.checkGameOver();
        if (winnerId != -1) {
            String winnerNick = turnManager.getWinnerNickname();
            broadcast(new Packet(MessageType.END_GAME, winnerNick));
            gameRunning = false;
        }
    }


    private void endTurn() {
        turnManager.endTurn();
        sendState();
    }

    public boolean isVotingComplete() {
        return mapVotingManager.isVotingComplete();
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
            System.out.println("Клиент " + client + "удален");
        }
    }
    // ============ МЕТОДЫ ОТПРАВКИ ДАННЫХ КЛИЕНТАМ ============
    /**
     * Отправляет полное состояние игры всем клиентам.
     */
    public void sendState() {
        synchronized (this) {
            String data = stateSerializer.serializeState();
            broadcast(new Packet(MessageType.UPDATE, data));
        }
    }

    /**
     * Отправляет карту клиентам (первоначальная загрузка).
     */
    public void sendMap() {
        synchronized (this) {
            String data = stateSerializer.serializeMap();
            broadcast(new Packet(MessageType.INIT_MAP, data));
        }
    }

    /**
     * Отправляет обновление карты (после деформации рельефа).
     */
    private void sendMapUpdate() {
        synchronized (this) {
            String data = stateSerializer.serializeMap();
            broadcast(new Packet(MessageType.MAP_UPDATE, data));
        }
    }

    /**
     * Отправляет объекты на карте (платформы, блоки).
     */
    private void sendObjects() {
        synchronized (this) {
            String data = stateSerializer.serializeObjects();
            broadcast(new Packet(MessageType.INIT_OBJECTS, data));
        }
    }

    /**
     * Отправляет обновление объектов (после разрушений).
     */
    private void sendObjectsUpdate() {
        synchronized (this) {
            String data = stateSerializer.serializeObjects();
            broadcast(new Packet(MessageType.OBJECTS_UPDATE, data));
        }
    }

    /**
     * Отправляет сигнал начала хода.
     */
    public void broadcastStartTurn() {
        broadcast(new Packet(MessageType.START_TURN, ""));
    }

    /**
     * Отправляет пакет всем подключённым клиентам.
     */
    private void broadcast(Packet p) {
        for (ClientHandler c : clients) {
            try {
                PacketIO.write(c.out, p);
            } catch (Exception ignored) {
            }
        }
    }
}
