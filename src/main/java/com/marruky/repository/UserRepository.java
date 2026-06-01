package com.marruky.repository;

import com.marruky.db.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public void save(String username, String passwordHah, String role) {
     try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement("INSERT INTO users (username, password_hash, role) VALUES (?,?,?)")) {
         stmt.setString(1, username);
         stmt.setString(2, passwordHah);
         stmt.setString(3, role);
         stmt.executeUpdate();

     }catch (SQLException e) {
         throw new RuntimeException(e);
     }

    }

    public boolean exists(String username, String passwordHash){
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement("SELECT id FROM users WHERE username = ? AND password_hash = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
