CREATE SCHEMA IF NOT EXISTS cleonora_database;
CREATE TABLE cleonora_database.persistent_logins (
                                   username VARCHAR(64) NOT NULL,
                                   series VARCHAR(64) PRIMARY KEY NOT NULL,
                                   token VARCHAR(64) NOT NULL,
                                   last_used TIMESTAMP NOT NULL
);