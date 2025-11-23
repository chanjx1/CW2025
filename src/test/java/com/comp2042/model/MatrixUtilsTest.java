package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MatrixUtilsTest {

    @Test
    void checkRemoving_NoFullRows_ReturnsZeroLinesRemoved() {
        int[][] board = {
                {0, 0, 0},
                {1, 0, 1},
                {1, 1, 0}
        };

        ClearRow result = MatrixUtils.checkRemoving(board);

        assertEquals(0, result.getLinesRemoved(),
                "No complete rows should be removed");
        assertEquals(0, result.getScoreBonus(),
                "No score bonus when no rows are cleared");

        // Matrix should be unchanged
        assertArrayEquals(board, result.getNewMatrix(),
                "Matrix should be unchanged if nothing is cleared");
    }

    @Test
    void checkRemoving_SingleFullRow_RemovesRowAndGivesCorrectBonus() {
        int[][] board = {
                {0, 0, 0},
                {1, 1, 1},  // full row
                {0, 1, 0}
        };

        ClearRow result = MatrixUtils.checkRemoving(board);

        assertEquals(1, result.getLinesRemoved());
        assertEquals(50, result.getScoreBonus(),
                "Score bonus should be 50 * n^2 for n cleared rows");

        int[][] expected = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 1, 0}
        };
        assertArrayEquals(expected, result.getNewMatrix(),
                "Cleared row should disappear and rows above should fall down");
    }

    @Test
    void checkRemoving_TwoFullRows_GivesQuadraticBonus() {
        int[][] board = {
                {1, 1, 1},  // full row
                {1, 1, 1},  // full row
                {0, 1, 0}
        };

        ClearRow result = MatrixUtils.checkRemoving(board);

        assertEquals(2, result.getLinesRemoved());
        assertEquals(50 * 2 * 2, result.getScoreBonus(),
                "Bonus should scale with 50 * rows^2 for two rows");
    }
}