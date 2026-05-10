package main.monster;

import entity.Entity;
import main.GamePanel;
import objects.SuperObject;

import java.util.Random;

public class MON_Stage2WitherBoss extends Entity {
    private static final float CHASE_SPEED = 0.9f;
    private static final float WANDER_SPEED = 0.55f;
    private static final int DETECTION_RANGE = 5;
    private static final int STAGE2_WANDER_ROW_INDEX = 2;
    private static final int SUMMON_INTERVAL_TICKS = 300;
    private static final int CHASE_PATH_RECALC_TICKS = 6;
    private static final int RETURN_TO_DOOR_INTERVAL_TICKS = 420;
    private static final int RETURN_TO_DOOR_DURATION_TICKS = 240;
    private static final int DOOR_ARRIVAL_DISTANCE_TILES = 1;
    private boolean active = false;
    private boolean firstSummonDone = false;
    private boolean summonsEnabled = true;
    private int summonCounter = 0;
    private int returnToDoorCounter = 0;
    private int returnToDoorTicks = 0;
    private boolean returningToDoor = false;
    private final Random random = new Random();
    private int chasePathRecalcCounter = 0;
    private String cachedChaseDirection = null;

    public MON_Stage2WitherBoss(GamePanel gp) {
        super(gp);
        type = 1;
        name = "Stage2WitherBoss";
        speed = WANDER_SPEED;
        direction = "down";
        maxLife = 20;
        life = maxLife;

        solidArea.x = 8;
        solidArea.y = 12;
        solidArea.width = 32;
        solidArea.height = 36;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        up1 = setup("/monster/stage2WItherBoss/WitherSlime1");
        up2 = setup("/monster/stage2WItherBoss/WitherSlime2");
        up3 = setup("/monster/stage2WItherBoss/WitherSlime3");
        up4 = setup("/monster/stage2WItherBoss/WitherSlime4");
        down1 = up1;
        down2 = up2;
        down3 = up3;
        down4 = up4;
        left1 = up1;
        left2 = up2;
        left3 = up3;
        left4 = up4;
        right1 = up1;
        right2 = up2;
        right3 = up3;
        right4 = up4;
    }

    public void activate() {
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void activateForStage3Chase() {
        active = true;
        summonsEnabled = false;
        firstSummonDone = true;
        summonCounter = 0;
    }

    @Override
    public void update() {
        if (!active) {
            animateOnly();
            return;
        }

        handleSummons();
        super.update();
    }

    @Override
    public void setAction() {
        int deltaX = gp.player.stageX - stageX;
        int deltaY = gp.player.stageY - stageY;
        int tileDistance = (Math.abs(deltaX) + Math.abs(deltaY)) / gp.tileSize;

        if (tileDistance <= DETECTION_RANGE) {
            returningToDoor = false;
            returnToDoorTicks = 0;
            speed = CHASE_SPEED;
            if (chasePathRecalcCounter <= 0 || cachedChaseDirection == null) {
                cachedChaseDirection = findPathDirectionToPlayer();
                chasePathRecalcCounter = CHASE_PATH_RECALC_TICKS;
            } else {
                chasePathRecalcCounter--;
            }
            if (cachedChaseDirection != null) {
                direction = cachedChaseDirection;
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
        cachedChaseDirection = null;
        chasePathRecalcCounter = 0;
        if (moveBackToWanderRowIfNeeded()) {
            return;
        }

        actionLockCounter++;
        if (actionLockCounter >= 120) {
            direction = random.nextBoolean() ? "left" : "right";
            actionLockCounter = 0;
        }
    }

    private boolean moveBackToWanderRowIfNeeded() {
        int currentRow = getCenterStageY() / gp.tileSize;
        if (currentRow == STAGE2_WANDER_ROW_INDEX) {
            return false;
        }
        direction = currentRow < STAGE2_WANDER_ROW_INDEX ? "down" : "up";
        return true;
    }

    private boolean updateReturnToDoorBehavior() {
        int[] doorCenter = findStage2DoorCenter();
        if (doorCenter == null) {
            returningToDoor = false;
            return false;
        }

        if (!returningToDoor) {
            returnToDoorCounter++;
            if (returnToDoorCounter < RETURN_TO_DOOR_INTERVAL_TICKS) {
                return false;
            }

            returningToDoor = true;
            returnToDoorTicks = RETURN_TO_DOOR_DURATION_TICKS;
            returnToDoorCounter = 0;
        }

        returnToDoorTicks--;
        int distanceToDoorTiles = getTileDistanceTo(doorCenter[0], doorCenter[1]);
        if (distanceToDoorTiles <= DOOR_ARRIVAL_DISTANCE_TILES || returnToDoorTicks <= 0) {
            returningToDoor = false;
            return false;
        }

        String pathDirection = findPathDirectionToStagePoint(doorCenter[0], doorCenter[1]);
        if (pathDirection != null) {
            direction = pathDirection;
            return true;
        }

        int deltaX = doorCenter[0] - stageX;
        int deltaY = doorCenter[1] - stageY;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            direction = deltaX > 0 ? "right" : "left";
        } else {
            direction = deltaY > 0 ? "down" : "up";
        }
        return true;
    }

    private int[] findStage2DoorCenter() {
        for (SuperObject object : gp.obj) {
            if (object != null && "DoorStage2".equals(object.name)) {
                return new int[] {
                        object.stageX + object.solidArea.x + object.solidArea.width / 2,
                        object.stageY + object.solidArea.y + object.solidArea.height / 2
                };
            }
        }
        return null;
    }

    private int getTileDistanceTo(int targetX, int targetY) {
        int deltaX = targetX - getCenterStageX();
        int deltaY = targetY - getCenterStageY();
        return (Math.abs(deltaX) + Math.abs(deltaY)) / gp.tileSize;
    }

    private void handleSummons() {
        if (!summonsEnabled) {
            return;
        }
        if (!firstSummonDone) {
            System.out.println("[BOSS] Performing initial summon of 5 minions");
            gp.spawnStage2Minions(stageX, stageY, 5);
            firstSummonDone = true;
            summonCounter = 0;
            return;
        }

        summonCounter++;
        if (summonCounter >= SUMMON_INTERVAL_TICKS) {
            System.out.println("[BOSS] Performing periodic summon (interval reached: " + summonCounter + "/" + SUMMON_INTERVAL_TICKS + ")");
            gp.spawnStage2Minions(stageX, stageY, 1);
            summonCounter = 0;
        }
    }

    private void animateOnly() {
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = spriteNum == 4 ? 1 : spriteNum + 1;
            spriteCounter = 0;
        }
    }

    @Override
    public void draw(java.awt.Graphics2D g2) {
        int cameraStageX = gp.getCameraStageX();
        int cameraStageY = gp.getCameraStageY();
        int screenX = stageX - cameraStageX + gp.player.screenX - gp.tileSize / 2;
        int screenY = stageY - cameraStageY + gp.player.screenY - gp.tileSize / 2;
        int drawSize = gp.tileSize * 2;

        if (stageX + drawSize > cameraStageX - gp.player.screenX &&
                stageX - drawSize < cameraStageX + gp.player.screenX &&
                stageY + drawSize > cameraStageY - gp.player.screenY &&
                stageY - drawSize < cameraStageY + gp.player.screenY) {

            java.awt.image.BufferedImage image = getCurrentBossImage();
            if (image != null) {
                g2.drawImage(image, screenX, screenY, drawSize, drawSize, null);
            }
        }
    }

    private java.awt.image.BufferedImage getCurrentBossImage() {
        switch (spriteNum) {
            case 1: return down1;
            case 2: return down2;
            case 3: return down3;
            case 4: return down4;
            default: return down1;
        }
    }
}
