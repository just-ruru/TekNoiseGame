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
        int collisionStep = entity.getCollisionStep();

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopStageY - collisionStep) / gp.tileSize;
                if (isOutOfMap(entityLeftCol, entityTopRow) || isOutOfMap(entityRightCol, entityTopRow)) {
                    entity.collisionOn = true;
                    break;
                }
                // Additional bounds check before array access
                if (entityLeftCol >= 0 && entityLeftCol < gp.maxStageCol && 
                    entityTopRow >= 0 && entityTopRow < gp.maxStageRow) {
                    tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                } else {
                    tileNum1 = 0; // Default to non-collision tile
                }
                if (entityRightCol >= 0 && entityRightCol < gp.maxStageCol && 
                    entityTopRow >= 0 && entityTopRow < gp.maxStageRow) {
                    tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                } else {
                    tileNum2 = 0; // Default to non-collision tile
                }
                checkTileCollision(entity, tileNum1, tileNum2);
                break;

            case "down":
                entityBottomRow = (entityBottomStageY + collisionStep) / gp.tileSize;
                if (isOutOfMap(entityLeftCol, entityBottomRow) || isOutOfMap(entityRightCol, entityBottomRow)) {
                    entity.collisionOn = true;
                    break;
                }
                // Additional bounds check before array access
                if (entityLeftCol >= 0 && entityLeftCol < gp.maxStageCol && 
                    entityBottomRow >= 0 && entityBottomRow < gp.maxStageRow) {
                    tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                } else {
                    tileNum1 = 0; // Default to non-collision tile
                }
                if (entityRightCol >= 0 && entityRightCol < gp.maxStageCol && 
                    entityBottomRow >= 0 && entityBottomRow < gp.maxStageRow) {
                    tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                } else {
                    tileNum2 = 0; // Default to non-collision tile
                }
                checkTileCollision(entity, tileNum1, tileNum2);
                break;

            case "left":
                entityLeftCol = (entityLeftStageX - collisionStep) / gp.tileSize;
                if (isOutOfMap(entityLeftCol, entityTopRow) || isOutOfMap(entityLeftCol, entityBottomRow)) {
                    entity.collisionOn = true;
                    break;
                }
                // Additional bounds check before array access
                if (entityLeftCol >= 0 && entityLeftCol < gp.maxStageCol && 
                    entityTopRow >= 0 && entityTopRow < gp.maxStageRow) {
                    tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                } else {
                    tileNum1 = 0; // Default to non-collision tile
                }
                if (entityLeftCol >= 0 && entityLeftCol < gp.maxStageCol && 
                    entityBottomRow >= 0 && entityBottomRow < gp.maxStageRow) {
                    tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                } else {
                    tileNum2 = 0; // Default to non-collision tile
                }
                checkTileCollision(entity, tileNum1, tileNum2);
                break;

            case "right":
                entityRightCol = (entityRightStageX + collisionStep) / gp.tileSize;
                if (isOutOfMap(entityRightCol, entityTopRow) || isOutOfMap(entityRightCol, entityBottomRow)) {
                    entity.collisionOn = true;
                    break;
                }
                // Additional bounds check before array access
                if (entityRightCol >= 0 && entityRightCol < gp.maxStageCol && 
                    entityTopRow >= 0 && entityTopRow < gp.maxStageRow) {
                    tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                } else {
                    tileNum1 = 0; // Default to non-collision tile
                }
                if (entityRightCol >= 0 && entityRightCol < gp.maxStageCol && 
                    entityBottomRow >= 0 && entityBottomRow < gp.maxStageRow) {
                    tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                } else {
                    tileNum2 = 0; // Default to non-collision tile
                }
                checkTileCollision(entity, tileNum1, tileNum2);
                break;
        }
    }

    private void checkTileCollision(Entity entity, int tileNum1, int tileNum2) {
        // During stage 3 chase, only block tiles with value 1 (walls) for enemies, not player
        if (gp.isStage3ChaseStarted() && entity != gp.player) {
            if (tileNum1 == 1 || tileNum2 == 1
                    || tileNum1 == tile.TileManager.STAGE3_BARRIER_TILE_ID
                    || tileNum2 == tile.TileManager.STAGE3_BARRIER_TILE_ID) {
                entity.collisionOn = true;
            }
        } else {
            if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                entity.collisionOn = true;
            }
        }
    }

    private boolean isOutOfMap(int col, int row) {
        return col < 0 || row < 0 || col >= gp.maxStageCol || row >= gp.maxStageRow;
    }

    public int checkObject(Entity entity, boolean player) {
        int index = 999;

        int offsetX = 0;
        int offsetY = 0;
        int collisionStep = entity.getCollisionStep();
        switch (entity.direction) {
            case "up": offsetY = -collisionStep; break;
            case "down": offsetY = collisionStep; break;
            case "left": offsetX = -collisionStep; break;
            case "right": offsetX = collisionStep; break;
        }

        int entityX = entity.stageX + entity.solidAreaDefaultX + offsetX;
        int entityY = entity.stageY + entity.solidAreaDefaultY + offsetY;
        int entityW = entity.solidArea.width;
        int entityH = entity.solidArea.height;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) {
                continue;
            }

            int objX = gp.obj[i].stageX + gp.obj[i].solidAreaDefaultX;
            int objY = gp.obj[i].stageY + gp.obj[i].solidAreaDefaultY;
            int objW = gp.obj[i].solidArea.width;
            int objH = gp.obj[i].solidArea.height;

            if (intersectsAabb(entityX, entityY, entityW, entityH, objX, objY, objW, objH)) {
                if (gp.obj[i].collision) {
                    entity.collisionOn = true;
                }
                if (player) {
                    index = i;
                }
            }
        }
        return index;
    }

    public boolean checkPlayer(Entity entity) {
        boolean contactPlayer = false;

        int offsetX = 0;
        int offsetY = 0;
        int collisionStep = entity.getCollisionStep();
        switch (entity.direction) {
            case "up": offsetY = -collisionStep; break;
            case "down": offsetY = collisionStep; break;
            case "left": offsetX = -collisionStep; break;
            case "right": offsetX = collisionStep; break;
        }

        int entityX = entity.stageX + entity.solidAreaDefaultX + offsetX;
        int entityY = entity.stageY + entity.solidAreaDefaultY + offsetY;
        int entityW = entity.solidArea.width;
        int entityH = entity.solidArea.height;
        int playerX = gp.player.stageX + gp.player.solidAreaDefaultX;
        int playerY = gp.player.stageY + gp.player.solidAreaDefaultY;
        int playerW = gp.player.solidArea.width;
        int playerH = gp.player.solidArea.height;

        if (intersectsAabb(entityX, entityY, entityW, entityH, playerX, playerY, playerW, playerH)) {
            if (!gp.player.invincible) {
                entity.collisionOn = true;
            }
            contactPlayer = true;
        }

        return contactPlayer;
    }

    public int checkObjectAtCurrentPosition(Entity entity, boolean player) {
        int index = 999;
        int entityX = entity.stageX + entity.solidAreaDefaultX;
        int entityY = entity.stageY + entity.solidAreaDefaultY;
        int entityW = entity.solidArea.width;
        int entityH = entity.solidArea.height;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) {
                continue;
            }

            int objX = gp.obj[i].stageX + gp.obj[i].solidAreaDefaultX;
            int objY = gp.obj[i].stageY + gp.obj[i].solidAreaDefaultY;
            int objW = gp.obj[i].solidArea.width;
            int objH = gp.obj[i].solidArea.height;

            if (intersectsAabb(entityX, entityY, entityW, entityH, objX, objY, objW, objH)) {
                if (gp.obj[i].collision) {
                    entity.collisionOn = true;
                }
                if (player) {
                    index = i;
                }
            }
        }

        return index;
    }

    public int checkObjectInFront(Entity entity, boolean player, int distance) {
        int index = 999;
        int offsetX = 0;
        int offsetY = 0;
        switch (entity.direction) {
            case "up": offsetY = -distance; break;
            case "down": offsetY = distance; break;
            case "left": offsetX = -distance; break;
            case "right": offsetX = distance; break;
        }

        int entityX = entity.stageX + entity.solidAreaDefaultX + offsetX;
        int entityY = entity.stageY + entity.solidAreaDefaultY + offsetY;
        int entityW = entity.solidArea.width;
        int entityH = entity.solidArea.height;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) {
                continue;
            }

            int objX = gp.obj[i].stageX + gp.obj[i].solidAreaDefaultX;
            int objY = gp.obj[i].stageY + gp.obj[i].solidAreaDefaultY;
            int objW = gp.obj[i].solidArea.width;
            int objH = gp.obj[i].solidArea.height;

            if (intersectsAabb(entityX, entityY, entityW, entityH, objX, objY, objW, objH) && player) {
                index = i;
            }
        }

        return index;
    }

    public int checkEntity(Entity entity, Entity[] target) {
        int index = 999;
        int offsetX = 0;
        int offsetY = 0;
        int collisionStep = entity.getCollisionStep();
        switch (entity.direction) {
            case "up": offsetY = -collisionStep; break;
            case "down": offsetY = collisionStep; break;
            case "left": offsetX = -collisionStep; break;
            case "right": offsetX = collisionStep; break;
        }

        int entityX = entity.stageX + entity.solidAreaDefaultX + offsetX;
        int entityY = entity.stageY + entity.solidAreaDefaultY + offsetY;
        int entityW = entity.solidArea.width;
        int entityH = entity.solidArea.height;

        for (int i = 0; i < target.length; i++) {
            if (target[i] == null || target[i] == entity) {
                continue;
            }

            int targetX = target[i].stageX + target[i].solidAreaDefaultX;
            int targetY = target[i].stageY + target[i].solidAreaDefaultY;
            int targetW = target[i].solidArea.width;
            int targetH = target[i].solidArea.height;

            if (intersectsAabb(entityX, entityY, entityW, entityH, targetX, targetY, targetW, targetH)) {
                if (!(entity == gp.player && entity.invincible)) {
                    entity.collisionOn = true;
                }
                index = i;
            }
        }

        return index;
    }

    private boolean intersectsAabb(int ax, int ay, int aw, int ah, int bx, int by, int bw, int bh) {
        return ax < bx + bw
                && ax + aw > bx
                && ay < by + bh
                && ay + ah > by;
    }
}
