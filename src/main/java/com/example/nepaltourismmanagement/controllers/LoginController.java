package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.LanguageManager;
import com.example.nepaltourismmanagement.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
    @FXML private Label adminHintLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label currentUserLabel;
    @FXML private ComboBox<String> languageCombo;

    private DatabaseUtil databaseUtil;
    private SceneManager sceneManager;
    private LanguageManager languageManager;

    // Constants
    private static final String CURRENT_DATE = "2025-07-30 06:21:09";
    private static final String CURRENT_USER = "BibekDkl";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing LoginController");
        databaseUtil = new DatabaseUtil();
        sceneManager = new SceneManager();
        languageManager = LanguageManager.getInstance();

        // Set current date and user
        if (currentTimeLabel != null) {
            currentTimeLabel.setText("Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): " + CURRENT_DATE);
        }

        if (currentUserLabel != null) {
            currentUserLabel.setText("Current User's Login: " + CURRENT_USER);
        }

        // Set admin hint if label exists
        if (adminHintLabel != null) {
            adminHintLabel.setText("Admin Login: username = admin, password = admin123\n" +
                    "Tourist Login: username = tourist, password = tourist123\n" +
                    "Guide Login: username = guide, password = guide123");
        }

        // Initialize language combo box if it exists
        if (languageCombo != null) {
            languageCombo.getItems().addAll("English", "Nepali");
            languageCombo.setValue(languageManager.getCurrentLanguage());
        }

        // Set focus to username field
        if (usernameField != null) {
            usernameField.requestFocus();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        System.out.println("Login button clicked");

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
                    System.out.println("Remembering credentials for: " + username);
                }

                // Navigate to appropriate dashboard based on role
                switch (user.getRole()) {
                    case ADMIN:
                        System.out.println("Navigating to admin dashboard");
                        sceneManager.switchToAdminDashboard(event, user);
                        break;
                    case GUIDE:
                        System.out.println("Navigating to guide dashboard");
                        sceneManager.switchToGuideDashboard(event, user);
                        break;
                    case TOURIST:
                        System.out.println("Navigating to tourist dashboard");
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
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred during login: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterLink(ActionEvent event) {
        try {
            System.out.println("Register link clicked, attempting to navigate...");
            sceneManager.switchToRegisterPage(event);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to register page: " + e.getMessage());
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Forgot Password", "Reset password feature will be available soon.");
    }

    @FXML
    private void handleLanguageChange(ActionEvent event) {
        if (languageCombo != null && languageCombo.getValue() != null) {
            String selectedLanguage = languageCombo.getValue();
            System.out.println("Language changed to: " + selectedLanguage);

            // Update UI with new language
            languageManager.setLanguageAndTranslate(selectedLanguage,
                    (Parent)languageCombo.getScene().getRoot());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}