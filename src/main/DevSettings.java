package main;

import java.awt.*;
import java.awt.event.KeyEvent;

public class DevSettings {
    public static final boolean TEST_MODE = true;

    private final GamePanel gp;
    private boolean open = false;
    private boolean phaseThroughWalls = false;
    private boolean unlimitedHealth = false;
    private boolean lightsOn = false;
    private boolean axeMania = false;
    private int selectedOption = 0;
    private int selectedSpeedIndex = 0;
    private int selectedStageIndex = 0;
    private static final int OPTION_COUNT = 6;
    private static final int OPTION_SPEED_SELECT = 4;
    private static final int OPTION_STAGE_SELECT = 5;
    private final String[] stageNames = {"Stage 1", "Stage 2", "Stage 3"};
    private final String[] stagePaths = {"/maps/stage01.txt", "/maps/stage02.txt", "/maps/stage03.txt"};
    private final String[] speedNames = {"Normal", "Dev", "Fast AF Boi"};
    private final float[] speedValues = {2.5f, 4f, 6f};

    public DevSettings(GamePanel gp) {
        this.gp = gp;
    }

    public boolean handleKeyPressed(int code) {
        if (!TEST_MODE) {
            return false;
        }

        if (code == KeyEvent.VK_TAB) {
            open = !open;
            return true;
        }

        if (!open) {
            return false;
        }

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            selectedOption--;
            if (selectedOption < 0) {
                selectedOption = OPTION_COUNT - 1;
            }
            return true;
        }

        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            selectedOption++;
            if (selectedOption >= OPTION_COUNT) {
                selectedOption = 0;
            }
            return true;
        }

        if (selectedOption == OPTION_SPEED_SELECT && (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)) {
            selectedSpeedIndex--;
            if (selectedSpeedIndex < 0) {
                selectedSpeedIndex = speedValues.length - 1;
            }
            return true;
        }

        if (selectedOption == OPTION_SPEED_SELECT && (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT)) {
            selectedSpeedIndex++;
            if (selectedSpeedIndex >= speedValues.length) {
                selectedSpeedIndex = 0;
            }
            return true;
        }

        if (selectedOption == OPTION_STAGE_SELECT && (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)) {
            selectedStageIndex--;
            if (selectedStageIndex < 0) {
                selectedStageIndex = stagePaths.length - 1;
            }
            return true;
        }

        if (selectedOption == OPTION_STAGE_SELECT && (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT)) {
            selectedStageIndex++;
            if (selectedStageIndex >= stagePaths.length) {
                selectedStageIndex = 0;
            }
            return true;
        }

        if (code == KeyEvent.VK_ENTER) {
            if (selectedOption == 0) {
                phaseThroughWalls = !phaseThroughWalls;
            } else if (selectedOption == 1) {
                unlimitedHealth = !unlimitedHealth;
            } else if (selectedOption == 2) {
                lightsOn = !lightsOn;
            } else if (selectedOption == 3) {
                axeMania = !axeMania;
            } else if (selectedOption == OPTION_STAGE_SELECT) {
                gp.loadStageForTesting(stagePaths[selectedStageIndex]);
            }
            return true;
        }

        return true;
    }

    public boolean isOpen() {
        return TEST_MODE && open;
    }

    public boolean isPhaseThroughWallsEnabled() {
        return TEST_MODE && phaseThroughWalls;
    }

    public boolean isUnlimitedHealthEnabled() {
        return TEST_MODE && unlimitedHealth;
    }

    public float getPlayerBaseSpeed() {
        return speedValues[selectedSpeedIndex];
    }

    public void applyContinuousEffects() {
        if (isUnlimitedHealthEnabled()) {
            gp.player.life = gp.player.maxLife;
        }
        if (TEST_MODE && axeMania) {
            gp.player.hasAxe = true;
        }
        gp.vignette.setDevRoomLightsOn(TEST_MODE && lightsOn);
    }

    public void draw(Graphics2D g2) {
        if (!isOpen()) {
            return;
        }

        Composite oldComposite = g2.getComposite();
        Font oldFont = g2.getFont();
        Color oldColor = g2.getColor();

        int panelX = gp.tileSize;
        int panelY = gp.tileSize;
        int panelW = gp.screenWidth - gp.tileSize * 2;
        int panelH = gp.tileSize * 9;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 12, 12);

        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 12, 12);

        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        g2.drawString("DEV SETTINGS", panelX + 24, panelY + 40);

        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        drawOption(g2, 0, phaseThroughWalls ? "[x] Phase through walls" : "[ ] Phase through walls", panelX, panelY + 88);
        drawOption(g2, 1, unlimitedHealth ? "[x] Unlimited health" : "[ ] Unlimited health", panelX, panelY + 126);
        drawOption(g2, 2, lightsOn ? "[x] Light on" : "[ ] Light on", panelX, panelY + 164);
        drawOption(g2, 3, axeMania ? "[x] Axe mania" : "[ ] Axe mania", panelX, panelY + 202);
        drawOption(g2, OPTION_SPEED_SELECT, "Speed: < " + speedNames[selectedSpeedIndex] + " (" + speedValues[selectedSpeedIndex] + "f) >", panelX, panelY + 240);
        drawOption(g2, OPTION_STAGE_SELECT, "Stage select: < " + stageNames[selectedStageIndex] + " >", panelX, panelY + 278);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g2.setColor(new Color(210, 210, 210));
        g2.drawString("W/S: move   A/D: stage   Enter: toggle/apply   Tab: close", panelX + 24, panelY + panelH - 24);

        g2.setComposite(oldComposite);
        g2.setFont(oldFont);
        g2.setColor(oldColor);
    }

    private void drawOption(Graphics2D g2, int optionIndex, String text, int panelX, int y) {
        g2.setColor(optionIndex == selectedOption ? new Color(255, 220, 120) : Color.WHITE);
        String pointer = optionIndex == selectedOption ? "> " : "  ";
        g2.drawString(pointer + text, panelX + 28, y);
    }
}
