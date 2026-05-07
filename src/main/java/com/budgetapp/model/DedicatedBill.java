package com.budgetapp.model;

import java.math.BigDecimal;

public class DedicatedBill {

    private long id;
    private String name;
    private BigDecimal amount;
    private Recurrence recurrence;
    private ExpenseCategory category;

    public DedicatedBill(long id, String name, BigDecimal amount,
                         Recurrence recurrence, ExpenseCategory category) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.recurrence = recurrence;
        this.category = category;
    }

    public DedicatedBill(String name, BigDecimal amount,
                         Recurrence recurrence, ExpenseCategory category) {
        this.name = name;
        this.amount = amount;
        this.recurrence = recurrence;
        this.category = category;
    }

    public long getId() {
        return id;
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

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setBillGroup(ExpenseCategory category) {
        this.category = category;
    }
}