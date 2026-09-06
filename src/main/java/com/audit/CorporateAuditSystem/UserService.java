package com.audit.CorporateAuditSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserService {

    // =========================================
    // LOGIN
    // =========================================

    public static User login(String username, String password) {

        String sql =
                "SELECT username, password, role " +
                "FROM users " +
                "WHERE username = ? AND password = ?";

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

                return new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Login Error ❌"
            );

            System.out.println(
                    e.getMessage()
            );
        }

        return null;
    }
}
