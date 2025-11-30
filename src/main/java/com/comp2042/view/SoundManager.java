package com.comp2042.view;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Facade for managing audio resources and playback.
 * <p>
 * This class handles loading sound files from the resources folder, caching them
 * as JavaFX {@link AudioClip} objects to minimize latency, and providing a simple
 * interface for the Controller to trigger game sound effects.
 * </p>
 */
public class SoundManager {

    /**
     * A cache mapping sound names (keys) to their loaded AudioClip resources.
     * Used to prevent reloading the same file from disk multiple times.
     */
    private final Map<String, AudioClip> soundEffects = new HashMap<>();

    /**
     * Initializes the sound manager and pre-loads all game sound effects.
     * <p>
     * Sounds are loaded immediately upon game start to ensure they are ready
     * when needed during gameplay.
     * </p>
     */
    public SoundManager() {
        // Pre-load sounds to avoid lag during gameplay
        loadSound("clear", "sounds/clear.wav");
        loadSound("levelup", "sounds/levelup.wav");
        loadSound("gameover", "sounds/gameover.wav");
    }

    /**
     * Loads a sound file from the resource path and caches it.
     * <p>
     * <b>Performance Note:</b> This method plays the clip at volume 0.0 immediately
     * after loading. This forces the JavaFX media engine to decode and cache the
     * audio data in memory, preventing the "first-play lag" often seen in JavaFX applications.
     * </p>
     *
     * @param name The internal key name used to reference this sound.
     * @param path The relative path to the resource file (e.g., "sounds/clear.wav").
     */
    private void loadSound(String name, String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource != null) {
            AudioClip clip = new AudioClip(resource.toExternalForm());
            // Pre-warm the audio engine
            clip.play(0.0);
            soundEffects.put(name, clip);
        } else {
            System.err.println("Warning: Sound file not found: " + path);
        }
    }

    /**
     * Plays the sound effect associated with clearing a line.
     */
    public void playClearLine() {
        playSound("clear");
    }

    /**
     * Plays the sound effect associated with leveling up.
     */
    public void playLevelUp() {
        playSound("levelup");
    }

    /**
     * Plays the sound effect associated with the game ending.
     */
    public void playGameOver() {
        playSound("gameover");
    }

    /**
     * Helper method to safely play a sound from the cache.
     *
     * @param name The key name of the sound to play.
     */
    private void playSound(String name) {
        AudioClip clip = soundEffects.get(name);
        if (clip != null) {
            clip.play();
        }
    }
}