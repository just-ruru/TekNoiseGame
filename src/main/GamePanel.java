package main;

import entity.Entity;
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
    public Entity monster[] = new Entity[20];

    // GAME STATE
    public  int gameState;
    public final int playState = 1;
    public final  int pauseState = 2;
    public final int transitionState = 3;

    // MAP / TRANSITION
    private String currentMapPath = "/maps/stage01.txt";
    private String nextMapPath = null;
    private float fadeAlpha = 0f; // 0..1
    private final float fadeSpeed = 0.06f;
    private boolean fadeOutPhase = true;

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
        // Stage start: keys are per-stage and shouldn't carry over.
        player.hasKey = 0;
        player.setDefaultValues();
        player.setSpawnForMap(currentMapPath);
        assetSetter.setObject(currentMapPath);
//        assetSetter.setObject();
        assetSetter.setMonster();
        playMusic(0);
        gameState = playState;
        ui.showMessage("Woke up* what the fuckkk!!!??\n\n"
        + "You gotta be kidding me \n\n" + "Im outta here");


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
            for(int i = 0; i < monster.length; i++) {
                if(monster[i] != null) {
                    monster[i].update();
                }
            }
        }
        if(gameState == pauseState) {
            // nothing
        }
        if (gameState == transitionState) {
            updateTransition();
        }
        ui.update();
        vignette.update();
    }

    private void updateTransition() {
        if (nextMapPath == null) {
            // Safety: cancel transition if no target set.
            fadeAlpha = 0f;
            gameState = playState;
            return;
        }

        if (fadeOutPhase) {
            fadeAlpha += fadeSpeed;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;

                // Swap map at full black
                currentMapPath = nextMapPath;
                tileM.loadMap(currentMapPath);
                player.setDefaultValues();
                player.setSpawnForMap(currentMapPath);
                player.hasKey = 0; // keys are per-stage
                assetSetter.setObject(currentMapPath);

                // now fade back in
                fadeOutPhase = false;
            }
        } else {
            fadeAlpha -= fadeSpeed;
            if (fadeAlpha <= 0f) {
                fadeAlpha = 0f;
                nextMapPath = null;
                fadeOutPhase = true;
                gameState = playState;
            }
        }
    }

    public void startMapTransition(String mapPath) {
        if (gameState == transitionState) return;
        nextMapPath = mapPath;
        fadeAlpha = 0f;
        fadeOutPhase = true;
        gameState = transitionState;
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
        //MONSTERS 3rd layer
        for(int i = 0; i < monster.length; i++){
            if(monster[i] != null){
                monster[i].draw(g2);
            }
        }
        //PLAYER 4th layer
        player.draw(g2);

        vignette.draw(g2, player.screenX, player.screenY, tileSize, tileSize);

        // UI
        ui.draw(g2);

        // Fade overlay (draw last so it covers everything)
        if (gameState == transitionState && fadeAlpha > 0f) {
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    Math.min(1f, Math.max(0f, fadeAlpha))));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.setComposite(old);
        }

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
