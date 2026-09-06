package com.audit.CorporateAuditSystem;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Database initialize
        DatabaseConnection.createTables();
        DatabaseConnection.createDefaultUsers();

        // Blockchain initialize
        Blockchain blockchain = new Blockchain();

        // Database entries blockchain mein load
        blockchain.rebuildFromDatabase();

        // Accounting service
        AccountingService service =
                new AccountingService(blockchain);

        System.out.println();
        System.out.println("=================================");
        System.out.println("     CORPORATE AUDIT SYSTEM");
        System.out.println("=================================");

        while (true) {

            System.out.println();
            System.out.println("===== LOGIN =====");

            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user =
                    UserService.login(username, password);

            if (user == null) {

                System.out.println(
                        "Invalid Username or Password ❌"
                );

                continue;
            }

            System.out.println();
            System.out.println(
                    "Login Successful ✅"
            );

            System.out.println(
                    "Welcome: " + user.getUsername()
            );

            System.out.println(
                    "Role: " + user.getRole()
            );

            // =====================================
            // ACCOUNTANT
            // =====================================

            if (user.getRole().equals("ACCOUNTANT")) {

                accountantDashboard(
                        scanner,
                        blockchain,
                        service,
                        user
                );
            }

            // =====================================
            // AUDITOR
            // =====================================

            else if (user.getRole().equals("AUDITOR")) {

                auditorDashboard(
                        scanner,
                        blockchain,
                        user
                );
            }

            else {

                System.out.println(
                        "Unknown Role ❌"
                );
            }
        }
    }

    // =====================================================
    // ACCOUNTANT DASHBOARD
    // =====================================================

    public static void accountantDashboard(
            Scanner scanner,
            Blockchain blockchain,
            AccountingService service,
            User user) {

        while (true) {

            System.out.println();
            System.out.println(
                    "===== ACCOUNTANT DASHBOARD ====="
            );

            System.out.println(
                    "1. Add Accounting Entry"
            );

            System.out.println(
                    "2. View Accounting Entries"
            );

            System.out.println(
                    "3. View Blockchain"
            );

            System.out.println(
                    "4. Update Accounting Entry"
            );

            System.out.println(
                    "5. Delete Accounting Entry"
            );

            System.out.println(
                    "6. Search Accounting Entry"
            );

            System.out.println(
                    "7. Logout"
            );

            System.out.println(
                    "8. Transaction Statistics"
            );

            System.out.print(
                    "Choose option: "
            );

            int choice = scanner.nextInt();

            // =====================================
            // OPTION 1 - ADD
            // =====================================

            if (choice == 1) {

                scanner.nextLine();

                System.out.print(
                        "Invoice Number: "
                );

                String invoiceNumber =
                        scanner.nextLine();

                System.out.print(
                        "Transaction Type: "
                );

                String transactionType =
                        scanner.nextLine();

                System.out.print(
                        "Debit Account: "
                );

                String debitAccount =
                        scanner.nextLine();

                System.out.print(
                        "Credit Account: "
                );

                String creditAccount =
                        scanner.nextLine();

                System.out.print(
                        "Amount: "
                );

                double amount =
                        scanner.nextDouble();

                scanner.nextLine();

                System.out.print(
                        "Description: "
                );

                String description =
                        scanner.nextLine();

                AccountingEntry entry =
                        new AccountingEntry(
                                invoiceNumber,
                                transactionType,
                                debitAccount,
                                creditAccount,
                                amount,
                                description,
                                user.getUsername()
                        );

                boolean created =
                        service.processEntry(entry);

                System.out.println(
                        created
                                ? "Entry Created Successfully ✅"
                                : "Entry Creation Failed ❌"
                );
            }

            // =====================================
            // OPTION 2 - VIEW
            // =====================================

            else if (choice == 2) {

                DatabaseConnection
                        .showAccountingEntries();
            }

            // =====================================
            // OPTION 3 - BLOCKCHAIN
            // =====================================

            else if (choice == 3) {

                blockchain.printBlockchain();
            }

            // =====================================
            // OPTION 4 - UPDATE
            // =====================================

            else if (choice == 4) {

                scanner.nextLine();

                System.out.print(
                        "Enter Invoice Number: "
                );

                String invoiceNumber =
                        scanner.nextLine();

                System.out.print(
                        "Enter New Transaction Type: "
                );

                String transactionType =
                        scanner.nextLine();

                System.out.print(
                        "Enter New Debit Account: "
                );

                String debitAccount =
                        scanner.nextLine();

                System.out.print(
                        "Enter New Credit Account: "
                );

                String creditAccount =
                        scanner.nextLine();

                System.out.print(
                        "Enter New Amount: "
                );

                double amount =
                        scanner.nextDouble();

                scanner.nextLine();

                System.out.print(
                        "Enter New Description: "
                );

                String description =
                        scanner.nextLine();

                boolean updated =
                        service.updateEntry(
                                invoiceNumber,
                                transactionType,
                                debitAccount,
                                creditAccount,
                                amount,
                                description
                        );

                System.out.println(
                        updated
                                ? "Entry Updated Successfully ✅"
                                : "Entry Update Failed ❌"
                );
            }

            // =====================================
            // OPTION 5 - DELETE
            // =====================================

            else if (choice == 5) {

                scanner.nextLine();

                System.out.print(
                        "Enter Invoice Number: "
                );

                String invoiceNumber =
                        scanner.nextLine();

                boolean deleted =
                        service.deleteEntry(
                                invoiceNumber
                        );

                System.out.println(
                        deleted
                                ? "Entry Deleted Successfully ✅"
                                : "Entry Delete Failed ❌"
                );
            }

            // =====================================
            // OPTION 6 - SEARCH
            // =====================================

            else if (choice == 6) {

                scanner.nextLine();

                System.out.print(
                        "Enter Invoice Number: "
                );

                String invoiceNumber =
                        scanner.nextLine();

                AccountingEntry entry =
                        DatabaseConnection
                                .searchAccountingEntry(
                                        invoiceNumber
                                );

                if (entry != null) {

                    System.out.println();
                    System.out.println(
                            "===== ENTRY FOUND ====="
                    );

                    System.out.println(
                            entry
                    );

                } else {

                    System.out.println(
                            "Invoice Not Found ❌"
                    );
                }
            }

            // =====================================
            // OPTION 7 - LOGOUT
            // =====================================

            else if (choice == 7) {

                System.out.println(
                        "Accountant Logged Out Successfully ✅"
                );

                break;
            }

            // =====================================
            // OPTION 8 - STATISTICS
            // =====================================

            else if (choice == 8) {

                int total =
                        DatabaseConnection
                                .getAccountingEntryCount();

                double totalAmount =
                        DatabaseConnection
                                .getTotalTransactionAmount();

                double highest =
                        DatabaseConnection
                                .getHighestTransactionAmount();

                double average =
                        DatabaseConnection
                                .getAverageTransactionAmount();

                System.out.println();
                System.out.println(
                        "===== TRANSACTION STATISTICS ====="
                );

                System.out.println(
                        "Total Transactions: "
                                + total
                );

                System.out.println(
                        "Total Amount: ₹"
                                + totalAmount
                );

                System.out.println(
                        "Highest Transaction: ₹"
                                + highest
                );

                System.out.println(
                        "Average Transaction: ₹"
                                + average
                );
            }

            else {

                System.out.println(
                        "Invalid Option ❌"
                );
            }
        }
    }

    // =====================================================
    // AUDITOR DASHBOARD
    // =====================================================

    public static void auditorDashboard(
            Scanner scanner,
            Blockchain blockchain,
            User user) {

        while (true) {

            System.out.println();
            System.out.println(
                    "===== AUDITOR DASHBOARD ====="
            );

            System.out.println(
                    "1. View Blockchain"
            );

            System.out.println(
                    "2. Verify Blockchain"
            );

            System.out.println(
                    "3. View Audit Logs"
            );

            System.out.println(
                    "4. Logout"
            );

            System.out.print(
                    "Choose option: "
            );

            int choice = scanner.nextInt();

            if (choice == 1) {

                blockchain.printBlockchain();
            }

            else if (choice == 2) {

                boolean valid =
                        blockchain.isChainValid();

                System.out.println(
                        valid
                                ? "Blockchain Valid ✅"
                                : "Blockchain Invalid ❌"
                );
            }

            else if (choice == 3) {

                DatabaseConnection
                        .showAuditLogsFromDatabase();
            }

            else if (choice == 4) {

                System.out.println(
                        "Auditor Logged Out Successfully ✅"
                );

                break;
            }

            else {

                System.out.println(
                        "Invalid Option ❌"
                );
            }
        }
    }
}