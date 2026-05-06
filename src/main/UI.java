package main;

import objects.OBJ_Heart;
import objects.OBJ_Key;
import objects.SuperObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UI {

    GamePanel gp;
    Graphics2D g2;
    Font arial_40;
    BufferedImage keyImage;
    BufferedImage health_5, health_4, health_3, health_2, health_1, health_0;
    private final DialogueBox dialogueBox;
    public int commandNum = 0;

    public UI (GamePanel gp) {
        this.gp = gp;

        arial_40 = new Font("Arial", Font.PLAIN, 40);
        OBJ_Key key = new OBJ_Key();
        keyImage = key.image;

        //CREATE HUD OBJECT
        SuperObject heart = new OBJ_Heart(gp);
        health_5 = heart.image;
        health_4 = heart.image2;
        health_3 = heart.image3;
        health_2 = heart.image4;
        health_1 = heart.image5;
        health_0 = heart.image6;

        dialogueBox = new DialogueBox(gp);
    }

    public void showMessage (String text) {
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

        // TITLE STATE
        if(gp.gameState == gp.titleState) {
            drawTitleScreen();
        }

        //PLAY STATE
        if(gp.gameState == gp.playState) {
            drawPlayerLife();
            drawKeyInventory();
            drawStaminaBar();
            drawAxeCooldown();
        }

        if(gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }

        dialogueBox.draw(g2);
        gp.devSettings.draw(g2);

    }

    public void drawTitleScreen() {

        g2.setColor(new Color(70,120,80));
        g2.fillRect(0,0,gp.screenWidth, gp.screenHeight);

        // TITLE NAME
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
        String text = "TekNoise";
        int x = getXforCenteredText(text);
        int y = gp.tileSize*3;

        // SHADOW
        g2.setColor(Color.black);
        g2.drawString(text, x+5, y+5);
        // MAIN COLOR
        g2.setColor(Color.white);
        g2.drawString(text,x,y);

        // LLAMA IMAGE
        x = gp.screenWidth / 2 - (gp.tileSize*2)/2;
        y += gp.tileSize * 2;
        g2.drawImage(gp.player.down1, x, y, gp.tileSize*2, gp.tileSize*2, null);

        // MENU
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

        text = "START";
        x = getXforCenteredText(text);
        y += gp.tileSize*4;
        g2.drawString(text, x, y);
        if(commandNum == 0) {
            g2.drawString(">", x - gp.tileSize, y);
        }

        text = "LOAD GAME";
        x = getXforCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(commandNum == 1) {
            g2.drawString(">", x - gp.tileSize, y);
        }

        text = "QUIT";
        x = getXforCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(commandNum == 2) {
            g2.drawString(">", x - gp.tileSize, y);
        }

    }
    public void drawPauseScreen() {

        String text = "PAUSED";

        int x = getXforCenteredText(text);
        int y = gp.screenHeight/2;

        g2.drawString(text, x, y);
    }

    public int getXforCenteredText(String text) {

        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth/2 - length/2;

        return x;
    }

    public void drawKeyInventory() { // CUSTOM METHOD by Llama -- gi himo ni nako para maapil og wagtang ang key UI text (in draw method) when game is paused
        g2.drawImage(keyImage, gp.tileSize/2, gp.tileSize/2, gp.tileSize, gp.tileSize, null);
        g2.drawString("x "+ gp.player.hasKey, 74, 65);
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
            case 5: g2.drawImage(health_5, x, y, null); break;
            case 4: g2.drawImage(health_4, x, y, null); break;
            case 3: g2.drawImage(health_3, x, y, null); break;
            case 2: g2.drawImage(health_2, x, y, null); break;
            case 1: g2.drawImage(health_1, x, y, null); break;
            default:
                g2.drawImage(health_0, x, y, null); break;
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
            String cooldownName = gp.player.isAxeDoorCooldownActive() ? "DOOR" : "TABLE";
            text = "RESTING" + " " + gp.player.getActiveAxeCooldownSecondsRemaining() + "s";
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

}
