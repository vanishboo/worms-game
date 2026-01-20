package ru.itis.dis403.client.ui.renderer.ui;

import ru.itis.dis403.client.ui.renderer.IRenderer;
import ru.itis.dis403.common.MapType;

import java.awt.*;


public class LobbyRenderer implements IRenderer {

    private final int width;

    private final int height;

    private final boolean inLobby;

    private final String lobbyMessage;

    private final boolean mapVotingActive;

    private final MapType myVote;

    public LobbyRenderer(int width, int height, boolean inLobby,
                         String lobbyMessage, boolean mapVotingActive, MapType myVote) {
        this.width = width;
        this.height = height;
        this.inLobby = inLobby;
        this.lobbyMessage = lobbyMessage;
        this.mapVotingActive = mapVotingActive;
        this.myVote = myVote;
    }

    @Override
    public void render(Graphics2D g2) {
        if (!inLobby) return;

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(30, 50, 80),
                0, height, new Color(50, 80, 120)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);

        if (mapVotingActive) {
            renderMapVoting(g2);
        } else {
            // Обычное ожидание игроков
            renderWaitingScreen(g2);
        }
    }



    private void renderMapVoting(Graphics2D g2) {
        // Заголовок
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        String title = "ВЫБОР КАРТЫ";
        g2.drawString(title, (width - fm.stringWidth(title)) / 2, 100);

        if (myVote != null) {
            // Проголосовали - показываем статус
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.setColor(Color.GREEN);
            String voted = "Вы проголосовали за: " + myVote.displayName;
            g2.drawString(voted, (width - g2.getFontMetrics().stringWidth(voted)) / 2, 500);

            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.setColor(Color.WHITE);
            String waiting = "Ожидание другого игрока...";
            g2.drawString(waiting, (width - g2.getFontMetrics().stringWidth(waiting)) / 2, 560);
        }
    }



    private void renderWaitingScreen(Graphics2D g2) {
        // Заголовок игры
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "Duel";
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(title)) / 2;

        // Тень заголовка
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(title, x + 3, 150 + 3);

        // Заголовок
        g2.setColor(new Color(255, 200, 50));
        g2.drawString(title, x, 150);

        // Сообщение о статусе
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        fm = g2.getFontMetrics();
        x = (width - fm.stringWidth(lobbyMessage)) / 2;
        g2.setColor(Color.WHITE);
        g2.drawString(lobbyMessage, x, height / 2);
    }

}
