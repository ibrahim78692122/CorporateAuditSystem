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
public class RegisterController {

    // ================================
    // REGISTER PAGE
    // ================================

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // ================================
    // REGISTER PROCESS
    // ================================

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String role,
            Model model,
            HttpSession session) {

        username = username.trim();

        // Username check
        if (username.isEmpty()) {
            model.addAttribute(
                    "error",
                    "Username cannot be empty."
            );
            return "register";
        }

        // Password check
        if (password.isEmpty()) {
            model.addAttribute(
                    "error",
                    "Password cannot be empty."
            );
            return "register";
        }

        // Confirm password
        if (!password.equals(confirmPassword)) {
            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );
            return "register";
        }

        // Role validation
        if (!role.equals("ACCOUNTANT")
                && !role.equals("AUDITOR")) {

            model.addAttribute(
                    "error",
                    "Invalid account role."
            );

            return "register";
        }

        // Check username already exists
        String checkSql = """
                SELECT username
                FROM users
                WHERE username = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement checkStatement =
                        connection.prepareStatement(checkSql)
        ) {

            checkStatement.setString(1, username);

            try (
                    ResultSet resultSet =
                            checkStatement.executeQuery()
            ) {

                if (resultSet.next()) {

                    model.addAttribute(
                            "error",
                            "Username already exists."
                    );

                    return "register";
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Unable to check username."
            );

            return "register";
        }

        // Create new account
        String insertSql = """
                INSERT INTO users
                (username, password, role)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(insertSql)
        ) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);

            int rows = statement.executeUpdate();

            System.out.println(
                    "New User Created Rows: " + rows
            );

            if (rows > 0) {

                model.addAttribute(
                        "success",
                        "Account created successfully! Please login."
                );

                return "login";
            }

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Unable to create account."
            );

            return "register";
        }

        model.addAttribute(
                "error",
                "Account creation failed."
        );

        return "register";
    }
}
