package ru.itis.dis403.client.net;

import ru.itis.dis403.client.ui.GamePanel;
import ru.itis.dis403.common.protocol.Packet;
import ru.itis.dis403.common.protocol.PacketIO;

import java.io.DataInputStream;

public class ServerListener extends Thread {

    private DataInputStream in;

    private GamePanel game;

    public ServerListener(DataInputStream in, GamePanel game) {
        this.in = in;
        this.game = game;
    }


    public void run() {
        try {
            while (true) {
                Packet p = PacketIO.read(in);
                game.handlePacket(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
