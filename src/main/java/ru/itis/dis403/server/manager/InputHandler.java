package ru.itis.dis403.server.manager;

import ru.itis.dis403.common.WeaponType;
import ru.itis.dis403.server.model.Bullet;
import ru.itis.dis403.server.model.Player;
import ru.itis.dis403.server.ServerGameState;
import ru.itis.dis403.server.storage.PlayerStatsManager;

import static ru.itis.dis403.common.Constants.*;


public class InputHandler {

    private final ServerGameState state;
    private final PlayerStatsManager statsManager;



    private long lastBulletFiredTime = 0;
    private int bulletsToFire = 0;
    private WeaponType currentFiringWeapon = null;
    private Player currentShooter = null;
    private double shootAngle = 0;
    private boolean isFiring = false;


    public InputHandler(ServerGameState state, PlayerStatsManager statsManager) {
        this.state = state;
        this.statsManager = statsManager;
    }


    public void handleMove(int playerId, String direction) {
        Player p = state.getPlayer(playerId);
        // Если игрока толкают - не может управлять
        if (p.hasImpulse) {
            return;
        }

        double speed = p.onGround ? PLAYER_MOVE_SPEED : PLAYER_MOVE_SPEED * PLAYER_AIR_CONTROL;

        if ("LEFT".equals(direction)) {
            p.vx = -speed;
        } else if ("RIGHT".equals(direction)) {
            p.vx = speed;
        }
    }

    public void handleMoveStop(int playerId) {
        state.getPlayer(playerId).vx = 0;
    }


    public void handleJump(int playerId) {
        Player p = state.getPlayer(playerId);

        // Прыгать можно только на земле
        if (p.onGround) {
            p.vy = -PLAYER_JUMP_POWER;
            p.onGround = false;
            System.out.println("Игрок " + playerId + " прыгнул");
        }
    }


    public boolean handleSelectWeapon(int playerId, String weaponName) {
        try {
            WeaponType weapon = WeaponType.valueOf(weaponName);

            // Проверяем доступность
            if (!state.isWeaponAvailable(playerId, weapon)) {
                int cd = state.getRemainingCooldown(playerId, weapon);
                System.out.println(weapon.displayName + " не готово (осталось " + cd + " ходов)");
                return false;
            }

            state.currentWeapon = weapon;
            System.out.println("Выбрано оружие: " + weapon.displayName);
            return true;
        } catch (Exception e) {
            System.err.println("Неверное оружие: " + weaponName);
            return false;
        }
    }


    public boolean handleShot(int playerId, double dx, double dy, double power) {
        if (state.projectile.active) return false;
        if (isFiring) return false;

        WeaponType weapon = state.currentWeapon;
        Player shooter = state.getPlayer(playerId);

        // ПРОВЕРЯЕМ ДОСТУПНОСТЬ
        if (!state.isWeaponAvailable(playerId, weapon)) {
            System.out.println("Оружие на кулдауне");
            return false;
        }

        // СРАЗУ СТАВИМ НА КД
        state.useWeapon(playerId, weapon);

        if (weapon == WeaponType.PISTOL || weapon == WeaponType.RIFLE) {
            // Пистолет/Автомат - начинаем очередь выстрелов
            shootBullets(shooter, dx, dy, weapon);
        } else if (weapon == WeaponType.AIRSTRIKE) {
            // Авиаудар - снаряд летит вертикально вниз
            state.projectile.launch(
                    dx,              // X-координата курсора
                    0,               // Стартуем с верха экрана
                    0,               // Без горизонтальной скорости
                    weapon.speed,    // Скорость падения вниз
                    playerId,
                    weapon
            );
            System.out.println("Воздушный удар по X=" + (int)dx);
        } else {
            // Граната/Базука - снаряд с траекторией
            double len = Math.hypot(dx, dy);
            if (len < 1) return false;

            dx /= len;
            dy /= len;

            double speed = weapon.speed * power;
            double vx = dx * speed;
            double vy = dy * speed;

            state.projectile.launch(
                    shooter.x + 20,
                    shooter.y - 20,
                    vx, vy, playerId, weapon
            );
        }

        statsManager.recordShot(state.getPlayerNickname(playerId), false);
        System.out.println("Выстрел из " + weapon.displayName);
        return true;
    }


    private void shootBullets(Player shooter, double dx, double dy, WeaponType weapon) {
        double angle = Math.atan2(dy, dx);
        this.currentShooter = shooter;
        this.shootAngle = angle;
        this.currentFiringWeapon = weapon;
        this.bulletsToFire = weapon.shotsCount;
        this.isFiring = true;
        this.lastBulletFiredTime = System.currentTimeMillis();

        fireOneBullet();
        System.out.println("Начата стрельба из " + weapon.displayName +
                " (" + bulletsToFire + " выстрелов)");
    }

    /**
     * Выпускает одну пулю из очереди.
     */
    private void fireOneBullet() {
        if (currentShooter == null || currentFiringWeapon == null) return;

        Bullet bullet = new Bullet();

        // Разброс для автомата
        double spread = (currentFiringWeapon == WeaponType.RIFLE) ?
                (Math.random() - 0.5) * 0.2 : 0;
        double shotAngle = shootAngle + spread;

        double vx = Math.cos(shotAngle) * 15;
        double vy = Math.sin(shotAngle) * 15;

        bullet.launch(
                currentShooter.x + 20,
                currentShooter.y - 20,
                vx, vy,
                currentShooter.id,
                currentFiringWeapon.damage
        );

        state.bullets.add(bullet);
        bulletsToFire--;


        System.out.println("Выстрел, Осталось: " + bulletsToFire);
    }


    public boolean updateFiring() {
        if (isFiring && bulletsToFire > 0) {
            long timeSinceLastShot = System.currentTimeMillis() - lastBulletFiredTime;
            long delay = (currentFiringWeapon == WeaponType.PISTOL) ? 300 : 100;

            if (timeSinceLastShot >= delay) {
                fireOneBullet();
                lastBulletFiredTime = System.currentTimeMillis();
            }
        }

        // Если стрельба закончена и все пули улетели - конец хода
        if (isFiring && bulletsToFire == 0 && state.bullets.isEmpty()) {
            System.out.println("Стрельба завершена, переход хода");
            isFiring = false;
            currentShooter = null;
            currentFiringWeapon = null;
            return true; // Нужно закончить ход
        }

        return false;
    }

}
