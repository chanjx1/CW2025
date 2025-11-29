package com.comp2042.controller.event;

/**
 * An immutable data object representing a movement request.
 * <p>
 * This class encapsulates <i>what</i> happened ({@link EventType}) and
 * <i>who</i> caused it ({@link EventSource}), passing this information from
 * the View to the Controller.
 * </p>
 */
public final class MoveEvent {

    /** The type of movement requested (e.g., LEFT, ROTATE). */
    private final EventType eventType;

    /** The source of the event (USER or THREAD). */
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent.
     *
     * @param eventType   The specific action to perform.
     * @param eventSource The origin of the event.
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Gets the type of movement associated with this event.
     *
     * @return The {@link EventType}.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Gets the source (origin) of this event.
     *
     * @return The {@link EventSource}.
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}
