package com.example.nepaltourismmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class Main extends Application {

    public static final String CURRENT_DATE = "2025-07-30 06:06:29";
    public static final String CURRENT_USER = "BibekDkl";

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            System.out.println("Starting Nepal Tourism Management System");
            System.out.println("Current time: " + CURRENT_DATE);
            System.out.println("Current user: " + CURRENT_USER);

            // Load the login FXML file
            URL loginFxmlUrl = getClass().getResource("/fxml/login.fxml");

            if (loginFxmlUrl == null) {
                // Try without /fxml/ prefix
                loginFxmlUrl = getClass().getResource("/login.fxml");
                System.out.println("Trying alternate path: /login.fxml");
            }

            if (loginFxmlUrl == null) {
                System.err.println("Could not find login.fxml in classpath. Checking file system...");

                // Try as direct file path
                File file = new File("src/main/resources/fxml/login.fxml");
                if (file.exists()) {
                    loginFxmlUrl = file.toURI().toURL();
                    System.out.println("Found login.fxml in file system: " + file.getAbsolutePath());
                } else {
                    throw new RuntimeException("Could not find login.fxml anywhere");
                }
            }

            FXMLLoader loader = new FXMLLoader(loginFxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Nepal Tourism Management");
            primaryStage.show();

            System.out.println("Application started successfully");
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}