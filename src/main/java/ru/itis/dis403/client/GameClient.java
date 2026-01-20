package ru.itis.dis403.client;

import lombok.Getter;
import ru.itis.dis403.client.net.ClientConnection;
import ru.itis.dis403.client.ui.MainFrame;
import ru.itis.dis403.common.protocol.MessageType;
import ru.itis.dis403.common.protocol.Packet;
import ru.itis.dis403.common.protocol.PacketIO;

import javax.swing.*;
@Getter
public class GameClient {

    private ClientConnection connection;
    private String playerNickname;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                GameClient client = new GameClient();
                client.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }





    public void start() throws Exception {
        connection = new ClientConnection();

        MainFrame frame = new MainFrame(this);
        frame.setVisible(true);
    }

    public void setPlayerNickname(String nickname) {
        this.playerNickname = nickname;

        try {
            PacketIO.write(connection.out,
                    new Packet(MessageType.REGISTER_PLAYER, nickname));
            System.out.println("Отправлен ник: " + nickname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reconnect() throws Exception {
        connection = new ClientConnection();
        System.out.println("Переподключение к серверу успешно!");
    }

    public void sendReady() {
        try {
            PacketIO.write(connection.out,
                    new Packet(MessageType.PLAYER_READY, ""));
            System.out.println("Отправлен статус ГОТОВ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
