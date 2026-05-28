package com.marruky.repository;

import com.marruky.db.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompareResultRepository {
    public int save(int jobId, String isoladoA, String isoladoB, int score, double similarity, String alignedA, String alignedB) {
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement("INSERT INTO compare_results (job_id, isolado_a, isolado_b, score, similarity, aligned_a, aligned_b) VALUES (?, ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, jobId);
            stmt.setString(2, isoladoA);
            stmt.setString(3, isoladoB);
            stmt.setInt(4, score);
            stmt.setDouble(5, similarity);
            stmt.setString(6, alignedA);
            stmt.setString(7, alignedB);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
