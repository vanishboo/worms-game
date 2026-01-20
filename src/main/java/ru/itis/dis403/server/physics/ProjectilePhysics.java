package ru.itis.dis403.server.physics;

import ru.itis.dis403.server.model.Arena;
import ru.itis.dis403.server.model.Platform;
import ru.itis.dis403.server.model.Player;
import ru.itis.dis403.server.model.Projectile;

import static ru.itis.dis403.common.Constants.*;


public class ProjectilePhysics {

    private final Arena arena;
    public ProjectilePhysics(Arena arena) {
        this.arena = arena;
    }
    public CollisionResult update(Projectile projectile, Player p1, Player p2) {
        if (!projectile.active) return null;

        double oldX = projectile.x;
        double oldY = projectile.y;

        // Обновляем позицию снаряда (применяем гравитацию)
        projectile.update(projectile.weaponType.gravity);

        double newX = projectile.x;
        double newY = projectile.y;

        // Проверка столкновения с платформами
        Platform hitPlatform = checkPlatformCollision(oldX, oldY, newX, newY);
        if (hitPlatform != null) {
            return new CollisionResult(CollisionType.PLATFORM, (int)newX, hitPlatform.y);
        }

        // Проверка попадания в землю
        int ix = (int) newX;
        if (ix >= 0 && ix < arena.getGround().length) {
            if (newY >= arena.getY(ix)) {
                return new CollisionResult(CollisionType.GROUND, ix, (int)newY);
            }
        }

        // Вылет за границы
        if (newX < 0 || newX >= WIDTH || newY > HEIGHT) {
            return new CollisionResult(CollisionType.OUT_OF_BOUNDS, (int)newX, (int)newY);
        }

        // Проверка попадания в игроков
        Player hitPlayer = checkPlayerCollision(projectile, p1, p2);
        if (hitPlayer != null) {
            return new CollisionResult(CollisionType.PLAYER, (int)projectile.x, (int)projectile.y, hitPlayer);
        }

        return null; // Снаряд продолжает лететь
    }


    private Platform checkPlatformCollision(double oldX, double oldY, double newX, double newY) {
        for (Platform p : arena.platforms) {
            if (p.destroyed) continue;

            // Если пересекли платформу сверху вниз
            if (oldX >= p.x && oldX <= p.x + p.width) {
                if (oldY <= p.y && newY >= p.y) {
                    return p;
                }
            }
        }
        return null;
    }


    private Player checkPlayerCollision(Projectile projectile, Player p1, Player p2) {
        // Не попадаем в самого себя
        Player target = (projectile.ownerId == 1) ? p2 : p1;
        if (target.id == projectile.ownerId) return null;

        double dx = (target.x + 20) - projectile.x;
        double dy = (target.y - 20) - projectile.y;

        if (Math.hypot(dx, dy) < 25) {
            return target;
        }

        return null;
    }


    public static class CollisionResult {
        public final CollisionType type;
        public final int x;
        public final int y;
        public final Player hitPlayer;

        public CollisionResult(CollisionType type, int x, int y) {
            this(type, x, y, null);
        }

        public CollisionResult(CollisionType type, int x, int y, Player hitPlayer) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.hitPlayer = hitPlayer;
        }
    }


    public enum CollisionType {
        PLATFORM,       // Попадание в платформу
        GROUND,         // Попадание в землю
        PLAYER,         // Попадание в игрока
        OUT_OF_BOUNDS   // Вылет за границы
    }
}
