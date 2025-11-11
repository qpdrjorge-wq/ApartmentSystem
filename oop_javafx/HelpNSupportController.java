package com.example.oop_javafx;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class HelpNSupportController {
    @FXML AnchorPane drawerPane;
    @FXML Button menuButton;
    private boolean drawerOpen = true;
    @FXML Label usernameLabel;
    private String userEmail;
    private String username;

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
    private void complaintsTenantButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("complaintsTenant.fxml");
    }

    @FXML
    private void apartmentLeaseButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("tenantLease.fxml");
    }

    @FXML
    private void PaymentHistoryButton(ActionEvent event) throws IOException {
        SceneManager.switchScene("PaymentHistoryUI.fxml");
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

    public void setUserEmail(String email) {
        this.userEmail = email;
        this.username = fetchUsernameFromDatabase(email);

        if (usernameLabel != null) {
            usernameLabel.setText(username + '!');
        }
    }

    private String fetchUsernameFromDatabase(String email) {
        String query = "SELECT username FROM tenantaccount WHERE email = ?";
        try (Connection conn = DbConn.connectDB();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            } else {
                return "User";
            }

        } catch (SQLException e) {
            System.err.println("Error fetching username: " + e.getMessage());
            return "User";
        }
    }


}
