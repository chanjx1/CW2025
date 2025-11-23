package com.comp2042.model.bricks;


import com.comp2042.model.MatrixUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for all tetromino bricks.
 * Stores the rotation matrices and returns defensive copies.
 */
public abstract class AbstractBrick implements Brick {

    private final List<int[][]> shapeMatrix = new ArrayList<>();

    protected AbstractBrick(int[][]... rotations) {
        shapeMatrix.addAll(Arrays.asList(rotations));
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        // defensive copy so model code can't mutate our internal state
        return MatrixUtils.deepCopyList(shapeMatrix);
    }
}