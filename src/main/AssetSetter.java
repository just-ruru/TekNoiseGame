package main;

import main.monster.MON_Dementor;
import objects.OBJ_Bag;
import objects.OBJ_LightSwitch;
import objects.OBJ_TablePaper;
import objects.*;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setObject(String mapPath){
        // clear previous stage objects
        for (int i = 0; i < gp.obj.length; i++) {
            gp.obj[i] = null;
        }

        if ("/maps/stage02.txt".equals(mapPath)) {
            setStage02Objects();
            return;
        }
        
        if ("/maps/stage03.txt".equals(mapPath)) {
            setStage03Objects();
            return;
        }

        // Default to stage 01 layout.
        setStage01Objects();
    }

    private void setStage01Objects() {
        gp.obj[0] = new OBJ_TablePaper();
        gp.obj[0].stageX = 7 * gp.tileSize;
        gp.obj[0].stageY = 3 * gp.tileSize;

        gp.obj[1] = new OBJ_LightSwitch();
        gp.obj[1].stageX = 1 * gp.tileSize;
        gp.obj[1].stageY = 1 * gp.tileSize;

        //Bag 18x 1y
        gp.obj[2] = new OBJ_Bag();
        gp.obj[2].stageX = 18 * gp.tileSize;
        gp.obj[2].stageY = 1 * gp.tileSize;

        // Door marker on the map uses tile ID 7 (two tiles stacked at col 41, rows 0-1).
        // Place the door object aligned to that 7-tile column.
        gp.obj[3] = new OBJ_DoorStage1();
        gp.obj[3].stageX = 41 * gp.tileSize;
        gp.obj[3].stageY = 0 * gp.tileSize;

        addStage01SearchTables();
    }

    private void addStage01SearchTables() {
        int[][] tableTiles = {
                {22, 2},
                {5, 3},
                {28, 3},
                {29, 3},
                {34, 3},
                {40, 3},
                {20, 4},
                {26, 5},
                {33, 5},
                {15, 7},
                {16, 9},
                {22, 9},
                {7, 11},
                {27, 11},
                {4, 12},
                {9, 13}
        };

        int objectIndex = 4;
        for (int i = 0; i < tableTiles.length; i++) {
            gp.obj[objectIndex] = new OBJ_Empty_Table();
            gp.obj[objectIndex].stageX = tableTiles[i][0] * gp.tileSize;
            gp.obj[objectIndex].stageY = tableTiles[i][1] * gp.tileSize;
            objectIndex++;
        }

        gp.obj[objectIndex] = new OBJ_Key_Table();
        gp.obj[objectIndex].stageX = 42 * gp.tileSize;
        gp.obj[objectIndex].stageY = 12 * gp.tileSize;
    }

    private void setStage02Objects() {
        gp.obj[0] = new OBJ_TablePaper();
        gp.obj[0].stageX = 10 * gp.tileSize;
        gp.obj[0].stageY = 5 * gp.tileSize;

        gp.obj[1] = new OBJ_LightSwitch();
        gp.obj[1].stageX = 2 * gp.tileSize;
        gp.obj[1].stageY = 1 * gp.tileSize;

        gp.obj[2] = new OBJ_Bag();
        gp.obj[2].stageX = 24 * gp.tileSize;
        gp.obj[2].stageY = 10 * gp.tileSize;

        gp.obj[3] = new OBJ_Key_Table();
        gp.obj[3].stageX = 36 * gp.tileSize;
        gp.obj[3].stageY = 8 * gp.tileSize;

        // Stage 2 can have its own exit door location.
        gp.obj[4] = new OBJ_DoorStage2();
        gp.obj[4].stageX = 41 * gp.tileSize;
        gp.obj[4].stageY = 8 * gp.tileSize;

        gp.obj[5] = new OBJ_Empty_Table();
        gp.obj[5].stageX = 12 * gp.tileSize;
        gp.obj[5].stageY = 12 * gp.tileSize;

        gp.obj[6] = new OBJ_Empty_Table();
        gp.obj[6].stageX = 29 * gp.tileSize;
        gp.obj[6].stageY = 4 * gp.tileSize;
    }
    
    private void setStage03Objects() {
        gp.obj[0] = new OBJ_TablePaper();
        gp.obj[0].stageX = 5 * gp.tileSize;
        gp.obj[0].stageY = 5 * gp.tileSize;

        gp.obj[1] = new OBJ_LightSwitch();
        gp.obj[1].stageX = 2 * gp.tileSize;
        gp.obj[1].stageY = 1 * gp.tileSize;

        gp.obj[2] = new OBJ_Bag();
        gp.obj[2].stageX = 15 * gp.tileSize;
        gp.obj[2].stageY = 10 * gp.tileSize;

        gp.obj[3] = new OBJ_Key_Table();
        gp.obj[3].stageX = 25 * gp.tileSize;
        gp.obj[3].stageY = 15 * gp.tileSize;

        // Stage 3 door to next stage
        gp.obj[4] = new OBJ_DoorStage3();
        gp.obj[4].stageX = 28 * gp.tileSize;
        gp.obj[4].stageY = 0 * gp.tileSize;

        gp.obj[5] = new OBJ_Empty_Table();
        gp.obj[5].stageX = 9 * gp.tileSize;
        gp.obj[5].stageY = 8 * gp.tileSize;

        gp.obj[6] = new OBJ_Empty_Table();
        gp.obj[6].stageX = 18 * gp.tileSize;
        gp.obj[6].stageY = 14 * gp.tileSize;
    }

//        gp.obj[3] = new OBJ_Door();
//        gp.obj[3].stageX = 18 * gp.tileSize;
//        gp.obj[3].stageY = 1 * gp.tileSize;

    public void setMonster() {
        setMonster("/maps/stage01.txt");
    }

    public void setMonster(String mapPath) {
        for (int i = 0; i < gp.monster.length; i++) {
            gp.monster[i] = null;
        }

        if ("/maps/stage02.txt".equals(mapPath)) {
            setStage02Monsters();
            return;
        }

        if ("/maps/stage03.txt".equals(mapPath)) {
            setStage03Monsters();
            return;
        }

        setStage01Monsters();
    }

    private void setStage01Monsters() {
        gp.monster[0] = new MON_Dementor(gp, 1);
        gp.monster[0].stageX = gp.tileSize * 23;
        gp.monster[0].stageY = gp.tileSize * 10;

        gp.monster[1] = new MON_Dementor(gp, 2);
        gp.monster[1].stageX = gp.tileSize * 28;
        gp.monster[1].stageY = gp.tileSize * 11;

        gp.monster[2] = new MON_Dementor(gp, 3);
        gp.monster[2].stageX = gp.tileSize * 32;
        gp.monster[2].stageY = gp.tileSize * 9;

        gp.monster[3] = new MON_Dementor(gp, 1);
        gp.monster[3].stageX = gp.tileSize * 35;
        gp.monster[3].stageY = gp.tileSize * 15;

        gp.monster[4] = new MON_Dementor(gp, 1);
        gp.monster[4].stageX = gp.tileSize * 20;
        gp.monster[4].stageY = gp.tileSize * 15;
    }

    private void setStage02Monsters() {
        gp.monster[0] = new MON_Dementor(gp, 2);
        gp.monster[0].stageX = gp.tileSize * 16;
        gp.monster[0].stageY = gp.tileSize * 8;

        gp.monster[1] = new MON_Dementor(gp, 3);
        gp.monster[1].stageX = gp.tileSize * 31;
        gp.monster[1].stageY = gp.tileSize * 12;
    }

    private void setStage03Monsters() {
        gp.monster[0] = new MON_Dementor(gp, 4);
        gp.monster[0].stageX = gp.tileSize * 12;
        gp.monster[0].stageY = gp.tileSize * 9;

        gp.monster[1] = new MON_Dementor(gp, 1);
        gp.monster[1].stageX = gp.tileSize * 22;
        gp.monster[1].stageY = gp.tileSize * 14;

        gp.monster[2] = new MON_Dementor(gp, 2);
        gp.monster[2].stageX = gp.tileSize * 26;
        gp.monster[2].stageY = gp.tileSize * 5;
    }
}

