package com.comp2042.controller.event;

/**
 * Enumerates the specific types of movement actions available in the game.
 * <p>
 * Used to categorize {@link MoveEvent}s so the controller knows which logic to apply.
 * </p>
 */
public enum EventType {
    /** Move the brick down by one row (soft drop or gravity). */
    DOWN,

    /** Move the brick one column to the left. */
    LEFT,

    /** Move the brick one column to the right. */
    RIGHT,

    /** Rotate the brick 90 degrees clockwise. */
    ROTATE,

    /** Instantly drop the brick to the bottom (hard drop). */
    HARD_DROP,

    /** Hold the current brick and swap with the stored one. */
    HOLD
}
