package com.budgetapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {
    private long id;
    private String name;
    private BigDecimal amount;
    private Recurrence recurrence;
    private LocalDate dueDate;
    private ExpenseCategory category;

    public Expense() {
    }

    public Expense(long id, String name, BigDecimal amount, Recurrence recurrence, LocalDate dueDate, ExpenseCategory category) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.recurrence = recurrence;
        this.dueDate = dueDate;
        this.category = category;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }
}
