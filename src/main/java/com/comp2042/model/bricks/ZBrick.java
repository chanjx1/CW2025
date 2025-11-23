package com.comp2042.model.bricks;

public final class ZBrick extends AbstractBrick {

    public ZBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {7, 7, 0, 0},
                        {0, 7, 7, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 7, 0, 0},
                        {7, 7, 0, 0},
                        {7, 0, 0, 0},
                        {0, 0, 0, 0}
                }
        );
    }
}