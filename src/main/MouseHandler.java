package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {
    private final GamePanel gp;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateTitleHover(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateTitleHover(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gp.requestFocusInWindow();

        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        Point gamePoint = gp.toGamePoint(e.getX(), e.getY());
        if (gamePoint == null) {
            return;
        }

        if (gp.gameState == gp.titleState && gp.ui.activateTitleCommandAt(gamePoint.x, gamePoint.y)) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if (gp.gameState == gp.titleSettingsState && gp.ui.handleSettingsClickAt(gamePoint.x, gamePoint.y)) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if (gp.gameState == gp.titleLoadState && gp.ui.handleLoadClickAt(gamePoint.x, gamePoint.y)) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if (gp.gameState == gp.characterSelectState && gp.ui.handleCharacterSelectionClickAt(gamePoint.x, gamePoint.y)) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if (gp.gameState == gp.gameOverState && gp.ui.activateGameOverCommandAt(gamePoint.x, gamePoint.y)) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        gp.setCursor(Cursor.getDefaultCursor());
    }

    private void updateTitleHover(MouseEvent e) {
        if (gp.gameState != gp.titleState && gp.gameState != gp.characterSelectState && gp.gameState != gp.gameOverState && gp.gameState != gp.titleSettingsState && gp.gameState != gp.titleLoadState) {
            gp.setCursor(Cursor.getDefaultCursor());
            return;
        }

        Point gamePoint = gp.toGamePoint(e.getX(), e.getY());
        boolean hoveringButton = false;
        if (gamePoint != null) {
            if (gp.gameState == gp.titleState) {
                hoveringButton = gp.ui.selectTitleCommandAt(gamePoint.x, gamePoint.y);
            } else if (gp.gameState == gp.titleSettingsState) {
                hoveringButton = gp.ui.selectSettingsOptionAt(gamePoint.x, gamePoint.y);
            } else if (gp.gameState == gp.titleLoadState) {
                hoveringButton = gp.ui.selectLoadOptionAt(gamePoint.x, gamePoint.y);
            } else if (gp.gameState == gp.characterSelectState) {
                hoveringButton = gp.ui.updateCharacterHoverAt(gamePoint.x, gamePoint.y);
            } else if (gp.gameState == gp.gameOverState) {
                hoveringButton = gp.ui.selectGameOverCommandAt(gamePoint.x, gamePoint.y);
            }
        } else if (gp.gameState == gp.characterSelectState) {
            gp.ui.updateCharacterHoverAt(-1, -1);
        }

        gp.setCursor(Cursor.getPredefinedCursor(
                hoveringButton ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }
}
