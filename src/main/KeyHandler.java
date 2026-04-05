package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean isUpPressed, isDownPressed, isRightPressed, isLeftPressed;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

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
    }
}
