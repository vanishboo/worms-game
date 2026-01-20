package ru.itis.dis403.common.protocol;

public class Packet {
    public byte type;
    public String payload;

    public Packet(byte type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}

