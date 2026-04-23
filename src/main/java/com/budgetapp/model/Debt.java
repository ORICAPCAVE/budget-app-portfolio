package com.budgetapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Debt {
    private long id;
    private String name;
    private BigDecimal amount;
    private Recurrence recurrence;
    private BigDecimal interestRate;
    private BigDecimal minimumPayment;

    public Debt(String name, BigDecimal amount, Recurrence recurrence,
                BigDecimal interestRate, BigDecimal minimumPayment) {
        this.name = name;
        this.amount = amount;
        this.recurrence = recurrence;
        this.interestRate = interestRate;
        this.minimumPayment = minimumPayment;
    }
    public Debt() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public BigDecimal getMinimumPayment() {
        return minimumPayment;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public void setMinimumPayment(BigDecimal minimumPayment) {
        this.minimumPayment = minimumPayment;
    }
}