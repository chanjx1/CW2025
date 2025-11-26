package com.comp2042.controller;

import com.comp2042.controller.event.EventSource;
import com.comp2042.controller.event.MoveEvent;
import com.comp2042.model.*;
import com.comp2042.view.GuiController;

/**
 * GameController acts as the "C" in MVC.
 * It owns the game rules, talks to the Board model, and instructs the GUI what to display.
 * Input events come from GuiController via the InputEventListener interface.
 */
public class GameController implements InputEventListener {

    private Board board = new TetrisBoard(TetrisBoard.BOARD_HEIGHT, TetrisBoard.BOARD_WIDTH);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());

        // initialise HOLD box as empty
        if (board instanceof TetrisBoard) {
            viewGuiController.showHoldPiece(((TetrisBoard) board).getHoldBrickShape());
        }
    }

    /**
     * Handles a single "tick" of the brick moving down.
     * - Moves the current brick down if possible
     * - Merges it into the background when it can no longer move
     * - Clears full rows and updates the score
     * - Creates a new brick, or triggers game over if the board is blocked
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }

        // build DownData, let controller handle clear-row, then return it
        DownData downData = new DownData(clearRow, board.getViewData());
        handleClearRow(downData);
        return downData;
    }

    public DownData onHardDropEvent(MoveEvent event) {
        ClearRow clearRow = null;

        // 1) Drop the piece as far as possible, counting soft-drop score
        boolean canMove;
        do {
            canMove = board.moveBrickDown();
            if (canMove && event.getEventSource() == EventSource.USER) {
                // Same behaviour as holding DOWN: +1 per row
                board.getScore().add(1);
            }
        } while (canMove);

        // 2) Lock piece, clear rows, update score and background
        board.mergeBrickToBackground();
        clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }
        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }
        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        // 3) Build DownData for the view (new brick position, etc.)
        DownData downData = new DownData(clearRow, board.getViewData());
        handleClearRow(downData);
        return downData;
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }


    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    /**
     * Helper used by the GUI to compute the ghost landing position.
     * It checks whether placing the current brick with its top row at
     * newY (in BOARD coordinates) would collide with the board edges
     * or any existing blocks in the background matrix.
     */
    @Override
    public boolean canMoveDown(ViewData brick, int newY) {
        int[][] shape = brick.getBrickData();
        int[][] matrix = board.getBoardMatrix();

        int height = matrix.length;       // rows
        int width  = matrix[0].length;    // columns

        int xPos = brick.getxPosition();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 0) {
                    continue;
                }

                int boardY = newY + i;    // BOARD coordinates
                int boardX = xPos + j;

                // Outside left/right or below bottom => cannot move
                if (boardX < 0 || boardX >= width) {
                    return false;
                }
                if (boardY >= height) {
                    return false;
                }

                // Above the visible area is fine (no collision)
                if (boardY < 0) {
                    continue;
                }

                // Collision with an existing block in the background
                if (matrix[boardY][boardX] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Applies visual feedback for cleared rows.
     * The controller interprets the DownData (model-level information)
     * and then asks the view to show the score bonus. This keeps the GUI
     * free from game-logic decisions.
     */
    private void handleClearRow(DownData downData) {
        if (downData.getClearRow() != null
                && downData.getClearRow().getLinesRemoved() > 0) {
            int bonus = downData.getClearRow().getScoreBonus();
            viewGuiController.showScoreBonus(bonus);
        }
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        TetrisBoard tBoard = (TetrisBoard) board;

        boolean gameOver = tBoard.holdCurrentBrick();

        // update HOLD box
        int[][] holdShape = tBoard.getHoldBrickShape();
        viewGuiController.showHoldPiece(holdShape);

        if (gameOver) {
            viewGuiController.gameOver();
        }

        // active piece has changed (new or swapped), so return its view
        return board.getViewData();
    }
}
