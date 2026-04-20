package com.budgetapp.service;

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
}
