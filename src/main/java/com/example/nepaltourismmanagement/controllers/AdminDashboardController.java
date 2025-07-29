package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.*;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.DateTimeUtil;
import com.example.nepaltourismmanagement.utils.SceneManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    // Header components
    @FXML private Label userLabel;
    @FXML private Label dateTimeLabel;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;

    // Booking Management Tab
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> touristNameColumn;
    @FXML private TableColumn<Booking, String> trekNameColumn;
    @FXML private TableColumn<Booking, String> bookingDateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, Double> priceColumn;
    @FXML private ComboBox<String> bookingStatusFilter;

    // Trek Guide Management Tab
    @FXML private TableView<Guide> guidesTable;
    @FXML private TableColumn<Guide, String> guideIdColumn;
    @FXML private TableColumn<Guide, String> guideNameColumn;
    @FXML private TableColumn<Guide, String> languagesColumn;
    @FXML private TableColumn<Guide, Integer> experienceColumn;
    @FXML private TableColumn<Guide, String> certificationsColumn;
    @FXML private TextField guideNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField languagesField;
    @FXML private TextField experienceField;
    @FXML private TextField certificationsField;

    // Trek Locations Tab
    @FXML private TableView<Trek> treksTable;
    @FXML private TableColumn<Trek, String> regionColumn;
    @FXML private TableColumn<Trek, Integer> maxAltitudeColumn;
    @FXML private TableColumn<Trek, Integer> durationColumn;
    @FXML private TableColumn<Trek, String> difficultyColumn;
    @FXML private TableColumn<Trek, Double> basePriceColumn;
    @FXML private TextField trekNameField;
    @FXML private TextField regionField;
    @FXML private TextField maxAltitudeField;
    @FXML private TextField durationField;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private TextField basePriceField;

    // Analytics Dashboard Tab
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label activeGuidesLabel;
    @FXML private BarChart<String, Number> bookingsChart;
    @FXML private PieChart revenueChart;

    // Footer components
    @FXML private Label activeUsersLabel;
    @FXML private Label pendingBookingsLabel;
    @FXML private Label systemStatusLabel;

    // Dependencies
    private DatabaseUtil databaseUtil;
    private SceneManager sceneManager;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseUtil = new DatabaseUtil();
        sceneManager = new SceneManager();

        initializeLanguageCombo();
        setupClock();
        initializeBookingsTable();
        initializeGuidesTable();
        initializeTreksTable();
        initializeCharts();
        updateSystemStats();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        userLabel.setText(user.getUsername());
        refreshData();
    }

    private void initializeLanguageCombo() {
        languageCombo.setItems(FXCollections.observableArrayList("English", "Nepali", "Hindi"));
        languageCombo.setValue("English");
    }

    private void setupClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            dateTimeLabel.setText(DateTimeUtil.formatDateTime(LocalDateTime.now()));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void initializeBookingsTable() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        touristNameColumn.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        guideNameColumn.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        trekNameColumn.setCellValueFactory(new PropertyValueFactory<>("trekName"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        bookingStatusFilter.setItems(FXCollections.observableArrayList(
                "All", "Pending", "Confirmed", "Completed", "Cancelled"));
        bookingStatusFilter.setValue("All");

        bookingStatusFilter.setOnAction(e -> filterBookingsByStatus());

        loadBookings();
    }

    private void initializeGuidesTable() {
        guideIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        guideNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        languagesColumn.setCellValueFactory(new PropertyValueFactory<>("languages"));
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));
        certificationsColumn.setCellValueFactory(new PropertyValueFactory<>("certifications"));

        loadGuides();
    }

    private void initializeTreksTable() {
        trekNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        maxAltitudeColumn.setCellValueFactory(new PropertyValueFactory<>("maxAltitude"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        basePriceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));

        difficultyCombo.setItems(FXCollections.observableArrayList(
                "Easy", "Moderate", "Difficult", "Very Difficult", "Extreme"));

        loadTreks();
    }

    private void initializeCharts() {
        updateBookingsChart();
        updateRevenueChart();
    }

    private void loadBookings() {
        try {
            List<Booking> bookings = databaseUtil.getAllBookings();
            bookingsTable.setItems(FXCollections.observableArrayList(bookings));

            // Update analytics
            totalBookingsLabel.setText(String.valueOf(bookings.size()));
            double totalRevenue = bookings.stream()
                    .mapToDouble(Booking::getPrice)
                    .sum();
            totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));

            // Update pending bookings count
            long pendingCount = bookings.stream()
                    .filter(b -> b.getStatus().equals("Pending"))
                    .count();
            pendingBookingsLabel.setText("Pending Bookings: " + pendingCount);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadGuides() {
        try {
            List<Guide> guides = databaseUtil.getAllGuides();
            guidesTable.setItems(FXCollections.observableArrayList(guides));

            // Update active guides count
            activeGuidesLabel.setText(String.valueOf(guides.size()));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load guides: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTreks() {
        try {
            List<Trek> treks = databaseUtil.getAllTreks();
            treksTable.setItems(FXCollections.observableArrayList(treks));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load treks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBookingsChart() {
        // This would typically pull data from the database
        // For demonstration, we'll use sample data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Bookings");

        series.getData().add(new XYChart.Data<>("Jan", 25));
        series.getData().add(new XYChart.Data<>("Feb", 35));
        series.getData().add(new XYChart.Data<>("Mar", 45));
        series.getData().add(new XYChart.Data<>("Apr", 55));
        series.getData().add(new XYChart.Data<>("May", 65));
        series.getData().add(new XYChart.Data<>("Jun", 75));

        bookingsChart.getData().clear();
        bookingsChart.getData().add(series);
    }

    private void updateRevenueChart() {
        // Sample data for the pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Everest Base Camp", 35),
                new PieChart.Data("Annapurna Circuit", 25),
                new PieChart.Data("Langtang Valley", 20),
                new PieChart.Data("Manaslu Circuit", 10),
                new PieChart.Data("Others", 10)
        );

        revenueChart.setData(pieChartData);
    }

    private void updateSystemStats() {
        // For demonstration purposes, we'll use static values
        activeUsersLabel.setText("Active Users: 12");
        systemStatusLabel.setText("System Status: Online");
    }

    @FXML
    private void handleRefresh() {
        refreshData();
    }

    private void refreshData() {
        loadBookings();
        loadGuides();
        loadTreks();
        updateBookingsChart();
        updateRevenueChart();
        updateSystemStats();
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

    @FXML
    private void handleUpdateStatus() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a booking to update.");
            return;
        }

        // Show dialog to select new status
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Confirmed",
                "Pending", "Confirmed", "Completed", "Cancelled");
        dialog.setTitle("Update Booking Status");
        dialog.setHeaderText("Select New Status");
        dialog.setContentText("Status:");

        dialog.showAndWait().ifPresent(status -> {
            try {
                selectedBooking.setStatus(status);
                databaseUtil.updateBookingStatus(selectedBooking.getId(), status);
                loadBookings(); // Refresh table
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Update Error",
                        "Could not update booking status: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleAssignGuide() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a booking to assign a guide.");
            return;
        }

        try {
            List<Guide> guides = databaseUtil.getAllGuides();
            if (guides.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "No Guides Available",
                        "There are no guides available to assign.");
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>();
            dialog.setTitle("Assign Guide");
            dialog.setHeaderText("Select a Guide");
            dialog.setContentText("Guide:");

            for (Guide guide : guides) {
                dialog.getItems().add(guide.getName());
            }

            dialog.showAndWait().ifPresent(guideName -> {
                try {
                    // Find the guide ID based on name
                    String guideId = guides.stream()
                            .filter(g -> g.getName().equals(guideName))
                            .findFirst()
                            .map(Guide::getId)
                            .orElse(null);

                    if (guideId != null) {
                        databaseUtil.assignGuideToBooking(selectedBooking.getId(), guideId);
                        loadBookings(); // Refresh table
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Assignment Error",
                            "Could not assign guide: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load guides: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a booking to cancel.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Cancel Booking");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("Do you want to cancel this booking? This action cannot be undone.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    databaseUtil.updateBookingStatus(selectedBooking.getId(), "Cancelled");
                    loadBookings(); // Refresh table
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Cancellation Error",
                            "Could not cancel booking: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleAddGuide() {
        if (!validateGuideFields()) {
            return;
        }

        Guide guide = new Guide();
        guide.setName(guideNameField.getText().trim());
        guide.setEmail(emailField.getText().trim());
        guide.setPhoneNumber(phoneField.getText().trim());
        guide.setLanguages(languagesField.getText().trim());
        guide.setYearsOfExperience(Integer.parseInt(experienceField.getText().trim()));
        guide.setCertifications(certificationsField.getText().trim());

        try {
            databaseUtil.addGuide(guide);
            clearGuideFields();
            loadGuides(); // Refresh table
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not add guide: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateGuide() {
        Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a guide to update.");
            return;
        }

        if (!validateGuideFields()) {
            return;
        }

        selectedGuide.setName(guideNameField.getText().trim());
        selectedGuide.setEmail(emailField.getText().trim());
        selectedGuide.setPhoneNumber(phoneField.getText().trim());
        selectedGuide.setLanguages(languagesField.getText().trim());
        selectedGuide.setYearsOfExperience(Integer.parseInt(experienceField.getText().trim()));
        selectedGuide.setCertifications(certificationsField.getText().trim());

        try {
            databaseUtil.updateGuide(selectedGuide);
            clearGuideFields();
            loadGuides(); // Refresh table
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not update guide: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteGuide() {
        Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a guide to delete.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Guide");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("Do you want to delete this guide? This action cannot be undone.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    databaseUtil.deleteGuide(selectedGuide.getId());
                    clearGuideFields();
                    loadGuides(); // Refresh table
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Deletion Error",
                            "Could not delete guide: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleAddTrek() {
        if (!validateTrekFields()) {
            return;
        }

        Trek trek = new Trek();
        trek.setName(trekNameField.getText().trim());
        trek.setRegion(regionField.getText().trim());
        trek.setMaxAltitude(Integer.parseInt(maxAltitudeField.getText().trim()));
        trek.setDuration(Integer.parseInt(durationField.getText().trim()));
        trek.setDifficulty(difficultyCombo.getValue());
        trek.setBasePrice(Double.parseDouble(basePriceField.getText().trim()));

        try {
            databaseUtil.addTrek(trek);
            clearTrekFields();
            loadTreks(); // Refresh table
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not add trek: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateTrek() {
        Trek selectedTrek = treksTable.getSelectionModel().getSelectedItem();
        if (selectedTrek == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a trek to update.");
            return;
        }

        if (!validateTrekFields()) {
            return;
        }

        selectedTrek.setName(trekNameField.getText().trim());
        selectedTrek.setRegion(regionField.getText().trim());
        selectedTrek.setMaxAltitude(Integer.parseInt(maxAltitudeField.getText().trim()));
        selectedTrek.setDuration(Integer.parseInt(durationField.getText().trim()));
        selectedTrek.setDifficulty(difficultyCombo.getValue());
        selectedTrek.setBasePrice(Double.parseDouble(basePriceField.getText().trim()));

        try {
            databaseUtil.updateTrek(selectedTrek);
            clearTrekFields();
            loadTreks(); // Refresh table
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not update trek: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteTrek() {
        Trek selectedTrek = treksTable.getSelectionModel().getSelectedItem();
        if (selectedTrek == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a trek to delete.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Trek");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("Do you want to delete this trek? This action cannot be undone.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    databaseUtil.deleteTrek(selectedTrek.getId());
                    clearTrekFields();
                    loadTreks(); // Refresh table
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Deletion Error",
                            "Could not delete trek: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void filterBookingsByStatus() {
        String status = bookingStatusFilter.getValue();
        try {
            List<Booking> bookings;
            if ("All".equals(status)) {
                bookings = databaseUtil.getAllBookings();
            } else {
                bookings = databaseUtil.getBookingsByStatus(status);
            }
            bookingsTable.setItems(FXCollections.observableArrayList(bookings));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Filter Error",
                    "Could not filter bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateGuideFields() {
        if (guideNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty() ||
                languagesField.getText().trim().isEmpty() ||
                experienceField.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "All required fields must be filled.");
            return false;
        }

        try {
            Integer.parseInt(experienceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Experience must be a number.");
            return false;
        }

        return true;
    }

    private boolean validateTrekFields() {
        if (trekNameField.getText().trim().isEmpty() ||
                regionField.getText().trim().isEmpty() ||
                maxAltitudeField.getText().trim().isEmpty() ||
                durationField.getText().trim().isEmpty() ||
                difficultyCombo.getValue() == null ||
                basePriceField.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "All required fields must be filled.");
            return false;
        }

        try {
            Integer.parseInt(maxAltitudeField.getText().trim());
            Integer.parseInt(durationField.getText().trim());
            Double.parseDouble(basePriceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Max altitude, duration, and base price must be numbers.");
            return false;
        }

        return true;
    }

    private void clearGuideFields() {
        guideNameField.clear();
        emailField.clear();
        phoneField.clear();
        languagesField.clear();
        experienceField.clear();
        certificationsField.clear();
    }

    private void clearTrekFields() {
        trekNameField.clear();
        regionField.clear();
        maxAltitudeField.clear();
        durationField.clear();
        difficultyCombo.getSelectionModel().clearSelection();
        basePriceField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}