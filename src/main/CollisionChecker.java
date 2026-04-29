package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {

        if (entity.direction == null) {
            return; // No movement direction; nothing to check
        }

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
                if (isOutOfMap(entityLeftCol, entityTopRow) || isOutOfMap(entityRightCol, entityTopRow)) {
                    entity.collisionOn = true;
                    break;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;

            case "down":
                entityBottomRow = (entityBottomStageY + entity.speed) / gp.tileSize;
                if (isOutOfMap(entityLeftCol, entityBottomRow) || isOutOfMap(entityRightCol, entityBottomRow)) {
                    entity.collisionOn = true;
                    break;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;

            case "left":
                entityLeftCol = (entityLeftStageX - entity.speed) / gp.tileSize;
                if (isOutOfMap(entityLeftCol, entityTopRow) || isOutOfMap(entityLeftCol, entityBottomRow)) {
                    entity.collisionOn = true;
                    break;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;

            case "right":
                entityRightCol = (entityRightStageX + entity.speed) / gp.tileSize;
                if (isOutOfMap(entityRightCol, entityTopRow) || isOutOfMap(entityRightCol, entityBottomRow)) {
                    entity.collisionOn = true;
                    break;
                }
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    // Returns true if the given (col,row) is outside the world bounds.
    private boolean isOutOfMap(int col, int row) {
        return col < 0 || row < 0 || col >= gp.maxStageCol || row >= gp.maxStageRow;
    }

    public int checkObject(Entity entity, boolean player) {
        //check if player is hitting any index, if hitting an object return the index of the object
        int index = 999;
        for (int i = 0; i < gp.obj.length; i++) {

            if (gp.obj[i] != null) {
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

    public boolean checkPlayer(Entity entity) {

        boolean contactPlayer = false;

        //get entity solid area position
        entity.solidArea.x = entity.stageX + entity.solidArea.x;
        entity.solidArea.y = entity.stageY + entity.solidArea.y;

        gp.player.solidArea.x = gp.player.stageX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.stageY + gp.player.solidArea.y;

        switch(entity.direction) {
            case "up": entity.solidArea.y -= entity.speed; break;
            case "down": entity.solidArea.y += entity.speed; break;
            case "left": entity.solidArea.x -= entity.speed; break;
            case "right": entity.solidArea.x += entity.speed; break;
        }

        if(entity.solidArea.intersects(gp.player.solidArea)) {
            entity.collisionOn = true;
            contactPlayer = true;
        }

        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;

        return contactPlayer;
    }

    // Check for objects the entity is currently overlapping, without "looking ahead"
    // in the movement direction. Useful for interaction while standing still.
    public int checkObjectAtCurrentPosition(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] != null) {
                // world → solid area positions
                entity.solidArea.x = entity.stageX + entity.solidArea.x;
                entity.solidArea.y = entity.stageY + entity.solidArea.y;

                gp.obj[i].solidArea.x = gp.obj[i].stageX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].stageY + gp.obj[i].solidArea.y;

                if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                    if (gp.obj[i].collision) {
                        entity.collisionOn = true;
                    }
                    if (player) {
                        index = i;
                    }
                }

                // reset to defaults
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }

        return index;
    }

    // Check for objects in front of the entity (interaction range), based on direction.
    public int checkObjectInFront(Entity entity, boolean player, int distance) {
        int index = 999;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] != null) {
                // world → solid area positions
                entity.solidArea.x = entity.stageX + entity.solidArea.x;
                entity.solidArea.y = entity.stageY + entity.solidArea.y;

                gp.obj[i].solidArea.x = gp.obj[i].stageX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].stageY + gp.obj[i].solidArea.y;

                // offset the entity's area forward to form an interaction box
                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= distance;
                        break;
                    case "down":
                        entity.solidArea.y += distance;
                        break;
                    case "left":
                        entity.solidArea.x -= distance;
                        break;
                    case "right":
                        entity.solidArea.x += distance;
                        break;
                }

                if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                    if (player) {
                        index = i;
                    }
                }

                // reset to defaults
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }

        return index;
    }

    public int checkEntity(Entity entity, Entity[] target) {
        int index = 999;

        for (int i = 0; i < target.length; i++) {
            if (target[i] != null) {
                // world → solid area positions
                entity.solidArea.x = entity.stageX + entity.solidArea.x;
                entity.solidArea.y = entity.stageY + entity.solidArea.y;

                target[i].solidArea.x = target[i].stageX + target[i].solidArea.x;
                target[i].solidArea.y = target[i].stageY + target[i].solidArea.y;

                // offset the entity's area forward to form an interaction box
                switch (entity.direction) {
                    case "up": entity.solidArea.y -= entity.speed; break;
                    case "down": entity.solidArea.y += entity.speed; break;
                    case "left": entity.solidArea.x -= entity.speed; break;
                    case "right": entity.solidArea.x += entity.speed; break;
                }

                if(entity.solidArea.intersects(target[i].solidArea)) {
                    if(target[i] != entity) {
                        entity.collisionOn = true;
                        index = i;
                    }
                }

                // reset to defaults
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[i].solidArea.x = target[i].solidAreaDefaultX;
                target[i].solidArea.y = target[i].solidAreaDefaultY;
            }
        }

        return index;
    }
}

