package main;

import object.OBJ_Bag;

public class AssetSetter {
     GamePanel gp;
     public AssetSetter(GamePanel gp){
         this.gp = gp;
     }

     public void setObject(){
         gp.obj[0] = new OBJ_Bag();
         gp.obj[0].worldX = 100;
         gp.obj[0].worldY = 100;

     }
}
