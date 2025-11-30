package com.comp2042.view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * A custom UI component for displaying temporary floating notifications.
 * <p>
 * This class is used to show score bonuses ("+100") or level-up messages ("LEVEL 2")
 * that appear on the screen, float upwards, fade out, and then remove themselves.
 * </p>
 */
public class NotificationPanel extends BorderPane {

    /**
     * Constructs a new notification panel with the specified text.
     * <p>
     * Sets up the label styling, including a glow effect and custom CSS class.
     * </p>
     *
     * @param text The message to display (e.g., score amount or level).
     */
    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(0.6);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);
    }

    /**
     * Triggers the entry animation for this notification.
     * <p>
     * The notification will float upwards (translate Y) and fade out simultaneously.
     * Once the animation completes, the node is automatically removed from the parent list.
     * </p>
     *
     * @param list The parent container's children list, used to remove this node after animation.
     */
    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(2500), this);

        // FIX: Use setByY instead of setToY so it floats up relative to where we place it
        tt.setByY(-40);

        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(event -> list.remove(NotificationPanel.this));
        transition.play();
    }
}