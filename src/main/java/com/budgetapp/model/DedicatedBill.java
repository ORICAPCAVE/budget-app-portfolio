package com.budgetapp.model;

import java.math.BigDecimal;

public class DedicatedBill {

    private long id;
    private String name;
    private BigDecimal amount;
    private Recurrence recurrence;
    private BillGroup billGroup;

    public DedicatedBill(long id, String name, BigDecimal amount,
                         Recurrence recurrence, BillGroup billGroup) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.recurrence = recurrence;
        this.billGroup = billGroup;
    }

    public DedicatedBill(String name, BigDecimal amount,
                         Recurrence recurrence, BillGroup billGroup) {
        this.name = name;
        this.amount = amount;
        this.recurrence = recurrence;
        this.billGroup = billGroup;
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

    public BillGroup getBillGroup() {
        return billGroup;
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

    public void setBillGroup(BillGroup billGroup) {
        this.billGroup = billGroup;
    }
}