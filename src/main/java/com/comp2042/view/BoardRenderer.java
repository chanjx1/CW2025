package com.comp2042.view;

import com.comp2042.controller.InputEventListener;
import com.comp2042.model.TetrisBoard;
import com.comp2042.model.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Responsible for rendering the game state onto the JavaFX scene.
 * <p>
 * This class manages the grid of rectangles that represent the background,
 * the active falling piece, the ghost piece projection, and the preview panels.
 * </p>
 */
public class BoardRenderer {

    private static final int BRICK_SIZE = 20;

    private final GridPane gamePanel;
    private final Pane brickOverlay;
    private final Pane holdPane;
    private final Pane nextBrickPane;
    private final BrickStyler brickStyler;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] activeBrick;
    private Rectangle[][] ghostBrick;
    private Rectangle[][] holdCells;
    private Rectangle[][] nextCells;

    // We need this to calculate the Ghost Piece position
    private InputEventListener eventListener;

    public BoardRenderer(GridPane gamePanel, Pane brickOverlay, Pane holdPane, Pane nextBrickPane) {
        this.gamePanel = gamePanel;
        this.brickOverlay = brickOverlay;
        this.holdPane = holdPane;
        this.nextBrickPane = nextBrickPane;
        this.brickStyler = new BrickStyler();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Helper method to initialize a grid of rectangles in a given Pane.
     * <p>
     * Creates a 2D array of transparent rectangles and adds them to the scene graph.
     * This method is used to initialize the Ghost, Active, Hold, and Next grids efficiently.
     * </p>
     *
     * @param pane The parent container for the rectangles.
     * @param rows Number of rows in the grid.
     * @param cols Number of columns in the grid.
     * @return A 2D array of the created Rectangle objects.
     */
    private Rectangle[][] createGrid(Pane pane, int rows, int cols) {
        Rectangle[][] grid = new Rectangle[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE - 1, BRICK_SIZE - 1);
                r.setFill(Color.TRANSPARENT);
                r.setArcWidth(9);
                r.setArcHeight(9);
                r.setStrokeWidth(1.0);
                r.setStrokeType(javafx.scene.shape.StrokeType.CENTERED);
                r.setX(j * BRICK_SIZE);
                r.setY(i * BRICK_SIZE);

                grid[i][j] = r;
                pane.getChildren().add(r);
            }
        }
        return grid;
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // 1. Init Background Grid
        // Note: Main board uses specific spacing/adding logic, so we might keep the main loop or refactor it carefully.
        // The main board usually adds to a GridPane (gamePanel.add), while the others add to Pane (pane.getChildren.add).
        // So we will leave the MAIN board loop alone, but refactor the Hold/Next/Ghost ones.

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = TetrisBoard.HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - TetrisBoard.HIDDEN_ROWS);
            }
        }

        // 2. Init Ghost Brick (Pane based)
        this.ghostBrick = createGrid(brickOverlay, brick.getBrickData().length, brick.getBrickData()[0].length);
        // Ghost needs specific styling differences (Stroke), so apply them after creation:
        for (Rectangle[] row : ghostBrick) {
            for (Rectangle r : row) {
                r.setStroke(Color.WHITE);
                r.setOpacity(0.35);
            }
        }

        // 3. Init Active Brick (Pane based)
        this.activeBrick = createGrid(brickOverlay, brick.getBrickData().length, brick.getBrickData()[0].length);
        // Active brick needs initial color styling
        for (int i = 0; i < activeBrick.length; i++) {
            for (int j = 0; j < activeBrick[i].length; j++) {
                brickStyler.style(activeBrick[i][j], brick.getBrickData()[i][j]);
            }
        }

        // 4. Init Hold Pane
        this.holdCells = createGrid(holdPane, 4, 4);

        // 5. Init Next Panes
        this.nextCells = createGrid(nextBrickPane, 4, 4);
        // If you still have nextBrickPane2
        // this.nextCells2 = createGrid(nextBrickPane2, 4, 4);
    }

    public void showNextPiece(int[][] shape) {
        if (nextCells == null || shape == null) return;
        renderCentered(nextCells, shape, nextBrickPane.getPrefWidth(), nextBrickPane.getPrefHeight());
    }

    public void showHoldPiece(int[][] shape) {
        if (holdCells == null) return;

        // FIX: If shape is null, clear the grid and return
        if (shape == null) {
            for (Rectangle[] row : holdCells) {
                for (Rectangle r : row) r.setVisible(false);
            }
            return;
        }

        renderCentered(holdCells, shape, holdPane.getPrefWidth(), holdPane.getPrefHeight());
    }

    /**
     * Renders a tetromino shape centered within a preview pane.
     * <p>
     * Calculates the pixel-perfect center based on the shape's bounding box width/height
     * and the pane's dimensions, ensuring visual alignment for odd-width pieces.
     * </p>
     *
     * @param targetGrid The grid of rectangles to use for rendering.
     * @param shape The 2D array representing the shape to draw.
     * @param paneWidth The width of the container pane.
     * @param paneHeight The height of the container pane.
     */
    private void renderCentered(Rectangle[][] targetGrid, int[][] shape, double paneWidth, double paneHeight) {
        // 1. Clear all cells first
        for (Rectangle[] row : targetGrid) {
            for (Rectangle r : row) r.setVisible(false);
        }

        // 2. Calculate the bounding box of the shape
        int[] box = getBoundingBox(shape);
        int top = box[0], bottom = box[1], left = box[2], right = box[3];

        // 3. Calculate actual dimensions in blocks
        int contentWidthInBlocks = right - left + 1;
        int contentHeightInBlocks = bottom - top + 1;

        // 4. Calculate dimensions in pixels
        double contentWidthPx = contentWidthInBlocks * BRICK_SIZE;
        double contentHeightPx = contentHeightInBlocks * BRICK_SIZE;

        // 5. Calculate the starting pixel coordinates to center the shape
        double startX = (paneWidth - contentWidthPx) / 2;
        double startY = (paneHeight - contentHeightPx) / 2;

        // 6. Draw the blocks
        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                if (shape[i][j] != 0) {
                    // We map the shape's loop indices (i, j) to the targetGrid flat pool
                    // We just need *any* rectangle from the pool, so we map loosely
                    int poolRow = i - top;
                    int poolCol = j - left;

                    if (poolRow < targetGrid.length && poolCol < targetGrid[0].length) {
                        Rectangle r = targetGrid[poolRow][poolCol];

                        r.setVisible(true);
                        brickStyler.style(r, shape[i][j]);

                        // KEY FIX: Set X/Y manually based on calculated center
                        r.setX(startX + (poolCol * BRICK_SIZE));
                        r.setY(startY + (poolRow * BRICK_SIZE));
                    }
                }
            }
        }
    }

    public void updateBrickPosition(ViewData brick) {
        updateGhostPosition(brick);

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle r = activeBrick[i][j];
                r.setX((brick.getxPosition() + j) * BRICK_SIZE);
                r.setY((brick.getyPosition() + i - TetrisBoard.HIDDEN_ROWS) * BRICK_SIZE);
                brickStyler.style(r, brick.getBrickData()[i][j]);
            }
        }
    }

    private void updateGhostPosition(ViewData brick) {
        if (ghostBrick == null || eventListener == null) return;

        int[][] shape = brick.getBrickData();
        int ghostY = calculateGhostY(brick);

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                Rectangle g = ghostBrick[i][j];
                if (shape[i][j] == 0) {
                    g.setVisible(false);
                    continue;
                }
                g.setVisible(true);
                g.setX((brick.getxPosition() + j) * BRICK_SIZE);
                g.setY((ghostY + i - TetrisBoard.HIDDEN_ROWS) * BRICK_SIZE);
            }
        }
    }

    private int calculateGhostY(ViewData brick) {
        int ghostY = brick.getyPosition();
        while (eventListener != null && eventListener.canMoveDown(brick, ghostY + 1)) {
            ghostY++;
        }
        return ghostY;
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = TetrisBoard.HIDDEN_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                brickStyler.style(displayMatrix[i][j], board[i][j]);
            }
        }
    }

    private int[] getBoundingBox(int[][] shape) {
        int top = shape.length, bottom = -1, left = shape[0].length, right = -1;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    top = Math.min(top, i);
                    bottom = Math.max(bottom, i);
                    left = Math.min(left, j);
                    right = Math.max(right, j);
                }
            }
        }
        return new int[]{top, bottom, left, right};
    }
}