package ru.itis.dis403.server.model;

import lombok.Getter;
import lombok.Setter;
import ru.itis.dis403.common.Constants;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Arena {
    public static final int WIDTH = Constants.WIDTH;

    private final int[] ground = new int[WIDTH];
    public List<Platform> platforms = new ArrayList<>();

    public Arena() {}


    public int getY(int x) {
        if (x < 0) return ground[0];
        if (x >= WIDTH) return ground[WIDTH - 1];
        return ground[x];
    }


    public void replaceGround(int[] newGround) {
        if (newGround.length != WIDTH) {
            throw new IllegalArgumentException("Ground array must be " + WIDTH + " elements");
        }
        System.arraycopy(newGround, 0, ground, 0, WIDTH);
    }
}
