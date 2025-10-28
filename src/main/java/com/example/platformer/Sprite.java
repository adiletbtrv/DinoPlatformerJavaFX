package com.example.platformer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class Sprite {
    private final Image sheet;
    private final int frameWidth;
    private final int frameHeight;
    private final int totalFrames;
    private final int startFrame;
    private final int columns;
    private double frameDuration;
    private double elapsed = 0;
    private int currentFrame = 0;
    private final int[] bottomPadding;
    private final int maxVisibleHeight;

    public Sprite(Image sheet, int frameWidth, int frameHeight, int totalFrames, double frameDuration, int startFrame) {
        this.sheet = sheet;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.totalFrames = Math.max(0, totalFrames);
        this.frameDuration = Math.max(0.001, frameDuration);
        this.startFrame = Math.max(0, startFrame);
        this.columns = Math.max(1, (int) Math.floor(sheet.getWidth() / frameWidth));
        this.bottomPadding = new int[Math.max(1, this.totalFrames)];
        int maxVis = 0;
        try {
            PixelReader pr = sheet.getPixelReader();
            if (pr != null) {
                for (int f = 0; f < this.totalFrames; f++) {
                    int abs = startFrame + f;
                    int col = abs % columns;
                    int row = abs / columns;
                    int fx = col * frameWidth;
                    int fy = row * frameHeight;
                    int pad = 0;
                    outer:
                    for (int y = frameHeight - 1; y >= 0; y--) {
                        for (int x = 0; x < frameWidth; x++) {
                            int argb = pr.getArgb(fx + x, fy + y);
                            int alpha = (argb >> 24) & 0xff;
                            if (alpha > 16) { pad = frameHeight - 1 - y; break outer; }
                        }
                    }
                    bottomPadding[f] = pad;
                    int vis = frameHeight - pad;
                    if (vis > maxVis) maxVis = vis;
                }
            } else {
                for (int f = 0; f < this.totalFrames; f++) bottomPadding[f] = 0;
                maxVis = frameHeight;
            }
        } catch (Exception ex) {
            for (int f = 0; f < this.totalFrames; f++) bottomPadding[f] = 0;
            maxVis = frameHeight;
        }
        this.maxVisibleHeight = Math.max(1, maxVis);
    }

    public void update(double dt) {
        if (totalFrames <= 0) return;
        elapsed += dt;
        while (elapsed >= frameDuration) {
            elapsed -= frameDuration;
            currentFrame = (currentFrame + 1) % totalFrames;
        }
    }

    public void setFrameDuration(double seconds) { this.frameDuration = Math.max(0.001, seconds); }

    public void reset() { currentFrame = 0; elapsed = 0; }

    public Image getSheet() { return sheet; }

    public int getSrcX() { int absoluteIndex = startFrame + currentFrame; int col = absoluteIndex % columns; return col * frameWidth; }

    public int getSrcY() { int absoluteIndex = startFrame + currentFrame; int row = absoluteIndex / columns; return row * frameHeight; }

    public int getFrameWidth() { return frameWidth; }

    public int getFrameHeight() { return frameHeight; }

    public int getCurrentFrameIndex() { return currentFrame; }

    public double getFrameYOffsetForCurrent(double displayScale) {
        if (totalFrames <= 0) return 0;
        int f = currentFrame;
        int pad = bottomPadding[f];
        int vis = frameHeight - pad;
        int delta = maxVisibleHeight - vis;
        return delta * displayScale;
    }
}