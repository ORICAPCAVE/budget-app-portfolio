package com.budgetapp.dao;

import com.budgetapp.model.Income;
import com.budgetapp.model.Recurrence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IncomeDao {

    private final DatabaseManager db = new DatabaseManager();

    public List<Income> findAll() {
        List<Income> list = new ArrayList<>();

        String sql = "SELECT * FROM income";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Income income = new Income();
                income.setId(rs.getLong("id"));
                income.setSource(rs.getString("source"));
                income.setAmount(rs.getBigDecimal("amount"));
                income.setRecurrence(Recurrence.valueOf(rs.getString("recurrence")));

                list.add(income);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void save(Income income) {
        String sql = """
            INSERT INTO income(source, amount, recurrence)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, income.getSource());
            ps.setBigDecimal(2, income.getAmount());
            ps.setString(3, income.getRecurrence().name());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM income WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}