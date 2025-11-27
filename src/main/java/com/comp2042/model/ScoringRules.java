package com.comp2042.model;

public class ScoringRules {

    private ScoringRules() {
        // utility class
    }

    /**
     * Bonus for clearing lines.
     * Formula: (50 * lines^2) * currentLevel
     * Example: Clearing 4 lines (Tetris) at Level 1 = 800 pts.
     * Clearing 4 lines at Level 10 = 8000 pts.
     */
    public static int lineClearBonus(int linesRemoved, int currentLevel) {
        if (linesRemoved <= 0) {
            return 0;
        }
        int baseScore = 50 * linesRemoved * linesRemoved;
        return baseScore * currentLevel;
    }
}
