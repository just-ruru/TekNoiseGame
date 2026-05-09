package entity;

import main.GamePanel;
import main.KeyHandler;
import main.monster.MON_MinionWitherSlime;
import npc.NPC_JhonPorkJerkyJake;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import main.Sound;

public class Player extends Entity{
    private static final float DEFAULT_WALK_SPEED = 2.5f;
    private static final float SPRINT_SPEED_MULTIPLIER = 1.6f;
    private static final float EXHAUSTED_SPEED = 1f;
    private static final int MAX_STAMINA_TICKS = 200;
    private static final int EXHAUSTED_TICKS = 180;
    private static final int SPRINT_COOLDOWN_TICKS = 300;
    private static final int STAMINA_REGEN_TICKS_PER_POINT = 2;
    private static final int AXE_TABLE_COOLDOWN_TICKS = 120;
    private static final int AXE_DOOR_COOLDOWN_TICKS = 600;

    GamePanel gp;
    KeyHandler keyH;
    Sound walkingSound;
    private int choice;
    private int interactCooldown = 0;
    private int staminaTicks = MAX_STAMINA_TICKS;
    private int exhaustedTicksRemaining = 0;
    private int sprintCooldownTicksRemaining = 0;
    private int staminaRegenCounter = 0;
    private int controlsInvertedTicks = 0;

    public final int screenX;
    public final int screenY;
    public int hasKey = 0;
    public boolean hasReplacementSwitch = false;
    public boolean hasAxe = false;
    private int axeTableCooldownTicks = 0;
    private int axeDoorCooldownTicks = 0;


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
        speed = gp != null ? gp.devSettings.getPlayerBaseSpeed() : DEFAULT_WALK_SPEED;
        direction = "down";
        staminaTicks = MAX_STAMINA_TICKS;
        exhaustedTicksRemaining = 0;
        sprintCooldownTicksRemaining = 0;
        staminaRegenCounter = 0;
        hasAxe = false;
        axeTableCooldownTicks = 0;
        axeDoorCooldownTicks = 0;

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
        if (axeTableCooldownTicks > 0) {
            axeTableCooldownTicks--;
        }
        if (axeDoorCooldownTicks > 0) {
            axeDoorCooldownTicks--;
        }
        if (controlsInvertedTicks > 0) {
            controlsInvertedTicks--;
        }

        boolean moving = keyH.isUpPressed || keyH.isDownPressed || keyH.isLeftPressed || keyH.isRightPressed;
        updateSprintState(moving);

        if(moving){

            boolean upPressed = controlsInvertedTicks > 0 ? keyH.isDownPressed : keyH.isUpPressed;
            boolean downPressed = controlsInvertedTicks > 0 ? keyH.isUpPressed : keyH.isDownPressed;
            boolean leftPressed = controlsInvertedTicks > 0 ? keyH.isRightPressed : keyH.isLeftPressed;
            boolean rightPressed = controlsInvertedTicks > 0 ? keyH.isLeftPressed : keyH.isRightPressed;

            if(upPressed == true){
                direction = "up";
            } else if (downPressed == true) {
                direction = "down";
            } else if (leftPressed == true) {
                direction = "left";
            } else if (rightPressed == true) {
                direction = "right";
            }

            // CHECK TILE COLLISION
            collisionOn = false;
            if (!gp.devSettings.isPhaseThroughWallsEnabled()) {
                gp.cChecker.checkTile(this);
            }

            //CHECK OBJ COLLISION
            if (!gp.devSettings.isPhaseThroughWallsEnabled()) {
                int objIndex = gp.cChecker.checkObject(this, true);
                pickUpObject(objIndex);
            }

            //CHECK MONSTER COLLISION
            if (!gp.devSettings.isPhaseThroughWallsEnabled()) {
                int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
                contactMonster(monsterIndex);
            }

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
                            if (isStage("/maps/stage01.txt")) {
                                gp.startStage1DoorRevealCutscene();
                            } else {
                                gp.ui.showMessage(getSwitchFixedDialogue());
                            }
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
                        objects.OBJ_DoorStage2 door = (objects.OBJ_DoorStage2) gp.obj[i];
                        if (door.isOpenable()) {
                            gp.playSE(3);
                            gp.ui.showMessage(getDoorOpenedDialogue());
                            gp.startMapTransition("/maps/stage03.txt");
                        } else if (!hasAxe) {
                            gp.ui.showMessage("The door is barred shut.\n\nI need something strong enough to break the planks.");
                        } else if (axeDoorCooldownTicks > 0) {
                            gp.ui.showMessage("I need to catch my breath before swinging the axe again.");
                        } else {
                            door.hitWithAxe();
                            axeDoorCooldownTicks = AXE_DOOR_COOLDOWN_TICKS;
                            if (door.isOpenable()) {
                                gp.ui.showMessage("The last plank breaks loose.\n\nThe door can open now.");
                            } else {
                                gp.ui.showMessage("You strike the planks with the axe.\n\nMore of the door is exposed.");
                            }
                        }
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "DoorStage3":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        if (!gp.isCandlePuzzleSolved()) {
                            gp.ui.showMessage("The door is open.\n\nBut I can't leave yet.\n\nSomething in this room still feels unfinished.");
                        } else if (!gp.isStage3ChaseStarted()) {
                            gp.ui.showMessage("The exit is there.\n\nOnce this room breaks loose, I need to run for it.");
                        } else {
                            gp.playSE(3);
                            gp.completeStage3Escape();
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

                case "Axe":
                    gp.playSE(3);
                    hasAxe = true;
                    gp.obj[i] = null;
                    gp.ui.showMessage("You picked up an axe.\n\nThis can break damaged tables and wooden planks.");
                    break;

                case "BreakableTable":
                case "CutsceneBreakable":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        if (!hasAxe) {
                            gp.ui.showMessage("This table is cracked, but I cannot break it by hand.");
                        } else if (axeTableCooldownTicks > 0) {
                            gp.ui.showMessage("I need a moment before swinging the axe again.");
                        } else {
                            gp.playSE(3);
                            int breakableRow = gp.obj[i].stageY / gp.tileSize;
                            gp.obj[i] = null;
                            axeTableCooldownTicks = AXE_TABLE_COOLDOWN_TICKS;
                            gp.ui.showMessage("You break the table apart with the axe.");
                            if (isStage("/maps/stage02.txt") && breakableRow == 16) {
                                gp.triggerStage2BossIntroFromBreakable();
                            }
                        }
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "HealthBag":
                    if (life < maxLife) {
                        gp.playSE(3);
                        life = maxLife;
                        gp.obj[i] = null;
                        gp.ui.showMessage("You used the health bag.\n\nHealth restored.");
                    } else if (keyH.interactPressed && interactCooldown == 0) {
                        gp.ui.showMessage("There is medicine inside.\n\nI should save it until I need it.");
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "Candle":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        gp.handleCandleInteraction((objects.OBJ_Candle) gp.obj[i]);
                        keyH.interactPressed = false;
                        interactCooldown = 30;
                    }
                    break;

                case "JhonPorkJerkyJake":
                    if (keyH.interactPressed && interactCooldown == 0) {
                        NPC_JhonPorkJerkyJake npc = (NPC_JhonPorkJerkyJake) gp.obj[i];
                        if (gp.isCandlePuzzleSolved()) {
                            gp.ui.showMessage("Jhon Pork Tocino: Thank you...\n\n"
                                    + "The light is back.\n\n"
                                    + "Jerky Jake and I can finally move on.");
                            npc.startFade();
                        } else {
                            gp.ui.showMessage("Jhon Pork Tocino: Please, help us bring the light back.\n\n"
                                    + "Jerky Jake is trapped with me, and the phantoms are getting closer.\n\n"
                                    + "Find the candle puzzle and light it correctly.");
                        }
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

    private boolean mapExists(String mapPath) {
        return getClass().getResourceAsStream(mapPath) != null;
    }

    private String getSwitchAlreadyOnDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "The repaired switch is humming steadily.\n\nLeave it alone. This room needs every bit of light.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "The switch is already holding the lights together.\n\nDo not touch it again.";
        }
        return "The lights are finally on.\n\nI should leave the switch alone.";
    }

    private String getBrokenSwitchDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "The switch panel is cracked open.\n\nSomething is missing from inside it.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "The switch is dead.\n\nIt needs a replacement part before it can work.";
        }
        return "The switch is broken.\n\nSomething inside it is missing.\n\nMaybe the replacement part is still somewhere in this room.";
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
        return "You searched the drawer.\n\nNothing useful.\n\nKeep checking the orange tables.";
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
            return "The door is already open.\n\nJust keep moving.";
        }
        return "You used 1 key.\n\nThe door opened!";
    }

    private String getLockedDoorDialogue() {
        if (isStage("/maps/stage02.txt")) {
            return "The door will not move.\n\nThere has to be a key somewhere in this room.";
        }
        if (isStage("/maps/stage03.txt")) {
            return "The exit is right there.\n\nI just need to survive long enough to reach it.";
        }
        return "Locked.\n\nIf Jhon Pork was right, one of the orange drawer tables might have the key.";
    }

    private String getTablePaperDialogue() {
        if(isStage("/maps/stage01.txt")){
            return "Name: Jhon Pork Tocino\n\n"
                    + "Most of the pages are smeared and torn.\n\n"
                    + "One passage is still readable:\n\n"
                    + "\"If you're reading this, you're probably trapped here too.\"\n\n"
                    + "\"I was a student, just like you. I pushed myself too far, fell asleep in class...\"\n\n"
                    + "\"When I woke up, this place was all that was left.\"\n\n"
                    + "\"There are shadow things wandering around here. If you see them, stay away.\"\n\n"
                    + "\"Search the tables with drawers. People leave things behind. Sometimes useful things.\"\n\n"
                    + "\"I was trying to fix the light switch, but I lost my bag before I could finish.\"\n\n"
                    + "\"If you find it, maybe you can get the lights back on.\"\n\n"
                    + "The final lines trail off into shaky scratches:\n\n"
                    + "\"The lights don't keep them out for long...\"\n\n"
                    + "\"If they get close, run.\"";
        }

        if (isStage("/maps/stage02.txt")) {
            return "insert text here kyle";
        }
        if (isStage("/maps/stage03.txt")) {
            return "The page is water-damaged, but a sketch is still visible.\n\n"
                    + "Four candle marks are connected in a jagged stroke:\n\n"
                    + "upper left -> upper right -> lower left -> lower right\n\n"
                    + "Someone wrote beneath it:\n\n"
                    + "\"Follow the shape. Do not second-guess it.\"\n\n"
                    + "The last line is scratched in hard enough to tear the page:\n\n"
                    + "\"When the room answers back, run.\"";
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
        return "You searched the bag.\n\nInside is a replacement switch.\n\nThis must be what was missing.";
    }

    public void contactMonster(int i){
        if(i != 999){
            if (gp.monster[i] instanceof MON_MinionWitherSlime) {
                ((MON_MinionWitherSlime) gp.monster[i]).hitPlayer();
                return;
            }
            if (gp.monster[i] != null && gp.monster[i].type != 1) {
                return;
            }
            if (gp.devSettings.isUnlimitedHealthEnabled()) {
                life = maxLife;
                return;
            }
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

    private void updateSprintState(boolean moving) {
        float baseWalkSpeed = gp.devSettings.getPlayerBaseSpeed();
        float sprintSpeed = baseWalkSpeed * SPRINT_SPEED_MULTIPLIER;

        if (exhaustedTicksRemaining > 0) {
            exhaustedTicksRemaining--;
            speed = EXHAUSTED_SPEED;
            return;
        }

        if (sprintCooldownTicksRemaining > 0) {
            sprintCooldownTicksRemaining--;
            speed = baseWalkSpeed;
            if (sprintCooldownTicksRemaining == 0) {
                staminaTicks = MAX_STAMINA_TICKS;
                staminaRegenCounter = 0;
            }
            return;
        }

        boolean canSprint = moving && keyH.sprintPressed && staminaTicks > 0;
        if (canSprint) {
            speed = sprintSpeed;
            staminaTicks--;
            staminaRegenCounter = 0;

            if (staminaTicks <= 0) {
                staminaTicks = 0;
                exhaustedTicksRemaining = EXHAUSTED_TICKS;
                sprintCooldownTicksRemaining = SPRINT_COOLDOWN_TICKS;
                speed = EXHAUSTED_SPEED;
            }
            return;
        }

        speed = baseWalkSpeed;
        if (staminaTicks < MAX_STAMINA_TICKS) {
            staminaRegenCounter++;
            if (staminaRegenCounter >= STAMINA_REGEN_TICKS_PER_POINT) {
                staminaTicks++;
                staminaRegenCounter = 0;
            }
        } else {
            staminaRegenCounter = 0;
        }
    }

    public float getStaminaPercent() {
        return (float) staminaTicks / MAX_STAMINA_TICKS;
    }

    public boolean isExhausted() {
        return exhaustedTicksRemaining > 0;
    }

    public boolean isSprintOnCooldown() {
        return sprintCooldownTicksRemaining > 0;
    }

    public int getCooldownSecondsRemaining() {
        return (int) Math.ceil(sprintCooldownTicksRemaining / 60.0);
    }

    public boolean hasAxeCooldownActive() {
        return axeTableCooldownTicks > 0 || axeDoorCooldownTicks > 0;
    }

    public int getAxeTableCooldownSecondsRemaining() {
        return (int) Math.ceil(axeTableCooldownTicks / 60.0);
    }

    public int getAxeDoorCooldownSecondsRemaining() {
        return (int) Math.ceil(axeDoorCooldownTicks / 60.0);
    }

    public int getActiveAxeCooldownSecondsRemaining() {
        return Math.max(getAxeDoorCooldownSecondsRemaining(), getAxeTableCooldownSecondsRemaining());
    }

    public float getAxeCooldownReadyPercent() {
        if (axeDoorCooldownTicks > 0) {
            return 1f - ((float) axeDoorCooldownTicks / AXE_DOOR_COOLDOWN_TICKS);
        }
        if (axeTableCooldownTicks > 0) {
            return 1f - ((float) axeTableCooldownTicks / AXE_TABLE_COOLDOWN_TICKS);
        }
        return 1f;
    }

    public boolean isAxeDoorCooldownActive() {
        return axeDoorCooldownTicks > 0;
    }

    public void setControlsInvertedTicks(int ticks) {
        controlsInvertedTicks = Math.max(controlsInvertedTicks, ticks);
    }

    public void draw(Graphics2D g2) {
//        g2.setColor(Color.RED);
//        g2.fillOval(x, y, gp.tileSize, gp.tileSize);

        int drawX = stageX - gp.getCameraStageX() + screenX;
        int drawY = stageY - gp.getCameraStageY() + screenY;

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
                g2.drawImage(image, drawX, drawY, 25 * 2, 40 * 2, null);
            break;

            case 2:
                g2.drawImage(image, drawX, drawY, 29*2, 32*2, null);
            break;

            case 3:
                g2.drawImage(image, drawX, drawY, 25*2, 41*2, null);
            break;

            case 4:
                g2.drawImage(image, drawX, drawY, 31*2, 37*2, null);
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

