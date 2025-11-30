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
 * The View Controller in the MVC architecture.
 * <p>
 * This class handles the JavaFX UI updates, captures user input, and manages the game loop (Timeline).
 * It delegates complex rendering logic to {@link BoardRenderer} and forwards game events to the {@link com.comp2042.controller.GameController}.
 * </p>
 */
public class GuiController implements Initializable {

    /** The grid container for the static background blocks. */
    @FXML private GridPane gamePanel;

    /** The overlay pane for rendering the active falling brick and ghost piece. */
    @FXML private Pane brickOverlay;

    /** The container for floating score/level notifications. */
    @FXML private Group groupNotification;

    /** The background grid visual container. */
    @FXML private GridPane brickPanel;

    /** The Game Over overlay panel. */
    @FXML private GameOverPanel gameOverPanel;

    /** The label displaying the current score. */
    @FXML private Label scoreLabel;

    /** The pane displaying the held piece. */
    @FXML private Pane holdPane;

    /** The pane displaying the next piece. */
    @FXML private Pane nextBrickPane;

    /** The label displaying the high score. */
    @FXML private Label highScoreLabel;

    /** The container for the in-game pause menu. */
    @FXML private VBox pauseMenu;

    /** The label displaying the current level. */
    @FXML private Label levelLabel;

    /** The label displaying the total lines cleared. */
    @FXML private Label linesLabel;

    /** The game loop timer that triggers gravity events. */
    private Timeline timeLine;

    /** The listener interface to communicate with the GameController. */
    private InputEventListener eventListener;

    /** Helper class responsible for drawing the grid and blocks. */
    private BoardRenderer boardRenderer;

    /** Observable property tracking the current game state (Running, Paused, etc.). */
    private final ObjectProperty<GameState> gameState = new SimpleObjectProperty<>(GameState.RUNNING);

    /**
     * Initializes the controller class.
     * <p>
     * Sets up the focus, key listeners, visual effects, and initializes the BoardRenderer.
     * This method is automatically called after the FXML file has been loaded.
     * </p>
     *
     * @param location  The location used to resolve relative paths for the root object, or null.
     * @param resources The resources used to localize the root object, or null.
     */
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

    /**
     * Central handler for keyboard input.
     * <p>
     * Maps keys (WASD, Arrows, P, ESC) to specific game events and forwards them
     * to the event listener if the game is running.
     * </p>
     *
     * @param keyEvent The key event triggered by the user.
     */
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

    /**
     * Initializes the visual state of the game and starts the game loop.
     *
     * @param boardMatrix The initial grid state.
     * @param brick       The initial view data for the active brick.
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        boardRenderer.initGameView(boardMatrix, brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /**
     * Refreshes the rendering of the active brick and next piece preview.
     *
     * @param brick The current view data containing brick positions and shapes.
     */
    private void refreshBrick(ViewData brick) {
        if (gameState.get() == GameState.RUNNING) {
            boardRenderer.updateBrickPosition(brick);
            boardRenderer.showNextPiece(brick.getNextBrickData());
        }
    }

    /**
     * Refreshes the background grid (locked blocks).
     *
     * @param board The 2D array representing the board state.
     */
    public void refreshGameBackground(int[][] board) {
        boardRenderer.refreshGameBackground(board);
    }

    /**
     * Handles a downward movement event (gravity or soft drop).
     *
     * @param event The move event details.
     */
    private void moveDown(MoveEvent event) {
        if (gameState.get() == GameState.RUNNING) {
            DownData downData = eventListener.onDownEvent(event);
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Handles a hard drop event.
     *
     * @param event The move event details.
     */
    private void moveHardDrop(MoveEvent event) {
        if (gameState.get() == GameState.RUNNING) {
            DownData downData = eventListener.onHardDropEvent(event);
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Sets the listener for input events.
     *
     * @param eventListener The controller implementing the listener interface.
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        this.boardRenderer.setEventListener(eventListener);
    }

    /**
     * Binds the UI labels to the observable properties in the Score model.
     * <p>
     * This ensures the UI automatically updates when the score, level, or lines change.
     * It also attaches a listener to the level property to adjust game speed dynamically.
     * </p>
     *
     * @param scoreProp The score property.
     * @param levelProp The level property.
     * @param linesProp The lines cleared property.
     */
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

    /**
     * Transitions the game to the Game Over state.
     * <p>
     * Stops the timeline and displays the Game Over banner.
     * </p>
     */
    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        gameState.set(GameState.GAME_OVER);
    }

    /**
     * Starts a new game session.
     *
     * @param actionEvent The event triggering the new game (can be null).
     */
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        gameState.set(GameState.RUNNING);
    }

    /**
     * Toggles the game state between Running and Paused.
     *
     * @param actionEvent The event triggering the pause (can be null).
     */
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

    /**
     * Displays a floating score bonus or notification text.
     *
     * @param text The text to display (e.g., "+100" or "LEVEL 2").
     */
    public void showScoreBonus(String text) {
        NotificationPanel notificationPanel = new NotificationPanel(text);
        int activeNotifications = groupNotification.getChildren().size();
        double yOffset = activeNotifications * 25;

        notificationPanel.setTranslateY(yOffset);

        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());
    }

    /**
     * Delegates the rendering of the held piece to the BoardRenderer.
     *
     * @param shape The shape matrix of the held brick.
     */
    public void showHoldPiece(int[][] shape) {
        boardRenderer.showHoldPiece(shape);
    }

    /**
     * Updates the High Score label text.
     *
     * @param score The high score value to display.
     */
    public void setHighScore(int score) {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(score));
        }
    }

    /**
     * Toggles the in-game pause menu overlay.
     * <p>
     * If the game is running, it pauses the timeline and shows the menu.
     * If paused, it resumes the timeline and hides the menu.
     * </p>
     */
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

    /**
     * Handler for the "Resume" button in the pause menu.
     *
     * @param event The button click event.
     */
    @FXML
    public void onResume(ActionEvent event) {
        togglePauseMenu();
    }

    /**
     * Handler for the "Main Menu" button.
     * <p>
     * Stops the game loop and navigates back to the main menu scene.
     * </p>
     *
     * @param event The button click event.
     * @throws IOException If the mainMenu.fxml file cannot be loaded.
     */
    @FXML
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