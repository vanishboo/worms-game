package ru.itis.dis403.server.physics;

import ru.itis.dis403.server.model.Arena;
import ru.itis.dis403.server.model.Platform;
import ru.itis.dis403.server.model.Player;

import static ru.itis.dis403.common.Constants.*;


public class PlayerPhysics {

    private final Arena arena;
    private static final double MAX_STEP_HEIGHT = 18.0;
    private static final double SLOPE_FRICTION = 0.92;
    private static final double AIR_RESISTANCE = 0.98;
    private static final double GROUND_FRICTION = 0.88;

    public PlayerPhysics(Arena arena) {
        this.arena = arena;
    }


    public void update(Player p) {
        applyImpulse(p);
        updateHorizontalMovement(p);
        updateVerticalMovement(p);
    }


    private void applyImpulse(Player p) {
        if (!p.hasImpulse) {
            return;
        }

        p.vx *= AIR_RESISTANCE;

        // Если скорость стала слишком маленькой - считаем что импульс закончился
        if (Math.abs(p.vx) < 0.15) {
            p.vx = 0;
            p.hasImpulse = false;
        }
    }


    private void updateHorizontalMovement(Player p) {
        double oldX = p.x;
        double newX = p.x + p.vx;

        // Ограничение по ширине арены
        if (newX < 0) {
            newX = 0;
        }
        if (newX >= arena.getGround().length - 1) {
            newX = arena.getGround().length - 1;
        }

        if (p.onGround && !p.hasImpulse) {
            int oldCenterX = (int)(oldX + 20);
            int newCenterX = (int)(newX + 20);

            if (isValidX(oldCenterX) && isValidX(newCenterX)) {
                int oldGroundY = arena.getY(oldCenterX);
                int newGroundY = arena.getY(newCenterX);

                if (newGroundY < oldGroundY - MAX_STEP_HEIGHT) {
                    newX = oldX;
                    p.vx = 0;
                } else if (newGroundY < oldGroundY) {
                    p.vx *= SLOPE_FRICTION;
                }
            }
        }

        p.x = newX;
    }


    private void updateVerticalMovement(Player p) {
        int centerX = (int)(p.x + 20);
        if (centerX >= arena.getGround().length) {
            centerX = arena.getGround().length - 1;
        }

        int groundY = arena.getY(centerX);

        // Применяем гравитацию если игрок в воздухе
        if (!p.onGround) {
            p.vy += PLAYER_GRAVITY;
        }

        double nextY = p.y + p.vy;

        // платформa под ногами
        Platform standingPlatform = null;
        for (Platform platform : arena.platforms) {
            if (platform.destroyed) continue;

            // Используем метод проверки "стоит на платформе"
            if (platform.isStandingOn(p.x, p.y, p.vy)) {
                standingPlatform = platform;
                break;
            }

            // Или если падаем И пересекли верхнюю грань
            if (p.x + 40 > platform.x && p.x < platform.x + platform.width) {
                if (p.y <= platform.y && nextY >= platform.y && p.vy >= 0) {
                    standingPlatform = platform;
                    break;
                }
            }
        }

        if (standingPlatform != null) {
            // Стоим на платформе
            landOnPlatform(p, standingPlatform);
        } else {
            // Падаем или прыгаем
            p.y += p.vy;

            // Проверка приземления на землю
            if (p.y >= groundY) {
                landOnGround(p, groundY);
            } else {
                p.onGround = false;
            }
        }

        // Защита от выхода за нижнюю границу экрана
        if (p.y > HEIGHT) {
            p.y = HEIGHT;
            p.vy = 0;
            p.onGround = true;
        }
    }


    private void landOnPlatform(Player p, Platform platform) {
        p.y = platform.y;
        p.vy = 0;
        p.onGround = true;

        // трение платформы к импульсу
        if (p.hasImpulse) {
            p.vx *= platform.friction;
            if (Math.abs(p.vx) < 0.2) {
                p.vx = 0;
                p.hasImpulse = false;
            }
        }
    }


    private void landOnGround(Player p, int groundY) {
        p.y = groundY;
        p.vy = 0;
        p.onGround = true;

        if (p.hasImpulse) {
            p.vx *= GROUND_FRICTION;
            if (Math.abs(p.vx) < 0.2) {
                p.vx = 0;
                p.hasImpulse = false;
            }
        }
    }

    private boolean isValidX(int x) {
        return x >= 0 && x < arena.getGround().length;
    }
}
