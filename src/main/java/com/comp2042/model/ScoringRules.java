package com.comp2042.model;

/**
 * Utility class defining the game's scoring logic.
 * <p>
 * Centralizes the formulas used to calculate points based on game events
 * like line clears and level multipliers.
 * </p>
 */
public class ScoringRules {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ScoringRules() {
        // utility class
    }

    /**
     * Calculates the score bonus for clearing lines.
     * <p>
     * Formula: {@code (50 * lines^2) * currentLevel}
     * </p>
     * Examples:
     * <ul>
     * <li>Clearing 1 line at Level 1 = 50 pts.</li>
     * <li>Clearing 4 lines (Tetris) at Level 1 = 800 pts.</li>
     * <li>Clearing 4 lines at Level 10 = 8000 pts.</li>
     * </ul>
     *
     * @param linesRemoved The number of lines cleared simultaneously.
     * @param currentLevel The current game level multiplier.
     * @return The calculated score bonus.
     */
    public static int lineClearBonus(int linesRemoved, int currentLevel) {
        if (linesRemoved <= 0) {
            return 0;
        }
        int baseScore = 50 * linesRemoved * linesRemoved;
        return baseScore * currentLevel;
    }
}
