package main;

import objects.OBJ_Heart;
import objects.OBJ_Key;
import objects.SuperObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UI {
    private static final int TITLE_START_COMMAND = 0;
    private static final int TITLE_LOAD_COMMAND = 1;
    private static final int TITLE_SETTINGS_COMMAND = 2;
    private static final int TITLE_EXIT_COMMAND = 3;
    private static final int TITLE_BUTTON_WIDTH = 224;
    private static final int TITLE_BUTTON_HEIGHT = 56;
    private static final int CHARACTER_COUNT = 4;
    private static final Font INTERACTION_PROMPT_FONT = new Font("Arial", Font.BOLD, 12);
    private static final long INTERACTION_PROMPT_DURATION_NS = 5_000_000_000L;
    private static final double INTERACTION_PROMPT_BOUNCE_SPEED = 5.5;
    private static final int INTERACTION_PROMPT_BOUNCE_HEIGHT = 4;

    GamePanel gp;
    Graphics2D g2;
    Font arial_40;
    BufferedImage keyImage;
    BufferedImage health_5, health_4, health_3, health_2, health_1, health_0;
    private final DialogueBox dialogueBox;
    private final BufferedImage titleBackground;
    private final Rectangle titleBackgroundBounds;
    private final BufferedImage[] titleButtonImages;
    private final Rectangle[] titleButtonBounds;
    private final BufferedImage backButtonImage;
    private final Rectangle backButtonBounds;
    private final BufferedImage[] characterButtonImages;
    private final BufferedImage[] characterButtonHoverImages;
    private final Rectangle[] characterButtonBounds;
    private final BufferedImage[][] characterDownFrames;
    private final int[] characterDrawWidths;
    private final int[] characterDrawHeights;
    private final Font characterSelectionTitleFont;
    private int hoveredCharacterIndex = -1;
    private int characterAnimationCounter = 0;
    private int characterAnimationFrame = 0;
    private int activePromptObjectIndex = 999;
    private String activePromptText = null;
    private long activePromptExpiresAt = 0L;
    public int commandNum = 0;

    // Game Over images
    private BufferedImage gameOverTextImg;
    private BufferedImage gameOverTryAgainImg;
    private BufferedImage gameOverBackToMenuImg;
    private Rectangle gameOverTryAgainBounds;
    private Rectangle gameOverBackToMenuBounds;

    public UI(GamePanel gp) {
        this.gp = gp;

        arial_40 = new Font("Arial", Font.PLAIN, 40);
        OBJ_Key key = new OBJ_Key();
        keyImage = key.image;

        // CREATE HUD OBJECT
        SuperObject heart = new OBJ_Heart(gp);
        health_5 = heart.image;
        health_4 = heart.image2;
        health_3 = heart.image3;
        health_2 = heart.image4;
        health_1 = heart.image5;
        health_0 = heart.image6;

        dialogueBox = new DialogueBox(gp);

        UtilityTool utilityTool = new UtilityTool();
        titleBackground = loadTitleBackgroundImage();
        titleBackgroundBounds = createTitleBackgroundBounds(titleBackground);
        titleButtonImages = new BufferedImage[]{
                loadTitleButtonImage(utilityTool, "/mainmenu/btnStart.png", "START"),
                loadTitleButtonImage(utilityTool, "/mainmenu/btnLoadGame.png", "LOAD GAME"),
                loadTitleButtonImage(utilityTool, "/mainmenu/btnSettings.png", "SETTINGS"),
                loadTitleButtonImage(utilityTool, "/mainmenu/btnExit.png", "EXIT")
        };
        titleButtonBounds = createTitleButtonBounds();
        backButtonImage = loadMenuImage("/mainmenu/btn_back.png");
        backButtonBounds = createBackButtonBounds();
        characterButtonImages = new BufferedImage[]{
                loadMenuImage("/mainmenu/character_selection/llama.png"),
                loadMenuImage("/mainmenu/character_selection/gasha.png"),
                loadMenuImage("/mainmenu/character_selection/neil.png"),
                loadMenuImage("/mainmenu/character_selection/romare.png")
        };
        characterButtonHoverImages = new BufferedImage[]{
                loadMenuImage("/mainmenu/character_selection/llama_selected.png"),
                loadMenuImage("/mainmenu/character_selection/gasha_selected.png"),
                loadMenuImage("/mainmenu/character_selection/neil_selected.png"),
                loadMenuImage("/mainmenu/character_selection/romare_selected.png")
        };
        characterButtonBounds = createCharacterButtonBounds();
        characterDrawWidths = new int[]{25 * 3 + 12, 29 * 3 + 12, 25 * 3 + 12, 31 * 3 + 12};
        characterDrawHeights = new int[]{40 * 3 + 18, 32 * 3 + 18, 41 * 3 + 18, 37 * 3 + 18};
        characterDownFrames = loadCharacterDownFrames();
        characterSelectionTitleFont = new Font("Monospaced", Font.BOLD, 36);

        // Load Game Over images
        gameOverTextImg = loadMenuImage("/gameover/YouDied.png");
        gameOverTryAgainImg = loadMenuImage("/gameover/Try Again.png");
        gameOverBackToMenuImg = loadMenuImage("/gameover/BackToMenu.png");

        if (gameOverTextImg != null) {
            double scale = Math.min((double) gp.screenWidth / gameOverTextImg.getWidth(), (double) (gp.screenHeight / 2) / gameOverTextImg.getHeight());
            int newWidth = (int) Math.round(gameOverTextImg.getWidth() * scale * 0.8);
            int newHeight = (int) Math.round(gameOverTextImg.getHeight() * scale * 0.8);
            gameOverTextImg = utilityTool.scaleImage(gameOverTextImg, newWidth, newHeight);
        }

        if (gameOverTryAgainImg != null) {
            double scale = Math.min((double) gp.screenWidth / gameOverTryAgainImg.getWidth(), (double) gp.screenHeight / gameOverTryAgainImg.getHeight());
            int newWidth = (int) Math.round(gameOverTryAgainImg.getWidth() * scale * 0.35);
            int newHeight = (int) Math.round(gameOverTryAgainImg.getHeight() * scale * 0.35);
            gameOverTryAgainImg = utilityTool.scaleImage(gameOverTryAgainImg, newWidth, newHeight);
            gameOverTryAgainBounds = new Rectangle((gp.screenWidth - newWidth) / 2, gp.screenHeight / 2 + 50, newWidth, newHeight);
        } else {
            gameOverTryAgainImg = createFallbackTitleButton("TRY AGAIN");
            gameOverTryAgainBounds = new Rectangle((gp.screenWidth - TITLE_BUTTON_WIDTH) / 2, gp.screenHeight / 2 + 50, TITLE_BUTTON_WIDTH, TITLE_BUTTON_HEIGHT);
        }

        if (gameOverBackToMenuImg != null) {
            double scale = Math.min((double) gp.screenWidth / gameOverBackToMenuImg.getWidth(), (double) gp.screenHeight / gameOverBackToMenuImg.getHeight());
            int newWidth = (int) Math.round(gameOverBackToMenuImg.getWidth() * scale * 0.35);
            int newHeight = (int) Math.round(gameOverBackToMenuImg.getHeight() * scale * 0.35);
            gameOverBackToMenuImg = utilityTool.scaleImage(gameOverBackToMenuImg, newWidth, newHeight);
            gameOverBackToMenuBounds = new Rectangle((gp.screenWidth - newWidth) / 2, gp.screenHeight / 2 + 50 + gameOverTryAgainBounds.height + 20, newWidth, newHeight);
        } else {
            gameOverBackToMenuImg = createFallbackTitleButton("BACK TO MENU");
            gameOverBackToMenuBounds = new Rectangle((gp.screenWidth - TITLE_BUTTON_WIDTH) / 2, gp.screenHeight / 2 + 50 + TITLE_BUTTON_HEIGHT + 20, TITLE_BUTTON_WIDTH, TITLE_BUTTON_HEIGHT);
        }
    }

    public void showMessage(String text) {
        dialogueBox.show(text);
    }

    public void update() {
        dialogueBox.update();
        updateInteractionPrompt();
        updateCharacterSelectionAnimation();
    }

    public boolean isDialogueActive() {
        return dialogueBox.isActive();
    }

    public void advanceDialogue() {
        dialogueBox.advance();
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.white);

        if (gp.shouldDrawTitleScreen()) {
            drawTitleScreen();
        }

        if (gp.gameState == gp.characterSelectState) {
            drawCharacterSelectionScreen();
        }

        if (gp.gameState == gp.playState) {
            if (!isDialogueActive()) {
                drawPlayerLife();
                drawStaminaBar();
                drawAxeCooldown();
            }
            drawKeyInventory();
            drawInteractionPrompt();
        }

        if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }

        if (gp.gameState == gp.gameOverState) {
            drawGameOverScreen();
        }

        dialogueBox.draw(g2);
        gp.devSettings.draw(g2);
    }

    public void drawGameOverScreen() {
        g2.setColor(new Color(0, 0, 0, 150)); // semi-transparent black overlay
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int textWidth = gameOverTextImg != null ? gameOverTextImg.getWidth() : 300;
        int textHeight = gameOverTextImg != null ? gameOverTextImg.getHeight() : 100;
        int textX = (gp.screenWidth - textWidth) / 2;
        int textY = gp.screenHeight / 3 - textHeight / 2;

        if (gameOverTextImg != null) {
            g2.drawImage(gameOverTextImg, textX, textY, null);
        } else {
            String text = "YOU DIED";
            int x = getXforCenteredText(text);
            int y = gp.screenHeight / 3;
            g2.drawString(text, x, y);
        }

        drawTitleButton(gameOverTryAgainImg, gameOverTryAgainBounds, commandNum == 0);
        drawTitleButton(gameOverBackToMenuImg, gameOverBackToMenuBounds, commandNum == 1);
    }

    public void moveGameOverSelection(int delta) {
        commandNum = Math.floorMod(commandNum + delta, 2);
    }

    public boolean selectGameOverCommandAt(int x, int y) {
        if (gameOverTryAgainBounds.contains(x, y)) {
            commandNum = 0;
            return true;
        }
        if (gameOverBackToMenuBounds.contains(x, y)) {
            commandNum = 1;
            return true;
        }
        return false;
    }

    public boolean activateGameOverCommandAt(int x, int y) {
        if (!selectGameOverCommandAt(x, y)) {
            return false;
        }
        activateSelectedGameOverCommand();
        return true;
    }

    public void activateSelectedGameOverCommand() {
        if (commandNum == 0) {
            // Try Again
            gp.playMusic(1);
            gp.retryCurrentMap();
        } else if (commandNum == 1) {
            // Back to Menu
            gp.gameState = gp.titleState;
            gp.playMusic(0);
        }
    }


    public void drawTitleScreen() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.drawImage(
                titleBackground,
                titleBackgroundBounds.x,
                titleBackgroundBounds.y,
                titleBackgroundBounds.width,
                titleBackgroundBounds.height,
                null
        );

        for (int i = 0; i < titleButtonImages.length; i++) {
            drawTitleButton(titleButtonImages[i], titleButtonBounds[i], commandNum == i);
        }
    }

    public void drawPauseScreen() {
        String text = "PAUSED";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);
    }

    public int getXforCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }

    public void moveTitleSelection(int delta) {
        commandNum = Math.floorMod(commandNum + delta, getTitleCommandCount());
    }

    public boolean selectTitleCommandAt(int x, int y) {
        int hoveredCommand = getTitleCommandAt(x, y);
        if (hoveredCommand == -1) {
            return false;
        }

        commandNum = hoveredCommand;
        return true;
    }

    public boolean activateTitleCommandAt(int x, int y) {
        if (!selectTitleCommandAt(x, y)) {
            return false;
        }

        activateSelectedTitleCommand();
        return true;
    }

    public void activateSelectedTitleCommand() {
        switch (commandNum) {
            case TITLE_START_COMMAND:
                gp.gameState = gp.characterSelectState;
                break;
            case TITLE_LOAD_COMMAND:
                gp.loadGameFromCheckpoint();
                break;
            case TITLE_SETTINGS_COMMAND:
                break;
            case TITLE_EXIT_COMMAND:
                gp.exitGame();
                break;
            default:
                break;
        }
    }

    public int getTitleCommandCount() {
        return titleButtonImages.length;
    }

    private int getTitleCommandAt(int x, int y) {
        for (int i = 0; i < titleButtonBounds.length; i++) {
            if (titleButtonBounds[i].contains(x, y)) {
                return i;
            }
        }

        return -1;
    }

    public boolean updateCharacterHoverAt(int x, int y) {
        if (gp.gameState != gp.characterSelectState) {
            hoveredCharacterIndex = -1;
            return false;
        }

        hoveredCharacterIndex = getCharacterButtonAt(x, y);
        return hoveredCharacterIndex != -1 || backButtonBounds.contains(x, y);
    }

    public boolean handleCharacterSelectionClickAt(int x, int y) {
        if (gp.gameState != gp.characterSelectState) {
            return false;
        }

        if (backButtonBounds.contains(x, y)) {
            gp.gameState = gp.titleState;
            hoveredCharacterIndex = -1;
            return true;
        }

        int buttonIndex = getCharacterButtonAt(x, y);
        if (buttonIndex != -1) {
            gp.startGameFromCharacterSelection(buttonIndex);
            hoveredCharacterIndex = -1;
            return true;
        }

        return false;
    }

    public void drawKeyInventory() { // CUSTOM METHOD by Llama -- gi himo ni nako para maapil og wagtang ang key UI text (in draw method) when game is paused
        g2.drawImage(keyImage, gp.tileSize / 2, gp.tileSize / 2, gp.tileSize, gp.tileSize, null);
        g2.drawString("x " + gp.player.hasKey, 74, 65);
    }

    public void drawPlayerLife() {
        int x = gp.tileSize / 2;
        int y = gp.screenHeight - (gp.tileSize * 2);

        int life = gp.player.life;
        int maxLife = gp.player.maxLife;

        int slots = 5;
        int lifePerSlot = maxLife / slots;
        int filledSlots = (int) Math.ceil((double) life / lifePerSlot);

        switch (filledSlots) {
            case 5:
                g2.drawImage(health_5, x, y, null);
                break;
            case 4:
                g2.drawImage(health_4, x, y, null);
                break;
            case 3:
                g2.drawImage(health_3, x, y, null);
                break;
            case 2:
                g2.drawImage(health_2, x, y, null);
                break;
            case 1:
                g2.drawImage(health_1, x, y, null);
                break;
            default:
                g2.drawImage(health_0, x, y, null);
                break;
        }
    }

    public void drawStaminaBar() {
        int barX = gp.tileSize / 2;
        int barY = gp.screenHeight - (gp.tileSize * 2) - gp.tileSize / 2;
        int barWidth = gp.tileSize * 3;
        int barHeight = 12;

        g2.setColor(new Color(20, 20, 20, 190));
        g2.fillRoundRect(barX, barY, barWidth, barHeight, 8, 8);

        float staminaPercent = gp.player.getStaminaPercent();
        int fillWidth = Math.round((barWidth - 4) * staminaPercent);
        if (gp.player.isExhausted()) {
            g2.setColor(new Color(200, 70, 70));
        } else if (gp.player.isSprintOnCooldown()) {
            g2.setColor(new Color(230, 180, 70));
        } else if (staminaPercent <= 0.20f) {
            g2.setColor(new Color(210, 70, 70));
        } else if (staminaPercent <= 0.50f) {
            g2.setColor(new Color(235, 200, 70));
        } else {
            g2.setColor(new Color(90, 210, 120));
        }
        g2.fillRoundRect(barX + 2, barY + 2, fillWidth, barHeight - 4, 6, 6);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(barX, barY, barWidth, barHeight, 8, 8);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14F));
        if (gp.player.isExhausted()) {
            g2.drawString("EXHAUSTED", barX + barWidth + 10, barY + 11);
        } else if (gp.player.isSprintOnCooldown()) {
            g2.drawString("Recovering... " + gp.player.getCooldownSecondsRemaining() + "s", barX + barWidth + 10, barY + 11);
        }
        g2.setFont(arial_40);
    }

    public void drawAxeCooldown() {
        if (!gp.player.hasAxe) {
            return;
        }

        int x = gp.tileSize / 2;
        int y = gp.screenHeight - (gp.tileSize * 3);
        int width = gp.tileSize * 3;
        int height = 18;
        boolean coolingDown = gp.player.hasAxeCooldownActive();

        String text = "AXE READY";
        Color fillColor = new Color(65, 160, 95);
        if (coolingDown) {
            text = "RESTING " + gp.player.getActiveAxeCooldownSecondsRemaining() + "s";
            fillColor = gp.player.isAxeDoorCooldownActive()
                    ? new Color(185, 95, 65)
                    : new Color(210, 160, 65);
        }

        g2.setColor(new Color(20, 20, 20, 190));
        g2.fillRoundRect(x, y, width, height, 8, 8);

        int fillWidth = width - 4;
        if (coolingDown) {
            fillWidth = Math.round((width - 4) * gp.player.getAxeCooldownReadyPercent());
        }

        g2.setColor(fillColor);
        g2.fillRoundRect(x + 2, y + 2, fillWidth, height - 4, 6, 6);

        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, width, height, 8, 8);

        Font oldFont = g2.getFont();
        g2.setFont(oldFont.deriveFont(Font.BOLD, 13F));
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, textX, textY);
        g2.setFont(oldFont);
    }

    private void updateInteractionPrompt() {
        if (gp.gameState != gp.playState || isDialogueActive()) {
            clearActivePrompt();
            return;
        }

        int objectIndex = findPromptObjectIndex();
        if (objectIndex == 999) {
            clearActivePrompt();
            return;
        }

        SuperObject object = gp.obj[objectIndex];
        String prompt = getInteractionPrompt(object);
        if (prompt == null) {
            clearActivePrompt();
            return;
        }

        if (objectIndex != activePromptObjectIndex || !prompt.equals(activePromptText)) {
            activePromptObjectIndex = objectIndex;
            activePromptText = prompt;
            activePromptExpiresAt = System.nanoTime() + INTERACTION_PROMPT_DURATION_NS;
        }
    }

    private void drawInteractionPrompt() {
        if (activePromptObjectIndex == 999 || activePromptText == null || System.nanoTime() > activePromptExpiresAt) {
            return;
        }

        if (activePromptObjectIndex >= gp.obj.length) {
            clearActivePrompt();
            return;
        }

        SuperObject object = gp.obj[activePromptObjectIndex];
        if (object == null) {
            clearActivePrompt();
            return;
        }

        int objectCenterX = object.stageX + object.solidArea.x + object.solidArea.width / 2;
        int objectTopY = object.stageY + object.solidArea.y;
        Point promptPoint = gp.stageToScreenPoint(objectCenterX, objectTopY);

        Font oldFont = g2.getFont();
        Color oldColor = g2.getColor();
        Composite oldComposite = g2.getComposite();

        g2.setFont(INTERACTION_PROMPT_FONT);
        FontMetrics fm = g2.getFontMetrics();
        double seconds = System.nanoTime() / 1_000_000_000.0;
        int bounceOffset = (int) Math.round(Math.sin(seconds * INTERACTION_PROMPT_BOUNCE_SPEED) * INTERACTION_PROMPT_BOUNCE_HEIGHT);
        int textWidth = fm.stringWidth(activePromptText);
        int textX = Math.max(8, Math.min(gp.screenWidth - textWidth - 8, promptPoint.x - textWidth / 2));
        int textY = Math.max(fm.getAscent() + 8, promptPoint.y - 10 + bounceOffset);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(activePromptText, textX + 1, textY + 1);

        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(new Color(248, 242, 225));
        g2.drawString(activePromptText, textX, textY);

        g2.setComposite(oldComposite);
        g2.setColor(oldColor);
        g2.setFont(oldFont);
    }

    private void clearActivePrompt() {
        activePromptObjectIndex = 999;
        activePromptText = null;
        activePromptExpiresAt = 0L;
    }

    private int findPromptObjectIndex() {
        Rectangle interactionArea = getPlayerInteractionArea();
        int closestIndex = 999;
        int closestDistanceSquared = Integer.MAX_VALUE;
        int playerCenterX = gp.player.stageX + gp.player.solidArea.x + gp.player.solidArea.width / 2;
        int playerCenterY = gp.player.stageY + gp.player.solidArea.y + gp.player.solidArea.height / 2;

        for (int i = 0; i < gp.obj.length; i++) {
            SuperObject object = gp.obj[i];
            if (object == null || getInteractionPrompt(object) == null) {
                continue;
            }

            Rectangle objectArea = getObjectStageArea(object);
            if (!interactionArea.intersects(objectArea)) {
                continue;
            }

            int objectCenterX = objectArea.x + objectArea.width / 2;
            int objectCenterY = objectArea.y + objectArea.height / 2;
            int dx = playerCenterX - objectCenterX;
            int dy = playerCenterY - objectCenterY;
            int distanceSquared = dx * dx + dy * dy;
            if (distanceSquared < closestDistanceSquared) {
                closestDistanceSquared = distanceSquared;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    private Rectangle getPlayerInteractionArea() {
        Rectangle area = new Rectangle(
                gp.player.stageX + gp.player.solidArea.x,
                gp.player.stageY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height);
        int distance = gp.tileSize / 2;

        switch (gp.player.direction) {
            case "up":
                area.y -= distance;
                break;
            case "down":
                area.y += distance;
                break;
            case "left":
                area.x -= distance;
                break;
            case "right":
                area.x += distance;
                break;
            default:
                break;
        }

        return area;
    }

    private Rectangle getObjectStageArea(SuperObject object) {
        return new Rectangle(
                object.stageX + object.solidArea.x,
                object.stageY + object.solidArea.y,
                object.solidArea.width,
                object.solidArea.height);
    }

    private String getInteractionPrompt(SuperObject object) {
        switch (object.name) {
            case "Empty_Table":
            case "Key_Table":
                return "Press E to search";
            case "TablePaper":
                return "Press E to read";
            case "Bag":
            case "Axe":
                return "Press E to pick up";
            case "DoorStage1":
            case "DoorStage3":
                return "Press E to escape";
            case "DoorStage2":
                return gp.player.hasAxe ? "Press E to break" : "Press E to inspect";
            case "BreakableTable":
            case "CutsceneBreakable":
                return gp.player.hasAxe ? "Press E to break" : "Press E to inspect";
            case "HealthBag":
                return "Press E to use";
            case "Candle":
                return "Press E to light";
            case "Switch":
                return "Press E to flip";
            case "JhonPorkJerkyJake":
                return "Press E to talk";
            default:
                return null;
        }
    }

    private BufferedImage loadTitleBackgroundImage() {
        BufferedImage image = loadMenuImage("/mainmenu/bgMainMenu.png");
        if (image != null) {
            return image;
        }

        return createFallbackTitleBackground();
    }

    private BufferedImage loadTitleButtonImage(UtilityTool utilityTool, String resourcePath, String label) {
        BufferedImage image = loadMenuImage(resourcePath);
        if (image == null) {
            return createFallbackTitleButton(label);
        }

        return utilityTool.scaleImage(image, TITLE_BUTTON_WIDTH, TITLE_BUTTON_HEIGHT);
    }

    private BufferedImage loadMenuImage(String resourcePath) {
        BufferedImage classpathImage = readImageFromClasspath(resourcePath);
        if (classpathImage != null) {
            return classpathImage;
        }

        String relativePath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        return readImageFromDisk(Paths.get("res").resolve(relativePath));
    }

    private Rectangle createBackButtonBounds() {
        if (backButtonImage == null) {
            int fallbackWidth = 64;
            int fallbackHeight = 40;
            int x = (gp.screenWidth - fallbackWidth) / 2;
            int y = gp.screenHeight - (gp.tileSize + fallbackHeight);
            return new Rectangle(x, y, fallbackWidth, fallbackHeight);
        }
        int width = backButtonImage.getWidth();
        int height = backButtonImage.getHeight();
        int x = (gp.screenWidth - width) / 2;
        int y = gp.screenHeight - (gp.tileSize + height);
        return new Rectangle(x, y, width, height);
    }

    private Rectangle[] createCharacterButtonBounds() {
        Rectangle[] bounds = new Rectangle[CHARACTER_COUNT];
        int buttonY = gp.screenHeight - (gp.tileSize * 3);
        int centerY = buttonY + (TITLE_BUTTON_HEIGHT / 2);
        int[] anchorXs = createCharacterAnchorXs();

        for (int i = 0; i < CHARACTER_COUNT; i++) {
            BufferedImage buttonImage = characterButtonImages[i];
            int width = buttonImage != null ? buttonImage.getWidth() : TITLE_BUTTON_WIDTH;
            int height = buttonImage != null ? buttonImage.getHeight() : TITLE_BUTTON_HEIGHT;
            bounds[i] = new Rectangle(anchorXs[i] - (width / 2), centerY - (height / 2), width, height);
        }

        return bounds;
    }

    private int[] createCharacterAnchorXs() {
        int[] anchors = new int[CHARACTER_COUNT];
        int spacing = gp.screenWidth / (CHARACTER_COUNT + 1);
        for (int i = 0; i < CHARACTER_COUNT; i++) {
            anchors[i] = spacing * (i + 1);
        }
        return anchors;
    }

    private BufferedImage[][] loadCharacterDownFrames() {
        BufferedImage[][] frames = new BufferedImage[CHARACTER_COUNT][4];

        String[] characterFolders = {"Lerler", "Gasha", "Nnyl", "Gemal"};
        String[] framePrefixes = {"cabo_down_", "gasha_down_", "rayos_down_", "gemal_down_"};

        for (int i = 0; i < CHARACTER_COUNT; i++) {
            for (int frame = 0; frame < 4; frame++) {
                String imagePath = "/player/" + characterFolders[i] + "/Down/" + framePrefixes[i] + (frame + 1) + ".png";
                frames[i][frame] = loadMenuImage(imagePath);
            }
        }

        return frames;
    }

    private int getCharacterButtonAt(int x, int y) {
        for (int i = 0; i < characterButtonBounds.length; i++) {
            if (characterButtonBounds[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    private void updateCharacterSelectionAnimation() {
        if (gp.gameState != gp.characterSelectState) {
            characterAnimationCounter = 0;
            characterAnimationFrame = 0;
            return;
        }

        characterAnimationCounter++;
        if (characterAnimationCounter > 12) {
            characterAnimationCounter = 0;
            characterAnimationFrame = (characterAnimationFrame + 1) % 4;
        }
    }

    private void drawCharacterSelectionScreen() {
        drawTitleBackgroundOnly();

        Composite previousComposite = g2.getComposite();
        Color previousColor = g2.getColor();
        Font previousFont = g2.getFont();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(new Color(250, 245, 220));
        g2.setFont(characterSelectionTitleFont);
        String title = "Choose your Character";
        int titleX = getXforCenteredText(title);
        int titleY = titleBackgroundBounds.y + gp.tileSize;
        g2.drawString(title, titleX, titleY);

        drawCharacterSprites();
        drawCharacterButtons();
        drawBackButton();

        g2.setComposite(previousComposite);
        g2.setColor(previousColor);
        g2.setFont(previousFont);
    }

    private void drawCharacterSprites() {
        int spriteCenterY = gp.screenHeight / 2;
        for (int i = 0; i < CHARACTER_COUNT; i++) {
            BufferedImage spriteImage = getCharacterDisplayFrame(i);
            if (spriteImage == null) {
                continue;
            }

            int drawWidth = characterDrawWidths[i];
            int drawHeight = characterDrawHeights[i];
            int buttonCenterX = characterButtonBounds[i].x + (characterButtonBounds[i].width / 2);
            int drawX = buttonCenterX - (drawWidth / 2);
            int drawY = spriteCenterY - (drawHeight / 2);
            g2.drawImage(spriteImage, drawX, drawY, drawWidth, drawHeight, null);
        }
    }

    private BufferedImage getCharacterDisplayFrame(int characterIndex) {
        if (hoveredCharacterIndex == characterIndex) {
            BufferedImage animatedFrame = characterDownFrames[characterIndex][characterAnimationFrame];
            if (animatedFrame != null) {
                return animatedFrame;
            }
        }
        return characterDownFrames[characterIndex][0];
    }

    private void drawCharacterButtons() {
        for (int i = 0; i < CHARACTER_COUNT; i++) {
            boolean hovered = hoveredCharacterIndex == i;
            BufferedImage image = hovered ? characterButtonHoverImages[i] : characterButtonImages[i];
            if (image == null) {
                continue;
            }

            Rectangle bounds = characterButtonBounds[i];
            g2.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
        }
    }

    private void drawBackButton() {
        if (backButtonImage != null) {
            g2.drawImage(backButtonImage, backButtonBounds.x, backButtonBounds.y, backButtonBounds.width, backButtonBounds.height, null);
        }
    }

    private void drawTitleBackgroundOnly() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.drawImage(
                titleBackground,
                titleBackgroundBounds.x,
                titleBackgroundBounds.y,
                titleBackgroundBounds.width,
                titleBackgroundBounds.height,
                null
        );
    }

    private BufferedImage readImageFromClasspath(String resourcePath) {
        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                return null;
            }

            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                System.err.println("Unreadable title image resource: " + resourcePath);
            }
            return image;
        } catch (IOException e) {
            System.err.println("Failed to load title image resource: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage readImageFromDisk(Path path) {
        if (!Files.exists(path)) {
            System.err.println("Missing title image file: " + path);
            return null;
        }

        try (InputStream stream = Files.newInputStream(path)) {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                System.err.println("Unreadable title image file: " + path);
            }
            return image;
        } catch (IOException e) {
            System.err.println("Failed to load title image file: " + path);
            e.printStackTrace();
            return null;
        }
    }

    private Rectangle createTitleBackgroundBounds(BufferedImage image) {
        if (image == null) {
            return new Rectangle(0, 0, gp.screenWidth, gp.screenHeight);
        }

        double scale = Math.min(
                (double) gp.screenWidth / image.getWidth(),
                (double) gp.screenHeight / image.getHeight()
        );

        int width = (int) Math.round(image.getWidth() * scale);
        int height = (int) Math.round(image.getHeight() * scale);
        int x = (gp.screenWidth - width) / 2;
        int y = (gp.screenHeight - height) / 2;
        return new Rectangle(x, y, width, height);
    }

    private Rectangle[] createTitleButtonBounds() {
        Rectangle[] bounds = new Rectangle[titleButtonImages.length];
        int buttonWidth = titleButtonImages[0].getWidth();
        int buttonHeight = titleButtonImages[0].getHeight();
        int gap = gp.tileSize / 2;
        int totalHeight = (buttonHeight * titleButtonImages.length) + (gap * (titleButtonImages.length - 1));
        int x = titleBackgroundBounds.x + (gp.tileSize + gp.tileSize / 2);
        int y = titleBackgroundBounds.y + ((titleBackgroundBounds.height - totalHeight) / 2);

        for (int i = 0; i < bounds.length; i++) {
            bounds[i] = new Rectangle(x, y + (i * (buttonHeight + gap)), buttonWidth, buttonHeight);
        }

        return bounds;
    }

    private void drawTitleButton(BufferedImage image, Rectangle bounds, boolean selected) {
        Graphics2D buttonG = (Graphics2D) g2.create();

        if (selected) {
            buttonG.setColor(new Color(255, 255, 255, 48));
            buttonG.fillRoundRect(bounds.x - 14, bounds.y - 10, bounds.width + 28, bounds.height + 20, 18, 18);
            buttonG.setColor(new Color(255, 233, 164, 230));
            buttonG.setStroke(new BasicStroke(3f));
            buttonG.drawRoundRect(bounds.x - 14, bounds.y - 10, bounds.width + 28, bounds.height + 20, 18, 18);
        }

        buttonG.drawImage(image, bounds.x, bounds.y, null);
        buttonG.dispose();
    }

    private BufferedImage createFallbackTitleBackground() {
        BufferedImage image = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D fallbackGraphics = image.createGraphics();

        fallbackGraphics.setColor(new Color(10, 10, 14));
        fallbackGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        int brickWidth = Math.max(260, image.getWidth() / 3);
        fallbackGraphics.setColor(new Color(84, 46, 42));
        fallbackGraphics.fillRect(0, 0, brickWidth, image.getHeight());

        fallbackGraphics.setColor(new Color(109, 63, 57));
        for (int row = 0; row < image.getHeight(); row += 36) {
            int offset = (row / 36) % 2 == 0 ? 0 : 28;
            for (int col = -offset; col < brickWidth; col += 56) {
                fallbackGraphics.fillRoundRect(col, row, 52, 30, 4, 4);
            }
        }

        fallbackGraphics.setColor(new Color(0, 0, 0, 120));
        fallbackGraphics.fillRect(brickWidth, 0, image.getWidth() - brickWidth, image.getHeight());
        fallbackGraphics.dispose();
        return image;
    }

    private BufferedImage createFallbackTitleButton(String label) {
        BufferedImage image = new BufferedImage(TITLE_BUTTON_WIDTH, TITLE_BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D fallbackGraphics = image.createGraphics();

        fallbackGraphics.setColor(new Color(35, 38, 48, 230));
        fallbackGraphics.fillRoundRect(0, 0, TITLE_BUTTON_WIDTH, TITLE_BUTTON_HEIGHT, 14, 14);
        fallbackGraphics.setColor(new Color(228, 229, 234));
        fallbackGraphics.setStroke(new BasicStroke(2f));
        fallbackGraphics.drawRoundRect(1, 1, TITLE_BUTTON_WIDTH - 3, TITLE_BUTTON_HEIGHT - 3, 14, 14);

        fallbackGraphics.setColor(Color.WHITE);
        fallbackGraphics.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics metrics = fallbackGraphics.getFontMetrics();
        int textX = (TITLE_BUTTON_WIDTH - metrics.stringWidth(label)) / 2;
        int textY = ((TITLE_BUTTON_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();
        fallbackGraphics.drawString(label, textX, textY);

        fallbackGraphics.dispose();
        return image;
    }
}
