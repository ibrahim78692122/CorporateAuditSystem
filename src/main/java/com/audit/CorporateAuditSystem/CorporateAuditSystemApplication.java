package com.audit.CorporateAuditSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CorporateAuditSystemApplication {

    public static void main(String[] args) {

        DatabaseConnection.createTables();
        DatabaseConnection.addProfileImageColumn();
        DatabaseConnection.createDefaultUsers();

        SpringApplication.run(
                CorporateAuditSystemApplication.class,
                args
        );
    }
}