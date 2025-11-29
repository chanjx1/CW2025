package com.comp2042.controller.event;

/**
 * Represents the origin of a game event.
 * <p>
 * This enum helps the controller distinguish between actions initiated by the
 * player (keyboard input) and actions initiated by the system (game loop/gravity).
 * </p>
 */
public enum EventSource {
    /** Indicates the event was triggered by the user (e.g., keyboard press). */
    USER,

    /** Indicates the event was triggered by the game thread (e.g., gravity tick). */
    THREAD
}
