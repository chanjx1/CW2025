package com.comp2042.view;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Handles the visual styling of Tetris blocks.
 * Extracts the design details (colors, stroke, arcs) away from the main controller.
 */
public class BrickStyler {

    private static final double ARC_SIZE = 9;
    private static final double STROKE_WIDTH = 1.2;

    public void style(Rectangle rectangle, int colorCode) {
        rectangle.setFill(getFillColor(colorCode));
        rectangle.setArcWidth(ARC_SIZE);
        rectangle.setArcHeight(ARC_SIZE);
        rectangle.setStrokeWidth(STROKE_WIDTH);
        rectangle.setStrokeType(javafx.scene.shape.StrokeType.CENTERED);
    }

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