package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.*;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.LanguageManager;
import com.example.nepaltourismmanagement.utils.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class AdminDashboardController implements Initializable {

    // Current date/time constant
    private static final String CURRENT_DATETIME = "2025-07-30 08:19:12";
    private static final String CURRENT_USER = "BibekDkl";

    // Header
    @FXML private Label welcomeLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label userLabel;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private VBox rootContainer;

    // Dashboard tabs
    @FXML private TabPane mainTabPane;

    // Dashboard overview
    @FXML private Label totalUsersLabel;
    @FXML private Label totalGuidesLabel;
    @FXML private Label totalTreksLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label revenueLabel;
    @FXML private Label popularTrekLabel;
    @FXML private Label activeGuidesLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label pendingBookingsLabel;
    @FXML private Label systemStatusLabel;
    @FXML private PieChart bookingStatusChart;
    @FXML private BarChart<String, Number> treksChart;
    @FXML private PieChart difficultyChart;
    @FXML private BarChart<String, Number> bookingsChart;
    @FXML private PieChart revenueChart;

    // Bookings tab
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> bookingTrekColumn;
    @FXML private TableColumn<Booking, String> touristColumn;
    @FXML private TableColumn<Booking, String> guideColumn;
    @FXML private TableColumn<Booking, String> bookingDateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, Double> bookingPriceColumn;
    @FXML private TableColumn<Booking, String> bookingActionColumn;
    @FXML private ComboBox<String> bookingStatusFilter;
    @FXML private TableColumn<Booking, String> touristNameColumn;

    // Guides tab
    @FXML private TableView<Guide> guidesTable;
    @FXML private TableColumn<Guide, String> guideIdColumn;
    @FXML private TableColumn<Guide, String> guideNameColumn;
    @FXML private TableColumn<Guide, String> guideEmailColumn;
    @FXML private TableColumn<Guide, String> guidePhoneColumn;
    @FXML private TableColumn<Guide, String> languagesColumn;
    @FXML private TableColumn<Guide, Integer> experienceColumn;
    @FXML private TableColumn<Guide, String> certificationsColumn;
    @FXML private TableColumn<Guide, String> guideActionColumn;
    @FXML private TextField guideNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField languagesField;
    @FXML private TextField experienceField;
    @FXML private TextField certificationsField;

    // Treks tab
    @FXML private TableView<Trek> treksTable;
    @FXML private TableColumn<Trek, String> trekIdColumn;
    @FXML private TableColumn<Trek, String> trekNameColumn;
    @FXML private TableColumn<Trek, String> regionColumn;
    @FXML private TableColumn<Trek, Integer> maxAltitudeColumn;
    @FXML private TableColumn<Trek, Integer> durationColumn;
    @FXML private TableColumn<Trek, String> difficultyColumn;
    @FXML private TableColumn<Trek, Double> priceColumn;
    @FXML private TableColumn<Trek, Double> basePriceColumn;
    @FXML private TableColumn<Trek, String> trekActionColumn;
    @FXML private TextField trekNameField;
    @FXML private TextField regionField;
    @FXML private TextField maxAltitudeField;
    @FXML private TextField durationField;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private TextField basePriceField;

    // Dependencies
    private User currentUser;
    private DatabaseUtil databaseUtil;
    private SceneManager sceneManager;
    private LanguageManager languageManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing AdminDashboardController");

        // Initialize utilities
        databaseUtil = new DatabaseUtil();
        sceneManager = new SceneManager();
        languageManager = LanguageManager.getInstance();

        // Set up date/time
        if (dateTimeLabel != null) {
            dateTimeLabel.setText(CURRENT_DATETIME);
        }

        // Set up language combo
        initializeLanguageCombo();

        // Initialize tabs
        initializeDashboardTab();
        initializeBookingsTab();
        initializeGuidesTab();
        initializeTreksTab();

        // Initialize system stats
        updateSystemStats();

        System.out.println("AdminDashboardController initialized");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;

        // Set welcome message
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + user.getFullName());
        }

        // Set user label
        if (userLabel != null) {
            userLabel.setText(CURRENT_USER);
        }

        // Load data
        loadDashboardData();
        loadBookingsData();
        loadGuidesData();
        loadTreksData();
    }

    private void initializeLanguageCombo() {
        if (languageCombo != null) {
            languageCombo.setItems(FXCollections.observableArrayList("English", "Nepali"));
            languageCombo.setValue("English");

            languageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.equals(oldVal)) {
                    handleLanguageChange(newVal);
                }
            });
        }
    }

    private void handleLanguageChange(String language) {
        System.out.println("Changing language to: " + language);

        // Apply translations to UI
        if (rootContainer != null) {
            languageManager.setLanguageAndTranslate(language, rootContainer);
        }

        // Refresh data with new translations
        refreshAllData();
    }

    private void refreshAllData() {
        loadDashboardData();
        loadBookingsData();
        loadGuidesData();
        loadTreksData();
        updateSystemStats();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Logging out admin user");
            sceneManager.switchToLoginPage(event);
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Logout Error",
                    "Could not log out: " + e.getMessage());
        }
    }

    /**
     * Refreshes all data in the dashboard
     */
    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing all data...");
        loadDashboardData();
        loadBookingsData();
        loadGuidesData();
        loadTreksData();

        // Update status labels
        updateSystemStats();
    }

    // ------ Dashboard Tab ------

    private void initializeDashboardTab() {
        System.out.println("Initializing dashboard tab");

        // Initialize charts
        if (bookingStatusChart != null) {
            bookingStatusChart.setTitle("Booking Status");
        }

        if (treksChart != null) {
            treksChart.setTitle("Treks by Region");
        }

        if (difficultyChart != null) {
            difficultyChart.setTitle("Trek Difficulty Levels");
        }

        if (bookingsChart != null) {
            bookingsChart.setTitle("Monthly Bookings");
        }

        if (revenueChart != null) {
            revenueChart.setTitle("Revenue by Trek Type");
        }
    }

    private void loadDashboardData() {
        try {
            System.out.println("Loading dashboard data");

            // Get counts
            List<User> allUsers = databaseUtil.getAllUsers();
            List<Guide> allGuides = databaseUtil.getAllGuides();
            List<Trek> allTreks = databaseUtil.getAllTreks();
            List<Booking> allBookings = databaseUtil.getAllBookings();

            // Set count labels
            if (totalUsersLabel != null) totalUsersLabel.setText(String.valueOf(allUsers.size()));
            if (totalGuidesLabel != null) totalGuidesLabel.setText(String.valueOf(allGuides.size()));
            if (totalTreksLabel != null) totalTreksLabel.setText(String.valueOf(allTreks.size()));
            if (totalBookingsLabel != null) totalBookingsLabel.setText(String.valueOf(allBookings.size()));

            // Get booking statistics
            Map<String, Object> bookingStats = databaseUtil.getBookingStatistics();

            // Set revenue and popular trek
            if (revenueLabel != null) {
                double revenue = (double) bookingStats.getOrDefault("totalRevenue", 0.0);
                revenueLabel.setText(String.format("$%.2f", revenue));
            }

            if (popularTrekLabel != null) {
                String popularTrek = (String) bookingStats.getOrDefault("mostPopularTrek", "None");
                popularTrekLabel.setText(popularTrek);
            }

            // Update charts
            updateBookingStatusChart(bookingStats);
            updateTreksChart(allTreks);
            updateDifficultyChart(allTreks);
            updateBookingsChart(allBookings);
            updateRevenueChart(allBookings, allTreks);

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void updateBookingStatusChart(Map<String, Object> bookingStats) {
        if (bookingStatusChart != null) {
            bookingStatusChart.getData().clear();

            // Get status counts
            Map<String, Integer> statusCounts =
                    (Map<String, Integer>) bookingStats.getOrDefault("statusCounts", new HashMap<>());

            // Create pie chart data
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            bookingStatusChart.setData(pieChartData);
        }
    }

    private void updateTreksChart(List<Trek> treks) {
        if (treksChart != null) {
            treksChart.getData().clear();

            // Count treks by region
            Map<String, Long> regionCounts = treks.stream()
                    .collect(Collectors.groupingBy(Trek::getRegion, Collectors.counting()));

            // Create series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Trek Count");

            for (Map.Entry<String, Long> entry : regionCounts.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            treksChart.getData().add(series);
        }
    }

    private void updateDifficultyChart(List<Trek> treks) {
        if (difficultyChart != null) {
            difficultyChart.getData().clear();

            // Count treks by difficulty
            Map<String, Long> difficultyCounts = treks.stream()
                    .collect(Collectors.groupingBy(Trek::getDifficulty, Collectors.counting()));

            // Create pie chart data
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            for (Map.Entry<String, Long> entry : difficultyCounts.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            difficultyChart.setData(pieChartData);
        }
    }

    private void updateBookingsChart(List<Booking> bookings) {
        if (bookingsChart != null) {
            bookingsChart.getData().clear();

            // For demo purposes, simulate monthly data
            // In real app, you'd extract month from booking date
            Map<String, Integer> monthlyBookings = new LinkedHashMap<>();
            monthlyBookings.put("Jan", 5);
            monthlyBookings.put("Feb", 8);
            monthlyBookings.put("Mar", 12);
            monthlyBookings.put("Apr", 15);
            monthlyBookings.put("May", 20);
            monthlyBookings.put("Jun", 25);
            monthlyBookings.put("Jul", bookings.size()); // Current month shows actual count

            // Create series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Number of Bookings");

            for (Map.Entry<String, Integer> entry : monthlyBookings.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            bookingsChart.getData().add(series);
        }
    }

    private void updateRevenueChart(List<Booking> bookings, List<Trek> treks) {
        if (revenueChart != null) {
            revenueChart.getData().clear();

            // Group treks by difficulty
            Map<String, List<Trek>> treksByDifficulty = treks.stream()
                    .collect(Collectors.groupingBy(Trek::getDifficulty));

            // Calculate revenue by difficulty
            Map<String, Double> revenueByDifficulty = new HashMap<>();

            // Initialize with all difficulties
            for (String difficulty : treksByDifficulty.keySet()) {
                revenueByDifficulty.put(difficulty, 0.0);
            }

            // Sum revenue for each difficulty level
            for (Booking booking : bookings) {
                if ("Confirmed".equals(booking.getStatus()) || "Completed".equals(booking.getStatus())) {
                    Trek trek = databaseUtil.getTrekById(booking.getTrekId());
                    if (trek != null) {
                        String difficulty = trek.getDifficulty();
                        revenueByDifficulty.put(difficulty,
                                revenueByDifficulty.getOrDefault(difficulty, 0.0) + booking.getPrice());
                    }
                }
            }

            // Create pie chart data
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            for (Map.Entry<String, Double> entry : revenueByDifficulty.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            revenueChart.setData(pieChartData);
        }
    }

    // ------ Bookings Tab ------

    private void initializeBookingsTab() {
        System.out.println("Initializing bookings tab");

        // Set up table columns
        if (bookingIdColumn != null) bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (touristNameColumn != null) touristNameColumn.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        if (bookingDateColumn != null) bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        if (statusColumn != null) statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (bookingPriceColumn != null) bookingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Set up booking status filter
        if (bookingStatusFilter != null) {
            bookingStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    filterBookings(newVal);
                }
            });
        }
    }

    private void loadBookingsData() {
        try {
            List<Booking> bookings = databaseUtil.getAllBookings();

            if (bookingsTable != null) {
                bookingsTable.setItems(FXCollections.observableArrayList(bookings));
            }

        } catch (Exception e) {
            System.err.println("Error loading bookings data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterBookings(String status) {
        try {
            List<Booking> allBookings = databaseUtil.getAllBookings();
            List<Booking> filteredBookings;

            if ("All".equals(status)) {
                filteredBookings = allBookings;
            } else {
                filteredBookings = allBookings.stream()
                        .filter(b -> status.equals(b.getStatus()))
                        .collect(Collectors.toList());
            }

            bookingsTable.setItems(FXCollections.observableArrayList(filteredBookings));
        } catch (Exception e) {
            System.err.println("Error filtering bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates booking status
     */
    @FXML
    private void handleUpdateStatus() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to update status.");
            return;
        }

        showUpdateStatusDialog(selectedBooking);
    }

    /**
     * Assigns a guide to a booking
     */
    @FXML
    private void handleAssignGuide() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to assign a guide.");
            return;
        }

        // Check if already has a guide
        if (selectedBooking.getGuideId() != null && !selectedBooking.getGuideId().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Guide Already Assigned",
                    "This booking already has an assigned guide. To change the guide, please cancel and create a new booking.");
            return;
        }

        showAssignGuideDialog(selectedBooking);
    }

    /**
     * Cancels a booking
     */
    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to cancel.");
            return;
        }

        // Cannot cancel completed bookings
        if ("Completed".equals(selectedBooking.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Cannot Cancel", "Completed bookings cannot be cancelled.");
            return;
        }

        // Confirm cancellation
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Booking");
        confirmAlert.setContentText("Are you sure you want to cancel this booking? This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = databaseUtil.updateBookingStatus(selectedBooking.getId(), "Cancelled");
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Booking Cancelled",
                        "The booking has been cancelled successfully.");
                loadBookingsData();
                loadDashboardData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel the booking.");
            }
        }
    }

    private void showUpdateStatusDialog(Booking booking) {
        try {
            // Create dialog
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Update Status");
            dialog.setHeaderText("Update status for booking ID: " + booking.getId());

            // Set button types
            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Create status combo
            ComboBox<String> statusCombo = new ComboBox<>();
            statusCombo.getItems().addAll("Pending", "Confirmed", "Completed", "Cancelled");
            statusCombo.setValue(booking.getStatus());

            grid.add(new Label("Tourist:"), 0, 0);
            grid.add(new Label(booking.getTouristName()), 1, 0);
            grid.add(new Label("Current Status:"), 0, 1);
            grid.add(new Label(booking.getStatus()), 1, 1);
            grid.add(new Label("New Status:"), 0, 2);
            grid.add(statusCombo, 1, 2);

            dialog.getDialogPane().setContent(grid);

            // Request focus on the status combo by default
            Platform.runLater(statusCombo::requestFocus);

            // Convert the result to String when the update button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    return statusCombo.getValue();
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newStatus -> {
                // Update booking status
                boolean success = databaseUtil.updateBookingStatus(booking.getId(), newStatus);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Status Updated",
                            "Booking status has been updated to " + newStatus + ".");
                    loadBookingsData();
                    loadDashboardData(); // Refresh dashboard stats
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "Could not update booking status.");
                }
            });

        } catch (Exception e) {
            System.err.println("Error showing update status dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not show update status dialog: " + e.getMessage());
        }
    }

    private void showAssignGuideDialog(Booking booking) {
        try {
            // Get available guides
            List<Guide> availableGuides = databaseUtil.getAllGuides().stream()
                    .filter(Guide::isAvailable)
                    .collect(Collectors.toList());

            if (availableGuides.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Guides Available",
                        "There are no available guides at the moment.");
                return;
            }

            // Create dialog
            Dialog<Guide> dialog = new Dialog<>();
            dialog.setTitle("Assign Guide");
            dialog.setHeaderText("Assign a guide to this booking");

            // Set button types
            ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

            // Create form
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));

            Label infoLabel = new Label("Select a guide to assign to this booking:");

            // Create guide selection table
            TableView<Guide> guidesTable = new TableView<>();
            guidesTable.setPrefHeight(300);

            TableColumn<Guide, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            nameCol.setPrefWidth(150);

            TableColumn<Guide, Integer> experienceCol = new TableColumn<>("Experience");
            experienceCol.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));
            experienceCol.setPrefWidth(100);

            TableColumn<Guide, String> languagesCol = new TableColumn<>("Languages");
            languagesCol.setCellValueFactory(new PropertyValueFactory<>("languages"));
            languagesCol.setPrefWidth(150);

            TableColumn<Guide, String> certsCol = new TableColumn<>("Certifications");
            certsCol.setCellValueFactory(new PropertyValueFactory<>("certifications"));
            certsCol.setPrefWidth(200);

            guidesTable.getColumns().addAll(nameCol, experienceCol, languagesCol, certsCol);
            guidesTable.setItems(FXCollections.observableArrayList(availableGuides));

            content.getChildren().addAll(infoLabel, guidesTable);
            dialog.getDialogPane().setContent(content);

            // Request default button focus
            Platform.runLater(() -> guidesTable.requestFocus());

            // Convert the result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == assignButtonType) {
                    Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
                    if (selectedGuide == null) {
                        showAlert(Alert.AlertType.ERROR, "No Selection",
                                "Please select a guide to assign.");
                        return null;
                    }
                    return selectedGuide;
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Guide> result = dialog.showAndWait();
            result.ifPresent(guide -> {
                // Assign guide to booking
                boolean success = databaseUtil.assignGuideToBooking(booking.getId(), guide.getId());

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Guide Assigned",
                            "Guide " + guide.getName() + " has been assigned to this booking.");
                    loadBookingsData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "Could not assign guide to booking.");
                }
            });

        } catch (Exception e) {
            System.err.println("Error showing assign guide dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not show assign guide dialog: " + e.getMessage());
        }
    }

    // ------ Guides Tab ------

    private void initializeGuidesTab() {
        System.out.println("Initializing guides tab");

        // Set up table columns
        if (guideIdColumn != null) guideIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (guideNameColumn != null) guideNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (languagesColumn != null) languagesColumn.setCellValueFactory(new PropertyValueFactory<>("languages"));
        if (experienceColumn != null) experienceColumn.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));
        if (certificationsColumn != null) certificationsColumn.setCellValueFactory(new PropertyValueFactory<>("certifications"));

        // Additional columns if needed
        if (guideEmailColumn != null) guideEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        if (guidePhoneColumn != null) guidePhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    private void loadGuidesData() {
        try {
            List<Guide> guides = databaseUtil.getAllGuides();

            if (guidesTable != null) {
                guidesTable.setItems(FXCollections.observableArrayList(guides));
            }

        } catch (Exception e) {
            System.err.println("Error loading guides data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adds a new guide
     */
    @FXML
    private void handleAddGuide() {
        try {
            // Validate input fields
            String name = guideNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String languages = languagesField.getText().trim();
            String experienceText = experienceField.getText().trim();
            String certifications = certificationsField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                    languages.isEmpty() || experienceText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Please fill in all required fields: Name, Email, Phone, Languages, and Experience.");
                return;
            }

            // Parse experience years
            int experience;
            try {
                experience = Integer.parseInt(experienceText);
                if (experience < 0) {
                    throw new NumberFormatException("Experience years must be a positive number");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Experience years must be a valid number.");
                return;
            }

            // Create a new guide object
            Guide guide = new Guide();
            guide.setName(name);
            guide.setEmail(email);
            guide.setPhoneNumber(phone);
            guide.setLanguages(languages);
            guide.setYearsOfExperience(experience);
            guide.setCertifications(certifications);
            guide.setSpecializations("General Trekking"); // Default
            guide.setAvailable(true); // Default to available

            // Save the guide
            boolean success = databaseUtil.addGuide(guide);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Guide Added",
                        "New guide has been added successfully.");

                // Clear input fields
                guideNameField.clear();
                emailField.clear();
                phoneField.clear();
                languagesField.clear();
                experienceField.clear();
                certificationsField.clear();

                // Reload guides data
                loadGuidesData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to add the guide. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Error adding guide: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while adding the guide: " + e.getMessage());
        }
    }

    /**
     * Updates an existing guide's information
     */
    @FXML
    private void handleUpdateGuide() {
        try {
            // Get selected guide
            Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
            if (selectedGuide == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection",
                        "Please select a guide to update.");
                return;
            }

            // Check if form fields are populated
            if (guideNameField.getText().isEmpty()) {
                // If fields are empty, populate them from the selected guide
                guideNameField.setText(selectedGuide.getName());
                emailField.setText(selectedGuide.getEmail());
                phoneField.setText(selectedGuide.getPhoneNumber());
                languagesField.setText(selectedGuide.getLanguages());
                experienceField.setText(String.valueOf(selectedGuide.getYearsOfExperience()));
                certificationsField.setText(selectedGuide.getCertifications());

                showAlert(Alert.AlertType.INFORMATION, "Update Guide",
                        "The form has been filled with the selected guide's information. " +
                                "Make your changes and click 'Update Guide' again to save.");
                return;
            }

            // Validate input fields
            String name = guideNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String languages = languagesField.getText().trim();
            String experienceText = experienceField.getText().trim();
            String certifications = certificationsField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                    languages.isEmpty() || experienceText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Please fill in all required fields: Name, Email, Phone, Languages, and Experience.");
                return;
            }

            // Parse experience years
            int experience;
            try {
                experience = Integer.parseInt(experienceText);
                if (experience < 0) {
                    throw new NumberFormatException("Experience years must be a positive number");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Experience years must be a valid number.");
                return;
            }

            // Update guide object
            selectedGuide.setName(name);
            selectedGuide.setEmail(email);
            selectedGuide.setPhoneNumber(phone);
            selectedGuide.setLanguages(languages);
            selectedGuide.setYearsOfExperience(experience);
            selectedGuide.setCertifications(certifications);

            // Save the updated guide
            boolean success = databaseUtil.updateGuide(selectedGuide);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Guide Updated",
                        "Guide information has been updated successfully.");

                // Clear input fields
                guideNameField.clear();
                emailField.clear();
                phoneField.clear();
                languagesField.clear();
                experienceField.clear();
                certificationsField.clear();

                // Reload guides data
                loadGuidesData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to update the guide. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Error updating guide: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while updating the guide: " + e.getMessage());
        }
    }

    /**
     * Deletes a selected guide
     */
    @FXML
    private void handleDeleteGuide() {
        try {
            // Get selected guide
            Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
            if (selectedGuide == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection",
                        "Please select a guide to delete.");
                return;
            }

            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Guide");
            confirmAlert.setContentText("Are you sure you want to delete guide '" +
                    selectedGuide.getName() + "'? This action cannot be undone.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = databaseUtil.deleteGuide(selectedGuide.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Guide Deleted",
                            "The guide has been deleted successfully.");
                    loadGuidesData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "Failed to delete the guide. The guide may be assigned to active bookings.");
                }
            }

        } catch (Exception e) {
            System.err.println("Error deleting guide: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while deleting the guide: " + e.getMessage());
        }
    }

    // ------ Treks Tab ------

    private void initializeTreksTab() {
        System.out.println("Initializing treks tab");

        // Set up table columns
        if (trekNameColumn != null) trekNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (regionColumn != null) regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        if (maxAltitudeColumn != null) maxAltitudeColumn.setCellValueFactory(new PropertyValueFactory<>("maxAltitude"));
        if (durationColumn != null) durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        if (difficultyColumn != null) difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        if (basePriceColumn != null) basePriceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));

        // Additional columns if available
        if (trekIdColumn != null) trekIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (priceColumn != null) priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadTreksData() {
        try {
            List<Trek> treks = databaseUtil.getAllTreks();

            if (treksTable != null) {
                treksTable.setItems(FXCollections.observableArrayList(treks));
            }

        } catch (Exception e) {
            System.err.println("Error loading treks data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adds a new trek
     */
    @FXML
    private void handleAddTrek() {
        try {
            // Validate input fields
            String name = trekNameField.getText().trim();
            String region = regionField.getText().trim();
            String maxAltitudeText = maxAltitudeField.getText().trim();
            String durationText = durationField.getText().trim();
            String difficulty = difficultyCombo.getValue();
            String basePriceText = basePriceField.getText().trim();

            if (name.isEmpty() || region.isEmpty() || maxAltitudeText.isEmpty() ||
                    durationText.isEmpty() || difficulty == null || basePriceText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Please fill in all required fields.");
                return;
            }

            // Parse numeric fields
            int maxAltitude, duration;
            double basePrice;
            try {
                maxAltitude = Integer.parseInt(maxAltitudeText);
                duration = Integer.parseInt(durationText);
                basePrice = Double.parseDouble(basePriceText);

                if (maxAltitude <= 0 || duration <= 0 || basePrice <= 0) {
                    throw new NumberFormatException("Values must be positive");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Max altitude, duration, and price must be valid positive numbers.");
                return;
            }

            // Create a new trek object
            Trek trek = new Trek();
            trek.setName(name);
            trek.setRegion(region);
            trek.setMaxAltitude(maxAltitude);
            trek.setDuration(duration);
            trek.setDifficulty(difficulty);
            trek.setBasePrice(basePrice);
            trek.setPrice(basePrice * 1.2); // Default markup of 20%
            trek.setDescription("A beautiful trek in the " + region + " region.");
            trek.setSeasonal(true);
            trek.setBestSeason("Spring,Autumn");

            // Save the trek
            boolean success = databaseUtil.addTrek(trek);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Trek Added",
                        "New trek has been added successfully.");

                // Clear input fields
                trekNameField.clear();
                regionField.clear();
                maxAltitudeField.clear();
                durationField.clear();
                difficultyCombo.setValue(null);
                basePriceField.clear();

                // Reload treks data
                loadTreksData();
                loadDashboardData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to add the trek. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Error adding trek: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while adding the trek: " + e.getMessage());
        }
    }

    /**
     * Updates an existing trek
     */
    @FXML
    private void handleUpdateTrek() {
        try {
            // Get selected trek
            Trek selectedTrek = treksTable.getSelectionModel().getSelectedItem();
            if (selectedTrek == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection",
                        "Please select a trek to update.");
                return;
            }

            // Check if form fields are populated
            if (trekNameField.getText().isEmpty()) {
                // If fields are empty, populate them from the selected trek
                trekNameField.setText(selectedTrek.getName());
                regionField.setText(selectedTrek.getRegion());
                maxAltitudeField.setText(String.valueOf(selectedTrek.getMaxAltitude()));
                durationField.setText(String.valueOf(selectedTrek.getDuration()));
                difficultyCombo.setValue(selectedTrek.getDifficulty());
                basePriceField.setText(String.valueOf(selectedTrek.getBasePrice()));

                showAlert(Alert.AlertType.INFORMATION, "Update Trek",
                        "The form has been filled with the selected trek's information. " +
                                "Make your changes and click 'Update Trek' again to save.");
                return;
            }

            // Validate input fields
            String name = trekNameField.getText().trim();
            String region = regionField.getText().trim();
            String maxAltitudeText = maxAltitudeField.getText().trim();
            String durationText = durationField.getText().trim();
            String difficulty = difficultyCombo.getValue();
            String basePriceText = basePriceField.getText().trim();

            if (name.isEmpty() || region.isEmpty() || maxAltitudeText.isEmpty() ||
                    durationText.isEmpty() || difficulty == null || basePriceText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Please fill in all required fields.");
                return;
            }

            // Parse numeric fields
            int maxAltitude, duration;
            double basePrice;
            try {
                maxAltitude = Integer.parseInt(maxAltitudeText);
                duration = Integer.parseInt(durationText);
                basePrice = Double.parseDouble(basePriceText);

                if (maxAltitude <= 0 || duration <= 0 || basePrice <= 0) {
                    throw new NumberFormatException("Values must be positive");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Max altitude, duration, and price must be valid positive numbers.");
                return;
            }

            // Update trek object
            selectedTrek.setName(name);
            selectedTrek.setRegion(region);
            selectedTrek.setMaxAltitude(maxAltitude);
            selectedTrek.setDuration(duration);
            selectedTrek.setDifficulty(difficulty);
            selectedTrek.setBasePrice(basePrice);
            selectedTrek.setPrice(basePrice * 1.2); // Maintain markup

            // Save the updated trek
            boolean success = databaseUtil.updateTrek(selectedTrek);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Trek Updated",
                        "Trek information has been updated successfully.");

                // Clear input fields
                trekNameField.clear();
                regionField.clear();
                maxAltitudeField.clear();
                durationField.clear();
                difficultyCombo.setValue(null);
                basePriceField.clear();

                // Reload treks data
                loadTreksData();
                loadDashboardData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to update the trek. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Error updating trek: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while updating the trek: " + e.getMessage());
        }
    }

    /**
     * Deletes a selected trek
     */
    @FXML
    private void handleDeleteTrek() {
        try {
            // Get selected trek
            Trek selectedTrek = treksTable.getSelectionModel().getSelectedItem();
            if (selectedTrek == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection",
                        "Please select a trek to delete.");
                return;
            }

            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Trek");
            confirmAlert.setContentText("Are you sure you want to delete trek '" +
                    selectedTrek.getName() + "'? This will also affect any bookings for this trek.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = databaseUtil.deleteTrek(selectedTrek.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Trek Deleted",
                            "The trek has been deleted successfully.");
                    loadTreksData();
                    loadDashboardData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "Failed to delete the trek. It may be referenced by active bookings.");
                }
            }

        } catch (Exception e) {
            System.err.println("Error deleting trek: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while deleting the trek: " + e.getMessage());
        }
    }

    // Helper Methods

    /**
     * Update system statistics in footer
     */
    private void updateSystemStats() {
        try {
            // Count active users (simplified - just count all users)
            int activeUsers = databaseUtil.getAllUsers().size();
            activeUsersLabel.setText("Active Users: " + activeUsers);

            // Count pending bookings
            int pendingBookings = databaseUtil.getBookingsByStatus("Pending").size();
            pendingBookingsLabel.setText("Pending Bookings: " + pendingBookings);

            // System status is always online for now
            systemStatusLabel.setText("System Status: Online");

            // Update dashboard summary labels
            int totalBookings = databaseUtil.getAllBookings().size();
            totalBookingsLabel.setText(String.valueOf(totalBookings));

            // Calculate total revenue
            double totalRevenue = databaseUtil.getAllBookings().stream()
                    .filter(b -> "Confirmed".equals(b.getStatus()) || "Completed".equals(b.getStatus()))
                    .mapToDouble(Booking::getPrice)
                    .sum();
            totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));

            // Count active guides
            int activeGuides = (int) databaseUtil.getAllGuides().stream()
                    .filter(Guide::isAvailable)
                    .count();
            activeGuidesLabel.setText(String.valueOf(activeGuides));

        } catch (Exception e) {
            System.err.println("Error updating system stats: " + e.getMessage());
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

    // Utility class for UI updates that need to happen on the JavaFX application thread
    private static class Platform {
        public static void runLater(Runnable runnable) {
            javafx.application.Platform.runLater(runnable);
        }
    }
}