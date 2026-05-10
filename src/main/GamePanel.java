package main;

import entity.Entity;
import entity.Player;
import main.monster.MON_Dementor;
import main.monster.MON_MinionWitherSlime;
import main.monster.MON_Stage3BossMomo;
import main.monster.MON_Stage2WitherBoss;
import npc.NPC_JhonPorkJerkyJake;
import objects.SuperObject;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import main.MapConfig;

import main.IntroCutscenePlayer;

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
    MouseHandler mouseH = new MouseHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    Sound phantomVoice = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public DevSettings devSettings = new DevSettings(this);

    Thread gameThread;

    // ENTITY AND OBJECT
    public Player player = new Player(this,keyH, choice);
    public SuperObject obj[]= new SuperObject[120];
    public Entity monster[] = new Entity[20];

    // GAME STATE
    public  int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final  int pauseState = 2;
    public final int transitionState = 3;
    public final int cutsceneState = 4;
    public final int introCutsceneState = 5;
    public final int characterSelectState = 6;
    public final int gameOverState = 7;
    private static final int TRANSITION_NONE = 0;
    private static final int TRANSITION_MAP = 1;
    private static final int TRANSITION_TITLE_START = 2;

    // MAP / TRANSITION
    private String currentMapPath = "/maps/stage01.txt";
    private String nextMapPath = null;
    private float fadeAlpha = 0f; // 0..1
    private final float fadeSpeed = 0.3f;
    private final float titleStartFadeSpeed = 0.005f;
    private boolean fadeOutPhase = true;
    private int activeTransitionType = TRANSITION_NONE;
    private boolean showTitleDuringTransition = false;
    private int transitionHoldTicks = 0;
    private float phantomVoiceVolume = 0f;
    private final float phantomVoiceFadeSpeed = 0.15f;
    private final float musicVolumeDuringPhantomVoice = 0.015f;
    private final float phantomVoiceMinVolume = 0.10f;
    private final float phantomVoiceMaxVolume = 0.80f;
    private final float startingMasterVolume = 0.7f;
    private float masterVolume = startingMasterVolume;
    private final float volumeStep = 0.1f;
    private float cameraStageX;
    private float cameraStageY;
    private static final float CAMERA_PAN_SPEED = 5f;
    private static final int CUTSCENE_HOLD_TICKS = 45;
    private static final int CUTSCENE_NONE = 0;
    private static final int CUTSCENE_STAGE1_LIGHTS_TO_DOOR = 1;
    private static final int CUTSCENE_STAGE2_BOSS_INTRO = 2;
    private static final int CUTSCENE_STAGE3_ESCAPE = 3;
    private static final int CUTSCENE_PHASE_PAN_TO_TARGET = 0;
    private static final int CUTSCENE_PHASE_WAIT_FOR_DIALOGUE = 1;
    private static final int CUTSCENE_PHASE_RETURN_TO_PLAYER = 2;
    private static final int CUTSCENE_STAGE3_MOVE_TO_CLUE = 3;
    private static final int CUTSCENE_STAGE3_PLAYER_PANIC = 4;
    private static final int CUTSCENE_STAGE3_GHOST_WARNING = 5;
    private static final int CUTSCENE_STAGE3_RUN_TO_BARRIER = 6;
    private static final int CUTSCENE_STAGE3_RUN_TO_SAFE_TILE = 7;
    private static final int CUTSCENE_STAGE3_PAN_TO_BOSS = 8;
    private static final int CUTSCENE_STAGE3_HOLD_ON_BOSS = 9;
    private static final int CUTSCENE_STAGE3_RETURN_AND_START_CHASE = 10;
    private static final int TITLE_START_BLACK_HOLD_TICKS = 30;
    private int activeCutscene = CUTSCENE_NONE;
    private int cutscenePhase = CUTSCENE_PHASE_PAN_TO_TARGET;
    private int cutsceneHoldTicks = 0;
    private float cutsceneTargetStageX;
    private float cutsceneTargetStageY;
    private boolean stage01DoorRevealPlayed = false;
    private boolean stage02BossIntroPlayed = false;
    private boolean stage02BossActivated = false;
    private int minionEffectTicks = 0;
    private final int[] candleSequence = {1, 2, 3, 4};
    private final java.util.List<Integer> litCandleOrder = new ArrayList<>();
    private boolean candlePuzzleSolved = false;
    private String pendingStartDialogue = null;
    private boolean stage3ChaseStarted = false;
    private boolean pendingReturnToTitleAfterDialogue = false;
    private int cameraShakeTicks = 0;
    private int cameraShakeMagnitude = 0;
    private float cameraShakeOffsetX = 0f;
    private float cameraShakeOffsetY = 0f;
    private Point stage3ClueMarker;
    private Point stage3ClueApproachTile;
    private java.util.List<Point> stage3BreakMarkers = new ArrayList<>();
    private Point stage3BreakApproachTile;
    private java.util.List<Point> stage3SafeMarkers = new ArrayList<>();
    private Point stage3SafeApproachTile;
    private Point stage3BossMarker;
    private int stage3MoveToClueTicks = 0;
    private boolean stage3BarrierBroken = false;
    private static final int STAGE3_BOSS_PAN_HOLD_TICKS = 360;
    private IntroCutscenePlayer introCutscenePlayer;

    /** Same order as the character selection UI: Lerler, Gasha, Nnyl, Gemal. */
    private static final String[] CHARACTER_INTRO_CUTSCENE_IDS = { "llama", "gasha", "neil", "romare" };

    int playerX = player.stageX;
    int playerY = player.stageY;
    int playerSpeed = 4;

    public GamePanel (){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);

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
                "I need to stay calm.\n\nThere has to be a way out of this room.");

        registerTileThought("/maps/stage01.txt", 3, 7,
                "It's too dark to see properly.\n\nIf I can get the lights back on, maybe I can make sense of this place.");

        registerTileThought("/maps/stage01.txt", 1, 2,
                "There's a switch on the wall.\n\nIf I can get it working, I might finally see what's in this room.");

        registerTileThought("/maps/stage01.txt", 7, 2, "stage01_7_2_or_7_4",
                "An orange booklet...\n\nMaybe it explains what happened here.");
        registerTileThought("/maps/stage01.txt", 7, 4, "stage01_7_2_or_7_4",
                "An orange booklet...\n\nMaybe it explains what happened here.");

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
        stage01DoorRevealPlayed = false;
        stage02BossIntroPlayed = false;
        stage02BossActivated = false;
        activeCutscene = CUTSCENE_NONE;
        stage3ChaseStarted = false;
        pendingReturnToTitleAfterDialogue = false;
        clearStage3CutsceneMarkers();
        stage3MoveToClueTicks = 0;
        stage3BarrierBroken = false;
        disposeIntroCutscenePlayer();
        resetCandlePuzzleState();
        player.hasKey = 0;
        player.hasReplacementSwitch = false;
        player.setDefaultValues();
        player.setSpawnForMap(currentMapPath);
        snapCameraToPlayer();
        assetSetter.setObject(currentMapPath);
//        assetSetter.setObject();
        assetSetter.setMonster(currentMapPath);
        phantomVoice.setFile(6);
        applyPhantomVoiceVolume();
        pendingStartDialogue = null;
        gameState = titleState;
    }

    public void startGameThread(){
        if (gameThread != null && gameThread.isAlive()) {
            return;
        }
        gameThread = new Thread(this);
        gameThread.start();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    public void startGameFromTitle() {
        if (gameState != titleState) {
            return;
        }

        prepareGameStart(choice);
        beginTitleStartTransition();
    }

    public void startGameFromCharacterSelection(int buttonIndex) {
        if (buttonIndex < 0 || buttonIndex >= CHARACTER_INTRO_CUTSCENE_IDS.length) {
            return;
        }

        prepareGameStart(buttonIndex + 1);

        introCutscenePlayer = new IntroCutscenePlayer(masterVolume, CHARACTER_INTRO_CUTSCENE_IDS[buttonIndex]);
        if (introCutscenePlayer.isAvailable()) {
            introCutscenePlayer.start();
            gameState = introCutsceneState;
            return;
        }

        disposeIntroCutscenePlayer();
        beginTitleStartTransition();
    }

    private void prepareGameStart(int selectedChoice) {
        choice = selectedChoice;
        player = new Player(this, keyH, choice);
        player.setWalkingVolume(masterVolume);
        keyH.interactPressed = false;
        keyH.sprintPressed = false;

        currentMapPath = "/maps/stage01.txt";
        nextMapPath = null;
        fadeAlpha = 0f;
        fadeOutPhase = true;
        activeTransitionType = TRANSITION_NONE;
        showTitleDuringTransition = false;
        transitionHoldTicks = 0;
        disposeIntroCutscenePlayer();

        resetStageThoughtState();
        stage01DoorRevealPlayed = false;
        stage02BossIntroPlayed = false;
        stage02BossActivated = false;
        activeCutscene = CUTSCENE_NONE;
        stage3ChaseStarted = false;
        pendingReturnToTitleAfterDialogue = false;
        clearStage3CutsceneMarkers();
        stage3MoveToClueTicks = 0;
        stage3BarrierBroken = false;
        resetCandlePuzzleState();

        tileM.loadMap(currentMapPath);
        currentMapConfig = tileM.getCurrentMapConfig();
        updateWorldDimensions();

        player.hasKey = 0;
        player.hasReplacementSwitch = false;
        player.setDefaultValues();
        player.setSpawnForMap(currentMapPath);
        snapCameraToPlayer();
        assetSetter.setObject(currentMapPath);
        assetSetter.setMonster(currentMapPath);
        phantomVoice.setFile(6);
        phantomVoiceVolume = 0f;
        applyPhantomVoiceVolume();
        pendingStartDialogue = "...\n\n"
                + "Where am I?\n\n"
                + "Why is it so dark?\n\n"
                + "Where did everyone go?";
    }

    private void beginTitleStartTransition() {
        fadeAlpha = 1f;
        fadeOutPhase = false;
        activeTransitionType = TRANSITION_TITLE_START;
        showTitleDuringTransition = false;
        transitionHoldTicks = TITLE_START_BLACK_HOLD_TICKS;
        playMusic(1);
        gameState = transitionState;
    }

    public void exitGame() {
        System.exit(0);
    }

    public boolean shouldDrawTitleScreen() {
        return gameState == titleState
                || (gameState == transitionState
                && activeTransitionType == TRANSITION_TITLE_START
                && showTitleDuringTransition);
    }

    public void update(){
        if(interactCooldown > 0) {
            interactCooldown--;
        }
        if(gameState == playState) {
            devSettings.applyContinuousEffects();
            updateTemporaryEffects();
            player.update();
            updateCameraFollowPlayer();
            updateStageObjects();

            if (!ui.isDialogueActive()) {
                for(int i = 0; i < monster.length; i++) {
                    if(monster[i] != null) {
                        monster[i].update();
                        if (monster[i] != null && monster[i].removeFromWorld) {
                            monster[i] = null;
                        }
                    }
                }

                stageTicks++;
                checkTileThoughtTriggers();
                checkEnemyProximityDimming();
                vignette.update();
            }
        }
        if(gameState == pauseState) {
            //nothing
        }
        if (gameState == cutsceneState) {
            updateTemporaryEffects();
            updateCutscene();
            vignette.update();
        }
        if (gameState == introCutsceneState) {
            updateIntroCutscene();
        }
        if (gameState == transitionState) {
            updateTransition();
        }
        if (gameState == gameOverState) {
            // Wait for input to restart or go back to menu, handled via UI.update() and MouseHandler
        }
        ui.update();
        if (gameState == cutsceneState && ui.isDialogueActive() && keyH.interactPressed) {
            ui.advanceDialogue();
            keyH.interactPressed = false;
        }
        if (gameState != playState && gameState != cutsceneState && gameState != introCutsceneState && gameState != gameOverState) {
            if (pendingReturnToTitleAfterDialogue && !ui.isDialogueActive()) {
                pendingReturnToTitleAfterDialogue = false;
                currentMapPath = "/maps/stage01.txt";
                tileM.loadMap(currentMapPath);
                currentMapConfig = tileM.getCurrentMapConfig();
                updateWorldDimensions();
                setupGame();
            }
        }
        if (gameState != playState && gameState != cutsceneState && gameState != gameOverState) {
            checkEnemyProximityDimming();
            vignette.update();
        }
    }

    private void updateIntroCutscene() {
        if (introCutscenePlayer == null) {
            finishIntroCutscene();
            return;
        }

        introCutscenePlayer.update();
        if (introCutscenePlayer.isFinished()) {
            finishIntroCutscene();
        }
    }

    public void skipIntroCutscene() {
        if (gameState != introCutsceneState) {
            return;
        }

        keyH.interactPressed = false;
        finishIntroCutscene();
    }

    private void updateCameraFollowPlayer() {
        cameraStageX = player.stageX;
        cameraStageY = player.stageY;
    }

    private void snapCameraToPlayer() {
        cameraStageX = player.stageX;
        cameraStageY = player.stageY;
    }

    private boolean moveCameraToward(float targetX, float targetY, float speed) {
        float dx = targetX - cameraStageX;
        float dy = targetY - cameraStageY;
        double distance = Math.sqrt((dx * dx) + (dy * dy));
        if (distance <= speed) {
            cameraStageX = targetX;
            cameraStageY = targetY;
            return true;
        }

        cameraStageX += (float) ((dx / distance) * speed);
        cameraStageY += (float) ((dy / distance) * speed);
        return false;
    }

    private void updateCutscene() {
        updateCutsceneEntityAnimations();

        if (activeCutscene == CUTSCENE_STAGE1_LIGHTS_TO_DOOR) {
            updateStage1DoorRevealCutscene();
        }
        if (activeCutscene == CUTSCENE_STAGE2_BOSS_INTRO) {
            updateStage2BossIntroCutscene();
        }
        if (activeCutscene == CUTSCENE_STAGE3_ESCAPE) {
            updateStage3EscapeCutscene();
        }
    }

    private void updateCutsceneEntityAnimations() {
        for (Entity entity : monster) {
            if (entity != null) {
                entity.updateAnimationOnly();
            }
        }
    }

    private void updateStage1DoorRevealCutscene() {
        if (cutscenePhase == CUTSCENE_PHASE_PAN_TO_TARGET) {
            if (moveCameraToward(cutsceneTargetStageX, cutsceneTargetStageY, CAMERA_PAN_SPEED)) {
                if (cutsceneHoldTicks > 0) {
                    cutsceneHoldTicks--;
                } else {
                    ui.showMessage("So that's the way out...\n\n"
                            + "But those things are still out there.\n\n"
                            + "I need to be careful.");
                    cutscenePhase = CUTSCENE_PHASE_WAIT_FOR_DIALOGUE;
                }
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_PHASE_WAIT_FOR_DIALOGUE) {
            if (!ui.isDialogueActive()) {
                cutscenePhase = CUTSCENE_PHASE_RETURN_TO_PLAYER;
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_PHASE_RETURN_TO_PLAYER) {
            if (moveCameraToward(player.stageX, player.stageY, CAMERA_PAN_SPEED)) {
                activeCutscene = CUTSCENE_NONE;
                gameState = playState;
            }
        }
    }

    private void checkEnemyProximityDimming() {
        boolean playerIsNearEnemy = false;
        int playerCenterX = player.stageX + player.solidArea.x + player.solidArea.width / 2;
        int playerCenterY = player.stageY + player.solidArea.y + player.solidArea.height / 2;
        int triggerDistance = tileSize * 5;
        int triggerDistanceSquared = triggerDistance * triggerDistance;
        int flickerDistance = tileSize * 3;
        int nearestEnemyDistanceSquared = Integer.MAX_VALUE;
        float enemyProximityLevel = 0f;
        float enemyFlickerLevel = 0f;

        if (minionEffectTicks > 0) {
            vignette.setEnemyProximityLevel(1f);
            vignette.setEnemyFlickerLevel(1f);
            updatePhantomVoice(phantomVoiceMaxVolume);
            return;
        }

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
            if (vignette.areRoomLightsOn()) {
                enemyProximityLevel = 1f;
            } else {
                enemyProximityLevel = 1f - (float) Math.min(1.0, nearestDistance / triggerDistance);
            }
            if (nearestDistance <= flickerDistance) {
                enemyFlickerLevel = 1f - (float) Math.min(1.0, nearestDistance / flickerDistance);
            }
        }

        vignette.setEnemyProximityLevel(enemyProximityLevel);
        vignette.setEnemyFlickerLevel(enemyFlickerLevel);
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
        if (activeTransitionType == TRANSITION_TITLE_START) {
            updateTitleStartTransition();
            return;
        }

        if (activeTransitionType != TRANSITION_MAP) {
            fadeAlpha = 0f;
            fadeOutPhase = true;
            showTitleDuringTransition = false;
            transitionHoldTicks = 0;
            activeTransitionType = TRANSITION_NONE;
            gameState = playState;
            return;
        }

        if (nextMapPath == null) {
            // Safety: cancel transition if no target set.
            fadeAlpha = 0f;
            fadeOutPhase = true;
            showTitleDuringTransition = false;
            transitionHoldTicks = 0;
            activeTransitionType = TRANSITION_NONE;
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
                int currentLife = player.life;
                boolean hadAxe = player.hasAxe;
                player.setDefaultValues();
                if (hadAxe || "/maps/stage03.txt".equals(currentMapPath)) {
                    player.hasAxe = true;
                }
                player.life = Math.min(currentLife, player.maxLife);
                player.setSpawnForMap(currentMapPath);
                snapCameraToPlayer();
                player.hasKey = 0; // keys are per-stage
                player.hasReplacementSwitch = false; // repair parts are per-stage
                resetCandlePuzzleState();
                stage02BossIntroPlayed = false;
                stage02BossActivated = false;
                stage3ChaseStarted = false;
                pendingReturnToTitleAfterDialogue = false;
                clearStage3CutsceneMarkers();
                stage3MoveToClueTicks = 0;
                stage3BarrierBroken = false;
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
                activeTransitionType = TRANSITION_NONE;
                gameState = playState;
            }
        }
    }

    private void updateTitleStartTransition() {
        if (transitionHoldTicks > 0) {
            transitionHoldTicks--;
            return;
        }

        fadeAlpha -= titleStartFadeSpeed;
        if (fadeAlpha <= 0f) {
            fadeAlpha = 0f;
            fadeOutPhase = true;
            showTitleDuringTransition = false;
            transitionHoldTicks = 0;
            activeTransitionType = TRANSITION_NONE;
            gameState = playState;
            if (pendingStartDialogue != null) {
                ui.showMessage(pendingStartDialogue);
                pendingStartDialogue = null;
            }
        }
    }

    private void finishIntroCutscene() {
        disposeIntroCutscenePlayer();
        fadeAlpha = 1f;
        fadeOutPhase = false;
        activeTransitionType = TRANSITION_TITLE_START;
        showTitleDuringTransition = false;
        transitionHoldTicks = 0;
        playMusic(1);
        gameState = transitionState;
    }

    private void disposeIntroCutscenePlayer() {
        if (introCutscenePlayer != null) {
            introCutscenePlayer.dispose();
            introCutscenePlayer = null;
        }
    }

    public void startMapTransition(String mapPath) {
        if (gameState == transitionState) return;
        nextMapPath = mapPath;
        fadeAlpha = 0f;
        fadeOutPhase = true;
        activeTransitionType = TRANSITION_MAP;
        showTitleDuringTransition = false;
        transitionHoldTicks = 0;
        gameState = transitionState;
    }

    public void loadStageForTesting(String mapPath) {
        if (!DevSettings.TEST_MODE) {
            return;
        }

        currentMapPath = mapPath;
        nextMapPath = null;
        fadeAlpha = 0f;
        fadeOutPhase = true;
        activeTransitionType = TRANSITION_NONE;
        showTitleDuringTransition = false;
        transitionHoldTicks = 0;
        disposeIntroCutscenePlayer();
        pendingStartDialogue = null;

        tileM.loadMap(currentMapPath);
        currentMapConfig = tileM.getCurrentMapConfig();
        updateWorldDimensions();

        resetStageThoughtState();
        resetCandlePuzzleState();
        stage02BossIntroPlayed = false;
        stage02BossActivated = false;
        activeCutscene = CUTSCENE_NONE;
        stage3ChaseStarted = false;
        pendingReturnToTitleAfterDialogue = false;
        clearStage3CutsceneMarkers();
        stage3MoveToClueTicks = 0;
        stage3BarrierBroken = false;

        int currentLife = player.life;
        boolean hadAxe = player.hasAxe;
        player.setDefaultValues();
        if (hadAxe || "/maps/stage03.txt".equals(currentMapPath)) {
            player.hasAxe = true;
        }
        player.life = devSettings.isUnlimitedHealthEnabled()
                ? player.maxLife
                : Math.max(1, Math.min(currentLife, player.maxLife));
        player.setSpawnForMap(currentMapPath);
        snapCameraToPlayer();
        assetSetter.setObject(currentMapPath);
        assetSetter.setMonster(currentMapPath);
        gameState = playState;
    }

    public String getCurrentMapPath() {
        return currentMapPath;
    }

    public int getCameraStageX() {
        return Math.round(cameraStageX + cameraShakeOffsetX);
    }

    public int getCameraStageY() {
        return Math.round(cameraStageY + cameraShakeOffsetY);
    }

    public Point stageToScreenPoint(int stageX, int stageY) {
        double screenX = stageX - getCameraStageX() + player.screenX;
        double screenY = stageY - getCameraStageY() + player.screenY;
        screenX = (screenX - screenWidth / 2.0) * cameraZoom + screenWidth / 2.0;
        screenY = (screenY - screenHeight / 2.0) * cameraZoom + screenHeight / 2.0;
        return new Point((int) Math.round(screenX), (int) Math.round(screenY));
    }

    public boolean isPathTileBlocked(int col, int row) {
        if (col < 0 || row < 0 || col >= maxStageCol || row >= maxStageRow) {
            return true;
        }

        int tileNum = tileM.mapTileNum[col][row];
        if (tileM.tile[tileNum] == null || tileM.tile[tileNum].collision) {
            return true;
        }

        Rectangle tileArea = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);
        for (SuperObject object : obj) {
            if (object == null || !object.collision) {
                continue;
            }

            Rectangle objectArea = new Rectangle(
                    object.stageX + object.solidArea.x,
                    object.stageY + object.solidArea.y,
                    object.solidArea.width,
                    object.solidArea.height);
            if (tileArea.intersects(objectArea)) {
                return true;
            }
        }

        return false;
    }

    public void startStage1DoorRevealCutscene() {
        if (stage01DoorRevealPlayed || !"/maps/stage01.txt".equals(currentMapPath)) {
            return;
        }

        SuperObject door = findObjectByName("DoorStage1");
        if (door == null) {
            return;
        }

        cameraStageX = player.stageX;
        cameraStageY = player.stageY;
        cutsceneTargetStageX = door.stageX;
        cutsceneTargetStageY = door.stageY + (tileSize / 2f);
        cutsceneHoldTicks = CUTSCENE_HOLD_TICKS;
        activeCutscene = CUTSCENE_STAGE1_LIGHTS_TO_DOOR;
        cutscenePhase = CUTSCENE_PHASE_PAN_TO_TARGET;
        stage01DoorRevealPlayed = true;
        gameState = cutsceneState;
    }

    public void triggerStage2BossIntroFromBreakable() {
        if (stage02BossIntroPlayed || !"/maps/stage02.txt".equals(currentMapPath)) {
            return;
        }
        startStage2BossIntroCutscene();
    }

    private void startStage2BossIntroCutscene() {
        Entity boss = findStage2Boss();
        if (boss == null) {
            stage02BossIntroPlayed = true;
            return;
        }

        cameraStageX = player.stageX;
        cameraStageY = player.stageY;
        cutsceneTargetStageX = boss.stageX;
        cutsceneTargetStageY = boss.stageY;
        cutsceneHoldTicks = CUTSCENE_HOLD_TICKS;
        activeCutscene = CUTSCENE_STAGE2_BOSS_INTRO;
        cutscenePhase = CUTSCENE_PHASE_PAN_TO_TARGET;
        stage02BossIntroPlayed = true;
        gameState = cutsceneState;
    }

    private void updateStage2BossIntroCutscene() {
        if (cutscenePhase == CUTSCENE_PHASE_PAN_TO_TARGET) {
            if (moveCameraToward(cutsceneTargetStageX, cutsceneTargetStageY, CAMERA_PAN_SPEED)) {
                if (cutsceneHoldTicks > 0) {
                    cutsceneHoldTicks--;
                } else {
                    ui.showMessage("Something stirs in the dark...");
                    cutscenePhase = CUTSCENE_PHASE_WAIT_FOR_DIALOGUE;
                }
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_PHASE_WAIT_FOR_DIALOGUE) {
            if (!ui.isDialogueActive()) {
                activateStage2Boss();
                cutscenePhase = CUTSCENE_PHASE_RETURN_TO_PLAYER;
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_PHASE_RETURN_TO_PLAYER) {
            if (moveCameraToward(player.stageX, player.stageY, CAMERA_PAN_SPEED)) {
                activeCutscene = CUTSCENE_NONE;
                gameState = playState;
            }
        }
    }

    private Entity findStage2Boss() {
        for (Entity entity : monster) {
            if (entity instanceof MON_Stage2WitherBoss) {
                return entity;
            }
        }
        return null;
    }

    private void activateStage2Boss() {
        if (stage02BossActivated) {
            return;
        }

        Entity boss = findStage2Boss();
        if (boss instanceof MON_Stage2WitherBoss) {
            ((MON_Stage2WitherBoss) boss).activate();
            stage02BossActivated = true;
        }
    }

    public void spawnStage2Minions(int sourceX, int sourceY, int count) {
        for (int spawned = 0; spawned < count; spawned++) {
            int slot = findFreeMonsterSlot();
            if (slot == -1) {
                return;
            }

            MON_MinionWitherSlime minion = new MON_MinionWitherSlime(this);
            minion.stageX = sourceX + ((spawned % 3) - 1) * tileSize;
            minion.stageY = sourceY + ((spawned / 3) + 1) * tileSize;
            monster[slot] = minion;
        }
    }

    private int findFreeMonsterSlot() {
        for (int i = 0; i < monster.length; i++) {
            if (monster[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void applyStage2MinionHitEffect() {
        minionEffectTicks = 180;
        player.setControlsInvertedTicks(180);
        vignette.setEnemyProximityLevel(1f);
        vignette.setEnemyFlickerLevel(1f);
    }

    private void updateTemporaryEffects() {
        if (minionEffectTicks > 0) {
            minionEffectTicks--;
        }
        if (cameraShakeTicks > 0) {
            cameraShakeTicks--;
            cameraShakeOffsetX = getRandomCameraShakeOffset();
            cameraShakeOffsetY = getRandomCameraShakeOffset();
            if (cameraShakeTicks == 0) {
                cameraShakeMagnitude = 0;
                cameraShakeOffsetX = 0f;
                cameraShakeOffsetY = 0f;
            }
        } else {
            cameraShakeOffsetX = 0f;
            cameraShakeOffsetY = 0f;
        }
    }

    private SuperObject findObjectByName(String objectName) {
        for (SuperObject object : obj) {
            if (object != null && objectName.equals(object.name)) {
                return object;
            }
        }
        return null;
    }

    public void handleCandleInteraction(objects.OBJ_Candle candle) {
        if (candlePuzzleSolved) {
            ui.showMessage("The candles are already burning in the right order.");
            return;
        }

        if (candle.isLit()) {
            candle.turnOff();
            litCandleOrder.remove(Integer.valueOf(candle.getCandleNumber()));
            ui.showMessage("The candle goes out.");
            return;
        }

        candle.setLit(true);
        litCandleOrder.add(candle.getCandleNumber());

        if (!areAllCandlesLit()) {
            ui.showMessage("The candle catches flame.");
            return;
        }

        if (isCandleSequenceCorrect()) {
            candlePuzzleSolved = true;
            if ("/maps/stage03.txt".equals(currentMapPath)) {
                startStage3EscapeCutscene();
            } else {
                ui.showMessage("The candles burn steadily.\n\nSomething clicked in the distance.");
            }
        } else {
            turnOffAllCandles();
            litCandleOrder.clear();
            ui.showMessage("The wind grows stronger and turns off all the candles.\n\n"
                    + "It must've been the wrong combination.\n\n"
                    + "Let's try again.");
        }
    }

    private void updateStageObjects() {
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] instanceof NPC_JhonPorkJerkyJake) {
                NPC_JhonPorkJerkyJake npc = (NPC_JhonPorkJerkyJake) obj[i];
                npc.update();
                if (npc.isFadedOut()) {
                    obj[i] = null;
                }
            }
        }
    }

    public boolean isCandlePuzzleSolved() {
        return candlePuzzleSolved;
    }

    public boolean isStage3ChaseStarted() {
        return stage3ChaseStarted;
    }

    private void resetCandlePuzzleState() {
        litCandleOrder.clear();
        candlePuzzleSolved = false;
        turnOffAllCandles();
    }

    private void turnOffAllCandles() {
        for (SuperObject object : obj) {
            if (object instanceof objects.OBJ_Candle) {
                ((objects.OBJ_Candle) object).turnOff();
            }
        }
    }

    private boolean areAllCandlesLit() {
        int candleCount = 0;
        for (SuperObject object : obj) {
            if (object instanceof objects.OBJ_Candle) {
                candleCount++;
                if (!((objects.OBJ_Candle) object).isLit()) {
                    return false;
                }
            }
        }
        return candleCount > 0;
    }

    private boolean isCandleSequenceCorrect() {
        if (litCandleOrder.size() != candleSequence.length) {
            return false;
        }
        for (int i = 0; i < candleSequence.length; i++) {
            if (litCandleOrder.get(i) != candleSequence[i]) {
                return false;
            }
        }
        return true;
    }

    public void completeStage3Escape() {
        if (!"/maps/stage03.txt".equals(currentMapPath)) {
            return;
        }

        stopMusic();
        ui.showMessage("You make it through the door.\n\nThe hallway beyond is quiet.\n\nFor now.");
        pendingReturnToTitleAfterDialogue = true;
        gameState = playState;
    }

    private void startStage3EscapeCutscene() {
        if (!"/maps/stage03.txt".equals(currentMapPath) || stage3ChaseStarted) {
            return;
        }

        stage3ClueMarker = assetSetter.findMapMarker(currentMapPath, "C");
        stage3ClueApproachTile = findNearbyWalkableTile(stage3ClueMarker, 2);
        stage3BreakMarkers = assetSetter.findMapMarkers(currentMapPath, "XX");
        stage3SafeMarkers = assetSetter.findMapMarkers(currentMapPath, "Y");
        stage3BossMarker = assetSetter.findMapMarker(currentMapPath, "S");
        stage3BreakApproachTile = findStage3BreakApproachTile();
        stage3SafeApproachTile = findStage3SafeApproachTile();

        if (stage3ClueMarker == null || stage3ClueApproachTile == null
                || stage3BreakMarkers.isEmpty() || stage3BreakApproachTile == null
                || stage3SafeMarkers.isEmpty() || stage3SafeApproachTile == null
                || stage3BossMarker == null) {
            ui.showMessage("The candles burn steadily.\n\nSomething stirs in the room.");
            return;
        }

        stage3ChaseStarted = true;
        stage3MoveToClueTicks = 0;
        stage3BarrierBroken = false;
        cameraStageX = player.stageX;
        cameraStageY = player.stageY;
        activeCutscene = CUTSCENE_STAGE3_ESCAPE;
        cutscenePhase = CUTSCENE_STAGE3_MOVE_TO_CLUE;
        gameState = cutsceneState;
    }

    private void updateStage3EscapeCutscene() {
        if (cutscenePhase == CUTSCENE_STAGE3_MOVE_TO_CLUE) {
            stage3MoveToClueTicks++;
            boolean nearClue = isPlayerNearTile(stage3ClueMarker.x, stage3ClueMarker.y, 2);
            boolean reachedApproach = movePlayerTowardTile(stage3ClueApproachTile.x, stage3ClueApproachTile.y, 2.8f);
            if (nearClue || reachedApproach || stage3MoveToClueTicks >= 180) {
                startCameraShake(90, 8);
                vignette.setEnemyProximityLevel(0.80f);
                vignette.setEnemyFlickerLevel(0.75f);
                ui.showMessage("The candles flare up all at once.\n\n"
                        + "No... that's wrong.\n\n"
                        + "Why is the room getting darker?\n\n"
                        + "The floor is shaking...");
                cutscenePhase = CUTSCENE_STAGE3_PLAYER_PANIC;
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_STAGE3_PLAYER_PANIC) {
            if (!ui.isDialogueActive()) {
                ui.showMessage("Jhon Pork and Jerky Jake: RUNNNN!!");
                cutscenePhase = CUTSCENE_STAGE3_GHOST_WARNING;
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_STAGE3_GHOST_WARNING) {
            if (!ui.isDialogueActive()) {
                cutscenePhase = CUTSCENE_STAGE3_RUN_TO_BARRIER;
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_STAGE3_RUN_TO_BARRIER) {
            boolean reachedApproach = movePlayerTowardTile(stage3BreakApproachTile.x, stage3BreakApproachTile.y, 4.5f);
            boolean nearBarrier = isPlayerNearAnyTile(stage3BreakMarkers, 1);
            if ((reachedApproach || nearBarrier) && !stage3BarrierBroken) {
                breakStage3CutsceneBarrier();
                stage3BarrierBroken = true;
                playSE(3);
            }
            if (stage3BarrierBroken) {
                cutscenePhase = CUTSCENE_STAGE3_RUN_TO_SAFE_TILE;
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_STAGE3_RUN_TO_SAFE_TILE) {
            boolean reachedApproach = movePlayerTowardTile(stage3SafeApproachTile.x, stage3SafeApproachTile.y, 4.5f);
            boolean nearSafeZone = isPlayerNearAnyTile(stage3SafeMarkers, 0);
            if (reachedApproach || nearSafeZone) {
                cameraShakeTicks = 0;
                cameraShakeMagnitude = 0;
                cameraShakeOffsetX = 0f;
                cameraShakeOffsetY = 0f;
                vignette.setEnemyProximityLevel(0f);
                vignette.setEnemyFlickerLevel(0f);
                cutsceneTargetStageX = stage3BossMarker.x * tileSize;
                cutsceneTargetStageY = stage3BossMarker.y * tileSize;
                cutscenePhase = CUTSCENE_STAGE3_PAN_TO_BOSS;
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_STAGE3_PAN_TO_BOSS) {
            if (moveCameraToward(cutsceneTargetStageX, cutsceneTargetStageY, CAMERA_PAN_SPEED * 1.4f)) {
                summonStage3ChaseEnemies();
                cutsceneHoldTicks = STAGE3_BOSS_PAN_HOLD_TICKS;
                cutscenePhase = CUTSCENE_STAGE3_HOLD_ON_BOSS;
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_STAGE3_HOLD_ON_BOSS) {
            if (cutsceneHoldTicks > 0) {
                cutsceneHoldTicks--;
            } else {
                cutscenePhase = CUTSCENE_STAGE3_RETURN_AND_START_CHASE;
            }
            return;
        }

        if (cutscenePhase == CUTSCENE_STAGE3_RETURN_AND_START_CHASE) {
            if (moveCameraToward(player.stageX, player.stageY, CAMERA_PAN_SPEED * 1.4f)) {
                activeCutscene = CUTSCENE_NONE;
                gameState = playState;
            }
        }
    }

    private boolean movePlayerTowardTile(int tileCol, int tileRow, float moveSpeed) {
        int targetStageX = tileCol * tileSize;
        int targetStageY = tileRow * tileSize;
        int deltaX = targetStageX - player.stageX;
        int deltaY = targetStageY - player.stageY;

        if (Math.abs(deltaX) <= 2 && Math.abs(deltaY) <= 2) {
            player.stageX = targetStageX;
            player.stageY = targetStageY;
            return true;
        }

        String pathDirection = player.getPathDirectionToStagePoint(targetStageX, targetStageY);
        if (pathDirection == null) {
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                pathDirection = deltaX > 0 ? "right" : "left";
            } else {
                pathDirection = deltaY > 0 ? "down" : "up";
            }
        }

        player.direction = pathDirection;
        player.speed = moveSpeed;
        player.collisionOn = false;
        cChecker.checkTile(player);
        if (!player.collisionOn) {
            switch (pathDirection) {
                case "up":
                    player.moveStage(0, -moveSpeed);
                    break;
                case "down":
                    player.moveStage(0, moveSpeed);
                    break;
                case "left":
                    player.moveStage(-moveSpeed, 0);
                    break;
                case "right":
                    player.moveStage(moveSpeed, 0);
                    break;
            }
        }
        cameraStageX = player.stageX;
        cameraStageY = player.stageY;
        player.updateAnimationOnly();
        return false;
    }

    private Point getPreferredAdjacentTile(Point markerTile) {
        if (markerTile == null) {
            return null;
        }

        int[][] candidates = {
                {markerTile.x, markerTile.y + 1},
                {markerTile.x - 1, markerTile.y},
                {markerTile.x + 1, markerTile.y},
                {markerTile.x, markerTile.y - 1}
        };

        for (int[] candidate : candidates) {
            if (!isPathTileBlocked(candidate[0], candidate[1])) {
                return new Point(candidate[0], candidate[1]);
            }
        }

        return new Point(markerTile.x, markerTile.y);
    }

    private void breakStage3CutsceneBarrier() {
        if (stage3BreakMarkers == null || stage3BreakMarkers.isEmpty()) {
            return;
        }

        for (int i = 0; i < obj.length; i++) {
            if (!(obj[i] instanceof objects.OBJ_CutsceneBreakable)) {
                continue;
            }

            int objCol = obj[i].stageX / tileSize;
            int objRow = obj[i].stageY / tileSize;
            for (Point breakMarker : stage3BreakMarkers) {
                if (objCol == breakMarker.x && objRow == breakMarker.y) {
                    obj[i] = null;
                    break;
                }
            }
        }
    }

    private Point findStage3BreakApproachTile() {
        if (stage3BreakMarkers == null || stage3BreakMarkers.isEmpty()) {
            return null;
        }

        Point bestTile = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Point breakMarker : stage3BreakMarkers) {
            Point candidate = findPreferredBreakApproachTile(breakMarker);
            if (candidate == null) {
                continue;
            }

            int distance = Math.abs(candidate.x - player.stageX / tileSize) + Math.abs(candidate.y - player.stageY / tileSize);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestTile = candidate;
            }
        }
        return bestTile;
    }

    private Point findStage3SafeApproachTile() {
        if (stage3SafeMarkers == null || stage3SafeMarkers.isEmpty()) {
            return null;
        }

        Point bestTile = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Point safeMarker : stage3SafeMarkers) {
            Point candidate = findPreferredSafeApproachTile(safeMarker);
            if (candidate == null) {
                continue;
            }

            int distance = Math.abs(candidate.x - player.stageX / tileSize) + Math.abs(candidate.y - player.stageY / tileSize);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestTile = candidate;
            }
        }
        return bestTile;
    }

    private Point findPreferredSafeApproachTile(Point markerTile) {
        if (markerTile == null) {
            return null;
        }

        if (!isPathTileBlocked(markerTile.x, markerTile.y)) {
            return new Point(markerTile.x, markerTile.y);
        }

        int[][] candidates = {
                {markerTile.x - 1, markerTile.y},
                {markerTile.x, markerTile.y - 1},
                {markerTile.x, markerTile.y + 1},
                {markerTile.x + 1, markerTile.y}
        };

        for (int[] candidate : candidates) {
            if (!isPathTileBlocked(candidate[0], candidate[1])) {
                return new Point(candidate[0], candidate[1]);
            }
        }

        return findNearbyWalkableTile(markerTile, 2);
    }

    private Point findPreferredBreakApproachTile(Point markerTile) {
        int[][] candidates = {
                {markerTile.x - 1, markerTile.y},
                {markerTile.x, markerTile.y - 1},
                {markerTile.x, markerTile.y + 1},
                {markerTile.x + 1, markerTile.y}
        };

        for (int[] candidate : candidates) {
            if (!isPathTileBlocked(candidate[0], candidate[1])) {
                return new Point(candidate[0], candidate[1]);
            }
        }

        return findNearbyWalkableTile(markerTile, 2);
    }

    private boolean isPlayerNearAnyTile(java.util.List<Point> tiles, int radiusTiles) {
        for (Point tile : tiles) {
            if (isPlayerNearTile(tile.x, tile.y, radiusTiles)) {
                return true;
            }
        }
        return false;
    }

    private void summonStage3ChaseEnemies() {
        MON_Stage3BossMomo stage3Boss = new MON_Stage3BossMomo(this);
        stage3Boss.activateForStage3Chase();
        assetSetter.spawnMonster(stage3Boss, stage3BossMarker.x, stage3BossMarker.y);

        java.util.List<Point> phantomMarkers = assetSetter.findMapMarkers(currentMapPath, "E");
        int variant = 1;
        for (Point marker : phantomMarkers) {
            MON_Dementor phantom = new MON_Dementor(this, variant);
            phantom.activateForStage3Chase();
            assetSetter.spawnMonster(phantom, marker.x, marker.y);
            variant = variant == 4 ? 1 : variant + 1;
        }

        java.util.List<Point> stage2BossMarkers = assetSetter.findMapMarkers(currentMapPath, "S2");
        for (Point marker : stage2BossMarkers) {
            MON_Stage2WitherBoss boss = new MON_Stage2WitherBoss(this);
            boss.activateForStage3Chase();
            assetSetter.spawnMonster(boss, marker.x, marker.y);
        }
    }

    private void clearStage3CutsceneMarkers() {
        stage3ClueMarker = null;
        stage3ClueApproachTile = null;
        stage3BreakMarkers.clear();
        stage3BreakApproachTile = null;
        stage3SafeMarkers.clear();
        stage3SafeApproachTile = null;
        stage3BossMarker = null;
    }

    private void startCameraShake(int ticks, int magnitude) {
        cameraShakeTicks = Math.max(cameraShakeTicks, ticks);
        cameraShakeMagnitude = Math.max(cameraShakeMagnitude, magnitude);
    }

    private float getRandomCameraShakeOffset() {
        if (cameraShakeTicks <= 0 || cameraShakeMagnitude <= 0) {
            return 0f;
        }

        return (thoughtRng.nextFloat() * (cameraShakeMagnitude * 2f)) - cameraShakeMagnitude;
    }

    private Point findNearbyWalkableTile(Point markerTile, int radius) {
        if (markerTile == null) {
            return null;
        }

        Point bestTile = null;
        int bestScore = Integer.MAX_VALUE;

        for (int row = markerTile.y - radius; row <= markerTile.y + radius; row++) {
            for (int col = markerTile.x - radius; col <= markerTile.x + radius; col++) {
                if (col == markerTile.x && row == markerTile.y) {
                    continue;
                }
                if (isPathTileBlocked(col, row)) {
                    continue;
                }

                int score = Math.abs(col - markerTile.x) + Math.abs(row - markerTile.y);
                if (score < bestScore) {
                    bestScore = score;
                    bestTile = new Point(col, row);
                }
            }
        }

        return bestTile;
    }

    private boolean isPlayerNearTile(int tileCol, int tileRow, int radiusTiles) {
        int playerCol = getPlayerTileCol();
        int playerRow = getPlayerTileRow();
        return Math.abs(playerCol - tileCol) <= radiusTiles
                && Math.abs(playerRow - tileRow) <= radiusTiles;
    }

    public void run(){
        double drawInterval = 1_000_000_000.0 / FPS;
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
    }

    private void drawGame(Graphics2D g2) {
        //Note: The order of rendering is so important, basically first rendered is the bottom most layer
        //followed by the next, this is how to create a layering system in the game.

        if (shouldDrawTitleScreen()) {
            ui.draw(g2);
            drawTransitionOverlay(g2);
            return;
        }

        if (gameState == introCutsceneState) {
            if (introCutscenePlayer != null) {
                introCutscenePlayer.draw(g2, screenWidth, screenHeight);
            } else {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, screenWidth, screenHeight);
            }
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

        drawTransitionOverlay(g2);
    }

    private void drawTransitionOverlay(Graphics2D g2) {
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
        if (introCutscenePlayer != null) {
            introCutscenePlayer.setVolume(masterVolume);
        }
        System.out.println("Volume: " + Math.round(masterVolume * 100) + "%");
    }

    public int getMasterVolumePercent() {
        return Math.round(masterVolume * 100);
    }

    public Point toGamePoint(int panelX, int panelY) {
        double scale = Math.min((double) getWidth() / screenWidth, (double) getHeight() / screenHeight);
        if (scale <= 0) {
            return null;
        }

        int drawWidth = (int) Math.round(screenWidth * scale);
        int drawHeight = (int) Math.round(screenHeight * scale);
        int drawX = (getWidth() - drawWidth) / 2;
        int drawY = (getHeight() - drawHeight) / 2;

        if (panelX < drawX || panelX >= drawX + drawWidth || panelY < drawY || panelY >= drawY + drawHeight) {
            return null;
        }

        int gameX = (int) ((panelX - drawX) / scale);
        int gameY = (int) ((panelY - drawY) / scale);

        gameX = Math.max(0, Math.min(screenWidth - 1, gameX));
        gameY = Math.max(0, Math.min(screenHeight - 1, gameY));
        return new Point(gameX, gameY);
    }

    // restarts the current map (for Game Over)
    public void retryCurrentMap() {
        // Stop any current action, just set game state to transition
        fadeAlpha = 0f;
        fadeOutPhase = true;
        activeTransitionType = TRANSITION_MAP;
        nextMapPath = currentMapPath; // reload same map
        showTitleDuringTransition = false;
        transitionHoldTicks = 0;
        gameState = transitionState;
        
        // Also reset player life properly since it's 0
        player.life = player.maxLife;
    }

}
