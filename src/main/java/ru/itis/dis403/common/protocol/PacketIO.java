package ru.itis.dis403.common.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketIO {

    public static void write(DataOutputStream out, Packet p) throws IOException {
        byte[] data = p.getPayload().getBytes(StandardCharsets.UTF_8);
        out.writeByte(p.getType());
        out.writeInt(data.length);
        out.write(data);
        out.flush();
    }

    public static Packet read(DataInputStream in) throws IOException {
        byte type = in.readByte();
        int len = in.readInt();
        byte[] buf = new byte[len];
        in.readFully(buf);
        return new Packet(type, new String(buf, StandardCharsets.UTF_8));
    }
}
