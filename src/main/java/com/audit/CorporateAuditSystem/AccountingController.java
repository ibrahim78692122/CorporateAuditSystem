package com.audit.CorporateAuditSystem;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class AccountingController {

    private final Blockchain blockchain;

    private final AccountingService service;

    public AccountingController() {

        blockchain = new Blockchain();

        // Database ki existing entries blockchain mein load karo
        blockchain.rebuildFromDatabase();

        service = new AccountingService(blockchain);
    }
    // ================================
// ACCOUNTANT DASHBOARD
// ================================

@GetMapping("/accountant/dashboard")
public String accountantDashboard(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    String username =
            (String) session.getAttribute("username");

    model.addAttribute("username", username);
    model.addAttribute("role", role);

    return "accountant-dashboard";
}
    // ================================
    // CREATE ENTRY PAGE
    // ================================

   @GetMapping("/accountant/create-entry")
public String createEntryPage(HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    return "create-entry";
}

    // ================================
    // SAVE ENTRY
    // ================================

 @PostMapping("/accountant/create-entry")
public String createEntry(
        @RequestParam String invoiceNumber,
        @RequestParam String transactionType,
        @RequestParam String debitAccount,
        @RequestParam String creditAccount,
        @RequestParam double amount,
        @RequestParam String description,
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    AccountingEntry entry = new AccountingEntry(
            invoiceNumber,
            transactionType,
            debitAccount,
            creditAccount,
            amount,
            description,
            "accountant"
    );

    boolean created = service.processEntry(entry);

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

    // ================================
    // VIEW ENTRIES
    // ================================

    @GetMapping("/accountant/entries")
public String viewEntriesPage(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    model.addAttribute(
            "entries",
            DatabaseConnection.getAllAccountingEntries()
    );

    return "accounting-entries";
}

// ================================
// UPDATE ENTRY
// ================================

@PostMapping("/accountant/update-entry")
public String updateEntry(
        @RequestParam String invoiceNumber,
        @RequestParam String transactionType,
        @RequestParam String debitAccount,
        @RequestParam String creditAccount,
        @RequestParam double amount,
        @RequestParam String description,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    service.updateEntry(
            invoiceNumber,
            transactionType,
            debitAccount,
            creditAccount,
            amount,
            description
    );

    return "redirect:/accountant/entries";
}
// ================================
// DELETE ENTRY
// ================================

@PostMapping("/accountant/delete-entry")
public String deleteEntry(
        @RequestParam String invoiceNumber,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    service.deleteEntry(invoiceNumber);

    return "redirect:/accountant/entries";
}
    // ================================
    // STATISTICS
    // ================================

   @GetMapping("/accountant/statistics")
public String statisticsPage(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
        return "redirect:/";
    }

    model.addAttribute(
            "total",
            DatabaseConnection.getAccountingEntryCount()
    );

    model.addAttribute(
            "totalAmount",
            DatabaseConnection.getTotalTransactionAmount()
    );

    model.addAttribute(
            "highest",
            DatabaseConnection.getHighestTransactionAmount()
    );

    model.addAttribute(
            "average",
            DatabaseConnection.getAverageTransactionAmount()
    );

    return "statistics";
}

    // ================================
    // BLOCKCHAIN
    // ================================

  @GetMapping("/accountant/blockchain")
public String blockchainPage(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("ACCOUNTANT")) {
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
}