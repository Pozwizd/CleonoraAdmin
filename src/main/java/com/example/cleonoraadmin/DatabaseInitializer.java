package com.example.cleonoraadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;

@Component
public class DatabaseInitializer {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initializeDatabase() {
        // Use template1 to create the postgres database if it doesn't exist
       try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/template1", "postgres", "0000");
             Statement statement = connection.createStatement()) {

            // Check if the "postgres" database exists
            ResultSet resultSet = statement.executeQuery("SELECT 1 FROM pg_database WHERE datname = 'postgres'");
            if (!resultSet.next()) {
                // If the database doesn't exist, create it
                statement.executeUpdate("CREATE DATABASE postgres");
            }

        } catch (SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
            System.exit(1);
        }

        // Use the DataSource to create the schema
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS cleonora_database");

        } catch (SQLException e) {
            System.err.println("Error initializing schema: " + e.getMessage());
            System.exit(1);
        }
    }
}
