# Dino Platformer

Project overview
----------------
This is a compact 2D platformer prototype built with JavaFX and a Canvas-based renderer. The game uses a tile atlas and a small level builder to assemble a single level. Gameplay mechanics include running, jumping, collecting items, opening boxes/lockers, and a simple win condition (collect a diamond).

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/857e9bcb-9d1c-4620-87ea-9222b62d209f" />

Design choices
--------------
- Tile-based level represented with two integer grids: `solidLayer` (collision tiles) and `decoLayer` (visual/foreground tiles). This keeps rendering and collision simple and separate.
- Interactive objects implement `InteractiveTile` and are stored in a `List<InteractiveTile>` this decouples logic (pickup, box behavior) from rendering.
- Sprites are driven by a `Sprite` class that scans sprite frames at load time to compute bottom padding.
- A Canvas is used for deterministic drawing and manual transform control (camera translation and sprite flipping).
- AABB collision detection with per-axis resolution and gravity + jump impulse.

Project structure
-----------------
- Main.java - application entry; loads assets and starts GameEngine.
- GameEngine.java - main loop (update + render), camera, physics, HUD.
- Level/LevelBuilder - tile arrays and level construction helpers.
- Tileset/TileAtlas - tile atlas utilities and semantic tile names.
- Sprite.java - sprite-sheet animation + per-frame bottom padding.
- Player.java - player physics, hitbox and inventory.
- InteractiveTile implementations:
  - CollectibleItem.java
  - YellowBoxInteractive.java
  - LockerInteractive.java
  - KeyItem.java
- Tile.java, ProgressBarHUD.java, SoundManager.java, ImageInfo.java — utilities.
- Assets: placed under resources /com/example/platformer/assets (spritesheets, tiles.png, bg tiles, fonts, sfx)

How to run
----------
Requirements:
- JDK 11+ (or later recommended).
- JavaFX SDK or a JDK distribution with JavaFX (e.g., LibericaFX, BellSoft).
- An IDE configured to include JavaFX modules on the module path or classpath.

Steps:
1. Ensure the assets are present in `src/main/resources/com/example/platformer/assets/` 
2. Build project
3. Run `Main` (Main.java)

Controls:
- Move: A / D or Left / Right arrows
- Jump / interact: W / Up / Space
- Exit fullscreen: ESC 

Video link
----------
- Video (walkthrough, gameplay and explanation): https://drive.google.com/file/d/1ufE4W170UOCZ8gcxwTG4RDCnjQnj-Yuu/view?usp=sharing

Algorithms and data structures
-----------------------------
Data structures:
- int[][] solidLayer and decoLayer (Level) stores tile indices for collision and decoration.
- List<InteractiveTile> (Level) dynamic list of interactive entities.
- Per-object doubles for x, y, size, velocity, used for physics.
- Sprite uses int[] bottomPadding per frame for pixel-perfect vertical alignment.

Key algorithms:
- Collision detection: axis-aligned bounding boxes using Rectangle2D intersection
- Collision resolution: when an intersection occurs, player's position is adjusted so the hitbox edges align with the tile edge with helper functions (setXForHitboxRight/Left, setYForHitboxBottom/Top)
- Movement and physics with simple Euler integration and gravity acceleration
- Sprite animation with elapsed-time accumulation updates current frame every frameDuration seconds
- Camera smoothing and velocity smoothing: linear interpolation and exponential smoothing produce smoother camera/player animation transitions
- Respawn easing

Challenges and how they were addressed
-----------------------------------
- Polished feel with simple methods: camera smoothing and velocity smoothing added substantial perceived quality with smaller code.
- Collision resolution corner cases: using hitbox that is smaller and bottom-aligned reduces snagging on tile corners.

Improvements and future work
--------------------------
Potential improvements:
- Spatial partitioning (chunks or tile buckets) for collision checks to scale to larger maps instead of scanning the whole grid each frame
- A level format (JSON) and a small level editor to author levels outside of Java code
- Better asset pipeline
- Particle system, improved audio mixing, and visual effects for pickup
- Save/load and high-score persistence

Input and Output file usage
-------------------------
- Input: The engine expects images and audio in the classpath under `/com/example/platformer/assets/`.
  - tiles.png - tiles atlas
  - hero_spritesheet.png - hero animation
  - sfx/pickup.wav, sfx/victory.mp3, sfx/bgm.mp3 - audio
  - PixelatedEleganceRegular.ttf - font for HUD/UI
- Output: The project currently doesn't write game state files. If considering adding level export/import, a JSON file per level would fit well

Additional explanations
-----------------------
- Rendering: The rendering pipeline is:
  1. Draw background (image tiled with parallax or bgTileset, for now I chose image)
  2. Translate GraphicsContext by -camX/-camY (plus center offsets if level smaller than window)
  3. Draw decorative tiles (decoLayer)
  4. Draw solid tiles (solidLayer)
  5. Render interactive objects (most draw via deco/fg tiles and have empty render())
  6. Draw player sprite using Sprite offsets and horizontal flip
  7. Restore GraphicsContext and draw HUD and overlays in screen space
- Audio: SoundManager preloads AudioClip for short sounds and MediaPlayer for BGM (looped).
- LevelBuilder: Encodes the entire level via code. To add new levels, add more builder methods or move builder data to a JSON format.

License and credits
-----------------
- Assets: (Creative Commons Zero, CC0)
	http://creativecommons.org/publicdomain/zero/1.0/

