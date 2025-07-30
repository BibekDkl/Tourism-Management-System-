package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.LanguageManager;
import com.example.nepaltourismmanagement.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    // Constants - Updated
    private static final String CURRENT_DATE = "2025-07-30 07:04:01";
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

        // Set up register link click handler
        if (registerLink != null) {
            registerLink.setOnAction(this::handleRegisterLink);
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

            // First try using SceneManager (preferred approach)
            try {
                sceneManager.switchToRegisterPage(event);
                return; // If successful, exit the method
            } catch (IOException e) {
                System.out.println("SceneManager failed, trying direct approach: " + e.getMessage());
                // Continue with fallback approach
            }

            // Fallback approach: Load FXML directly
            Stage stage = (Stage) registerLink.getScene().getWindow();

            // Try multiple possible locations for the registerPage.fxml
            Parent root = loadRegisterPage();

            if (root != null) {
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Register - Trip Sewa Nepal");
                stage.show();
                System.out.println("Successfully navigated to register page using fallback method");
            } else {
                throw new IOException("Could not load register page from any location");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to register page: " + e.getMessage() +
                            "\nPlease check if registerPage.fxml exists in resources.");

            // Log paths for debugging
            System.err.println("Current directory: " + System.getProperty("user.dir"));
            System.err.println("Checking fxml paths...");
            debugResourcePaths("registerPage.fxml");
        }
    }

    private Parent loadRegisterPage() throws IOException {
        // Try different possible locations for the FXML file
        String[] possiblePaths = {
                "/fxml/registerPage.fxml",
                "/registerPage.fxml",
                "fxml/registerPage.fxml",
                "registerPage.fxml"
        };

        // First try as resources
        for (String path : possiblePaths) {
            System.out.println("Trying to load: " + path);
            URL url = getClass().getResource(path);
            if (url != null) {
                System.out.println("Found at: " + url);
                return FXMLLoader.load(url);
            }
        }

        // Then try file system locations
        String[] filePaths = {
                "src/main/resources/fxml/registerPage.fxml",
                "src/main/resources/registerPage.fxml",
                "target/classes/fxml/registerPage.fxml",
                "target/classes/registerPage.fxml"
        };

        for (String path : filePaths) {
            System.out.println("Trying file: " + path);
            File file = new File(path);
            if (file.exists()) {
                System.out.println("Found at: " + file.getAbsolutePath());
                return FXMLLoader.load(file.toURI().toURL());
            }
        }

        // Create a basic register page programmatically if all else fails
        return createEmergencyRegisterPage();
    }

    private Parent createEmergencyRegisterPage() {
        System.out.println("Creating emergency register page");

        // Create a basic registration form
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(10);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(20));
        root.setStyle("-fx-background-color: white;");

        Label title = new Label("Emergency Registration Form");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white;");

        Hyperlink backLink = new Hyperlink("Back to Login");
        backLink.setOnAction(e -> {
            try {
                sceneManager.switchToLoginPage(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(
                title,
                new Label("This is an emergency registration form as the regular form couldn't be loaded."),
                new Label("Username:"),
                usernameField,
                new Label("Email:"),
                emailField,
                new Label("Password:"),
                passwordField,
                registerButton,
                backLink
        );

        return root;
    }

    private void debugResourcePaths(String filename) {
        // Try different resource paths
        String[] paths = {
                "/fxml/" + filename,
                "/" + filename,
                "fxml/" + filename,
                filename
        };

        for (String path : paths) {
            URL url = getClass().getResource(path);
            System.err.println("Resource " + path + ": " + (url != null ? "EXISTS" : "not found"));
        }

        // Check file system paths
        String[] filePaths = {
                "src/main/resources/fxml/" + filename,
                "src/main/resources/" + filename,
                "target/classes/fxml/" + filename,
                "target/classes/" + filename
        };

        for (String path : filePaths) {
            File file = new File(path);
            System.err.println("File " + path + ": " + (file.exists() ? "EXISTS (" + file.length() + " bytes)" : "not found"));
        }

        // List contents of resources directory
        try {
            File resourcesDir = new File("src/main/resources");
            if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                System.err.println("Contents of src/main/resources:");
                listDirectoryContents(resourcesDir, 1);
            }

            File fxmlDir = new File("src/main/resources/fxml");
            if (fxmlDir.exists() && fxmlDir.isDirectory()) {
                System.err.println("Contents of src/main/resources/fxml:");
                listDirectoryContents(fxmlDir, 1);
            }
        } catch (Exception e) {
            System.err.println("Error listing directories: " + e.getMessage());
        }
    }

    private void listDirectoryContents(File dir, int level) {
        String indent = "  ".repeat(level);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                System.err.println(indent + file.getName() + (file.isDirectory() ? "/" : " (" + file.length() + " bytes)"));
                if (file.isDirectory() && level < 3) {
                    listDirectoryContents(file, level + 1);
                }
            }
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

    // These getters and setters may be useful for testing
    public Hyperlink getRegisterLink() {
        return registerLink;
    }

    public void setRegisterLink(Hyperlink registerLink) {
        this.registerLink = registerLink;
    }
}