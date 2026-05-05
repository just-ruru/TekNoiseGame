package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Entity {
    GamePanel gp;
    public int stageX, stageY;
    public float speed;
    private float moveRemainderX = 0f;
    private float moveRemainderY = 0f;

    public BufferedImage up1, up2, up3, up4, down1, down2, down3, down4, left1, left2, left3, left4, right1, right2, right3, right4;
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidArea;

    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public int type; //0 = player, 1 = monster

    //CHARACTER STATUS
    public int maxLife;
    public int life;

    public String name;

    public int actionLockCounter = 0;

    public boolean invincible = false;
    public int invincibleCounter = 0;

    public Entity(GamePanel gp) {
        this.gp = gp;
        this.solidArea = new Rectangle();
    }

    public void setAction() {}

    public int getCollisionStep() {
        return Math.max(1, (int)Math.ceil(speed));
    }

    public void moveStage(float dx, float dy) {
        moveRemainderX += dx;
        moveRemainderY += dy;

        int moveX = (int)moveRemainderX;
        int moveY = (int)moveRemainderY;

        stageX += moveX;
        stageY += moveY;

        moveRemainderX -= moveX;
        moveRemainderY -= moveY;
    }

    public void update() {

        setAction();

        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkEntity(this, gp.monster);
            gp.cChecker.checkPlayer(this);
        boolean contactPlayer = gp.cChecker.checkPlayer(this);

        if(this.type == 1 && contactPlayer == true) {
            if(gp.player.invincible == false) {
                //we can give damage
                gp.player.life -= 1;
                gp.player.invincible = true;
            }
        }
        if(collisionOn == false) {

            switch(direction) {
                case "up":
                    moveStage(0, -speed);
                    break;

                case "down":
                    moveStage(0, speed);
                    break;

                case "left":
                    moveStage(-speed, 0);
                    break;

                case "right":
                    moveStage(speed, 0);
                    break;
            }
        }

        spriteCounter++;
        if(spriteCounter > 12){
            if(spriteNum == 1){
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 3;
            } else if (spriteNum == 3) {
                spriteNum = 4;
            } else if (spriteNum == 4) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public BufferedImage setup(String imagePath) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void draw(Graphics2D g2) {
        int screenX = stageX - gp.player.stageX + gp.player.screenX;
        int screenY = stageY - gp.player.stageY + gp.player.screenY;

        // Only draw if within the on-screen area
        if (stageX + gp.tileSize > gp.player.stageX - gp.player.screenX &&
            stageX - gp.tileSize < gp.player.stageX + gp.player.screenX &&
            stageY + gp.tileSize > gp.player.stageY - gp.player.screenY &&
            stageY - gp.tileSize < gp.player.stageY + gp.player.screenY) {

            BufferedImage image = null;
            boolean useFirstFrame = (spriteNum == 1 || spriteNum == 3);

            if (direction == null) direction = "down";

            switch (direction) {
                case "up":
                    image = useFirstFrame ? (up1 != null ? up1 : up2) : (up2 != null ? up2 : up1);
                    break;
                case "down":
                    image = useFirstFrame ? (down1 != null ? down1 : down2) : (down2 != null ? down2 : down1);
                    break;
                case "left":
                    image = useFirstFrame ? (left1 != null ? left1 : left2) : (left2 != null ? left2 : left1);
                    break;
                case "right":
                    image = useFirstFrame ? (right1 != null ? right1 : right2) : (right2 != null ? right2 : right1);
                    break;
            }

            if (image != null) {
                g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}
