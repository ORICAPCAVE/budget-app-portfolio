package com.budgetapp.dao;

import com.budgetapp.model.Debt;
import com.budgetapp.model.Recurrence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DebtDao {

    private final DatabaseManager db = new DatabaseManager();

    public List<Debt> findAll() {
        List<Debt> list = new ArrayList<>();

        String sql = "SELECT * FROM debt";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Debt debt = new Debt();
                debt.setId(rs.getLong("id"));
                debt.setName(rs.getString("name")); // <-- changed
                debt.setAmount(rs.getBigDecimal("amount"));
                debt.setRecurrence(Recurrence.valueOf(rs.getString("recurrence")));

                list.add(debt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void save(Debt debt) {
        String sql = """
            INSERT INTO debt(name, amount, recurrence)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, debt.getName()); // <-- changed
            ps.setBigDecimal(2, debt.getAmount());
            ps.setString(3, debt.getRecurrence().name());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM debt WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}