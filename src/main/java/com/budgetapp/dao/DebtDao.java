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
                debt.setName(rs.getString("name"));
                debt.setAmount(rs.getBigDecimal("amount"));
                debt.setInterestRate(rs.getBigDecimal("interest_rate"));
                debt.setMinimumPayment(rs.getBigDecimal("minimum_payment"));
                debt.setRecurrence(Recurrence.valueOf(rs.getString("recurrence")));

                list.add(debt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public void save(Debt debt) {
        if (debt.getId() == 0 || debt.getId() == 0) {
            insert(debt);
        } else {
            update(debt);
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
    private void insert(Debt debt) {
        String sql = """
        INSERT INTO debt(name, amount, interest_rate, minimum_payment, recurrence)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, debt.getName());
            ps.setBigDecimal(2, debt.getAmount());
            ps.setBigDecimal(3, debt.getInterestRate());
            ps.setBigDecimal(4, debt.getMinimumPayment());
            ps.setString(5, debt.getRecurrence().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    debt.setId(keys.getLong(1));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void update(Debt debt) {
        String sql = """
        UPDATE debt
        SET name = ?, amount = ?, interest_rate = ?, minimum_payment = ?, recurrence = ?
        WHERE id = ?
    """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, debt.getName());
            ps.setBigDecimal(2, debt.getAmount());
            ps.setBigDecimal(3, debt.getInterestRate());
            ps.setBigDecimal(4, debt.getMinimumPayment());
            ps.setString(5, debt.getRecurrence().name());
            ps.setLong(6, debt.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}