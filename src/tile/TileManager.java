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
    Tile[] tile;
    int mapTileNum[][];

    public TileManager(GamePanel gp) {

        this.gp = gp;

        tile = new Tile[10];
        mapTileNum = new int[gp.maxStageCol][gp.maxStageRow];

        getTileImage();
        loadMap();
    }

    public void getTileImage() {

        try {

            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/floor.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadMap() {

        try {
            InputStream is = getClass().getResourceAsStream("/maps/stage01.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while(col < gp.maxStageCol && row < gp.maxStageRow) {

                String line = br.readLine();

                while(col < gp.maxStageCol) {

                    String numbers[] = line.split(" ");

                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[col][row] = num;
                    col++;
                }
                if(col == gp.maxStageCol) {
                    col = 0;
                    row ++;
                }
            }
            br.close();

        } catch(Exception e) {

        }
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

                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
            stageCol++;

            if(stageCol == gp.maxStageCol) {
                stageCol = 0;
                stageRow++;
            }
        }
    }
}
