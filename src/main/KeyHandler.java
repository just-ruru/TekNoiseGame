package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean isUpPressed, isDownPressed, isRightPressed, isLeftPressed, interactPressed, sprintPressed;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE STATE
        if (gp.gameState == gp.titleState) {

            if (code == KeyEvent.VK_W) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 2;
                }
            }
            if (code == KeyEvent.VK_S) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 2) {
                    gp.ui.commandNum = 0;
                }
            }
            if(code == KeyEvent.VK_ENTER) {
                if(gp.ui.commandNum == 0) {
                    gp.gameState = gp.playState;
                    gp.playMusic(1);
                    gp.ui.showMessage("woke up*\n\n"
                            + "what's happening... \n\n"
                            + "Its so dark...\n\n"
                            + "where is everybody...");
                }
                if(gp.ui.commandNum == 1) {
                    // add later
                }
                if(gp.ui.commandNum == 2) {
                    System.exit(0);
                }
            }
        }

        // PLAY STATE
        if(gp.gameState == gp.playState) {
            //character movement
            if(code == KeyEvent.VK_W){
                isUpPressed = true;
            }

            if(code == KeyEvent.VK_A){
                isLeftPressed = true;
            }

            if(code == KeyEvent.VK_S){
                isDownPressed = true;
            }

            if(code == KeyEvent.VK_D){
                isRightPressed = true;
            }

            if(code == KeyEvent.VK_E){
                interactPressed = true;
            }

            if(code == KeyEvent.VK_SHIFT){
                sprintPressed = true;
            }

        }

        if (gp.gameState == gp.cutsceneState) {
            if(code == KeyEvent.VK_E){
                interactPressed = true;
            }
        }

        if(code == KeyEvent.VK_P){
            if(gp.gameState == gp.playState) {
                gp.gameState = gp.pauseState;
                gp.pauseMusic();
            } else if (gp.gameState == gp.pauseState) {
                gp.gameState = gp.playState;
                gp.resumeMusic();
            }
        }

        if(code == KeyEvent.VK_F11){
            gp.toggleFullScreen();
        }

        if(code == KeyEvent.VK_EQUALS || code == KeyEvent.VK_ADD){
            gp.increaseVolume();
        }

        if(code == KeyEvent.VK_MINUS || code == KeyEvent.VK_SUBTRACT){
            gp.decreaseVolume();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W){
            isUpPressed = false;
        }

        if(code == KeyEvent.VK_A){
            isLeftPressed = false;
        }

        if(code == KeyEvent.VK_S){
            isDownPressed = false;
        }

        if(code == KeyEvent.VK_D){
            isRightPressed = false;
        }

        if(code == KeyEvent.VK_E){
            interactPressed = false;
        }

        if(code == KeyEvent.VK_SHIFT){
            sprintPressed = false;
        }
    }
}
