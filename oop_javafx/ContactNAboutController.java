package com.example.oop_javafx;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Optional;

public class ContactNAboutController {
    @FXML
    AnchorPane drawerPane;
    @FXML Button menuButton;
    private boolean drawerOpen = true;
    @FXML private Label usernameLabel;


    @FXML //for the side drawer
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

    @FXML
    public void initialize() {

        //navdrawer initialization
        drawerPane.setTranslateX(-350);
        drawerOpen = false;
        try {
            TenantSession session = TenantSession.getInstance();
            if (session.isLoggedIn()) {
                usernameLabel.setText(session.getUsername() + "!");
            } else {
                usernameLabel.setText("Guest");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void dashboardButton (ActionEvent event) throws IOException {
        SceneManager.switchScene("tenantDashboard.fxml");
    }

    @FXML
    private void paymentMethodButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("optionPayMethod.fxml");
    }

    @FXML
    private void complaintsTenantButton (ActionEvent event) throws IOException {
        SceneManager.switchScene("complaintsTenant.fxml");
    }

    @FXML
    private void apartmentLeaseButton (ActionEvent event) throws IOException {
        SceneManager.switchScene("tenantLease.fxml");
    }

    @FXML
    private void PaymentHistoryButton (ActionEvent event) throws IOException {
        SceneManager.switchScene("PaymentHistoryUI.fxml");
    }

    @FXML
    private void HelpNSupportButton (ActionEvent event) throws IOException {
        SceneManager.switchScene("HelpNSupportUI.fxml");
    }

    @FXML
    private void logoutButton(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to log out?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            SceneManager.switchScene("loginPage.fxml");
        } else {
            alert.close();
        }
    }


}
