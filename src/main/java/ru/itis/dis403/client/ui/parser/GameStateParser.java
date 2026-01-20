package ru.itis.dis403.client.ui.parser;

import ru.itis.dis403.client.model.*;
import ru.itis.dis403.client.ui.map.MapManager;
import ru.itis.dis403.common.WeaponType;
import ru.itis.dis403.server.model.Bullet;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 *
 * Сервер каждые 16мс отправляет строку с полным состоянием игры.
 * Этот класс разбирает эту строку и обновляет всё что видит юзер.
 * 1. ServerListener получает пакет UPDATE с длинной строкой
 * 2. GamePanel вызывает gameStateParser.applyState(строка)
 * 3. Этот класс парсит строку и обновляет модели (игроки, пули, взрывы)
 * 4. GamePanel рисует обновлённую модель на экране
 */
public class GameStateParser {

    private final MapManager mapManager;

    private final GameStateView state;

    private final List<Platform> platforms;

    public GameStateParser(MapManager mapManager, GameStateView state,
                           List<Platform> platforms) {
        this.mapManager = mapManager;
        this.state = state;
        this.platforms = platforms;
    }


    public void applyMap(String data) {
        try {
            String[] parts = data.split(",");
            int[] ground = new int[parts.length];

            for (int i = 0; i < parts.length; i++) {
                ground[i] = Integer.parseInt(parts[i].trim());
            }

            mapManager.setGround(ground);
        } catch (Exception e) {
            System.err.println("Ошибка парсинга карты: " + e.getMessage());
        }
    }


    public void updateMap(String data) {
        try {
            String[] parts = data.split(",");
            int[] ground = mapManager.getGround();
            if (ground == null) return;

            for (int i = 0; i < parts.length && i < ground.length; i++) {
                ground[i] = Integer.parseInt(parts[i].trim());
            }
        } catch (Exception e) {
            System.err.println("Ошибка обновления карты: " + e.getMessage());
        }
    }


    public void applyObjects(String data) {
        try {
            platforms.clear();
            String[] platformsData = data.split("\\|");

            for (String pData : platformsData) {
                if (pData.trim().isEmpty()) continue;

                if (pData.startsWith("PLAT:")) {
                    String[] parts = pData.substring(5).split(",");

                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int width = Integer.parseInt(parts[2]);
                    int height = Integer.parseInt(parts[3]);
                    int hp = Integer.parseInt(parts[4]);
                    int maxHp = Integer.parseInt(parts[5]);
                    String type = parts.length > 6 ? parts[6] : "WOOD";

                    platforms.add(new Platform(x, y, width, height, hp, maxHp, type));
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка парсинга объектов: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void applyState(String data, long[] turnTimeRemaining, WeaponType[] selectedWeapon,
                           String[] player1Nick, String[] player2Nick,
                           Map<WeaponType, Integer> weaponCooldowns,
                           Point[] projectile, List<BulletTrail> bullets) {
        try {

            String[] sections = data.split(";");
            if (sections.length < 6) return;

            // === ПАРСИМ СЕКЦИИ 0-5 ===

            // [0] чей ход
            state.setCurrentTurn(Integer.parseInt(sections[0].trim()));

            // [1] сколько времени осталось (в миллисекундах)
            turnTimeRemaining[0] = Long.parseLong(sections[1].trim());

            // [2] выбранное оружие
            selectedWeapon[0] = WeaponType.valueOf(sections[2].trim());

            // [3][4] ники игроков
            player1Nick[0] = sections[3].trim();
            player2Nick[0] = sections[4].trim();

            // [5] КУЛДАУНЫ ОРУЖИЯ
            // формат: "RIFLE:5,BAZOOKA:2,GRENADE:0"
            String[] cdData = sections[5].split(",");
            weaponCooldowns.clear();

            for (String cd : cdData) {
                if (cd.trim().isEmpty()) continue;

                String[] parts = cd.split(":");
                if (parts.length == 2) {
                    try {
                        WeaponType weapon = WeaponType.valueOf(parts[0].trim());
                        int cooldown = Integer.parseInt(parts[1].trim());
                        weaponCooldowns.put(weapon, cooldown);
                    } catch (Exception ignored) {}
                }
            }

            // === ПАРСИМ СЕКЦИЮ [6] (САМАЯ СЛОЖНАЯ ЧАСТЬ) ===

            String mainPart = sections[6].trim();
            String projectilePart = null;
            String bulletsData = null;
            String explosionPart = null;

            // РАЗБИРАЕМ СТРОКУ ЗАДОМ НАПЕРЁД

            // 1) ИЩЕМ ВЗРЫВ (в конце после #EXPL#)
            if (mainPart.contains("#EXPL#")) {
                String[] splitExpl = mainPart.split("#EXPL#");
                mainPart = splitExpl[0];
                explosionPart = splitExpl.length > 1 ? splitExpl[1] : null;
            }

            // 2) ИЩЕМ ПУЛИ (после #BULLETS#)
            if (mainPart.contains("#BULLETS#")) {
                String[] splitBullets = mainPart.split("#BULLETS#");
                mainPart = splitBullets[0];
                bulletsData = splitBullets.length > 1 ? splitBullets[1] : null;
            }

            // 3) ИЩЕМ СНАРЯД (после первого #)
            if (mainPart.contains("#")) {
                String[] split = mainPart.split("#");
                mainPart = split[0].trim();
                projectilePart = split.length > 1 ? split[1].trim() : null;
            }

            // === ПАРСИМ ИГРОКОВ ===
            // формат: "1 320.5 450.2 100 | 2 480.3 450.2 85"
            String[] players = mainPart.split("\\|");

            for (String playerStr : players) {
                String[] parts = playerStr.trim().split(" ");

                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    int hp = Integer.parseInt(parts[3]);

                    if (id == 1) {
                        state.getP1().setX(x);
                        state.getP1().setY(y);
                        state.getP1().setHp(Math.max(0, hp));
                    } else if (id == 2) {
                        state.getP2().setX(x);
                        state.getP2().setY(y);
                        state.getP2().setHp(Math.max(0, hp));
                    }
                }
            }

            // === ПАРСИМ СНАРЯД (граната/ракета в полёте) ===
            if (projectilePart != null && !projectilePart.isEmpty()) {
                String[] parts = projectilePart.split(" ");

                if (parts.length == 2) {
                    projectile[0] = new Point(
                            (int) Double.parseDouble(parts[0]),
                            (int) Double.parseDouble(parts[1])
                    );
                }
            } else {
                projectile[0] = null;
            }

            // === ПАРСИМ ПУЛИ (следы от автомата/пистолета) ===

            if (bulletsData != null && !bulletsData.isEmpty()) {
                bullets.clear();
                String[] bulletsArray = bulletsData.split(";");

                for (String bulletStr : bulletsArray) {
                    if (bulletStr.trim().isEmpty()) continue;

                    String[] parts = bulletStr.split(",");

                    if (parts.length == 2) {
                        int bx = (int) Double.parseDouble(parts[0]);
                        int by = (int) Double.parseDouble(parts[1]);
                        bullets.add(new BulletTrail(bx, by));
                    }
                }
            }


        } catch (Exception e) {
            System.err.println("Ошибка парсинга состояния: " + e.getMessage());
        }
    }

}
