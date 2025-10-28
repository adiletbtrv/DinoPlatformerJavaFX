package com.example.platformer;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public class KeyItem implements InteractiveTile {
    private final double x, y, size;
    private final int row, col;
    private boolean taken = false;

    public KeyItem(double x, double y, int row, int col) {
        this.x = x; this.y = y; this.size = 32;
        this.row = row; this.col = col;
    }

    public Rectangle2D getBounds() { return new Rectangle2D(x, y, size, size); }

    public boolean isSolid() { return false; }

    public void update(double dt, Player player, Input input, Level level) {
        if (taken) return;
        if (player.getHitboxBounds().intersects(getBounds())) {
            if (!player.hasGoldenKey()) {
                player.setHasGoldenKey(true);
                level.setFgTile(row, col, 0);
                taken = true;
            }
        }
    }

    public void render(GraphicsContext gc, double offsetX, double offsetY) { }
}