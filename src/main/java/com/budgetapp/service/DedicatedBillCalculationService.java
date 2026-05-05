package com.budgetapp.service;

import com.budgetapp.model.BillGroup;
import com.budgetapp.model.DedicatedBill;

import java.math.BigDecimal;
import java.util.List;

public class DedicatedBillCalculationService {

    public BigDecimal totalMonthly(List<DedicatedBill> bills) {
        return bills.stream()
                .map(bill -> MoneyMath.monthlyEquivalent(
                        bill.getAmount(),
                        bill.getRecurrence()
                ))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalMonthlyForGroup(List<DedicatedBill> bills, BillGroup group) {
        return bills.stream()
                .filter(bill -> bill.getBillGroup() == group)
                .map(bill -> MoneyMath.monthlyEquivalent(
                        bill.getAmount(),
                        bill.getRecurrence()
                ))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}