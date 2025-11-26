package com.comp2042.model;

import com.comp2042.model.bricks.Brick;
import com.comp2042.model.bricks.BrickGenerator;
import com.comp2042.model.bricks.BrickRotator;
import com.comp2042.model.bricks.RandomBrickGenerator;

import java.awt.*;

/**
 * Core model of the Tetris game board.
 *
 * The board uses a couple of "hidden" rows at the top (HIDDEN_ROWS) where new
 * bricks spawn. These rows are not drawn in the UI, but they allow pieces to
 * appear smoothly from above the visible playfield.
 */
public class TetrisBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick holdBrick = null;
    private boolean holdUsedThisTurn = false;

    // Board configuration (logical size in cells)
    public static final int BOARD_HEIGHT = 25;
    public static final int BOARD_WIDTH  = 10;

    /**
     * Number of hidden rows at the top of the board.
     * These rows are used for spawning bricks and are not drawn in the UI.
     */
    public static final int HIDDEN_ROWS  = 2;

    // Spawn position for new bricks (measured in board coordinates)
    private static final int SPAWN_X = 4;
    private static final int SPAWN_Y = HIDDEN_ROWS;

    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixUtils.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    /**
     * Creates a new random brick and positions it at the spawn location.
     *
     * @return true if the new brick immediately intersects existing blocks
     *         (i.e. there is no space to spawn -> game over condition), false otherwise.
     */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(SPAWN_X, SPAWN_Y);
        holdUsedThisTurn = false;
        return MatrixUtils.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixUtils.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixUtils.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[BOARD_HEIGHT][BOARD_WIDTH];
        score.reset();
        createNewBrick();
    }

    @Override
    public boolean holdCurrentBrick() {
        // Only allow one hold per falling piece
        if (holdUsedThisTurn) {
            return false;
        }
        holdUsedThisTurn = true;

        // Get the currently active brick from the rotator
        Brick currentBrick = brickRotator.getBrick();  // we'll add this in BrickRotator

        // First time holding: store current brick, spawn a fresh one
        if (holdBrick == null) {
            holdBrick = currentBrick;
            // Spawn a new random brick as the active one
            return createNewBrick();  // true if new brick immediately collides (game over)
        }

        // Subsequent holds: swap current with held
        Brick temp = holdBrick;
        holdBrick = currentBrick;

        // Make the held brick the new active brick
        brickRotator.setBrick(temp);

        // Reset position for the new active brick at the spawn location
        currentOffset = new Point(SPAWN_X, SPAWN_Y);

        // Check if the swapped-in brick immediately collides (rare, but consistent)
        return MatrixUtils.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
    }

    @Override
    public int[][] getHoldBrickShape() {
        if (holdBrick == null) {
            return null;
        }
        // Use the default orientation (index 0) for the preview
        return holdBrick.getShapeMatrix().get(0);
    }
}
