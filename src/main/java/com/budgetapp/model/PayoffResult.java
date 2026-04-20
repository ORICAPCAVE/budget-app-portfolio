package com.budgetapp.model;

import java.math.BigDecimal;

public record PayoffResult(
        int monthsToPayoff,
        BigDecimal totalInterestPaid,
        boolean negativeAmortization
) {
}
