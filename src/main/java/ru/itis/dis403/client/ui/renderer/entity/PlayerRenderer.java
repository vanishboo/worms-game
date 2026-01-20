package ru.itis.dis403.client.ui.renderer.entity;

import ru.itis.dis403.client.model.GameStateView;
import ru.itis.dis403.client.ui.renderer.IRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;


public class PlayerRenderer implements IRenderer {
    private final GameStateView state;

    private final BufferedImage[] walkP1;

    private final BufferedImage[] walkP2;

    private int frameP1 = 0;

    private int frameP2 = 0;

    private long lastAnimP1 = 0;

    private long lastAnimP2 = 0;

    private double lastX1 = 0;

    private double lastX2 = 0;

    private int dirP1 = 1;

    private int dirP2 = -1;

    public PlayerRenderer(GameStateView state, BufferedImage[] walkP1, BufferedImage[] walkP2) {
        this.state = state;
        this.walkP1 = walkP1;
        this.walkP2 = walkP2;
    }

    @Override
    public void render(Graphics2D g2) {
        drawPlayer(g2, 1);
        drawPlayer(g2, 2);
    }


    private void drawPlayer(Graphics2D g2, int playerId) {
        double x, y;
        BufferedImage[] frames;
        int frame;
        int dir;

        if (playerId == 1) {
            x = state.getP1().getX();
            y = state.getP1().getY();
            frames = walkP1;
            frame = frameP1;
            dir = dirP1;

            // Обновление анимации при движении
            if (Math.abs(x - lastX1) > 0.5) {
                long now = System.currentTimeMillis();
                if (now - lastAnimP1 > 100) {
                    frameP1 = (frameP1 + 1) % 6;
                    lastAnimP1 = now;
                }
                dirP1 = (x > lastX1) ? 1 : -1;
            }
            lastX1 = x;

        } else {
            x = state.getP2().getX();
            y = state.getP2().getY();
            frames = walkP2;
            frame = frameP2;
            dir = dirP2;

            // Обновление анимации при движении
            if (Math.abs(x - lastX2) > 0.5) {
                long now = System.currentTimeMillis();
                if (now - lastAnimP2 > 100) {
                    frameP2 = (frameP2 + 1) % 6;
                    lastAnimP2 = now;
                }
                dirP2 = (x > lastX2) ? 1 : -1;
            }
            lastX2 = x;
        }

        // Отрисовка спрайта
        if (frames != null && frames.length > 0) {
            BufferedImage sprite = frames[frame];
            int w = sprite.getWidth();
            int h = sprite.getHeight();

            if (dir < 0) {
                // Отражение по горизонтали для движения влево
                g2.drawImage(sprite, (int) x + w, (int) y - h, -w, h, null);
            } else {
                g2.drawImage(sprite, (int) x, (int) y - h, w, h, null);
            }
        } else {
            // Запасной вариант (простой круг) если нет спрайтов
            Color playerColor = (playerId == 1) ?
                    new Color(100, 150, 255) : new Color(255, 100, 150);

            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillOval((int) x + 2, (int) y - 38, 36, 36);

            g2.setColor(playerColor);
            g2.fillOval((int) x, (int) y - 40, 36, 36);

            g2.setColor(Color.WHITE);
            g2.fillOval((int) x + 10, (int) y - 30, 6, 6);
            g2.fillOval((int) x + 20, (int) y - 30, 6, 6);
        }
    }
}
