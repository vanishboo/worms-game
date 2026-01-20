package ru.itis.dis403.client.ui.renderer.ui;

import ru.itis.dis403.client.ui.renderer.IRenderer;

import java.awt.*;


public class GameOverRenderer implements IRenderer {

    private final int width;

    private final int height;

    private final boolean gameOver;

    private final String gameOverMessage;


    public GameOverRenderer(int width, int height, boolean gameOver, String gameOverMessage) {
        this.width = width;
        this.height = height;
        this.gameOver = gameOver;
        this.gameOverMessage = gameOverMessage;
    }

    @Override
    public void render(Graphics2D g2) {
        if (!gameOver) return;

        // Затемнение всего экрана
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, width, height);

        // Заголовок "ИГРА ОКОНЧЕНА"
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "ИГРА ОКОНЧЕНА";
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleX = (width - fmTitle.stringWidth(title)) / 2;

        // Заголовок
        g2.setColor(new Color(255, 50, 50));
        g2.drawString(title, titleX, height / 2 - 50);

        // Сообщение о победителе
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fmMsg = g2.getFontMetrics();
        int msgX = (width - fmMsg.stringWidth(gameOverMessage)) / 2;

        // Сообщение
        g2.setColor(new Color(255, 200, 50));
        g2.drawString(gameOverMessage, msgX, height / 2 + 20);
    }
}
