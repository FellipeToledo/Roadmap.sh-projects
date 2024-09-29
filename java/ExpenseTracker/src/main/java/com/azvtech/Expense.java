package com.azvtech;

import java.time.LocalDate;
import java.util.UUID;

public class Expense {
    private final UUID id;
    private double amount;
    private String description;
    private LocalDate date;
    private ExpenseCategory category;

    public Expense(double amount, String description, ExpenseCategory category) {
        this.id = UUID.randomUUID();
        this.amount = amount;
        this.description = description;
        this.date = LocalDate.now();
        this.category = category;
    }

    public UUID getId() {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Expense: #" + id + " \n " +
                "amount = " + amount + " \n " +
                "description = " + description + " \n" +
                " date = " + date + " \n " +
                "category = " + category + " \n" +
                "_____________________________";
    }
}
