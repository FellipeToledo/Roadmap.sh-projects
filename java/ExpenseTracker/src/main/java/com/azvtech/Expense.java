package com.azvtech;

public class Expense {
    private static int idCounter = 1;

    private final int id;
    private double amount;
    private String description;

    public Expense(double amount, String description) {
        this.id = idCounter++;
        this.amount = amount;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Expense{id=" + id + ", amount=" + amount + ", description='" + description + "'}";
    }
}
