package com.budgetapp.service;

import com.budgetapp.model.Recurrence;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyMath {
    public static final int SCALE = 2;

    private MoneyMath() {
    }

    public static BigDecimal monthlyRateFromApr(BigDecimal aprPercent) {
        return aprPercent
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
    }

    public static BigDecimal money(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal monthlyEquivalent(BigDecimal amount, Recurrence recurrence) {

        if (amount == null || recurrence == null) {
            return BigDecimal.ZERO;
        }

        switch (recurrence) {
            case DAILY:
                return amount.multiply(BigDecimal.valueOf(30));

            case WEEKLY:
                return amount.multiply(BigDecimal.valueOf(4));

            case BIWEEKLY:
                return amount.multiply(BigDecimal.valueOf(2));

            case MONTHLY:
                return amount;

            case QUARTERLY:
                return amount.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);

            case YEARLY:
                return amount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

            default:
                return BigDecimal.ZERO;
        }
    }
}
