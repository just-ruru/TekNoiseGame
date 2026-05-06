package npc;

import main.GamePanel;
import objects.SuperObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class NPC_JhonPorkJerkyJake extends SuperObject {
    private final BufferedImage[] animationFrames = new BufferedImage[3];
    private boolean fading = false;
    private float alpha = 1f;
    private int frameIndex = 0;
    private int frameCounter = 0;
    private static final int FRAME_CHANGE_TICKS = 12;

    public NPC_JhonPorkJerkyJake() {
        name = "JhonPorkJerkyJake";
        collision = true;
        solidArea = new Rectangle(0, 0, 48, 48);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        try {
            for (int i = 0; i < animationFrames.length; i++) {
                animationFrames[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/npc/JhonPorkJerkyJake/JhonPork&JerkyJake" + (i + 1) + ".png"));
            }
            image = animationFrames[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startFade() {
        fading = true;
        collision = false;
    }

    public boolean isFadedOut() {
        return alpha <= 0f;
    }

    public void update() {
        frameCounter++;
        if (frameCounter >= FRAME_CHANGE_TICKS) {
            frameIndex = (frameIndex + 1) % animationFrames.length;
            image = animationFrames[frameIndex];
            frameCounter = 0;
        }

        if (fading) {
            alpha = Math.max(0f, alpha - 0.02f);
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        int cameraStageX = gp.getCameraStageX();
        int cameraStageY = gp.getCameraStageY();
        int screenX = stageX - cameraStageX + gp.player.screenX;
        int screenY = stageY - cameraStageY + gp.player.screenY;

        if (stageX + gp.tileSize > cameraStageX - gp.player.screenX &&
                stageX - gp.tileSize < cameraStageX + gp.player.screenX &&
                stageY + gp.tileSize > cameraStageY - gp.player.screenY &&
                stageY - gp.tileSize < cameraStageY + gp.player.screenY) {

            Composite oldComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            g2.setComposite(oldComposite);
        }
    }
}
