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
}
