package com.audit.CorporateAuditSystem;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Controller
public class LoginController {

    // ================================
    // LOGIN PAGE
    // ================================

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    // ================================
    // LOGIN PROCESS
    // ================================

  @PostMapping("/login")
public String login(
        @RequestParam String username,
        @RequestParam String password,
        Model model,
        HttpSession session) {

    username = username.trim();

    User user = DatabaseConnection.getUserByUsername(username);

    if (user != null && user.getPassword().equals(password)) {

        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        if (user.getRole().equals("ACCOUNTANT")) {
            return "redirect:/accountant/dashboard";
        }

        if (user.getRole().equals("AUDITOR")) {
            return "redirect:/auditor/dashboard";
        }
    }

    model.addAttribute(
            "error",
            "Invalid username or password"
    );

    return "login";
}
    // ================================
    // LOGOUT
    // ================================

    @GetMapping("/do-logout")
public String logout(HttpSession session) {

    session.invalidate();

    return "redirect:/";
}
}