package com.example.nepaltourismmanagement.controllers;

import com.example.nepaltourismmanagement.models.Booking;
import com.example.nepaltourismmanagement.models.Trek;
import com.example.nepaltourismmanagement.models.User;
import com.example.nepaltourismmanagement.utils.DatabaseUtil;
import com.example.nepaltourismmanagement.utils.DateTimeUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class WarningController {

    @FXML private Label timestampLabel;
    @FXML private Label userLabel;
    @FXML private Button chooseAnotherButton;
    @FXML private Button proceedButton;

    private Trek currentTrek;
    private User currentUser;
    private DatabaseUtil databaseUtil;

    public void initialize() {
        databaseUtil = new DatabaseUtil();
        timestampLabel.setText(DateTimeUtil.formatDateTime(LocalDateTime.now()));
    }

    public void setTrekAndUser(Trek trek, User user) {
        this.currentTrek = trek;
        this.currentUser = user;
        userLabel.setText(user.getUsername());
        System.out.println("Warning controller initialized with trek: " + trek.getName() + " and user: " + user.getUsername());
    }

    @FXML
    private void handleChooseAnother(ActionEvent event) {
        System.out.println("User chose to select another trek");
        closeStage(event);
    }

    @FXML
    private void handleProceed(ActionEvent event) {
        System.out.println("User chose to proceed with booking despite warning");
        try {
            // Create a new booking despite the warning
            Booking booking = new Booking();
            booking.setTrekId(currentTrek.getId());
            booking.setTouristId(currentUser.getId());
            booking.setStatus("Pending");
            booking.setPrice(currentTrek.getBasePrice());
            booking.setDuration(currentTrek.getDuration());
            booking.setBookingDate(DateTimeUtil.formatDate(LocalDateTime.now()));
            booking.setHighRiskAcknowledged(true); // Mark as acknowledged high risk

            // Save to database
            boolean success = databaseUtil.createBooking(booking);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Booking Successful",
                        "Your booking for " + currentTrek.getName() + " has been submitted with risk acknowledgment and is awaiting confirmation.");
                System.out.println("High-risk booking created successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Booking Error",
                        "Could not create the booking. Please try again.");
                System.out.println("Failed to create high-risk booking");
            }

            closeStage(event);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Booking Error",
                    "Could not complete booking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
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