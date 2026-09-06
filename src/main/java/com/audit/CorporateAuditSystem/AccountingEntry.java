package com.audit.CorporateAuditSystem;

import java.time.LocalDateTime;

public class AccountingEntry {

    private String invoiceNumber;
    private String transactionType;
    private String debitAccount;
    private String creditAccount;
    private double amount;
    private String description;
    private String createdBy;
    private LocalDateTime timestamp;

    public AccountingEntry(
            String invoiceNumber,
            String transactionType,
            String debitAccount,
            String creditAccount,
            double amount,
            String description,
            String createdBy) {

        this.invoiceNumber = invoiceNumber;
        this.transactionType = transactionType;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
        this.amount = amount;
        this.description = description;
        this.createdBy = createdBy;
        this.timestamp = LocalDateTime.now();
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
                + ", Debit=" + debitAccount
                + ", Credit=" + creditAccount
                + ", Amount=" + amount
                + ", Description=" + description
                + ", CreatedBy=" + createdBy;
    }
}