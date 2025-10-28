package com.example.platformer;

public class LevelBuilder {
    private final Level level;
    private final Tileset tileset;

    public LevelBuilder(Level level, Tileset tileset) {
        this.level = level;
        this.tileset = tileset;
    }

    public void buildCompleteLevel() {
        int gr = 9;
        buildContinuousGround(gr);

        //section 1(0-30)
        createTree(gr, 5);
        createTree(gr, 15);
        createTree(gr, 25);

        createPlatform(gr - 3, 10, 5, PlatformType.NORMAL);
        createCoin(gr - 5, 12);

        //section 2(30-80)
        createTree(gr, 35);
        createTree(gr, 50);
        createMushroom(gr - 1, 59);
        createTree(gr, 65);
        createTree(gr, 75);

        createPlatform(gr - 3, 33, 4, PlatformType.NORMAL);
        createCoin(gr - 5, 34);

        createPlatform(gr - 4, 40, 4, PlatformType.NORMAL);
        createCoin(gr - 6, 41);

        createPlatform(gr - 3, 48, 5, PlatformType.NORMAL);
        createCoin(gr - 5, 50);

        createPlatform(gr - 2, 58, 3, PlatformType.NORMAL);
        createPlatform(gr - 3, 62, 3, PlatformType.NORMAL);
        createPlatform(gr - 4, 66, 3, PlatformType.NORMAL);
        createCoin(gr - 6, 67);

        createPlatform(gr - 5, 70, 5, PlatformType.NORMAL);
        createCoin(gr - 7, 72);

        //section 3(80-120)
        createPine(gr, 85);
        createPine(gr, 95);
        createPine(gr, 105);
        createPine(gr, 115);

        createPlatform(gr - 3, 90, 6, PlatformType.SNOWY);
        createSnowman(gr - 4, 92);

        createPlatform(gr - 2, 100, 3, PlatformType.SNOWY);
        createPlatform(gr - 4, 104, 3, PlatformType.SNOWY);
        createPlatform(gr - 6, 108, 3, PlatformType.SNOWY);
        createCoin(gr - 8, 109);

        createPlatform(gr - 4, 113, 3, PlatformType.SNOWY);
        createPlatform(gr - 2, 117, 3, PlatformType.SNOWY);

        //section 4(120-160)
        createTree(gr, 125);
        createTree(gr, 135);
        createTree(gr, 145);
        createTree(gr, 155);

        createPlatform(gr - 3, 128, 4, PlatformType.NORMAL);
        createCoin(gr - 5, 129);

        createPlatform(gr - 4, 135, 4, PlatformType.NORMAL);
        createCoin(gr - 6, 136);

        createPlatform(gr - 3, 142, 5, PlatformType.NORMAL);
        createCoin(gr - 5, 144);

        createPlatform(gr - 2, 150, 3, PlatformType.NORMAL);
        placeYellowBox(gr - 1, 152);

        placeLocker(gr - 1, 156);
    }


    private void buildContinuousGround(int groundRow) {
        //forest ground(0-50)
        for (int c = 0; c < 50; c++) {
            if (c == 0) {
                level.setSolid(groundRow, c, TileAtlas.GRASS_BLOCK_LEFT.index(tileset));
            } else {
                level.setSolid(groundRow, c, TileAtlas.GRASS_BLOCK_TOP.index(tileset));
            }
            level.setSolid(groundRow + 1, c, TileAtlas.DIRT_BLOCK_TOP_CENTER.index(tileset));
            level.setSolid(groundRow + 2, c, TileAtlas.DIRT_BLOCK_BOTTOM_CENTER.index(tileset));
        }

        //desert ground(50-90)
        for (int c = 50; c < 90; c++) {
            level.setSolid(groundRow, c, TileAtlas.SAND_BLOCK_TOP.index(tileset));
            level.setSolid(groundRow + 1, c, TileAtlas.DIRT_BLOCK_TOP_CENTER.index(tileset));
            level.setSolid(groundRow + 2, c, TileAtlas.DIRT_BLOCK_BOTTOM_CENTER.index(tileset));
        }

        //snow ground(90-130)
        for (int c = 90; c < 130; c++) {
            level.setSolid(groundRow, c, TileAtlas.SNOW_BLOCK_TOP.index(tileset));
            level.setSolid(groundRow + 1, c, TileAtlas.DIRT_BLOCK_TOP_CENTER.index(tileset));
            level.setSolid(groundRow + 2, c, TileAtlas.DIRT_BLOCK_BOTTOM_CENTER.index(tileset));
        }

        //final forest(130-160)
        for (int c = 130; c < 160; c++) {
            level.setSolid(groundRow, c, TileAtlas.GRASS_BLOCK_TOP.index(tileset));
            level.setSolid(groundRow + 1, c, TileAtlas.DIRT_BLOCK_TOP_CENTER.index(tileset));
            level.setSolid(groundRow + 2, c, TileAtlas.DIRT_BLOCK_BOTTOM_CENTER.index(tileset));

            if (c == 159) {
                level.setSolid(groundRow, c, TileAtlas.GRASS_BLOCK_RIGHT.index(tileset));
            }
        }
    }

    public void createPlatform(int row, int col, int length, PlatformType type) {
        int l = Math.max(1, length);
        int left = switch (type) {
            case MUSHROOM -> TileAtlas.MUSHROOM_PLATFORM_LEFT.index(tileset);
            case SNOWY -> TileAtlas.SNOWY_PLATFORM_LEFT.index(tileset);
            default -> TileAtlas.PLATFORM_LEFT.index(tileset);
        };
        int center = switch (type) {
            case SNOWY -> TileAtlas.SNOWY_PLATFORM_CENTER.index(tileset);
            default -> TileAtlas.PLATFORM_CENTER.index(tileset);
        };
        int right = switch (type) {
            case MUSHROOM -> TileAtlas.MUSHROOM_PLATFORM_RIGHT.index(tileset);
            case SNOWY -> TileAtlas.SNOWY_PLATFORM_RIGHT.index(tileset);
            default -> TileAtlas.PLATFORM_RIGHT.index(tileset);
        };
        for (int i = 0; i < l; i++) {
            if (i == 0) level.setSolid(row, col + i, left);
            else if (i == l - 1) level.setSolid(row, col + i, right);
            else level.setSolid(row, col + i, center);
        }
    }

    public void createMushroom(int row, int col) {
        level.setDeco(row - 1, col, TileAtlas.MUSHROOM_STEM_TOP.index(tileset));
        level.setDeco(row, col, TileAtlas.MUSHROOM_1.index(tileset));
        level.addInteractive(new CollectibleItem(row, col, level.getTileSize(), tileset, CollectibleItem.Type.MUSHROOM));
    }

    public void createCoin(int row, int col) {
        level.setDeco(row, col, TileAtlas.COIN.index(tileset));
        level.addInteractive(new CollectibleItem(row, col, level.getTileSize(), tileset, CollectibleItem.Type.COIN));
    }

    public void createSnowman(int row, int col) {
        level.setDeco(row, col, TileAtlas.SNOWMAN.index(tileset));
        level.addInteractive(new CollectibleItem(row, col, level.getTileSize(), tileset, CollectibleItem.Type.SNOWMAN));
    }

    public void createTree(int row, int col) {
        level.setDeco(row - 1, col, TileAtlas.TREE_CROWN.index(tileset));
        level.setDeco(row - 2, col, TileAtlas.TREE_TRUNK_MID.index(tileset));
        level.setDeco(row - 3, col, TileAtlas.TREE_TRUNK_TOP_LEAVES.index(tileset));
        level.setDeco(row - 4, col, TileAtlas.LEAVES_TOP_CENTER.index(tileset));
        level.setDeco(row - 4, col - 1, TileAtlas.LEAVES_TOP_LEFT.index(tileset));
        level.setDeco(row - 4, col + 1, TileAtlas.LEAVES_TOP_RIGHT.index(tileset));
        level.setDeco(row - 3, col - 1, TileAtlas.LEAVES_BOTTOM_LEFT.index(tileset));
        level.setDeco(row - 3, col + 1, TileAtlas.LEAVES_BOTTOM_RIGHT.index(tileset));
    }

    public void createCactus(int row, int col) {
        level.setDeco(row - 1, col, TileAtlas.CACTUS.index(tileset));
    }

    public void createDoor(int row, int col) {
        level.setDeco(row - 2, col, TileAtlas.DOOR_TOP.index(tileset));
        level.setSolid(row - 1, col, TileAtlas.DOOR.index(tileset));
    }

    public void createPine(int row, int col) {
        level.setDeco(row - 1, col, TileAtlas.PINE.index(tileset));
    }

    public void placeYellowBox(int row, int col) {
        level.setDeco(row, col, TileAtlas.YELLOW_BOX_CIRCLE.index(tileset));
        level.addInteractive(new YellowBoxInteractive(row, col, level.getTileSize()));
    }

    public void placeLocker(int row, int col) {
        level.setDeco(row, col, TileAtlas.YELLOW_LOCKER.index(tileset));
        level.addInteractive(new LockerInteractive(row, col, level.getTileSize(), TileAtlas.YELLOW_LOCKER.index(tileset), TileAtlas.DIAMOND.index(tileset)));
    }

    public enum Biome { FOREST, DESERT, TAIGA }
    public enum PlatformType { NORMAL, MUSHROOM, SNOWY }
}