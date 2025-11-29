package com.comp2042.model.bricks;

import java.util.List;

/**
 * Interface representing a Tetromino piece.
 * <p>
 * Defines the contract for any object that acts as a game piece.
 * Implementations should provide the shape data for all rotation states.
 * </p>
 */
public interface Brick {

    /**
     * Retrieves the matrix data representing the shape of the brick.
     *
     * @return A list of 2D integer arrays, where each array is a rotation state.
     */
    List<int[][]> getShapeMatrix();
}
