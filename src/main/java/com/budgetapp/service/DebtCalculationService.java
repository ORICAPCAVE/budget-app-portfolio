package com.budgetapp.service;

import com.budgetapp.model.Debt;
import com.budgetapp.model.PayoffResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DebtCalculationService {

    public PayoffResult calculatePayoff(Debt debt) {
        BigDecimal balance = debt.getBalance();
        BigDecimal monthlyPayment = debt.getTotalMonthlyPayment();
        BigDecimal monthlyRate = MoneyMath.monthlyRateFromApr(debt.getApr());

        int months = 0;
        BigDecimal totalInterest = BigDecimal.ZERO;

        while (balance.compareTo(BigDecimal.valueOf(0.01)) > 0 && months < 600) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);

            if (monthlyPayment.compareTo(interest) <= 0) {
                return new PayoffResult(months, MoneyMath.money(totalInterest), true);
            }

            totalInterest = totalInterest.add(interest);
            balance = balance.add(interest).subtract(monthlyPayment);

            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                balance = BigDecimal.ZERO;
            }

            months++;
        }

        return new PayoffResult(months, MoneyMath.money(totalInterest), false);
    }
}
