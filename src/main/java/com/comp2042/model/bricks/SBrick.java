package com.comp2042.model.bricks;

/**
 * Represents the 'S' Tetromino.
 * <p>
 * Defines the specific rotation matrices for the green S-shaped brick.
 * This brick typically alternates between two visual orientations.
 * </p>
 */
public final class SBrick extends AbstractBrick {

    /**
     * Constructs an SBrick with its specific rotation states.
     */
    public SBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 5, 5, 0},
                        {5, 5, 0, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {5, 0, 0, 0},
                        {5, 5, 0, 0},
                        {0, 5, 0, 0},
                        {0, 0, 0, 0}
                }
        );
    }
}