package com.comp2042.model;

/**
 * Immutable data holder for the result of a rotation calculation.
 * <p>
 * This class stores the matrix of the "next" shape state and its rotation index,
 * allowing the board to validate the rotation before applying it.
 * </p>
 */
public final class NextShapeInfo {

    /** The 2D matrix representing the rotated shape. */
    private final int[][] shape;

    /** The index of this rotation state (0, 1, 2, or 3). */
    private final int position;

    /**
     * Constructs a new info object.
     *
     * @param shape    The shape matrix.
     * @param position The rotation index.
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets a copy of the shape matrix.
     *
     * @return A deep copy of the 2D integer array.
     */
    public int[][] getShape() {
        return MatrixUtils.copy(shape);
    }

    /**
     * Gets the rotation position index.
     *
     * @return The index.
     */
    public int getPosition() {
        return position;
    }
}
