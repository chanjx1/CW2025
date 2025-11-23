package com.comp2042.model.bricks;

public final class JBrick extends AbstractBrick {

    public JBrick() {
        super(
                new int[][]{
                        {0, 0, 0, 0},
                        {2, 2, 2, 0},
                        {0, 0, 2, 0},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 2, 2, 0},
                        {0, 2, 0, 0},
                        {0, 2, 0, 0}
                },
                new int[][]{
                        {0, 0, 0, 0},
                        {0, 2, 0, 0},
                        {0, 2, 2, 2},
                        {0, 0, 0, 0}
                },
                new int[][]{
                        {0, 0, 2, 0},
                        {0, 0, 2, 0},
                        {0, 2, 2, 0},
                        {0, 0, 0, 0}
                }
        );
    }
}