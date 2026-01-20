package ru.itis.dis403.client.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GameStateView {

    public PlayerView p1 = new PlayerView();
    public PlayerView p2 = new PlayerView();
    public int currentTurn;
}
