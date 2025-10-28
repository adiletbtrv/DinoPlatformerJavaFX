package com.example.platformer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Tileset {
    private final Image sheet;
    private final int tileW;
    private final int tileH;
    private final int spacingX;
    private final int spacingY;
    private final int cols;
    private final int rows;

    public Tileset(Image sheet, int tileW, int tileH, int spacingX, int spacingY, int cols, int rows) {
        this.sheet = sheet;
        this.tileW = tileW;
        this.tileH = tileH;
        this.spacingX = spacingX;
        this.spacingY = spacingY;
        this.cols = Math.max(1, cols);
        this.rows = Math.max(1, rows);
    }

    public Image getSheet() { return sheet; }
    public int getTileWidth() { return tileW; }
    public int getTileHeight() { return tileH; }

    public void drawTile(GraphicsContext gc, int tileIndex, double dx, double dy, double dw, double dh) {
        if (tileIndex <= 0) return;
        int idx = tileIndex - 1;
        int atlasCol = idx % cols;
        int atlasRow = idx / cols;
        if (atlasRow >= rows) return;

        int sx = atlasCol * (tileW + spacingX);
        int sy = atlasRow * (tileH + spacingY);

        int maxSX = Math.max(0, (int)Math.round(sheet.getWidth()) - tileW);
        int maxSY = Math.max(0, (int)Math.round(sheet.getHeight()) - tileH);
        if (sx < 0) sx = 0;
        if (sy < 0) sy = 0;
        if (sx > maxSX) sx = maxSX;
        if (sy > maxSY) sy = maxSY;

        gc.drawImage(sheet, sx, sy, tileW, tileH, dx, dy, dw, dh);
    }

    public int indexFrom(int atlasCol, int atlasRow) {
        if (atlasCol < 0 || atlasCol >= cols || atlasRow < 0 || atlasRow >= rows) return 0;
        return atlasRow * cols + atlasCol + 1;
    }
}