package com.comp2042.model.bricks;

/**
 * Strategy interface for generating Tetromino bricks.
 * <p>
 * Implementations define how the sequence of bricks is determined (e.g., pure random,
 * or the fairer 7-Bag system).
 * </p>
 */
public interface BrickGenerator {

    /**
     * Generates and returns the next brick in the sequence.
     * This method advances the generator state (consumes the brick).
     *
     * @return The next {@link Brick} for the board.
     */
    Brick getBrick();

    /**
     * Previews the next brick without advancing the generator state.
     * Used for UI previews.
     *
     * @return The next {@link Brick} that will be generated.
     */
    Brick getNextBrick();
}
