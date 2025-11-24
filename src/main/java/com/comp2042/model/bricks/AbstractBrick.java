package com.comp2042.model.bricks;


import com.comp2042.model.MatrixUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for all tetromino bricks.
 * Each concrete brick only defines its rotation matrices. Common behaviour such as
 * storing the rotations and returning a defensive copy is handled here, which
 * removes a lot of duplication from the individual brick classes.
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