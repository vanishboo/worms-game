package ru.itis.dis403.client.model;

import lombok.Getter;


@Getter
public class BulletTrail {

    private final int x;
    private final int y;
    private int life = 10;

    public BulletTrail(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void decreaseLife() {
        this.life--;
    }
}
