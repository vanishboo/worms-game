package ru.itis.dis403.server.model;

import ru.itis.dis403.common.WeaponType;

public class Projectile {
    public double x;
    public double y;
    public double vx;
    public double vy;
    public int ownerId;
    public boolean active = false;
    public WeaponType weaponType = WeaponType.BAZOOKA;

    public void launch(double x, double y, double vx, double vy, int ownerId, WeaponType weaponType) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ownerId = ownerId;
        this.weaponType = weaponType;
        this.active = true;
    }

    public void update(double gravity) {
        if (!active) return;
        x += vx;
        y += vy;
        vy += gravity;
    }

    public void deactivate() {
        active = false;
    }
}
