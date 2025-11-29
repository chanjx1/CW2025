package com.comp2042.model;

/**
 * Interface defining the contract for a Tetris game board.
 * <p>
 * This interface abstracts the game physics and state management, allowing for
 * different implementations of the board logic (e.g., standard Tetris, custom variants, or mock boards for testing).
 * </p>
 */
public interface Board {

    /**
     * Attempts to move the active brick down by one row.
     * @return true if the move was successful, false if blocked.
     */
    boolean moveBrickDown();

    /**
     * Attempts to move the active brick left by one column.
     * @return true if the move was successful, false if blocked.
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the active brick right by one column.
     * @return true if the move was successful, false if blocked.
     */
    boolean moveBrickRight();

    /**
     * Attempts to rotate the active brick.
     * @return true if rotation was successful, false if blocked.
     */
    boolean rotateLeftBrick();

    /**
     * Spawns a new brick at the top of the board.
     * @return true if the spawn location is blocked (Game Over), false otherwise.
     */
    boolean createNewBrick();

    /**
     * Gets the current state of the board grid.
     * @return A 2D integer array representing the board.
     */
    int[][] getBoardMatrix();

    /**
     * Gets the view data required to render the current frame.
     * @return A {@link ViewData} object containing brick positions and shapes.
     */
    ViewData getViewData();

    /**
     * Locks the current active brick into the background grid.
     */
    void mergeBrickToBackground();

    /**
     * Checks for and removes any complete rows.
     * @return A {@link ClearRow} object detailing which lines were removed.
     */
    ClearRow clearRows();

    /**
     * Gets the score object associated with this board.
     * @return The {@link Score} model.
     */
    Score getScore();

    /**
     * Resets the board state for a new game.
     */
    void newGame();

    /**
     * Holds the current brick and swaps it with the stored hold piece.
     * @return true if the swap resulted in an immediate collision (Game Over), false otherwise.
     */
    boolean holdCurrentBrick();

    /**
     * Gets the shape of the currently held brick.
     * @return A 2D integer array of the held brick, or null if empty.
     */
    int[][] getHoldBrickShape();

    /**
     * Advances the game simulation by one step (gravity).
     * @param awardSoftDropScore whether to award points for this drop.
     * @return A {@link DownData} object containing the result of the step.
     */
    DownData stepDown(boolean awardSoftDropScore);

    /**
     * Instantly drops the current brick to the bottom.
     * @param awardSoftDropScore whether to award points for the drop distance.
     * @return A {@link DownData} object containing the result of the drop.
     */
    DownData hardDrop(boolean awardSoftDropScore);
}
