package com.example.platformer;

import javafx.geometry.Rectangle2D;

public class Player {
    private double x, y;
    private final double width, height;
    private double velX, velY;
    private boolean canJump = false;

    //hitbox relative to sprite top-left
    private double hbOffsetX = 0;
    private double hbOffsetY = 0;
    private double hbWidth;
    private double hbHeight;

    // sprite support
    private Sprite sprite;
    private boolean facingRight = true; // for flipping the sprite when drawing

    // control & state flags
    private boolean controlLocked = false; // prevents player movement (used during respawn)
    private boolean onRope = false;

    // inventory
    private boolean hasMushroom = false;
    private boolean hasSnowman = false;
    private boolean hasCoin = false;
    private boolean hasGoldenKey = false;
    private boolean hasDiamond = false;

    public Player(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // default hitbox: slightly narrower and shorter than the sprite, bottom-aligned
        setHitbox(Math.round(width * 0.2), Math.round(height * 0.12),
                Math.round(width * 0.6), Math.round(height * 0.80));
    }

    public void setHitbox(double offsetX, double offsetY, double hbWidth, double hbHeight) {
        this.hbOffsetX = offsetX;
        this.hbOffsetY = offsetY;
        this.hbWidth = hbWidth;
        this.hbHeight = hbHeight;
    }

    // returns hitbox bounds
    public Rectangle2D getHitboxBounds() {
        return new Rectangle2D(x + hbOffsetX, y + hbOffsetY, hbWidth, hbHeight);
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    public double getHitboxLeft() { return x + hbOffsetX; }
    public double getHitboxRight() { return x + hbOffsetX + hbWidth; }
    public double getHitboxTop() { return y + hbOffsetY; }
    public double getHitboxBottom() { return y + hbOffsetY + hbHeight; }

    // adjust X so hitbox right equals targetX (useful when colliding on the right)
    public void setXForHitboxRight(double targetHitboxRightX) {
        double newX = targetHitboxRightX - hbOffsetX - hbWidth;
        this.x = Math.floor(newX);
    }

    // adjust X so hitbox left equals targetX (useful when colliding on the left)
    public void setXForHitboxLeft(double targetHitboxLeftX) {
        double newX = targetHitboxLeftX - hbOffsetX;
        this.x = Math.floor(newX);
    }

    // adjust Y so hitbox bottom equals targetY (place on top of tile)
    public void setYForHitboxBottom(double targetHitboxBottomY) {
        double newY = targetHitboxBottomY - hbOffsetY - hbHeight;
        this.y = Math.floor(newY);
    }

    // adjust Y so hitbox top equals targetY (bumped head)
    public void setYForHitboxTop(double targetHitboxTopY) {
        double newY = targetHitboxTopY - hbOffsetY;
        this.y = Math.floor(newY);
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public double getVelX() { return velX; }
    public void setVelX(double velX) { this.velX = velX; }
    public double getVelY() { return velY; }
    public void setVelY(double velY) { this.velY = velY; }

    public boolean canJump() { return canJump; }
    public void setCanJump(boolean canJump) { this.canJump = canJump; }

    //sprite API
    public Sprite getSprite() { return sprite; }
    public void setSprite(Sprite sprite) { this.sprite = sprite; }

    public boolean isFacingRight() { return facingRight; }
    public void setFacingRight(boolean facingRight) { this.facingRight = facingRight; }

    //control lock
    public boolean isControlLocked() { return controlLocked; }
    public void setControlLocked(boolean locked) { this.controlLocked = locked; }

    // rope state
    public boolean isOnRope() { return onRope; }
    public void setOnRope(boolean onRope) { this.onRope = onRope; }

    // nventory
    public boolean hasMushroom() { return hasMushroom; }
    public void setHasMushroom(boolean v) { this.hasMushroom = v; }

    public boolean hasSnowman() { return hasSnowman; }
    public void setHasSnowman(boolean v) { this.hasSnowman = v; }

    public boolean hasCoin() { return hasCoin; }
    public void setHasCoin(boolean v) { this.hasCoin = v; }

    public boolean hasGoldenKey() { return hasGoldenKey; }
    public void setHasGoldenKey(boolean v) { this.hasGoldenKey = v; }

    public boolean hasDiamond() { return hasDiamond; }
    public void setHasDiamond(boolean v) { this.hasDiamond = v; }
}