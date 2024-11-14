package com.example.cleonoraadmin.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class SchemaInitializer {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initializeSchema() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            String checkSchemaExists = "SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'cleonora_database'";
            var rs = statement.executeQuery(checkSchemaExists);

            if (!rs.next()) {
                statement.execute("CREATE SCHEMA cleonora_database");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
