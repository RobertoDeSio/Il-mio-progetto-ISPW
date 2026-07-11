package org.ispw.eventi.model.dao.dbms;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String DB_URL = "jdbc:sqlite:data/weekender.db";

    private static Connection connection;

    private DatabaseConnection() {}

    public static Connection getInstance() {
        try {
            Class.forName("org.sqlite.JDBC");
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Driver SQLite non trovato");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore connessione database: " + DB_URL);
        }
        return connection;
    }

    public static void inizializzaSchema() {
        try {
            String sql = Files.readString(Paths.get("data/schema.sql"), StandardCharsets.UTF_8);
            try (Statement stmt = getInstance().createStatement()) {
                stmt.executeUpdate(sql);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore lettura schema.sql");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore inizializzazione schema database");
        }
    }
}