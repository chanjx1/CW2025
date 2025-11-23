package com.comp2042.model.bricks;

public final class IBrick extends AbstractBrick {

    public IBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {1, 1, 1, 1},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 0, 1, 0},
                        {0, 0, 1, 0},
                        {0, 0, 1, 0},
                        {0, 0, 1, 0}
                }
        );
    }
}