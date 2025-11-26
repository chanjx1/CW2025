package com.comp2042.model;

public class ScoringRules {

    private ScoringRules() {
        // utility class
    }

    /**
     * Bonus for clearing lines in one move.
     * Currently: 50 * linesRemoved^2
     */
    public static int lineClearBonus(int linesRemoved) {
        if (linesRemoved <= 0) {
            return 0;
        }
        return 50 * linesRemoved * linesRemoved;
    }
}
