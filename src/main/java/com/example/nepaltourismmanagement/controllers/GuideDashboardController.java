package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.Booking;
import com.example.nepaltourismmanagement.models.Guide;
import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.DateTimeUtil;
import com.example.nepaltourismmanagement.utils.SceneManager;
import com.example.nepaltourismmanagement.utils.WeatherUtil;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class GuideDashboardController implements Initializable {

    @FXML private Label userLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label footerDateTimeLabel;
    @FXML private Label weatherLabel;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;

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
    private User currentUser;
    private Guide guideProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseUtil = new DatabaseUtil();
        weatherUtil = new WeatherUtil();
        sceneManager = new SceneManager();

        setupClock();
        initializeUpcomingTripsTable();
        updateWeatherInfo();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        userLabel.setText(user.getUsername());

        // Load guide profile information
        loadGuideProfile();

        // Load assigned trips
        loadAssignedTrips();
    }

    private void setupClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = DateTimeUtil.formatDateTime(now);
            dateTimeLabel.setText(formattedDateTime);
            footerDateTimeLabel.setText(formattedDateTime);
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
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
                experienceLabel.setText(guideProfile.getYearsOfExperience() + " years");

//                // Calculate earnings
//                double totalEarnings = databaseUtil.calculateGuideEarnings(guideProfile.getId());
//                totalEarningsLabel.setText(String.format("$%.2f", totalEarnings));
//
//                double monthlyEarnings = databaseUtil.calculateGuideMonthlyEarnings(guideProfile.getId());
//                monthlyEarningsLabel.setText(String.format("$%.2f", monthlyEarnings));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Profile Error",
                    "Could not load guide profile: " + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load assigned trips: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateWeatherInfo() {
        try {
            // In a real app, this would use an actual weather API
            // For demonstration, we'll use sample data
            String weather = weatherUtil.getCurrentWeather("Kathmandu");
            weatherLabel.setText("Weather: " + weather);
            currentWeatherLabel.setText("Temperature: 25°C");
            humidityLabel.setText("Humidity: 60%");
            windSpeedLabel.setText("Wind Speed: 10 km/h");
        } catch (Exception e) {
            weatherLabel.setText("Weather data unavailable");
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to login page: " + e.getMessage());
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