package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.Booking;
import com.example.nepaltourismmanagement.models.Trek;
import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.LanguageManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class WarningController implements Initializable {

    @FXML private Text warningTitle;
    @FXML private Text warningDescription;
    @FXML private Label trekNameLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label altitudeLabel;
    @FXML private ImageView warningImage;
    @FXML private CheckBox acknowledgementCheckbox;
    @FXML private Button proceedButton;
    @FXML private Button cancelButton;

    // New fields for handling trip date and price
    private LocalDate tripDate;
    private double finalPrice;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Trek currentTrek;
    private User currentUser;
    private DatabaseUtil databaseUtil;
    private LanguageManager languageManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseUtil = new DatabaseUtil();
        languageManager = LanguageManager.getInstance();

        // Disable proceed button until checkbox is checked
        proceedButton.setDisable(true);

        // Add listener for checkbox
        acknowledgementCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            proceedButton.setDisable(!newVal);
        });
    }

    public void setTrekAndUser(Trek trek, User user) {
        this.currentTrek = trek;
        this.currentUser = user;

        // Update UI with trek details
        trekNameLabel.setText(trek.getName());
        difficultyLabel.setText(trek.getDifficulty());
        altitudeLabel.setText(String.valueOf(trek.getMaxAltitude()) + " m");

        // Update warning text
        warningTitle.setText(languageManager.translate("Warning: High Difficulty Trek"));
        warningDescription.setText(languageManager.translate(
                "This trek is rated as") + " " + trek.getDifficulty() + ". " +
                languageManager.translate("It requires excellent physical fitness, prior high-altitude experience, and proper acclimatization. " +
                        "Weather conditions can change rapidly, and emergency evacuation may be difficult."));

        // Update checkbox text
        acknowledgementCheckbox.setText(languageManager.translate(
                "I acknowledge the risks involved and confirm I am physically prepared for this trek."));
    }

    // Add the new method to set trip date and price
    public void setTripDateAndPrice(LocalDate tripDate, double finalPrice) {
        this.tripDate = tripDate;
        this.finalPrice = finalPrice;
    }

    @FXML
    private void handleProceed() {
        if (!acknowledgementCheckbox.isSelected()) {
            return;
        }

        try {
            // Create a new booking
            Booking booking = new Booking();
            booking.setTrekId(currentTrek.getId());
            booking.setTouristId(currentUser.getId());
            booking.setStatus("Pending");
            booking.setPrice(finalPrice);
            booking.setDuration(currentTrek.getDuration());
            booking.setBookingDate(tripDate != null ? tripDate.format(dateFormatter) : "2025-07-30");
            booking.setHighRiskAcknowledged(true);

            // Save to database
            boolean success = databaseUtil.createBooking(booking);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, languageManager.translate("Booking Successful"),
                        languageManager.translate("Your booking for") + " " + currentTrek.getName() + " " +
                                languageManager.translate("has been submitted and is awaiting confirmation."));
            } else {
                showAlert(Alert.AlertType.ERROR, languageManager.translate("Booking Error"),
                        languageManager.translate("Failed to create booking. Please try again."));
            }

            // Close the warning dialog
            closeWindow();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, languageManager.translate("Booking Error"),
                    languageManager.translate("Could not complete booking") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}