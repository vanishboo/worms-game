package ru.itis.dis403.server.weapons;

import ru.itis.dis403.server.model.*;
import ru.itis.dis403.common.WeaponType;

import java.util.ArrayList;
import java.util.List;

import static ru.itis.dis403.common.Constants.HEIGHT;


public class ExplosionHandler {
    private final Arena arena;



    public ExplosionHandler(Arena arena) {
        this.arena = arena;
    }


    public Explosion explode(Projectile projectile, int x, int y, Player p1, Player p2) {
        WeaponType weapon = projectile.weaponType;
        int radius = weapon.explosionRadius;
        int damage = weapon.damage;

        return explode(x, y, radius, damage, p1, p2);
    }


    public Explosion explode(int x, int y, int radius, int damage,
                             Player p1, Player p2) {
        Explosion explosion = new Explosion(x, y, radius);

        // Урон игрокам
        damagePlayersInRadius(x, y, radius, damage, p1, p2);

        // Урон объектам + проверка цепных взрывов
        damageObjectsInRadius(x, y, radius);

        return explosion;
    }

    private void damagePlayersInRadius(int cx, int cy, int radius, int damage,
                                       Player p1, Player p2) {
        for (Player p : new Player[]{p1, p2}) {
            double dx = (p.x + 20) - cx;
            double dy = (p.y - 20) - cy;
            double dist = Math.hypot(dx, dy);

            if (dist < radius) {
                // Урон уменьшается с расстоянием
                double damageRatio = 1.0 - (dist / radius);
                int actualDamage = (int) (damage * damageRatio);
                p.hp -= actualDamage;
                if (p.hp < 0) p.hp = 0;

                // ОТТАЛКИВАНИЕ от взрыва
                double angle = Math.atan2(dy, dx);
                double force = damageRatio * 7;
                p.vx = Math.cos(angle) * force;
                p.vy = Math.sin(angle) * force - 3.5;
                p.onGround = false;
                p.hasImpulse = true;

                System.out.println("Игрок " + p.id + " получил " + actualDamage +
                        " урона + отброс!");
            }
        }
    }


    private void damageObjectsInRadius(int cx, int cy, int radius) {
        // Урон платформам
        for (Platform p : arena.platforms) {
            if (p.destroyed) continue;

            double dist = Math.hypot((p.x + p.width/2) - cx, (p.y + p.height/2) - cy);
            if (dist < radius + 50) {
                p.takeDamage(60);
                if (p.destroyed) {
                    System.out.println("Платформа уничтожена");
                }
            }
        }
    }
}
