package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * The main entry point for the TetrisJFX application.
 * <p>
 * This class extends {@link Application} to manage the JavaFX lifecycle.
 * It is responsible for loading the initial resources (Main Menu FXML) and setting up
 * the primary stage (window) settings such as title and dimensions.
 * </p>
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * <p>
     * This method is called after the system is ready for the application to begin running.
     * It loads the {@code mainMenu.fxml} layout, sets the scene dimensions to 800x600,
     * and displays the primary window.
     * </p>
     *
     * @param primaryStage The primary stage for this application, onto which
     * the application scene can be set.
     * @throws Exception If the FXML resource cannot be loaded.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * <p>
     * main() serves only as fallback in case the application can not be launched
     * through deployment artifacts, e.g., in IDEs with limited FX support.
     * </p>
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
