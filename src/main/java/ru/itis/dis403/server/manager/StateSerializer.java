package ru.itis.dis403.server.manager;

import ru.itis.dis403.common.WeaponType;
import ru.itis.dis403.server.model.*;
import ru.itis.dis403.server.ServerGameState;

import java.util.List;

/**
 * Сериализатор игрового состояния для отправки клиентам.
 * Преобразует объекты игры в строковый формат для передачи по сети.
 */
public class StateSerializer {

    private final ServerGameState state;


    public StateSerializer(ServerGameState state) {
        this.state = state;
    }


    public String serializeState() {
        StringBuilder sb = new StringBuilder();

        // Базовая информация о ходе
        sb.append(state.currentTurn).append(";");
        sb.append(state.getRemainingTurnTime()).append(";");
        sb.append(state.currentWeapon.name()).append(";");
        sb.append(state.player1Nickname).append(";");
        sb.append(state.player2Nickname).append(";");

        // КД для текущего игрока
        int currentPlayer = state.currentTurn;
        StringBuilder cdInfo = new StringBuilder();
        for (WeaponType weapon : WeaponType.values()) {
            int cd = state.getRemainingCooldown(currentPlayer, weapon);
            cdInfo.append(weapon.name()).append(":").append(cd).append(",");
        }
        sb.append(cdInfo).append(";");

        // Игроки
        sb.append(state.p1.id).append(" ")
                .append(state.p1.x).append(" ")
                .append(state.p1.y).append(" ")
                .append(state.p1.hp).append("|");

        sb.append(state.p2.id).append(" ")
                .append(state.p2.x).append(" ")
                .append(state.p2.y).append(" ")
                .append(state.p2.hp);

        // Снаряд
        if (state.projectile.active) {
            sb.append("#")
                    .append(state.projectile.x).append(" ")
                    .append(state.projectile.y);
        }

        // Пули
        if (!state.bullets.isEmpty()) {
            sb.append("#BULLETS#");
            for (Bullet bullet : state.bullets) {
                if (bullet.active) {
                    sb.append(bullet.x).append(",").append(bullet.y).append(";");
                }
            }
        }


        return sb.toString();
    }


    public String serializeMap() {
        StringBuilder sb = new StringBuilder();
        int[] ground = state.arena.getGround();

        for (int i = 0; i < ground.length; i++) {
            sb.append(ground[i]);
            if (i < ground.length - 1) sb.append(",");
        }

        return sb.toString();
    }


    public String serializeObjects() {
        StringBuilder sb = new StringBuilder();


        for (Platform p : state.arena.platforms) {
            sb.append(p.serialize()).append("|");
        }


        return sb.toString();
    }
}
