package com.comp2042.view;

import com.comp2042.controller.InputEventListener;
import com.comp2042.model.TetrisBoard;
import com.comp2042.model.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardRenderer {

    private static final int BRICK_SIZE = 20;

    private final GridPane gamePanel;
    private final Pane brickOverlay;
    private final Pane holdPane;
    private final BrickStyler brickStyler;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] activeBrick;
    private Rectangle[][] ghostBrick;
    private Rectangle[][] holdCells;

    // We need this to calculate the Ghost Piece position
    private InputEventListener eventListener;

    public BoardRenderer(GridPane gamePanel, Pane brickOverlay, Pane holdPane) {
        this.gamePanel = gamePanel;
        this.brickOverlay = brickOverlay;
        this.holdPane = holdPane;
        this.brickStyler = new BrickStyler();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // 1. Init Background Grid
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = TetrisBoard.HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - TetrisBoard.HIDDEN_ROWS);
            }
        }

        // 2. Init Ghost Brick
        ghostBrick = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                int blockSize = BRICK_SIZE - 1;
                Rectangle rectangle = new Rectangle(blockSize, blockSize);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.WHITE);
                rectangle.setOpacity(0.35);
                rectangle.setArcWidth(9);
                rectangle.setArcHeight(9);
                rectangle.setStrokeWidth(1.0);
                rectangle.setStrokeType(javafx.scene.shape.StrokeType.CENTERED);
                ghostBrick[i][j] = rectangle;
                brickOverlay.getChildren().add(rectangle);
            }
        }

        // 3. Init Active Brick
        activeBrick = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                int blockSize = BRICK_SIZE - 1;
                Rectangle rectangle = new Rectangle(blockSize, blockSize);
                brickStyler.style(rectangle, brick.getBrickData()[i][j]);
                activeBrick[i][j] = rectangle;
                brickOverlay.getChildren().add(rectangle);
            }
        }

        // 4. Init Hold Pane
        initHoldPane();
    }

    private void initHoldPane() {
        final int HOLD_ROWS = 4;
        final int HOLD_COLS = 4;
        holdCells = new Rectangle[HOLD_ROWS][HOLD_COLS];

        for (int i = 0; i < HOLD_ROWS; i++) {
            for (int j = 0; j < HOLD_COLS; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE - 1, BRICK_SIZE - 1);
                r.setFill(Color.TRANSPARENT);
                r.setArcWidth(9);
                r.setArcHeight(9);
                r.setStrokeWidth(1.0);
                r.setStrokeType(javafx.scene.shape.StrokeType.CENTERED);
                r.setX(j * BRICK_SIZE);
                r.setY(i * BRICK_SIZE);
                holdCells[i][j] = r;
                holdPane.getChildren().add(r);
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

    public void showHoldPiece(int[][] shape) {
        if (holdCells == null) return;
        for (Rectangle[] row : holdCells) {
            for (Rectangle r : row) {
                r.setVisible(false);
            }
        }
        if (shape == null) return;

        int[] box = getBoundingBox(shape);
        int top = box[0], bottom = box[1], left = box[2], right = box[3];
        int realHeight = bottom - top + 1;
        int realWidth  = right - left + 1;
        int rows = holdCells.length;
        int cols = holdCells[0].length;
        int offsetY = (rows - realHeight) / 2;
        int offsetX;
        if (realWidth == 4) offsetX = 0;
        else if (realWidth == 2) offsetX = 1;
        else if (realWidth == 3) offsetX = 1;
        else offsetX = Math.max(0, (cols - realWidth) / 2);

        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                if (shape[i][j] == 0) continue;
                int yy = offsetY + (i - top);
                int xx = offsetX + (j - left);
                if (yy >= 0 && yy < rows && xx >= 0 && xx < cols) {
                    Rectangle cell = holdCells[yy][xx];
                    cell.setVisible(true);
                    brickStyler.style(cell, shape[i][j]);
                }
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