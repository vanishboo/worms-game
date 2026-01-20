package ru.itis.dis403.client.ui.renderer.entity;

import ru.itis.dis403.client.model.BulletTrail;
import ru.itis.dis403.client.ui.renderer.IRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ProjectileRenderer implements IRenderer {
    private final Point projectile;
    private final List<BulletTrail> bullets;


    public ProjectileRenderer(Point projectile, List<BulletTrail> bullets) {
        this.projectile = projectile;
        this.bullets = bullets;
    }

    @Override
    public void render(Graphics2D g2) {
        // Снаряд (граната/ракета)
        if (projectile != null) {
            drawProjectile(g2);
        }
        for (BulletTrail bullet : bullets) {
            drawBullet(g2, bullet);
        }
    }

    private void drawBullet(Graphics2D g2, BulletTrail bullet) {

        g2.setColor(new Color(255, 200, 0));
        g2.fillOval(bullet.getX() - 3, bullet.getY() - 3, 6, 6);

    }

    private void drawProjectile(Graphics2D g2) {
        int x = projectile.x;
        int y = projectile.y;

        for (int i = 3; i > 0; i--) {
            g2.setColor(new Color(255, 100, 0, 30 - i * 8));
            g2.fillOval(x - 8 - i * 3, y - 8 - i * 3, 16 + i * 6, 16 + i * 6);
        }

        // Снаряд
        g2.setColor(new Color(200, 50, 0));
        g2.fillOval(x - 8, y - 8, 16, 16);


        g2.setColor(new Color(255, 150, 50));
        g2.fillOval(x - 5, y - 5, 6, 6);


        g2.setColor(new Color(150, 150, 150, 100));
        for (int i = 0; i < 3; i++) {
            g2.fillOval(x - i * 5, y + i * 3, 4 - i, 4 - i);
        }
    }

}
