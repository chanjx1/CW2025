package com.comp2042.model.bricks;

/**
 * Represents the 'Z' Tetromino.
 * <p>
 * Defines the specific rotation matrices for the red Z-shaped brick.
 * This brick typically alternates between two visual orientations.
 * </p>
 */
public final class ZBrick extends AbstractBrick {

    /**
     * Constructs a ZBrick with its specific rotation states.
     */
    public ZBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {7, 7, 0, 0},
                        {0, 7, 7, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 7, 0, 0},
                        {7, 7, 0, 0},
                        {7, 0, 0, 0},
                        {0, 0, 0, 0}
                }
        );
    }
}