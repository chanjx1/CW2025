package com.comp2042.view;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Handles the visual styling of Tetris blocks.
 * <p>
 * This class encapsulates the design details (colors, stroke width, corner arcs)
 * to ensure consistent rendering across the board, ghost pieces, and previews.
 * It helps decouple the visual design from the logic in {@link BoardRenderer}.
 * </p>
 */
public class BrickStyler {

    /** The radius of the rounded corners for each block (in pixels). */
    private static final double ARC_SIZE = 9;

    /** The width of the border stroke for each block (in pixels). */
    private static final double STROKE_WIDTH = 1.2;

    /**
     * Default constructor.
     */
    public BrickStyler() {
    }

    /**
     * Applies visual styling to a given rectangle based on the brick type.
     * <p>
     * Sets the fill color, rounded corners, and stroke width.
     * </p>
     *
     * @param rectangle The JavaFX {@link Rectangle} to modify.
     * @param colorCode The integer ID representing the brick type (0 for empty, 1-7 for pieces).
     */
    public void style(Rectangle rectangle, int colorCode) {
        rectangle.setFill(getFillColor(colorCode));
        rectangle.setArcWidth(ARC_SIZE);
        rectangle.setArcHeight(ARC_SIZE);
        rectangle.setStrokeWidth(STROKE_WIDTH);
        rectangle.setStrokeType(javafx.scene.shape.StrokeType.CENTERED);
    }

    /**
     * Maps a numeric brick ID to a specific JavaFX Paint color.
     *
     * @param i The brick ID.
     * @return The corresponding {@link Color} object, or transparent if 0.
     */
    private Paint getFillColor(int i) {
        return switch (i) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.AQUA;
            case 2 -> Color.BLUEVIOLET;
            case 3 -> Color.DARKGREEN;
            case 4 -> Color.YELLOW;
            case 5 -> Color.RED;
            case 6 -> Color.BEIGE;
            case 7 -> Color.BURLYWOOD;
            default -> Color.WHITE;
        };
    }
}