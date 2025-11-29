package com.comp2042.model.bricks;

/**
 * Represents the 'L' Tetromino.
 * <p>
 * Defines the specific rotation matrices for the orange L-shaped brick.
 * </p>
 */
public final class LBrick extends AbstractBrick {

    /**
     * Constructs an LBrick with its specific 4x4 rotation states.
     */
    public LBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 3, 3, 3},
                        {0, 3, 0, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 3, 3, 0},
                        {0, 0, 3, 0},
                        {0, 0, 3, 0}
                },
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 0, 3, 0},
                        {3, 3, 3, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 3, 0, 0},
                        {0, 3, 0, 0},
                        {0, 3, 3, 0},
                        {0, 0, 0, 0}
                }
        );
    }
}