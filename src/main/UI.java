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

        //PLAY STATE
        if(gp.gameState == gp.playState) {
            drawPlayerLife();
            drawKeyInventory();
        }

        if(gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }

        dialogueBox.draw(g2);

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
        int y = gp.screenHeight - gp.tileSize - gp.tileSize / 2;

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

}
