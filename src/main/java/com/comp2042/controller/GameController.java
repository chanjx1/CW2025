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

    /** The main game board model containing the grid and physics logic. */
    private Board board = new TetrisBoard(TetrisBoard.BOARD_HEIGHT, TetrisBoard.BOARD_WIDTH);

    /** The view controller responsible for rendering the game state. */
    private final GuiController viewGuiController;

    /** Facade for managing and playing audio effects. */
    private final SoundManager soundManager;

    /** Manager for handling high score persistence (saving/loading). */
    private final ScoreManager scoreManager;

    /**
     * Initializes the game controller, sets up dependencies, and binds the view to the model.
     *
     * @param c The GUI controller instance used to interact with the JavaFX view.
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        this.soundManager = new SoundManager();
        this.scoreManager = new ScoreManager();
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.setHighScore(scoreManager.getHighScore());

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

    /**
     * Handles the hard-drop event (instantly dropping the piece to the bottom).
     * <p>
     * Awards a fixed score bonus for hard dropping and immediately locks the piece.
     * </p>
     *
     * @param event The move event triggering this action.
     * @return The result of the drop (clear rows, game over status).
     */
    public DownData onHardDropEvent(MoveEvent event) {
        boolean fromUser = event.getEventSource() == EventSource.USER;
        DownData downData = board.hardDrop(fromUser);

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

    /**
     * Handles the "Move Left" event.
     *
     * @param event The input event.
     * @return The updated view data after attempting the move.
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles the "Move Right" event.
     *
     * @param event The input event.
     * @return The updated view data after attempting the move.
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles the "Rotate" event.
     *
     * @param event The input event.
     * @return The updated view data after attempting the rotation.
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Starts a new game session.
     * <p>
     * Resets the board, score, and view, and spawns the first brick.
     * Also updates the high score display.
     * </p>
     */
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
     * Checks if the active brick can move to the specified Y-coordinate.
     * <p>
     * Used mainly for calculating the "Ghost Piece" position.
     * </p>
     *
     * @param brick The current view data of the brick.
     * @param newY The target Y-coordinate on the board.
     * @return true if the move is valid (no collision), false otherwise.
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

    /**
     * Handles the "Hold Piece" event (swapping active with held).
     *
     * @param event The input event.
     * @return The updated view data (the new active piece).
     */
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

    /**
     * Helper method to centralize game-over logic.
     * <p>
     * Stops the game loop, plays the game over sound, and checks if a new high score was achieved.
     * </p>
     */
    private void handleGameOver() {
        viewGuiController.gameOver();
        soundManager.playGameOver();

        // Check and Save High Score
        int currentScore = board.getScore().scoreProperty().get();
        if (scoreManager.isNewHighScore(currentScore)) {
            scoreManager.saveHighScore(currentScore);
            viewGuiController.showScoreBonus("NEW HIGH SCORE!");
            viewGuiController.setHighScore(scoreManager.getHighScore());
        }
    }
}
