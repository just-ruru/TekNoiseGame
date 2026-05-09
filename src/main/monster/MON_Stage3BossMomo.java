package main.monster;

import entity.Entity;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MON_Stage3BossMomo extends Entity {
    private static final int DETECTION_RANGE = 8;
    private static final float CHASE_SPEED = 1.15f;
    private static final float WANDER_SPEED = 0.7f;
    private static final int WANDER_TURN_TICKS = 120;
    private static final int DRAW_SIZE_TILES = 3;
    private final Random random = new Random();
    private boolean forceChase = false;

    public MON_Stage3BossMomo(GamePanel gp) {
        super(gp);
        type = 1;
        name = "Stage3BossMomo";
        speed = WANDER_SPEED;
        direction = "down";
        maxLife = 28;
        life = maxLife;

        // Keep the collision footprint smaller than one tile so the boss can still
        // move through single-tile paths while the sprite reads as much larger.
        solidArea.x = 12;
        solidArea.y = 20;
        solidArea.width = 24;
        solidArea.height = 24;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        up1 = loadSprite("/monster/stage3BossMomo/Back/sprite-back-1.png");
        up2 = loadSprite("/monster/stage3BossMomo/Back/sprite-back-2.png");
        up3 = loadSprite("/monster/stage3BossMomo/Back/sprite-back-3.png");
        up4 = loadSprite("/monster/stage3BossMomo/Back/sprite-back-4.png");

        down1 = loadSprite("/monster/stage3BossMomo/Front/sprite-front-1.png");
        down2 = loadSprite("/monster/stage3BossMomo/Front/sprite-front-2.png");
        down3 = loadSprite("/monster/stage3BossMomo/Front/sprite-front-3.png");
        down4 = loadSprite("/monster/stage3BossMomo/Front/sprite-front-4.png");

        left1 = loadSprite("/monster/stage3BossMomo/Left/sprite-left-1.png");
        left2 = loadSprite("/monster/stage3BossMomo/Left/sprite-left-2.png");
        left3 = loadSprite("/monster/stage3BossMomo/Left/sprite-left-3.png");
        left4 = loadSprite("/monster/stage3BossMomo/Left/sprite-left-4.png");

        right1 = loadSprite("/monster/stage3BossMomo/Right/sprite-right-1.png");
        right2 = loadSprite("/monster/stage3BossMomo/Right/sprite-right-2.png");
        right3 = loadSprite("/monster/stage3BossMomo/Right/sprite-right-3.png");
        right4 = loadSprite("/monster/stage3BossMomo/Right/sprite-right-4.png");
    }

    @Override
    public void setAction() {
        int deltaX = gp.player.stageX - stageX;
        int deltaY = gp.player.stageY - stageY;
        int tileDistance = (Math.abs(deltaX) + Math.abs(deltaY)) / gp.tileSize;

        if (forceChase || tileDistance <= DETECTION_RANGE) {
            speed = CHASE_SPEED;
            String pathDirection = findPathDirectionToPlayer();
            if (pathDirection != null) {
                direction = pathDirection;
                return;
            }

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                direction = deltaX > 0 ? "right" : "left";
            } else {
                direction = deltaY > 0 ? "down" : "up";
            }
            return;
        }

        speed = WANDER_SPEED;
        actionLockCounter++;
        if (actionLockCounter >= WANDER_TURN_TICKS) {
            int value = random.nextInt(4);
            direction = value == 0 ? "up" : value == 1 ? "down" : value == 2 ? "left" : "right";
            actionLockCounter = 0;
        }
    }

    public void activateForStage3Chase() {
        forceChase = true;
        speed = CHASE_SPEED;
    }

    @Override
    public void draw(Graphics2D g2) {
        int cameraStageX = gp.getCameraStageX();
        int cameraStageY = gp.getCameraStageY();
        int drawSize = gp.tileSize * DRAW_SIZE_TILES;

        // Center the large sprite around the entity's tile and let it hang upward.
        int screenX = stageX - cameraStageX + gp.player.screenX - gp.tileSize;
        int screenY = stageY - cameraStageY + gp.player.screenY - (gp.tileSize * 2);

        if (stageX + drawSize > cameraStageX - gp.player.screenX &&
                stageX - drawSize < cameraStageX + gp.player.screenX &&
                stageY + drawSize > cameraStageY - gp.player.screenY &&
                stageY - drawSize < cameraStageY + gp.player.screenY) {

            BufferedImage image = getCurrentImage();
            if (image != null) {
                g2.drawImage(image, screenX, screenY, drawSize, drawSize, null);
            }
        }
    }

    private BufferedImage getCurrentImage() {
        switch (direction) {
            case "up":
                return getDirectionalFrame(up1, up2, up3, up4);
            case "left":
                return getDirectionalFrame(left1, left2, left3, left4);
            case "right":
                return getDirectionalFrame(right1, right2, right3, right4);
            case "down":
            default:
                return getDirectionalFrame(down1, down2, down3, down4);
        }
    }

    private BufferedImage getDirectionalFrame(BufferedImage frame1, BufferedImage frame2,
                                              BufferedImage frame3, BufferedImage frame4) {
        switch (spriteNum) {
            case 1: return frame1;
            case 2: return frame2 != null ? frame2 : frame1;
            case 3: return frame3 != null ? frame3 : frame1;
            case 4: return frame4 != null ? frame4 : frame2 != null ? frame2 : frame1;
            default: return frame1;
        }
    }

    private BufferedImage loadSprite(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                System.out.println("Warning: missing sprite " + path);
                return null;
            }
            return ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
