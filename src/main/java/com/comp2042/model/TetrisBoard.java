package com.comp2042.model;

import com.comp2042.model.bricks.Brick;
import com.comp2042.model.bricks.BrickGenerator;
import com.comp2042.model.bricks.BrickRotator;
import com.comp2042.model.bricks.RandomBrickGenerator;

// REFACTORING: Removed java.awt.* dependency

/**
 * Core model of the Tetris game board.
 */
public class TetrisBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;

    // REFACTORING: Replaced java.awt.Point with internal GamePoint
    private GamePoint currentOffset;

    private final Score score;
    private Brick holdBrick = null;
    private boolean holdUsedThisTurn = false;

    // Board configuration
    public static final int BOARD_HEIGHT = 25;
    public static final int BOARD_WIDTH  = 10;
    public static final int HIDDEN_ROWS  = 2;

    // Spawn position
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
        // REFACTORING: using immutable GamePoint logic
        GamePoint p = currentOffset.translate(0, 1);

        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), p.x(), p.y());
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
        GamePoint p = currentOffset.translate(-1, 0);

        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), p.x(), p.y());
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
        GamePoint p = currentOffset.translate(1, 0);

        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), p.x(), p.y());
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
        boolean conflict = MatrixUtils.intersect(currentMatrix, nextShape.getShape(), currentOffset.x(), currentOffset.y());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new GamePoint(SPAWN_X, SPAWN_Y);
        holdUsedThisTurn = false;
        return MatrixUtils.intersect(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixUtils.merge(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y());
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
        if (holdUsedThisTurn) {
            return false;
        }
        holdUsedThisTurn = true;

        Brick currentBrick = brickRotator.getBrick();

        if (holdBrick == null) {
            holdBrick = currentBrick;
            return createNewBrick();
        }

        Brick temp = holdBrick;
        holdBrick = currentBrick;
        brickRotator.setBrick(temp);

        currentOffset = new GamePoint(SPAWN_X, SPAWN_Y);

        return MatrixUtils.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                currentOffset.x(),
                currentOffset.y()
        );
    }

    @Override
    public int[][] getHoldBrickShape() {
        if (holdBrick == null) {
            return null;
        }
        return holdBrick.getShapeMatrix().get(0);
    }

    @Override
    public DownData stepDown(boolean awardSoftDropScore) {
        boolean canMove = moveBrickDown();
        ClearRow clearRow = null;
        boolean gameOver = false;

        if (!canMove) {
            mergeBrickToBackground();
            clearRow = clearRows();

            if (clearRow.getLinesRemoved() > 0) {
                int bonus = ScoringRules.lineClearBonus(clearRow.getLinesRemoved());
                score.add(bonus);
            }

            if (createNewBrick()) {
                gameOver = true;
            }

        } else if (awardSoftDropScore) {
            score.add(1);
        }

        return new DownData(clearRow, getViewData(), gameOver);
    }

    @Override
    public DownData hardDrop(boolean awardSoftDropScore) {
        ClearRow clearRow;
        boolean gameOver;
        boolean canMove;
        do {
            canMove = moveBrickDown();
            if (canMove && awardSoftDropScore) {
                score.add(1);
            }
        } while (canMove);

        mergeBrickToBackground();
        clearRow = clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            int bonus = ScoringRules.lineClearBonus(clearRow.getLinesRemoved());
            score.add(bonus);
        }

        gameOver = createNewBrick();

        return new DownData(clearRow, getViewData(), gameOver);
    }
}