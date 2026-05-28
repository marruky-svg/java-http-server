package com.marruky.repository;

import com.marruky.db.DatabaseConnection;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class AnalysisJobRepository {

    public int save(String type, String status) {
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement("INSERT INTO analysis_jobs (type, status) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, type);
            stmt.setString(2, status);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            return -1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
