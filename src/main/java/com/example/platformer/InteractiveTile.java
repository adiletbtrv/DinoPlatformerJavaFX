package com.example.platformer;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public interface InteractiveTile {
    Rectangle2D getBounds();
    boolean isSolid();
    void update(double dt, Player player, Input input, Level level);
    void render(GraphicsContext gc, double offsetX, double offsetY);
}