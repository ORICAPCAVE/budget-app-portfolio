package com.budgetapp.service;
import com.budgetapp.model.Debt;
import com.budgetapp.model.PayoffResult;
import com.budgetapp.model.Recurrence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DebtCalculationService {

    private static final BigDecimal TWELVE = new BigDecimal("12");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final int MAX_MONTHS = 1200; // safety cap: 100 years


    public BigDecimal calculateTotalMonthly(List<Debt> debts) {
        BigDecimal total = BigDecimal.ZERO;

        for (Debt debt : debts) {
            if (debt.getMinimumPayment() != null) {
                total = total.add(debt.getMinimumPayment());
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
    public PayoffResult calculatePayoff(Debt debt) {
        if (debt == null) {
            throw new IllegalArgumentException("Debt cannot be null");
        }

        if (debt.getAmount() == null || debt.getInterestRate() == null || debt.getMinimumPayment() == null) {
            throw new IllegalArgumentException("Debt amount, interest rate, and minimum payment are required");
        }

        BigDecimal balance = debt.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal annualRate = debt.getInterestRate().setScale(10, RoundingMode.HALF_UP);
        BigDecimal payment = debt.getMinimumPayment().setScale(2, RoundingMode.HALF_UP);

        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            return new PayoffResult(0, BigDecimal.ZERO, false);
        }

        if (payment.compareTo(BigDecimal.ZERO) <= 0) {
            return new PayoffResult(-1, BigDecimal.ZERO, true);
        }

        BigDecimal monthlyRate = annualRate
                .divide(ONE_HUNDRED, 10, RoundingMode.HALF_UP)
                .divide(TWELVE, 10, RoundingMode.HALF_UP);

        int months = 0;
        BigDecimal totalInterestPaid = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        while (balance.compareTo(BigDecimal.ZERO) > 0 && months < MAX_MONTHS) {
            BigDecimal monthlyInterest = balance
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            if (payment.compareTo(monthlyInterest) <= 0) {
                return new PayoffResult(-1, totalInterestPaid, true);
            }

            BigDecimal principalPaid = payment.subtract(monthlyInterest);

            if (principalPaid.compareTo(balance) > 0) {
                principalPaid = balance;
            }

            balance = balance.subtract(principalPaid).setScale(2, RoundingMode.HALF_UP);
            totalInterestPaid = totalInterestPaid.add(monthlyInterest).setScale(2, RoundingMode.HALF_UP);
            months++;
        }

        boolean negativeAmortization = balance.compareTo(BigDecimal.ZERO) > 0;

        if (negativeAmortization) {
            return new PayoffResult(-1, totalInterestPaid, true);
        }

        return new PayoffResult(months, totalInterestPaid, false);
    }
}