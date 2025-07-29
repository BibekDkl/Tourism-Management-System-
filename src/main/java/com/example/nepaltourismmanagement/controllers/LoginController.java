package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private CheckBox rememberMe;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Hyperlink registerLink;
    @FXML private Label adminHintLabel; // New label for admin hint

    private DatabaseUtil databaseUtil;
    private SceneManager sceneManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseUtil = new DatabaseUtil();
        sceneManager = new SceneManager();

        // If remember me is enabled, try to load saved credentials
        loadSavedCredentials();

        // Set focus to username field
        usernameField.requestFocus();

        // Add admin credentials hint (can be removed in production)
        adminHintLabel.setText("Admin Login: username = admin, password = admin123");

        // Add tooltip for admin credentials
        Tooltip adminTip = new Tooltip("For admin access: username = admin, password = admin123");
        usernameField.setTooltip(adminTip);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Username or password cannot be empty.");
            return;
        }

        try {
            // Authenticate user
            User user = databaseUtil.authenticateUser(username, password);

            if (user != null) {
                // Save credentials if remember me is checked
                if (rememberMe.isSelected()) {
                    saveCredentials(username, password);
                }

                // Navigate to appropriate dashboard based on role
                switch (user.getRole()) {
                    case ADMIN:
                        sceneManager.switchToAdminDashboard(event, user);
                        break;
                    case GUIDE:
                        sceneManager.switchToGuideDashboard(event, user);
                        break;
                    case TOURIST:
                        sceneManager.switchToTouristDashboard(event, user);
                        break;
                    default:
                        showAlert(Alert.AlertType.ERROR, "Role Error", "Unknown role assigned to user.");
                        break;
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred during login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterLink(ActionEvent event) {
        try {
            System.out.println("Register link clicked, attempting to navigate...");
            sceneManager.switchToRegisterPage(event);
        } catch (IOException e) {
            System.out.println("Error loading register page: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to register page: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        // Implement forgot password functionality
        showAlert(Alert.AlertType.INFORMATION, "Forgot Password", "Reset password feature will be available soon.");
    }

    private void loadSavedCredentials() {
        // Implementation to load saved credentials from preferences or secure storage
        // For demo purposes, we can pre-fill the admin username
        // usernameField.setText("admin");
    }

    private void saveCredentials(String username, String password) {
        // Implementation to save credentials if remember me is checked
        // This is a placeholder - in a real app, use secure storage
        System.out.println("Saving credentials for: " + username);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}