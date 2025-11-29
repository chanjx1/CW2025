package com.comp2042.model;

/**
 * An immutable data record representing a 2D coordinate on the game board.
 * <p>
 * This class replaces the use of {@code java.awt.Point} to remove AWT dependencies
 * from the Model layer, ensuring a cleaner architecture.
 * </p>
 */
public class GamePoint {

    /** The horizontal coordinate (column index). */
    private final int x;

    /** The vertical coordinate (row index). */
    private final int y;

    /**
     * Constructs a new point at the specified coordinates.
     *
     * @param x The column index.
     * @param y The row index.
     */
    public GamePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor.
     * Creates a new point with the same coordinates as the given point.
     *
     * @param other The point to copy.
     */
    public GamePoint(GamePoint other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Gets the x-coordinate (column).
     *
     * @return The column index.
     */
    public int x() {
        return x;
    }

    /**
     * Gets the y-coordinate (row).
     *
     * @return The row index.
     */
    public int y() {
        return y;
    }

    /**
     * Creates a new point offset by the specified amount.
     * <p>
     * This method is immutable; it returns a new instance rather than modifying the current one.
     * </p>
     *
     * @param dx The amount to shift horizontally.
     * @param dy The amount to shift vertically.
     * @return A new {@link GamePoint} representing the translated position.
     */
    public GamePoint translate(int dx, int dy) {
        return new GamePoint(this.x + dx, this.y + dy);
    }
}
