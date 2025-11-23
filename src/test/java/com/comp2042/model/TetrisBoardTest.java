package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TetrisBoardTest {

    @Test
    void createNewBrick_SpawnsAtTopInCenter() {
        TetrisBoard board = new TetrisBoard(
                TetrisBoard.BOARD_HEIGHT,
                TetrisBoard.BOARD_WIDTH
        );

        boolean intersects = board.createNewBrick();  // true if spawn collision

        assertFalse(intersects,
                "On an empty board, first brick should not collide");

        ViewData viewData = board.getViewData();

        assertEquals(4, viewData.getxPosition(),
                "Brick should spawn horizontally near the centre at x=4 (for width 10)");
        assertEquals(TetrisBoard.HIDDEN_ROWS, viewData.getyPosition(),
                "Brick should spawn in the hidden rows just above the visible board");
    }
}
