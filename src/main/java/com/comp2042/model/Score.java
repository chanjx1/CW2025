package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty lines = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty linesProperty() {
        return lines;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public void addScore(int amount) {
        score.set(score.get() + amount);
    }

    public void addLines(int count) {
        lines.set(lines.get() + count);
        // Level up every 10 lines
        int newLevel = (lines.get() / 10) + 1;
        if (newLevel > level.get()) {
            level.set(newLevel);
        }
    }

    public void reset() {
        score.set(0);
        lines.set(0);
        level.set(1);
    }
}