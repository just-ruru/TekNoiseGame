package entity;

import main.GamePanel;
import java.util.Random;

/**
 * Abstract base class for all monsters in the game.
 * Provides common functionality for monster behavior, movement, and AI.
 */
public abstract class Monster extends Entity {
    
    // Common monster properties
    protected Random random = new Random();
    protected int detectionRange;
    protected float wanderSpeed;
    protected float chaseSpeed;
    
    // Original spawn position for wandering behavior
    protected int originalX = -1;
    protected int originalY = -1;
    protected int wanderRadius;
    
    // Action lock for timing-based behaviors
    protected int actionLockCounter = 0;
    protected int actionLockDuration = 120;
    
    public Monster(GamePanel gp) {
        super(gp);
        type = 1; // All monsters are type 1
    }
    
    /**
     * Initialize monster-specific properties.
     * Subclasses should call this in their constructor.
     */
    protected void initializeMonster(int detectionRange, float wanderSpeed, 
                                   float chaseSpeed, int wanderRadius) {
        this.detectionRange = detectionRange;
        this.wanderSpeed = wanderSpeed;
        this.chaseSpeed = chaseSpeed;
        this.wanderRadius = wanderRadius;
        this.speed = wanderSpeed;
    }
    
    /**
     * Store original spawn position for wandering behavior.
     */
    protected void storeOriginalPosition() {
        if (originalX == -1 && originalY == -1) {
            originalX = stageX;
            originalY = stageY;
        }
    }
    
    /**
     * Calculate distance to player in tiles.
     */
    protected int getTileDistanceToPlayer() {
        int playerX = gp.player.stageX;
        int playerY = gp.player.stageY;
        
        int distanceX = Math.abs(stageX - playerX);
        int distanceY = Math.abs(stageY - playerY);
        
        return (distanceX + distanceY) / gp.tileSize;
    }
    
    /**
     * Determine direction to move towards a target position.
     */
    protected String getDirectionToPosition(int targetX, int targetY) {
        int deltaX = targetX - stageX;
        int deltaY = targetY - stageY;
        
        // Prioritize the axis with greater distance
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            return deltaX > 0 ? "right" : "left";
        } else {
            return deltaY > 0 ? "down" : "up";
        }
    }
    
    /**
     * Get direction towards the player.
     */
    protected String getDirectionToPlayer() {
        return getDirectionToPosition(gp.player.stageX, gp.player.stageY);
    }
    
    /**
     * Get direction back to original spawn position.
     */
    protected String getDirectionToOriginalPosition() {
        return getDirectionToPosition(originalX, originalY);
    }
    
    /**
     * Check if monster is too far from original spawn position.
     */
    protected boolean isTooFarFromOriginal() {
        if (originalX == -1 || originalY == -1) return false;
        
        int distToOriginalX = Math.abs(stageX - originalX);
        int distToOriginalY = Math.abs(stageY - originalY);
        int tileDistToOriginal = (distToOriginalX + distToOriginalY) / gp.tileSize;
        
        return tileDistToOriginal > wanderRadius;
    }
    
    /**
     * Perform random wandering movement.
     */
    protected void performRandomWander() {
        actionLockCounter++;
        if (actionLockCounter >= actionLockDuration) {
            int i = random.nextInt(100) + 1;
            
            if (i <= 25) {
                direction = "up";
            } else if (i <= 50) {
                direction = "down";
            } else if (i <= 75) {
                direction = "left";
            } else {
                direction = "right";
            }
            
            actionLockCounter = 0;
        }
    }
    
    /**
     * Switch to chase mode - increase speed and set direction to player.
     */
    protected void enterChaseMode() {
        speed = chaseSpeed;
        direction = getDirectionToPlayer();
    }
    
    /**
     * Switch to wander mode - normal speed and either return to spawn or wander randomly.
     */
    protected void enterWanderMode() {
        speed = wanderSpeed;
        
        if (isTooFarFromOriginal()) {
            // Return to original position
            direction = getDirectionToOriginalPosition();
        } else {
            // Random wandering
            performRandomWander();
        }
    }
    
    /**
     * Abstract method for loading monster images.
     * Each monster subclass must implement this to load their specific sprites.
     */
    protected abstract void loadImages();
    
    /**
     * Abstract method for setting monster-specific behavior.
     * Called during the update loop to determine monster actions.
     */
    public abstract void setAction();
    
    /**
     * Template method for monster AI behavior.
     * Provides a default implementation that can be overridden by subclasses.
     */
    protected void updateMonsterBehavior() {
        storeOriginalPosition();
        
        int tileDistance = getTileDistanceToPlayer();
        
        if (tileDistance <= detectionRange) {
            enterChaseMode();
        } else {
            enterWanderMode();
        }
    }
}
