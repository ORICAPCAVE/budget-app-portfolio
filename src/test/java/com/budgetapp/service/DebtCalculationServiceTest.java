package com.budgetapp.service;

import com.budgetapp.model.Debt;
import com.budgetapp.model.DebtType;
import com.budgetapp.model.PayoffResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebtCalculationServiceTest {

    @Test
    void shouldCalculatePayoffForCreditCard() {
        Debt debt = new Debt(
                1,
                "Visa",
                DebtType.CREDIT_CARD,
                new BigDecimal("3500.00"),
                new BigDecimal("21.99"),
                new BigDecimal("125.00"),
                new BigDecimal("75.00"),
                LocalDate.now()
        );

        DebtCalculationService service = new DebtCalculationService();
        PayoffResult result = service.calculatePayoff(debt);

        assertTrue(result.monthsToPayoff() > 0);
        assertFalse(result.negativeAmortization());
    }
}
