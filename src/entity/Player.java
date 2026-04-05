package entity;

import main.GamePanel;
import main.KeyHandler;

import java.awt.*;
import java.security.Key;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;
    }

    public void setDefaultValues(){
        x = 100;
        y = 100;
        speed = 4;
    }

    public void update(){
        if(keyH.isUpPressed == true){
            y -= speed;
        } else if (keyH.isDownPressed == true) {
            y += speed;
        } else if (keyH.isLeftPressed == true) {
            x -= speed;
        } else if (keyH.isRightPressed == true) {
            x += speed;
        }
    }

    public void draw(Graphics2D g2){
        g2.setColor(Color.RED);

        g2.fillOval(x, y, gp.tileSize, gp.tileSize);

    }
}
