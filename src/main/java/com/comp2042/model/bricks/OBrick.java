package com.comp2042.model.bricks;

/**
 * Represents the 'O' Tetromino (the Square).
 * <p>
 * Defines the specific rotation matrix for the yellow square brick.
 * Unlike other bricks, the O-piece does not change shape when rotated,
 * so it is defined with a single rotation state.
 * </p>
 */
public final class OBrick extends AbstractBrick {

    /**
     * Constructs an OBrick with its single unique rotation state.
     */
    public OBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 4, 4, 0},
                        {0, 4, 4, 0},
                        {0, 0, 0, 0}
                }
        );
    }
}