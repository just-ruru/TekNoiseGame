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
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;

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
    }

    public void showMessage (String text) {

        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.white);
        g2.drawImage(keyImage, gp.tileSize/2, gp.tileSize/2, gp.tileSize, gp.tileSize, null);
        g2.drawString("x "+ gp.player.hasKey, 74, 65);

        // MESSAGE
        if(messageOn == true) {

            g2.setFont(g2.getFont().deriveFont(30F));
            g2.drawString(message, gp.tileSize/2, gp.tileSize*5);

            messageCounter++;

            if(messageCounter > 120) {
                messageCounter = 0;
                messageOn = false;
            }
        }

        //PLAY STATE
        drawPlayerLife(); //put this method inside if condition for playstate once gamestates are implemented


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
