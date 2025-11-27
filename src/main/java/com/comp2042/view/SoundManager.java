package com.comp2042.view;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private final Map<String, AudioClip> soundEffects = new HashMap<>();

    public SoundManager() {
        // Pre-load sounds to avoid lag during gameplay
        loadSound("clear", "sounds/clear.mp3");
        loadSound("levelup", "sounds/levelup.mp3");
        loadSound("gameover", "sounds/gameover.mp3");
    }

    private void loadSound(String name, String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource != null) {
            soundEffects.put(name, new AudioClip(resource.toExternalForm()));
        } else {
            System.err.println("Warning: Sound file not found: " + path);
        }
    }

    public void playClearLine() {
        playSound("clear");
    }

    public void playLevelUp() {
        playSound("levelup");
    }

    public void playGameOver() {
        playSound("gameover");
    }

    private void playSound(String name) {
        AudioClip clip = soundEffects.get(name);
        if (clip != null) {
            clip.play();
        }
    }
}