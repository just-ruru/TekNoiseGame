package main;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * VignetteLight — simulates a dark room with a soft light radius around the player.
 *
 * FEATURES:
 *  - Dark overlay with a radial "torch" cutout centered on the player
 *  - Toggle light ON/OFF (e.g. via OBJ_Switch or key press)
 *  - Flicker effect with configurable intensity and frequency
 *
 * HOW TO USE:
 *  1. Create one instance in your GamePanel (or wherever you manage rendering):
 *       VignetteLight vignette = new VignetteLight(screenWidth, screenHeight);
 *
 *  2. Call vignette.update() every game tick (in your update loop).
 *
 *  3. Call vignette.draw(g2, playerScreenX, playerScreenY) AFTER drawing all tiles/entities.
 *
 *  4. Toggle light:
 *       vignette.setLightOn(true / false);
 *       // or simply:
 *       vignette.toggleLight();
 *
 *  5. Enable flicker:
 *       vignette.setFlicker(true);
 */
public class VignetteLight {

    // ── Config ────────────────────────────────────────────────────────────────
    /** Base radius of the light circle around the player (pixels). */
    private int lightRadius = 60;

    /** How dark the unlit area is (0.0 = transparent, 1.0 = pitch black). */
    public static float darkness = 0.95f;

    /** Color of the dark overlay. */
    private Color darkColor = new Color(0, 0, 0);

    /** Soft glow colour blended at the edge of the light circle. */
    private Color glowColor = new Color(255, 200, 100, 80); // warm torch tint

    // ── Flicker ───────────────────────────────────────────────────────────────
    private boolean flickerEnabled = false;

    /** Max pixels the radius shrinks/grows during a flicker. */
    private int flickerAmount = 18;

    /** Chance per tick (0-100) that a flicker frame starts. */
    private int flickerChance = 3;

    /** How many ticks a single flicker lasts. */
    private int flickerDuration = 6;

    // ── State ─────────────────────────────────────────────────────────────────
    private boolean lightOn = true;
    public static boolean roomLight = false;
    private int currentRadius;
    private int flickerTimer = 0;
    private boolean isFlickering = false;
    private int flickerOffset = 0;

    private final int screenW;
    private final int screenH;
    private final Random rand = new Random();

    // Cached image — recreated only when screen size changes
    private BufferedImage overlay;

    // ─────────────────────────────────────────────────────────────────────────
    public VignetteLight(int screenWidth, int screenHeight) {
        this.screenW = screenWidth;
        this.screenH = screenHeight;
        this.currentRadius = lightRadius;
        overlay = new BufferedImage(screenW, screenH, BufferedImage.TYPE_INT_ARGB);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Call once per game tick in your update() method. */
    public void update() {
        if (!lightOn) {
            currentRadius = 0;
            return;
        }

        if (flickerEnabled) {
            updateFlicker();
        } else {
            currentRadius = lightRadius;
        }
    }

    /**
     * Draw the dark vignette overlay.
     *
     * @param g2          Graphics2D from your paintComponent / draw method
     * @param playerScreenX  player's X position on screen (not world coords)
     * @param playerScreenY  player's Y position on screen (not world coords)
     */
    public void draw(Graphics2D g2, int playerScreenX, int playerScreenY, int playerWidth, int playerHeight) {
        // Center the light on the player's center (adjust 24 to half your tile/sprite size)
        int cx = playerScreenX + 24;
        int cy = playerScreenY + 40;

        // ── Build overlay ────────────────────────────────────────────────────
        Graphics2D og = overlay.createGraphics();
        og.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Fill everything black (with darkness alpha)
        og.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        og.setColor(new Color(
                darkColor.getRed(),
                darkColor.getGreen(),
                darkColor.getBlue(),
                (int)(darkness * 255)));
        og.fillRect(0, 0, screenW, screenH);

        if (lightOn && currentRadius > 0) {
            // 2. Cut out a transparent circle where the light is
            og.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
            drawLightCircle(og, cx, cy, currentRadius);

            // 3. Add warm glow ring on top (DST_ATOP blends only inside the cutout edge)
            og.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            drawGlowRing(og, cx, cy, currentRadius);
        }

        og.dispose();

        // ── Composite overlay onto game screen ───────────────────────────────
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2.drawImage(overlay, 0, 0, null);
        g2.setComposite(old);
    }

    // ── Light toggle ──────────────────────────────────────────────────────────

    /** Turn the light on or off. */
    public void setLightOn(boolean on) {
        this.lightOn = on;
        if (!on) flickerTimer = 0;
    }

    /** Toggle between on and off. */
    public void toggleLight() {

        if(lightOn == true){
            darkness = 0;
        }else if(lightOn == false) {
            darkness = 0.95f;
        };
        lightOn = !lightOn;
    }

    public boolean isLightOn() { return lightOn; }

    // ── Flicker ───────────────────────────────────────────────────────────────

    public void setFlicker(boolean enabled) { this.flickerEnabled = enabled; }
    public boolean isFlickerEnabled()        { return flickerEnabled; }

    // ── Customisation helpers ─────────────────────────────────────────────────

    public void setLightRadius(int radius)    { this.lightRadius = radius; }
    public void setDarkness(float darkness)   { this.darkness = Math.max(0f, Math.min(1f, darkness)); }
    public void setDarkColor(Color c)         { this.darkColor = c; }
    public void setGlowColor(Color c)         { this.glowColor = c; }
    public void setFlickerAmount(int px)      { this.flickerAmount = px; }
    public void setFlickerChance(int pct)     { this.flickerChance = pct; }
    public void setFlickerDuration(int ticks) { this.flickerDuration = ticks; }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void updateFlicker() {
        if (isFlickering) {
            flickerTimer--;
            if (flickerTimer <= 0) {
                isFlickering = false;
                flickerOffset = 0;
                currentRadius = lightRadius;
            }
            // radius shrinks during flicker
            currentRadius = lightRadius - Math.abs(flickerOffset);
        } else {
            currentRadius = lightRadius;
            // random chance to start a flicker
            if (rand.nextInt(100) < flickerChance) {
                isFlickering = true;
                flickerTimer = flickerDuration;
                flickerOffset = rand.nextInt(flickerAmount * 2) - flickerAmount;
            }
        }
    }

    /** Radial gradient cutout — soft edge so darkness fades naturally. */
    private void drawLightCircle(Graphics2D g, int cx, int cy, int radius) {
        // Use a radial gradient: fully transparent at centre → fully opaque at edge
        // (DST_OUT makes opaque = cut out, so centre becomes visible)
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point(cx, cy),
                radius,
                new float[]{0.0f, 0.65f, 1.0f},
                new Color[]{
                        new Color(0, 0, 0, 255),  // full cut-out at centre
                        new Color(0, 0, 0, 220),  // mostly cut-out mid-range
                        new Color(0, 0, 0, 0)     // fades to nothing at edge
                });
        g.setPaint(rgp);
        g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    /** Warm glow ring blended at the boundary of the light. */
    private void drawGlowRing(Graphics2D g, int cx, int cy, int radius) {
        int glowRadius = (int)(radius * 1.15f);
        RadialGradientPaint glow = new RadialGradientPaint(
                new Point(cx, cy),
                glowRadius,
                new float[]{0.0f, 0.55f, 1.0f},
                new Color[]{
                        new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 0),
                        new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), glowColor.getAlpha() / 2),
                        new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 0)
                });
        g.setPaint(glow);
        g.fillOval(cx - glowRadius, cy - glowRadius, glowRadius * 2, glowRadius * 2);
    }
}
