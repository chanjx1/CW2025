package com.comp2042.view;

import com.comp2042.controller.InputEventListener;
import com.comp2042.controller.event.EventSource;
import com.comp2042.controller.event.EventType;
import com.comp2042.controller.event.MoveEvent;
import com.comp2042.model.DownData;
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
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller for the game.
 * Refactored to delegate rendering to BoardRenderer and styling to BrickStyler.
 */
public class GuiController implements Initializable {

    @FXML private GridPane gamePanel;
    @FXML private Pane brickOverlay;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private GameOverPanel gameOverPanel;
    @FXML private Label scoreLabel;
    @FXML private Pane holdPane;
    @FXML private Pane nextBrickPane;
    @FXML private Label highScoreLabel;
    @FXML private VBox pauseMenu;
    @FXML private Label levelLabel;
    @FXML private Label linesLabel;

    private Timeline timeLine;
    private InputEventListener eventListener;

    private BoardRenderer boardRenderer;

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

        this.boardRenderer = new BoardRenderer(gamePanel, brickOverlay, holdPane, nextBrickPane);
    }

    private void handleKeyPressed(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.ESCAPE || code == KeyCode.P) {
            togglePauseMenu();
            keyEvent.consume();
            return;
        }

        if (code == KeyCode.N) {
            newGame(null);
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
        boardRenderer.initGameView(boardMatrix, brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private void refreshBrick(ViewData brick) {
        if (gameState.get() == GameState.RUNNING) {
            boardRenderer.updateBrickPosition(brick);
            boardRenderer.showNextPiece(brick.getNextBrickData());
        }
    }

    public void refreshGameBackground(int[][] board) {
        boardRenderer.refreshGameBackground(board);
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
        this.boardRenderer.setEventListener(eventListener);
    }

    public void bindGameStats(IntegerProperty scoreProp, IntegerProperty levelProp, IntegerProperty linesProp) {
        if (scoreLabel != null) {
            scoreLabel.textProperty().bind(scoreProp.asString("Score: %05d"));
        }

        if (levelLabel != null) {
            levelLabel.textProperty().bind(levelProp.asString("%d"));
        }

        if (linesLabel != null) {
            linesLabel.textProperty().bind(linesProp.asString("%d"));
        }

        levelProp.addListener((obs, oldVal, newVal) -> {
            int level = newVal.intValue();
            double delay = Math.max(100, 400 - ((level - 1) * 50));

            timeLine.stop();
            timeLine.getKeyFrames().setAll(new KeyFrame(
                    Duration.millis(delay),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.play();

            showScoreBonus("LEVEL " + level);
        });
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

    public void showScoreBonus(String text) {
        NotificationPanel notificationPanel = new NotificationPanel(text);
        int activeNotifications = groupNotification.getChildren().size();
        double yOffset = activeNotifications * 25;

        notificationPanel.setTranslateY(yOffset);

        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());
    }

    public void showHoldPiece(int[][] shape) {
        boardRenderer.showHoldPiece(shape);
    }

    public void setHighScore(int score) {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(score));
        }
    }

    public void togglePauseMenu() {
        if (pauseMenu.isVisible()) {
            pauseMenu.setVisible(false);
            if (gameState.get() == GameState.PAUSED) {
                timeLine.play();
                gameState.set(GameState.RUNNING);
            }
            gamePanel.requestFocus();
        } else {
            pauseMenu.setVisible(true);
            if (gameState.get() == GameState.RUNNING) {
                timeLine.stop();
                gameState.set(GameState.PAUSED);
            }
        }
    }

    public void onResume(ActionEvent event) {
        togglePauseMenu();
    }

    public void onExitToMenu(ActionEvent event) throws IOException {
        timeLine.stop();
        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
        Parent root = FXMLLoader.load(location);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}