package com.budgetapp.dao;

import com.budgetapp.model.BillGroup;
import com.budgetapp.model.DedicatedBill;
import com.budgetapp.model.Recurrence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DedicatedBillDao {
    private final DatabaseManager db = new DatabaseManager();

    public List<DedicatedBill> findAll() {
        List<DedicatedBill> bills = new ArrayList<>();

        String sql = "SELECT id, name, amount, recurrence, bill_group FROM dedicated_bill";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DedicatedBill bill = new DedicatedBill(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("amount"),
                        Recurrence.valueOf(rs.getString("recurrence")),
                        BillGroup.valueOf(rs.getString("bill_group"))
                );

                bills.add(bill);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bills;
    }

    public void save(DedicatedBill bill) {
        String sql = """
                INSERT INTO dedicated_bill(name, amount, recurrence, bill_group)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bill.getName());
            ps.setBigDecimal(2, bill.getAmount());
            ps.setString(3, bill.getRecurrence().name());
            ps.setString(4, bill.getBillGroup().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(DedicatedBill bill) {
        String sql = "DELETE FROM dedicated_bill WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bill.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void update(DedicatedBill bill) {
        String sql = """
            UPDATE dedicated_bill
            SET name = ?, amount = ?, recurrence = ?, bill_group = ?
            WHERE id = ?
            """;

        try (Connection conn =db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bill.getName());
            ps.setBigDecimal(2, bill.getAmount());
            ps.setString(3, bill.getRecurrence().name());
            ps.setString(4, bill.getBillGroup().name());
            ps.setLong(5, bill.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}