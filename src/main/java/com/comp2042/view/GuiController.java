package com.comp2042.view;

import com.comp2042.controller.InputEventListener;
import com.comp2042.controller.event.EventSource;
import com.comp2042.controller.event.EventType;
import com.comp2042.controller.event.MoveEvent;
import com.comp2042.model.DownData;
import com.comp2042.model.TetrisBoard;
import com.comp2042.model.ViewData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller for the game.
 * REFACTOR: Visual styling delegated to BrickStyler.
 */
public class GuiController implements Initializable {

    private enum GameState {
        RUNNING, PAUSED, GAME_OVER
    }

    private static final int BRICK_SIZE = 20;

    @FXML private GridPane gamePanel;
    @FXML private Pane brickOverlay;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private GameOverPanel gameOverPanel;
    @FXML private Label scoreLabel;
    @FXML private Pane holdPane;

    private Rectangle[][] holdCells;
    private Rectangle[][] displayMatrix;
    private Rectangle[][] activeBrick;
    private Rectangle[][] ghostBrick;

    private Timeline timeLine;
    private InputEventListener eventListener;

    // REFACTOR: New dependency for styling
    private final BrickStyler brickStyler = new BrickStyler();

    private final ObjectProperty<GameState> gameState = new SimpleObjectProperty<>(GameState.RUNNING);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(this::handleKeyPressed);
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    private void handleKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.N) {
            newGame(null);
            keyEvent.consume();
            return;
        }

        if (code == KeyCode.P) {
            pauseGame(null);
            keyEvent.consume();
            return;
        }

        if (gameState.get() != GameState.RUNNING) {
            return;
        }

        switch (code) {
            case LEFT, A -> {
                refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                keyEvent.consume();
            }
            case RIGHT, D -> {
                refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                keyEvent.consume();
            }
            case UP, W -> {
                refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                keyEvent.consume();
            }
            case DOWN, S -> {
                moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                keyEvent.consume();
            }
            case SPACE -> {
                moveHardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
                keyEvent.consume();
            }
            case SHIFT, C -> {
                refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
                keyEvent.consume();
            }
            default -> {}
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = TetrisBoard.HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - TetrisBoard.HIDDEN_ROWS);
            }
        }

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

        activeBrick = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                int blockSize = BRICK_SIZE - 1;
                Rectangle rectangle = new Rectangle(blockSize, blockSize);
                // REFACTOR: Use BrickStyler
                brickStyler.style(rectangle, brick.getBrickData()[i][j]);
                activeBrick[i][j] = rectangle;
                brickOverlay.getChildren().add(rectangle);
            }
        }

        initHoldPane();

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
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

    private int calculateGhostY(ViewData brick) {
        int ghostY = brick.getyPosition();
        while (eventListener != null && eventListener.canMoveDown(brick, ghostY + 1)) {
            ghostY++;
        }
        return ghostY;
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

    private void updateBrickPosition(ViewData brick) {
        updateGhostPosition(brick);
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle r = activeBrick[i][j];
                r.setX((brick.getxPosition() + j) * BRICK_SIZE);
                r.setY((brick.getyPosition() + i - TetrisBoard.HIDDEN_ROWS) * BRICK_SIZE);
                // REFACTOR: Use BrickStyler logic implicitly (or explicitly if color changes)
                // Note: If color never changes after init, we don't need to re-style, just move.
                // But for robustness, we re-apply style if the brick type changes.
                brickStyler.style(r, brick.getBrickData()[i][j]);
            }
        }
    }

    private void refreshBrick(ViewData brick) {
        if (gameState.get() == GameState.RUNNING) {
            updateBrickPosition(brick);
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = TetrisBoard.HIDDEN_ROWS; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        // REFACTOR: Use BrickStyler
        brickStyler.style(rectangle, color);
    }

    private void moveDown(MoveEvent event) {
        if (gameState.get() == GameState.RUNNING) {
            DownData downData = eventListener.onDownEvent(event);
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    private void moveHardDrop(MoveEvent event) {
        if (gameState.get() == GameState.RUNNING) {
            DownData downData = eventListener.onHardDropEvent(event);
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty scoreProperty) {
        if (scoreLabel != null) {
            scoreLabel.textProperty().bind(scoreProperty.asString("Score: %d"));
        }
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        gameState.set(GameState.GAME_OVER);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        gameState.set(GameState.RUNNING);
    }

    public void pauseGame(ActionEvent actionEvent) {
        if (timeLine == null || gameState.get() == GameState.GAME_OVER) {
            gamePanel.requestFocus();
            return;
        }
        if (gameState.get() == GameState.PAUSED) {
            timeLine.play();
            gameState.set(GameState.RUNNING);
        } else if (gameState.get() == GameState.RUNNING) {
            timeLine.stop();
            gameState.set(GameState.PAUSED);
        }
        gamePanel.requestFocus();
    }

    public void showScoreBonus(int bonus) {
        NotificationPanel notificationPanel = new NotificationPanel("+" + bonus);
        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());
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
                    // REFACTOR: Use BrickStyler
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