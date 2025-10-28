package com.example.platformer;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private final int cols;
    private final int rows;
    private final int tileSize;
    private final int[][] solidLayer;
    private final int[][] decoLayer;
    private final List<InteractiveTile> interactive = new ArrayList<>();

    public Level(int tileDisplaySize) {
        this.tileSize = tileDisplaySize;
        this.cols = 160;
        this.rows = 12;
        this.solidLayer = new int[rows][cols];
        this.decoLayer = new int[rows][cols];
        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) { solidLayer[r][c] = 0; decoLayer[r][c] = 0; }
    }

    public void addInteractive(InteractiveTile it) { interactive.add(it); }

    public int getCols() { return cols; }
    public int getRows() { return rows; }
    public int getTileSize() { return tileSize; }
    public double getWidthPx() { return cols * tileSize; }
    public double getHeightPx() { return rows * tileSize; }

    public int getSolidTile(int r, int c) { if (r < 0 || r >= rows || c < 0 || c >= cols) return 0; return solidLayer[r][c]; }
    public int getDecoTile(int r, int c) { if (r < 0 || r >= rows || c < 0 || c >= cols) return 0; return decoLayer[r][c]; }

    public void setSolid(int r, int c, int idx) { if (r < 0 || r >= rows || c < 0 || c >= cols) return; solidLayer[r][c] = idx; }
    public void setDeco(int r, int c, int idx) { if (r < 0 || r >= rows || c < 0 || c >= cols) return; decoLayer[r][c] = idx; }

    public void setFgTile(int row, int col, int tileIndex) { setDeco(row, col, tileIndex); }
    public int getFgTile(int row, int col) { return getDecoTile(row, col); }

    public List<Tile> getCollisionTiles() {
        List<Tile> out = new ArrayList<>();
        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) {
            int idx = solidLayer[r][c];
            if (idx != 0) out.add(new Tile(c * tileSize, r * tileSize, tileSize, tileSize, true));
        }
        return out;
    }

    public List<InteractiveTile> getInteractiveTiles() { return interactive; }

    public void fillGroundSafe(int startCol, int endCol, int groundRow, Tileset tileset, int leftIdx, int midIdx, int rightIdx) {
        if (groundRow < 0 || groundRow >= rows) return;

        for (int c = startCol; c < endCol && c < cols; c++) {
            if (c < 0) continue;

            if (c == startCol) {
                solidLayer[groundRow][c] = leftIdx;
            } else if (c == endCol - 1) {
                solidLayer[groundRow][c] = rightIdx;
            } else {
                solidLayer[groundRow][c] = midIdx;
            }

            //fill below ground with dirt
            if (groundRow + 1 < rows) {
                solidLayer[groundRow + 1][c] = TileAtlas.DIRT_BLOCK_TOP_CENTER.index(tileset);
            }
            if (groundRow + 2 < rows) {
                solidLayer[groundRow + 2][c] = TileAtlas.DIRT_BLOCK_BOTTOM_CENTER.index(tileset);
            }
        }
    }

    public int findGroundRow() {
        for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) if (solidLayer[r][c] != 0) return r;
        return rows - 1;
    }

    public double getSpawnX() { return Math.max(0, tileSize * 4); }
    public double getSpawnY() { return Math.max(0, findGroundRow() * tileSize - tileSize); }
}