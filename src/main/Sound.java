package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sound {

    public Clip clip;
    URL soundURL[] = new URL[30];

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

        clip.start();
    }

    public void loop() {

        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }
}
