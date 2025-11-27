package com.comp2042;

import com.comp2042.controller.GameController;
import com.comp2042.view.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // CHANGE: Load mainMenu.fxml instead of gameLayout.fxml
        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        // NOTE: We do NOT create GameController here anymore.
        // MenuController will do that when "New Game" is clicked.

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
