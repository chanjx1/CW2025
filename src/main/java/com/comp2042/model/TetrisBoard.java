package com.comp2042.model;

import com.comp2042.model.bricks.Brick;
import com.comp2042.model.bricks.BrickGenerator;
import com.comp2042.model.bricks.BrickRotator;
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

    /** The logical width of the board (in columns). */
    private final int width;

    /** The logical height of the board (in rows), including hidden rows. */
    private final int height;

    /** Strategy for generating new bricks (e.g., Random or Bag-7). */
    private final BrickGenerator brickGenerator;

    /** Helper to manage the rotation state of the active brick. */
    private final BrickRotator brickRotator;

    /** The 2D grid representing the background (locked) blocks. */
    private int[][] currentGameMatrix;

    /** The current (x, y) position of the active falling brick. */
    private GamePoint currentOffset;

    /** The score model tracking points, levels, and lines cleared. */
    private final Score score;

    /** The brick currently held in reserve (swap storage). */
    private Brick holdBrick = null;

    /** Flag to prevent multiple swaps in a single turn (lock until drop). */
    private boolean holdUsedThisTurn = false;

    /** Total height of the board grid. */
    public static final int BOARD_HEIGHT = 25;

    /** Total width of the board grid. */
    public static final int BOARD_WIDTH  = 10;

    /** Number of rows at the top hidden from the view to facilitate smooth spawning. */
    public static final int HIDDEN_ROWS  = 2;

    /** Default X spawning coordinate (centered). */
    private static final int SPAWN_X = 4;

    /** Default Y spawning coordinate (inside hidden rows). */
    private static final int SPAWN_Y = HIDDEN_ROWS;

    /**
     * Constructs a new TetrisBoard with the specified dimensions.
     *
     * @param width  The width of the board in blocks.
     * @param height The height of the board in blocks.
     */
    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new Bag7BrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Attempts to move the active brick down by one row.
     *
     * @return true if the move was successful; false if blocked by collision or floor.
     */
    @Override public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        GamePoint p = currentOffset.translate(0, 1);
        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), p.x(), p.y());
        if (conflict) { return false; } else { currentOffset = p; return true; }
    }

    /**
     * Attempts to move the active brick left by one column.
     *
     * @return true if the move was successful; false if blocked by collision or wall.
     */
    @Override public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        GamePoint p = currentOffset.translate(-1, 0);
        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), p.x(), p.y());
        if (conflict) { return false; } else { currentOffset = p; return true; }
    }

    /**
     * Attempts to move the active brick right by one column.
     *
     * @return true if the move was successful; false if blocked by collision or wall.
     */
    @Override public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixUtils.copy(currentGameMatrix);
        GamePoint p = currentOffset.translate(1, 0);
        boolean conflict = MatrixUtils.intersect(currentMatrix, brickRotator.getCurrentShape(), p.x(), p.y());
        if (conflict) { return false; } else { currentOffset = p; return true; }
    }

    /**
     * Attempts to rotate the active brick 90 degrees clockwise.
     *
     * @return true if rotation was valid; false if blocked by surrounding blocks.
     */
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

    /**
     * Retrieves the current state of the board grid (background blocks).
     *
     * @return A 2D integer array where non-zero values represent locked blocks.
     */
    @Override public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Generates a snapshot of the board state for the View.
     *
     * @return A {@link ViewData} object containing the current brick, position, and next piece preview.
     */
    @Override public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
    }

    /**
     * Locks the currently active brick into the background matrix.
     * <p>
     * This is called when the brick can no longer move down.
     * </p>
     */
    @Override public void mergeBrickToBackground() {
        currentGameMatrix = MatrixUtils.merge(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y());
    }

    /**
     * Scans the board for complete rows, removes them, and shifts blocks down.
     *
     * @return A {@link ClearRow} object detailing which lines were removed and the new matrix.
     */
    @Override public ClearRow clearRows() {
        ClearRow clearRow = MatrixUtils.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    /**
     * Retrieves the score model.
     *
     * @return The {@link Score} object.
     */
    @Override public Score getScore() {
        return score;
    }

    /**
     * Resets the board state for a completely new game.
     * <p>
     * Clears the matrix, resets the score, clears the hold piece, and spawns the first brick.
     * </p>
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[BOARD_HEIGHT][BOARD_WIDTH];
        score.reset();

        holdBrick = null;
        holdUsedThisTurn = false;

        createNewBrick();
    }

    /**
     * Swaps the current active brick with the held brick.
     * <p>
     * If no brick is held, the current one is stored and a new one is spawned.
     * This action can only be performed once per turn (until a piece locks).
     * </p>
     *
     * @return true if the swap causes an immediate collision (Game Over), false otherwise.
     */
    @Override public boolean holdCurrentBrick() {
        if (holdUsedThisTurn) return false;
        holdUsedThisTurn = true;
        Brick currentBrick = brickRotator.getBrick();
        if (holdBrick == null) { holdBrick = currentBrick; return createNewBrick(); }
        Brick temp = holdBrick; holdBrick = currentBrick; brickRotator.setBrick(temp);
        currentOffset = new GamePoint(SPAWN_X, SPAWN_Y);
        return MatrixUtils.intersect(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.x(), currentOffset.y());
    }

    /**
     * Retrieves the shape of the currently held brick for display purposes.
     *
     * @return The 2D array of the held brick, or null if empty.
     */
    @Override public int[][] getHoldBrickShape() {
        if (holdBrick == null) return null;
        return holdBrick.getShapeMatrix().get(0);
    }

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

    /**
     * Instantly drops the brick to the lowest valid position.
     * <p>
     * Loops the move-down logic until collision occurs, then locks the piece.
     * </p>
     *
     * @param awardSoftDropScore Unused in this implementation.
     * @return A {@link DownData} object containing the clear-row result and game-over status.
     */
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