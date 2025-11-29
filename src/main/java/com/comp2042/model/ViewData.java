package com.comp2042.model;

/**
 * An immutable snapshot of the game state sent to the View for rendering.
 * <p>
 * This class decouples the Model from the View by providing a safe copy of the data
 * needed to draw a single frame (active brick, position, and next piece preview).
 * </p>
 */
public final class ViewData {

    /** The shape matrix of the currently falling brick. */
    private final int[][] brickData;

    /** The current x-coordinate (column) of the falling brick. */
    private final int xPosition;

    /** The current y-coordinate (row) of the falling brick. */
    private final int yPosition;

    /** The shape matrix of the next brick in the queue (for preview). */
    private final int[][] nextBrickData;

    /**
     * Constructs a new ViewData snapshot.
     *
     * @param brickData     The matrix of the active brick.
     * @param xPosition     The active brick's column.
     * @param yPosition     The active brick's row.
     * @param nextBrickData The matrix of the next brick.
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
    }

    /**
     * Gets the active brick's shape.
     *
     * @return A copy of the 2D array.
     */
    public int[][] getBrickData() {
        return MatrixUtils.copy(brickData);
    }

    /**
     * Gets the x-coordinate.
     *
     * @return The column index.
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the y-coordinate.
     *
     * @return The row index.
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets the preview brick's shape.
     *
     * @return A copy of the 2D array for the next piece.
     */
    public int[][] getNextBrickData() {
        return MatrixUtils.copy(nextBrickData);
    }
}
