package com.example.platformer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class ProgressBarHUD {
    private final Level level;
    private final Player player;
    private final Tileset tileset;
    private final Font font;

    public ProgressBarHUD(Level level, Player player, Tileset tileset) {
        this.level = level; this.player = player; this.tileset = tileset;
        Font f;
        try (var is = ProgressBarHUD.class.getResourceAsStream("/com/example/platformer/assets/PixelatedEleganceRegular.ttf")) {
            if (is != null) f = Font.loadFont(is, 18); else f = Font.font("Monospaced",14);
        } catch (Exception e) { f = Font.font("Monospaced",14); }
        this.font = f;
    }

    public void render(GraphicsContext gc) {
        double pad = 12;
        double barW = Math.min(720, gc.getCanvas().getWidth()*0.6);
        double barH = 14;
        double x = (gc.getCanvas().getWidth() - barW)/2.0;
        double y = pad + 6;
        double progress = clamp((player.getX() + player.getWidth()/2) / level.getWidthPx(), 0, 1);

        gc.setFill(Color.color(0,0,0,0.35));
        gc.fillRoundRect(x-6,y-6,barW+12,barH+64,8,8);

        gc.setFill(Color.web("#2b2b2b"));
        gc.fillRoundRect(x,y+20,barW,barH,6,6);

        gc.setFill(Color.web("#82f48a"));
        gc.fillRoundRect(x,y+20,barW*progress,barH,6,6);

        gc.setFill(Color.WHITE);
        gc.setFont(font);
        gc.setTextAlign(TextAlignment.CENTER);
        String percent = String.format("%d%%",(int)Math.round(progress*100));
        gc.fillText(percent,x+barW/2.0,y+16);

        double iconSize = Math.min(28, level.getTileSize()*0.72);
        double gap = iconSize * 0.18;
        double iconsX = x + 12;
        double iconsY = y + 44 - iconSize*0.5;

        tileset.drawTile(gc, TileAtlas.MUSHROOM_1.index(tileset), iconsX, iconsY, iconSize, iconSize);
        if (player.hasMushroom()) drawCheck(gc, iconsX, iconsY, iconSize);

        double coinX = iconsX + iconSize + gap;
        tileset.drawTile(gc, TileAtlas.COIN.index(tileset), coinX, iconsY, iconSize, iconSize);
        if (player.hasCoin()) drawCheck(gc, coinX, iconsY, iconSize);

        double snowX = coinX + iconSize + gap;
        tileset.drawTile(gc, TileAtlas.SNOWMAN.index(tileset), snowX, iconsY, iconSize, iconSize);
        if (player.hasSnowman()) drawCheck(gc, snowX, iconsY, iconSize);

        double remaining = Math.max(0, level.getWidthPx() - (player.getX()+player.getWidth()/2));
        double maxSpeed = 260;
        int secs = (int)Math.ceil(remaining/maxSpeed);
        gc.setFont(Font.font(font.getName(),12));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("est: "+secs+"s", x+barW-6, y+44);
    }

    private void drawCheck(GraphicsContext gc,double x,double y,double s){
        gc.setFill(Color.web("#82f48a"));
        gc.fillOval(x + s*0.62, y + s*0.62, s*0.30, s*0.30);
    }

    private double clamp(double v,double a,double b){ if(v<a) return a; if(v>b) return b; return v; }
}