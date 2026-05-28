package com.marruky.repository;

import com.marruky.db.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AmrResultRepository {
    public int save(int jobId, String isoladoId, String gene, String antibioticClass, double similarity, int score) {
        try(PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement("INSERT INTO amr_results (job_id, isolado_id, gene, antibiotic_class, similarity, score) VALUES(?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)){

            stmt.setInt(1, jobId);
            stmt.setString(2, isoladoId);
            stmt.setString(3, gene);
            stmt.setString(4, antibioticClass);
            stmt.setDouble(5, similarity);
            stmt.setInt(6, score);
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
