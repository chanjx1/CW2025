package com.comp2042.model;

/**
 * Data class representing the result of a downward movement (step or drop).
 * <p>
 * This class aggregates all necessary information for the controller to update the game state,
 * including cleared rows, current view data, and whether the game has ended.
 * </p>
 */
public final class DownData {

    /** Information about any rows cleared during this step (or null/empty if none). */
    private final ClearRow clearRow;

    /** Snapshot of the board state for rendering. */
    private final ViewData viewData;

    /** Flag indicating if the game has ended (e.g., due to spawn collision). */
    private final boolean gameOver;

    /**
     * Constructs a new DownData result.
     *
     * @param clearRow Information about any rows cleared during this step.
     * @param viewData Snapshot of the board state for rendering.
     * @param gameOver True if the game has ended (collision on spawn), false otherwise.
     */
    public DownData(ClearRow clearRow, ViewData viewData, boolean gameOver) {
        this.clearRow = clearRow;
        this.viewData = viewData;
        this.gameOver = gameOver;
    }

    /**
     * Gets the row clearing results.
     *
     * @return The {@link ClearRow} object.
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the visual data for the board.
     *
     * @return The {@link ViewData} object.
     */
    public ViewData getViewData() {
        return viewData;
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }
}