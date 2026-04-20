package com.budgetapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Debt {
    private long id;
    private String name;
    private DebtType type;
    private BigDecimal balance;
    private BigDecimal apr;
    private BigDecimal minimumPayment;
    private BigDecimal extraPayment;
    private LocalDate dueDate;

    public Debt() {
    }

    public Debt(long id, String name, DebtType type, BigDecimal balance, BigDecimal apr,
                BigDecimal minimumPayment, BigDecimal extraPayment, LocalDate dueDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.apr = apr;
        this.minimumPayment = minimumPayment;
        this.extraPayment = extraPayment;
        this.dueDate = dueDate;
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

    public void setName(String name) {
        this.name = name;
    }

    public DebtType getType() {
        return type;
    }

    public void setType(DebtType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getApr() {
        return apr;
    }

    public void setApr(BigDecimal apr) {
        this.apr = apr;
    }

    public BigDecimal getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(BigDecimal minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

    public BigDecimal getExtraPayment() {
        return extraPayment;
    }

    public void setExtraPayment(BigDecimal extraPayment) {
        this.extraPayment = extraPayment;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalMonthlyPayment() {
        return minimumPayment.add(extraPayment);
    }
}
