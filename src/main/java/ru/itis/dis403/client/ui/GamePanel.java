package ru.itis.dis403.client.ui;

import lombok.Getter;
import ru.itis.dis403.client.GameClient;
import ru.itis.dis403.client.model.*;
import ru.itis.dis403.client.net.ClientConnection;
import ru.itis.dis403.client.ui.input.KeyboardHandler;
import ru.itis.dis403.client.ui.input.MouseHandler;
import ru.itis.dis403.client.ui.map.MapManager;
import ru.itis.dis403.client.ui.parser.GameStateParser;
import ru.itis.dis403.client.ui.renderer.IRenderer;
import ru.itis.dis403.client.ui.renderer.entity.*;
import ru.itis.dis403.client.ui.renderer.terrain.*;
import ru.itis.dis403.client.ui.renderer.ui.*;
import ru.itis.dis403.client.ui.resources.SpriteLoader;
import ru.itis.dis403.common.Constants;
import ru.itis.dis403.common.MapType;
import ru.itis.dis403.common.WeaponType;
import ru.itis.dis403.common.model.PlayerStats;
import ru.itis.dis403.common.protocol.MessageType;
import ru.itis.dis403.common.protocol.Packet;
import ru.itis.dis403.common.protocol.PacketIO;
import ru.itis.dis403.server.model.Bullet;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * игровая панель (координатор рендеринга и ввода).
 * Делегирует отрисовку рендерерам, обработку ввода - handlers,
 * парсинг данных - парсеру, эффекты - менеджеру эффектов.
 */
public class GamePanel extends JPanel implements
        KeyboardHandler.WeaponChangeCallback,
        MouseHandler.MouseStateCallback {

    // ============ КОНСТАНТЫ ============
    private static final int W = Constants.WIDTH;
    private static final int H = Constants.HEIGHT;

    // ============ СЕТЕВОЕ ПОДКЛЮЧЕНИЕ ============
    private final ClientConnection connection;
    private final MainFrame mainFrame;
    // ============ МЕНЕДЖЕРЫ ============
    @Getter
    private final MapManager mapManager;

    private final GameStateParser stateParser;

    // ============ СОСТОЯНИЕ ИГРЫ ============
    @Getter
    private final GameStateView state = new GameStateView();

    @Getter
    private int myPlayerId = -1;

    @Getter
    private boolean gameOver = false;

    @Getter
    private String gameOverMessage = "";

    @Getter
    private boolean inLobby = true;

    @Getter
    private MapType myVote = null;

    @Getter
    private boolean mapVotingActive = true;


    @Getter
    private String lobbyMessage = "Ожидание игроков...";

    // ============ ИГРОВЫЕ ОБЪЕКТЫ ============
    @Getter
    private MapType currentMapType = MapType.CLASSIC_ARENA; // Дефолтная карта

    @Getter
    private final List<Platform> platforms = new ArrayList<>();


    @Getter
    private Point projectile;

    @Getter
    private List<BulletTrail> bullets = new ArrayList<>();


    @Getter
    private boolean terrainNeedsUpdate = true;

    // ============ ВВОД (МЫШЬ) ============
    @Getter
    private Point mouse = new Point();

    @Getter
    private Point lockedMouse;

    @Getter
    private boolean charging = false;

    @Getter
    private long chargeStart;

    // ============ UI СОСТОЯНИЕ ============
    @Getter
    private long turnTimeRemaining = 30000;

    @Getter
    private WeaponType selectedWeapon = WeaponType.PISTOL;

    @Getter
    private final Map<WeaponType, Integer> weaponCooldowns = new HashMap<>();

    @Getter
    private String player1Nick = "Player1";

    @Getter
    private String player2Nick = "Player2";

    // ============ АНИМАЦИЯ ИГРОКОВ ============
    private final BufferedImage[] walkP1;
    private final BufferedImage[] walkP2;

    // ============ HANDLERS ============
    private final KeyboardHandler keyboardHandler;
    private final MouseHandler mouseHandler;

    // ============ РЕНДЕРЕРЫ ============
    private IRenderer terrainRenderer;
    private IRenderer platformRenderer;
    private IRenderer playerRenderer;
    private IRenderer healthBarRenderer;
    private IRenderer projectileRenderer;
    private IRenderer uiRenderer;
    private IRenderer chargingBarRenderer;
    private IRenderer aimingRenderer;
    private IRenderer gameOverRenderer;
    private LobbyRenderer lobbyRenderer;
    private JButton leaderboardBtn = null;
    private List<JButton> mapButtons = new ArrayList<>();

    /**
     * Создаёт игровую панель.
     *
     * @param client игровой клиент
     */
    public GamePanel(GameClient client, MainFrame mainFrame) {
        this.connection = client.getConnection();
        this.mainFrame = mainFrame;
        // Настройка панели
        setFocusable(true);
        setBackground(new Color(200, 220, 255));

        setLayout(null); // Абсолютное позиционирование
        createMapButtons(); // Создаем кнопки карт

        // Инициализация менеджеров
        this.mapManager = new MapManager();
        this.stateParser = new GameStateParser(
                mapManager,
                state,
                platforms
        );

        // Загрузка спрайтов через SpriteLoader
        walkP1 = SpriteLoader.loadWalkAnimation("/sprites/blue_walk.png");
        walkP2 = SpriteLoader.loadWalkAnimation("/sprites/pink_walk.png");



        // Инициализация handlers
        keyboardHandler = new KeyboardHandler(connection, this);
        mouseHandler = new MouseHandler(connection, state, myPlayerId, this);

        addKeyListener(keyboardHandler);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        // Инициализация рендереров
        initRenderers();

        // Таймер обновления
        new Timer(16, e -> {
            repaint();
        }).start();
    }


    /**
     * Инициализирует все рендереры.
     */
    private void initRenderers() {
        if (terrainNeedsUpdate) {
            terrainRenderer = new TerrainRenderer(mapManager.getGround(),
                    currentMapType);
            terrainNeedsUpdate = false;
        }
        platformRenderer = new PlatformRenderer(platforms);
        playerRenderer = new PlayerRenderer(state, walkP1, walkP2);
        healthBarRenderer = new HealthBarRenderer(state, player1Nick, player2Nick);
        projectileRenderer = new ProjectileRenderer(projectile, bullets);
    }

    /**
     * Обновляет динамические рендереры (с изменяемыми данными).
     */
    private void updateDynamicRenderers() {
        terrainRenderer = new TerrainRenderer(mapManager.getGround(), currentMapType);
        platformRenderer = new PlatformRenderer(platforms);
        healthBarRenderer = new HealthBarRenderer(state, player1Nick, player2Nick);
        projectileRenderer = new ProjectileRenderer(projectile, new ArrayList<>(bullets));

        uiRenderer = new UIRenderer(W, H, turnTimeRemaining,
                state.getCurrentTurn(), player1Nick, player2Nick,
                myPlayerId, selectedWeapon, weaponCooldowns);

        chargingBarRenderer = new ChargingBarRenderer(W, H, charging, chargeStart);
        aimingRenderer = new AimingRenderer(state, myPlayerId, state.getCurrentTurn(), mouse);
        gameOverRenderer = new GameOverRenderer(W, H, gameOver, gameOverMessage);
        lobbyRenderer = new LobbyRenderer(W, H, inLobby, lobbyMessage, mapVotingActive, myVote);
        // Управление кнопкой лидерборда
        if (gameOver && leaderboardBtn == null) {
            leaderboardBtn = new JButton("Таблица лидеров");
            leaderboardBtn.setBounds(W/2 - 120, H/2 + 50, 240, 50);
            leaderboardBtn.setFont(new Font("Arial", Font.BOLD, 18));
            leaderboardBtn.setBackground(new Color(50, 150, 255));
            leaderboardBtn.setForeground(Color.WHITE);
            leaderboardBtn.setFocusPainted(false);
            leaderboardBtn.addActionListener(e -> {
                try {
                    PacketIO.write(connection.out, new Packet(MessageType.LEADERBOARD_REQUEST, ""));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            add(leaderboardBtn);
            revalidate();
        } else if (!gameOver && leaderboardBtn != null) {
            remove(leaderboardBtn);
            leaderboardBtn = null;
            revalidate();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        bullets.removeIf(bullet -> {
            bullet.decreaseLife();
            return bullet.getLife() <= 0;
        });

        // Обновляем динамические рендереры
        updateDynamicRenderers();

        // Если в лобби - рисуем только экран ожидания
        if (inLobby) {
            lobbyRenderer.render(g2);
            return;
        }


        if (mapManager.getGround() != null) {
            terrainRenderer.render(g2);
        }

        platformRenderer.render(g2);
        playerRenderer.render(g2);
        healthBarRenderer.render(g2);
        projectileRenderer.render(g2);
        uiRenderer.render(g2);

        if (!gameOver && myPlayerId == state.getCurrentTurn()) {
            aimingRenderer.render(g2);
            chargingBarRenderer.render(g2);
        }

        if (gameOver) {
            gameOverRenderer.render(g2);
        }
    }


    public void handlePacket(Packet p) {
        switch (p.type) {
            case MessageType.LOBBY_WAIT:
                lobbyMessage = p.payload;
                inLobby = true;
                System.out.println("LOBBY_WAIT: mapVotingActive=" + mapVotingActive + ", кнопок=" + mapButtons.size());

                if (mapVotingActive) {
                    for (JButton btn : mapButtons) {
                        System.out.println("Показываю кнопку: " + btn.getText());
                        btn.setVisible(true);
                    }
                }
                break;


            case MessageType.START_TURN:
                inLobby = false;
                charging = false;
                break;

            case MessageType.MAP_SELECTED:
                currentMapType = MapType.valueOf(p.payload);
                mapVotingActive = false;
                // Скрываем кнопки
                for (JButton btn : mapButtons) {
                    btn.setVisible(false);
                }
                terrainNeedsUpdate = true;
                System.out.println("Карта выбрана: " + currentMapType.displayName);
                break;


            case MessageType.INIT_MAP:
                stateParser.applyMap(p.payload);
                terrainNeedsUpdate = true;
                break;

            case MessageType.MAP_UPDATE:
                stateParser.updateMap(p.payload);
                terrainNeedsUpdate = true;
                break;

            case MessageType.INIT_OBJECTS:
                stateParser.applyObjects(p.payload);
                break;

            case MessageType.OBJECTS_UPDATE:
                stateParser.applyObjects(p.payload);
                break;

            case MessageType.UPDATE:

                long[] turnTime = {turnTimeRemaining};
                WeaponType[] weapon = {selectedWeapon};
                String[] nick1 = {player1Nick};
                String[] nick2 = {player2Nick};
                Point[] proj = {projectile};

                stateParser.applyState(p.payload, turnTime, weapon, nick1, nick2,
                        weaponCooldowns, proj, bullets);

                // Применяем результаты
                turnTimeRemaining = turnTime[0];
                selectedWeapon = weapon[0];
                player1Nick = nick1[0];
                player2Nick = nick2[0];
                projectile = proj[0];
                break;

            case MessageType.END_GAME:
                gameOver = true;
                gameOverMessage = p.payload + " победил!";
                mainFrame.onGameOver(gameOverMessage);
                break;

            case MessageType.ASSIGN_ID:
                myPlayerId = Integer.parseInt(p.payload);
                mouseHandler.setMyPlayerId(myPlayerId);
                mouseHandler.setCurrentWeapon(selectedWeapon);
                break;

            case MessageType.LEADERBOARD_RESPONSE:
                List<PlayerStats> players = PlayerStats.fromJsonList(p.payload);
                StringBuilder sb = new StringBuilder("ТАБЛИЦА ЛИДЕРОВ\n\n");
                int place = 1;
                for (PlayerStats s : players) {
                    double accuracy = s.getTotalShots() > 0 ?
                            (double) s.getTotalHits() / s.getTotalShots() * 100.0 : 0.0;
                    sb.append(String.format("%d. %s — %d побед, %d поражений (%.1f%% точность)\n",
                            place++, s.getNickname(), s.getWins(), s.getLosses(), accuracy));
                }
                JOptionPane.showMessageDialog(this, sb.toString(),
                        "Таблица лидеров", JOptionPane.INFORMATION_MESSAGE);
                break;

        }
    }

    // ============ CALLBACKS ОТ HANDLERS ============

    @Override
    public void onWeaponChanged(WeaponType weapon) {
        this.selectedWeapon = weapon;
        mouseHandler.setCurrentWeapon(weapon);
    }

    @Override
    public void onMouseMoved(Point mouse) {
        this.mouse = mouse;
    }

    @Override
    public void onChargingStarted(Point lockedMouse, long chargeStart) {
        this.lockedMouse = lockedMouse;
        this.charging = true;
        this.chargeStart = chargeStart;
    }

    @Override
    public void onChargingStopped() {
        this.charging = false;
    }


     // Создает Swing кнопки для выбора карт

    private void createMapButtons() {
        int btnY = 180;
        int btnWidth = 400;
        int btnHeight = 70;
        int btnX = (W - btnWidth) / 2;

        for (MapType map : MapType.values()) {
            JButton btn = new JButton(map.displayName);
            btn.setBounds(btnX, btnY, btnWidth, btnHeight);

            // Стиль
            Color mapColor = getMapColorForButton(map);
            btn.setBackground(mapColor);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 24));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);


            // Клик
            btn.addActionListener(e -> {
                if (myVote != null) return;
                myVote = map;

                try {
                    PacketIO.write(connection.out,
                            new Packet(MessageType.VOTE_MAP, map.name()));
                    System.out.println("🗳️ Проголосовали за: " + map.displayName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Блокируем все кнопки
                for (JButton b : mapButtons) {
                    b.setEnabled(false);
                    b.setBackground(b.getBackground().darker());
                }
            });

            btn.setVisible(false); // Сначала скрыты
            mapButtons.add(btn);
            add(btn);

            btnY += 90;
        }
    }

    private Color getMapColorForButton(MapType map) {
        switch (map) {
            case CLASSIC_ARENA: return new Color(77, 148, 9);
            case DESERT_CANYON: return new Color(200, 140, 80);
            case ICE_FORTRESS: return new Color(150, 200, 240);
            default: return new Color(100, 100, 100);
        }
    }

}
