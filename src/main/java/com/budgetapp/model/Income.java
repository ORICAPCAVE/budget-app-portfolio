package com.budgetapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Income {
    private long id;
    private String source;
    private BigDecimal amount;
    private Recurrence recurrence;
    private LocalDate receivedDate;

    public Income() {
    }

    public Income(long id, String source, BigDecimal amount, Recurrence recurrence, LocalDate receivedDate) {
        this.id = id;
        this.source = source;
        this.amount = amount;
        this.recurrence = recurrence;
        this.receivedDate = receivedDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }
}
