package ru.itis.dis403.server.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ru.itis.dis403.common.model.PlayerStats;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerStatsManager {

    private static final String STATS_FILE = "server_players_stats.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Map<String, PlayerStats> playersStats;

    public PlayerStatsManager() {
        loadStats();
    }

    private void loadStats() {
        try (Reader reader = new FileReader(STATS_FILE)) {
            Type type = new TypeToken<Map<String, PlayerStats>>(){}.getType();
            playersStats = gson.fromJson(reader, type);
            if (playersStats == null) {
                playersStats = new HashMap<>();
            }
            System.out.println("Загружена статистика " + playersStats.size() + " игроков");
        } catch (FileNotFoundException e) {
            playersStats = new HashMap<>();
            System.out.println("Файл статистики не найден. Создан новый.");
        } catch (Exception e) {
            playersStats = new HashMap<>();
            System.err.println("Ошибка загрузки статистики: " + e.getMessage());
        }
    }

    public synchronized void saveStats() {
        try (Writer writer = new FileWriter(STATS_FILE)) {
            gson.toJson(playersStats, writer);
            System.out.println("Статистика сохранена: " + playersStats.size() + " игроков");
        } catch (Exception e) {
            System.err.println("Ошибка сохранения статистики: " + e.getMessage());
        }
    }

    public synchronized PlayerStats getOrCreatePlayer(String nickname) {
        return playersStats.computeIfAbsent(nickname, PlayerStats::new);
    }

    public synchronized void recordWin(String nickname) {
        getOrCreatePlayer(nickname).addWin();
        saveStats();
    }

    public synchronized void recordLoss(String nickname) {
        getOrCreatePlayer(nickname).addLoss();
        saveStats();
    }

    public synchronized void recordShot(String nickname, boolean hit) {
        getOrCreatePlayer(nickname).addShot(hit);
        saveStats();
    }

    // Получить всех игроков отсортированных по победам
    public synchronized String getAllPlayersJson() {
        List<PlayerStats> sorted = playersStats.values().stream()
                .sorted((a, b) -> Integer.compare(b.getWins(), a.getWins()))
                .collect(Collectors.toList());
        return gson.toJson(sorted);
    }
}
