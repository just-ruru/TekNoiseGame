package objects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class OBJ_DoorStage1 extends objects.SuperObject {
    public OBJ_DoorStage1(){
        name = "DoorStage1";
        collision = true;

        // Default door hitbox: only the lower half blocks the player (bottom tile).
        // (Tile size in this project is 48, see GamePanel.tileSize.)
        solidArea = new Rectangle(0, 48, 48, 48);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/doorfull.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp){
        int screenX =  stageX - gp.player.stageX + gp.player.screenX;
        int screenY = stageY - gp.player.stageY + gp.player.screenY;

        // Door sprite is meant to be 2 tiles tall.
        int drawW = gp.tileSize;
        int drawH = gp.tileSize * 2;

        if(stageX + drawW > gp.player.stageX - gp.player.screenX &&
                stageX - drawW < gp.player.stageX + gp.player.screenX &&
                stageY + drawH > gp.player.stageY - gp.player.screenY &&
                stageY - drawH < gp.player.stageY + gp.player.screenY) {

            g2.drawImage(image, screenX, screenY, drawW, drawH, null);
        }
    }
}




