package com.comp2042.model.bricks;

/**
 * Represents the 'J' Tetromino.
 * <p>
 * Defines the specific rotation matrices for the blue J-shaped brick.
 * </p>
 */
public final class JBrick extends AbstractBrick {

    /**
     * Constructs a JBrick with its specific 4x4 rotation states.
     */
    public JBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {2, 2, 2, 0},
                        {0, 0, 2, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 2, 2, 0},
                        {0, 2, 0, 0},
                        {0, 2, 0, 0}
                },
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 2, 0, 0},
                        {0, 2, 2, 2},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 0, 2, 0},
                        {0, 0, 2, 0},
                        {0, 2, 2, 0},
                        {0, 0, 0, 0}
                }
        );
    }
}