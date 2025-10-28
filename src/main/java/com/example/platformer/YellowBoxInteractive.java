package com.example.platformer;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public class YellowBoxInteractive implements InteractiveTile {
    private final double x, y, size;
    private final int row, col;
    private boolean used = false;

    public YellowBoxInteractive(int row, int col, int tileSize) {
        this.row = row; this.col = col;
        this.x = col * tileSize; this.y = row * tileSize; this.size = tileSize;
    }

    public Rectangle2D getBounds() { return new Rectangle2D(x, y, size, size); }

    public boolean isSolid() { return false; }

    public void update(double dt, Player player, Input input, Level level) {
        if (used) return;
        if (player.getHitboxBounds().intersects(getBounds())) {
            if (input.isPressedJump()) {
                if (player.hasMushroom() && player.hasSnowman() && player.hasCoin()) {
                    player.setHasMushroom(false);
                    player.setHasSnowman(false);
                    player.setHasCoin(false);
                    player.setHasGoldenKey(true);
                    used = true;
                    level.setFgTile(row, col, 0);
                    SoundManager.getInstance().playPickup();
                }
            }
        }
    }

    public void render(GraphicsContext gc, double offsetX, double offsetY) { }
}