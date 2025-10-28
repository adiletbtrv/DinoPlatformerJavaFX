package com.example.platformer;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public class LockerInteractive implements InteractiveTile {
    private final double x, y, size;
    private final int row, col;
    private final int lockerTileIndex;
    private final int diamondTileIndex;
    private boolean opened = false;

    public LockerInteractive(int row, int col, int tileSize, int lockerTileIndex, int diamondTileIndex) {
        this.row = row; this.col = col;
        this.x = col * tileSize; this.y = row * tileSize; this.size = tileSize;
        this.lockerTileIndex = lockerTileIndex;
        this.diamondTileIndex = diamondTileIndex;
    }

    public Rectangle2D getBounds() { return new Rectangle2D(x, y, size, size); }

    public boolean isSolid() { return true; }

    public void update(double dt, Player player, Input input, Level level) {
        if (opened) return;
        if (player.getHitboxBounds().intersects(getBounds())) {
            if (input.isPressedJump() && player.hasGoldenKey()) {
                level.setFgTile(row, col, diamondTileIndex);
                opened = true;
                player.setHasDiamond(true);
                SoundManager.getInstance().playPickup();
            }
        }
    }

    public void render(GraphicsContext gc, double offsetX, double offsetY) { }
}