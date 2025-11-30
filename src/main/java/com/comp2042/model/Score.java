package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Represents the dynamic score state of the current game session.
 * <p>
 * This class uses JavaFX Properties to store the score, lines cleared, and current level.
 * </p>
 */
public final class Score {

    /** The current accumulated score points. */
    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /** The total number of lines cleared in this session. */
    private final IntegerProperty lines = new SimpleIntegerProperty(0);

    /** The current difficulty level (starts at 1). */
    private final IntegerProperty level = new SimpleIntegerProperty(1);

    /**
     * Creates a new Score instance with default values.
     */
    public Score() {
        // Default constructor
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty linesProperty() {
        return lines;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    /**
     * Adds points to the current score.
     *
     * @param amount The number of points to add.
     */
    public void addScore(int amount) {
        score.set(score.get() + amount);
    }

    /**
     * Adds to the total line count and checks for level advancement.
     * <p>
     * The level increases by 1 for every 10 lines cleared.
     * </p>
     *
     * @param count The number of lines recently cleared.
     */
    public void addLines(int count) {
        lines.set(lines.get() + count);
        // Level up every 10 lines
        int newLevel = (lines.get() / 10) + 1;
        if (newLevel > level.get()) {
            level.set(newLevel);
        }
    }

    /**
     * Resets all stats to their initial values (Score: 0, Lines: 0, Level: 1).
     */
    public void reset() {
        score.set(0);
        lines.set(0);
        level.set(1);
    }
}