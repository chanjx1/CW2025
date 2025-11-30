package com.comp2042.view;

/**
 * Enumeration representing the possible states of the game lifecycle.
 * <p>
 * This enum is used by the {@link GuiController} and {@link com.comp2042.controller.GameController}
 * to manage the game flow, handle pause/resume logic, and determine when to accept user input.
 * </p>
 */
public enum GameState {

    /**
     * The game is currently active.
     * The timer is running, blocks are falling, and user input is fully enabled.
     */
    RUNNING,

    /**
     * The game is temporarily suspended.
     * The timer is stopped, the pause menu is displayed, and most user input is disabled (except resuming).
     */
    PAUSED,

    /**
     * The game has ended (e.g., blocks reached the top).
     * The timer is stopped, the Game Over screen is shown, and the game awaits a reset.
     */
    GAME_OVER
}