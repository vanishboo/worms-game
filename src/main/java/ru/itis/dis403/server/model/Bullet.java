package ru.itis.dis403.server.model;

public class Bullet {
    public double x, y;
    public double vx, vy;
    public int ownerId;
    public int damage;
    public boolean active = false;
    public boolean hasHit = false;

    public void launch(double x, double y, double vx, double vy, int ownerId, int damage) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ownerId = ownerId;
        this.damage = damage;
        this.active = true;
        this.hasHit = false;
    }

    public void update() {
        if (!active) return;
        x += vx;
        y += vy;
    }

    public void deactivate() {
        active = false;
    }
}
