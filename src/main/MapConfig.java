package main;

public class MapConfig {
    public int maxStageCol;
    public int maxStageRow;
    public int worldWidth;
    public int worldHeight;
    
    public MapConfig(int maxStageCol, int maxStageRow, int tileSize) {
        this.maxStageCol = maxStageCol;
        this.maxStageRow = maxStageRow;
        this.worldWidth = tileSize * maxStageCol;
        this.worldHeight = tileSize * maxStageRow;
    }
    
    public static MapConfig getDefaultConfig(int tileSize) {
        return new MapConfig(44, 15, tileSize);
    }
    
    public static MapConfig getStage1Config(int tileSize) {
        return new MapConfig(44, 15, tileSize);
    }
    
    public static MapConfig getStage2Config(int tileSize) {
        return new MapConfig(44, 50, tileSize);
    }
    
    public static MapConfig getStage3Config(int tileSize) {
        return new MapConfig(30, 20, tileSize);
    }
    
    public static MapConfig getStage4Config(int tileSize) {
        return new MapConfig(60, 10, tileSize);
    }
    
    public static MapConfig getConfigForMap(String mapPath, int tileSize) {
        if (mapPath.contains("stage01")) {
            return getStage1Config(tileSize);
        } else if (mapPath.contains("stage02")) {
            return getStage2Config(tileSize);
        } else if (mapPath.contains("stage03")) {
            return getStage3Config(tileSize);
        } else if (mapPath.contains("stage04")) {
            return getStage4Config(tileSize);
        } else {
            return getDefaultConfig(tileSize);
        }
    }
}
