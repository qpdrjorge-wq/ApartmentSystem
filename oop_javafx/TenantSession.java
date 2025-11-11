package com.example.oop_javafx;

import java.time.LocalDate;

public class TenantSession {
    private static TenantSession instance;

    private String email;
    private String username;
    private int tenantAccountId;
    private int unitId;

    private LocalDate leaseStartDate;
    private LocalDate leaseEndDate;
    private double rentPrice;

    private String billingPeriod;
    private String paymentStatus;

    private TenantSession() {}

    public static TenantSession getInstance() {
        if (instance == null) {
            instance = new TenantSession();
        }
        return instance;
    }

    public void setTenantData(int tenantAccountId, String username, String email, int unitId) {
        this.tenantAccountId = tenantAccountId;
        this.username = username;
        this.email = email;
        this.unitId = unitId;
    }

    public void setLeaseData(LocalDate startDate, LocalDate endDate, double price) {
        this.leaseStartDate = startDate;
        this.leaseEndDate = endDate;
        this.rentPrice = price;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // Getters
    public int getTenantId() {
        return tenantAccountId;
    }

    public int getTenantAccountId() {
        return tenantAccountId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getUnitId() {
        return unitId;
    }

    public LocalDate getLeaseStartDate() {
        return leaseStartDate;
    }

    public LocalDate getLeaseEndDate() {
        return leaseEndDate;
    }

    public double getRentPrice() {
        return rentPrice;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public boolean isLoggedIn() {
        return tenantAccountId > 0 && username != null && !username.isEmpty();
    }

    public void clearSession() {
        instance = null;
    }
}
