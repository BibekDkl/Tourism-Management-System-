package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.Booking;
import com.example.nepaltourismmanagement.models.Trek;
import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.LanguageManager;
import com.example.nepaltourismmanagement.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class TouristDashboardController implements Initializable {

    // Header components
    @FXML private Label userLabel;
    @FXML private Label dateTimeLabel;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private VBox rootContainer; // Root container for the entire dashboard

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
    private LanguageManager languageManager;
    private User currentUser;

    // Current date/time constant
    private static final String CURRENT_DATETIME = "2025-07-30 07:30:26";
    private static final String CURRENT_USERNAME = "BibekDkl";

    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Festival periods for discount
    private static final LocalDate DASHAIN_START = LocalDate.of(2025, Month.SEPTEMBER, 27);
    private static final LocalDate DASHAIN_END = LocalDate.of(2025, Month.OCTOBER, 5);
    private static final LocalDate TIHAR_START = LocalDate.of(2025, Month.OCTOBER, 15);
    private static final LocalDate TIHAR_END = LocalDate.of(2025, Month.OCTOBER, 30);
    private static final LocalDate HOLI_START = LocalDate.of(2025, Month.MARCH, 1);
    private static final LocalDate HOLI_END = LocalDate.of(2025, Month.MARCH, 5);

    // Discount percentage for festivals
    private static final double FESTIVAL_DISCOUNT_PERCENTAGE = 20.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing TouristDashboardController");
        databaseUtil = new DatabaseUtil();
        sceneManager = new SceneManager();
        languageManager = LanguageManager.getInstance();

        initializeLanguageCombo();
        setupDateTime();
        initializeAvailableTripsTable();
        initializeBookedTripsTable();
        initializeManageTripsTable();

        System.out.println("TouristDashboardController initialized");
    }

    public void setCurrentUser(User user) {
        if (user == null) {
            System.err.println("ERROR: Attempted to set null user in TouristDashboardController");
            showAlert(Alert.AlertType.ERROR, languageManager.translate("System Error"),
                    languageManager.translate("User information could not be loaded. Please log in again."));
            return;
        }

        System.out.println("Setting current user to: " + user.getUsername() + " (ID: " + user.getId() + ")");
        this.currentUser = user;

        // Use the constant username instead of the actual user's username
        userLabel.setText(CURRENT_USERNAME);

        loadData();
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

        // Update dynamic UI elements
        refreshActionButtonLabels();
        refreshDynamicContent();
    }

    private void refreshActionButtonLabels() {
        // The actionColumn contains buttons that need manual translation
        if (availableTripsTable != null && availableTripsTable.getItems() != null) {
            availableTripsTable.refresh();
        }
    }

    private void refreshDynamicContent() {
        // Refresh data that might need translation
        loadAvailableTrips();
        loadBookedTrips();
    }

    private void setupDateTime() {
        dateTimeLabel.setText(CURRENT_DATETIME);
    }

    private void initializeAvailableTripsTable() {
        tripNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));

        // Custom cell factory for action column with "Book" button
        actionColumn.setCellFactory(column -> {
            return new TableCell<Trek, Button>() {
                private final Button bookButton = new Button(languageManager.translate("Book"));

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
                        // Update button text with current language
                        bookButton.setText(languageManager.translate("Book"));
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
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Database Error"),
                    languageManager.translate("Could not load available treks") + ": " + e.getMessage());
        }
    }

    private void loadBookedTrips() {
        try {
            if (currentUser == null) {
                System.err.println("ERROR: currentUser is null in loadBookedTrips");
                showAlert(Alert.AlertType.ERROR, languageManager.translate("System Error"),
                        languageManager.translate("User information is missing. Please log out and log in again."));
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
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Database Error"),
                    languageManager.translate("Could not load booked trips") + ": " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing data...");
        loadData();
        refreshActionButtonLabels();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            System.out.println("Logging out user: " + (currentUser != null ? currentUser.getUsername() : "unknown"));
            sceneManager.switchToLoginPage(event);
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Navigation Error"),
                    languageManager.translate("Could not navigate to login page") + ": " + e.getMessage());
        }
    }

    private void handleBookTrek(Trek trek) {
        System.out.println("Booking trek: " + trek.getName());

        // Check if user is properly set
        if (currentUser == null) {
            System.err.println("ERROR: currentUser is null in handleBookTrek");
            showAlert(Alert.AlertType.ERROR, languageManager.translate("System Error"),
                    languageManager.translate("User information is missing. Please log out and log in again."));
            return;
        }

        System.out.println("Trek difficulty: " + trek.getDifficulty());
        System.out.println("Current user: " + currentUser.getUsername() + " (ID: " + currentUser.getId() + ")");

        try {
            // Show the date picker dialog first
            showDatePickerDialog(trek);
        } catch (Exception e) {
            System.err.println("Error in handleBookTrek: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Booking Error"),
                    languageManager.translate("An error occurred while processing your booking") + ": " + e.getMessage());
        }
    }

    /**
     * New method to show date picker dialog with festival discount information
     */
    private void showDatePickerDialog(Trek trek) {
        // Create a dialog
        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle(languageManager.translate("Select Trip Date"));
        dialog.setHeaderText(languageManager.translate("Please select a date for your trip"));

        // Set the button types
        ButtonType confirmButtonType = new ButtonType(languageManager.translate("Confirm"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Create the date picker and grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusDays(7)); // Default to one week from now

        // Add festival discount information
        VBox festivalInfo = new VBox(5);

        Text festivalTitle = new Text(languageManager.translate("Festival Discount Information"));
        festivalTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        festivalTitle.setFill(Color.GREEN);

        Label discountLabel = new Label(languageManager.translate("Book during festivals and get 20% discount!"));

        // Festival dates
        VBox festivalDates = new VBox(2);
        festivalDates.getChildren().addAll(
                new Label("• " + languageManager.translate("Dashain") + ": " +
                        DASHAIN_START.format(dateFormatter) + " - " +
                        DASHAIN_END.format(dateFormatter)),
                new Label("• " + languageManager.translate("Tihar") + ": " +
                        TIHAR_START.format(dateFormatter) + " - " +
                        TIHAR_END.format(dateFormatter)),
                new Label("• " + languageManager.translate("Holi") + ": " +
                        HOLI_START.format(dateFormatter) + " - " +
                        HOLI_END.format(dateFormatter))
        );

        festivalInfo.getChildren().addAll(festivalTitle, discountLabel, festivalDates);

        // Add a label to show the price calculation
        Label priceInfoLabel = new Label();
        updatePriceInfo(trek, datePicker.getValue(), priceInfoLabel);

        // Update price info when date changes
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            updatePriceInfo(trek, newVal, priceInfoLabel);
        });

        // Add everything to the grid
        grid.add(new Label(languageManager.translate("Trip Date:")), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(festivalInfo, 0, 1, 2, 1);
        grid.add(priceInfoLabel, 0, 2, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the date picker by default
        datePicker.requestFocus();

        // Convert the result to LocalDate when the confirm button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return datePicker.getValue();
            }
            return null;
        });

        // Show the dialog and process the result
        dialog.showAndWait().ifPresent(selectedDate -> {
            // Check if the selected date is valid (not in the past)
            if (selectedDate.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, languageManager.translate("Invalid Date"),
                        languageManager.translate("Please select a future date for your trip."));
                return;
            }

            // Calculate the final price with potential discount
            double finalPrice = calculatePrice(trek.getBasePrice(), selectedDate);

            // Continue with booking process
            if ("Very Difficult".equals(trek.getDifficulty()) || "Extreme".equals(trek.getDifficulty())) {
                showWarningDialog(trek, selectedDate, finalPrice);
            } else {
                proceedWithBooking(trek, selectedDate, finalPrice);
            }
        });
    }

    /**
     * Update the price information label based on selected date
     */
    private void updatePriceInfo(Trek trek, LocalDate selectedDate, Label priceInfoLabel) {
        double basePrice = trek.getBasePrice();
        double finalPrice = calculatePrice(basePrice, selectedDate);

        StringBuilder priceInfo = new StringBuilder();
        priceInfo.append(languageManager.translate("Base Price") + ": $" + String.format("%.2f", basePrice) + "\n");

        if (isFestivalDate(selectedDate)) {
            priceInfo.append(languageManager.translate("Festival Discount") + ": " +
                    FESTIVAL_DISCOUNT_PERCENTAGE + "%\n");
            priceInfo.append(languageManager.translate("Final Price") + ": $" +
                    String.format("%.2f", finalPrice) + " 🎉");
            priceInfoLabel.setTextFill(Color.GREEN);
        } else {
            priceInfo.append(languageManager.translate("Final Price") + ": $" +
                    String.format("%.2f", finalPrice));
            priceInfoLabel.setTextFill(Color.BLACK);
        }

        priceInfoLabel.setText(priceInfo.toString());
    }

    /**
     * Calculate price with potential festival discount
     */
    private double calculatePrice(double basePrice, LocalDate date) {
        if (isFestivalDate(date)) {
            return basePrice * (1 - FESTIVAL_DISCOUNT_PERCENTAGE / 100.0);
        }
        return basePrice;
    }

    /**
     * Check if the date falls within any festival period
     */
    private boolean isFestivalDate(LocalDate date) {
        return (date.isEqual(DASHAIN_START) || date.isEqual(DASHAIN_END) ||
                (date.isAfter(DASHAIN_START) && date.isBefore(DASHAIN_END))) ||
                (date.isEqual(TIHAR_START) || date.isEqual(TIHAR_END) ||
                        (date.isAfter(TIHAR_START) && date.isBefore(TIHAR_END))) ||
                (date.isEqual(HOLI_START) || date.isEqual(HOLI_END) ||
                        (date.isAfter(HOLI_START) && date.isBefore(HOLI_END)));
    }

    /**
     * Get the festival name if the date is within a festival period
     */
    private String getFestivalName(LocalDate date) {
        if (date.isEqual(DASHAIN_START) || date.isEqual(DASHAIN_END) ||
                (date.isAfter(DASHAIN_START) && date.isBefore(DASHAIN_END))) {
            return "Dashain";
        } else if (date.isEqual(TIHAR_START) || date.isEqual(TIHAR_END) ||
                (date.isAfter(TIHAR_START) && date.isBefore(TIHAR_END))) {
            return "Tihar";
        } else if (date.isEqual(HOLI_START) || date.isEqual(HOLI_END) ||
                (date.isAfter(HOLI_START) && date.isBefore(HOLI_END))) {
            return "Holi";
        }
        return "";
    }

    private void showWarningDialog(Trek trek, LocalDate tripDate, double finalPrice) {
        try {
            // Make sure to use the correct path based on your application structure
            // Try multiple paths to find the warning dialog
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
                showAlert(Alert.AlertType.ERROR, languageManager.translate("Resource Error"),
                        languageManager.translate("Could not find warning dialog resource. Proceeding with direct booking."));
                proceedWithBooking(trek, tripDate, finalPrice);
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
            // Pass the selected date and final price to the warning controller if it supports it
            if (controller instanceof WarningController) {
                ((WarningController) controller).setTripDateAndPrice(tripDate, finalPrice);
            }

            Stage warningStage = new Stage();
            warningStage.initModality(Modality.APPLICATION_MODAL);
            warningStage.setTitle(languageManager.translate("Trek Warning"));
            warningStage.setScene(new Scene(root));
            warningStage.showAndWait();

            // Refresh bookings after warning dialog closes (booking may have been made)
            System.out.println("Warning dialog closed, refreshing bookings");
            loadBookedTrips();
        } catch (IOException e) {
            System.err.println("Error loading warning dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Dialog Error"),
                    languageManager.translate("Could not show warning dialog") + ": " +
                            e.getMessage() + "\n" + languageManager.translate("Proceeding with direct booking."));
            proceedWithBooking(trek, tripDate, finalPrice);
        }
    }

    private void proceedWithBooking(Trek trek, LocalDate tripDate, double finalPrice) {
        try {
            // Double-check user is set
            if (currentUser == null) {
                throw new IllegalStateException(languageManager.translate("User information is missing"));
            }

            // Create a new booking
            Booking booking = new Booking();
            booking.setTrekId(trek.getId());
            booking.setTouristId(currentUser.getId());
            booking.setStatus("Pending");
            booking.setPrice(finalPrice);
            booking.setDuration(trek.getDuration());
            booking.setBookingDate(tripDate.format(dateFormatter));
            booking.setHighRiskAcknowledged(false);

            System.out.println("Creating booking: " +
                    "Trek ID: " + trek.getId() + ", " +
                    "Tourist ID: " + currentUser.getId() + ", " +
                    "Date: " + booking.getBookingDate() + ", " +
                    "Price: $" + finalPrice);

            // Save to database
            boolean success = databaseUtil.createBooking(booking);

            if (success) {
                StringBuilder message = new StringBuilder();
                message.append(languageManager.translate("Your booking for") + " " + trek.getName() + " " +
                        languageManager.translate("has been submitted and is awaiting confirmation."));

                // Add discount information if applicable
                if (isFestivalDate(tripDate)) {
                    String festivalName = getFestivalName(tripDate);
                    message.append("\n\n" + languageManager.translate("Congratulations! You received a") + " " +
                            FESTIVAL_DISCOUNT_PERCENTAGE + "% " +
                            languageManager.translate("discount for booking during") + " " +
                            languageManager.translate(festivalName) + "!");
                }

                showAlert(Alert.AlertType.INFORMATION, languageManager.translate("Booking Successful"), message.toString());
                System.out.println("Booking created successfully with price: $" + finalPrice);
                loadBookedTrips();
            } else {
                showAlert(Alert.AlertType.ERROR, languageManager.translate("Booking Error"),
                        languageManager.translate("Failed to create booking. Please try again."));
                System.out.println("Failed to create booking");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Booking Error"),
                    languageManager.translate("Could not complete booking") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateBooking() {
        Booking selectedBooking = manageTripsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, languageManager.translate("Selection Required"),
                    languageManager.translate("Please select a booking to update."));
            return;
        }

        // Can only update bookings in "Pending" status
        if (!"Pending".equals(selectedBooking.getStatus())) {
            showAlert(Alert.AlertType.WARNING, languageManager.translate("Update Restricted"),
                    languageManager.translate("Only pending bookings can be updated."));
            return;
        }

        try {
            // Get the trek for this booking
            Trek trek = databaseUtil.getTrekById(selectedBooking.getTrekId());
            if (trek == null) {
                showAlert(Alert.AlertType.ERROR, languageManager.translate("Trek Not Found"),
                        languageManager.translate("Could not find trek information for this booking."));
                return;
            }

            // Show date picker dialog for updating
            showDateUpdateDialog(selectedBooking, trek);
        } catch (Exception e) {
            System.err.println("Error getting trek information: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Data Error"),
                    languageManager.translate("Could not load trek information") + ": " + e.getMessage());
        }
    }

    /**
     * New method for showing date update dialog
     */
    private void showDateUpdateDialog(Booking booking, Trek trek) {
        // Create a dialog
        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle(languageManager.translate("Update Trip Date"));
        dialog.setHeaderText(languageManager.translate("Update date for") + " " + trek.getName());

        // Set the button types
        ButtonType updateButtonType = new ButtonType(languageManager.translate("Update"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the date picker and grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker();

        // Try to parse the current booking date
        try {
            LocalDate currentDate = LocalDate.parse(booking.getBookingDate(), dateFormatter);
            datePicker.setValue(currentDate);
        } catch (DateTimeParseException e) {
            // If parsing fails, set to one week from now
            datePicker.setValue(LocalDate.now().plusDays(7));
        }

        // Add festival discount information
        VBox festivalInfo = new VBox(5);

        Text festivalTitle = new Text(languageManager.translate("Festival Discount Information"));
        festivalTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        festivalTitle.setFill(Color.GREEN);

        Label discountLabel = new Label(languageManager.translate("Book during festivals and get 20% discount!"));

        // Festival dates
        VBox festivalDates = new VBox(2);
        festivalDates.getChildren().addAll(
                new Label("• " + languageManager.translate("Dashain") + ": " +
                        DASHAIN_START.format(dateFormatter) + " - " +
                        DASHAIN_END.format(dateFormatter)),
                new Label("• " + languageManager.translate("Tihar") + ": " +
                        TIHAR_START.format(dateFormatter) + " - " +
                        TIHAR_END.format(dateFormatter)),
                new Label("• " + languageManager.translate("Holi") + ": " +
                        HOLI_START.format(dateFormatter) + " - " +
                        HOLI_END.format(dateFormatter))
        );

        festivalInfo.getChildren().addAll(festivalTitle, discountLabel, festivalDates);

        // Add a label to show the price calculation
        Label priceInfoLabel = new Label();
        updatePriceInfo(trek, datePicker.getValue(), priceInfoLabel);

        // Update price info when date changes
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            updatePriceInfo(trek, newVal, priceInfoLabel);
        });

        // Add everything to the grid
        grid.add(new Label(languageManager.translate("Current Date:") + " " + booking.getBookingDate()), 0, 0, 2, 1);
        grid.add(new Label(languageManager.translate("New Trip Date:")), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(festivalInfo, 0, 2, 2, 1);
        grid.add(priceInfoLabel, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the date picker by default
        datePicker.requestFocus();

        // Convert the result to LocalDate when the update button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return datePicker.getValue();
            }
            return null;
        });

        // Show the dialog and process the result
        dialog.showAndWait().ifPresent(selectedDate -> {
            // Check if the selected date is valid (not in the past)
            if (selectedDate.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, languageManager.translate("Invalid Date"),
                        languageManager.translate("Please select a future date for your trip."));
                return;
            }

            // Calculate the final price with potential discount
            double finalPrice = calculatePrice(trek.getBasePrice(), selectedDate);

            // Update the booking with new date and price
            updateBookingDateAndPrice(booking, selectedDate, finalPrice);
        });
    }

    /**
     * Update booking date and price
     */
    private void updateBookingDateAndPrice(Booking booking, LocalDate newDate, double newPrice) {
        try {
            // Create an updated booking
            Booking updatedBooking = booking;
            updatedBooking.setBookingDate(newDate.format(dateFormatter));
            updatedBooking.setPrice(newPrice);

            // Update the booking in database
            boolean success = databaseUtil.updateBooking(updatedBooking);

            if (success) {
                StringBuilder message = new StringBuilder();
                message.append(languageManager.translate("Your booking has been updated successfully."));

                // Add discount information if applicable
                if (isFestivalDate(newDate)) {
                    String festivalName = getFestivalName(newDate);
                    message.append("\n\n" + languageManager.translate("Congratulations! You received a") + " " +
                            FESTIVAL_DISCOUNT_PERCENTAGE + "% " +
                            languageManager.translate("discount for booking during") + " " +
                            languageManager.translate(festivalName) + "!");
                }

                showAlert(Alert.AlertType.INFORMATION, languageManager.translate("Booking Updated"), message.toString());
                loadBookedTrips();
            } else {
                showAlert(Alert.AlertType.ERROR, languageManager.translate("Update Error"),
                        languageManager.translate("Failed to update booking. Please try again."));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Update Error"),
                    languageManager.translate("Could not update booking") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = manageTripsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, languageManager.translate("Selection Required"),
                    languageManager.translate("Please select a booking to cancel."));
            return;
        }

        // Cannot cancel completed bookings
        if ("Completed".equals(selectedBooking.getStatus())) {
            showAlert(Alert.AlertType.WARNING, languageManager.translate("Cancellation Restricted"),
                    languageManager.translate("Completed bookings cannot be cancelled."));
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle(languageManager.translate("Cancel Booking"));
        confirmDialog.setHeaderText(languageManager.translate("Are you sure?"));
        confirmDialog.setContentText(
                languageManager.translate("Do you want to cancel this booking? This action cannot be undone."));

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    System.out.println("Cancelling booking with ID: " + selectedBooking.getId());
                    boolean success = databaseUtil.updateBookingStatus(selectedBooking.getId(), "Cancelled");

                    if (success) {
                        System.out.println("Booking cancelled successfully");
                        showAlert(Alert.AlertType.INFORMATION, languageManager.translate("Booking Cancelled"),
                                languageManager.translate("Your booking has been cancelled successfully."));
                    } else {
                        System.err.println("Failed to cancel booking");
                        showAlert(Alert.AlertType.ERROR, languageManager.translate("Cancellation Error"),
                                languageManager.translate("Could not cancel booking. Please try again."));
                    }

                    loadBookedTrips();
                } catch (Exception e) {
                    System.err.println("Error cancelling booking: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, languageManager.translate("Cancellation Error"),
                            languageManager.translate("Could not cancel booking") + ": " + e.getMessage());
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