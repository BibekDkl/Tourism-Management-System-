package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.Booking;
import com.example.nepaltourismmanagement.models.Trek;
import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.DateTimeUtil;
import com.example.nepaltourismmanagement.utils.SceneManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class TouristDashboardController implements Initializable {

    // Header components
    @FXML private Label userLabel;
    @FXML private Label dateTimeLabel;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;

    // Book New Trip Tab
    @FXML private TableView<Trek> availableTripsTable;
    @FXML private TableColumn<Trek, String> tripNameColumn;
    @FXML private TableColumn<Trek, Integer> durationColumn;
    @FXML private TableColumn<Trek, String> difficultyColumn;
    @FXML private TableColumn<Trek, Double> priceColumn;
    @FXML private TableColumn<Trek, Button> actionColumn;

    // View Your Trips Tab
    @FXML private TableView<Booking> bookedTripsTable;
    @FXML private TableColumn<Booking, String> bookedTripNameColumn;
    @FXML private TableColumn<Booking, String> bookingDateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, Double> bookedPriceColumn;

    // Manage Trips Tab
    @FXML private TableView<Booking> manageTripsTable;
    @FXML private TableColumn<Booking, String> manageTripNameColumn;
    @FXML private TableColumn<Booking, String> manageDateColumn;
    @FXML private TableColumn<Booking, String> manageStatusColumn;
    @FXML private TableColumn<Booking, Double> managePriceColumn;

    // Dependencies
    private DatabaseUtil databaseUtil;
    private SceneManager sceneManager;
    private User currentUser;

    // Current date/time constant
    private static final String CURRENT_DATETIME = "2025-07-29 18:54:14";
    private static final String CURRENT_USERNAME = "BibekDkl";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing TouristDashboardController");
        databaseUtil = new DatabaseUtil();
        sceneManager = new SceneManager();

        initializeLanguageCombo();
        setupClock();
        initializeAvailableTripsTable();
        initializeBookedTripsTable();
        initializeManageTripsTable();

        System.out.println("TouristDashboardController initialized");
    }

    public void setCurrentUser(User user) {
        if (user == null) {
            System.err.println("ERROR: Attempted to set null user in TouristDashboardController");
            showAlert(Alert.AlertType.ERROR, "System Error",
                    "User information could not be loaded. Please log in again.");
            return;
        }

        System.out.println("Setting current user to: " + user.getUsername() + " (ID: " + user.getId() + ")");
        this.currentUser = user;
        userLabel.setText(user.getUsername());
        loadData();
    }

    private void initializeLanguageCombo() {
        languageCombo.setItems(FXCollections.observableArrayList("English", "Nepali", "Hindi"));
        languageCombo.setValue("English");
    }

    private void setupClock() {
        // Set fixed date/time for demo
        dateTimeLabel.setText(CURRENT_DATETIME);

        // Uncomment for real clock if needed
        /*
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            dateTimeLabel.setText(DateTimeUtil.formatDateTime(LocalDateTime.now()));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        */
    }

    private void initializeAvailableTripsTable() {
        tripNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));

        // Custom cell factory for action column with "Book" button
        actionColumn.setCellFactory(column -> {
            return new TableCell<Trek, Button>() {
                private final Button bookButton = new Button("Book");

                {
                    bookButton.setOnAction(event -> {
                        Trek trek = getTableView().getItems().get(getIndex());
                        handleBookTrek(trek);
                    });

                    bookButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white;");
                }

                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(bookButton);
                    }
                }
            };
        });
    }

    private void initializeBookedTripsTable() {
        bookedTripNameColumn.setCellValueFactory(new PropertyValueFactory<>("trekName"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        bookedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void initializeManageTripsTable() {
        manageTripNameColumn.setCellValueFactory(new PropertyValueFactory<>("trekName"));
        manageDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        manageStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        managePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadData() {
        loadAvailableTrips();
        loadBookedTrips();
    }

    private void loadAvailableTrips() {
        try {
            List<Trek> treks = databaseUtil.getAllTreks();
            availableTripsTable.setItems(FXCollections.observableArrayList(treks));
            System.out.println("Loaded " + treks.size() + " available treks");
        } catch (Exception e) {
            System.err.println("Error loading available treks: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load available treks: " + e.getMessage());
        }
    }

    private void loadBookedTrips() {
        try {
            if (currentUser == null) {
                System.err.println("ERROR: currentUser is null in loadBookedTrips");
                showAlert(Alert.AlertType.ERROR, "System Error",
                        "User information is missing. Please log out and log in again.");
                return;
            }

            System.out.println("Loading booked trips for user ID: " + currentUser.getId());
            List<Booking> bookings = databaseUtil.getBookingsForTourist(currentUser.getId());
            bookedTripsTable.setItems(FXCollections.observableArrayList(bookings));
            manageTripsTable.setItems(FXCollections.observableArrayList(bookings));
            System.out.println("Loaded " + bookings.size() + " bookings for user");
        } catch (Exception e) {
            System.err.println("Error loading booked trips: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load booked trips: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing data...");
        loadData();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Logging out user: " + (currentUser != null ? currentUser.getUsername() : "unknown"));
            sceneManager.switchToLoginPage(event);
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to login page: " + e.getMessage());
        }
    }

    private void handleBookTrek(Trek trek) {
        System.out.println("Booking trek: " + trek.getName());

        // Check if user is properly set
        if (currentUser == null) {
            System.err.println("ERROR: currentUser is null in handleBookTrek");
            showAlert(Alert.AlertType.ERROR, "System Error",
                    "User information is missing. Please log out and log in again.");
            return;
        }

        System.out.println("Trek difficulty: " + trek.getDifficulty());
        System.out.println("Current user: " + currentUser.getUsername() + " (ID: " + currentUser.getId() + ")");

        try {
            // Check if this trek might require a warning based on difficulty
            if ("Very Difficult".equals(trek.getDifficulty()) || "Extreme".equals(trek.getDifficulty())) {
                System.out.println("High difficulty trek detected. Showing warning...");
                showWarningDialog(trek);
            } else {
                System.out.println("Standard difficulty trek. Proceeding with booking...");
                proceedWithBooking(trek);
            }
        } catch (Exception e) {
            System.err.println("Error in handleBookTrek: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Booking Error",
                    "An error occurred while processing your booking: " + e.getMessage());
        }
    }

    private void showWarningDialog(Trek trek) {
        try {
            // Make sure to use the correct path based on your application structure
            // Use absolute path to ensure loading works
            String[] possiblePaths = {"/Warning.fxml", "/fxml/Warning.fxml", "Warning.fxml"};
            URL warningFxmlUrl = null;

            for (String path : possiblePaths) {
                warningFxmlUrl = getClass().getResource(path);
                if (warningFxmlUrl != null) {
                    System.out.println("Found Warning.fxml at: " + path);
                    break;
                }
            }

            if (warningFxmlUrl == null) {
                System.err.println("Warning FXML file not found at any attempted location");
                showAlert(Alert.AlertType.ERROR, "Resource Error",
                        "Could not find warning dialog resource. Proceeding with direct booking.");
                proceedWithBooking(trek);
                return;
            }

            System.out.println("Loading warning FXML from: " + warningFxmlUrl);
            FXMLLoader loader = new FXMLLoader(warningFxmlUrl);
            Parent root = loader.load();

            WarningController controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Could not get WarningController");
            }

            controller.setTrekAndUser(trek, currentUser);

            Stage warningStage = new Stage();
            warningStage.initModality(Modality.APPLICATION_MODAL);
            warningStage.setTitle("Trek Warning");
            warningStage.setScene(new Scene(root));
            warningStage.showAndWait();

            // Refresh bookings after warning dialog closes (booking may have been made)
            System.out.println("Warning dialog closed, refreshing bookings");
            loadBookedTrips();
        } catch (IOException e) {
            System.err.println("Error loading warning dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Dialog Error",
                    "Could not show warning dialog: " + e.getMessage() + "\nProceeding with direct booking.");
            proceedWithBooking(trek);
        }
    }

    private void proceedWithBooking(Trek trek) {
        try {
            // Double-check user is set
            if (currentUser == null) {
                throw new IllegalStateException("User information is missing");
            }

            // Create a new booking
            Booking booking = new Booking();
            booking.setTrekId(trek.getId());
            booking.setTouristId(currentUser.getId());
            booking.setStatus("Pending");
            booking.setPrice(trek.getBasePrice());
            booking.setDuration(trek.getDuration());
            booking.setBookingDate("2025-07-29"); // Fixed date
            booking.setHighRiskAcknowledged(false);

            System.out.println("Creating booking: " +
                    "Trek ID: " + trek.getId() + ", " +
                    "Tourist ID: " + currentUser.getId() + ", " +
                    "Date: " + booking.getBookingDate());

            // Save to database
            boolean success = databaseUtil.createBooking(booking);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Booking Successful",
                        "Your booking for " + trek.getName() + " has been submitted and is awaiting confirmation.");
                System.out.println("Standard booking created successfully");
                loadBookedTrips();
            } else {
                showAlert(Alert.AlertType.ERROR, "Booking Error",
                        "Failed to create booking. Please try again.");
                System.out.println("Failed to create standard booking");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Booking Error",
                    "Could not complete booking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateBooking() {
        Booking selectedBooking = manageTripsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a booking to update.");
            return;
        }

        // Can only update bookings in "Pending" status
        if (!"Pending".equals(selectedBooking.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Update Restricted",
                    "Only pending bookings can be updated.");
            return;
        }

        try {
            // Get the trek for this booking
            Trek trek = databaseUtil.getTrekById(selectedBooking.getTrekId());
            if (trek == null) {
                showAlert(Alert.AlertType.ERROR, "Trek Not Found",
                        "Could not find trek information for this booking.");
                return;
            }

            // For now, just show dialog with booking and trek info
            showAlert(Alert.AlertType.INFORMATION, "Update Booking",
                    "Booking Information:\n" +
                            "Trek: " + trek.getName() + "\n" +
                            "Date: " + selectedBooking.getBookingDate() + "\n" +
                            "Status: " + selectedBooking.getStatus() + "\n" +
                            "Price: $" + selectedBooking.getPrice() + "\n\n" +
                            "Booking update feature will be available soon.");
        } catch (Exception e) {
            System.err.println("Error getting trek information: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Error",
                    "Could not load trek information: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = manageTripsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a booking to cancel.");
            return;
        }

        // Cannot cancel completed bookings
        if ("Completed".equals(selectedBooking.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Cancellation Restricted",
                    "Completed bookings cannot be cancelled.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Cancel Booking");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("Do you want to cancel this booking? This action cannot be undone.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    System.out.println("Cancelling booking with ID: " + selectedBooking.getId());
                    boolean success = databaseUtil.updateBookingStatus(selectedBooking.getId(), "Cancelled");

                    if (success) {
                        System.out.println("Booking cancelled successfully");
                        showAlert(Alert.AlertType.INFORMATION, "Booking Cancelled",
                                "Your booking has been cancelled successfully.");
                    } else {
                        System.err.println("Failed to cancel booking");
                        showAlert(Alert.AlertType.ERROR, "Cancellation Error",
                                "Could not cancel booking. Please try again.");
                    }

                    loadBookedTrips();
                } catch (Exception e) {
                    System.err.println("Error cancelling booking: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Cancellation Error",
                            "Could not cancel booking: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}