package main;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class IntroCutscenePlayer {
    private static final long BLACK_HOLD_NANOS = 3_000_000_000L;
    private static final int FRAME_CACHE_SIZE = 48;

    private final String metadataResourcePath;
    private final Path metadataDiskPath;
    private final String audioResourcePath;
    private final Path audioDiskPath;

    private final Map<Integer, BufferedImage> frameCache = new LinkedHashMap<>(FRAME_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, BufferedImage> eldest) {
            return size() > FRAME_CACHE_SIZE;
        }
    };

    private boolean available;
    private boolean finished;
    private boolean playbackStarted;
    private boolean audioStarted;
    private long blackHoldStartedAtNanos;
    private long playbackStartedAtNanos;
    private int currentFrameIndex = -1;
    private BufferedImage currentFrame;
    private Clip audioClip;

    private int frameCount;
    private int framesPerSecond;
    private int frameWidth;
    private int frameHeight;
    private String framesDirectory;
    private String framePattern;

    public IntroCutscenePlayer(float volume) {
        this(volume, "llama");
    }

    /**
     * @param cutsceneId base name matching assets under {@code res/cutscene/{id}_intro_scene/}
     *                   (e.g. llama, gasha, neil, romare).
     */
    public IntroCutscenePlayer(float volume, String cutsceneId) {
        String id = cutsceneId == null ? "" : cutsceneId.trim();
        String folder = id + "_intro_scene";
        String base = id + "_intro";
        metadataResourcePath = "/cutscene/" + folder + "/" + base + ".properties";
        metadataDiskPath = Paths.get("res", "cutscene", folder, base + ".properties");
        audioResourcePath = "/cutscene/" + folder + "/" + base + ".wav";
        audioDiskPath = Paths.get("res", "cutscene", folder, base + ".wav");

        try {
            loadMetadata();
            currentFrame = loadFrame(0);
            currentFrameIndex = currentFrame == null ? -1 : 0;
            loadAudioClip();
            setVolume(volume);
            available = currentFrame != null && frameCount > 0 && framesPerSecond > 0;
        } catch (Exception e) {
            available = false;
            System.err.println("Failed to initialize intro cutscene: " + id);
            e.printStackTrace();
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void start() {
        blackHoldStartedAtNanos = System.nanoTime();
        playbackStartedAtNanos = 0L;
        playbackStarted = false;
        audioStarted = false;
        finished = !available;

        if (audioClip != null) {
            audioClip.stop();
            audioClip.setFramePosition(0);
        }
    }

    public void update() {
        if (finished || !available) {
            finished = true;
            return;
        }

        long now = System.nanoTime();
        if (!playbackStarted) {
            if (now - blackHoldStartedAtNanos < BLACK_HOLD_NANOS) {
                return;
            }

            playbackStarted = true;
            playbackStartedAtNanos = now;
            startAudio();
        }

        int nextFrameIndex = (int) (((now - playbackStartedAtNanos) * framesPerSecond) / 1_000_000_000L);
        if (nextFrameIndex >= frameCount) {
            finished = true;
            stopAudio();
            return;
        }

        if (nextFrameIndex != currentFrameIndex) {
            BufferedImage nextFrame = loadFrame(nextFrameIndex);
            if (nextFrame == null) {
                finished = true;
                stopAudio();
                return;
            }

            currentFrame = nextFrame;
            currentFrameIndex = nextFrameIndex;
        }
    }

    public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        if (!playbackStarted || currentFrame == null) {
            return;
        }

        int integerScale = Math.max(1, Math.min(screenWidth / frameWidth, screenHeight / frameHeight));
        int drawWidth = frameWidth * integerScale;
        int drawHeight = frameHeight * integerScale;
        int drawX = (screenWidth - drawWidth) / 2;
        int drawY = (screenHeight - drawHeight) / 2;

        RenderingHints oldHints = g2.getRenderingHints();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2.drawImage(currentFrame, drawX, drawY, drawWidth, drawHeight, null);
        g2.setRenderingHints(oldHints);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setVolume(float volume) {
        if (audioClip == null || !audioClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }

        FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
        float clampedVolume = Math.max(0.0001f, Math.min(1f, volume));
        float decibels = (float) (20.0 * Math.log10(clampedVolume));
        decibels = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), decibels));
        gainControl.setValue(decibels);
    }

    public void dispose() {
        stopAudio();
        if (audioClip != null) {
            audioClip.close();
            audioClip = null;
        }

        frameCache.clear();
        currentFrame = null;
    }

    private void loadMetadata() throws IOException {
        Properties properties = new Properties();
        try (InputStream stream = openResourceStream(metadataResourcePath, metadataDiskPath)) {
            if (stream == null) {
                throw new IOException("Missing cutscene metadata: " + metadataResourcePath);
            }
            properties.load(stream);
        }

        frameCount = Integer.parseInt(properties.getProperty("frameCount"));
        framesPerSecond = Integer.parseInt(properties.getProperty("fps"));
        frameWidth = Integer.parseInt(properties.getProperty("frameWidth"));
        frameHeight = Integer.parseInt(properties.getProperty("frameHeight"));
        framesDirectory = properties.getProperty("framesDirectory");
        framePattern = properties.getProperty("framePattern");
    }

    private void loadAudioClip() {
        try {
            AudioInputStream audioInputStream = null;
            URL resourceUrl = getClass().getResource(audioResourcePath);
            if (resourceUrl != null) {
                audioInputStream = AudioSystem.getAudioInputStream(resourceUrl);
            } else if (Files.exists(audioDiskPath)) {
                audioInputStream = AudioSystem.getAudioInputStream(audioDiskPath.toFile());
            }

            if (audioInputStream == null) {
                return;
            }

            audioClip = AudioSystem.getClip();
            audioClip.open(audioInputStream);
            audioInputStream.close();
        } catch (Exception e) {
            audioClip = null;
            System.err.println("Failed to load intro cutscene audio.");
            e.printStackTrace();
        }
    }

    private void startAudio() {
        if (audioStarted || audioClip == null) {
            return;
        }

        audioClip.setFramePosition(0);
        audioClip.start();
        audioStarted = true;
    }

    private void stopAudio() {
        if (audioClip == null) {
            return;
        }

        audioClip.stop();
        audioClip.setFramePosition(0);
        audioStarted = false;
    }

    private BufferedImage loadFrame(int frameIndex) {
        BufferedImage cachedFrame = frameCache.get(frameIndex);
        if (cachedFrame != null) {
            return cachedFrame;
        }

        String frameFileName = String.format(framePattern, frameIndex);
        String resourcePath = "/" + framesDirectory + "/" + frameFileName;
        Path diskPath = Paths.get("res").resolve(framesDirectory).resolve(frameFileName);

        try {
            BufferedImage loadedFrame = readImage(resourcePath, diskPath);
            if (loadedFrame != null) {
                frameCache.put(frameIndex, loadedFrame);
            }
            return loadedFrame;
        } catch (IOException e) {
            System.err.println("Failed to load cutscene frame: " + frameFileName);
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage readImage(String resourcePath, Path diskPath) throws IOException {
        URL resourceUrl = getClass().getResource(resourcePath);
        if (resourceUrl != null) {
            return ImageIO.read(resourceUrl);
        }

        if (Files.exists(diskPath)) {
            return ImageIO.read(diskPath.toFile());
        }

        return null;
    }

    private InputStream openResourceStream(String resourcePath, Path diskPath) throws IOException {
        InputStream classpathStream = getClass().getResourceAsStream(resourcePath);
        if (classpathStream != null) {
            return classpathStream;
        }

        if (Files.exists(diskPath)) {
            return Files.newInputStream(diskPath);
        }

        return null;
    }
}
