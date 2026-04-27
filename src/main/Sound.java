package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sound {

    public Clip clip;
    URL soundURL[] = new URL[30];
    private int pausedFrame = 0;
    private boolean wasLooping = false;

    public Sound() {

        soundURL[0] = getClass().getResource("/sound/Menu.wav");
        soundURL[1] = getClass().getResource("/sound/InGame.wav");
        soundURL[2] = getClass().getResource("/sound/General_Feedback.wav");
        soundURL[3] = getClass().getResource("/sound/Item_Collection.wav");
        soundURL[4] = getClass().getResource("/sound/Door_Open.wav");
        soundURL[5] = getClass().getResource("/sound/Walking.wav");
    }

    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        }catch(Exception e) {

        }
    }

    public void play() {

        if (clip == null) return;
        clip.start();
    }

    public void loop() {

        if (clip == null) return;
        wasLooping = true;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip == null) return;
        clip.stop();
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

        if (pausedFrame > 0) {
            clip.setFramePosition(pausedFrame);
        }
        clip.start();
        if (wasLooping) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}
