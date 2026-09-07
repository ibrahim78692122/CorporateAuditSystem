package com.audit.CorporateAuditSystem;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;

@Controller
public class AccountingController {

    private final Blockchain blockchain;
    private final AccountingService service;

    public AccountingController() {

        blockchain = new Blockchain();
        blockchain.rebuildFromDatabase();

        service = new AccountingService(blockchain);
    }


    // ==============================
    // ACCOUNTANT DASHBOARD
    // ==============================

    @GetMapping("/accountant/dashboard")
    public String accountantDashboard(
            Model model,
            HttpSession session) {

        String role =
                (String) session.getAttribute("role");

        if (role == null ||
                !role.equals("ACCOUNTANT")) {

            return "redirect:/";
        }

        String username =
                (String) session.getAttribute("username");

        model.addAttribute("username", username);
        model.addAttribute("role", role);

        return "accountant-dashboard";
    }


    // ==============================
    // CREATE ENTRY PAGE
    // ==============================

    @GetMapping("/accountant/create-entry")
    public String createEntryPage(
            HttpSession session) {

        String role =
                (String) session.getAttribute("role");

        if (role == null ||
                !role.equals("ACCOUNTANT")) {

            return "redirect:/";
        }

        return "create-entry";
    }


    // ==============================
    // CREATE ENTRY
    // ==============================

    @PostMapping("/accountant/create-entry")
    public String createEntry(

            @RequestParam String invoiceNumber,

            @RequestParam String transactionType,

            @RequestParam String debitAccount,

            @RequestParam String creditAccount,

            @RequestParam double amount,

            @RequestParam double paidAmount,

            @RequestParam String customerSupplierName,

            @RequestParam(required = false)
            String dueDate,

            @RequestParam String description,

            Model model,

            HttpSession session) {


        // Check Accountant Role

        String role =
                (String) session.getAttribute("role");

        if (role == null ||
                !role.equals("ACCOUNTANT")) {

            return "redirect:/";
        }


        // Validate Paid Amount

        if (paidAmount < 0) {

            model.addAttribute(
                    "message",
                    "Paid amount cannot be negative ❌"
            );

            return "create-entry";
        }


        // Paid amount cannot exceed total amount

        if (paidAmount > amount) {

            model.addAttribute(
                    "message",
                    "Paid amount cannot be greater than total amount ❌"
            );

            return "create-entry";
        }


        // Convert Due Date

        LocalDate parsedDueDate = null;

        if (dueDate != null &&
                !dueDate.isBlank()) {

            try {

                parsedDueDate =
                        LocalDate.parse(dueDate);

            } catch (Exception e) {

                model.addAttribute(
                        "message",
                        "Invalid due date ❌"
                );

                return "create-entry";
            }
        }


        // Create Accounting Entry

        String username =
                (String) session.getAttribute("username");

        AccountingEntry entry =
                new AccountingEntry(

                        invoiceNumber,

                        transactionType,

                        debitAccount,

                        creditAccount,

                        amount,

                        paidAmount,

                        customerSupplierName,

                        parsedDueDate,

                        description,

                        username
                );


        // Save Entry

        boolean created =
                service.processEntry(entry);


        if (created) {

            return "redirect:/accountant/entries";

        } else {

            model.addAttribute(
                    "message",
                    "Entry already exists or could not be created ❌"
            );

            return "create-entry";
        }
    }


    // ==============================
    // VIEW ACCOUNTING ENTRIES
    // ==============================

    @GetMapping("/accountant/entries")
    public String viewEntriesPage(
            Model model,
            HttpSession session) {

        String role =
                (String) session.getAttribute("role");

        if (role == null ||
                !role.equals("ACCOUNTANT")) {

            return "redirect:/";
        }

        model.addAttribute(
                "entries",
                DatabaseConnection
                        .getAllAccountingEntries()
        );

        return "accounting-entries";
    }


    // ==============================
    // UPDATE ENTRY
    // ==============================

    @PostMapping("/accountant/update-entry")
    public String updateEntry(

            @RequestParam String invoiceNumber,

            @RequestParam String transactionType,

            @RequestParam String debitAccount,

            @RequestParam String creditAccount,

            @RequestParam double amount,

            @RequestParam double paidAmount,

            @RequestParam String customerSupplierName,

            @RequestParam(required = false)
            String dueDate,

            @RequestParam String description,

            HttpSession session) {


        String role =
                (String) session.getAttribute("role");

        if (role == null ||
                !role.equals("ACCOUNTANT")) {

            return "redirect:/";
        }


        if (paidAmount < 0 ||
                paidAmount > amount) {

            return "redirect:/accountant/entries";
        }


        LocalDate parsedDueDate = null;

        if (dueDate != null &&
                !dueDate.isBlank()) {

            try {

                parsedDueDate =
                        LocalDate.parse(dueDate);

            } catch (Exception e) {

                return "redirect:/accountant/entries";
            }
        }


        service.updateEntry(

                invoiceNumber,

                transactionType,

                debitAccount,

                creditAccount,

                amount,

                paidAmount,

                customerSupplierName,

                parsedDueDate,

                description
        );


        return "redirect:/accountant/entries";
    }


    // ==============================
    // DELETE ENTRY
    // ==============================

    @PostMapping("/accountant/delete-entry")
    public String deleteEntry(

            @RequestParam String invoiceNumber,

            HttpSession session) {

        String role =
                (String) session.getAttribute("role");

        if (role == null ||
                !role.equals("ACCOUNTANT")) {

            return "redirect:/";
        }

        service.deleteEntry(invoiceNumber);

        return "redirect:/accountant/entries";
    }


    // ==============================
    // STATISTICS
    // ==============================

    @GetMapping("/accountant/statistics")
    public String statisticsPage(
            Model model,
            HttpSession session) {

        String role =
                (String) session.getAttribute("role");

        if (role == null ||
                !role.equals("ACCOUNTANT")) {

            return "redirect:/";
        }

        model.addAttribute(
                "total",
                DatabaseConnection
                        .getAccountingEntryCount()
        );

        model.addAttribute(
                "totalAmount",
                DatabaseConnection
                        .getTotalTransactionAmount()
        );

        model.addAttribute(
                "highest",
                DatabaseConnection
                        .getHighestTransactionAmount()
        );

        model.addAttribute(
                "average",
                DatabaseConnection
                        .getAverageTransactionAmount()
        );

        return "statistics";
    }


    // ==============================
    // BLOCKCHAIN
    // ==============================

    @GetMapping("/accountant/blockchain")
    public String blockchainPage(
            Model model,
            HttpSession session) {

        String role =
                (String) session.getAttribute("role");

        if (role == null ||
                !role.equals("ACCOUNTANT")) {

            return "redirect:/";
        }

        blockchain.rebuildFromDatabase();

        model.addAttribute(
                "blockchainValid",
                blockchain.isChainValid()
        );

        model.addAttribute(
                "blocks",
                blockchain.getChain()
        );

        return "blockchain";
    }
    // Outstanding 

   @GetMapping("/accountant/bakaya")
public String bakayaPage(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null ||
            !role.equals("ACCOUNTANT")) {

        return "redirect:/";
    }

    // Total Sale
    model.addAttribute(
            "totalSale",
            DatabaseConnection.getTotalSaleAmount()
    );

    // Total Purchase
    model.addAttribute(
            "totalPurchase",
            DatabaseConnection.getTotalPurchaseAmount()
    );

    // Total Paid
    model.addAttribute(
            "totalPaid",
            DatabaseConnection.getTotalPaidAmount()
    );

    // Total Outstanding
    model.addAttribute(
            "totalBakaya",
            DatabaseConnection.getTotalBakayaAmount()
    );


    // Customer/Supplier Outstanding Entries
    model.addAttribute(
            "outstandingEntries",
            DatabaseConnection.getOutstandingEntries()
    );

   model.addAttribute(
        "overdueEntries",
        DatabaseConnection.getOverdueEntries()
);

 model.addAttribute(
        "overdueAmount",
        DatabaseConnection.getOverdueAmount()
);

    return "bakaya-dashboard";
}

@GetMapping("/accountant/customer-supplier")
public String customerSupplierPage(HttpSession session) {

    String role = (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    return "customer-supplier";
}


@PostMapping("/accountant/customer-supplier")
public String addCustomerSupplier(
        @RequestParam String name,
        @RequestParam(required = false) String phone,
        @RequestParam(required = false) String address,
        @RequestParam(required = false) String profileImage,
        Model model,
        HttpSession session) {

    String role = (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    boolean saved = DatabaseConnection.addCustomerSupplier(
            name,
            phone,
            address,
            profileImage
    );

    if (saved) {
        return "redirect:/accountant/customer-suppliers";
    }

    model.addAttribute(
            "message",
            "Customer/Supplier already exists or could not be saved ❌"
    );

    return "customer-supplier";
}

@GetMapping("/accountant/customer-suppliers")
public String customerSuppliersPage(
        Model model,
        HttpSession session) {

    String role = (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    model.addAttribute(
            "customers",
            DatabaseConnection.getAllCustomerSuppliers()
    );

    return "customer-suppliers";
}

@GetMapping("/accountant/customer-supplier/{id}")
public String customerSupplierProfile(
        @org.springframework.web.bind.annotation.PathVariable long id,
        Model model,
        HttpSession session) {

    String role = (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    CustomerSupplier customer =
            DatabaseConnection.getCustomerSupplierById(id);

    if (customer == null) {
        return "redirect:/accountant/customer-suppliers";
    }

    model.addAttribute("customer", customer);

    return "customer-supplier-profile";
}
}