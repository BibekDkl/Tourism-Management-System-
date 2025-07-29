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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField fullnameField;
    @FXML private TextField nationalityField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private TextField passportField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField contactField1; // Emergency contact
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button registerButton;
    @FXML private Hyperlink loginLink;

    private DatabaseUtil databaseUtil;
    private ValidationUtil validationUtil;
    private SceneManager sceneManager;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseUtil = new DatabaseUtil();
        validationUtil = new ValidationUtil();
        sceneManager = new SceneManager();

        // Populate role dropdown
        roleComboBox.setItems(FXCollections.observableArrayList("Tourist", "Guide"));
        roleComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
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
    private void handleLoginLink() {
        try {
            sceneManager.switchToLoginPage(loginLink);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        // Check required fields
        if (fullnameField.getText().trim().isEmpty() ||
                nationalityField.getText().trim().isEmpty() ||
                contactField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty() ||
                confirmPasswordField.getText().trim().isEmpty() ||
                contactField1.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error", "All required fields must be filled.");
            return false;
        }

        // Validate email format
        if (!validationUtil.isValidEmail(emailField.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid email format.");
            return false;
        }

        // Validate phone number
        if (!validationUtil.isValidPhoneNumber(contactField.getText().trim())) {
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

        // Generate a username from email (first part before @)
        String email = emailField.getText().trim();
        String username = email.split("@")[0];

        user.setUsername(username);
        user.setFullName(fullnameField.getText().trim());
        user.setNationality(nationalityField.getText().trim());
        user.setContactNumber(contactField.getText().trim());
        user.setEmail(email);
        user.setPassportNumber(passportField.getText().trim());
        user.setPassword(passwordField.getText()); // In real app, hash this before storing
        user.setEmergencyContact(contactField1.getText().trim());

        // Set role based on selection
        String roleSelection = roleComboBox.getValue();
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