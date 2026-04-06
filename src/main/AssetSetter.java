package main;

import objects.OBJ_Bag;
import objects.OBJ_Key;
import objects.OBJ_LightSwitch;
import objects.OBJ_TablePaper;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setObject(){
        gp.obj[0] = new OBJ_TablePaper();
        gp.obj[0].stageX = 7 * gp.tileSize;
        gp.obj[0].stageY = 3 * gp.tileSize;

        gp.obj[1] = new OBJ_LightSwitch();
        gp.obj[1].stageX = 1 * gp.tileSize;
        gp.obj[1].stageY = 0 * gp.tileSize;

        //Bag 18x 1y
        gp.obj[2] = new OBJ_Bag();
        gp.obj[2].stageX = 18 * gp.tileSize;
        gp.obj[2].stageY = 1 * gp.tileSize;

        gp.obj[3] = new OBJ_Key();
        gp.obj[3].stageX = 42 * gp.tileSize;
        gp.obj[3].stageY = 12 * gp.tileSize;

//        gp.obj[3] = new OBJ_Door();
//        gp.obj[3].stageX = 18 * gp.tileSize;
//        gp.obj[3].stageY = 1 * gp.tileSize;


    }
}
