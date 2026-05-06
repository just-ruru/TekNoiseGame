package main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class VignetteLight {

    // VIGNETTE TUNING:
    // - lightRadius controls how large the character's visible circle is when the room is dark.
    // - darkness controls how dark the room is when the room lights are off.
    // - brokenRoomLightDarkness controls the faint overlay when the room lights are on.
    // - enemyDimAmount controls how much darker the room gets near enemies.
    // - enemyDimSmoothing controls how softly the enemy dim fades toward its target.
    // - flickerAmount, flickerChance, and flickerDuration control the character light flicker.
    private int lightRadius = 75;
    private float darkness = 0.9f;
    private float brokenRoomLightDarkness = 0.4f;
    private float roomLightEnemyDarkness = 0.8f;
    private final Color darkColor = new Color(0, 0, 0);
    private Color glowColor = new Color(255, 200, 100, 80);

    private boolean roomLight = false;
    private boolean lightsActivated = false;
    private boolean flickerEnabled = true;
    private int currentRadius;

    private int flickerTimer = 0;
    private boolean isFlickering = false;
    private int flickerOffset = 0;
    private int flickerAmount = 14;
    private int flickerChance = 3;
    private int flickerDuration = 6;
    private float enemyFlickerTarget = 0f;
    private float enemyFlickerProgress = 0f;
    private float enemyFlickerSmoothing = 0.08f;
    private int enemyFlickerAmountBonus = 28;
    private int enemyFlickerChanceBonus = 32;
    private int enemyFlickerDurationBonus = 5;

    private float enemyDimTarget = 0f;
    private float enemyDimSmoothing = 0.045f;
    private float enemyDimProgress = 0f;
    private float enemyDimAmount = 0.55f;

    private final int screenW;
    private final int screenH;
    private final java.util.Random rand = new java.util.Random();
    private final BufferedImage overlay;

    public VignetteLight(int screenWidth, int screenHeight) {
        this.screenW = screenWidth;
        this.screenH = screenHeight;
        this.currentRadius = lightRadius;
        overlay = new BufferedImage(screenW, screenH, BufferedImage.TYPE_INT_ARGB);
    }

    public void update() {
        updateEnemyProximityDimming();

        if (roomLight && enemyDimTarget <= 0f && enemyDimProgress <= 0.001f) {
            currentRadius = 0;
            return;
        }

        if (flickerEnabled) {
            updateFlicker();
        } else {
            currentRadius = lightRadius;
        }
    }

    public void draw(Graphics2D g2, int playerScreenX, int playerScreenY, int playerWidth, int playerHeight) {
        int cx = playerScreenX + playerWidth / 2;
        int cy = playerScreenY + playerHeight - 3;

        Graphics2D og = overlay.createGraphics();
        og.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        og.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));

        float currentDarkness = roomLight ? brokenRoomLightDarkness : darkness;
        float easedEnemyDim = smoothStep(enemyDimProgress);
        if (easedEnemyDim > 0f) {
            if (roomLight) {
                currentDarkness = lerp(brokenRoomLightDarkness, roomLightEnemyDarkness, easedEnemyDim);
            } else {
                currentDarkness = Math.min(1f, currentDarkness + easedEnemyDim * enemyDimAmount);
            }
        }

        og.setColor(new Color(
                darkColor.getRed(),
                darkColor.getGreen(),
                darkColor.getBlue(),
                Math.round(currentDarkness * 255)));
        og.fillRect(0, 0, screenW, screenH);

        int radiusToDraw = currentRadius;
        if ((!roomLight || easedEnemyDim > 0f) && radiusToDraw > 1) {
            Composite lightComposite = og.getComposite();
            float lightAlpha = roomLight ? easedEnemyDim : 1f;

            og.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, lightAlpha));
            drawLightCircle(og, cx, cy, radiusToDraw);

            og.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, lightAlpha));
            drawGlowRing(og, cx, cy, radiusToDraw);

            og.setComposite(lightComposite);
        }

        og.dispose();

        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2.drawImage(overlay, 0, 0, null);
        g2.setComposite(old);
    }

    public boolean turnLightsOn() {
        if (lightsActivated) {
            return false;
        }

        lightsActivated = true;
        roomLight = true;
        flickerEnabled = true;
        isFlickering = true;
        flickerTimer = 0;
        currentRadius = 60;
        return true;
    }

    public boolean areRoomLightsOn() {
        return roomLight;
    }

    public boolean wereLightsActivated() {
        return lightsActivated;
    }

    public void triggerEnemyProximityDim() {
        setEnemyProximityLevel(1f);
    }

    public void setEnemyNearby(boolean nearby) {
        setEnemyProximityLevel(nearby ? 1f : 0f);
    }

    public void setEnemyProximityLevel(float level) {
        this.enemyDimTarget = Math.max(0f, Math.min(1f, level));
    }

    public void setLightRadius(int radius) {
        this.lightRadius = Math.max(1, radius);
        this.currentRadius = this.lightRadius;
    }

    public void setDarkness(float darkness) {
        this.darkness = Math.max(0f, Math.min(1f, darkness));
    }

    public void setBrokenRoomLightDarkness(float darkness) {
        this.brokenRoomLightDarkness = Math.max(0f, Math.min(1f, darkness));
    }

    public void setRoomLightEnemyDarkness(float darkness) {
        this.roomLightEnemyDarkness = Math.max(0f, Math.min(1f, darkness));
    }

    public void setGlowColor(Color c) {
        this.glowColor = c;
    }

    public void setFlicker(boolean enabled) {
        this.flickerEnabled = enabled;
    }

    public void setFlickerAmount(int px) {
        this.flickerAmount = Math.max(0, px);
    }

    public void setFlickerChance(int pct) {
        this.flickerChance = Math.max(0, Math.min(100, pct));
    }

    public void setFlickerDuration(int ticks) {
        this.flickerDuration = Math.max(1, ticks);
    }

    public void setEnemyDimAmount(float amount) {
        this.enemyDimAmount = Math.max(0f, Math.min(1f, amount));
    }

    public void setEnemyDimTransitionTicks(int ticks) {
        this.enemyDimSmoothing = Math.max(0.005f, Math.min(1f, 1f / Math.max(1, ticks)));
    }

    private void updateEnemyProximityDimming() {
        enemyDimProgress += (enemyDimTarget - enemyDimProgress) * enemyDimSmoothing;

        if (Math.abs(enemyDimTarget - enemyDimProgress) < 0.001f) {
            enemyDimProgress = enemyDimTarget;
        }

        enemyFlickerProgress += (enemyFlickerTarget - enemyFlickerProgress) * enemyFlickerSmoothing;
        if (Math.abs(enemyFlickerTarget - enemyFlickerProgress) < 0.001f) {
            enemyFlickerProgress = enemyFlickerTarget;
        }
    }

    private float smoothStep(float value) {
        float clamped = Math.max(0f, Math.min(1f, value));
        return clamped * clamped * (3f - 2f * clamped);
    }

    private float lerp(float start, float end, float amount) {
        return start + (end - start) * amount;
    }

    private void updateFlicker() {
        if (isFlickering) {
            flickerTimer--;
            currentRadius = Math.max(1, lightRadius - Math.abs(flickerOffset));

            if (flickerTimer <= 0) {
                isFlickering = false;
                flickerOffset = 0;
                currentRadius = lightRadius;
            }
            return;
        }

        currentRadius = lightRadius;
        int effectiveFlickerChance = Math.min(100, flickerChance + Math.round(enemyFlickerChanceBonus * enemyFlickerProgress));
        int effectiveFlickerAmount = flickerAmount + Math.round(enemyFlickerAmountBonus * enemyFlickerProgress);
        int effectiveFlickerDuration = flickerDuration + Math.round(enemyFlickerDurationBonus * enemyFlickerProgress);

        if (effectiveFlickerAmount > 0 && rand.nextInt(100) < effectiveFlickerChance) {
            isFlickering = true;
            flickerTimer = effectiveFlickerDuration;
            flickerOffset = rand.nextInt(effectiveFlickerAmount * 2 + 1) - effectiveFlickerAmount;
        }
    }

    public void setEnemyFlickerLevel(float level) {
        this.enemyFlickerTarget = Math.max(0f, Math.min(1f, level));
    }

    private void drawLightCircle(Graphics2D g, int cx, int cy, int radius) {
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point(cx, cy),
                radius,
                new float[]{0.0f, 0.65f, 1.0f},
                new Color[]{
                        new Color(0, 0, 0, 255),
                        new Color(0, 0, 0, 220),
                        new Color(0, 0, 0, 0)
                });
        g.setPaint(rgp);
        g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    private void drawGlowRing(Graphics2D g, int cx, int cy, int radius) {
        int glowRadius = (int) (radius * 1.15f);
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
