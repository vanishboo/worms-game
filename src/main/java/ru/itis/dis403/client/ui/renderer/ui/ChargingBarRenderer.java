package ru.itis.dis403.client.ui.renderer.ui;

import ru.itis.dis403.client.ui.renderer.IRenderer;

import java.awt.*;


public class ChargingBarRenderer implements IRenderer {

    private final int width;

    private final int height;

    private final boolean charging;

    private final long chargeStart;


    public ChargingBarRenderer(int width, int height, boolean charging, long chargeStart) {
        this.width = width;
        this.height = height;
        this.charging = charging;
        this.chargeStart = chargeStart;
    }

    @Override
    public void render(Graphics2D g2) {
        if (!charging) return;

        // Вычисляем мощность выстрела (от 0.0 до 1.0)
        long chargeTime = System.currentTimeMillis() - chargeStart;
        float power = Math.min(1.0f, chargeTime / 2000.0f);

        int barWidth = 300;
        int barHeight = 30;
        int barX = (width - barWidth) / 2;
        int barY = height - 100;

        // Фон полоски
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(barX, barY, barWidth, barHeight);

        // Заполнение (цвет меняется от зелёного к красному)
        Color chargeColor = power < 0.3f ? Color.GREEN :
                power < 0.7f ? Color.YELLOW : Color.RED;
        g2.setColor(chargeColor);
        g2.fillRect(barX + 2, barY + 2, (int) ((barWidth - 4) * power), barHeight - 4);

        // Текст с процентами
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String powerText = String.format("Мощность: %d%%", (int) (power * 100));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(powerText, barX + (barWidth - fm.stringWidth(powerText)) / 2,
                barY + barHeight - 8);

        // Обводка
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(barX, barY, barWidth, barHeight);
    }
}
