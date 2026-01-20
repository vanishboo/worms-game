package ru.itis.dis403.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class Platform {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int hp;
    private final int maxHp;
    private final String type;
}
