package com.comp2042.controller;

import com.comp2042.controller.event.MoveEvent;
import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;

/**
 * Interface for handling user input events from the GUI.
 * <p>
 * This interface defines the contract between the View (GuiController) and the Controller (GameController).
 * The GUI forwards raw input events to the listener, which processes them into game logic actions.
 * </p>
 */
public interface InputEventListener {

    /**
     * Called when the "Down" action is triggered (either by gravity or user soft-drop).
     *
     * @param event The event details containing the source of the action.
     * @return A {@link DownData} object containing the result of the move (e.g., cleared rows, game over).
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Called when the "Hard Drop" action is triggered (instantly dropping the piece).
     *
     * @param event The event details.
     * @return A {@link DownData} object containing the result of the drop.
     */
    DownData onHardDropEvent(MoveEvent event);

    /**
     * Called when the "Left" action is triggered.
     *
     * @param event The event details.
     * @return The updated {@link ViewData} reflecting the new state of the board.
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Called when the "Right" action is triggered.
     *
     * @param event The event details.
     * @return The updated {@link ViewData} reflecting the new state of the board.
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Called when the "Rotate" action is triggered.
     *
     * @param event The event details.
     * @return The updated {@link ViewData} reflecting the rotated piece state.
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Called when the "Hold" action is triggered (swapping active and held pieces).
     *
     * @param event The event details.
     * @return The updated {@link ViewData} reflecting the new active piece.
     */
    ViewData onHoldEvent(MoveEvent event);

    /**
     * Checks if the active brick can move to a specific Y-coordinate without collision.
     * <p>
     * This is primarily used by the View to calculate the "Ghost Piece" position.
     * </p>
     *
     * @param brick The current view data of the brick.
     * @param newY  The target Y-coordinate on the board.
     * @return true if the move is valid, false otherwise.
     */
    boolean canMoveDown(ViewData brick, int newY);

    /**
     * Signals the controller to start a completely new game session.
     * <p>
     * This should reset the score, board, and level state.
     * </p>
     */
    void createNewGame();
}
