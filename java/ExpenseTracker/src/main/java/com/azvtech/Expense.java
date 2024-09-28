package com.azvtech;

public class Expense {
    private final double amount;
    private final String description;

    public Expense(double amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return amount + " - " + description;
    }
}
