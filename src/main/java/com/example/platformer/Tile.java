package com.example.platformer;

import javafx.geometry.Rectangle2D;

public class Tile {
    private final double x, y, width, height;
    private final boolean solid;

    public Tile(double x, double y, double width, double height, boolean solid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.solid = solid;
    }

    public Rectangle2D getBounds() { return new Rectangle2D(x, y, width, height); }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isSolid() { return solid; }
}