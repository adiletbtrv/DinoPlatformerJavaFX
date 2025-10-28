package com.example.platformer;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundManager {
    private static final SoundManager INSTANCE = new SoundManager();

    private AudioClip pickupClip;
    private AudioClip victoryClip;
    private MediaPlayer bgm;
    private boolean initialized = false;

    private SoundManager() {}

    public static SoundManager getInstance() { return INSTANCE; }

    public void init(Class<?> context) {
        if (initialized) return;
        initialized = true;
        try {
            URL pickupUrl = context.getResource("/com/example/platformer/assets/sfx/pickup.wav");
            if (pickupUrl != null) pickupClip = new AudioClip(pickupUrl.toExternalForm());
        } catch (Throwable t) { }
        try {
            URL victoryUrl = context.getResource("/com/example/platformer/assets/sfx/victory.mp3");
            if (victoryUrl != null) victoryClip = new AudioClip(victoryUrl.toExternalForm());
        } catch (Throwable t) { }
        try {
            URL musicUrl = context.getResource("/com/example/platformer/assets/sfx/bgm.mp3");
            if (musicUrl != null) {
                Media media = new Media(musicUrl.toExternalForm());
                bgm = new MediaPlayer(media);
                bgm.setCycleCount(MediaPlayer.INDEFINITE);
                bgm.setVolume(0.8);
                bgm.setAutoPlay(true);

            }
        } catch (Throwable t) { }
    }

    public void playPickup() { if (pickupClip != null) pickupClip.play(); }
    public void playVictory() { if (victoryClip != null) victoryClip.play(); }
    public void playBgm() { if (bgm != null) bgm.play(); }
}