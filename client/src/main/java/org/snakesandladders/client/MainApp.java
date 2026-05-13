package org.snakesandladders.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.snakesandladders.client.controller.EndController;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Snakes and Ladders");
        primaryStage.setResizable(false);
        showStartScreen();
        primaryStage.show();
    }

    public static void showStartScreen() throws Exception {
        loadScene("/org/snakesandladders/client/fxml/start.fxml", 600, 500);
    }

    public static void showGameScreen() throws Exception {
        loadScene("/org/snakesandladders/client/fxml/game.fxml", 900, 750);
    }

    public static void showEndScreen(String winnerName, boolean isWinner) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/org/snakesandladders/client/fxml/end.fxml"));
        Parent root = loader.load();
        EndController controller = loader.getController();
        controller.initialize(winnerName, isWinner);
        primaryStage.setScene(new Scene(root, 600, 500));
    }

    private static void loadScene(String fxmlPath, int width, int height) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, width, height));
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}