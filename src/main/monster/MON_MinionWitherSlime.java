package main.monster;

import entity.Entity;
import main.GamePanel;

public class MON_MinionWitherSlime extends Entity {
    private static final int LIFE_TICKS = 600;
    private static final int FADE_TICKS = 6;
    private int lifeTicks = LIFE_TICKS;
    private float alpha = 1f;

    public MON_MinionWitherSlime(GamePanel gp) {
        super(gp);
        type = 2;
        name = "MinionWitherSlime";
        speed = 2f;
        direction = "left";

        solidArea.x = 20;
        solidArea.y = 20;
        solidArea.width = 8;
        solidArea.height = 8;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        left1 = setup("/monster/minionWitherSlime/Left/MiniWitherSlimeLeft1");
        left2 = setup("/monster/minionWitherSlime/Left/MiniWitherSlimeLeft2");
        left3 = setup("/monster/minionWitherSlime/Left/MiniWitherSlimeLeft3");
        left4 = setup("/monster/minionWitherSlime/Left/MiniWitherSlimeLeft4");
        right1 = setup("/monster/minionWitherSlime/Right/MiniWitherSlimeRight1");
        right2 = setup("/monster/minionWitherSlime/Right/MiniWitherSlimeRight2");
        right3 = setup("/monster/minionWitherSlime/Right/MiniWitherSlimeRight3");
        right4 = setup("/monster/minionWitherSlime/Right/MiniWitherSlimeRight4");
        up1 = left1;
        up2 = left2;
        up3 = left3;
        up4 = left4;
        down1 = right1;
        down2 = right2;
        down3 = right3;
        down4 = right4;
    }

    @Override
    public void update() {
        lifeTicks--;
        if (lifeTicks <= FADE_TICKS) {
            alpha = Math.max(0f, lifeTicks / (float) FADE_TICKS);
        }
        if (lifeTicks <= 0) {
            removeFromWorld = true;
            return;
        }

        setAction();

        collisionOn = false;
        gp.cChecker.checkTile(this);
        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        if (contactPlayer) {
            hitPlayer();
            return;
        }

        if (!collisionOn) {
            moveInCurrentDirection();
            if (isOverlappingPlayer()) {
                hitPlayer();
                return;
            }
        } else {
            tryAlternateEnemyMovement();
            if (isOverlappingPlayer()) {
                hitPlayer();
                return;
            }
        }

        spriteCounter++;
        if (spriteCounter > 8) {
            spriteNum = spriteNum == 4 ? 1 : spriteNum + 1;
            spriteCounter = 0;
        }
    }

    public void hitPlayer() {
        if (removeFromWorld) {
            return;
        }
        if (gp.ui != null && gp.ui.isDialogueActive()) {
            return;
        }
        gp.playSE(7);
        gp.applyStage2MinionHitEffect();
        removeFromWorld = true;
    }

    private boolean isOverlappingPlayer() {
        java.awt.Rectangle minionArea = new java.awt.Rectangle(
                stageX + solidArea.x,
                stageY + solidArea.y,
                solidArea.width,
                solidArea.height);
        java.awt.Rectangle playerArea = new java.awt.Rectangle(
                gp.player.stageX + gp.player.solidArea.x,
                gp.player.stageY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height);
        return minionArea.intersects(playerArea);
    }

    @Override
    public void setAction() {
        int deltaX = gp.player.stageX - stageX;
        int deltaY = gp.player.stageY - stageY;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            direction = deltaX > 0 ? "right" : "left";
        } else {
            direction = deltaY > 0 ? "down" : "up";
        }
    }

    @Override
    public void draw(java.awt.Graphics2D g2) {
        java.awt.Composite oldComposite = g2.getComposite();
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));

        int cameraStageX = gp.getCameraStageX();
        int cameraStageY = gp.getCameraStageY();
        int drawSize = gp.tileSize / 2;
        int screenX = stageX - cameraStageX + gp.player.screenX + gp.tileSize / 4;
        int screenY = stageY - cameraStageY + gp.player.screenY + gp.tileSize / 4;

        if (stageX + gp.tileSize > cameraStageX - gp.player.screenX &&
                stageX - gp.tileSize < cameraStageX + gp.player.screenX &&
                stageY + gp.tileSize > cameraStageY - gp.player.screenY &&
                stageY - gp.tileSize < cameraStageY + gp.player.screenY) {

            java.awt.image.BufferedImage image = getCurrentMinionImage();
            if (image != null) {
                g2.drawImage(image, screenX, screenY, drawSize, drawSize, null);
            }
        }

        g2.setComposite(oldComposite);
    }

    private java.awt.image.BufferedImage getCurrentMinionImage() {
        boolean facingRight = "right".equals(direction) || "down".equals(direction);
        switch (spriteNum) {
            case 1: return facingRight ? right1 : left1;
            case 2: return facingRight ? right2 : left2;
            case 3: return facingRight ? right3 : left3;
            case 4: return facingRight ? right4 : left4;
            default: return facingRight ? right1 : left1;
        }
    }
}
