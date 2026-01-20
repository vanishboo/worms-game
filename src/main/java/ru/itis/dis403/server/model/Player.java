package ru.itis.dis403.server.model;

public class Player {
    public int id;
    public double x;
    public double y;
    public int hp = 100;

    public double vx = 0;
    public double vy = 0;

    public boolean hasImpulse = false;
    public boolean onGround = true;

    public Player(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}
