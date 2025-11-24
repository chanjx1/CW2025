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

public class GuiController implements Initializable {

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

    private Rectangle[][] displayMatrix;  // background blocks
    private Rectangle[][] activeBrick;    // current falling piece

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

            default:
                // other keys ignored
        }
    }

    /** Called once at the start of a game to build the view. */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // Background cells
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = TetrisBoard.HIDDEN_ROWS; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
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

    private void updateBrickPosition(ViewData brick) {
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle r = activeBrick[i][j];

                // Position in pixels inside the brickOverlay Pane
                r.setX((brick.getxPosition() + j) * BRICK_SIZE);
                r.setY((brick.getyPosition() + i - 2) * BRICK_SIZE);

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
}
