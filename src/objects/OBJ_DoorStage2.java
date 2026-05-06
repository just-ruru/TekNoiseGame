package objects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class OBJ_DoorStage2 extends SuperObject {
    private int phase = 1;
    private final BufferedImage[] phaseImages = new BufferedImage[4];

    public OBJ_DoorStage2() {
        name = "DoorStage2";
        collision = true;
        solidArea = new Rectangle(0, 0, 144, 96);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        image = loadImage("/objects/doorfull.png");
        for (int i = 0; i < phaseImages.length; i++) {
            phaseImages[i] = loadImage("/objects/stage2Door/Stage2Door" + (i + 1) + ".png");
        }
    }

    public int getPhase() {
        return phase;
    }

    public boolean isOpenable() {
        return phase >= 4;
    }

    public void hitWithAxe() {
        if (phase < 4) {
            phase++;
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        int cameraStageX = gp.getCameraStageX();
        int cameraStageY = gp.getCameraStageY();
        int screenX = stageX - cameraStageX + gp.player.screenX;
        int screenY = stageY - cameraStageY + gp.player.screenY;

        int drawW = gp.tileSize * 3;
        int drawH = gp.tileSize * 2;

        if (stageX + drawW > cameraStageX - gp.player.screenX &&
                stageX - drawW < cameraStageX + gp.player.screenX &&
                stageY + drawH > cameraStageY - gp.player.screenY &&
                stageY - drawH < cameraStageY + gp.player.screenY) {

            BufferedImage phaseImage = phaseImages[Math.max(0, Math.min(phaseImages.length - 1, phase - 1))];
            if (phaseImage != null) {
                g2.drawImage(phaseImage, screenX, screenY, drawW, drawH, null);
            } else {
                for (int col = 0; col < 3; col++) {
                    g2.drawImage(image, screenX + (col * gp.tileSize), screenY, gp.tileSize, drawH, null);
                }
                drawPlanks(g2, screenX, screenY, drawW, drawH);
            }
        }
    }

    private void drawPlanks(Graphics2D g2, int x, int y, int width, int height) {
        Color oldColor = g2.getColor();
        Stroke oldStroke = g2.getStroke();

        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(92, 55, 28));

        if (phase <= 1) {
            g2.drawLine(x + 12, y + 18, x + width - 12, y + height - 18);
        }
        if (phase <= 2) {
            g2.drawLine(x + 12, y + height - 18, x + width - 12, y + 18);
        }
        if (phase <= 3) {
            g2.drawLine(x + 18, y + height / 2, x + width - 18, y + height / 2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(oldColor);
    }

    private BufferedImage loadImage(String path) {
        try {
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                return null;
            }
            return ImageIO.read(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
