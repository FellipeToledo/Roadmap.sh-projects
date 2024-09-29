package com.azvtech;

import java.time.LocalDate;
import java.util.UUID;

public class Expense {
    private final UUID id;
    private double amount;
    private String description;
    private LocalDate date;

    public Expense(double amount, String description) {
        this.id = UUID.randomUUID();
        this.amount = amount;
        this.description = description;
        this.date = LocalDate.now();
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

    @Override
    public String toString() {
        return "Expense{id=" + id + ", amount=" + amount + ", description='" + description + "', date=" + date + "}";
    }
}
