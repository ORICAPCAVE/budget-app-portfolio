package com.budgetapp.service;

import com.budgetapp.model.Debt;
import com.budgetapp.model.DebtType;
import com.budgetapp.model.PayoffResult;
import com.budgetapp.model.Recurrence;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebtCalculationServiceTest {

    @Test
    void shouldCalculatePayoffForCreditCard() {
        Debt debt = new Debt(
                "Visa",
                new BigDecimal("3500.00"),
                Recurrence.MONTHLY,
                new BigDecimal("21.99"),
                new BigDecimal("125.00")
        );

        DebtCalculationService service = new DebtCalculationService();
        PayoffResult result = service.calculatePayoff(debt);

        assertTrue(result.monthsToPayoff() > 0);
        assertFalse(result.negativeAmortization());
    }
    @Test
    void shouldDetectNegativeAmortization() {
        Debt debt = new Debt(
                "Bad Card",
                new BigDecimal("3500.00"),
                Recurrence.MONTHLY,
                new BigDecimal("29.99"),
                new BigDecimal("20.00")
        );

        DebtCalculationService service = new DebtCalculationService();
        PayoffResult result = service.calculatePayoff(debt);

        assertTrue(result.negativeAmortization());
    }
}