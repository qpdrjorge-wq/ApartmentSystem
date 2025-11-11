package com.example.oop_javafx;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class tenantLeaseController {

    @FXML AnchorPane drawerPane;
    @FXML Button menuButton;
    private boolean drawerOpen = true;
    @FXML private Label usernameLabel;
    private int unitId;
    private String userEmail;

    @FXML private Label leaseStart;
    @FXML private Label leaseEnd;
    @FXML private Label rentAmount;
    @FXML private Label dueDate;
    @FXML private Label billingPeriod;

    @FXML
    public void initialize() {
        drawerPane.setTranslateX(-350);
        drawerOpen = false;

        TenantSession session = TenantSession.getInstance();
        if (session.isLoggedIn()) {
            usernameLabel.setText(session.getUsername() + "!");
            userEmail = session.getEmail();
            unitId = session.getUnitId();

            LocalDate start = session.getLeaseStartDate();
            LocalDate end = session.getLeaseEndDate();
            double price = session.getRentPrice();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

            if (start != null) leaseStart.setText(start.format(formatter));
            if (end != null) leaseEnd.setText(end.format(formatter));
            rentAmount.setText(String.format("₱%.2f", price));

            if (start != null) {
                LocalDate due = start.plusDays(31);
                dueDate.setText(due.format(formatter));
            }

            billingPeriod.setText(session.getBillingPeriod() != null ? session.getBillingPeriod() : "N/A");

        }
    }

    @FXML
    private void toggleDrawer() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.millis(300));
        slide.setNode(drawerPane);

        if (drawerOpen) {
            slide.setToX(-250);
            drawerOpen = false;
        } else {
            slide.setToX(0);
            drawerOpen = true;
        }
        slide.play();
    }


    @FXML private void dashboardButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("tenantDashboard.fxml");
    }

    @FXML private void paymentMethodButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("optionPayMethod.fxml");
    }

    @FXML private void complaintsTenantButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("complaintsTenant.fxml");
    }

    @FXML private void PaymentHistoryButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("PaymentHistoryUI.fxml");
    }

    @FXML private void HelpNSupportButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("HelpNSupportUI.fxml");
    }

    @FXML private void ContactNAboutButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("ContactNAbout.fxml");
    }

    @FXML private void logoutButton(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to log out?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            TenantSession.getInstance().clearSession();
            SceneManager.switchScene("loginPage.fxml");
        } else {
            alert.close();
        }
    }
}
