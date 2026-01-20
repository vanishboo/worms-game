package ru.itis.dis403.client.ui.resources;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Загрузчик спрайтов и анимаций.
 */
public class SpriteLoader {


    public static BufferedImage[] loadWalkAnimation(String path) {
        try {
            BufferedImage sheet = loadSprite(path);
            if (sheet == null) {
                System.err.println("⚠️ Используются стандартные текстуры для: " + path);
                return createFallbackSprites(path.contains("blue") ? Color.BLUE : Color.PINK);
            }

            // Нарезаем спрайтшит: 6 кадров, каждый 32x32
            int framesCount = 6;
            int frameWidth = sheet.getWidth() / framesCount; // Должно быть 32
            int frameHeight = sheet.getHeight();            // Должно быть 32

            BufferedImage[] frames = new BufferedImage[framesCount];

            for (int i = 0; i < framesCount; i++) {
                frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            }
            return frames;
        } catch (Exception e) {
            e.printStackTrace();
            return createFallbackSprites(Color.GRAY);
        }
    }



    public static BufferedImage loadSprite(String path) {
        try {
            // Пробуем загрузить как ресурс
            InputStream is = SpriteLoader.class.getResourceAsStream(path);
            if (is == null) {
                // Пробуем без начального слеша
                if (path.startsWith("/")) {
                    is = SpriteLoader.class.getResourceAsStream(path.substring(1));
                }
            }

            if (is != null) {
                return ImageIO.read(is);
            }

            System.err.println("Не удалось найти спрайт: " + path);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Создает простые цветные прямоугольники, если спрайты не найдены.
     */
    private static BufferedImage[] createFallbackSprites(Color color) {
        BufferedImage[] frames = new BufferedImage[4];
        for (int i = 0; i < 4; i++) {
            BufferedImage img = new BufferedImage(32, 48, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(color);
            g.fillRect(4, 8, 24, 40); // Тело
            g.setColor(Color.WHITE);
            g.fillOval(8, 0, 16, 16); // Голова

            // Анимация ног
            g.setColor(Color.BLACK);
            if (i % 2 == 0) {
                g.fillRect(8, 38, 6, 10);
                g.fillRect(18, 38, 6, 10);
            } else {
                g.fillRect(6, 36, 6, 10);
                g.fillRect(20, 36, 6, 10);
            }

            g.dispose();
            frames[i] = img;
        }
        return frames;
    }
}
