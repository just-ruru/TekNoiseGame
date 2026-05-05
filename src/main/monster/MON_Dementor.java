package main.monster;

import entity.Entity;
import main.GamePanel;

import java.util.Random;

public class MON_Dementor extends Entity {

    private int imageVariant;

    public MON_Dementor(GamePanel gp, int imageVariant) {
        super(gp);

        this.imageVariant = imageVariant;
        type = 1;
        name = "Dementor";
        speed = 1;
        maxLife = 4;
        life = maxLife;
        direction = "down";

        solidArea.x = 8;
        solidArea.y = 45;
        solidArea.width = 28;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage() {
        switch(imageVariant) {
            case 1: // cabo
                up1 = setup("/monster/cabo_down1");
                up2 = setup("/monster/cabo_down2");
                down1 = setup("/monster/cabo_down1");
                down2 = setup("/monster/cabo_down2");
                left1 = setup("/monster/cabo_down1");
                left2 = setup("/monster/cabo_down2");
                right1 = setup("/monster/cabo_down1");
                right2 = setup("/monster/cabo_down2");
                break;

            case 2: // gasha
                up1 = setup("/monster/gasha_down1");
                up2 = setup("/monster/gasha_down2");
                down1 = setup("/monster/gasha_down1");
                down2 = setup("/monster/gasha_down2");
                left1 = setup("/monster/gasha_down1");
                left2 = setup("/monster/gasha_down2");
                right1 = setup("/monster/gasha_down1");
                right2 = setup("/monster/gasha_down2");
                break;

            case 3: // gemal
                up1 = setup("/monster/gemal_down1");
                up2 = setup("/monster/gemal_down2");
                down1 = setup("/monster/gemal_down1");;
                down2 = setup("/monster/gemal_down2");
                left1 = setup("/monster/gemal_down1");
                left2 = setup("/monster/gemal_down2");
                right1 = setup("/monster/gemal_down1");
                right2 = setup("/monster/gemal_down2");
                break;

            case 4: // rayos
                up1 = setup("/monster/rayos_down1");
                up2 = setup("/monster/rayos_down2");
                down1 = setup("/monster/rayos_down1");
                down2 = setup("/monster/rayos_down2");
                left1 = setup("/monster/rayos_down1");
                left2 = setup("/monster/rayos_down2");
                right1 = setup("/monster/rayos_down1");
                right2 = setup("/monster/rayos_down2");
                break;

            default: // Fallback to variant 1
                up1 = setup("/monster/cabo_down1");
                up2 = setup("/monster/cabo_down2");
                down1 = setup("/monster/cabo_down1");
                down2 = setup("/monster/cabo_down2");
                left1 = setup("/monster/cabo_down1");
                left2 = setup("/monster/cabo_down2");
                right1 = setup("/monster/cabo_down1");
                right2 = setup("/monster/cabo_down2");
                break;
        }
    }

    public void setAction() {
        actionLockCounter ++;

        if(actionLockCounter == 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;

            if(i <= 25) {
                direction = "up";
            }
            if(i > 25 && i <= 50) {
                direction = "down";
            }
            if(i > 50 && i <= 75) {
                direction = "left";
            }
            if(i > 75 && i < 100) {
                direction = "right";
            }

            actionLockCounter = 0;
        }
    }
}
