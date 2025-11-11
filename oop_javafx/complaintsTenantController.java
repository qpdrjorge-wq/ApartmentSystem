package com.example.oop_javafx;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Optional;


public class complaintsTenantController {
    @FXML AnchorPane drawerPane;
    @FXML Button menuButton;
    private boolean drawerOpen = true;

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
    private void ContactNAboutButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("ContactNAbout.fxml");
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
