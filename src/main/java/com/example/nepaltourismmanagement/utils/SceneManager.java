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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneManager {

    // Path to FXML files - adjust these to match your actual file locations
    private static final String LOGIN_PAGE = "/fxml/login.fxml";
    private static final String REGISTER_PAGE = "/fxml/registePage.fxml";
    private static final String ADMIN_DASHBOARD = "/fxml/adminDashboard.fxml";
    private static final String GUIDE_DASHBOARD = "/fxml/guideDashboard.fxml";
    private static final String TOURIST_DASHBOARD = "/fxml/touristDashboard.fxml";

    // Switch to login page
    public void switchToLoginPage(ActionEvent event) throws IOException {
        switchScene(event, LOGIN_PAGE);
    }

    // Switch to login page using Hyperlink or any Node
    public void switchToLoginPage(Node node) throws IOException {
        switchScene(node, LOGIN_PAGE);
    }

    // Switch to register page
    public void switchToRegisterPage(ActionEvent event) throws IOException {
        System.out.println("Switching to register page...");
        switchScene(event, REGISTER_PAGE);
    }

    // Switch to register page using Hyperlink or any Node
    public void switchToRegisterPage(Node node) throws IOException {
        switchScene(node, REGISTER_PAGE);
    }

    // Switch to admin dashboard
    public void switchToAdminDashboard(ActionEvent event, User user) throws IOException {
        System.out.println("Switching to admin dashboard for user: " + user.getUsername());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ADMIN_DASHBOARD));
            Parent root = loader.load();

            // Pass user data to the admin controller
            AdminDashboardController controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Could not get AdminDashboardController");
            }

            controller.setCurrentUser(user);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard - Trip Sewa Nepal");
            stage.show();

            System.out.println("Admin dashboard loaded successfully");
        } catch (Exception e) {
            System.err.println("Error switching to admin dashboard: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to switch to admin dashboard: " + e.getMessage(), e);
        }
    }

    // Switch to guide dashboard
    public void switchToGuideDashboard(ActionEvent event, User user) throws IOException {
        System.out.println("Switching to guide dashboard for user: " + user.getUsername());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(GUIDE_DASHBOARD));
            Parent root = loader.load();

            // Pass user data to the guide controller
            GuideDashboardController controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Could not get GuideDashboardController");
            }

            controller.setCurrentUser(user);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Guide Dashboard - Trip Sewa Nepal");
            stage.show();

            System.out.println("Guide dashboard loaded successfully");
        } catch (Exception e) {
            System.err.println("Error switching to guide dashboard: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to switch to guide dashboard: " + e.getMessage(), e);
        }
    }

    // Switch to tourist dashboard
    public void switchToTouristDashboard(ActionEvent event, User user) throws IOException {
        System.out.println("Switching to tourist dashboard for user: " + user.getUsername() + " with ID: " + user.getId());

        try {
            // Try to load the FXML file
            URL fxmlUrl = getClass().getResource(TOURIST_DASHBOARD);
            if (fxmlUrl == null) {
                // If not found, try alternative locations
                System.out.println("Tourist dashboard FXML not found at: " + TOURIST_DASHBOARD);
                fxmlUrl = getClass().getResource("/TouristDashboard.fxml");

                if (fxmlUrl == null) {
                    throw new IOException("Could not find FXML file for tourist dashboard");
                }
            }

            System.out.println("Loading tourist dashboard from: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Get the controller and set the user
            TouristDashboardController controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Could not get TouristDashboardController");
            }

            // Set the current user - this is the crucial step that was failing
            System.out.println("Setting user in TouristDashboardController: " + user.getUsername());
            controller.setCurrentUser(user);

            // Set up the stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Tourist Dashboard - Trip Sewa Nepal");
            stage.show();

            System.out.println("Tourist dashboard loaded successfully");
        } catch (Exception e) {
            System.err.println("Error switching to tourist dashboard: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to switch to tourist dashboard: " + e.getMessage(), e);
        }
    }

    // Generic method to switch scene from ActionEvent
    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        try {
            System.out.println("Loading FXML: " + fxmlPath);
            URL fxmlUrl = getClass().getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("FXML file not found at: " + fxmlPath);

                // Try alternative path formats
                String altPath = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : "/" + fxmlPath;
                fxmlUrl = getClass().getResource(altPath);

                if (fxmlUrl == null) {
                    throw new IOException("FXML file not found: " + fxmlPath);
                }

                System.out.println("Found FXML at alternative path: " + altPath);
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            System.out.println("Scene switched successfully");
        } catch (Exception e) {
            System.err.println("Error switching scene: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to switch scene: " + e.getMessage(), e);
        }
    }

    // Generic method to switch scene from Node
    private void switchScene(Node node, String fxmlPath) throws IOException {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("FXML file not found at: " + fxmlPath);

                // Try alternative path formats
                String altPath = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : "/" + fxmlPath;
                fxmlUrl = getClass().getResource(altPath);

                if (fxmlUrl == null) {
                    throw new IOException("FXML file not found: " + fxmlPath);
                }

                System.out.println("Found FXML at alternative path: " + altPath);
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error switching scene: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to switch scene: " + e.getMessage(), e);
        }
    }
}