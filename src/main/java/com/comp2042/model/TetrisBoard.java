package com.comp2042.model;

import com.comp2042.model.bricks.Brick;
import com.comp2042.model.bricks.BrickGenerator;
import com.comp2042.model.bricks.BrickRotator;
import com.comp2042.model.bricks.RandomBrickGenerator;
import com.comp2042.model.bricks.Bag7BrickGenerator;

/**
 * Represents the game board logic and physics engine.
 * <p>
 * This class manages the state of the 2D grid, collision detection, brick movement,
 * and the spawn mechanics. It implements the {@link Board} interface.
 * </p>
 * The board uses a coordinate system where (0,0) is the top-left corner.
 * It includes hidden rows at the top for smooth brick spawning.
 */
public class TetrisBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private GamePoint currentOffset;
    private final Score score;
    private Brick holdBrick = null;
    private boolean holdUsedThisTurn = false;

    public static final int BOARD_HEIGHT = 25;
    public static final int BOARD_WIDTH  = 10;
    public static final int HIDDEN_ROWS  = 2;
    private static final int SPAWN_X = 4;
    private static final int SPAWN_Y = HIDDEN_ROWS;

    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new Bag7BrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        GamePoint p = currentOffset.translate(0, 1);
        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), p.x(), p.y());
        if (conflict) { return false; } else { currentOffset = p; return true; }
    }
    @Override public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        GamePoint p = currentOffset.translate(-1, 0);
        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), p.x(), p.y());
        if (conflict) { return false; } else { currentOffset = p; return true; }
    }
    @Override public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        GamePoint p = currentOffset.translate(1, 0);
        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), p.x(), p.y());
        if (conflict) { return false; } else { currentOffset = p; return true; }
    }
    @Override public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixUtils.intersect(currentMatrix, nextShape.getShape(), currentOffset.x(), currentOffset.y());
        if (conflict) { return false; } else { brickRotator.setCurrentShape(nextShape.getPosition()); return true; }
    }
    /**
     * Spawns a new active brick at the top of the board.
     * <p>
     * Uses the {@link BrickGenerator} to fetch the next shape and resets the spawn position.
     * </p>
     *
     * @return true if the new brick immediately collides with existing blocks (Game Over condition).
     */
    @Override public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new GamePoint(SPAWN_X, SPAWN_Y);
        holdUsedThisTurn = false;
        return MatrixUtils.intersect(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y());
    }
    @Override public int[][] getBoardMatrix() { return currentGameMatrix; }
    @Override public ViewData getViewData() { return new ViewData(brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y(), brickGenerator.getNextBrick().getShapeMatrix().get(0)); }
    @Override public void mergeBrickToBackground() { currentGameMatrix = MatrixUtils.merge(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y()); }
    @Override public ClearRow clearRows() {
        ClearRow clearRow = MatrixUtils.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }
    @Override public Score getScore() { return score; }
    @Override public void newGame() { currentGameMatrix = new int[BOARD_HEIGHT][BOARD_WIDTH]; score.reset(); createNewBrick(); }
    @Override public boolean holdCurrentBrick() {
        if (holdUsedThisTurn) return false;
        holdUsedThisTurn = true;
        Brick currentBrick = brickRotator.getBrick();
        if (holdBrick == null) { holdBrick = currentBrick; return createNewBrick(); }
        Brick temp = holdBrick; holdBrick = currentBrick; brickRotator.setBrick(temp);
        currentOffset = new GamePoint(SPAWN_X, SPAWN_Y);
        return MatrixUtils.intersect(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y());
    }
    @Override public int[][] getHoldBrickShape() { if (holdBrick == null) return null; return holdBrick.getShapeMatrix().get(0); }

    /**
     * Advances the game state by one step (gravity).
     * <p>
     * Attempts to move the active brick down by one row. If movement is blocked,
     * the brick is merged into the background grid, rows are checked for clearing,
     * and a new brick is spawned.
     * </p>
     *
     * @param awardSoftDropScore Unused in this implementation (scoring is handled by Controller).
     * @return A {@link DownData} object containing the clear-row result and game-over status.
     */
    @Override
    public DownData stepDown(boolean awardSoftDropScore) {
        boolean canMove = moveBrickDown();
        ClearRow clearRow = null;
        boolean gameOver = false;

        if (!canMove) {
            mergeBrickToBackground();
            clearRow = clearRows();

            if (createNewBrick()) {
                gameOver = true;
            }
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
        } while (canMove);

        mergeBrickToBackground();
        clearRow = clearRows();

        gameOver = createNewBrick();

        return new DownData(clearRow, getViewData(), gameOver);
    }
}