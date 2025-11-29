package com.comp2042.model.bricks;


import com.comp2042.model.MatrixUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for all tetromino bricks.
 * <p>
 * Each concrete brick only defines its rotation matrices. Common behaviour such as
 * storing the rotations and returning a defensive copy is handled here, which
 * removes a lot of duplication from the individual brick classes.
 * </p>
 */
public abstract class AbstractBrick implements Brick {

    /**
     * Stores the sequence of 2D matrices representing the brick's rotation states.
     * Index 0 is the default rotation, Index 1 is 90 degrees, etc.
     */
    private final List<int[][]> shapeMatrix = new ArrayList<>();

    /**
     * Constructs a new brick with the specified rotation matrices.
     *
     * @param rotations Variable arguments of 2D arrays, where each array represents
     * a distinct rotation state of the tetromino.
     */
    protected AbstractBrick(int[][]... rotations) {
        shapeMatrix.addAll(Arrays.asList(rotations));
    }

    /**
     * Retrieves the list of rotation matrices for this brick.
     * <p>
     * Returns a deep copy of the list to ensure the internal state of the brick
     * cannot be mutated by external classes.
     * </p>
     *
     * @return A list of 2D integer arrays representing the shape rotations.
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        // defensive copy so model code can't mutate our internal state
        return MatrixUtils.deepCopyList(shapeMatrix);
    }
}