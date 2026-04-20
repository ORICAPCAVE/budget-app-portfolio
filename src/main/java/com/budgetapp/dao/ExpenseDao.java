package com.budgetapp.dao;

import com.budgetapp.model.Expense;
import com.budgetapp.model.Recurrence;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDao {

    private final DatabaseManager db = new DatabaseManager();

    public List<Expense> findAll() {
        List<Expense> list = new ArrayList<>();

        String sql = "SELECT * FROM expense";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Expense e = new Expense();
                e.setId(rs.getLong("id"));
                e.setName(rs.getString("name"));
                e.setAmount(rs.getBigDecimal("amount"));
                e.setRecurrence(Recurrence.valueOf(rs.getString("recurrence")));

                String date = rs.getString("due_date");
                if (date != null) {
                    e.setDueDate(LocalDate.parse(date));
                }

                list.add(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void save(Expense expense) {
        String sql = """
            INSERT INTO expense(name, amount, recurrence, due_date, category)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, expense.getName());
            ps.setBigDecimal(2, expense.getAmount());
            ps.setString(3, expense.getRecurrence().name());
            ps.setString(4, expense.getDueDate() != null ? expense.getDueDate().toString() : null);
            ps.setString(5, "GENERAL"); // simple for now

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM expense WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}