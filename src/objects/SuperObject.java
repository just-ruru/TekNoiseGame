package objects;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SuperObject {
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int stageX, stageY;
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public void draw(Graphics2D g2, GamePanel gp){
        int screenX =  stageX - gp.player.stageX + gp.player.screenX;
        int screenY = stageY - gp.player.stageY + gp.player.screenY;

        if(stageX + gp.tileSize > gp.player.stageX - gp.player.screenX &&
                stageX - gp.tileSize < gp.player.stageX + gp.player.screenX &&
                stageY + gp.tileSize > gp.player.stageY - gp.player.screenY &&
                stageY - gp.tileSize < gp.player.stageY + gp.player.screenY) {

            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
