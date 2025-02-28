package com.example.cleonoraadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.*;

@SpringBootApplication
public class CleonoraAdminApplication {

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        initializeDatabase();
        SpringApplication.run(CleonoraAdminApplication.class, args);
    }

    private static void initializeDatabase() {
        // First, connect to the template1 database to check for and create the postgres database
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/template1", "postgres", "0000");
             Statement statement = connection.createStatement()) {

            // Check if the "postgres" database exists
            ResultSet resultSet = statement.executeQuery("SELECT 1 FROM pg_database WHERE datname = 'postgres'");
            if (!resultSet.next()) {
                // Create the "postgres" database if it doesn't exist
                statement.executeUpdate("CREATE DATABASE postgres");
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            System.exit(1);
        }

        // Now, connect to the postgres database to create the schema
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "0000");
             Statement statement = connection.createStatement()) {

            // Create the "cleonora_database" schema if it doesn't exist
            statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS cleonora_database");

        } catch (SQLException e) {
            System.err.println("Error initializing schema: " + e.getMessage());
            System.exit(1);
        }
    }
}
