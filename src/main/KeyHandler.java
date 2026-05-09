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

        if (gp.devSettings.handleKeyPressed(code)) {
            return;
        }

        // TITLE STATE
        if (gp.gameState == gp.titleState) {

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.moveTitleSelection(-1);
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.moveTitleSelection(1);
            }
            if(code == KeyEvent.VK_ENTER) {
                gp.ui.activateSelectedTitleCommand();
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

        if (gp.gameState == gp.introCutsceneState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.skipIntroCutscene();
                return;
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
