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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller for the game.
 * This class is responsible for:
 * - Rendering the board and active brick
 * - Handling user input (keyboard)
 * - Driving the Timeline that makes the brick fall
 * Game rules and scoring are delegated to GameController.
 */
public class GuiController implements Initializable {

    /**
     * Simple game state machine used by the GUI to decide when to
     * accept input and when to stop the Timeline.
     */
    private enum GameState {
        RUNNING,
        PAUSED,
        GAME_OVER
    }

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel; // background board (fixed grid)

    @FXML
    private Pane brickOverlay;  // active falling brick drawn on top

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel; // full background grid (the dim background)

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Pane holdPane;

    private Rectangle[][] holdCells;
    private Rectangle[][] displayMatrix;  // background blocks
    private Rectangle[][] activeBrick;    // current falling piece
    private Rectangle[][] ghostBrick;     // ghost projection of current piece

    private Timeline timeLine;
    private InputEventListener eventListener;

    private final ObjectProperty<GameState> gameState =
            new SimpleObjectProperty<>(GameState.RUNNING);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // Delegate to a separate method
        gamePanel.setOnKeyPressed(this::handleKeyPressed);

        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    /**
     * Centralised keyboard handler.
     * N = new game (always allowed)
     * P = pause / resume (always allowed)
     * Arrow keys / WASD = move / rotate active brick (only when RUNNING)
     */
    private void handleKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        // N = new game (works even when paused/game over)
        if (code == KeyCode.N) {
            newGame(null);
            keyEvent.consume();
            return;
        }

        // P = pause/resume (must work even when currently paused)
        if (code == KeyCode.P) {
            pauseGame(null);
            keyEvent.consume();
            return;
        }

        // Ignore movement keys if paused or game over
        if (gameState.get() != GameState.RUNNING) {
            return;
        }

        // Map keys to actions
        switch (code) {
            case LEFT:
            case A:
                refreshBrick(eventListener.onLeftEvent(
                        new MoveEvent(EventType.LEFT, EventSource.USER)));
                keyEvent.consume();
                break;

            case RIGHT:
            case D:
                refreshBrick(eventListener.onRightEvent(
                        new MoveEvent(EventType.RIGHT, EventSource.USER)));
                keyEvent.consume();
                break;

            case UP:
            case W:
                refreshBrick(eventListener.onRotateEvent(
                        new MoveEvent(EventType.ROTATE, EventSource.USER)));
                keyEvent.consume();
                break;

            case DOWN:
            case S:
                moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                keyEvent.consume();
                break;

            case SPACE:
                moveHardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
                keyEvent.consume();
                break;

            case SHIFT:
            case C:
                refreshBrick(eventListener.onHoldEvent(
                        new MoveEvent(EventType.HOLD, EventSource.USER)));
                keyEvent.consume();
                break;

            default:
                // other keys ignored
        }
    }

    /**
     * Called once at the start of the game to build the initial view.
     * It creates the background grid cells and the rectangles used to draw
     * the active brick overlay, and starts the falling Timeline.
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // Background cells
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = TetrisBoard.HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - TetrisBoard.HIDDEN_ROWS);
            }
        }

        // Ghost brick overlay (drawn first so it stays under the active brick)
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

        // Active brick overlay
        activeBrick = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                int blockSize = BRICK_SIZE - 1;
                Rectangle rectangle = new Rectangle(blockSize, blockSize);

                // use shared styling helper
                styleBlock(rectangle, brick.getBrickData()[i][j]);

                activeBrick[i][j] = rectangle;
                brickOverlay.getChildren().add(rectangle);
            }
        }

        // ---- HOLD preview initialisation ----
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

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private Paint getFillColor(int i) {
        switch (i) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.AQUA;
            case 2: return Color.BLUEVIOLET;
            case 3: return Color.DARKGREEN;
            case 4: return Color.YELLOW;
            case 5: return Color.RED;
            case 6: return Color.BEIGE;
            case 7: return Color.BURLYWOOD;
            default: return Color.WHITE;
        }
    }

    private void styleBlock(Rectangle rectangle, int colorCode) {
        rectangle.setFill(getFillColor(colorCode));
        rectangle.setArcWidth(9);
        rectangle.setArcHeight(9);
        rectangle.setStrokeWidth(1.2);
        rectangle.setStrokeType(javafx.scene.shape.StrokeType.CENTERED);
    }

    /**
     * Uses the controller's canMoveDown() to find the board Y position
     * where the current brick would land if dropped straight down.
     */
    private int calculateGhostY(ViewData brick) {
        int ghostY = brick.getyPosition();  // BOARD coordinates

        // Keep moving down until the next row would collide
        while (eventListener != null && eventListener.canMoveDown(brick, ghostY + 1)) {
            ghostY++;
        }
        return ghostY;
    }

    /**
     * Positions and shows the ghost rectangles according to the landing spot.
     */
    private void updateGhostPosition(ViewData brick) {
        if (ghostBrick == null || eventListener == null) {
            return;
        }

        int[][] shape = brick.getBrickData();
        int ghostY = calculateGhostY(brick);  // BOARD coordinates

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                Rectangle g = ghostBrick[i][j];

                if (shape[i][j] == 0) {
                    g.setVisible(false);
                    continue;
                }

                g.setVisible(true);
                // Same X mapping as active brick
                g.setX((brick.getxPosition() + j) * BRICK_SIZE);
                // Same Y mapping as active brick, but using ghostY
                g.setY((ghostY + i - TetrisBoard.HIDDEN_ROWS) * BRICK_SIZE);
            }
        }
    }

    private void updateBrickPosition(ViewData brick) {
        // First update the ghost projection
        updateGhostPosition(brick);

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle r = activeBrick[i][j];

                // Position in pixels inside the brickOverlay Pane
                r.setX((brick.getxPosition() + j) * BRICK_SIZE);
                r.setY((brick.getyPosition() + i - TetrisBoard.HIDDEN_ROWS) * BRICK_SIZE);

                r.setFill(getFillColor(brick.getBrickData()[i][j]));
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
        styleBlock(rectangle, color);
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
            scoreLabel.textProperty().bind(
                    scoreProperty.asString("Score: %d")
            );
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

    /**
     * Toggles between RUNNING and PAUSED.
     * When paused, the Timeline is stopped and movement input is ignored.
     * When resumed, the Timeline continues from where it left off.
     */
    public void pauseGame(ActionEvent actionEvent) {
        // If the game hasn't started or we are already game over, do nothing
        if (timeLine == null || gameState.get() == GameState.GAME_OVER) {
            gamePanel.requestFocus();
            return;
        }

        if (gameState.get() == GameState.PAUSED) {
            // Currently paused -> resume
            timeLine.play();
            gameState.set(GameState.RUNNING);
        } else if (gameState.get() == GameState.RUNNING) {
            // Currently running -> pause
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

        // Clear
        for (Rectangle[] row : holdCells) {
            for (Rectangle r : row) {
                r.setVisible(false);
            }
        }

        if (shape == null) return;

        // ---- trim empty space ----
        int[] box = getBoundingBox(shape);
        int top = box[0], bottom = box[1], left = box[2], right = box[3];

        int realHeight = bottom - top + 1;
        int realWidth  = right - left + 1;

        int rows = holdCells.length;
        int cols = holdCells[0].length;

        // vertical: true centre
        int offsetY = (rows - realHeight) / 2;

        // horizontal: tweak per width
        int offsetX;
        if (realWidth == 4) {
            // I piece spans full width
            offsetX = 0;
        } else if (realWidth == 2) {
            // O piece: columns 1–2 look best
            offsetX = 1;
        } else if (realWidth == 3) {
            // T/J/L/S/Z: shift one cell right so they don't hug the border
            offsetX = 1;
        } else {
            // fallback
            offsetX = Math.max(0, (cols - realWidth) / 2);
        }

        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                if (shape[i][j] == 0) continue;

                int yy = offsetY + (i - top);
                int xx = offsetX + (j - left);

                if (yy >= 0 && yy < rows && xx >= 0 && xx < cols) {
                    Rectangle cell = holdCells[yy][xx];
                    cell.setVisible(true);
                    cell.setFill(getFillColor(shape[i][j]));
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
