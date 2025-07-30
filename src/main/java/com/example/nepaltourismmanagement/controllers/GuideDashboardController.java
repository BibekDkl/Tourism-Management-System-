package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.Booking;
import com.example.nepaltourismmanagement.models.Guide;
import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.LanguageManager;
import com.example.nepaltourismmanagement.utils.SceneManager;
import com.example.nepaltourismmanagement.utils.WeatherUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuideDashboardController implements Initializable {

    @FXML private Label userLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label footerDateTimeLabel;
    @FXML private Label weatherLabel;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private ComboBox<String> languageCombo;
    @FXML private VBox rootContainer; // Root container for the entire dashboard

    // Upcoming Trips Tab
    @FXML private TableView<Booking> upcomingTripsTable;
    @FXML private TableColumn<Booking, String> tripNameColumn;
    @FXML private TableColumn<Booking, String> startDateColumn;
    @FXML private TableColumn<Booking, Integer> durationColumn;
    @FXML private TableColumn<Booking, String> touristsColumn;
    @FXML private TableColumn<Booking, String> statusColumn;

    // Profile Tab
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label experienceLabel;
    @FXML private Label totalEarningsLabel;
    @FXML private Label monthlyEarningsLabel;

    // Weather Tab
    @FXML private Label currentWeatherLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windSpeedLabel;

    // Dependencies
    private DatabaseUtil databaseUtil;
    private WeatherUtil weatherUtil;
    private SceneManager sceneManager;
    private LanguageManager languageManager;
    private User currentUser;
    private Guide guideProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing GuideDashboardController");
        databaseUtil = new DatabaseUtil();
        weatherUtil = new WeatherUtil();
        sceneManager = new SceneManager();
        languageManager = LanguageManager.getInstance();

        initializeLanguageCombo();
        setupDateTime();
        initializeUpcomingTripsTable();
        updateWeatherInfo();

        System.out.println("GuideDashboardController initialized");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        userLabel.setText(user.getUsername());

        // Load guide profile information
        loadGuideProfile();

        // Load assigned trips
        loadAssignedTrips();
    }

    private void initializeLanguageCombo() {
        languageCombo.setItems(FXCollections.observableArrayList("English", "Nepali", "Hindi"));
        languageCombo.setValue(languageManager.getCurrentLanguage());

        // Add listener for language change
        languageCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                handleLanguageChange(newValue);
            }
        });
    }

    private void handleLanguageChange(String language) {
        if ("Hindi".equals(language)) {
            // Hindi not fully supported yet
            showAlert(Alert.AlertType.INFORMATION, "Language Support",
                    "Hindi language support coming soon. Defaulting to English.");
            languageCombo.setValue("English");
            return;
        }

        System.out.println("Changing language to: " + language);
        languageManager.setLanguageAndTranslate(language, rootContainer);
    }

    private void setupDateTime() {
        dateTimeLabel.setText(languageManager.getCurrentDateTime());
        footerDateTimeLabel.setText(languageManager.getCurrentDateTime());
    }

    private void initializeUpcomingTripsTable() {
        tripNameColumn.setCellValueFactory(new PropertyValueFactory<>("trekName"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        touristsColumn.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadGuideProfile() {
        try {
            guideProfile = databaseUtil.getGuideByUserId(currentUser.getId());

            if (guideProfile != null) {
                fullNameLabel.setText(guideProfile.getName());
                emailLabel.setText(guideProfile.getEmail());
                phoneLabel.setText(guideProfile.getPhoneNumber());
                experienceLabel.setText(guideProfile.getYearsOfExperience() + " " +
                        languageManager.translate("years"));

                // Default earnings if calculation method isn't implemented
                totalEarningsLabel.setText("$3,450.00");
                monthlyEarningsLabel.setText("$875.00");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Profile Error"),
                    languageManager.translate("Could not load guide profile") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAssignedTrips() {
        try {
            if (guideProfile != null) {
                List<Booking> trips = databaseUtil.getAssignedTripsForGuide(guideProfile.getId());
                upcomingTripsTable.setItems(FXCollections.observableArrayList(trips));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Database Error"),
                    languageManager.translate("Could not load assigned trips") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateWeatherInfo() {
        try {
            // In a real app, this would use an actual weather API
            // For demonstration, we'll use sample data
            String weather = weatherUtil.getCurrentWeather("Kathmandu");
            weatherLabel.setText(languageManager.translate("Weather") + ": " + weather);
            currentWeatherLabel.setText(languageManager.translate("Temperature") + ": 25°C");
            humidityLabel.setText(languageManager.translate("Humidity") + ": 60%");
            windSpeedLabel.setText(languageManager.translate("Wind Speed") + ": 10 km/h");
        } catch (Exception e) {
            weatherLabel.setText(languageManager.translate("Weather data unavailable"));
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadGuideProfile();
        loadAssignedTrips();
        updateWeatherInfo();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            sceneManager.switchToLoginPage(event);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Navigation Error"),
                    languageManager.translate("Could not navigate to login page") + ": " + e.getMessage());
            e.printStackTrace();
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