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

        if (gp.gameState != gp.titleState || !SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        Point gamePoint = gp.toGamePoint(e.getX(), e.getY());
        if (gamePoint == null) {
            return;
        }

        if (gp.ui.activateTitleCommandAt(gamePoint.x, gamePoint.y)) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        gp.setCursor(Cursor.getDefaultCursor());
    }

    private void updateTitleHover(MouseEvent e) {
        if (gp.gameState != gp.titleState) {
            gp.setCursor(Cursor.getDefaultCursor());
            return;
        }

        Point gamePoint = gp.toGamePoint(e.getX(), e.getY());
        boolean hoveringButton = gamePoint != null && gp.ui.selectTitleCommandAt(gamePoint.x, gamePoint.y);
        gp.setCursor(Cursor.getPredefinedCursor(
                hoveringButton ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }
}
