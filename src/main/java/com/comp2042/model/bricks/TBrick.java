package com.comp2042.model.bricks;

/**
 * Represents the 'T' Tetromino.
 * <p>
 * Defines the specific rotation matrices for the purple T-shaped brick.
 * </p>
 */
public final class TBrick extends AbstractBrick {

    /**
     * Constructs a TBrick with its specific 4x4 rotation states.
     */
    public TBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {6, 6, 6, 0},
                        {0, 6, 0, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 6, 0, 0},
                        {0, 6, 6, 0},
                        {0, 6, 0, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 6, 0, 0},
                        {6, 6, 6, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 6, 0, 0},
                        {6, 6, 0, 0},
                        {0, 6, 0, 0},
                        {0, 0, 0, 0}
                }
        );
    }
}