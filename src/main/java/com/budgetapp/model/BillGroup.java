package com.budgetapp.model;

public enum BillGroup {
    BUSINESS("Business"),
    LIVING("Living");

    private final String label;

    BillGroup(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}