package com.comp2042.model.bricks;

import com.comp2042.model.NextShapeInfo;

/**
 * Manages the rotation state of the active brick.
 * <p>
 * This class tracks the current rotation index of the active {@link Brick} and
 * calculates the next rotation state when requested.
 * </p>
 */
public class BrickRotator {

    /** The current brick being manipulated. */
    private Brick brick;

    /** The index of the current rotation state (0 to 3). */
    private int currentShape = 0;

    /**
     * Calculates the next rotation state for the current brick.
     * <p>
     * This method simulates a rotation to check for collisions before actually
     * applying the change. It cycles through the available rotation matrices.
     * </p>
     *
     * @return A {@link NextShapeInfo} object containing the matrix and index of the next rotation.
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Retrieves the matrix representing the brick's current orientation.
     *
     * @return A 2D integer array representing the current shape.
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation index.
     *
     * @param currentShape The new rotation index (must be valid for the current brick).
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets the active brick to be rotated.
     * <p>
     * Resets the rotation index to 0 (default orientation) whenever a new brick is assigned.
     * </p>
     *
     * @param brick The new {@link Brick} to control.
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    /**
     * Gets the currently active brick.
     *
     * @return The {@link Brick} object.
     */
    public Brick getBrick() {
        return brick;
    }
}
