package com.example.platformer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dino Platformer");

        Pane root = new Pane();
        Canvas canvas = new Canvas(1920, 1080);
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        //fullscreen
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("Press ESC to exit fullscreen");

        //canvas responsive to window size
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        primaryStage.show();

        Input input = new Input(scene);

        //load tilesets
        int srcTileW = 18;
        int srcTileH = 18;
        int spacing = 1;
        int atlasCols = 20;
        int atlasRows = 9;

        Image tilesImg = null;
        Image bgImg = null;
        try (var is = Main.class.getResourceAsStream("/com/example/platformer/assets/tiles.png")) {
            if (is != null) tilesImg = new Image(is);
        } catch (Exception ignored) {}
        try (var is2 = Main.class.getResourceAsStream("/com/example/platformer/assets/bg_tiles.png")) {
            if (is2 != null) bgImg = new Image(is2);
        } catch (Exception ignored) {}

        Tileset tileset = null;
        Tileset bgTileset = null;
        if (tilesImg != null) {
            tileset = new Tileset(tilesImg, srcTileW, srcTileH, spacing, spacing, atlasCols, atlasRows);
        }
        if (bgImg != null) {
            bgTileset = new Tileset(bgImg, srcTileW, srcTileH, spacing, spacing, atlasCols, atlasRows);
        }

        int displayTileSize = 36;

        //create level
        Level level = new Level(displayTileSize);
        LevelBuilder builder = new LevelBuilder(level, tileset);
        builder.buildCompleteLevel();

        //start game engine
        GameEngine engine = new GameEngine(canvas, input, level, tileset, bgTileset);
        ImageInfo.printImageSize("/com/example/platformer/assets/hero_spritesheet.png");
        engine.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}