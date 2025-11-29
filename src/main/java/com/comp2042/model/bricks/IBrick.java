package com.comp2042.model.bricks;

/**
 * Represents the 'I' Tetromino (the long bar).
 * <p>
 * Defines the specific rotation matrices for the cyan I-shaped brick.
 * </p>
 */
public final class IBrick extends AbstractBrick {

    /**
     * Constructs an IBrick with its specific 4x4 rotation states.
     */
    public IBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {1, 1, 1, 1},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 0, 1, 0},
                        {0, 0, 1, 0},
                        {0, 0, 1, 0},
                        {0, 0, 1, 0}
                }
        );
    }
}