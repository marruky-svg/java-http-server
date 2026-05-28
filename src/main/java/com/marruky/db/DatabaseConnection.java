package com.marruky.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        Properties properties = new Properties();
        try {
            if (connection == null || connection.isClosed()) {
                properties.load(DatabaseConnection.class.getResourceAsStream("/config.properties"));
                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.user");
                String password = properties.getProperty("db.password");
                connection = DriverManager.getConnection(url, user, password);
            }
        }catch (Exception e){
            throw new RuntimeException("O erro está aqui: " + e);
        }
        return connection;
    }
}
