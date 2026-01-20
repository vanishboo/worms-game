package ru.itis.dis403.common.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerStats {
    private String nickname;
    private int wins;
    private int losses;
    private int totalShots;
    private int totalHits;

    public PlayerStats(String nickname) {
        this.nickname = nickname;
        this.wins = 0;
        this.losses = 0;
        this.totalShots = 0;
        this.totalHits = 0;
    }

    public void addWin() { this.wins++; }
    public void addLoss() { this.losses++; }
    public void addShot(boolean hit) {
        this.totalShots++;
        if (hit) this.totalHits++;
    }

    public String getNickname() { return nickname; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getTotalShots() { return totalShots; }
    public int getTotalHits() { return totalHits; }



    // Парсинг списка из JSON
    public static List<PlayerStats> fromJsonList(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<PlayerStats>>(){}.getType();
        return gson.fromJson(json, listType);
    }

}
