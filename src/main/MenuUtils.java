package main;

import java.awt.Rectangle;

public class MenuUtils {
    public static Rectangle panelRect(GamePanel gp, int panelWidth, int panelHeight) {
        int x = (gp.screenWidth - panelWidth) / 2;
        int y = (gp.screenHeight - panelHeight) / 2;
        return new Rectangle(x, y, panelWidth, panelHeight);
    }

    public static int optionStartX(Rectangle panel) {
        return panel.x + 28;
    }

    public static int optionEndX(Rectangle panel) {
        return panel.x + panel.width - 28;
    }

    public static Rectangle optionRect(Rectangle panel, int optionY, int optionHeight) {
        int startX = optionStartX(panel);
        int width = optionEndX(panel) - startX;
        int top = optionY - 20;
        return new Rectangle(startX, top, width, optionHeight);
    }

    public static Rectangle optionRectForIndex(Rectangle panel, int optionIndex, int optionStartY, int lineHeight, int optionHeight) {
        int optionY = panel.y + optionStartY + optionIndex * lineHeight;
        return optionRect(panel, optionY, optionHeight);
    }

    public static Rectangle backOptionRect(Rectangle panel, int optionY, int extraYOffset) {
        int y = optionY + extraYOffset;
        return optionRect(panel, y, 30);
    }
}

