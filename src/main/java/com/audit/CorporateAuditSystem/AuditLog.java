package com.audit.CorporateAuditSystem;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class AuditLog {

    private int blockIndex;
    private String action;
    private String performedBy;
    private String result;
    private String message;
    private LocalDateTime timestamp;

    public AuditLog(
            int blockIndex,
            String action,
            String performedBy,
            String result,
            String message) {

        this.blockIndex = blockIndex;
        this.action = action;
        this.performedBy = performedBy;
        this.result = result;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public String getAction() {
        return action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Console par Audit Log dikhana
    public void printLog() {

        System.out.println();
        System.out.println("=================================");
        System.out.println("          AUDIT LOG");
        System.out.println("=================================");

        System.out.println("Block Index: " + blockIndex);
        System.out.println("Action: " + action);
        System.out.println("Performed By: " + performedBy);
        System.out.println("Result: " + result);
        System.out.println("Message: " + message);
        System.out.println("Timestamp: " + timestamp);

        System.out.println("=================================");
    }

    // File mein Audit Log save karna
    public void saveToFile() {

        try (FileWriter writer =
                     new FileWriter("audit_logs.txt", true)) {

            writer.write(
                    "=================================\n"
            );

            writer.write(
                    "Block Index: " + blockIndex + "\n"
            );

            writer.write(
                    "Action: " + action + "\n"
            );

            writer.write(
                    "Performed By: " + performedBy + "\n"
            );

            writer.write(
                    "Result: " + result + "\n"
            );

            writer.write(
                    "Message: " + message + "\n"
            );

            writer.write(
                    "Timestamp: " + timestamp + "\n"
            );

            writer.write(
                    "=================================\n\n"
            );

            System.out.println(
                    "Audit Log Saved To File ✅"
            );

        } catch (IOException e) {

            System.out.println(
                    "Unable To Save Audit Log ❌"
            );

            System.out.println(e.getMessage());
        }
    }

    // Database mein Audit Log save karna
    public void saveToDatabase() {

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
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setInt(1, blockIndex);
        statement.setString(2, action);
        statement.setString(3, performedBy);
        statement.setString(4, result);
        statement.setString(5, message);
        statement.setObject(6, timestamp);

        statement.executeUpdate();

        System.out.println(
                "Audit Log Saved To Database ✅"
        );

    } catch (Exception e) {

        System.out.println(
                "Audit Log Database Save Failed ❌"
        );

        System.out.println(e.getMessage());
    }
}
}
