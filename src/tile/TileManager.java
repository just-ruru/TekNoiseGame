package tile;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {

    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    private String currentMapPath = "/maps/stage01.txt";

    public TileManager(GamePanel gp) {

        this.gp = gp;

        tile = new Tile[99];
        mapTileNum = new int[gp.maxStageCol][gp.maxStageRow];

        getTileImage();
        loadMap(currentMapPath);
    }

    public void getTileImage() {
        //GUIDE
//        0 - Ground
//        1 - wall
//        2 - table
//        3 - narrow table
//        4 - chairs
//        5 - table type2
//        6 - table type3
//        7 - placeholder
//        8 - vase
//        9 - door1
//        10 - door2
//        11 - tv1
//        12 - tv2
//        13 - Board1
//        14 - Board2
//
        try {

            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/1.png"));
            tile[1].collision = true;

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/2.png"));
            tile[2].collision = true;

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/3.png"));
            tile[3].collision = true;

            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/4.png"));
            tile[4].collision = true;

            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/5.png"));
            tile[5].collision = true;

            tile[6] = new Tile();
            tile[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/6.png"));
            tile[6].collision = true;

            tile[7] = new Tile();
            tile[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/7.png"));
            // Used as a placement marker in the map (e.g., door position), not as a wall.
            tile[7].collision = false;

            tile[8] = new Tile();
            tile[8].image = ImageIO.read(getClass().getResourceAsStream("/tiles/8.png"));
            tile[8].collision = true;

            tile[9] = new Tile();
            tile[9].image = ImageIO.read(getClass().getResourceAsStream("/tiles/Door1.png"));
            tile[9].collision = true;

            tile[10] = new Tile();
            tile[10].image = ImageIO.read(getClass().getResourceAsStream("/tiles/Door2.png"));
            tile[10].collision = true;

            tile[11] = new Tile();
            tile[11].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tv1.png"));
            tile[11].collision = true;

            tile[12] = new Tile();
            tile[12].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tv2.png"));
            tile[12].collision = true;

            tile[13] = new Tile();
            tile[13].image = ImageIO.read(getClass().getResourceAsStream("/tiles/Board1.png"));
            tile[13].collision = true;

            tile[14] = new Tile();
            tile[14].image = ImageIO.read(getClass().getResourceAsStream("/tiles/Board2.png"));
            tile[14].collision = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadMap(String mapPath) {
        currentMapPath = mapPath;
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < gp.maxStageCol && row < gp.maxStageRow) {
                String line = br.readLine();

                if (line == null) break; // Guard against short/empty files

                String[] numbers = line.trim().split("\\s+"); // Handles multiple spaces/tabs

                while (col < gp.maxStageCol) {
                    int num = Integer.parseInt(numbers[col]);

                    if (num >= 0 && num < tile.length) { // Bounds check against tile[]
                        mapTileNum[col][row] = num;
                    } else {
                        System.out.println("Warning: tile ID " + num + " out of range at [" + col + "][" + row + "]");
                    }

                    col++;
                }

                if (col == gp.maxStageCol) {
                    col = 0;
                    row++;
                }
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace(); // Don't silently swallow errors
        }
    }

    public String getCurrentMapPath() {
        return currentMapPath;
    }

    public void draw(Graphics2D g2) {

        int stageCol = 0;
        int stageRow = 0;

        while(stageCol < gp.maxStageCol && stageRow < gp.maxStageRow) {

            int tileNum = mapTileNum[stageCol][stageRow];

            int stageX = stageCol * gp.tileSize;
            int stageY = stageRow * gp.tileSize;
            int screenX =  stageX - gp.player.stageX + gp.player.screenX;
            int screenY = stageY - gp.player.stageY + gp.player.screenY;

            if(stageX + gp.tileSize > gp.player.stageX - gp.player.screenX &&
                    stageX - gp.tileSize < gp.player.stageX + gp.player.screenX &&
                    stageY + gp.tileSize > gp.player.stageY - gp.player.screenY &&
                    stageY - gp.tileSize < gp.player.stageY + gp.player.screenY) {


                if (tile[tileNum] == null) {
                    System.out.println("WARNING: tile[" + tileNum + "] is null at [" + stageCol + "][" + stageRow + "]");
                } else {
                    g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }
            }
            stageCol++;

            if(stageCol == gp.maxStageCol) {
                stageCol = 0;
                stageRow++;
            }
        }
    }
}
