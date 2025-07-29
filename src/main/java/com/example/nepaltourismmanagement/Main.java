package com.example.nepaltourismmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.File;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Create a data directory if it doesn't exist
        new File("data").mkdirs();

        // Load the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Configure and display the primary stage
        primaryStage.setTitle("Nepal Trek Paradise - Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void switchScene(Scene scene, String title) {
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}