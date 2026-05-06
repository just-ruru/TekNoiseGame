package entity;

import main.GamePanel;
import main.KeyHandler;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;

import main.Sound;
import main.VignetteLight;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;
    Sound walkingSound;
    private int choice;
    private int interactCooldown = 0;

    public final int screenX;
    public final int screenY;
    public int hasKey = 0;
    public boolean hasReplacementSwitch = false;


    public Player(GamePanel gp, KeyHandler keyH, int choice){
        super(gp);

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

        walkingSound = new Sound();
        walkingSound.setFile(5);
    }

    public void setDefaultValues(){
        speed = 4f;
        direction = "down";

        //PLAYER STATUS
        maxLife = 5;
        life = maxLife;
    }

    public void setSpawnForMap(String mapPath){
        // Set spawn using tile coordinates: stageX = col * tileSize, stageY = row * tileSize
        if ("/maps/stage02.txt".equals(mapPath)) {
            stageX = gp.tileSize * 2;
            stageY = gp.tileSize * 2;
            return;
        }
        
        if ("/maps/stage03.txt".equals(mapPath)) {
            stageX = gp.tileSize * 3;
            stageY = gp.tileSize * 11;
            return;
        }

        // Default (stage01 and any unknown maps)
        stageX = gp.tileSize * 3;
        stageY = gp.tileSize * 11;
    }

    public void getPlayerImage(){
        switch(choice){
            case 1:
                try{
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
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            case 2:
                try{
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
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            case 3:
                try{
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
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;

            case 4:
                try{
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
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;

            default:

                break;
        }

    }

    public void update(){
        if (gp.ui.isDialogueActive()) {
            if (keyH.interactPressed) {
                gp.ui.advanceDialogue();
                keyH.interactPressed = false;
            }
            walkingSound.stop();
            return;
        }

        if (interactCooldown > 0) {
            interactCooldown--;
        }

        boolean moving = keyH.isUpPressed || keyH.isDownPressed || keyH.isLeftPressed || keyH.isRightPressed;

        if(moving){

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

            //CHECK OBJ COLLISION
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            //CHECK MONSTER COLLISION
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            contactMonster(monsterIndex);

            // IF COLLISION IS FALSE, PLAYER CAN MOVE
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

        if (moving) {
            if (!walkingSound.clip.isRunning()) {
                walkingSound.loop(); // continuous walking loop
            }
        } else {
            walkingSound.stop();
        }


        // Allow interaction while standing still: press E when overlapping an object
        if (keyH.interactPressed) {
            // Prefer an interaction box in front of the player (so you don't need to be overlapping)
            int objIndex = gp.cChecker.checkObjectInFront(this, true, gp.tileSize / 2);
            if (objIndex == 999) {
                // fallback: if you're already overlapping something, still allow it
                objIndex = gp.cChecker.checkObjectAtCurrentPosition(this, true);
            }
            pickUpObject(objIndex);
        }

        if(invincible == true) {
            invincibleCounter++;
            if(invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    public void pickUpObject(int i){
        if(i != 999){
            String objectName = gp.obj[i].name;

            switch(objectName){
                case "Switch":
                    if (keyH.interactPressed && interactCooldown == 0) {              // E was pressed
                        if (gp.vignette.wereLightsActivated()) {
                            gp.ui.showMessage(getSwitchAlreadyOnDialogue());
                        } else if (!hasReplacementSwitch) {
                            gp.ui.showMessage(getBrokenSwitchDialogue());
                        } else if (gp.vignette.turnLightsOn()) {
                            hasReplacementSwitch = false;
                            gp.ui.showMessage(getSwitchFixedDialogue());
                        } else {
                            gp.ui.showMessage(getSwitchAlreadyOnDialogue());
                        }
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "Key":
                    gp.playSE(3);
                    hasKey++;
                    gp.obj[i]=null;
                    gp.ui.showMessage(getLooseKeyDialogue());

                    break;

                case "Empty_Table":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        gp.ui.showMessage(getEmptyTableDialogue());
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "Key_Table":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        int tableX = gp.obj[i].stageX;
                        int tableY = gp.obj[i].stageY;
                        gp.playSE(3);
                        hasKey++;
                        gp.obj[i] = new objects.OBJ_Empty_Table();
                        gp.obj[i].stageX = tableX;
                        gp.obj[i].stageY = tableY;
                        gp.ui.showMessage(getKeyTableDialogue());
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "DoorStage1":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        if(hasKey > 0) {
                            gp.playSE(3);
                            hasKey--;
                            gp.ui.showMessage(getDoorOpenedDialogue());
                            gp.startMapTransition("/maps/stage02.txt");
                        } else {
                            gp.ui.showMessage(getLockedDoorDialogue());
                        }
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "DoorStage2":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        if(hasKey > 0) {
                            gp.playSE(3);
                            hasKey--;
                            gp.ui.showMessage(getDoorOpenedDialogue());
                            gp.startMapTransition("/maps/stage03.txt");
                        } else {
                            gp.ui.showMessage(getLockedDoorDialogue());
                        }
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "DoorStage3":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        if(hasKey > 0) {
                            gp.playSE(3);
                            hasKey--;
                            gp.ui.showMessage(getDoorOpenedDialogue());
                            gp.startMapTransition("/maps/stage03.txt");
                        } else {
                            gp.ui.showMessage(getLockedDoorDialogue());
                        }
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "TablePaper":
                    if(keyH.interactPressed && interactCooldown == 0){
                        gp.ui.showMessage(getTablePaperDialogue());

                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "Bag":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        hasReplacementSwitch = true;
                        gp.obj[i] = null;
                        gp.ui.showMessage(getBagDialogue());
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

            }
        }
    }

    private String getStagePath() {
        return gp.getCurrentMapPath();
    }

    private boolean isStage(String mapPath) {
        return mapPath.equals(getStagePath());
    }

    private String getSwitchAlreadyOnDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "The repaired switch is humming steadily.\n\nLeave it alone. This room needs every bit of light.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "The switch is already holding the lights together.\n\nDo not touch it again.";
        }
        return "The switch is already on.\n\nWe need it to stay on, so don't touch it!";
    }

    private String getBrokenSwitchDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "The switch panel is cracked open.\n\nSomething is missing from inside it.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "The switch is dead.\n\nIt needs a replacement part before it can work.";
        }
        return "The switch seems to be broken...\n\nThere should be something here that can fix it.";
    }

    private String getSwitchFixedDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "You fit the spare switch into place.\n\nThe lights struggle, then turn on.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "The replacement switch clicks in.\n\nThe room lights wake up.";
        }
        return "You replaced the broken switch.\n\nLights are now on.";
    }

    private String getLooseKeyDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "You found a key.\n\nKeys in bag: " + hasKey;
        }
        if (isStage("/maps/stage03.txt")) {
            return "Another key...\n\nKeys in bag: " + hasKey;
        }
        return "You got a key!\n\nKeys in bag: " + hasKey;
    }

    private String getEmptyTableDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "You searched the table.\n\nOnly dust and torn notes.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "You checked the table carefully.\n\nNothing but scratches.";
        }
        return "You searched the table.\n\nNothing useful here.";
    }

    private String getKeyTableDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "You searched the drawer.\n\nA key was hidden under loose papers.\n\nKeys in bag: " + hasKey;
        }
        if (isStage("/maps/stage03.txt")) {
            return "You searched beneath the table.\n\nA cold key was taped underneath.\n\nKeys in bag: " + hasKey;
        }
        return "You searched the table.\n\nThere's a key hidden underneath!\n\nKeys in bag: " + hasKey;
    }

    private String getDoorOpenedDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "You used 1 key.\n\nThe next door unlocked.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "You used 1 key.\n\nThe door gives way.";
        }
        return "You used 1 key.\n\nThe door opened!";
    }

    private String getLockedDoorDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "The door will not move.\n\nThere has to be a key somewhere in this room.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "Locked again.\n\nFind the key before this place finds you.";
        }
        return "The door is locked.\n\nYou need a key.";
    }

    private String getTablePaperDialogue() {
        if(isStage("/maps/stage01.txt")){
            return "Name: Jhon Pork Tocino\n\n"
                    + "Hmmm...... *turns the page\n\n"
                    + "Scribles* Scribles* \n\n"
                    + "This is getting creepy...\n\n"
                    + "*turns the page\n\n"
                    + "Ooh something is written at the back part\n\n"
                    + "\"Idk if someone will eventually read this...\"\n\n" + "I am JPT,,, a student just like you\n\n"
                    + "\"This place... isnt what u think it is...\n\n"
                    + "\"I've been studying endlessly just like you, with no sleep at all\"\n\n"
                    + "\"and then I got to class,, fell asleep,, and when I woke up,, I became trapped here\"\n\n"
                    + "\"I've been wandering around and...\"\n\n" + "\"there's a few things you should now\"\n\n"
                    + "\"1. there are shadowy creatures here that wander around, avoid them\"\n\n"
                    + "\"2. you should check the tables with drawers for items\"\n\n" + "\"you should be able to find some eventually...\"\n\n" + " I hope\"\n\n"
                    + "\"and lastly... I left some couple of pages here and there to maybe help you\"\n\n"
                    + "\"...\"\n\n"
                    + "\"You should read them\"\n\n"
                    + "\"...\"\n\n"
                    + "\"Anyways, I was trying to fix the switch here but I forgot where my bag was..\"\n\n so maybe find that first---\n\n"
                    + "\"The lights...\"\n\n\"they're...\"\n\n\"they're...\"\n\n\"here...\"\n\n"
                    + "\"Remember...\"\n\n\"AVOID THEM!!!!!\"\n\n"
                    + "The rest of the page was ripped off with some red paint splots over it...\n\n"
                    + "... I wonder what happened...";
        }

        if (isStage("/maps/stage02.txt")) {
            return "The paper is covered in smudged diagrams.\n\n"
                    + "Rooms, doors, switches...\n\n"
                    + "Someone was trying to understand the pattern.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "The page is almost unreadable.\n\n"
                    + "One line remains clear:\n\n"
                    + "\"If the lights fail, run before the whispers get close.\"";
        }
        return "bye muna world";
    }

    private String getBagDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "You searched the bag.\n\nThere is another spare switch inside.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "You opened the bag.\n\nA switch part is wrapped in old cloth.";
        }
        return "You searched the bag.\n\nInside is a spare switch that might fix the lights.";
    }

    public void contactMonster(int i){
        if(i != 999){
            if(invincible == false) {
                gp.playSE(7);
                life -= 1;
                invincible = true;
            }
        }
    }

    public void setWalkingVolume(float volume) {
        walkingSound.setVolume(volume);
    }

    public void draw(Graphics2D g2) {
//        g2.setColor(Color.RED);
//        g2.fillOval(x, y, gp.tileSize, gp.tileSize);

        BufferedImage image = null;

        switch(direction){
            case "up":
                if(spriteNum == 1){
                    image = up1;
                }
                if(spriteNum == 2){
                    image = up2;
                }
                if(spriteNum == 3){
                    image = up3;
                }
                if(spriteNum == 4){
                    image = up4;
                }
                break;

            case "down":
                if(spriteNum == 1){
                    image = down1;
                }
                if(spriteNum == 2){
                    image = down2;
                }
                if(spriteNum == 3){
                    image = down3;
                }
                if(spriteNum == 4){
                    image = down4;
                }
                break;

            case "left":
                if(spriteNum == 1){
                    image = left1;
                }
                if(spriteNum == 2){
                    image = left2;
                }
                if(spriteNum == 3){
                    image = left3;
                }
                if(spriteNum == 4){
                    image = left4;
                }
                break;

            case "right":
                if(spriteNum == 1){
                    image = right1;
                }
                if(spriteNum == 2){
                    image = right2;
                }
                if(spriteNum == 3){
                    image = right3;
                }
                if(spriteNum == 4){
                    image = right4;
                }
                break;
        }

        // Flickering effect during invincibility - must be set BEFORE drawing
        if(invincible == true) {
            // Flicker every 5 frames (creates a visible blinking effect)
            if(invincibleCounter % 10 < 5) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            }
        }

        switch(choice) {
            case 1:
                g2.drawImage(image, screenX, screenY, 25 * 2, 40 * 2, null);
            break;

            case 2:
                g2.drawImage(image, screenX, screenY, 29*2, 32*2, null);
            break;

            case 3:
                g2.drawImage(image, screenX, screenY, 25*2, 41*2, null);
            break;

            case 4:
                g2.drawImage(image, screenX, screenY, 31*2, 37*2, null);
            break;
            default:
                break;
        }

        //reset alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        //debug
//        g2.setFont(new Font("Arial", Font.PLAIN, 26));
//        g2.setColor(Color.WHITE);
//        g2.drawString("Invincible: " + invincibleCounter, 10, 400);
    }
}

