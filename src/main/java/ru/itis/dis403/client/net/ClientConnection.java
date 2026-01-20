package ru.itis.dis403.client.net;

import ru.itis.dis403.common.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;




public class ClientConnection {

    public final Socket socket;
    public final DataInputStream in;
    public final DataOutputStream out;

    public ClientConnection() throws Exception {
        this.socket = new Socket(Constants.SERVER_HOST, Constants.PORT);

        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());

        System.out.println("подключение к серверу установлено");
    }

    public void close() throws IOException {
        socket.close();
    }
}
