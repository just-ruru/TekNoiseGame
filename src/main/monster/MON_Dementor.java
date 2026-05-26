package main.monster;

import entity.Monster;
import main.GamePanel;

public class MON_Dementor extends Monster {

    private int imageVariant;
    private boolean forceChase = false;
    private static final float STAGE3_CHASE_SPEED = 2.8f;

    public MON_Dementor(GamePanel gp, int imageVariant) {
        super(gp);
        
        this.imageVariant = imageVariant;
        name = "Dementor";
        maxLife = 4;
        life = maxLife;
        direction = "down";

        // Initialize monster properties using the abstract class method
        initializeMonster(3, 1f, 1f, 3); // detectionRange, wanderSpeed, chaseSpeed, wanderRadius

        solidArea.x = 12;
        solidArea.y = 24;
        solidArea.width = 24;
        solidArea.height = 24;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        loadImages();
    }

    @Override
    protected void loadImages() {
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
                down1 = setup("/monster/gemal_down1");
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

    @Override
    public void setAction() {
        if (forceChase) {
            // Force chase mode - always chase player
            enterChaseMode();
        } else {
            // Use the default monster behavior from abstract class
            updateMonsterBehavior();
        }
    }

    public void activateForStage3Chase() {
        forceChase = true;
        speed = STAGE3_CHASE_SPEED;
    }
}
