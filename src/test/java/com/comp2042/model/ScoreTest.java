package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreTest {

    @Test
    void addIncreasesScore() {
        Score score = new Score();

        score.add(10);
        score.add(5);

        assertEquals(15, score.scoreProperty().get(),
                "Score should increase by the amount added");
    }

    @Test
    void resetSetsScoreBackToZero() {
        Score score = new Score();
        score.add(42);

        score.reset();

        assertEquals(0, score.scoreProperty().get(),
                "Score.reset() should set score back to zero");
    }
}