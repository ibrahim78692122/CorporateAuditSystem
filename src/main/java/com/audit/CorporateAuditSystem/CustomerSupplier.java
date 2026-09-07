package com.audit.CorporateAuditSystem;

public class CustomerSupplier {

    private long id;
    private String name;
    private String phone;
    private String address;
    private String profileImage;

    private int totalTransactions;
    private double totalPaid;
    private double totalOutstanding;
    private double overdueAmount;

    public CustomerSupplier() {
    }

    public CustomerSupplier(
            long id,
            String name,
            String phone,
            String address,
            String profileImage) {

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.profileImage = profileImage;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public double getTotalOutstanding() {
        return totalOutstanding;
    }

    public double getOverdueAmount() {
        return overdueAmount;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public void setTotalOutstanding(double totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public void setOverdueAmount(double overdueAmount) {
        this.overdueAmount = overdueAmount;
    }
}