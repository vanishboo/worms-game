package ru.itis.dis403.server.model;


public class Platform {
    public int x, y;
    public int width, height;
    public int hp;
    public final int maxHp;
    public boolean destroyed = false;

    public PlatformType type;

    public double friction = 0.85;
    public double bounciness = 0.0;

    public Platform(int x, int y, int width, int height, int hp) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hp = hp;
        this.maxHp = hp;

        this.type = randomType();
        applyTypeProperties();
    }

    public Platform(int x, int y, int width, int height, int hp, PlatformType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hp = hp;
        this.maxHp = hp;
        this.type = type;
        applyTypeProperties();
    }

    private PlatformType randomType() {
        double rand = Math.random();
        if (rand < 0.5) return PlatformType.WOOD;
        else if (rand < 0.85) return PlatformType.METAL;
        else return PlatformType.ICE;
    }

    private void applyTypeProperties() {
        switch (type) {
            case WOOD:
                friction = 0.85;
                bounciness = 0.0;
                break;
            case METAL:
                friction = 0.75;
                bounciness = 0.1;
                break;
            case ICE:
                friction = 0.98; // Скользкая!
                bounciness = 0.05;
                break;
        }
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            destroyed = true;
            hp = 0;
        }
    }


    public boolean intersects(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }


    // Проверка что игрок стоит сверху платформы (а не сбоку)
    public boolean isStandingOn(double playerX, double playerY, double playerVy) {
        // Горизонтально внутри?
        boolean horizontallyInside = playerX + 40 > x && playerX < x + width;

        // Вертикально на уровне верха
        boolean verticallyOnTop = Math.abs(playerY - y) < 3;

        // Падает вниз или стоит
        boolean movingDown = playerVy >= 0;

        return horizontallyInside && verticallyOnTop && movingDown;
    }

    /**
     * Сериализация для отправки клиенту
     * Формат: PLAT:x,y,width,height,hp,maxHp,type
     */
    public String serialize() {
        return String.format("PLAT:%d,%d,%d,%d,%d,%d,%s",
                x, y, width, height, hp, maxHp, type.name());
    }

    public enum PlatformType {
        WOOD,
        METAL,
        ICE
    }
}
