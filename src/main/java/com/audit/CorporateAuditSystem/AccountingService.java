package com.audit.CorporateAuditSystem;

public class AccountingService {

    private Blockchain blockchain;

    public AccountingService(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    // =========================================
    // CREATE
    // =========================================

   public boolean processEntry(AccountingEntry entry) {

    boolean saved =
            DatabaseConnection.insertAccountingEntry(
                    entry.getInvoiceNumber(),
                    entry.getTransactionType(),
                    entry.getDebitAccount(),
                    entry.getCreditAccount(),
                    entry.getAmount(),
                    entry.getDescription(),
                    entry.getCreatedBy()
            );

    if (saved) {

        blockchain.rebuildFromDatabase();

        AuditLog log = new AuditLog(
                blockchain.getLatestBlock().getIndex(),
                "CREATE ENTRY",
                entry.getCreatedBy(),
                "SUCCESS",
                "Accounting entry created: "
                        + entry.getInvoiceNumber()
        );

        log.saveToDatabase();
        log.saveToFile();

        return true;

    } else {

        AuditLog log = new AuditLog(
                -1,
                "CREATE ENTRY",
                entry.getCreatedBy(),
                "FAILED",
                "Unable to create accounting entry: "
                        + entry.getInvoiceNumber()
        );

        log.saveToDatabase();
        log.saveToFile();

        return false;
    }
}
    // =========================================
    // UPDATE
    // =========================================

 public boolean updateEntry(
        String invoiceNumber,
        String newTransactionType,
        String newDebitAccount,
        String newCreditAccount,
        double newAmount,
        String newDescription) {

    boolean updated =
            DatabaseConnection.updateAccountingEntry(
                    invoiceNumber,
                    newTransactionType,
                    newDebitAccount,
                    newCreditAccount,
                    newAmount,
                    newDescription
            );

    if (updated) {
        blockchain.rebuildFromDatabase();

        AuditLog log = new AuditLog(
                blockchain.getLatestBlock().getIndex(),
                "UPDATE ENTRY",
                "accountant",
                "SUCCESS",
                "Accounting entry updated: " + invoiceNumber
        );

        log.saveToDatabase();
        log.saveToFile();

        return true;
    }

    AuditLog log = new AuditLog(
            -1,
            "UPDATE ENTRY",
            "accountant",
            "FAILED",
            "Invoice not found: " + invoiceNumber
    );

    log.saveToDatabase();
    log.saveToFile();

    return false;
}
    // =========================================
    // DELETE
    // =========================================

   public boolean deleteEntry(String invoiceNumber) {

    boolean deleted =
            DatabaseConnection.deleteAccountingEntry(
                    invoiceNumber
            );

    if (deleted) {

        blockchain.rebuildFromDatabase();

        System.out.println(
                "Blockchain Synchronized After Delete ✅"
        );

        AuditLog log = new AuditLog(
                blockchain.getLatestBlock().getIndex(),
                "DELETE ENTRY",
                "accountant",
                "SUCCESS",
                "Accounting entry deleted: "
                        + invoiceNumber
        );

        log.saveToDatabase();
        log.saveToFile();

        return true;
    }

    AuditLog log = new AuditLog(
            -1,
            "DELETE ENTRY",
            "accountant",
            "FAILED",
            "Invoice not found: "
                    + invoiceNumber
    );

    log.saveToDatabase();
    log.saveToFile();

    return false;
}
}