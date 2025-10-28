package com.example.platformer;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class GameEngine {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Input input;
    private final Level level;
    private final Player player;
    private long lastTimeNs = 0;
    private static final double GRAVITY = 1400;
    private final Sprite heroIdle;
    private final Sprite heroRun;
    private final Tileset tileset;
    private final Tileset bgTileset;
    private boolean respawning = false;
    private double respawnStartY;
    private double respawnTargetY;
    private double respawnDuration = 0.8;
    private double respawnElapsed = 0;
    private final ProgressBarHUD hud;
    private double camX = 0;
    private double camY = 0;
    private static final double PLAYER_SCALE = 1.0;
    private Image backgroundImage;
    private double bgParallax = 0.5;
    private Font uiFont;
    private double smoothedVelX = 0.0;
    private static final double VEL_SMOOTHING_ALPHA = 6.0;
    private boolean showVictory = false;
    private double victoryOverlayAlpha = 0;
    private boolean runningState = false;

    public GameEngine(Canvas canvas, Input input, Level level, Tileset tileset, Tileset bgTileset) {
        this.tileset = tileset;
        this.bgTileset = bgTileset;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.input = input;
        this.level = level;

        int baseTile = level.getTileSize();
        double playerW = baseTile * PLAYER_SCALE;
        double playerH = baseTile * PLAYER_SCALE;
        double spawnX = Math.max(0, baseTile * 4);
        double spawnY = (level.findGroundRow() * baseTile) - baseTile;
        this.player = new Player(spawnX, spawnY, playerW, playerH);

        SoundManager.getInstance().init(getClass());
        SoundManager.getInstance().playBgm();


        try (var is = getClass().getResourceAsStream("/com/example/platformer/assets/background.jpg")) { if (is != null) backgroundImage = new Image(is); } catch (Exception ignored) {}
        try (var is = getClass().getResourceAsStream("/com/example/platformer/assets/PixelatedEleganceRegular.ttf")) { if (is != null) uiFont = Font.loadFont(is,18); else uiFont = Font.font("Monospaced",16); } catch (Exception ex) { uiFont = Font.font("Monospaced",16); }

        Image heroSheet = null;
        Sprite idle = null;
        Sprite run = null;
        try (var is = getClass().getResourceAsStream("/com/example/platformer/assets/hero_spritesheet.png")) {
            if (is != null) {
                heroSheet = new Image(is);
                int frameWidth = 24;
                int frameHeight = 24;
                int imgW = (int)Math.round(heroSheet.getWidth());
                int totalGlobalFrames = Math.max(0, imgW / frameWidth);
                int idleStart = 0;
                int idleCount = 4;
                int runStart = 4;
                int runCount = 8;
                if (idleStart + idleCount > totalGlobalFrames) idleCount = Math.max(0, totalGlobalFrames - idleStart);
                if (runStart + runCount > totalGlobalFrames) runCount = Math.max(0, totalGlobalFrames - runStart);
                if (idleCount > 0) idle = new Sprite(heroSheet, frameWidth, frameHeight, idleCount, 0.14, idleStart);
                if (runCount > 0) run = new Sprite(heroSheet, frameWidth, frameHeight, runCount, 0.11, runStart);
            }
        } catch (Exception ex) { System.err.println("Error loading spritesheet: " + ex.getMessage()); }

        this.heroIdle = idle;
        this.heroRun = run;

        if (heroSheet != null) autoConfigureHitboxFromSpritesheet(heroSheet,24,24);
        if (this.heroIdle != null) this.player.setSprite(this.heroIdle);

        gc.setImageSmoothing(false);
        this.hud = new ProgressBarHUD(level, player, tileset);

        camX = player.getX();
        camY = player.getY();
    }

    private void autoConfigureHitboxFromSpritesheet(Image sheet,int frameW,int frameH){
        var pr = sheet.getPixelReader();
        if (pr == null) return;
        int imgW = (int)Math.round(sheet.getWidth());
        int columns = Math.max(1, imgW / frameW);
        int totalFrames = (int)Math.round(sheet.getWidth()) / frameW;
        int minBottomPadding = frameH;
        for(int frame=0;frame<totalFrames;frame++){
            int fx=(frame%columns)*frameW;
            int fy=(frame/columns)*frameH;
            int pad=frameH;
            outer:
            for(int y=frameH-1;y>=0;y--){
                for(int x=0;x<frameW;x++){
                    int argb=pr.getArgb(fx+x,fy+y);
                    int alpha=(argb>>24)&0xff;
                    if(alpha>16){ pad=frameH-1-y; break outer; }
                }
            }
            minBottomPadding=Math.min(minBottomPadding,pad);
        }
        double scale = player.getHeight()/(double)frameH;
        double displayPadding = minBottomPadding * scale;
        int headClearance = Math.max(4,(int)Math.round(player.getHeight()*0.12));
        int hbHeight = (int)Math.round(player.getHeight() - displayPadding - headClearance);
        hbHeight = Math.max(8, Math.min((int)player.getHeight(), hbHeight));
        double hbWidth = Math.max(8, Math.round(player.getWidth()*0.55));
        double hbOffsetX = (player.getWidth()-hbWidth)/2.0;
        double hbOffsetY = player.getHeight()-hbHeight-displayPadding;
        hbOffsetX = Math.round(hbOffsetX);
        hbOffsetY = Math.round(hbOffsetY);
        player.setHitbox(hbOffsetX,hbOffsetY,hbWidth,hbHeight);
    }

    public void start(){
        AnimationTimer timer = new AnimationTimer(){
            @Override public void handle(long now){
                if(lastTimeNs==0) lastTimeNs=now;
                double delta=(now-lastTimeNs)/1_000_000_000.0;
                delta = Math.min(delta,0.05);
                update(delta);
                render();
                lastTimeNs=now;
            }
        };
        timer.start();
    }

    private void update(double dt){
        if(respawning){
            respawnElapsed += dt;
            double t = Math.min(1.0, respawnElapsed/respawnDuration);
            double ease = 1 - Math.pow(1 - t, 2);
            double newY = respawnStartY + (respawnTargetY - respawnStartY)*ease;
            player.setX(level.getSpawnX()); player.setY(newY); player.setVelX(0); player.setVelY(0); player.setControlLocked(true);
            if(t>=1.0){ respawning=false; respawnElapsed=0; player.setControlLocked(false); }
            for(InteractiveTile it: level.getInteractiveTiles()) it.update(dt,player,input,level);
            updateCamera(dt); return;
        }

        if(showVictory) return;

        double moveSpeed=260, accel=2000;
        double targetVelX=0;
        if(!player.isControlLocked()){
            if(input.isPressedLeft()) targetVelX -= moveSpeed;
            if(input.isPressedRight()) targetVelX += moveSpeed;
        }

        if(targetVelX>0) player.setFacingRight(true); else if(targetVelX<0) player.setFacingRight(false);

        if(!player.isOnRope()){
            if(player.getVelX() < targetVelX) player.setVelX(Math.min(player.getVelX() + accel*dt, targetVelX));
            else if(player.getVelX() > targetVelX) player.setVelX(Math.max(player.getVelX() - accel*dt, targetVelX));
            else if(Math.abs(player.getVelX())<1) player.setVelX(0);
            player.setVelY(player.getVelY() + GRAVITY*dt);
        } else {
            player.setVelY(0);
            player.setVelX(0);
        }

        if(!player.isControlLocked() && input.isPressedJump() && player.canJump()){
            player.setVelY(-560); player.setCanJump(false);
        }

        double newX = player.getX() + player.getVelX()*dt; player.setX(newX);
        List<Tile> collisionTiles = level.getCollisionTiles();
        for(Tile t: collisionTiles){
            if(!t.isSolid()) continue;
            if(player.getHitboxBounds().intersects(t.getBounds())){
                if(player.getVelX()>0) player.setXForHitboxRight(t.getX());
                else if(player.getVelX()<0) player.setXForHitboxLeft(t.getX()+t.getWidth());
                player.setVelX(0);
            }
        }

        double newY = player.getY() + player.getVelY()*dt; player.setY(newY);
        boolean onGround=false;
        for(Tile t: collisionTiles){
            if(!t.isSolid()) continue;
            if(player.getHitboxBounds().intersects(t.getBounds())){
                if(player.getVelY()>0){ player.setYForHitboxBottom(t.getY()); onGround=true; }
                else if(player.getVelY()<0){ player.setYForHitboxTop(t.getY()+t.getHeight()); }
                player.setVelY(0);
            }
        }
        player.setCanJump(onGround);

        List<InteractiveTile> interactive = level.getInteractiveTiles();
        for(InteractiveTile it: interactive) it.update(dt,player,input,level);
        interactive.removeIf(it -> (it instanceof CollectibleItem) && ((CollectibleItem)it).isCollected());

        double targetSmooth = player.getVelX();
        double alpha = clamp(dt * VEL_SMOOTHING_ALPHA, 0, 1);
        smoothedVelX += (targetSmooth - smoothedVelX) * alpha;

        double high = 70.0, low = 30.0;
        if(Double.isFinite(smoothedVelX) && Math.abs(smoothedVelX) > high && heroRun != null){
            if(!runningState){ player.setSprite(heroRun); runningState = true; }
        } else if(Double.isFinite(smoothedVelX) && Math.abs(smoothedVelX) < low && heroIdle != null){
            if(runningState){ player.setSprite(heroIdle); runningState = false; }
        }

        if(player.getSprite() != null) player.getSprite().update(dt);

        if(player.hasDiamond()) triggerVictory();
        if(player.getY() > level.getHeightPx() + level.getTileSize()*3) startRespawn();

        updateCamera(dt);
    }

    private void triggerVictory(){ if(showVictory) return; showVictory=true; player.setControlLocked(true); SoundManager.getInstance().playVictory(); }

    private void updateCamera(double dt){
        double targetCamX = player.getX() + player.getWidth()/2 - canvas.getWidth()/2;
        double targetCamY = player.getY() + player.getHeight()/2 - canvas.getHeight()/2;
        targetCamX = clamp(targetCamX,0, Math.max(0, level.getWidthPx()-canvas.getWidth()));
        targetCamY = clamp(targetCamY,0, Math.max(0, level.getHeightPx()-canvas.getHeight()));
        double smooth = 8.0;
        double alpha = clamp(dt * smooth, 0, 1);
        camX += (targetCamX - camX) * alpha;
        camY += (targetCamY - camY) * alpha;
    }

    private void startRespawn(){
        respawning=true; respawnElapsed=0; respawnStartY=-level.getTileSize()*4;
        respawnTargetY = level.getSpawnY() - (player.getHeight() - level.getTileSize());
        player.setVelX(0); player.setVelY(0); player.setControlLocked(true);
        if(heroIdle != null) player.setSprite(heroIdle);
    }

    private void render(){
        gc.setFill(Color.web("#DFF6F5")); gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        double tileW = level.getTileSize(), tileH = level.getTileSize();

        if(backgroundImage != null){
            double imgW = backgroundImage.getWidth(), imgH = backgroundImage.getHeight();
            if(imgW>0 && imgH>0){
                double bgOffsetX = - (camX * bgParallax) % imgW;
                double bgOffsetY = - (camY * bgParallax) % imgH;
                if(bgOffsetX>0) bgOffsetX -= imgW; if(bgOffsetY>0) bgOffsetY -= imgH;
                int cols = (int)Math.ceil(canvas.getWidth()/imgW)+2, rows = (int)Math.ceil(canvas.getHeight()/imgH)+2;
                for(int r=0;r<rows;r++) for(int c=0;c<cols;c++) gc.drawImage(backgroundImage, bgOffsetX + c*imgW, bgOffsetY + r*imgH, imgW, imgH);
            }
        } else {
            if(bgTileset != null){
                int bgCols = (int)Math.ceil(canvas.getWidth()/tileW), bgRows = (int)Math.ceil(canvas.getHeight()/tileH);
                for(int r=0;r<bgRows;r++) for(int c=0;c<bgCols;c++) bgTileset.drawTile(gc, bgTileset.indexFrom(0,0), c*tileW, r*tileH, tileW, tileH);
            }
        }

        double levelRenderOffsetX = Math.max(0, (canvas.getWidth()-level.getWidthPx())/2.0);
        double levelRenderOffsetY = Math.max(0, (canvas.getHeight()-level.getHeightPx())/2.0);
        double translateX = Math.round(-camX + levelRenderOffsetX), translateY = Math.round(-camY + levelRenderOffsetY);
        gc.save(); gc.translate(translateX, translateY);

        for(int r=0;r<level.getRows();r++) for(int c=0;c<level.getCols();c++){
            int deco = level.getDecoTile(r,c); if(deco!=0) tileset.drawTile(gc,deco,c*tileW,r*tileH,tileW,tileH);
        }

        for(int r=0;r<level.getRows();r++) for(int c=0;c<level.getCols();c++){
            int s = level.getSolidTile(r,c); if(s!=0) tileset.drawTile(gc,s,c*tileW,r*tileH,tileW,tileH);
        }

        for(InteractiveTile it: level.getInteractiveTiles())
            it.render(gc, 0, 0);  // Canvas is already transformed, no offset needed

        Sprite sp = player.getSprite();
        if(sp!=null){
            Image img = sp.getSheet();
            int sx = sp.getSrcX(), sy = sp.getSrcY(), sw = sp.getFrameWidth(), sh = sp.getFrameHeight();
            double scale = player.getHeight()/(double)sh;
            double yOffset = sp.getFrameYOffsetForCurrent(scale);
            int drawX = (int)Math.round(player.getX()), drawY = (int)Math.round(player.getY() - yOffset);
            int drawW = (int)Math.round(player.getWidth()), drawH = (int)Math.round(player.getHeight() + Math.round(yOffset));
            if(player.isFacingRight()) gc.drawImage(img,sx,sy,sw,sh,drawX,drawY,drawW,drawH);
            else { gc.save(); gc.translate(drawX+drawW, drawY); gc.scale(-1,1); gc.drawImage(img,sx,sy,sw,sh,0,0,drawW,drawH); gc.restore(); }
        } else { gc.setFill(Color.web("#FF4500")); gc.fillRoundRect(player.getX(),player.getY(),player.getWidth(),player.getHeight(),8,8); }

        gc.restore();

        hud.render(gc);

        String instructions = "use A/D or ←/→ to move  ↑ to jump/interact";
        gc.setFill(Color.color(0,0,0,0.6)); gc.setFont(uiFont); gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        double instrX = canvas.getWidth()/2.0, instrY = canvas.getHeight() - (level.getTileSize()*0.6) - 18;
        double paddingX = 10, textWidth = Math.min(canvas.getWidth()*0.8,640);
        gc.setFill(Color.color(0,0,0,0.35)); gc.fillRoundRect(instrX - textWidth/2 - paddingX, instrY - 20, textWidth + paddingX*2, 36,8,8);
        gc.setFill(Color.WHITE); gc.fillText(instructions, instrX, instrY);

        if(showVictory){
            victoryOverlayAlpha = Math.min(1.0, victoryOverlayAlpha+0.02);
            gc.setFill(Color.color(0,0,0,0.6*victoryOverlayAlpha));
            gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(uiFont.getName(),36));
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.fillText("congratulations! you won!", canvas.getWidth()/2, canvas.getHeight()/2 - 20);
            gc.setFont(Font.font(uiFont.getName(),18));
            gc.fillText("collected the diamond and completed the level", canvas.getWidth()/2, canvas.getHeight()/2 + 12);
        }
    }

    public void setBackgroundImage(Image img){ this.backgroundImage = img; }

    private double clamp(double v,double a,double b){ if(v<a) return a; if(v>b) return b; return v; }
}