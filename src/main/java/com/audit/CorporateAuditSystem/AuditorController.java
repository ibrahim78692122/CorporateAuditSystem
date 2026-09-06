package com.audit.CorporateAuditSystem;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuditorController {

    private final Blockchain blockchain;

    public AuditorController() {

        blockchain = new Blockchain();

        // Database se saari accounting entries load karo
        blockchain.rebuildFromDatabase();
    }

    // ================================
    // AUDITOR DASHBOARD
    // ================================

    @GetMapping("/auditor/dashboard")
public String auditorDashboard(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("AUDITOR")) {
        return "redirect:/";
    }

    String username =
            (String) session.getAttribute("username");

    model.addAttribute("username", username);
    model.addAttribute("role", role);

    return "auditor-dashboard";
}

    // ================================
    // VIEW BLOCKCHAIN
    // ================================

   @GetMapping("/auditor/blockchain")
public String viewBlockchain(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("AUDITOR")) {
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
    // ================================
    // VERIFY BLOCKCHAIN
    // ================================

  @GetMapping("/auditor/verify")
public String verifyBlockchain(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("AUDITOR")) {
        return "redirect:/";
    }

    blockchain.rebuildFromDatabase();

    boolean valid =
            blockchain.isChainValid();

    model.addAttribute(
            "blockchainValid",
            valid
    );

    model.addAttribute(
            "blocks",
            blockchain.getChain()
    );

    return "blockchain";
}

// ================================
// VIEW ACCOUNTING ENTRIES
// ================================

@GetMapping("/auditor/entries")
public String viewAccountingEntries(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("AUDITOR")) {
        return "redirect:/";
    }

    model.addAttribute(
            "entries",
            DatabaseConnection.getAllAccountingEntries()
    );

    return "accounting-entries";
}
// ================================
// VIEW AUDIT LOGS
// ================================

@GetMapping("/auditor/audit-logs")
public String viewAuditLogs(
        Model model,
        HttpSession session) {

    String role =
            (String) session.getAttribute("role");

    if (role == null || !role.equals("AUDITOR")) {
        return "redirect:/";
    }

    model.addAttribute(
            "auditLogs",
            DatabaseConnection.getAllAuditLogs()
    );

    return "audit-logs";
}
}