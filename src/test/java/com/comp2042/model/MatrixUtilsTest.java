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

        assertEquals(2, result.getLinesRemoved(),
                "Should report the number of cleared lines");
    }
}