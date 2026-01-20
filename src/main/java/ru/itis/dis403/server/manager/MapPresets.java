package ru.itis.dis403.server.manager;

import ru.itis.dis403.common.MapType;
import ru.itis.dis403.server.model.Arena;
import ru.itis.dis403.server.model.Platform;

import static ru.itis.dis403.common.Constants.*;


public class MapPresets {


    public static void generateClassicArena(Arena arena) {
        int[] ground = new int[WIDTH];
        for (int x = 0; x < WIDTH; x++) {
            if (x < 200) {
                ground[x] = 550 - (int)(Math.sin((x / 200.0) * Math.PI / 2) * 80);
            } else if (x > WIDTH - 200) {
                ground[x] = 550 - (int)(Math.sin(((WIDTH - x) / 200.0) * Math.PI / 2) * 80);
            } else {
                ground[x] = 550 + (int)(Math.sin((x - 200) / 100.0) * 15);
            }
        }
        arena.replaceGround(ground);


        arena.platforms.add(new Platform(50, 180, 160, 15, 70, Platform.PlatformType.WOOD));

        arena.platforms.add(new Platform(WIDTH - 210, 180, 160, 15, 70, Platform.PlatformType.WOOD));

        arena.platforms.add(new Platform(280, 400, 120, 15, 50, Platform.PlatformType.WOOD));
        arena.platforms.add(new Platform(450, 320, 100, 15, 50, Platform.PlatformType.WOOD));
        arena.platforms.add(new Platform(WIDTH / 2 - 70, 240, 140, 15, 60, Platform.PlatformType.METAL)); // Центральная прочная
        arena.platforms.add(new Platform(700, 320, 100, 15, 50, Platform.PlatformType.WOOD));
        arena.platforms.add(new Platform(870, 400, 120, 15, 50, Platform.PlatformType.WOOD));


        System.out.println("Классическая арена загружена");
    }


    public static void generateDesertCanyon(Arena arena) {
        int[] ground = new int[WIDTH];
        for (int x = 0; x < WIDTH; x++) {
            if (x < 150) {
                ground[x] = 400 + (int)(Math.pow((150 - x) / 150.0, 2) * 150);
            } else if (x > WIDTH - 150) {
                ground[x] = 400 + (int)(Math.pow((x - (WIDTH - 150)) / 150.0, 2) * 150);
            } else {
                double center = WIDTH / 2.0;
                double dist = Math.abs(x - center) / (WIDTH / 2.0 - 150);
                ground[x] = 560 - (int)(Math.pow(1 - dist, 2) * 80);
            }
        }
        arena.replaceGround(ground);


        arena.platforms.add(new Platform(70, 260, 100, 15, 60, Platform.PlatformType.METAL));
        arena.platforms.add(new Platform(WIDTH - 170, 260, 100, 15, 60, Platform.PlatformType.METAL));

        arena.platforms.add(new Platform(250, 380, 120, 15, 50, Platform.PlatformType.WOOD));
        arena.platforms.add(new Platform(WIDTH - 370, 380, 120, 15, 50, Platform.PlatformType.WOOD));

        arena.platforms.add(new Platform(180, 520, 200, 15, 70, Platform.PlatformType.METAL));
        arena.platforms.add(new Platform(WIDTH - 380, 520, 200, 15, 70, Platform.PlatformType.METAL));

        arena.platforms.add(new Platform(WIDTH / 2 - 90, 420, 180, 15, 60, Platform.PlatformType.WOOD));


        System.out.println("Пустынный каньон загружен");
    }


    public static void generateIceFortress(Arena arena) {
        int[] ground = new int[WIDTH];
        int baseY = 520;
        for (int x = 0; x < WIDTH; x++) {
            ground[x] = baseY + (int)(Math.sin(x / 180.0) * 40 + Math.sin(x / 80.0) * 20);
        }
        arena.replaceGround(ground);


        arena.platforms.add(new Platform(60, 420, 110, 15, 50, Platform.PlatformType.ICE));
        arena.platforms.add(new Platform(50, 320, 100, 15, 50, Platform.PlatformType.ICE));
        arena.platforms.add(new Platform(70, 220, 90, 15, 50, Platform.PlatformType.ICE));

        arena.platforms.add(new Platform(WIDTH - 170, 420, 110, 15, 50, Platform.PlatformType.ICE));
        arena.platforms.add(new Platform(WIDTH - 150, 320, 100, 15, 50, Platform.PlatformType.ICE));
        arena.platforms.add(new Platform(WIDTH - 160, 220, 90, 15, 50, Platform.PlatformType.ICE));

        arena.platforms.add(new Platform(WIDTH / 2 - 150, 360, 100, 15, 50, Platform.PlatformType.METAL));
        arena.platforms.add(new Platform(WIDTH / 2 + 50, 360, 100, 15, 50, Platform.PlatformType.METAL));
        arena.platforms.add(new Platform(WIDTH / 2 - 100, 260, 200, 15, 70, Platform.PlatformType.ICE)); // Верх крепости

        arena.platforms.add(new Platform(300, 480, 80, 15, 40, Platform.PlatformType.ICE));
        arena.platforms.add(new Platform(WIDTH - 380, 480, 80, 15, 40, Platform.PlatformType.ICE));


        System.out.println("Ледяная крепость загружена");
    }




    public static void loadMap(Arena arena, MapType mapType) {
        arena.platforms.clear();

        switch (mapType) {
            case CLASSIC_ARENA:
                generateClassicArena(arena);
                break;
            case DESERT_CANYON:
                generateDesertCanyon(arena);
                break;
            case ICE_FORTRESS:
                generateIceFortress(arena);
                break;
        }
    }
}