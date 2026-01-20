package ru.itis.dis403.client.ui.renderer.terrain;

import ru.itis.dis403.client.model.Platform;
import ru.itis.dis403.client.ui.renderer.IRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class PlatformRenderer implements IRenderer {

    private final List<Platform> platforms;

    public PlatformRenderer(List<Platform> platforms) {
        this.platforms = platforms;
    }

    @Override
    public void render(Graphics2D g2) {
        // дабы избежать исключение
        List<Platform> platformsCopy =  new ArrayList<>(platforms);
        for (Platform p : platformsCopy) {

            if (p.getHp() <= 0) continue;
            Color baseColor = getPlatformColor(p.getType());


            // Основа платформы
            g2.setColor(baseColor);
            g2.fillRoundRect(p.getX(), p.getY(),
                    p.getWidth(), p.getHeight(), 8, 8);


            // Обводка
            g2.setColor(baseColor.darker());
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(p.getX(), p.getY(),
                    p.getWidth(), p.getHeight(), 8, 8);

            // HP бар если платформа повреждена
            if (p.getHp() < p.getMaxHp()) {
                drawHealthBar(g2, p);
            }
        }
    }


    private Color getPlatformColor(String type) {
        switch (type) {
            case "WOOD": return new Color(139, 90, 43);
            case "METAL": return new Color(120, 120, 140);
            case "ICE": return new Color(173, 216, 230);
            default: return new Color(139, 90, 43);
        }
    }


    private void drawHealthBar(Graphics2D g2, Platform p) {
        int barWidth = p.getWidth() - 10;
        int barHeight = 4;
        int barX = p.getX() + 5;
        int barY = p.getY() - 8;

        g2.setColor(new Color(231, 19, 19));
        g2.fillRect(barX, barY, barWidth, barHeight);

        float hpPercent = (float) p.getHp() / p.getMaxHp();
        g2.setColor(new Color(50, 200, 50));
        g2.fillRect(barX, barY, (int) (barWidth * hpPercent), barHeight);

        g2.setColor(Color.BLACK);
        g2.drawRect(barX, barY, barWidth, barHeight);
    }
}
