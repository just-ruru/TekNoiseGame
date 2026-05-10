package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Sound {

    public Clip clip;
    URL soundURL[] = new URL[30];
    private int pausedFrame = 0;
    private boolean wasLooping = false;
    private float volume = 1f;

    public Sound() {

        soundURL[0] = getClass().getResource("/sound/Menu.wav");
        soundURL[1] = getClass().getResource("/sound/InGame.wav");
        soundURL[2] = getClass().getResource("/sound/General_Feedback.wav");
        soundURL[3] = getClass().getResource("/sound/Item_Collection.wav");
        soundURL[4] = getClass().getResource("/sound/Door_Open.wav");
        soundURL[5] = getClass().getResource("/sound/Walking.wav");
        soundURL[6] = getClass().getResource("/sound/phantomvoice.wav");
        soundURL[7] = getClass().getResource("/sound/damaged.wav");
    }

    public void setFile(int i) {
        try {
            // Close any existing clip to prevent overlapping/memory leaks
            if (clip != null) {
                clip.stop();
                clip.close();
            }
            
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            applyVolume();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {

        if (clip == null) return;
        if (!clip.isOpen()) return;
        clip.start();
    }

    public void loop() {

        if (clip == null) return;
        if (!clip.isOpen()) return;
        wasLooping = true;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip == null) return;
        clip.stop();
        // Do NOT close the clip here, as it breaks re-playing (e.g., walking sound).
        // Only close it when a NEW file is loaded in setFile().
        pausedFrame = 0;
        wasLooping = false;
    }

    public void pause() {
        if (clip == null) return;
        if (!clip.isRunning()) return;

        pausedFrame = clip.getFramePosition();
        clip.stop();
    }

    public void resume() {
        if (clip == null) return;
        if (!clip.isOpen()) return;

        if (pausedFrame > 0) {
            clip.setFramePosition(pausedFrame);
        }
        clip.start();
        if (wasLooping) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
        applyVolume();
    }

    public float getVolume() {
        return volume;
    }

    private void applyVolume() {
        if (clip == null) return;
        if (!clip.isOpen()) return;
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float clampedVolume = Math.max(0.0001f, Math.min(1f, volume));
        float decibels = (float) (20.0 * Math.log10(clampedVolume));
        decibels = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), decibels));
        gainControl.setValue(decibels);
    }
}
