package ru.itis.dis403.client.ui.map;

import lombok.Getter;

public class MapManager {


    @Getter
    private int[] ground;

    @Getter
    private int[] originalGround;


    public MapManager() {
    }


    public void setGround(int[] ground) {
        this.ground = ground;
        this.originalGround = ground.clone();
    }


}
