package com.comp2042.model.bricks;

public final class SBrick extends AbstractBrick {

    public SBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 5, 5, 0},
                        {5, 5, 0, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {5, 0, 0, 0},
                        {5, 5, 0, 0},
                        {0, 5, 0, 0},
                        {0, 0, 0, 0}
                }
        );
    }
}