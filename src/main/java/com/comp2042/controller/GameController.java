package com.comp2042.controller;

import com.comp2042.controller.event.EventSource;
import com.comp2042.controller.event.MoveEvent;
import com.comp2042.model.*;
import com.comp2042.view.GuiController;
import com.comp2042.view.SoundManager;

/**
 * The central controller in the MVC architecture.
 * <p>
 * This class orchestrates the game loop by:
 * <ul>
 * <li>Handling input events from the View (via {@link InputEventListener}).</li>
 * <li>Updating the Model ({@link Board}).</li>
 * <li>Managing high-level game rules like Scoring, Leveling, and Audio triggers.</li>
 * </ul>
 */
public class GameController implements InputEventListener {

    private Board board = new TetrisBoard(TetrisBoard.BOARD_HEIGHT, TetrisBoard.BOARD_WIDTH);

    private final GuiController viewGuiController;
    private final SoundManager soundManager;
    private final ScoreManager scoreManager;

    public GameController(GuiController c) {
        viewGuiController = c;
        this.soundManager = new SoundManager();
        this.scoreManager = new ScoreManager();
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.setHighScore(scoreManager.getHighScore());

        // NEW: Bind Score AND Level
        viewGuiController.bindGameStats(board.getScore().scoreProperty(), board.getScore().levelProperty(), board.getScore().linesProperty());

        if (board instanceof TetrisBoard) {
            viewGuiController.showHoldPiece(((TetrisBoard) board).getHoldBrickShape());
        }
    }

    /**
     * Handles the "Tick" event (gravity) or user soft-drop input.
     * <p>
     * Moves the piece down, updates the view, and checks for game-over conditions.
     * It also awards points for manual soft-drops.
     * </p>
     *
     * @param event The move event triggering this action.
     * @return The result of the downward movement (clear rows, game over status).
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean fromUser = event.getEventSource() == EventSource.USER;
        DownData downData = board.stepDown(fromUser);

        // NEW: Manually add score for soft drop if user pressed down and piece moved (no collision yet)
        if (fromUser && downData.getClearRow() == null && !downData.isGameOver()) {
            board.getScore().addScore(1);
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        if (downData.isGameOver()) {
            viewGuiController.gameOver();
            soundManager.playGameOver();
            handleGameOver();
        }
        handleClearRow(downData);
        return downData;
    }

    // For hard drop, we can just give a fixed bonus for simplicity
    public DownData onHardDropEvent(MoveEvent event) {
        boolean fromUser = event.getEventSource() == EventSource.USER;
        DownData downData = board.hardDrop(fromUser);

        // Fixed bonus for hard drop (since we don't calculate exact rows anymore)
        if (fromUser) {
            board.getScore().addScore(20);
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        if (downData.isGameOver()) {
            viewGuiController.gameOver();
            soundManager.playGameOver();
            handleGameOver();
        }
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
        viewGuiController.setHighScore(scoreManager.getHighScore());

        if (board instanceof TetrisBoard) {
            viewGuiController.showHoldPiece(((TetrisBoard) board).getHoldBrickShape());
        }
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
     * Processes scoring and leveling logic when rows are cleared.
     * <p>
     * Calculates the score bonus based on the current level using {@link ScoringRules},
     * updates the {@link Score} model, and triggers visual/audio feedback.
     * </p>
     *
     * @param downData The data returned from the board after a drop operation.
     */
    private void handleClearRow(DownData downData) {
        if (downData.getClearRow() != null
                && downData.getClearRow().getLinesRemoved() > 0) {

            int linesRemoved = downData.getClearRow().getLinesRemoved();
            int currentLevel = board.getScore().levelProperty().get(); // Get Level

            // 1. Calculate Score Bonus using Level Multiplier
            // UPDATE: Pass currentLevel to the rules
            int bonus = ScoringRules.lineClearBonus(linesRemoved, currentLevel);

            board.getScore().addScore(bonus);

            // 2. Update Lines and Level
            int oldLevel = board.getScore().levelProperty().get();
            board.getScore().addLines(linesRemoved);
            int newLevel = board.getScore().levelProperty().get();

            // 3. Audio & Visuals
            if (newLevel > oldLevel) {
                soundManager.playLevelUp();
                viewGuiController.showScoreBonus("LEVEL " + newLevel);
            } else {
                soundManager.playClearLine();
                viewGuiController.showScoreBonus("+" + bonus);
            }
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
            soundManager.playGameOver();
            handleGameOver();
        }

        // active piece has changed (new or swapped), so return its view
        return board.getViewData();
    }

    // Helper method to handle game over logic centrally
    private void handleGameOver() {
        viewGuiController.gameOver();
        soundManager.playGameOver();

        // NEW: Check and Save High Score
        int currentScore = board.getScore().scoreProperty().get();
        if (scoreManager.isNewHighScore(currentScore)) {
            scoreManager.saveHighScore(currentScore);
            viewGuiController.showScoreBonus("NEW HIGH SCORE!");
            viewGuiController.setHighScore(scoreManager.getHighScore());
        }
    }
}
