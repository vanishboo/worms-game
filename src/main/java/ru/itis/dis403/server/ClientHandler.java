package ru.itis.dis403.server;

import ru.itis.dis403.common.protocol.Packet;
import ru.itis.dis403.common.protocol.PacketIO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {

    public final int playerId;
    public final DataInputStream in;
    public final DataOutputStream out;

    private final GameEngine engine;

    public ClientHandler(Socket socket, int playerId, GameEngine engine) throws Exception {
        this.playerId = playerId;
        this.engine = engine;

        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                Packet p = PacketIO.read(in);
                engine.handle(this, p);
            }
        } catch (Exception e) {
            System.out.println("Игрок " + playerId + " отключился");
            engine.removeClient(this);
        }
    }
}
