package com.example.oop_javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class tenantLoginController {

    @FXML private TextField tenantEmail;
    @FXML private TextField tenantPassword;

    @FXML
    void handleLogin(ActionEvent event) {
        String email = tenantEmail.getText().trim();
        String password = tenantPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter both email and password.");
            return;
        }

        try (Connection conn = DbConn.connectDB()) {

            String query = "SELECT * FROM tenantaccount WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int tenantId = rs.getInt("tenantAccountId");
                String username = rs.getString("username");
                int unitId = rs.getInt("unitId");

                TenantSession session = TenantSession.getInstance();
                session.setTenantData(tenantId, username, email, unitId);

                String roomQuery = "SELECT startDate, endDate, price FROM roomaccount WHERE unitId = ?";
                PreparedStatement roomStmt = conn.prepareStatement(roomQuery);
                roomStmt.setInt(1, unitId);
                ResultSet roomRs = roomStmt.executeQuery();

                if (roomRs.next()) {
                    Timestamp startTs = roomRs.getTimestamp("startDate");
                    Timestamp endTs = roomRs.getTimestamp("endDate");
                    double price = roomRs.getDouble("price");

                    LocalDate startDate = (startTs != null) ? startTs.toLocalDateTime().toLocalDate() : null;
                    LocalDate endDate = (endTs != null) ? endTs.toLocalDateTime().toLocalDate() : null;

                    session.setLeaseData(startDate, endDate, price);
                }

                String billingQuery = "SELECT billingPeriod FROM billing WHERE unitId = ?";
                PreparedStatement billingStmt = conn.prepareStatement(billingQuery);
                billingStmt.setInt(1, unitId);
                ResultSet billingRs = billingStmt.executeQuery();

                if (billingRs.next()) {
                    String billingPeriod = billingRs.getString("billingPeriod");
                    session.setBillingPeriod(billingPeriod != null ? billingPeriod : "N/A");
                } else {
                    session.setBillingPeriod("N/A");
                }

                showAlert(Alert.AlertType.INFORMATION, "Login successful!");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("tenantDashboard.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Tenant Dashboard");
                stage.show();

            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid email or password.");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
