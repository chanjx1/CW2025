package com.comp2042.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages data persistence for high scores.
 * <p>
 * This class handles reading from and writing to the local 'highscore.dat' file.
 * It provides a simple API to save new records and retrieve the current best score.
 * </p>
 */
public class ScoreManager {

    /** The name of the file used for persistence. */
    private static final String FILE_NAME = "highscore.dat";

    /** The cached high score value loaded from disk. */
    private int highScore;

    /**
     * Initializes the manager and attempts to load the existing high score.
     */
    public ScoreManager() {
        this.highScore = loadHighScore();
    }

    /**
     * Reads the high score from the local file system.
     * <p>
     * If the file does not exist or is corrupted, it defaults to 0.
     * </p>
     *
     * @return The saved high score, or 0 if none exists.
     */
    private int loadHighScore() {
        Path path = Paths.get(FILE_NAME);
        if (!Files.exists(path)) {
            return 0;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to load high score: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Saves a new score to the file system if it exceeds the current high score.
     *
     * @param score The new score achieved by the player.
     */
    public void saveHighScore(int score) {
        if (score > highScore) {
            this.highScore = score;
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
                writer.write(String.valueOf(highScore));
            } catch (IOException e) {
                System.err.println("Failed to save high score: " + e.getMessage());
            }
        }
    }

    /**
     * Retrieves the current highest score.
     *
     * @return The high score.
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Checks if a given score beats the current record.
     *
     * @param score The score to check.
     * @return true if the score is greater than the current high score.
     */
    public boolean isNewHighScore(int score) {
        return score > highScore;
    }
}