package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.PriorityQueue;

public class Entity {
    public GamePanel gp;
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
    public boolean removeFromWorld = false;
    private String avoidedDirection = null;
    private int avoidedDirectionTicks = 0;
    private static final int AVOID_BLOCKED_DIRECTION_TICKS = 24;

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

        updateBlockedDirectionMemory();
        setAction();

        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkEntity(this, gp.monster);
        boolean contactPlayer = gp.cChecker.checkPlayer(this);

        if(this.type == 1 && contactPlayer == true) {
            if (gp.devSettings.isUnlimitedHealthEnabled()) {
                gp.player.life = gp.player.maxLife;
            } else if(gp.player.invincible == false) {
                gp.playSE(7);
                //we can give damage
                gp.player.life -= 1;
                gp.player.invincible = true;
            }
        }
        if(collisionOn == false) {
            moveInCurrentDirection();
        } else if (type != 0) {
            rememberBlockedDirection(direction);
            tryAlternateEnemyMovement();
        }

        updateAnimationOnly();
    }

    protected void updateBlockedDirectionMemory() {
        if (avoidedDirectionTicks > 0) {
            avoidedDirectionTicks--;
            if (avoidedDirectionTicks == 0) {
                avoidedDirection = null;
            }
        }
    }

    protected void rememberBlockedDirection(String blockedDirection) {
        avoidedDirection = blockedDirection;
        avoidedDirectionTicks = AVOID_BLOCKED_DIRECTION_TICKS;
    }

    public void updateAnimationOnly() {
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

    protected void moveInCurrentDirection() {
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

    protected boolean tryAlternateEnemyMovement() {
        String originalDirection = direction;
        String[] fallbackDirections = getSortedFallbackDirections(originalDirection);

        for (String fallbackDirection : fallbackDirections) {
            direction = fallbackDirection;
            if (canMoveInDirection(fallbackDirection)) {
                moveInCurrentDirection();
                return true;
            }
        }

        direction = originalDirection;
        collisionOn = true;
        return false;
    }

    private boolean canMoveInDirection(String testDirection) {
        String originalDirection = direction;
        direction = testDirection;
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkEntity(this, gp.monster);
        boolean canMove = !collisionOn;
        direction = originalDirection;
        collisionOn = false;
        return canMove;
    }

    private String[] getSortedFallbackDirections(String blockedDirection) {
        String[] fallbackDirections = {"up", "down", "left", "right"};

        for (int i = 0; i < fallbackDirections.length - 1; i++) {
            for (int j = i + 1; j < fallbackDirections.length; j++) {
                if (getFallbackScore(fallbackDirections[j], blockedDirection)
                        < getFallbackScore(fallbackDirections[i], blockedDirection)) {
                    String temp = fallbackDirections[i];
                    fallbackDirections[i] = fallbackDirections[j];
                    fallbackDirections[j] = temp;
                }
            }
        }

        return fallbackDirections;
    }

    private int getFallbackScore(String testDirection, String blockedDirection) {
        int nextCenterX = getCenterStageX();
        int nextCenterY = getCenterStageY();
        int step = gp.tileSize;

        switch (testDirection) {
            case "up": nextCenterY -= step; break;
            case "down": nextCenterY += step; break;
            case "left": nextCenterX -= step; break;
            case "right": nextCenterX += step; break;
        }

        int playerCenterX = gp.player.stageX + gp.player.solidArea.x + gp.player.solidArea.width / 2;
        int playerCenterY = gp.player.stageY + gp.player.solidArea.y + gp.player.solidArea.height / 2;
        int dx = playerCenterX - nextCenterX;
        int dy = playerCenterY - nextCenterY;
        int score = (dx * dx) + (dy * dy);

        if (testDirection.equals(blockedDirection)) {
            score += gp.tileSize * gp.tileSize * 8;
        } else if (testDirection.equals(avoidedDirection) && avoidedDirectionTicks > 0) {
            score += gp.tileSize * gp.tileSize * 4;
        } else if (testDirection.equals(getOppositeDirection(blockedDirection))) {
            score += gp.tileSize * gp.tileSize;
        }

        return score;
    }

    private String getOppositeDirection(String currentDirection) {
        switch (currentDirection) {
            case "up": return "down";
            case "down": return "up";
            case "left": return "right";
            case "right": return "left";
            default: return "down";
        }
    }

    protected String findPathDirectionToPlayer() {
        int targetX = gp.player.stageX + gp.player.solidArea.x + gp.player.solidArea.width / 2;
        int targetY = gp.player.stageY + gp.player.solidArea.y + gp.player.solidArea.height / 2;
        return findPathDirectionToStagePoint(targetX, targetY);
    }

    protected String findPathDirectionToStagePoint(int targetStageX, int targetStageY) {
        String pathDirection = findPathDirectionToStagePoint(targetStageX, targetStageY, true);
        if (pathDirection != null) {
            return pathDirection;
        }
        return findPathDirectionToStagePoint(targetStageX, targetStageY, false);
    }

    private String findPathDirectionToStagePoint(int targetStageX, int targetStageY, boolean avoidRecentlyBlockedDirection) {
        int startCol = getCenterStageX() / gp.tileSize;
        int startRow = getCenterStageY() / gp.tileSize;
        int targetCol = targetStageX / gp.tileSize;
        int targetRow = targetStageY / gp.tileSize;

        if (startCol == targetCol && startRow == targetRow) {
            return direction;
        }

        if (gp.isPathTileBlocked(startCol, startRow)) {
            return null;
        }

        if (gp.isPathTileBlocked(targetCol, targetRow)) {
            int[] nearestTarget = findNearestOpenPathTile(targetCol, targetRow);
            if (nearestTarget == null) {
                return null;
            }
            targetCol = nearestTarget[0];
            targetRow = nearestTarget[1];
        }

        int cols = gp.maxStageCol;
        int rows = gp.maxStageRow;
        boolean[][] closed = new boolean[cols][rows];
        int[][] costSoFar = new int[cols][rows];
        String[][] firstDirection = new String[cols][rows];
        PriorityQueue<int[]> frontier = new PriorityQueue<>((a, b) -> Integer.compare(a[2], b[2]));

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                costSoFar[col][row] = Integer.MAX_VALUE;
            }
        }

        costSoFar[startCol][startRow] = 0;
        frontier.add(new int[] {startCol, startRow, heuristic(startCol, startRow, targetCol, targetRow)});

        int[] dCol = {0, 0, -1, 1};
        int[] dRow = {-1, 1, 0, 0};
        String[] directionNames = {"up", "down", "left", "right"};

        while (!frontier.isEmpty()) {
            int[] current = frontier.poll();
            int currentCol = current[0];
            int currentRow = current[1];

            if (closed[currentCol][currentRow]) {
                continue;
            }
            closed[currentCol][currentRow] = true;

            sortDirectionsByHeuristic(dCol, dRow, directionNames, currentCol, currentRow, targetCol, targetRow);

            for (int i = 0; i < directionNames.length; i++) {
                int nextCol = currentCol + dCol[i];
                int nextRow = currentRow + dRow[i];

                if (avoidRecentlyBlockedDirection
                        && avoidedDirectionTicks > 0
                        && currentCol == startCol
                        && currentRow == startRow
                        && directionNames[i].equals(avoidedDirection)) {
                    continue;
                }

                if (nextCol < 0 || nextRow < 0 || nextCol >= cols || nextRow >= rows) {
                    continue;
                }
                if (closed[nextCol][nextRow] || gp.isPathTileBlocked(nextCol, nextRow)) {
                    continue;
                }

                int newCost = costSoFar[currentCol][currentRow] + 1;
                if (newCost >= costSoFar[nextCol][nextRow]) {
                    continue;
                }

                costSoFar[nextCol][nextRow] = newCost;
                firstDirection[nextCol][nextRow] = firstDirection[currentCol][currentRow] != null
                        ? firstDirection[currentCol][currentRow]
                        : directionNames[i];

                if (nextCol == targetCol && nextRow == targetRow) {
                    return firstDirection[nextCol][nextRow];
                }

                int priority = newCost + heuristic(nextCol, nextRow, targetCol, targetRow);
                frontier.add(new int[] {nextCol, nextRow, priority});
            }
        }

        return null;
    }

    private int[] findNearestOpenPathTile(int centerCol, int centerRow) {
        int bestCol = -1;
        int bestRow = -1;
        int bestScore = Integer.MAX_VALUE;

        for (int radius = 1; radius <= 3; radius++) {
            for (int col = centerCol - radius; col <= centerCol + radius; col++) {
                for (int row = centerRow - radius; row <= centerRow + radius; row++) {
                    if (gp.isPathTileBlocked(col, row)) {
                        continue;
                    }

                    int score = heuristic(centerCol, centerRow, col, row);
                    if (score < bestScore) {
                        bestCol = col;
                        bestRow = row;
                        bestScore = score;
                    }
                }
            }

            if (bestCol != -1) {
                return new int[] {bestCol, bestRow};
            }
        }

        return null;
    }

    private void sortDirectionsByHeuristic(int[] dCol, int[] dRow, String[] directionNames,
                                           int currentCol, int currentRow, int targetCol, int targetRow) {
        for (int i = 0; i < directionNames.length - 1; i++) {
            for (int j = i + 1; j < directionNames.length; j++) {
                int scoreI = heuristic(currentCol + dCol[i], currentRow + dRow[i], targetCol, targetRow);
                int scoreJ = heuristic(currentCol + dCol[j], currentRow + dRow[j], targetCol, targetRow);
                if (scoreJ < scoreI) {
                    int tempCol = dCol[i];
                    dCol[i] = dCol[j];
                    dCol[j] = tempCol;

                    int tempRow = dRow[i];
                    dRow[i] = dRow[j];
                    dRow[j] = tempRow;

                    String tempDirection = directionNames[i];
                    directionNames[i] = directionNames[j];
                    directionNames[j] = tempDirection;
                }
            }
        }
    }

    private int heuristic(int colA, int rowA, int colB, int rowB) {
        return Math.abs(colA - colB) + Math.abs(rowA - rowB);
    }

    protected int getCenterStageX() {
        return stageX + solidArea.x + solidArea.width / 2;
    }

    protected int getCenterStageY() {
        return stageY + solidArea.y + solidArea.height / 2;
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
        int cameraStageX = gp.getCameraStageX();
        int cameraStageY = gp.getCameraStageY();
        int screenX = stageX - cameraStageX + gp.player.screenX;
        int screenY = stageY - cameraStageY + gp.player.screenY;

        // Only draw if within the on-screen area
        if (stageX + gp.tileSize > cameraStageX - gp.player.screenX &&
            stageX - gp.tileSize < cameraStageX + gp.player.screenX &&
            stageY + gp.tileSize > cameraStageY - gp.player.screenY &&
            stageY - gp.tileSize < cameraStageY + gp.player.screenY) {

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
