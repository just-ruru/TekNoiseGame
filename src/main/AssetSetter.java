package main;

import main.monster.MON_Dementor;
import main.monster.MON_Stage3BossMomo;
import main.monster.MON_Stage2WitherBoss;
import npc.NPC_JhonPorkJerkyJake;
import objects.OBJ_Bag;
import objects.OBJ_LightSwitch;
import objects.OBJ_TablePaper;
import objects.*;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        gp.obj[2].stageX = 19 * gp.tileSize;
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
        int objectIndex = 0;
        boolean doorPlaced = false;
        List<Point> breakableTableTiles = new ArrayList<>();
        List<Point> candleTiles = new ArrayList<>();

        try {
            InputStream is = getClass().getResourceAsStream("/maps/stage02.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                for (int col = 0; col < tokens.length; col++) {
                    String token = tokens[col];

                    if ("D".equalsIgnoreCase(token) && !doorPlaced) {
                        objectIndex = addObject(objectIndex, new OBJ_DoorStage2(), col, row);
                        doorPlaced = true;
                    } else if ("X".equalsIgnoreCase(token)) {
                        breakableTableTiles.add(new Point(col, row));
                    } else if ("C".equalsIgnoreCase(token)) {
                        objectIndex = addObject(objectIndex, new OBJ_TablePaper(), col, row);
                    } else if ("H".equalsIgnoreCase(token)) {
                        objectIndex = addObject(objectIndex, new OBJ_HealthBag(), col, row);
                    } else if ("A".equalsIgnoreCase(token)) {
                        objectIndex = addObject(objectIndex, new OBJ_Axe(), col, row);
                    } else if ("J".equalsIgnoreCase(token)) {
                        objectIndex = addObject(objectIndex, new NPC_JhonPorkJerkyJake(), col, row);
                    } else if ("N".equalsIgnoreCase(token)) {
                        candleTiles.add(new Point(col, row));
                    }
                }
                row++;
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        candleTiles.sort(Comparator.comparingInt((Point point) -> point.y).thenComparingInt(point -> point.x));
        for (int i = 0; i < candleTiles.size(); i++) {
            Point tile = candleTiles.get(i);
            objectIndex = addObject(objectIndex, new OBJ_Candle(i + 1), tile.x, tile.y);
        }

        Collections.shuffle(breakableTableTiles);
        for (int i = 0; i < breakableTableTiles.size(); i++) {
            Point tile = breakableTableTiles.get(i);
            int tableVariant = (i % 4) + 1;
            objectIndex = addObject(objectIndex, new OBJ_BreakableTable(tableVariant), tile.x, tile.y);
        }
    }

    private int addObject(int objectIndex, SuperObject object, int tileCol, int tileRow) {
        if (objectIndex >= gp.obj.length) {
            System.out.println("Warning: object array is full, skipped " + object.name + " at " + tileCol + "," + tileRow);
            return objectIndex;
        }

        gp.obj[objectIndex] = object;
        gp.obj[objectIndex].stageX = tileCol * gp.tileSize;
        gp.obj[objectIndex].stageY = tileRow * gp.tileSize;
        return objectIndex + 1;
    }
    
    private void setStage03Objects() {
        int objectIndex = 0;
        boolean doorPlaced = false;
        List<Point> candleTiles = new ArrayList<>();
        List<Point> breakableTableTiles = new ArrayList<>();
        List<Point> cutsceneBreakTiles = new ArrayList<>();

        try {
            InputStream is = getClass().getResourceAsStream("/maps/stage03.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                for (int col = 0; col < tokens.length; col++) {
                    String token = tokens[col];

                    if ("D".equalsIgnoreCase(token) && !doorPlaced) {
                        objectIndex = addObject(objectIndex, new OBJ_DoorStage3(), col, row);
                        doorPlaced = true;
                    } else if ("C".equalsIgnoreCase(token)) {
                        objectIndex = addObject(objectIndex, new OBJ_TablePaper(), col, row);
                    } else if ("H".equalsIgnoreCase(token)) {
                        objectIndex = addObject(objectIndex, new OBJ_HealthBag(), col, row);
                    } else if ("N".equalsIgnoreCase(token)) {
                        candleTiles.add(new Point(col, row));
                    } else if ("XX".equalsIgnoreCase(token)) {
                        cutsceneBreakTiles.add(new Point(col, row));
                    } else if ("X".equalsIgnoreCase(token)) {
                        breakableTableTiles.add(new Point(col, row));
                    }
                }
                row++;
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        candleTiles.sort(Comparator.comparingInt((Point point) -> point.y).thenComparingInt(point -> point.x));
        for (int i = 0; i < candleTiles.size(); i++) {
            Point tile = candleTiles.get(i);
            objectIndex = addObject(objectIndex, new OBJ_Candle(i + 1), tile.x, tile.y);
        }

        int tableVariant = 1;
        for (Point tile : breakableTableTiles) {
            objectIndex = addObject(objectIndex, new OBJ_BreakableTable(tableVariant), tile.x, tile.y);
            tableVariant = tableVariant % 4 + 1;
        }

        for (Point tile : cutsceneBreakTiles) {
            objectIndex = addObject(objectIndex, new OBJ_CutsceneBreakable(tableVariant), tile.x, tile.y);
            tableVariant = tableVariant % 4 + 1;
        }
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
        // Scattered from middle to right side, avoiding left side
        gp.monster[0] = new MON_Dementor(gp, 1);
        gp.monster[0].stageX = gp.tileSize * 25;
        gp.monster[0].stageY = gp.tileSize * 3;

        gp.monster[1] = new MON_Dementor(gp, 2);
        gp.monster[1].stageX = gp.tileSize * 37;
        gp.monster[1].stageY = gp.tileSize * 6;

        gp.monster[2] = new MON_Dementor(gp, 3);
        gp.monster[2].stageX = gp.tileSize * 30;
        gp.monster[2].stageY = gp.tileSize * 11;

        gp.monster[3] = new MON_Dementor(gp, 4);
        gp.monster[3].stageX = gp.tileSize * 24;
        gp.monster[3].stageY = gp.tileSize * 9;
    }

    private void setStage02Monsters() {
        Point bossTile = findMapMarker("/maps/stage02.txt", "S");
        if (bossTile == null) {
            System.out.println("Warning: stage02 boss marker S not found.");
            return;
        }

        gp.monster[0] = new MON_Stage2WitherBoss(gp);
        gp.monster[0].stageX = gp.tileSize * bossTile.x;
        gp.monster[0].stageY = gp.tileSize * bossTile.y;
    }

    public Point findMapMarker(String mapPath, String marker) {
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                for (int col = 0; col < tokens.length; col++) {
                    if (marker.equalsIgnoreCase(tokens[col])) {
                        br.close();
                        return new Point(col, row);
                    }
                }
                row++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Point> findMapMarkers(String mapPath, String marker) {
        List<Point> markers = new ArrayList<>();
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                for (int col = 0; col < tokens.length; col++) {
                    if (marker.equalsIgnoreCase(tokens[col])) {
                        markers.add(new Point(col, row));
                    }
                }
                row++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return markers;
    }

    private void setStage03Monsters() {
        // Stage 3 stays quiet until the candle puzzle cutscene triggers the chase sequence.
    }

    public int spawnMonster(entity.Entity entity, int tileCol, int tileRow) {
        for (int i = 0; i < gp.monster.length; i++) {
            if (gp.monster[i] == null) {
                gp.monster[i] = entity;
                gp.monster[i].stageX = tileCol * gp.tileSize;
                gp.monster[i].stageY = tileRow * gp.tileSize;
                return i;
            }
        }

        System.out.println("Warning: monster array is full, skipped " + entity.name + " at " + tileCol + "," + tileRow);
        return -1;
    }
}

