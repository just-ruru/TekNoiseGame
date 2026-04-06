package entity;

import main.GamePanel;
import main.KeyHandler;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.Key;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;
    private int choice;

    public final int screenX;
    public final int screenY;
    int hasKey = 0;

    public Player(GamePanel gp, KeyHandler keyH, int choice){
        this.gp = gp;
        this.keyH = keyH;
        this.choice = choice;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 45;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 28;
        solidArea.height = 32;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){
        stageX = gp.tileSize * 3;
        stageY = gp.tileSize * 11;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {

        switch (choice) {
            case 1:
                try {
                    up1 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Up/cabo_up_1.png"));
                    up2 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Up/cabo_up_2.png"));
                    up3 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Up/cabo_up_3.png"));
                    up4 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Up/cabo_up_4.png"));

                    down1 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Down/cabo_down_1.png"));
                    down2 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Down/cabo_down_2.png"));
                    down3 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Down/cabo_down_3.png"));
                    down4 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Down/cabo_down_4.png"));

                    left1 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Left/cabo_left_1.png"));
                    left2 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Left/cabo_left_2.png"));
                    left3 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Left/cabo_left_3.png"));
                    left4 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Left/cabo_left_4.png"));

                    right1 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Right/cabo_right_1.png"));
                    right2 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Right/cabo_right_2.png"));
                    right3 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Right/cabo_right_3.png"));
                    right4 = ImageIO.read(getClass().getResourceAsStream("/player/Lerler/Right/cabo_right_4.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    up1 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Up/gasha_up_1.png"));
                    up2 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Up/gasha_up_2.png"));
                    up3 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Up/gasha_up_3.png"));
                    up4 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Up/gasha_up_4.png"));

                    down1 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Down/gasha_down_1.png"));
                    down2 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Down/gasha_down_2.png"));
                    down3 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Down/gasha_down_3.png"));
                    down4 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Down/gasha_down_4.png"));

                    left1 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Left/gasha_left_1.png"));
                    left2 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Left/gasha_left_2.png"));
                    left3 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Left/gasha_left_3.png"));
                    left4 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Left/gasha_left_4.png"));

                    right1 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Right/gasha_right_1.png"));
                    right2 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Right/gasha_right_2.png"));
                    right3 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Right/gasha_right_3.png"));
                    right4 = ImageIO.read(getClass().getResourceAsStream("/player/Gasha/Right/gasha_right_4.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    up1 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Up/rayos_up_1.png"));
                    up2 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Up/rayos_up_2.png"));
                    up3 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Up/rayos_up_3.png"));
                    up4 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Up/rayos_up_4.png"));

                    down1 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Down/rayos_down_1.png"));
                    down2 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Down/rayos_down_2.png"));
                    down3 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Down/rayos_down_3.png"));
                    down4 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Down/rayos_down_4.png"));

                    left1 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Left/rayos_left_1.png"));
                    left2 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Left/rayos_left_2.png"));
                    left3 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Left/rayos_left_3.png"));
                    left4 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Left/rayos_left_4.png"));

                    right1 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Right/rayos_right_1.png"));
                    right2 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Right/rayos_right_2.png"));
                    right3 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Right/rayos_right_3.png"));
                    right4 = ImageIO.read(getClass().getResourceAsStream("/player/Nnyl/Right/rayos_right_4.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 4:
                try {
                    up1 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Up/gemal_up_1.png"));
                    up2 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Up/gemal_up_2.png"));
                    up3 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Up/gemal_up_3.png"));
                    up4 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Up/gemal_up_4.png"));

                    down1 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Down/gemal_down_1.png"));
                    down2 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Down/gemal_down_2.png"));
                    down3 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Down/gemal_down_3.png"));
                    down4 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Down/gemal_down_4.png"));

                    left1 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Left/gemal_left_1.png"));
                    left2 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Left/gemal_left_2.png"));
                    left3 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Left/gemal_left_3.png"));
                    left4 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Left/gemal_left_4.png"));

                    right1 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Right/gemal_right_1.png"));
                    right2 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Right/gemal_right_2.png"));
                    right3 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Right/gemal_right_3.png"));
                    right4 = ImageIO.read(getClass().getResourceAsStream("/player/Gemal/Right/gemal_right_4.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void update(){
        if(keyH.isUpPressed == true || keyH.isDownPressed == true || keyH.isLeftPressed == true || keyH.isRightPressed == true){
            if(keyH.isUpPressed == true){
                direction = "up";
            } else if (keyH.isDownPressed == true) {
                direction = "down";
            } else if (keyH.isLeftPressed == true) {
                direction = "left";
            } else if (keyH.isRightPressed == true) {
                direction = "right";
            }

            // CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);

            //CHECK OBJ COLLISSION
            int objIndex = gp.cChecker.checkObject(this, true);
//            pickUpObject(objIndex);
            // IF COLLISION IS FALSE, PLAYER CAN MOVE
            if(collisionOn == false) {

                switch(direction) {
                    case "up":
                        stageY -= speed;
                        break;

                    case "down":
                        stageY += speed;
                        break;

                    case "left":
                        stageX -= speed;
                        break;

                    case "right":
                        stageX += speed;
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
    }

//    public void pickUpObject(int i){
//        if(i != 999){
//           gp.obj[i] = null;
//        }
//    }

    public void draw(Graphics2D g2) {
//        g2.setColor(Color.RED);
//        g2.fillOval(x, y, gp.tileSize, gp.tileSize);

        BufferedImage image = null;

        switch (direction) {
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                }
                if (spriteNum == 2) {
                    image = up2;
                }
                if (spriteNum == 3) {
                    image = up3;
                }
                if (spriteNum == 4) {
                    image = up4;
                }
                break;

            case "down":
                if (spriteNum == 1) {
                    image = down1;
                }
                if (spriteNum == 2) {
                    image = down2;
                }
                if (spriteNum == 3) {
                    image = down3;
                }
                if (spriteNum == 4) {
                    image = down4;
                }
                break;

            case "left":
                if (spriteNum == 1) {
                    image = left1;
                }
                if (spriteNum == 2) {
                    image = left2;
                }
                if (spriteNum == 3) {
                    image = left3;
                }
                if (spriteNum == 4) {
                    image = left4;
                }
                break;

            case "right":
                if (spriteNum == 1) {
                    image = right1;
                }
                if (spriteNum == 2) {
                    image = right2;
                }
                if (spriteNum == 3) {
                    image = right3;
                }
                if (spriteNum == 4) {
                    image = right4;
                }
                break;
        }

        switch (choice) {
            case 1:
                g2.drawImage(image, screenX, screenY, 25 * 2, 40 * 2, null);
                break;

            case 2:
                g2.drawImage(image, screenX, screenY, 29 * 2, 32 * 2, null);
                break;

            case 3:
                g2.drawImage(image, screenX, screenY, 25 * 2, 41 * 2, null);
                break;

            case 4:
                g2.drawImage(image, screenX, screenY, 31 * 2, 37 * 2, null);
                break;
            default:
                break;
        }
    }
}

