package com.marruky.repository;

import com.marruky.db.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AnalyseResultRepository {

    public int save(int jobId, String isoladoId, int length, int countA, int countT, int countC, int countG, double gcContent) {
        try(PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement("INSERT INTO analyse_results (job_id, isolado_id, length, count_A, count_T, count_G, count_C, gc_content) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, jobId);
            stmt.setString(2, isoladoId);
            stmt.setInt(3, length);
            stmt.setInt(4, countA);
            stmt.setInt(5, countT);
            stmt.setInt(6, countG);
            stmt.setInt(7, countC);
            stmt.setDouble(8, gcContent);
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
