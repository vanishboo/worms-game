package ru.itis.dis403.client.ui.renderer.ui;

import ru.itis.dis403.client.model.GameStateView;
import ru.itis.dis403.client.ui.renderer.IRenderer;

import java.awt.*;


public class AimingRenderer implements IRenderer {

    private final GameStateView state;

    private final int myPlayerId;

    private final int currentTurn;

    private final Point mouse;


    public AimingRenderer(GameStateView state, int myPlayerId, int currentTurn, Point mouse) {
        this.state = state;
        this.myPlayerId = myPlayerId;
        this.currentTurn = currentTurn;
        this.mouse = mouse;
    }

    @Override
    public void render(Graphics2D g2) {
        if (myPlayerId != currentTurn || mouse == null) return;

        // Получаем позицию текущего игрока
        double playerX, playerY;
        if (myPlayerId == 1) {
            playerX = state.getP1().getX() + 20;
            playerY = state.getP1().getY() - 20;
        } else {
            playerX = state.getP2().getX() + 20;
            playerY = state.getP2().getY() - 20;
        }

        // Линия прицеливания
        g2.setColor(new Color(255, 255, 100, 200));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0, new float[]{10, 5}, 0));
        g2.drawLine((int) playerX, (int) playerY, mouse.x, mouse.y);

        // Прицел в позиции мыши
        g2.setColor(new Color(255, 0, 0, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(mouse.x - 10, mouse.y - 10, 20, 20);
        g2.drawLine(mouse.x - 15, mouse.y, mouse.x - 5, mouse.y);
        g2.drawLine(mouse.x + 5, mouse.y, mouse.x + 15, mouse.y);
        g2.drawLine(mouse.x, mouse.y - 15, mouse.x, mouse.y - 5);
        g2.drawLine(mouse.x, mouse.y + 5, mouse.x, mouse.y + 15);
    }
}
