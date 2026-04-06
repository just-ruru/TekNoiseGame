package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {

        int entityLeftStageX = entity.stageX + entity.solidArea.x;
        int entityRightStageX = entity.stageX + entity.solidArea.x + entity.solidArea.width;
        int entityTopStageY = entity.stageY + entity.solidArea.y;
        int entityBottomStageY = entity.stageY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftStageX / gp.tileSize;
        int entityRightCol = entityRightStageX / gp.tileSize;
        int entityTopRow = entityTopStageY / gp.tileSize;
        int entityBottomRow = entityBottomStageY / gp.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopStageY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }

                break;

            case "down":
                entityBottomRow = (entityBottomStageY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }

                break;

            case "left":
                entityLeftCol = (entityLeftStageX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }

                break;

            case "right":
                entityRightCol = (entityRightStageX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }

                break;
        }
    }

    public int checkObject(Entity entity, boolean player) {
        //check if player is hitting any index, if hitting an object return the index of the object
        int index = 999;
        for (int i = 0; i < gp.obj.length; i++) {

            if (gp.obj[i] != null) {
//                System.out.println();
                //get entity solid area position
                entity.solidArea.x = entity.stageX + entity.solidArea.x;
                entity.solidArea.y = entity.stageY + entity.solidArea.y;
                //get the object's solid area position
                gp.obj[i].solidArea.x = gp.obj[i].stageX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].stageY + gp.obj[i].solidArea.y;

                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if(gp.obj[i].collision == true){
                                entity.collisionOn = true;
                            }

                            if(player == true){
                                index = i;
                            }
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if(gp.obj[i].collision == true){
                                entity.collisionOn = true;
                            }

                            if(player == true){
                                index = i;
                            }
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if(gp.obj[i].collision == true){
                                entity.collisionOn = true;
                            }

                            if(player == true){
                                index = i;
                            }
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if(gp.obj[i].collision == true){
                                entity.collisionOn = true;
                            }

                            if(player == true){
                                index = i;
                            }
                        }
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    }
}

