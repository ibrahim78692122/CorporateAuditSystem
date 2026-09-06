package com.audit.CorporateAuditSystem;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {

    private static final String URL = "jdbc:h2:./auditdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    // =========================================
    // DATABASE CONNECTION
    // =========================================

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }

    // =========================================
    // CREATE TABLES
    // =========================================

    public static void createTables() {

        String usersSql = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(100) UNIQUE,
                    password VARCHAR(255),
                    role VARCHAR(50)
                )
                """;

        String accountingSql = """
                CREATE TABLE IF NOT EXISTS accounting_entries (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    invoice_number VARCHAR(50) UNIQUE,
                    transaction_type VARCHAR(50),
                    debit_account VARCHAR(100),
                    credit_account VARCHAR(100),
                    amount DECIMAL(15,2),
                    description VARCHAR(255),
                    created_by VARCHAR(100),
                    timestamp TIMESTAMP
                )
                """;

        String auditSql = """
                CREATE TABLE IF NOT EXISTS audit_logs (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    block_index INT,
                    action VARCHAR(100),
                    performed_by VARCHAR(100),
                    result VARCHAR(50),
                    message VARCHAR(255),
                    timestamp TIMESTAMP
                )
                """;

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {

            statement.execute(usersSql);
            statement.execute(accountingSql);
            statement.execute(auditSql);

            System.out.println(
                    "Database Tables Created Successfully! ✅"
            );

        } catch (SQLException e) {

            System.out.println(
                    "Table Creation Failed ❌"
            );

            System.out.println(e.getMessage());
        }
    }

    // =========================================
    // DEFAULT USERS
    // =========================================

    public static void createDefaultUsers() {

        String sql = """
                MERGE INTO users
                (username, password, role)
                KEY(username)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            // Accountant
            statement.setString(1, "accountant");
            statement.setString(2, "1234");
            statement.setString(3, "ACCOUNTANT");
            statement.executeUpdate();

            // Auditor
            statement.setString(1, "auditor");
            statement.setString(2, "1234");
            statement.setString(3, "AUDITOR");
            statement.executeUpdate();

            System.out.println(
                    "Default Users Created Successfully! ✅"
            );

        } catch (SQLException e) {

            System.out.println(
                    "User Creation Failed ❌"
            );

            System.out.println(e.getMessage());
        }
    }

    // =========================================
    // INSERT ACCOUNTING ENTRY
    // =========================================

    public static boolean insertAccountingEntry(
            String invoiceNumber,
            String transactionType,
            String debitAccount,
            String creditAccount,
            double amount,
            String description,
            String createdBy) {

        String sql = """
                INSERT INTO accounting_entries
                (
                    invoice_number,
                    transaction_type,
                    debit_account,
                    credit_account,
                    amount,
                    description,
                    created_by,
                    timestamp
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, invoiceNumber);
            statement.setString(2, transactionType);
            statement.setString(3, debitAccount);
            statement.setString(4, creditAccount);
            statement.setDouble(5, amount);
            statement.setString(6, description);
            statement.setString(7, createdBy);

            statement.executeUpdate();

            System.out.println(
                    "Accounting Entry Saved Successfully! ✅"
            );

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Insert Failed ❌"
            );

            System.out.println(e.getMessage());

            return false;
        }
    }

    // =========================================
    // GET ALL ACCOUNTING ENTRIES
    // =========================================

    public static ArrayList<AccountingEntry>
    getAllAccountingEntries() {

        ArrayList<AccountingEntry> entries =
                new ArrayList<>();

        String sql =
                "SELECT * FROM accounting_entries ORDER BY id ASC";

        try (
                Connection connection = getConnection();
                Statement statement =
                        connection.createStatement();
                ResultSet resultSet =
                        statement.executeQuery(sql)
        ) {

            while (resultSet.next()) {

                AccountingEntry entry =
                        new AccountingEntry(

                                resultSet.getString(
                                        "invoice_number"),

                                resultSet.getString(
                                        "transaction_type"),

                                resultSet.getString(
                                        "debit_account"),

                                resultSet.getString(
                                        "credit_account"),

                                resultSet.getDouble(
                                        "amount"),

                                resultSet.getString(
                                        "description"),

                                resultSet.getString(
                                        "created_by")
                        );

                entries.add(entry);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Unable To Load Accounting Entries ❌"
            );

            System.out.println(e.getMessage());
        }

        return entries;
    }

    // =========================================
    // UPDATE ACCOUNTING ENTRY
    // =========================================

  // =========================================
// UPDATE ACCOUNTING ENTRY
// =========================================

public static boolean updateAccountingEntry(
        String invoiceNumber,
        String newTransactionType,
        String newDebitAccount,
        String newCreditAccount,
        double newAmount,
        String newDescription) {

    String sql = """
            UPDATE accounting_entries
            SET transaction_type = ?,
                debit_account = ?,
                credit_account = ?,
                amount = ?,
                description = ?
            WHERE invoice_number = ?
            """;

    try (
            Connection connection = getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setString(1, newTransactionType);
        statement.setString(2, newDebitAccount);
        statement.setString(3, newCreditAccount);
        statement.setDouble(4, newAmount);
        statement.setString(5, newDescription);
        statement.setString(6, invoiceNumber);

        int rows = statement.executeUpdate();

        if (rows > 0) {

            System.out.println(
                    "Accounting Entry Updated Successfully! ✅"
            );

            return true;
        }

        System.out.println(
                "Invoice Not Found ❌"
        );

        return false;

    } catch (SQLException e) {

        System.out.println(
                "Update Failed ❌"
        );

        System.out.println(e.getMessage());

        return false;
    }
}
    // =========================================
    // DELETE ACCOUNTING ENTRY
    // =========================================

    public static boolean deleteAccountingEntry(
            String invoiceNumber) {

        String sql =
                "DELETE FROM accounting_entries " +
                "WHERE invoice_number = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, invoiceNumber);

            int rows = statement.executeUpdate();

            if (rows > 0) {

                System.out.println(
                        "Accounting Entry Deleted Successfully! ✅"
                );

                return true;
            }

            System.out.println(
                    "Invoice Not Found ❌"
            );

            return false;

        } catch (SQLException e) {

            System.out.println(
                    "Delete Failed ❌"
            );

            System.out.println(e.getMessage());

            return false;
        }
    }

    // =========================================
    // SEARCH ACCOUNTING ENTRY
    // =========================================

    public static AccountingEntry
    searchAccountingEntry(String invoiceNumber) {

        String sql =
                "SELECT * FROM accounting_entries " +
                "WHERE invoice_number = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, invoiceNumber);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return new AccountingEntry(

                        resultSet.getString(
                                "invoice_number"),

                        resultSet.getString(
                                "transaction_type"),

                        resultSet.getString(
                                "debit_account"),

                        resultSet.getString(
                                "credit_account"),

                        resultSet.getDouble(
                                "amount"),

                        resultSet.getString(
                                "description"),

                        resultSet.getString(
                                "created_by")
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Search Failed ❌"
            );

            System.out.println(e.getMessage());
        }

        return null;
    }

    // =========================================
    // SAVE AUDIT LOG
    // =========================================

    public static void saveAuditLog(
            int blockIndex,
            String action,
            String performedBy,
            String result,
            String message,
            java.time.LocalDateTime timestamp) {

        String sql = """
                INSERT INTO audit_logs
                (
                    block_index,
                    action,
                    performed_by,
                    result,
                    message,
                    timestamp
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, blockIndex);
            statement.setString(2, action);
            statement.setString(3, performedBy);
            statement.setString(4, result);
            statement.setString(5, message);

            statement.setTimestamp(
                    6,
                    Timestamp.valueOf(timestamp)
            );

            statement.executeUpdate();

            System.out.println(
                    "Audit Log Saved To Database! ✅"
            );

        } catch (SQLException e) {

            System.out.println(
                    "Unable To Save Audit Log ❌"
            );

            System.out.println(e.getMessage());
        }
    }

    // =========================================
    // SHOW ACCOUNTING ENTRIES
    // =========================================

    public static void showAccountingEntries() {

        ArrayList<AccountingEntry> entries =
                getAllAccountingEntries();

        System.out.println();
        System.out.println(
                "===== ACCOUNTING ENTRIES ====="
        );

        for (AccountingEntry entry : entries) {

            System.out.println(
                    "----------------------------"
            );

            System.out.println(
                    "Invoice: "
                            + entry.getInvoiceNumber()
            );

            System.out.println(
                    "Type: "
                            + entry.getTransactionType()
            );

            System.out.println(
                    "Debit: "
                            + entry.getDebitAccount()
            );

            System.out.println(
                    "Credit: "
                            + entry.getCreditAccount()
            );

            System.out.println(
                    "Amount: "
                            + entry.getAmount()
            );

            System.out.println(
                    "Description: "
                            + entry.getDescription()
            );

            System.out.println(
                    "Created By: "
                            + entry.getCreatedBy()
            );
        }
    }

    // =========================================
    // SHOW AUDIT LOGS
    // =========================================
public static ArrayList<AuditLog> getAllAuditLogs() {

    ArrayList<AuditLog> logs = new ArrayList<>();

    String sql =
            "SELECT * FROM audit_logs ORDER BY id DESC";

    try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
    ) {

        while (resultSet.next()) {

            AuditLog log = new AuditLog(
                    resultSet.getInt("block_index"),
                    resultSet.getString("action"),
                    resultSet.getString("performed_by"),
                    resultSet.getString("result"),
                    resultSet.getString("message")
            );

            logs.add(log);
        }

    } catch (SQLException e) {

        System.out.println(
                "Unable To Load Audit Logs ❌"
        );

        System.out.println(e.getMessage());
    }

    return logs;
}
    public static void showAuditLogsFromDatabase() {

        String sql =
                "SELECT * FROM audit_logs " +
                "ORDER BY id DESC";

        System.out.println();
        System.out.println(
                "================================="
        );
        System.out.println(
                "       DATABASE AUDIT LOGS"
        );
        System.out.println(
                "================================="
        );

        try (
                Connection connection = getConnection();
                Statement statement =
                        connection.createStatement();
                ResultSet resultSet =
                        statement.executeQuery(sql)
        ) {

            boolean found = false;

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        "-------------------------------"
                );

                System.out.println(
                        "ID: "
                                + resultSet.getLong("id")
                );

                System.out.println(
                        "Action: "
                                + resultSet.getString("action")
                );

                System.out.println(
                        "Performed By: "
                                + resultSet.getString(
                                        "performed_by")
                );

                System.out.println(
                        "Result: "
                                + resultSet.getString("result")
                );

                System.out.println(
                        "Message: "
                                + resultSet.getString("message")
                );

                System.out.println(
                        "Timestamp: "
                                + resultSet.getTimestamp(
                                        "timestamp")
                );
            }

            if (!found) {
                System.out.println(
                        "No Audit Logs Found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Unable To Read Audit Logs ❌"
            );

            System.out.println(e.getMessage());
        }
    }
    // =========================================
// ACCOUNTING STATISTICS
// =========================================

public static int getAccountingEntryCount() {

    String sql = "SELECT COUNT(*) FROM accounting_entries";

    try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
    ) {

        if (resultSet.next()) {
            return resultSet.getInt(1);
        }

    } catch (SQLException e) {

        System.out.println("Unable To Get Transaction Count ❌");
        System.out.println(e.getMessage());
    }

    return 0;
}


// =========================================
// TOTAL TRANSACTION AMOUNT
// =========================================

public static double getTotalTransactionAmount() {

    String sql =
            "SELECT COALESCE(SUM(amount), 0) FROM accounting_entries";

    try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
    ) {

        if (resultSet.next()) {
            return resultSet.getDouble(1);
        }

    } catch (SQLException e) {

        System.out.println("Unable To Get Total Amount ❌");
        System.out.println(e.getMessage());
    }

    return 0;
}


// =========================================
// HIGHEST TRANSACTION AMOUNT
// =========================================

public static double getHighestTransactionAmount() {

    String sql =
            "SELECT COALESCE(MAX(amount), 0) FROM accounting_entries";

    try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
    ) {

        if (resultSet.next()) {
            return resultSet.getDouble(1);
        }

    } catch (SQLException e) {

        System.out.println("Unable To Get Highest Amount ❌");
        System.out.println(e.getMessage());
    }

    return 0;
}


// =========================================
// AVERAGE TRANSACTION AMOUNT
// =========================================

public static double getAverageTransactionAmount() {

    String sql =
            "SELECT COALESCE(AVG(amount), 0) FROM accounting_entries";

    try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
    ) {

        if (resultSet.next()) {
            return resultSet.getDouble(1);
        }

    } catch (SQLException e) {

        System.out.println("Unable To Get Average Amount ❌");
        System.out.println(e.getMessage());
    }

    return 0;
}
}
