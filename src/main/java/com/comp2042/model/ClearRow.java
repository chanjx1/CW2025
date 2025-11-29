package com.comp2042.model;

/**
 * Data class representing the result of a row-clearing operation.
 * <p>
 * This class encapsulates the number of lines removed and the new state of the board matrix.
 * It is returned by {@link MatrixUtils#checkRemoving(int[][])}.
 * </p>
 */
public final class ClearRow {

    /** The number of full rows detected and cleared in this step. */
    private final int linesRemoved;

    /** The updated board matrix after rows were removed and blocks shifted down. */
    private final int[][] newMatrix;

    /**
     * Constructs a new ClearRow result.
     *
     * @param linesRemoved The number of full rows detected and cleared.
     * @param newMatrix    The updated board matrix after rows were removed and blocks shifted down.
     */
    public ClearRow(int linesRemoved, int[][] newMatrix) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
    }

    /**
     * Gets the count of lines that were cleared.
     *
     * @return The number of cleared lines (0 to 4).
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets the updated grid matrix.
     *
     * @return A deep copy of the new 2D board array.
     */
    public int[][] getNewMatrix() {
        return MatrixUtils.copy(newMatrix);
    }
}
