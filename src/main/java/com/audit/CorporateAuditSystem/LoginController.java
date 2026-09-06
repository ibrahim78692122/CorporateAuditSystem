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

        String sql =
                "SELECT * FROM users WHERE username = ? AND password = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                String role =
                        resultSet.getString("role");

                // Login information session mein save karo
                session.setAttribute("username", username);
                session.setAttribute("role", role);

                // ================================
                // ACCOUNTANT
                // ================================

                if (role.equals("ACCOUNTANT")) {

                    return "redirect:/accountant/dashboard";
                }

                // ================================
                // AUDITOR
                // ================================

                else if (role.equals("AUDITOR")) {

                    return "redirect:/auditor/dashboard";
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Database connection error"
            );

            return "login";
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