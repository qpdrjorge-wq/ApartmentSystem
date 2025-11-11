package com.example.oop_javafx;

import javafx.fxml.FXML;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class tenantDashController {

    @FXML private AnchorPane drawerPane;
    @FXML private Label dateToday;
    @FXML private Button menuButton;
    @FXML private Label usernameLabel;
    @FXML private Label balance;
    @FXML private Label dueDate;
    @FXML private Label paymentStatus;

    @FXML private TilePane recentPayHistory;

    private String userEmail;
    private int unitId;
    private int tenantId;
    private boolean drawerOpen = true;

    @FXML
    public void initialize() {
        try {
            drawerPane.setTranslateX(-350);
            drawerOpen = false;

            TenantSession session = TenantSession.getInstance();
            if (session.isLoggedIn()) {
                usernameLabel.setText(session.getUsername() + "!");
                userEmail = session.getEmail();
                unitId = session.getUnitId();
                tenantId = session.getTenantAccountId();
            }

            LocalDate today = LocalDate.now();
            dateToday.setText(today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));

            loadTenantDashboardData();
            loadRecentPayments(tenantId); // Load 2 most recent payments

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Initialization Error", e.getMessage());
        }
    }

    private void loadTenantDashboardData() {
        try (Connection conn = DbConn.connectDB()) {

            LocalDate startDate = null;
            String roomQuery = "SELECT startDate FROM roomaccount WHERE unitId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(roomQuery)) {
                stmt.setInt(1, unitId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Date sDate = rs.getDate("startDate");
                    if (sDate != null) startDate = sDate.toLocalDate();
                }
            }

            String payStatus = "-";
            String billingPeriod = "Monthly";
            String billingQuery = "SELECT billingPeriod, paymentStatus FROM billing WHERE unitId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(billingQuery)) {
                stmt.setInt(1, unitId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String period = rs.getString("billingPeriod");
                    if (period != null) billingPeriod = period;
                    payStatus = rs.getString("paymentStatus");
                }
            }

            double totalAmount = 0;
            String billStmtQuery = "SELECT totalAmount FROM billingstatement WHERE tenantId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(billStmtQuery)) {
                stmt.setInt(1, tenantId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalAmount = rs.getDouble("totalAmount");
                }
            }

            double totalPaid = 0;
            String payQuery = "SELECT SUM(amountPaid) AS totalPaid FROM paymenttracking WHERE tenantId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(payQuery)) {
                stmt.setInt(1, tenantId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalPaid = rs.getDouble("totalPaid");
                }
            }

            double remainingBalance = totalAmount - totalPaid;
            if (remainingBalance < 0) remainingBalance = 0;

            LocalDate nextDueDate = computeDueDate(startDate, billingPeriod, totalAmount, totalPaid);

            balance.setText(String.format("₱%.2f", remainingBalance));
            paymentStatus.setText(payStatus != null ? payStatus : "-");
            dueDate.setText(nextDueDate != null ? nextDueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "—");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private LocalDate computeDueDate(LocalDate startDate, String billingPeriod,
                                     double totalAmount, double totalPaid) {
        if (startDate == null) return null;

        long periodDays;
        switch (billingPeriod.toLowerCase()) {
            case "quarterly" -> periodDays = 93;
            case "semi-annual" -> periodDays = 186;
            case "annual" -> periodDays = 365;
            default -> periodDays = 31; // monthly
        }

        LocalDate dueDate = startDate.plusDays(periodDays);

        if (totalPaid >= totalAmount) return null;

        return dueDate.isBefore(LocalDate.now()) ? LocalDate.now() : dueDate;
    }

    private void loadRecentPayments(int tenantId) {
        recentPayHistory.getChildren().clear();

        String query = """
                SELECT paymentDate, amountPaid, modeOfPayment
                FROM paymenttracking
                WHERE tenantId = ?
                ORDER BY paymentDate DESC
                LIMIT 2
                """;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        try (Connection conn = DbConn.connectDB();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tenantId);
            ResultSet rs = stmt.executeQuery();

            boolean hasPayments = false;

            while (rs.next()) {
                hasPayments = true;

                FXMLLoader loader = new FXMLLoader(getClass().getResource("paymentHistoryCard.fxml"));
                AnchorPane card = loader.load();

                paymentHistoryCardController controller = loader.getController();

                String rawDate = rs.getString("paymentDate"); // assuming YYYY-MM-DD or Timestamp
                LocalDate date = LocalDate.parse(rawDate.substring(0, 10)); // take only date portion
                String formattedDate = date.format(formatter);

                controller.setData(
                        formattedDate,
                        rs.getDouble("amountPaid"),
                        rs.getString("modeOfPayment")
                );

                recentPayHistory.getChildren().add(card);
            }

            if (!hasPayments) {
                Label placeholder = new Label("No recent payments.");
                placeholder.setStyle("-fx-font-size: 32px; -fx-text-fill: gray;");
                recentPayHistory.getChildren().add(placeholder);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void toggleDrawer() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.millis(300));
        slide.setNode(drawerPane);
        slide.setToX(drawerOpen ? -250 : 0);
        drawerOpen = !drawerOpen;
        slide.play();
    }

    @FXML private void paymentMethodButton(ActionEvent event) throws IOException { SceneManager.switchScene("optionPayMethod.fxml"); }
    @FXML private void complaintsTenantButton(ActionEvent event) throws IOException { SceneManager.switchScene("complaintsTenant.fxml"); }
    @FXML private void apartmentLeaseButton(ActionEvent event) throws IOException { SceneManager.switchScene("tenantLease.fxml"); }
    @FXML private void PaymentHistoryButton(ActionEvent event) throws IOException { SceneManager.switchScene("PaymentHistoryUI.fxml"); }
    @FXML private void HelpNSupportButton(ActionEvent event) throws IOException { SceneManager.switchScene("HelpNSupportUI.fxml"); }
    @FXML private void ContactNAboutButton(ActionEvent event) throws IOException { SceneManager.switchScene("ContactNAbout.fxml"); }

    @FXML
    private void logoutButton(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SceneManager.switchScene("loginPage.fxml");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
