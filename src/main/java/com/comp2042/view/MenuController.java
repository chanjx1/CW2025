package com.comp2042.view;

import com.comp2042.controller.GameController;
import com.comp2042.model.ScoreManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MenuController {

    @FXML
    public void onNewGame(ActionEvent event) throws IOException {
        // 1. Load the Game Layout
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();

        // 2. Initialize the Game Controller (Connects Logic to View)
        GuiController c = loader.getController();
        new GameController(c); // This starts the game loop

        // 3. Switch the Scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void onHighScores(ActionEvent event) {
        // Simple popup to show the high score
        ScoreManager sm = new ScoreManager();
        int highScore = sm.getHighScore();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("High Scores");
        alert.setHeaderText("Leaderboard Top Score");
        alert.setContentText("The current champion score is: " + highScore);
        alert.showAndWait();
    }

    @FXML
    public void onControls(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Controls");
        alert.setHeaderText("Keyboard Controls");

        String content = """
            Movement:
            ← / A  : Move Left
            → / D  : Move Right
            ↓ / S  : Soft Drop
            
            Actions:
            ↑ / W  : Rotate
            SPACE  : Hard Drop
            SHIFT / C : Hold Piece
            
            Game:
            P / ESC : Pause & Menu
            N       : New Game
            """;

        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void onExit(ActionEvent event) {
        System.exit(0);
    }
}