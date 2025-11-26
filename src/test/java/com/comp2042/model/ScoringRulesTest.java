package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoringRulesTest {

    @Test
    void lineClearBonus_ZeroLines_GivesZero() {
        assertEquals(0, ScoringRules.lineClearBonus(0));
    }

    @Test
    void lineClearBonus_OneLine_GivesBaseBonus() {
        assertEquals(50, ScoringRules.lineClearBonus(1));
    }

    @Test
    void lineClearBonus_TwoLines_GivesQuadraticBonus() {
        assertEquals(50 * 2 * 2, ScoringRules.lineClearBonus(2));
    }
}
