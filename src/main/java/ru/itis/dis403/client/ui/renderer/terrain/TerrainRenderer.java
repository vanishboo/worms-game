package ru.itis.dis403.client.ui.renderer.terrain;


import ru.itis.dis403.client.ui.renderer.IRenderer;
import ru.itis.dis403.common.MapType;

import java.awt.*;

public class TerrainRenderer implements IRenderer {

    private final int[] ground;



    private final MapType mapType;


    public TerrainRenderer(int[] ground, MapType mapType) {
        this.ground = ground;
        this.mapType = mapType;
    }

    @Override
    public void render(Graphics2D g2) {
        if (ground == null) return;

        int[] xPoints = new int[ground.length + 2];
        int[] yPoints = new int[ground.length + 2];

        for (int i = 0; i < ground.length; i++) {
            xPoints[i] = i;
            yPoints[i] = ground[i];
        }

        xPoints[ground.length] = ground.length - 1;
        yPoints[ground.length] = 700;
        xPoints[ground.length + 1] = 0;
        yPoints[ground.length + 1] = 700;

        Color groundColor = mapType != null ?
                new Color(mapType.groundColor[0], mapType.groundColor[1], mapType.groundColor[2]) :
                new Color(139, 90, 43); // Fallback

        GradientPaint groundPaint = new GradientPaint(
                0, 400, groundColor,
                0, 700, groundColor.darker()
        );

        g2.setPaint(groundPaint);
        g2.fillPolygon(xPoints, yPoints, ground.length + 2);



    }

}
