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
        if (gp.gameState == gp.endingState && gp.isEndingNameEntryPhase()) {
            gp.appendLeaderboardNameChar(e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (gp.devSettings.handleKeyPressed(code)) {
            return;
        }

        if (code == KeyEvent.VK_ESCAPE) {
            if (gp.isHotkeyGuideVisible()) {
                gp.closeHotkeyGuide();
                return;
            }
            if (gp.gameState == gp.introCutsceneState) {
                gp.skipIntroCutscene();
                return;
            }
            if (gp.gameState == gp.titleSettingsState) {
                gp.ui.closeDialogue();
                gp.gameState = gp.titleState;
                return;
            }
            if (gp.gameState == gp.titleLoadState) {
                gp.ui.closeDialogue();
                gp.gameState = gp.titleState;
                return;
            }
            if (gp.gameState == gp.titleSaveState) {
                gp.ui.closeDialogue();
                gp.gameState = gp.pauseState;
                return;
            }
            if (gp.gameState == gp.characterSelectState) {
                gp.ui.closeDialogue();
                gp.gameState = gp.titleState;
                return;
            }
            if (gp.gameState == gp.gameOverState) {
                if (!gp.isGameOverInputReady()) {
                    return;
                }
                gp.ui.commandNum = 1;
                gp.ui.activateSelectedGameOverCommand();
                return;
            }
            if (gp.gameState == gp.endingState) {
                gp.skipEndingCreditsSlide();
                return;
            }
        }

        if (code == KeyEvent.VK_F10) {
            gp.toggleHotkeyGuide();
            return;
        }

        // TITLE STATE
        if (gp.gameState == gp.titleState) {

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.moveTitleSelection(-1);
                return;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.moveTitleSelection(1);
                return;
            }
            if(code == KeyEvent.VK_ENTER) {
                gp.ui.activateSelectedTitleCommand();
                return;
            }
        }

        if (gp.gameState == gp.titleSettingsState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.moveSettingsSelection(-1);
                return;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.moveSettingsSelection(1);
                return;
            }
            if (code == KeyEvent.VK_ENTER) {
                gp.ui.activateSelectedSettingsOption();
                return;
            }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                if (gp.ui.getSettingsSelectedOption() == gp.ui.SETTINGS_OPTION_VOLUME) {
                    gp.ui.decreaseSettingsVolume();
                }
                return;
            }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                if (gp.ui.getSettingsSelectedOption() == gp.ui.SETTINGS_OPTION_VOLUME) {
                    gp.ui.increaseSettingsVolume();
                }
                return;
            }
        }

        if (gp.gameState == gp.titleLoadState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.moveLoadSelection(-1);
                return;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.moveLoadSelection(1);
                return;
            }
            if (code == KeyEvent.VK_ENTER) {
                gp.ui.activateSelectedLoadOption();
                return;
            }
        }

        if (gp.gameState == gp.titleSaveState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.moveSaveSelection(-1);
                return;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.moveSaveSelection(1);
                return;
            }
            if (code == KeyEvent.VK_ENTER) {
                gp.ui.activateSelectedSaveOption();
                return;
            }
        }

        if (gp.gameState == gp.characterSelectState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.moveCharacterSelection(0, -1);
                return;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.moveCharacterSelection(0, 1);
                return;
            }
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                gp.ui.moveCharacterSelection(-1, 0);
                return;
            }
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                gp.ui.moveCharacterSelection(1, 0);
                return;
            }
            if (code == KeyEvent.VK_ENTER) {
                gp.ui.activateSelectedCharacterOption();
                return;
            }
        }

        if (gp.gameState == gp.gameOverState) {
            if (!gp.isGameOverInputReady()) {
                return;
            }
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.moveGameOverSelection(-1);
                return;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.moveGameOverSelection(1);
                return;
            }
            if (code == KeyEvent.VK_ENTER) {
                gp.ui.activateSelectedGameOverCommand();
                return;
            }
        }

        if (gp.gameState == gp.endingState) {
            if (code == KeyEvent.VK_BACK_SPACE) {
                gp.deleteLeaderboardNameChar();
                return;
            }
            if (code == KeyEvent.VK_ENTER) {
                gp.confirmLeaderboardNameEntry();
                return;
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
                gp.ui.pauseSelectedOption = 0;
            } else if (gp.gameState == gp.pauseState) {
                gp.gameState = gp.playState;
                gp.resumeMusic();
            }
        }

        if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.movePauseSelection(-1);
                return;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.movePauseSelection(1);
                return;
            }
            if (code == KeyEvent.VK_ENTER) {
                gp.ui.activateSelectedPauseOption();
                return;
            }
        }

        if(code == KeyEvent.VK_F5){
            if(gp.gameState == gp.playState) {
                gp.saveGame("autosave");
                gp.ui.showMessage("Game saved!");
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
