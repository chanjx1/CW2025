package com.comp2042.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScoreManager {

    private static final String FILE_NAME = "highscore.dat";
    private int highScore;

    public ScoreManager() {
        this.highScore = loadHighScore();
    }

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

    public int getHighScore() {
        return highScore;
    }

    public boolean isNewHighScore(int score) {
        return score > highScore;
    }
}