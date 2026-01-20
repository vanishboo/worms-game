package ru.itis.dis403.common;

public enum WeaponType {
    PISTOL("Пистолет", 10, 0, 0, 0, 3, false, 10),           // БЕЗ КД
    RIFLE("Автомат", 8, 0, 0, 1, 9, false, 10),              // КД 1 ход
    BAZOOKA("Базука", 50, 45, 0.2, 2, 1, true, 18),          // КД 2 хода
    GRENADE("Граната", 50, 60, 0.6, 2, 1, true, 16),         // КД 2 хода
    AIRSTRIKE("Воздушный удар", 55, 80, 3, 3, 1, true, 4); // КД 3 хода

    public final String displayName;
    public final int damage;
    public final int explosionRadius;
    public final double gravity;
    public final int cooldown;
    public final int shotsCount;
    public final boolean isExplosive;
    public final double speed;

    WeaponType(String displayName, int damage, int explosionRadius, double gravity,
               int cooldown, int shotsCount, boolean isExplosive, double speed) {
        this.displayName = displayName;
        this.damage = damage;
        this.explosionRadius = explosionRadius;
        this.gravity = gravity;
        this.cooldown = cooldown;
        this.shotsCount = shotsCount;
        this.isExplosive = isExplosive;
        this.speed = speed;
    }
}
