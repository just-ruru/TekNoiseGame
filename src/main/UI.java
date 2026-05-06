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
    public int commandNum = 0;

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
    }

    public void showMessage(String text) {
        dialogueBox.show(text);
    }

    public void update() {
        dialogueBox.update();
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

        if (gp.gameState == gp.playState) {
            drawPlayerLife();
            drawKeyInventory();
            drawStaminaBar();
            drawAxeCooldown();
        }

        if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }

        dialogueBox.draw(g2);
        gp.devSettings.draw(g2);
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

    public void activateSelectedTitleCommand() {
        switch (commandNum) {
            case TITLE_START_COMMAND:
                gp.startGameFromTitle();
                break;
            case TITLE_LOAD_COMMAND:
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
