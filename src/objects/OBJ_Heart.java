package objects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Heart extends SuperObject{
    GamePanel gp;
    public OBJ_Heart(GamePanel gp) {
        this.gp = gp;
        name = "Heart";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/healthBar_1.png"));
            image2 = ImageIO.read(getClass().getResourceAsStream("/objects/healthBar_2.png"));
            image3 = ImageIO.read(getClass().getResourceAsStream("/objects/healthBar_3.png"));
            image4 = ImageIO.read(getClass().getResourceAsStream("/objects/healthBar_4.png"));
            image5 = ImageIO.read(getClass().getResourceAsStream("/objects/healthBar_5.png"));
            image6 = ImageIO.read(getClass().getResourceAsStream("/objects/healthBar_6.png"));
            image = uTool.scaleImage(image, gp.tileSize * 5, gp.tileSize - 8);
            image2 = uTool.scaleImage(image2, gp.tileSize * 5, gp.tileSize);
            image3 = uTool.scaleImage(image3, gp.tileSize * 5, gp.tileSize);
            image4 = uTool.scaleImage(image4, gp.tileSize * 5, gp.tileSize);
            image5 = uTool.scaleImage(image5, gp.tileSize * 5, gp.tileSize);
            image6 = uTool.scaleImage(image6, gp.tileSize * 5, gp.tileSize);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
