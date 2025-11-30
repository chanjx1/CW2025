package com.comp2042.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * The overlay panel displayed when the game ends.
 * <p>
 * This class extends {@link BorderPane} to center a large "GAME OVER" label
 * on the screen. It is toggled visible/invisible by the {@link GuiController}
 * based on the game state.
 * </p>
 */
public class GameOverPanel extends BorderPane {

    /**
     * Constructs the Game Over panel.
     * <p>
     * Initializes the label with the text "GAME OVER", applies the
     * "gameOverStyle" CSS class for styling (red background, large font),
     * and centers it within the pane layout.
     * </p>
     */
    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        setCenter(gameOverLabel);
    }

}