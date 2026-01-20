package ru.itis.dis403.server.manager;

import ru.itis.dis403.common.WeaponType;
import ru.itis.dis403.server.model.Player;
import ru.itis.dis403.server.ServerGameState;
import ru.itis.dis403.server.storage.PlayerStatsManager;


public class TurnManager {

    private final ServerGameState state;

    private final PlayerStatsManager statsManager;

    private boolean gameOver = false;


    public TurnManager(ServerGameState state, PlayerStatsManager statsManager) {
        this.state = state;
        this.statsManager = statsManager;
    }


    public void startGame() {
        state.turnStartTime = System.currentTimeMillis();
        state.turnNumber = 1;

        // Инициализация счётчиков ходов игроков
        if (state.player1TurnCount == 0) state.player1TurnCount = 1;
        if (state.player2TurnCount == 0) state.player2TurnCount = 1;

        System.out.println("Игра началась! Ход игрока " + state.currentTurn);
    }

    public void endTurn() {

        if (state.currentTurn == 1) {
            state.player1TurnCount++;
        } else {
            state.player2TurnCount++;
        }

        // Смена игрока
        state.currentTurn = (state.currentTurn == 1) ? 2 : 1;
        state.turnStartTime = System.currentTimeMillis();
        state.turnNumber++;
        state.currentWeapon = WeaponType.PISTOL;

        System.out.println("Смена хода! Ход игрока " + state.currentTurn);
    }


    public int checkGameOver() {
        if (gameOver) return -1;

        if (state.p1.hp <= 0) {
            endGame(2);
            return 2;
        } else if (state.p2.hp <= 0) {
            endGame(1);
            return 1;
        }

        return -1;
    }


    private void endGame(int winnerId) {
        if (gameOver) return;

        gameOver = true;
        String winnerNick = state.getPlayerNickname(winnerId);
        String loserNick = state.getPlayerNickname(winnerId == 1 ? 2 : 1);

        // Записываем статистику
        statsManager.recordWin(winnerNick);
        statsManager.recordLoss(loserNick);

        System.out.println("Игра завершена! Победитель: " + winnerNick);
    }


    public String getWinnerNickname() {
        if (!gameOver) return null;

        if (state.p1.hp <= 0) {
            return state.getPlayerNickname(2);
        } else if (state.p2.hp <= 0) {
            return state.getPlayerNickname(1);
        }

        return null;
    }

}
