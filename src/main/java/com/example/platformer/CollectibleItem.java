package com.example.platformer;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public class CollectibleItem implements InteractiveTile {
    public enum Type { MUSHROOM, COIN, SNOWMAN }

    private final int row;
    private final int col;
    private final int tileIndex;
    private final double x;
    private final double y;
    private final double size;
    private boolean collected;
    private final Tileset tileset;
    private final Type type;

    public CollectibleItem(int row, int col, int tileSize, Tileset tileset, Type type) {
        this.row = row;
        this.col = col;
        this.tileset = tileset;
        this.type = type;
        this.tileIndex = switch (type) {
            case MUSHROOM -> TileAtlas.MUSHROOM_1.index(tileset);
            case COIN -> TileAtlas.COIN.index(tileset);
            default -> TileAtlas.SNOWMAN.index(tileset);
        };
        this.x = col * tileSize;
        this.y = row * tileSize;
        this.size = tileSize;
        this.collected = false;
    }

    public Rectangle2D getBounds() { return new Rectangle2D(x, y, size, size); }

    public boolean isSolid() { return false; }

    public void update(double dt, Player player, Input input, Level level) {
        if (collected) return;
        if (player.getHitboxBounds().intersects(getBounds())) {
            collect(player, level);
            return;
        }
        double px = player.getX() + player.getWidth() * 0.5;
        double py = player.getY() + player.getHeight() * 0.5;
        double cx = x + size * 0.5;
        double cy = y + size * 0.5;
        double dx = Math.abs(px - cx);
        double dy = Math.abs(py - cy);
        double threshold = size * 0.6;
        if (dx <= threshold && dy <= threshold && input.isPressedJump()) collect(player, level);
    }

    private void collect(Player player, Level level) {
        if (collected) return;
        collected = true;
        switch (type) {
            case MUSHROOM -> player.setHasMushroom(true);
            case COIN -> player.setHasCoin(true);
            case SNOWMAN -> player.setHasSnowman(true);
        }
        if (level.getDecoTile(row, col) == tileIndex) level.setDeco(row, col, 0);
        SoundManager.getInstance().playPickup();
    }

    public void render(GraphicsContext gc, double offsetX, double offsetY) {
        if (collected) return;
        tileset.drawTile(gc, tileIndex, x - offsetX, y - offsetY, size, size);
    }

    public boolean isCollected() { return collected; }
}