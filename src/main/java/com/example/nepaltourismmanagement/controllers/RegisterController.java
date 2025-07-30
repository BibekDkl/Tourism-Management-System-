package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.models.UserRole;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.SceneManager;
import com.example.nepaltourismmanagement.utils.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    // Constants for current date and user
    private static final String CURRENT_DATE = "2025-07-30 06:58:45";
    private static final String CURRENT_USER = "BibekDkl";

    // FXML field references - renamed to match your FXML
    @FXML private TextField fullNameField;  // Changed from fullnameField
    @FXML private TextField emailField;
    @FXML private TextField phoneField;     // Changed from contactField
    @FXML private TextField usernameField;  // Added as it's in your FXML
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> userRoleCombo; // Changed from roleComboBox
    @FXML private Button registerButton;
    @FXML private Hyperlink loginLink;
    @FXML private Label currentTimeLabel;   // Added
    @FXML private Label currentUserLabel;   // Added

    private DatabaseUtil databaseUtil;
    private ValidationUtil validationUtil;
    private SceneManager sceneManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("RegisterController initializing");
        databaseUtil = new DatabaseUtil();
        validationUtil = new ValidationUtil();
        sceneManager = new SceneManager();

        // Set current date and user labels
        if (currentTimeLabel != null) {
            currentTimeLabel.setText("Current Date and Time (UTC - YYYY-MM-DD HH:MM:SS formatted): " + CURRENT_DATE);
        }

        if (currentUserLabel != null) {
            currentUserLabel.setText("Current User's Login: " + CURRENT_USER);
        }

        // Populate role dropdown
        if (userRoleCombo != null) {
            userRoleCombo.setItems(FXCollections.observableArrayList("Tourist", "Guide"));
            userRoleCombo.getSelectionModel().selectFirst();
        } else {
            System.err.println("WARNING: userRoleCombo is null!");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        System.out.println("Register button clicked");

        // Validate fields
        if (!validateFields()) {
            return;
        }

        // Create user object from form data
        User user = createUserFromForm();

        try {
            // Register user in the file storage
            boolean success = databaseUtil.registerUser(user);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                        "Welcome to Trip Sewa Nepal! Please login with your credentials.");
                sceneManager.switchToLoginPage(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed",
                        "Could not register the user. Please try again.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Registration Error",
                    "An error occurred during registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginLink(ActionEvent event) {
        try {
            System.out.println("Login link clicked");
            sceneManager.switchToLoginPage(event);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to login page: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        System.out.println("Validating fields");

        // Check required fields
        if (fullNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty() ||
                usernameField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty() ||
                confirmPasswordField.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error", "All required fields must be filled.");
            return false;
        }

        // Validate email format
        if (!validationUtil.isValidEmail(emailField.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid email format.");
            return false;
        }

        // Validate phone number
        if (!validationUtil.isValidPhoneNumber(phoneField.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid phone number format.");
            return false;
        }

        // Check password match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match.");
            return false;
        }

        // Check password strength
        if (!validationUtil.isStrongPassword(passwordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Password must be at least 8 characters long and include letters, numbers, and special characters.");
            return false;
        }

        return true;
    }

    private User createUserFromForm() {
        User user = new User();

        user.setUsername(usernameField.getText().trim());
        user.setFullName(fullNameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setContactNumber(phoneField.getText().trim());
        user.setPassword(passwordField.getText()); // In real app, hash this before storing

        // Set role based on selection
        String roleSelection = userRoleCombo.getValue();
        if ("Guide".equals(roleSelection)) {
            user.setRole(UserRole.GUIDE);
        } else {
            user.setRole(UserRole.TOURIST);
        }

        return user;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}