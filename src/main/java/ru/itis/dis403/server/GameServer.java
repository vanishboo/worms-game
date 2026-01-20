package ru.itis.dis403.server;

import ru.itis.dis403.common.Constants;
import ru.itis.dis403.common.protocol.MessageType;
import ru.itis.dis403.common.protocol.Packet;
import ru.itis.dis403.common.protocol.PacketIO;
import ru.itis.dis403.server.storage.PlayerStatsManager;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {

    private static boolean player1Ready = false;
    private static boolean player2Ready = false;

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(Constants.PORT);
        System.out.println("Сервер запущен на порту " + Constants.PORT);

        PlayerStatsManager statsManager = new PlayerStatsManager();

        while (true) {
            runGame(server, statsManager);
        }
    }


    // ========================= GAME =========================

    private static void runGame(ServerSocket server,
                                PlayerStatsManager statsManager) throws Exception {

        System.out.println("Ожидание подключения игроков");

        ServerGameState state = new ServerGameState();
        List<ClientHandler> clients = new ArrayList<>();
        GameEngine engine = new GameEngine(state, clients, statsManager);

        resetReadyFlags();

        ClientHandler player1 = connectFirstPlayer(server, engine, clients);
        ClientHandler player2 = connectSecondPlayer(server, engine, clients);

        notifyPlayersConnected(player1, player2);

        waitForPlayersReady();
        waitForVoting(engine);

        startGame(engine);
    }

    // ========================= CONNECTION =========================

    private static ClientHandler connectFirstPlayer(ServerSocket server,
                                                    GameEngine engine,
                                                    List<ClientHandler> clients) throws Exception {

        Socket socket = server.accept();
        ClientHandler client = new ClientHandler(socket, 1, engine);

        PacketIO.write(client.out,
                new Packet(MessageType.ASSIGN_ID, "1"));

        clients.add(client);
        client.start();

        System.out.println("Игрок 1 подключен");

        PacketIO.write(client.out,
                new Packet(MessageType.LOBBY_WAIT,
                        "Ожидание второго игрока..."));

        return client;
    }

    private static ClientHandler connectSecondPlayer(ServerSocket server,
                                                     GameEngine engine,
                                                     List<ClientHandler> clients) throws Exception {

        Socket socket = server.accept();
        ClientHandler client = new ClientHandler(socket, 2, engine);

        PacketIO.write(client.out,
                new Packet(MessageType.ASSIGN_ID, "2"));

        clients.add(client);
        client.start();

        System.out.println("Игрок 2 подключен");

        return client;
    }

    private static void notifyPlayersConnected(ClientHandler c1,
                                               ClientHandler c2) throws Exception {

        PacketIO.write(c1.out,
                new Packet(MessageType.LOBBY_WAIT,
                        "Оба подключены. Введите ник и нажмите ГОТОВ"));

        PacketIO.write(c2.out,
                new Packet(MessageType.LOBBY_WAIT,
                        "Оба подключены. Введите ник и нажмите ГОТОВ"));
    }

    // ========================= WAITING =========================

    private static void waitForPlayersReady() throws Exception {
        System.out.println("Ожидание готовности игроков...");

        while (!player1Ready || !player2Ready) {
            Thread.sleep(100);
        }

        System.out.println("Оба игрока готовы!");
    }

    private static void waitForVoting(GameEngine engine) throws Exception {
        System.out.println("Начинается голосование за карту...");

        while (!engine.isVotingComplete()) {
            Thread.sleep(100);
        }

        System.out.println("Голосование завершено!");
    }

    // ========================= GAME =========================

    private static void startGame(GameEngine engine) throws Exception {
        engine.startGame();

        Thread gameLoop = new Thread(() -> {
            while (engine.isGameRunning()) {
                try {
                    engine.update();
                    engine.sendState();
                    Thread.sleep(16);
                } catch (Exception e) {
                    break;
                }
            }
        });

        gameLoop.start();
        System.out.println("Игра началась");

        gameLoop.join();
        System.out.println("Игра завершена. Ожидание новых игроков");
    }

    // ========================= READY =========================

    private static void resetReadyFlags() {
        player1Ready = false;
        player2Ready = false;
    }

    public static void setPlayerReady(int playerId) {
        if (playerId == 1) {
            player1Ready = true;
        } else if (playerId == 2) {
            player2Ready = true;
        }
        System.out.println("Игрок " + playerId + " готов!");
    }
}
