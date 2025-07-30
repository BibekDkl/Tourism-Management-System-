package com.example.nepaltourismmanagement.utils;

import com.example.nepaltourismmanagement.controllers.AdminDashboardController;
import com.example.nepaltourismmanagement.controllers.GuideDashboardController;
import com.example.nepaltourismmanagement.controllers.TouristDashboardController;
import com.example.nepaltourismmanagement.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SceneManager {

    private static final String CURRENT_DATE = "2025-07-30 06:21:09";
    private static final String CURRENT_USER = "BibekDkl";

    public void switchToLoginPage(ActionEvent event) throws IOException {
        System.out.println("Switching to login page");
        Parent root = loadFXML("login.fxml");
        setScene(event, root);
    }

    public void switchToRegisterPage(ActionEvent event) throws IOException {
        System.out.println("Switching to register page");
        Parent root = loadFXML("registerPage.fxml");
        setScene(event, root);
    }

    public void switchToAdminDashboard(ActionEvent event, User user) throws IOException {
        System.out.println("Switching to admin dashboard for user: " + user.getUsername());
        FXMLLoader loader = new FXMLLoader();
        Parent root = loadFXML("adminDashboard.fxml", loader);

        try {
            AdminDashboardController controller = loader.getController();
            controller.setCurrentUser(user);
        } catch (Exception e) {
            System.err.println("Error setting user in admin controller: " + e.getMessage());
            e.printStackTrace();
            // Continue anyway to show dashboard
        }

        setScene(event, root);
    }

    public void switchToTouristDashboard(ActionEvent event, User user) throws IOException {
        System.out.println("Switching to tourist dashboard for user: " + user.getUsername());
        FXMLLoader loader = new FXMLLoader();
        Parent root = loadFXML("touristDashboard.fxml", loader);

        try {
            TouristDashboardController controller = loader.getController();
            controller.setCurrentUser(user);
        } catch (Exception e) {
            System.err.println("Error setting user in tourist controller: " + e.getMessage());
            e.printStackTrace();
            // Continue anyway to show dashboard
        }

        setScene(event, root);
    }

    public void switchToGuideDashboard(ActionEvent event, User user) throws IOException {
        System.out.println("Switching to guide dashboard for user: " + user.getUsername());
        FXMLLoader loader = new FXMLLoader();
        Parent root = loadFXML("guideDashboard.fxml", loader);

        try {
            GuideDashboardController controller = loader.getController();
            controller.setCurrentUser(user);
        } catch (Exception e) {
            System.err.println("Error setting user in guide controller: " + e.getMessage());
            e.printStackTrace();
            // Continue anyway to show dashboard
        }

        setScene(event, root);
    }

    private Parent loadFXML(String fxmlFile) throws IOException {
        return loadFXML(fxmlFile, null);
    }

    private Parent loadFXML(String fxmlFile, FXMLLoader providedLoader) throws IOException {
        FXMLLoader loader = providedLoader != null ? providedLoader : new FXMLLoader();

        // Try different paths to find the FXML
        URL fxmlUrl = null;

        // First try with /fxml/ prefix
        fxmlUrl = getClass().getResource("/fxml/" + fxmlFile);

        // Then try without prefix
        if (fxmlUrl == null) {
            fxmlUrl = getClass().getResource("/" + fxmlFile);
        }

        // Try just the filename
        if (fxmlUrl == null) {
            fxmlUrl = getClass().getResource(fxmlFile);
        }

        // If still not found, try file system paths
        if (fxmlUrl == null) {
            File file = new File("src/main/resources/fxml/" + fxmlFile);
            if (file.exists()) {
                fxmlUrl = file.toURI().toURL();
            }
        }

        if (fxmlUrl == null) {
            File file = new File("src/main/resources/" + fxmlFile);
            if (file.exists()) {
                fxmlUrl = file.toURI().toURL();
            }
        }

        if (fxmlUrl == null) {
            showErrorAlert("FXML Not Found", "Could not find FXML file: " + fxmlFile);
            throw new IOException("Could not find FXML file: " + fxmlFile);
        }

        // Set the URL in the loader
        System.out.println("Loading FXML from: " + fxmlUrl);
        loader.setLocation(fxmlUrl);

        // Try to load
        try {
            return loader.load();
        } catch (IOException e) {
            // Try as input stream as a last resortadmincontroller and databesncontroller?

            try {
                InputStream is = fxmlUrl.openStream();
                loader = new FXMLLoader();
                return loader.load(is);
            } catch (Exception e2) {
                System.err.println("Failed to load FXML from stream: " + e2.getMessage());
                showErrorAlert("FXML Loading Error", "Error loading FXML file: " + e2.getMessage());
                throw new IOException("Error loading FXML file: " + fxmlFile, e2);
            }
        }
    }

    private void setScene(ActionEvent event, Parent root) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}