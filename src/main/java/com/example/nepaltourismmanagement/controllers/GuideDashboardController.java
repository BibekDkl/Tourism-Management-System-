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
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
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

    // Constants
    private static final String CURRENT_DATE = "2025-07-30 06:26:05";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing GuideDashboardController");

        try {
            // Initialize utilities
            databaseUtil = new DatabaseUtil();
            weatherUtil = new WeatherUtil();
            sceneManager = new SceneManager();
            languageManager = LanguageManager.getInstance();

            // Set up language combo box
            initializeLanguageCombo();

            // Set up date and time labels
            setupDateTime();

            // Initialize table columns
            initializeUpcomingTripsTable();

            // Update weather info
            updateWeatherInfo();

            System.out.println("GuideDashboardController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing GuideDashboardController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        try {
            System.out.println("Setting current user in guide dashboard: " + user);
            this.currentUser = user;

            if (userLabel != null) {
                userLabel.setText(user.getUsername());
            }

            // Load guide profile information
            loadGuideProfile();

            // Load assigned trips
            loadAssignedTrips();
        } catch (Exception e) {
            System.err.println("Error setting current user: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load guide dashboard: " + e.getMessage());
        }
    }

    private void initializeLanguageCombo() {
        try {
            if (languageCombo != null) {
                languageCombo.setItems(FXCollections.observableArrayList("English", "Nepali"));
                languageCombo.setValue(languageManager.getCurrentLanguage());

                // Add listener for language change
                languageCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.equals(oldValue)) {
                        handleLanguageChange(newValue);
                    }
                });
            } else {
                System.err.println("languageCombo is null, skipping initialization");
            }
        } catch (Exception e) {
            System.err.println("Error initializing language combo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLanguageChange(String language) {
        try {
            System.out.println("Changing language to: " + language);

            if (rootContainer != null) {
                languageManager.setLanguageAndTranslate(language, rootContainer);
            } else {
                // Try to get the scene's root if rootContainer is null
                Parent root = languageCombo.getScene().getRoot();
                languageManager.setLanguageAndTranslate(language, root);
            }

            // Update any dynamic text that might need translation
            updateDynamicTranslations();

            System.out.println("Language changed successfully");
        } catch (Exception e) {
            System.err.println("Error changing language: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Language Error", "Could not change language: " + e.getMessage());
        }
    }

    private void updateDynamicTranslations() {
        // Retranslate weather info
        updateWeatherInfo();

        // Update column headers if not automatically translated
        if (tripNameColumn != null) tripNameColumn.setText(languageManager.translate("Trek Name"));
        if (startDateColumn != null) startDateColumn.setText(languageManager.translate("Start Date"));
        if (durationColumn != null) durationColumn.setText(languageManager.translate("Duration"));
        if (touristsColumn != null) touristsColumn.setText(languageManager.translate("Tourists"));
        if (statusColumn != null) statusColumn.setText(languageManager.translate("Status"));
    }

    private void setupDateTime() {
        try {
            if (dateTimeLabel != null) {
                dateTimeLabel.setText(CURRENT_DATE);
            }

            if (footerDateTimeLabel != null) {
                footerDateTimeLabel.setText(CURRENT_DATE);
            }
        } catch (Exception e) {
            System.err.println("Error setting up date time: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeUpcomingTripsTable() {
        try {
            if (upcomingTripsTable == null) {
                System.out.println("upcomingTripsTable is null, skipping initialization");
                return;
            }

            if (tripNameColumn != null) {
                tripNameColumn.setCellValueFactory(new PropertyValueFactory<>("trekName"));
            }

            if (startDateColumn != null) {
                startDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
            }

            if (durationColumn != null) {
                durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
            }

            if (touristsColumn != null) {
                touristsColumn.setCellValueFactory(new PropertyValueFactory<>("touristName"));
            }

            if (statusColumn != null) {
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            }

            System.out.println("Trip table columns initialized");
        } catch (Exception e) {
            System.err.println("Error initializing trips table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadGuideProfile() {
        try {
            if (currentUser == null) {
                System.err.println("Current user is null, cannot load guide profile");
                return;
            }

            // For now, create a sample guide profile if database doesn't have one
            guideProfile = databaseUtil.getGuideByUserId(currentUser.getId());

            if (guideProfile == null) {
                System.out.println("No guide profile found, creating sample data");
                guideProfile = createSampleGuideProfile();
            }

            if (fullNameLabel != null) fullNameLabel.setText(guideProfile.getName());
            if (emailLabel != null) emailLabel.setText(guideProfile.getEmail());
            if (phoneLabel != null) phoneLabel.setText(guideProfile.getPhoneNumber());

            if (experienceLabel != null) {
                experienceLabel.setText(guideProfile.getYearsOfExperience() + " " +
                        languageManager.translate("years"));
            }

            if (totalEarningsLabel != null) totalEarningsLabel.setText("$3,450.00");
            if (monthlyEarningsLabel != null) monthlyEarningsLabel.setText("$875.00");

            System.out.println("Guide profile loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading guide profile: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Profile Error"),
                    languageManager.translate("Could not load guide profile") + ": " + e.getMessage());
        }
    }

    private Guide createSampleGuideProfile() {
        Guide guide = new Guide();
        guide.setId("1");
        guide.setUserId(currentUser.getId());
        guide.setName(currentUser.getUsername());
        guide.setEmail("guide@example.com");
        guide.setPhoneNumber("9876543210");
        guide.setYearsOfExperience(5);
        return guide;
    }

    private void loadAssignedTrips() {
        try {
            if (upcomingTripsTable == null) {
                System.err.println("upcomingTripsTable is null, skipping loading trips");
                return;
            }

            List<Booking> trips;
            if (guideProfile != null) {
                trips = databaseUtil.getAssignedTripsForGuide(guideProfile.getId());
                if (trips == null || trips.isEmpty()) {
                    System.out.println("No trips found, creating sample data");
                    trips = createSampleTrips();
                }
            } else {
                System.out.println("No guide profile available, using sample data");
                trips = createSampleTrips();
            }

            upcomingTripsTable.setItems(FXCollections.observableArrayList(trips));
            System.out.println("Loaded " + trips.size() + " trips");
        } catch (Exception e) {
            System.err.println("Error loading assigned trips: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Database Error"),
                    languageManager.translate("Could not load assigned trips") + ": " + e.getMessage());
        }
    }

    private List<Booking> createSampleTrips() {
        List<Booking> sampleTrips = new ArrayList<>();

        // Sample trip 1
        Booking trip1 = new Booking();
        trip1.setId("1");
        trip1.setTrekName("Everest Base Camp");
        trip1.setBookingDate("2025-08-15");
        trip1.setDuration(12);
        trip1.setTouristName("John Doe");
        trip1.setStatus("Confirmed");
        sampleTrips.add(trip1);

        // Sample trip 2
        Booking trip2 = new Booking();
        trip2.setId("2");
        trip2.setTrekName("Annapurna Circuit");
        trip2.setBookingDate("2025-09-05");
        trip2.setDuration(14);
        trip2.setTouristName("Jane Smith");
        trip2.setStatus("Pending");
        sampleTrips.add(trip2);

        // Sample trip 3
        Booking trip3 = new Booking();
        trip3.setId("3");
        trip3.setTrekName("Langtang Valley");
        trip3.setBookingDate("2025-08-25");
        trip3.setDuration(7);
        trip3.setTouristName("Mike Johnson");
        trip3.setStatus("Confirmed");
        sampleTrips.add(trip3);

        return sampleTrips;
    }

    private void updateWeatherInfo() {
        try {
            // Use consistent values for weather data
            String weather = "Partly Cloudy";

            if (weatherLabel != null) {
                weatherLabel.setText(languageManager.translate("Weather") + ": " +
                        languageManager.translate(weather));
            }

            if (currentWeatherLabel != null) {
                currentWeatherLabel.setText(languageManager.translate("Temperature") + ": 25°C");
            }

            if (humidityLabel != null) {
                humidityLabel.setText(languageManager.translate("Humidity") + ": 60%");
            }

            if (windSpeedLabel != null) {
                windSpeedLabel.setText(languageManager.translate("Wind Speed") + ": 10 km/h");
            }
        } catch (Exception e) {
            System.err.println("Error updating weather info: " + e.getMessage());
            e.printStackTrace();

            if (weatherLabel != null) {
                weatherLabel.setText(languageManager.translate("Weather data unavailable"));
            }
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        System.out.println("Refresh button clicked");
        loadGuideProfile();
        loadAssignedTrips();
        updateWeatherInfo();
        setupDateTime(); // Refresh date/time display
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Logout button clicked");
            sceneManager.switchToLoginPage(event);
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Navigation Error"),
                    languageManager.translate("Could not navigate to login page") + ": " + e.getMessage());
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