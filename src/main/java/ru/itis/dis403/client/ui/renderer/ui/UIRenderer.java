package ru.itis.dis403.client.ui.renderer.ui;

import ru.itis.dis403.client.ui.renderer.IRenderer;
import ru.itis.dis403.common.WeaponType;

import java.awt.*;
import java.util.Map;


public class UIRenderer implements IRenderer {

    private final int width;
    private final int height;
    private final long turnTimeRemaining;
    private final int currentTurn;
    private final String player1Nick;
    private final String player2Nick;
    private final int myPlayerId;
    private final WeaponType selectedWeapon;
    private final Map<WeaponType, Integer> weaponCooldowns;


    public UIRenderer(int width, int height, long turnTimeRemaining,
                      int currentTurn, String player1Nick, String player2Nick,
                      int myPlayerId, WeaponType selectedWeapon,
                      Map<WeaponType, Integer> weaponCooldowns) {
        this.width = width;
        this.height = height;
        this.turnTimeRemaining = turnTimeRemaining;
        this.currentTurn = currentTurn;
        this.player1Nick = player1Nick;
        this.player2Nick = player2Nick;
        this.myPlayerId = myPlayerId;
        this.selectedWeapon = selectedWeapon;
        this.weaponCooldowns = weaponCooldowns;
    }

    @Override
    public void render(Graphics2D g2) {
        drawTopHUD(g2);
        drawWeaponPanel(g2);
        drawBottomHints(g2);
    }


    private void drawTopHUD(Graphics2D g2) {
        // Тёмная панель сверху
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, width, 60);

        // Таймер
        int seconds = (int) (turnTimeRemaining / 1000);
        Color timerColor = seconds > 10 ? Color.GREEN :
                seconds > 5 ? Color.ORANGE : Color.RED;
        g2.setColor(timerColor);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        String timeStr = String.format("%02d", seconds);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(timeStr, (width - fm.stringWidth(timeStr)) / 2, 40);

        // Чей ход
        String turnText = "Ход: " + (currentTurn == 1 ? player1Nick : player2Nick);
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        g2.drawString(turnText, 20, 35);

        // Индикатор своего хода
        if (myPlayerId == currentTurn) {
            g2.setColor(new Color(50, 255, 50));
            g2.fillOval(width - 50, 20, 20, 20);
            g2.setColor(Color.WHITE);
            g2.drawString("ВАШ ХОД", width - 150, 35);
        }
    }


    private void drawWeaponPanel(Graphics2D g2) {
        int panelWidth = 230;
        int panelHeight = 300;
        int panelX = width - panelWidth - 10;
        int panelY = 70;

        // Фон панели
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);

        // Заголовок
        g2.setColor(new Color(255, 200, 50));
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("ОРУЖИЕ", panelX + 60, panelY + 25);

        // Список оружия
        int yOffset = panelY + 50;
        for (WeaponType weapon : WeaponType.values()) {
            drawWeaponSlot(g2, weapon, panelX + 10, yOffset);
            yOffset += 50;
        }
    }


    private void drawWeaponSlot(Graphics2D g2, WeaponType weapon, int x, int y) {
        boolean selected = (weapon == selectedWeapon);
        int cooldown = weaponCooldowns.getOrDefault(weapon, 0);
        boolean available = (cooldown == 0);

        // Фон слота
        if (selected) {
            g2.setColor(new Color(100, 150, 255, 150));
        } else if (available) {
            g2.setColor(new Color(50, 50, 50, 150));
        } else {
            g2.setColor(new Color(80, 30, 30, 150));
        }
        g2.fillRoundRect(x, y, 210, 40, 10, 10);

        // Название оружия
        g2.setColor(available ? Color.WHITE : new Color(150, 150, 150));
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(weapon.displayName, x + 10, y + 25);

        // Кулдаун
        if (cooldown > 0) {
            g2.setColor(new Color(255, 100, 100));
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString("КД: " + cooldown, x + 160, y + 25);
        }

        // Рамка выбранного
        if (selected) {
            g2.setColor(new Color(255, 255, 100));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(x, y, 210, 40, 10, 10);
        }
    }


    private void drawBottomHints(Graphics2D g2) {
        if (myPlayerId != currentTurn) return;

        // Полупрозрачная панель снизу
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, height - 40, width, 40);

        // Текст подсказок
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String hints = "A/D - движение | SPACE - прыжок | 1-5 - оружие | ЛКМ - прицел/выстрел";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hints, (width - fm.stringWidth(hints)) / 2, height - 15);
    }
}
