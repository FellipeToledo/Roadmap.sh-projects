package com.azvtech;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ExpenseTracker
{

    @Parameter(names = {"--add", "-a"}, description = "Add a new expense. Usage: --add amount description", arity = 2)
    List<String> addExpense = new ArrayList<>();

    @Parameter(names = {"--summary", "-s"}, description = "Show the summary of expenses")
    boolean showSummary = false;

    @Parameter(names = {"--help", "-h"}, help = true, description = "Display help")
    private boolean help;

    private static final String EXPENSE_FILE = "expenses.json";
    private final List<Expense> expenses = new ArrayList<>();

    private final Gson gson = new Gson();

    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        JCommander jc = JCommander.newBuilder().addObject(tracker).build();

        try {
            jc.parse(args);

            if (tracker.help) {
                jc.usage();
                return;
            }

            tracker.loadExpenses();  // Load expenses from JSON file

            if (!tracker.addExpense.isEmpty()) {
                tracker.addNewExpense();
                tracker.saveExpenses();  // Save expenses after adding new ones
            }

            if (tracker.showSummary) {
                tracker.showExpenseSummary();
            }

        } catch (ParameterException ex) {
            System.out.println(ex.getMessage());
            jc.usage();
        } catch (IOException e) {
            System.out.println("Error reading or writing to file: " + e.getMessage());
        }
    }

    private void addNewExpense() {
        try {
            double amount = Double.parseDouble(addExpense.get(0));
            String description = addExpense.get(1);
            expenses.add(new Expense(amount, description));
            System.out.println("Expense added: " + amount + " - " + description);
        } catch (NumberFormatException e) {
            System.out.println("Error: Amount should be a number.");
        }
    }

    private void showExpenseSummary() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }

        double total = 0;
        System.out.println("Expense Summary:");
        for (Expense expense : expenses) {
            System.out.println(expense);
            total += expense.getAmount();
        }
        System.out.println("Total: " + total);
    }

    // Load expenses from a JSON file
    private void loadExpenses() throws IOException {
        File file = new File(EXPENSE_FILE);
        if (!file.exists()) {
            return;  // If no file exists, just return
        }

        try (Reader reader = new FileReader(file)) {
            Type expenseListType = new TypeToken<List<Expense>>() {}.getType();
            List<Expense> loadedExpenses = gson.fromJson(reader, expenseListType);
            if (loadedExpenses != null) {
                expenses.addAll(loadedExpenses);
            }
        } catch (IOException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

    // Save expenses to a JSON file
    private void saveExpenses() throws IOException {
        try (Writer writer = new FileWriter(EXPENSE_FILE)) {
            gson.toJson(expenses, writer);
        }
    }
}
