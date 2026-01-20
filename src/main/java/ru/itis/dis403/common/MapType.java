package ru.itis.dis403.common;


public enum MapType {
    CLASSIC_ARENA("Классическая арена",
            new int[]{135, 206, 250}, // Голубое небо
            new int[]{55, 145, 13}),  // Зеленая земля

    DESERT_CANYON("Пустынный каньон",
            new int[]{255, 200, 150}, // Оранжевое небо
            new int[]{210, 160, 100}), // Песчаная земля

    ICE_FORTRESS("Ледяная крепость",
            new int[]{200, 230, 255}, // Холодное небо
            new int[]{220, 240, 255}); // Снежная земля

    public final String displayName;
    public final int[] skyColor;    // RGB неба
    public final int[] groundColor; // RGB земли

    MapType(String displayName, int[] skyColor, int[] groundColor) {
        this.displayName = displayName;
        this.skyColor = skyColor;
        this.groundColor = groundColor;
    }
}
