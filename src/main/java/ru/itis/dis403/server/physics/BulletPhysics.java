package ru.itis.dis403.server.physics;

import ru.itis.dis403.server.model.*;

import java.util.ArrayList;
import java.util.List;

import static ru.itis.dis403.common.Constants.*;


public class BulletPhysics {

    private final Arena arena;


    public BulletPhysics(Arena arena) {
        this.arena = arena;
    }


    public List<BulletHitEvent> update(List<Bullet> bullets, Player p1, Player p2) {
        List<BulletHitEvent> events = new ArrayList<>();
        List<Bullet> toRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            if (!bullet.active) {
                toRemove.add(bullet);
                continue;
            }

            bullet.update();

            // Вылет за границы
            if (bullet.x < 0 || bullet.x >= WIDTH || bullet.y < 0 || bullet.y > HEIGHT) {
                bullet.deactivate();
                toRemove.add(bullet);
                continue;
            }

            // Попадание в землю
            int bx = (int) bullet.x;
            int by = (int) bullet.y;
            if (bx >= 0 && bx < arena.getGround().length && by >= arena.getY(bx)) {
                bullet.deactivate();
                toRemove.add(bullet);
                events.add(new BulletHitEvent(BulletHitType.GROUND, bullet, bx, by));
                continue;
            }

            // Попадание в платформы
            Platform hitPlatform = checkPlatformHit(bullet);
            if (hitPlatform != null) {
                hitPlatform.takeDamage(bullet.damage);
                bullet.deactivate();
                toRemove.add(bullet);
                events.add(new BulletHitEvent(BulletHitType.PLATFORM, bullet, (int)bullet.x, (int)bullet.y, hitPlatform));
                continue;
            }

            // Проверка попадания в игроков
            if (!bullet.hasHit) {
                Player target = (bullet.ownerId == 1) ? p2 : p1;
                double dx = (target.x + 20) - bullet.x;
                double dy = (target.y - 20) - bullet.y;
                double dist = Math.hypot(dx, dy);

                if (dist < 20) {

                    target.hp -= bullet.damage;
                    if (target.hp < 0) target.hp = 0;

                    // Отталкивание
                    double angle = Math.atan2(bullet.vy, bullet.vx);
                    target.vx = Math.cos(angle) * 5;
                    target.vy = -2.5;
                    target.onGround = false;
                    target.hasImpulse = true;

                    bullet.hasHit = true;
                    bullet.deactivate();
                    toRemove.add(bullet);
                    events.add(new BulletHitEvent(BulletHitType.PLAYER, bullet, (int)bullet.x, (int)bullet.y, target));
                }
            }
        }

        bullets.removeAll(toRemove);
        return events;
    }


    private Platform checkPlatformHit(Bullet bullet) {
        for (Platform platform : arena.platforms) {
            if (platform.destroyed) continue;
            if (platform.intersects(bullet.x, bullet.y)) {
                return platform;
            }
        }
        return null;
    }



    public static class BulletHitEvent {
        public final BulletHitType type;
        public final Bullet bullet;
        public final int x;
        public final int y;
        public final Object target; // Platform или Player

        public BulletHitEvent(BulletHitType type, Bullet bullet, int x, int y) {
            this(type, bullet, x, y, null);
        }

        public BulletHitEvent(BulletHitType type, Bullet bullet, int x, int y, Object target) {
            this.type = type;
            this.bullet = bullet;
            this.x = x;
            this.y = y;
            this.target = target;
        }
    }

    /**
     * Тип попадания пули.
     */
    public enum BulletHitType {
        GROUND,    // Попадание в землю
        PLATFORM,  // Попадание в платформу
        PLAYER     // Попадание в игрока
    }
}
