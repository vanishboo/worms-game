package ru.itis.dis403.client.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlayerView {
    public int id;
    public double x;
    public double y;
    public int hp;


    public PlayerView() {
        this.id = 0;
        this.x = 0;
        this.y = 0;
        this.hp = 100;
    }
}
