package com.audit.CorporateAuditSystem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountingEntry {

    private String invoiceNumber;
    private String transactionType;
    private String debitAccount;
    private String creditAccount;

    private double amount;
    private double paidAmount;
    private double remainingAmount;

    private String customerSupplierName;
    private LocalDate dueDate;
    private String paymentStatus;

    private String description;
    private String createdBy;
    private LocalDateTime timestamp;

    public AccountingEntry(
            String invoiceNumber,
            String transactionType,
            String debitAccount,
            String creditAccount,
            double amount,
            double paidAmount,
            String customerSupplierName,
            LocalDate dueDate,
            String description,
            String createdBy) {

        this.invoiceNumber = invoiceNumber;
        this.transactionType = transactionType;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;

        this.amount = amount;
        this.paidAmount = paidAmount;

        this.remainingAmount = amount - paidAmount;

        this.customerSupplierName = customerSupplierName;
        this.dueDate = dueDate;

        this.description = description;
        this.createdBy = createdBy;

        this.timestamp = LocalDateTime.now();

        calculatePaymentStatus();
    }

    private void calculatePaymentStatus() {

        if (remainingAmount <= 0) {
            remainingAmount = 0;
            paymentStatus = "PAID";

        } else if (paidAmount > 0) {
            paymentStatus = "PARTIAL";

        } else {
            paymentStatus = "PENDING";
        }
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getDebitAccount() {
        return debitAccount;
    }

    public String getCreditAccount() {
        return creditAccount;
    }

    public double getAmount() {
        return amount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public String getCustomerSupplierName() {
        return customerSupplierName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {

        return "Invoice=" + invoiceNumber
                + ", Type=" + transactionType
                + ", Customer/Supplier=" + customerSupplierName
                + ", Amount=" + amount
                + ", Paid=" + paidAmount
                + ", Remaining=" + remainingAmount
                + ", Status=" + paymentStatus
                + ", Description=" + description
                + ", CreatedBy=" + createdBy;
    }
}