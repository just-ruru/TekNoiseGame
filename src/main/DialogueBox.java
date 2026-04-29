package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogueBox {

    private final GamePanel gp;
    private final Font pixelFont;
    private final List<String> pages = new ArrayList<>();

    private boolean active = false;
    private int pageIndex = 0;
    private int visibleChars = 0;

    // Text reveal speed in characters per frame.
    private final int charsPerFrame = 1;

    public DialogueBox(GamePanel gp) {
        this.gp = gp;
        this.pixelFont = new Font("Monospaced", Font.BOLD, 24);
    }

    public void show(String text) {
        pages.clear();
        pageIndex = 0;
        visibleChars = 0;

        if (text == null || text.isBlank()) {
            active = false;
            return;
        }

        String[] rawPages = text.split("\\n\\n");
        Collections.addAll(pages, rawPages);
        active = !pages.isEmpty();
    }

    public boolean isActive() {
        return active;
    }

    public void update() {
        if (!active || pages.isEmpty()) return;
        String currentPage = pages.get(pageIndex);
        if (visibleChars < currentPage.length()) {
            visibleChars = Math.min(currentPage.length(), visibleChars + charsPerFrame);
        }
    }

    public void advance() {
        if (!active || pages.isEmpty()) return;

        String currentPage = pages.get(pageIndex);
        if (visibleChars < currentPage.length()) {
            // First press while text is typing: reveal all immediately.
            visibleChars = currentPage.length();
            return;
        }

        // Second press: go to next page or close.
        if (pageIndex < pages.size() - 1) {
            pageIndex++;
            visibleChars = 0;
        } else {
            active = false;
        }
    }

    public void draw(Graphics2D g2) {
        if (!active || pages.isEmpty()) return;

        int boxX = gp.tileSize / 2;
        int boxY = gp.screenHeight - (gp.tileSize * 4);
        int boxWidth = gp.screenWidth - gp.tileSize;
        int boxHeight = gp.tileSize * 3;
        int borderThickness = 4;
        int textPadding = gp.tileSize / 2;

        Composite oldComposite = g2.getComposite();
        Font oldFont = g2.getFont();
        Color oldColor = g2.getColor();

        // Semi-transparent black background.
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 14, 14);

        // Solid white border.
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 14, 14);

        g2.setFont(pixelFont);
        FontMetrics fm = g2.getFontMetrics();

        String currentPage = pages.get(pageIndex);
        int end = Math.min(visibleChars, currentPage.length());
        String visibleText = currentPage.substring(0, end);

        int maxTextWidth = boxWidth - (textPadding * 2);
        List<String> lines = wrapText(visibleText, fm, maxTextWidth);

        int lineHeight = fm.getHeight();
        int textX = boxX + textPadding;
        int textY = boxY + textPadding + fm.getAscent();

        for (String line : lines) {
            g2.drawString(line, textX, textY);
            textY += lineHeight;
        }

        // Draw continue prompt when current page is fully shown.
        if (visibleChars >= currentPage.length()) {
            String prompt = "Press E >";
            int promptWidth = fm.stringWidth(prompt);

            // Right-align inside the box padding so it always fits.
            int promptX = boxX + boxWidth - textPadding - promptWidth;
            int promptY = boxY + boxHeight - (textPadding / 2) - fm.getDescent();

            g2.drawString(prompt, promptX, promptY);
        }

        g2.setComposite(oldComposite);
        g2.setFont(oldFont);
        g2.setColor(oldColor);
    }

    private List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
        List<String> wrapped = new ArrayList<>();
        if (text.isEmpty()) {
            return wrapped;
        }

        String[] paragraphs = text.split("\\n");
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                wrapped.add("");
                continue;
            }

            String[] words = paragraph.split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                String candidate = line.length() == 0 ? word : line + " " + word;
                if (fm.stringWidth(candidate) <= maxWidth) {
                    line.setLength(0);
                    line.append(candidate);
                } else {
                    wrapped.add(line.toString());
                    line.setLength(0);
                    line.append(word);
                }
            }
            if (line.length() > 0) {
                wrapped.add(line.toString());
            }
        }

        return wrapped;
    }
}
