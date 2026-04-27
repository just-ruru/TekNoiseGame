package main;

import entity.Player;
import objects.SuperObject;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    //SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48 x 48 tile size

    public final int maxScreenColumn = 16;
    public final int maxScreenRow = 12;

    public final int screenWidth = tileSize * maxScreenColumn; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTINGS
    public final int maxStageCol = 44;
    public final int maxStageRow = 15;
    public final int worldWidth = tileSize * maxStageCol;
    public final int worldHeight = tileSize * maxStageRow;
    public VignetteLight vignette = new VignetteLight(screenWidth, screenHeight);
    private int interactCooldown = 0;

    // FPS
    int FPS = 60;
    int choice = 1;

    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this);
    public UI ui = new UI(this);

    Thread gameThread;

    // ENTITY AND OBJECT
    public Player player = new Player(this,keyH, choice);
    public SuperObject obj[]= new SuperObject[10];

    // GAME STATE
    public  int gameState;
    public final int playState = 1;
    public final  int pauseState = 2;

    int playerX = player.stageX;
    int playerY = player.stageY;
    int playerSpeed = 4;

    public GamePanel (){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }


    public void setupGame() {
        assetSetter.setObject();
        playMusic(0);
        gameState = playState;

    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update(){
        if(interactCooldown > 0) {
            interactCooldown--;
        }
        if(gameState == playState) {
            player.update();
        }
        if(gameState == pauseState) {
            // nothing
        }
        vignette.update();
    }

    public void run(){
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread != null){
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta>= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if(timer >= 1000000000){
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //Note: The order of rendering is so important, basically first rendered is the bottom most layer
        //followed by the next, this is how to create a layering system in the game.

        Graphics2D g2 = (Graphics2D)g;
        //TILE 1st layer
        tileM.draw(g2);
        //OBJECT 2nd layer
        for(int i = 0; i <obj.length; i++){
            if(obj[i] != null){
                obj[i].draw(g2, this);
            }
        }
        //PLAYER 3rd layer
        player.draw(g2);

        vignette.draw(g2, player.screenX, player.screenY, tileSize, tileSize);

        // UI
        ui.draw(g2);

        g2.dispose();
    }

    public void playMusic(int i) {

        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {

        music.stop();
    }

    public void pauseMusic() {
        music.pause();
    }

    public void resumeMusic() {
        music.resume();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

}
