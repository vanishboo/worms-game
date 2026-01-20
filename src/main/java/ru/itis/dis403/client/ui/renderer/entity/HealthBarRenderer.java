package ru.itis.dis403.client.ui.renderer.entity;

import ru.itis.dis403.client.model.GameStateView;
import ru.itis.dis403.client.ui.renderer.IRenderer;

import java.awt.*;


public class HealthBarRenderer implements IRenderer {
    private final GameStateView state;

    private final String player1Nick;

    private final String player2Nick;


    public HealthBarRenderer(GameStateView state, String player1Nick, String player2Nick) {
        this.state = state;
        this.player1Nick = player1Nick;
        this.player2Nick = player2Nick;
    }

    @Override
    public void render(Graphics2D g2) {
        drawHealthBar(g2, state.getP1().getX(), state.getP1().getY(),
                state.getP1().getHp(), player1Nick, 1);
        drawHealthBar(g2, state.getP2().getX(), state.getP2().getY(),
                state.getP2().getHp(), player2Nick, 2);
    }


    private void drawHealthBar(Graphics2D g2, double x, double y, int hp, String nickname, int playerId) {
        int barWidth = 50;
        int barHeight = 6;
        int barX = (int) x - 5;
        int barY = (int) y - 55;

        // Фон полоски
        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(barX, barY, barWidth, barHeight);

        // Заполнение HP (цвет зависит от количества HP)
        double hpPercent = hp / 100.0;
        Color hpColor = hp > 60 ? new Color(50, 200, 50) :
                hp > 30 ? new Color(255, 200, 0) :
                        new Color(200, 50, 50);
        g2.setColor(hpColor);
        g2.fillRect(barX, barY, (int) (barWidth * hpPercent), barHeight);

        // Обводка
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(barX, barY, barWidth, barHeight);

        // Никнейм над полоской
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int nickWidth = fm.stringWidth(nickname);

        // Никнейм (разные цвета для игроков)
        Color nickColor = playerId == 1 ? new Color(100, 150, 255) : new Color(255, 100, 150);
        g2.setColor(nickColor);
        g2.drawString(nickname, barX + (barWidth - nickWidth) / 2, barY - 5);
    }
}
