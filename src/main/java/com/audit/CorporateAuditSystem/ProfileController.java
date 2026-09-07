package com.audit.CorporateAuditSystem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {
    

    // =========================================
    // PROFILE PAGE
    // =========================================

    @GetMapping("/profile")
    public String profile(
            Model model,
            HttpSession session) {

        String username =
                (String) session.getAttribute("username");

        String role =
                (String) session.getAttribute("role");

        if (username == null || role == null) {
            return "redirect:/";
        }

        User user =
                DatabaseConnection.getUserByUsername(username);

        if (user == null) {
            session.invalidate();
            return "redirect:/";
        }

        model.addAttribute("user", user);

        return "profile";
    }


    // =========================================
    // EDIT PROFILE PAGE
    // =========================================

    @GetMapping("/profile/edit")
    public String editProfile(
            Model model,
            HttpSession session) {

        String username =
                (String) session.getAttribute("username");

        String role =
                (String) session.getAttribute("role");

        if (username == null || role == null) {
            return "redirect:/";
        }

        User user =
                DatabaseConnection.getUserByUsername(username);

        if (user == null) {
            session.invalidate();
            return "redirect:/";
        }

        model.addAttribute("user", user);

        return "edit-profile";
    }


    // =========================================
    // UPDATE PROFILE
    // =========================================

    @PostMapping("/profile/update")
    public String updateProfile(

            @RequestParam String newUsername,

            @RequestParam String newPassword,

            @RequestParam String confirmPassword,

            Model model,

            HttpSession session) {

        String currentUsername =
                (String) session.getAttribute("username");

        String role =
                (String) session.getAttribute("role");

        if (currentUsername == null || role == null) {
            return "redirect:/";
        }


        // Remove extra spaces
        newUsername = newUsername.trim();


        // =====================================
        // USERNAME CHECK
        // =====================================

        if (newUsername.isEmpty()) {

            model.addAttribute(
                    "error",
                    "Username cannot be empty."
            );

            return "edit-profile";
        }


        // =====================================
        // PASSWORD CHECK
        // =====================================

        if (!newPassword.isEmpty()) {

            if (!newPassword.equals(confirmPassword)) {

                model.addAttribute(
                        "error",
                        "Passwords do not match."
                );

                return "edit-profile";
            }
        }


        // =====================================
        // CHECK USERNAME ALREADY EXISTS
        // =====================================

        if (!currentUsername.equals(newUsername)) {

            User existingUser =
                    DatabaseConnection
                            .getUserByUsername(newUsername);

            if (existingUser != null) {

                model.addAttribute(
                        "error",
                        "Username already exists."
                );

                return "edit-profile";
            }
        }


        // =====================================
        // UPDATE USERNAME
        // =====================================

        boolean usernameUpdated = true;

        if (!currentUsername.equals(newUsername)) {

            usernameUpdated =
                    DatabaseConnection.updateUsername(
                            currentUsername,
                            newUsername
                    );
        }


        if (!usernameUpdated) {

            model.addAttribute(
                    "error",
                    "Unable to update username."
            );

            return "edit-profile";
        }


        // =====================================
        // UPDATE PASSWORD
        // =====================================

        if (!newPassword.isEmpty()) {

            String usernameForPassword =
                    newUsername;

            boolean passwordUpdated =
                    DatabaseConnection.updatePassword(
                            usernameForPassword,
                            newPassword
                    );

            if (!passwordUpdated) {

                model.addAttribute(
                        "error",
                        "Unable to update password."
                );

                return "edit-profile";
            }
        }


        // =====================================
        // UPDATE SESSION
        // =====================================

        session.setAttribute(
                "username",
                newUsername
        );

        session.setAttribute(
                "role",
                role
        );


        // =====================================
        // SUCCESS MESSAGE
        // =====================================

        model.addAttribute(
                "success",
                "Profile updated successfully! ✅"
        );

        User updatedUser =
                DatabaseConnection
                        .getUserByUsername(newUsername);

        model.addAttribute(
                "user",
                updatedUser
        );

        return "profile";
    }

    @PostMapping("/profile/upload-image")
public String uploadProfileImage(
        @RequestParam("profileImage") MultipartFile file,
        HttpSession session) {

    String username =
            (String) session.getAttribute("username");

    String role =
            (String) session.getAttribute("role");

    if (username == null || role == null) {
        return "redirect:/";
    }

    if (file == null || file.isEmpty()) {
        return "redirect:/profile/edit";
    }

    try {

        String originalName = file.getOriginalFilename();

        String extension = "";

        if (originalName != null &&
                originalName.contains(".")) {

            extension = originalName.substring(
                    originalName.lastIndexOf(".")
            );
        }

        String fileName =
                UUID.randomUUID() + extension;

        Path uploadDirectory =
                Paths.get("uploads/profile");

        Files.createDirectories(uploadDirectory);

        Path filePath =
                uploadDirectory.resolve(fileName);

        Files.write(
                filePath,
                file.getBytes()
        );

        String imageUrl =
                "/uploads/profile/" + fileName;

        boolean updated =
                DatabaseConnection.updateProfileImage(
                        username,
                        imageUrl
                );

        if (!updated) {
            return "redirect:/profile/edit";
        }

        return "redirect:/profile";

    } catch (IOException e) {

        e.printStackTrace();

        return "redirect:/profile/edit";
    }
}
}