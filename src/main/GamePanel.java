package main;

import entity.Entity;
import entity.Player;
import objects.SuperObject;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import main.MapConfig;

public class GamePanel extends JPanel implements Runnable{

    //SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48 x 48 tile size

    public final int maxScreenColumn = 16;
    public final int maxScreenRow = 12;

    public final int screenWidth = tileSize * maxScreenColumn; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels
    private BufferedImage screenBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
    private JFrame window;
    private boolean fullScreen = false;

    // WORLD SETTINGS - Now dynamic based on current map
    private MapConfig currentMapConfig;
    public int maxStageCol;
    public int maxStageRow;
    public int worldWidth;
    public int worldHeight;
    public VignetteLight vignette = new VignetteLight(screenWidth, screenHeight);
    private int interactCooldown = 0;
    private boolean monsterDimmingDialogueShown = false;
    private boolean monsterDimmingDialoguePending = false;

    // ── "Random thought" dialogue triggers (no E needed) ─────────────────────
    // Shows once per stage when the player ENTERS a specific tile.
    private long stageTicks = 0;
    private final Set<String> shownTileThoughtKeys = new HashSet<>();
    private int lastThoughtTileCol = Integer.MIN_VALUE;
    private int lastThoughtTileRow = Integer.MIN_VALUE;

    // Configure which tile triggers which thought:
    // key = "<stagePath>:<col>,<row>" and value = message (optionally "a || b || c").
    private final Map<String, String> tileThoughtMessages = new HashMap<>();
    private final Map<String, String> tileThoughtTriggerKeys = new HashMap<>();
    private final Random thoughtRng = new Random();

    // FPS
    int FPS = 60;
    int choice = 1;
    private final double cameraZoom = 1.5;

    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    Sound phantomVoice = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this);
    public UI ui = new UI(this);

    Thread gameThread;

    // ENTITY AND OBJECT
    public Player player = new Player(this,keyH, choice);
    public SuperObject obj[]= new SuperObject[30];
    public Entity monster[] = new Entity[20];

    // GAME STATE
    public  int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final  int pauseState = 2;
    public final int transitionState = 3;

    // MAP / TRANSITION
    private String currentMapPath = "/maps/stage01.txt";
    private String nextMapPath = null;
    private float fadeAlpha = 0f; // 0..1
    private final float fadeSpeed = 0.3f;
    private boolean fadeOutPhase = true;
    private float phantomVoiceVolume = 0f;
    private final float phantomVoiceFadeSpeed = 0.15f;
    private final float musicVolumeDuringPhantomVoice = 0.015f;
    private final float phantomVoiceMinVolume = 0.10f;
    private final float phantomVoiceMaxVolume = 0.80f;
    private final float startingMasterVolume = 0.7f;
    private float masterVolume = startingMasterVolume;
    private final float volumeStep = 0.1f;

    int playerX = player.stageX;
    int playerY = player.stageY;
    int playerSpeed = 4;

    public GamePanel (){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Initialize map config based on default map
        currentMapConfig = MapConfig.getConfigForMap(currentMapPath, tileSize);
        updateWorldDimensions();

        registerThoughtTriggers();
    }

    public void setWindow(JFrame window) {
        this.window = window;
    }

    public void toggleFullScreen() {
        if (window == null) {
            return;
        }

        if (!fullScreen) {
            window.dispose();
            window.setUndecorated(true);
            window.setResizable(false);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setVisible(true);
            fullScreen = true;
        } else {
            window.dispose();
            window.setUndecorated(false);
            window.setResizable(false);
            window.setExtendedState(JFrame.NORMAL);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
            fullScreen = false;
        }

        revalidate();
        repaint();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }


    private void updateWorldDimensions() {
        this.maxStageCol = currentMapConfig.maxStageCol;
        this.maxStageRow = currentMapConfig.maxStageRow;
        this.worldWidth = currentMapConfig.worldWidth;
        this.worldHeight = currentMapConfig.worldHeight;
    }

    private void registerThoughtTriggers() {
        // Test thought at the marked X area in stage01.
        registerTileThought("/maps/stage01.txt", 5, 13,
                "I should look around...\n\n and find... a way out...");

        registerTileThought("/maps/stage01.txt", 3, 7,
                "Damn its very dark...\n\nA light switch can really be a big help right now");

        registerTileThought("/maps/stage01.txt", 7, 2, "stage01_7_2_or_7_4",
                "Hmmmm...\n\n" + "An orange booklet, maybe there's something inside that can tell us what's happening");
        registerTileThought("/maps/stage01.txt", 7, 4, "stage01_7_2_or_7_4",
                "Hmmmm...\n\n" + "An orange booklet, maybe there's something inside that can tell us what's happening");

        //
        // Optional random thoughts for the same tile:
        // tileThoughtMessages.put(makeThoughtKey("/maps/stage01.txt", 5, 9),
        //         "Option A || Option B || Option C");
    }

    private String makeThoughtKey(String stagePath, int col, int row) {
        return stagePath + ":" + col + "," + row;
    }

    private void registerTileThought(String stagePath, int col, int row, String message) {
        String thoughtKey = makeThoughtKey(stagePath, col, row);
        tileThoughtMessages.put(thoughtKey, message);
        tileThoughtTriggerKeys.put(thoughtKey, thoughtKey);
    }

    private void registerTileThought(String stagePath, int col, int row, String sharedTriggerKey, String message) {
        String thoughtKey = makeThoughtKey(stagePath, col, row);
        tileThoughtMessages.put(thoughtKey, message);
        tileThoughtTriggerKeys.put(thoughtKey, stagePath + ":" + sharedTriggerKey);
    }

    private void resetStageThoughtState() {
        stageTicks = 0;
        shownTileThoughtKeys.clear();
        lastThoughtTileCol = Integer.MIN_VALUE;
        lastThoughtTileRow = Integer.MIN_VALUE;
    }

    // Convert player's world coordinates to tile indices (col/row).
    private int getPlayerTileCol() {
        int centerX = player.stageX + player.solidArea.x + player.solidArea.width / 2;
        return centerX / tileSize;
    }

    private int getPlayerTileRow() {
        int centerY = player.stageY + player.solidArea.y + player.solidArea.height / 2;
        return centerY / tileSize;
    }

    // Called every tick in playState.
    // Triggers ONCE per stage when player ENTERS a configured tile.
    private void checkTileThoughtTriggers() {
        // Don't interrupt an active dialogue.
        if (ui.isDialogueActive()) return;

        int col = getPlayerTileCol();
        int row = getPlayerTileRow();

        // Trigger only on tile entry (when the player changes tile).
        if (col == lastThoughtTileCol && row == lastThoughtTileRow) return;
        lastThoughtTileCol = col;
        lastThoughtTileRow = row;

        String thoughtKey = makeThoughtKey(currentMapPath, col, row);
        String message = tileThoughtMessages.get(thoughtKey);
        if (message == null) return;
        String triggerKey = tileThoughtTriggerKeys.getOrDefault(thoughtKey, thoughtKey);
        if (!shownTileThoughtKeys.add(triggerKey)) return; // once per stage or shared group

        // Optional randomization: split on "||"
        String chosen = message;
        if (message.contains("||")) {
            String[] parts = message.split("\\|\\|");
            chosen = parts[thoughtRng.nextInt(parts.length)].trim();
        }

        ui.showMessage(chosen);
    }

    public void setupGame() {
        // Stage start: keys are per-stage and shouldn't carry over.
        resetStageThoughtState();
        player.hasKey = 0;
        player.hasReplacementSwitch = false;
        player.setDefaultValues();
        player.setSpawnForMap(currentMapPath);
        assetSetter.setObject(currentMapPath);
//        assetSetter.setObject();
        assetSetter.setMonster(currentMapPath);
        phantomVoice.setFile(6);
        applyPhantomVoiceVolume();
        gameState = titleState;
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update(){
        if(interactCooldown > 0) {
            interactCooldown--;
        }
        if(gameState == playState) {
            player.update();
            for(int i = 0; i < monster.length; i++) {
                if(monster[i] != null) {
                    monster[i].update();
                }
            }

            stageTicks++;
            checkTileThoughtTriggers();
        }
        if(gameState == pauseState) {
            //nothing
        }
        if (gameState == transitionState) {
            updateTransition();
        }
        ui.update();
        checkEnemyProximityDimming();
        vignette.update();
    }

    private void checkEnemyProximityDimming() {
        boolean playerIsNearEnemy = false;
        int playerCenterX = player.stageX + player.solidArea.x + player.solidArea.width / 2;
        int playerCenterY = player.stageY + player.solidArea.y + player.solidArea.height / 2;
        int triggerDistance = tileSize * 5;
        int triggerDistanceSquared = triggerDistance * triggerDistance;
        int nearestEnemyDistanceSquared = Integer.MAX_VALUE;
        float enemyProximityLevel = 0f;

        for (int i = 0; i < monster.length; i++) {
            if (monster[i] == null) {
                continue;
            }

            Entity enemy = monster[i];
            int enemyCenterX = enemy.stageX + enemy.solidArea.x + enemy.solidArea.width / 2;
            int enemyCenterY = enemy.stageY + enemy.solidArea.y + enemy.solidArea.height / 2;
            int dx = playerCenterX - enemyCenterX;
            int dy = playerCenterY - enemyCenterY;
            int distanceSquared = dx * dx + dy * dy;

            if (distanceSquared <= triggerDistanceSquared) {
                playerIsNearEnemy = true;
                nearestEnemyDistanceSquared = Math.min(nearestEnemyDistanceSquared, distanceSquared);
            }
        }

        if (playerIsNearEnemy) {
            double nearestDistance = Math.sqrt(nearestEnemyDistanceSquared);
            enemyProximityLevel = 1f - (float) Math.min(1.0, nearestDistance / triggerDistance);
        }

        vignette.setEnemyProximityLevel(enemyProximityLevel);
        updatePhantomVoice(getPhantomVoiceTargetVolume(
                playerIsNearEnemy,
                nearestEnemyDistanceSquared,
                triggerDistance));

        if (monsterDimmingDialoguePending && !ui.isDialogueActive()) {
            showMonsterDimmingDialogue();
        }

        if (playerIsNearEnemy && !monsterDimmingDialogueShown) {
            if (ui.isDialogueActive()) {
                monsterDimmingDialoguePending = true;
            } else {
                showMonsterDimmingDialogue();
            }
        }
    }

    private float getPhantomVoiceTargetVolume(boolean playerIsNearEnemy, int nearestEnemyDistanceSquared, int triggerDistance) {
        if (!playerIsNearEnemy) {
            return 0f;
        }

        double nearestDistance = Math.sqrt(nearestEnemyDistanceSquared);
        float closeness = 1f - (float) Math.min(1.0, nearestDistance / triggerDistance);
        return phantomVoiceMinVolume + (closeness * (phantomVoiceMaxVolume - phantomVoiceMinVolume));
    }

    private void updatePhantomVoice(float targetVolume) {
        if (targetVolume > 0f && phantomVoice.clip != null && !phantomVoice.clip.isRunning()) {
            phantomVoice.loop();
        }

        if (phantomVoiceVolume < targetVolume) {
            phantomVoiceVolume = Math.min(targetVolume, phantomVoiceVolume + phantomVoiceFadeSpeed);
        } else if (phantomVoiceVolume > targetVolume) {
            phantomVoiceVolume = Math.max(targetVolume, phantomVoiceVolume - phantomVoiceFadeSpeed);
        }

        applyPhantomVoiceVolume();

        if (phantomVoiceVolume <= 0f && targetVolume == 0f && phantomVoice.clip != null && phantomVoice.clip.isRunning()) {
            phantomVoice.stop();
        }
    }

    private void applyPhantomVoiceVolume() {
        phantomVoice.setVolume(phantomVoiceVolume * masterVolume);
        applyMusicVolume();
    }

    private void applyMusicVolume() {
        float phantomProgress = phantomVoiceMaxVolume == 0f ? 0f : Math.min(1f, phantomVoiceVolume / phantomVoiceMaxVolume);
        float musicDucking = 1f - ((1f - musicVolumeDuringPhantomVoice) * phantomProgress);
        music.setVolume(masterVolume * musicDucking);
    }

    private void showMonsterDimmingDialogue() {
        ui.showMessage("The room is getting darker...\n\n"
                + "Something nearby is draining the light.\n\n"
                + "Stay alert when this happens.");
        monsterDimmingDialogueShown = true;
        monsterDimmingDialoguePending = false;
    }

    private void updateTransition() {
        if (nextMapPath == null) {
            // Safety: cancel transition if no target set.
            fadeAlpha = 0f;
            gameState = playState;
            return;
        }

        if (fadeOutPhase) {
            fadeAlpha += fadeSpeed;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;

                // Swap map at full black
                currentMapPath = nextMapPath;
                tileM.loadMap(currentMapPath);

                // Update world dimensions based on new map
                currentMapConfig = tileM.getCurrentMapConfig();
                updateWorldDimensions();

                // Reset "once per stage" thought state after the map changes.
                resetStageThoughtState();
                player.setDefaultValues();
                player.setSpawnForMap(currentMapPath);
                player.hasKey = 0; // keys are per-stage
                player.hasReplacementSwitch = false; // repair parts are per-stage
                assetSetter.setObject(currentMapPath);
                assetSetter.setMonster(currentMapPath);

                // now fade back in
                fadeOutPhase = false;
            }
        } else {
            fadeAlpha -= fadeSpeed;
            if (fadeAlpha <= 0f) {
                fadeAlpha = 0f;
                nextMapPath = null;
                fadeOutPhase = true;
                gameState = playState;
            }
        }
    }

    public void startMapTransition(String mapPath) {
        if (gameState == transitionState) return;
        nextMapPath = mapPath;
        fadeAlpha = 0f;
        fadeOutPhase = true;
        gameState = transitionState;
    }

    public void run(){
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread != null){
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta>= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if(timer >= 1000000000){
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D bufferG = screenBuffer.createGraphics();
        bufferG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        bufferG.setColor(Color.BLACK);
        bufferG.fillRect(0, 0, screenWidth, screenHeight);
        bufferG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        drawGame(bufferG);
        bufferG.dispose();

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        double scale = Math.min((double) getWidth() / screenWidth, (double) getHeight() / screenHeight);
        int drawWidth = (int)Math.round(screenWidth * scale);
        int drawHeight = (int)Math.round(screenHeight * scale);
        int drawX = (getWidth() - drawWidth) / 2;
        int drawY = (getHeight() - drawHeight) / 2;

        g2.drawImage(screenBuffer, drawX, drawY, drawWidth, drawHeight, null);
        g2.dispose();
    }

    private void drawGame(Graphics2D g2) {
        //Note: The order of rendering is so important, basically first rendered is the bottom most layer
        //followed by the next, this is how to create a layering system in the game.

        if(gameState == titleState) {
            ui.draw(g2);
            return;
        }

        Graphics2D worldG = (Graphics2D) g2.create();
        worldG.translate(screenWidth / 2.0, screenHeight / 2.0);
        worldG.scale(cameraZoom, cameraZoom);
        worldG.translate(-screenWidth / 2.0, -screenHeight / 2.0);

        //TILE 1st layer
        tileM.draw(worldG);
        //OBJECT 2nd layer
        for(int i = 0; i <obj.length; i++){
            if(obj[i] != null){
                obj[i].draw(worldG, this);
            }
        }
        //MONSTERS 3rd layer
        for(int i = 0; i < monster.length; i++){
            if(monster[i] != null){
                monster[i].draw(worldG);
            }
        }
        //PLAYER 4th layer
        player.draw(worldG);
        worldG.dispose();

        vignette.draw(g2, player.screenX, player.screenY, tileSize, tileSize);
        ui.draw(g2);

        // Fade overlay (draw last so it covers everything)
        if (gameState == transitionState && fadeAlpha > 0f) {
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    Math.min(1f, Math.max(0f, fadeAlpha))));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.setComposite(old);
        }
    }

    public void playMusic(int i) {

        music.setFile(i);
        applyMusicVolume();
        music.play();
        music.loop();
    }

    public void stopMusic() {

        music.stop();
    }

    public void pauseMusic() {
        music.pause();
    }

    public void resumeMusic() {
        music.resume();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.setVolume(masterVolume);
        se.play();
    }

    public void increaseVolume() {
        setMasterVolume(masterVolume + volumeStep);
    }

    public void decreaseVolume() {
        setMasterVolume(masterVolume - volumeStep);
    }

    public void setMasterVolume(float volume) {
        masterVolume = Math.max(0f, Math.min(1f, volume));
        applyMusicVolume();
        se.setVolume(masterVolume);
        player.setWalkingVolume(masterVolume);
        applyPhantomVoiceVolume();
        System.out.println("Volume: " + Math.round(masterVolume * 100) + "%");
    }

    public int getMasterVolumePercent() {
        return Math.round(masterVolume * 100);
    }

}
