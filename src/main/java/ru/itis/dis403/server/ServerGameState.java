package ru.itis.dis403.server;

import ru.itis.dis403.common.WeaponType;
import ru.itis.dis403.server.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerGameState {
    public final Arena arena = new Arena();

    public Player p1;
    public Player p2;

    public final Projectile projectile = new Projectile();

    public int explosionX = -1;
    public int explosionY = -1;
    public int explosionRadius = 0;
    public long explosionTime = 0;

    public List<Bullet> bullets = new ArrayList<>();

    public int currentTurn = 1;
    public long turnStartTime;
    public static final long TURN_DURATION_MS = 30000;
    public WeaponType currentWeapon = WeaponType.PISTOL;

    // Map<playerId, Map<weapon, nextAvailableTurn>>
    public Map<Integer, Map<WeaponType, Integer>> weaponCooldowns = new HashMap<>();

    public int turnNumber = 0;
    public String player1Nickname = null;
    public String player2Nickname = null;
    public int player1TurnCount = 0;
    public int player2TurnCount = 0;

    public ServerGameState() {
        turnStartTime = System.currentTimeMillis();

        weaponCooldowns.put(1, new HashMap<>());
        weaponCooldowns.put(2, new HashMap<>());

    }


    public void initPlayers() {
        int spawn1X = 150;
        int spawn2X = Arena.WIDTH - 150;
        p1 = new Player(1, spawn1X, arena.getY(spawn1X));
        p2 = new Player(2, spawn2X, arena.getY(spawn2X));
        System.out.println("Игроки размещены на карте");
    }

    public Player getPlayer(int id) {
        return id == 1 ? p1 : p2;
    }

    public void setPlayerNickname(int id, String nickname) {
        if (id == 1) {
            player1Nickname = nickname;
        } else {
            player2Nickname = nickname;
        }
    }

    public String getPlayerNickname(int id) {
        return id == 1 ? player1Nickname : player2Nickname;
    }

    public boolean isTurnExpired() {
        return (System.currentTimeMillis() - turnStartTime) > TURN_DURATION_MS;
    }

    public long getRemainingTurnTime() {
        long elapsed = System.currentTimeMillis() - turnStartTime;
        return Math.max(0, TURN_DURATION_MS - elapsed);
    }

    public boolean isWeaponAvailable(int playerId, WeaponType weapon) {
        return getRemainingCooldown(playerId, weapon) == 0;
    }

    public int getRemainingCooldown(int playerId, WeaponType weapon) {
        if (weapon.cooldown == 0) return 0;

        Map<WeaponType, Integer> playerCooldowns = weaponCooldowns.get(playerId);
        if (!playerCooldowns.containsKey(weapon)) return 0;

        int nextAvailableTurn = playerCooldowns.get(weapon);
        int currentPlayerTurn = getCurrentPlayerTurnCount(playerId);
        int remaining = nextAvailableTurn - currentPlayerTurn;

        return Math.max(0, remaining);
    }

    public void useWeapon(int playerId, WeaponType weapon) {
        if (weapon.cooldown > 0) {
            int nextAvailableTurn = getCurrentPlayerTurnCount(playerId) + weapon.cooldown + 1;
            weaponCooldowns.get(playerId).put(weapon, nextAvailableTurn);
        }
    }

    public int getCurrentPlayerTurnCount(int playerId) {
        return playerId == 1 ? player1TurnCount : player2TurnCount;
    }
}
