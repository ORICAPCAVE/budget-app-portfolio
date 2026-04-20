package com.budgetapp.model;

import java.math.BigDecimal;

public record BudgetSummary(
        BigDecimal totalMonthlyIncome,
        BigDecimal totalMonthlyExpenses,
        BigDecimal totalMonthlyDebtPayments,
        BigDecimal remainingCash
) {
}
