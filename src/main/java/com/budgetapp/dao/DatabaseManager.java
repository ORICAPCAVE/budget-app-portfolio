package com.budgetapp.dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final Path DB_PATH = Paths.get(System.getProperty("user.home"), ".budget-app", "budget.db");
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_PATH;

    public Connection getConnection() throws SQLException {
        try {
            Files.createDirectories(DB_PATH.getParent());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create application data folder", e);
        }
        return DriverManager.getConnection(JDBC_URL);
    }

    public void initializeSchema() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS income (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        source TEXT NOT NULL,
                        amount NUMERIC NOT NULL,
                        recurrence TEXT NOT NULL,
                        received_date TEXT
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS expense (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        amount NUMERIC NOT NULL,
                        recurrence TEXT NOT NULL,
                        due_date TEXT,
                        category TEXT NOT NULL
                    )
                    """);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS debt (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        balance NUMERIC NOT NULL,
                        apr NUMERIC NOT NULL,
                        minimum_payment NUMERIC NOT NULL,
                        extra_payment NUMERIC NOT NULL,
                        due_date TEXT
                    )
                    """);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize database schema", e);
        }
    }
}
