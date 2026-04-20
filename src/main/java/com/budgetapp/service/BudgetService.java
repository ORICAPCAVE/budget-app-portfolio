package com.budgetapp.service;

import com.budgetapp.model.BudgetSummary;
import com.budgetapp.model.Debt;
import com.budgetapp.model.Expense;
import com.budgetapp.model.Income;
import com.budgetapp.model.Recurrence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BudgetService {

    public BudgetSummary summarize(List<Income> incomes, List<Expense> expenses, List<Debt> debts) {
        BigDecimal totalIncome = incomes.stream()
                .map(income -> monthlyEquivalent(income.getAmount(), income.getRecurrence()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = expenses.stream()
                .map(expense -> monthlyEquivalent(expense.getAmount(), expense.getRecurrence()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebtPayments = debts.stream()
                .map(Debt::getTotalMonthlyPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = totalIncome.subtract(totalExpenses).subtract(totalDebtPayments);

        return new BudgetSummary(
                MoneyMath.money(totalIncome),
                MoneyMath.money(totalExpenses),
                MoneyMath.money(totalDebtPayments),
                MoneyMath.money(remaining)
        );
    }

    public BigDecimal monthlyEquivalent(BigDecimal amount, Recurrence recurrence) {
        return switch (recurrence) {
            case WEEKLY -> amount.multiply(BigDecimal.valueOf(52)).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            case BIWEEKLY -> amount.multiply(BigDecimal.valueOf(26)).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            case MONTHLY -> amount;
            case QUARTERLY -> amount.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
            case YEARLY -> amount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        };
    }
}
