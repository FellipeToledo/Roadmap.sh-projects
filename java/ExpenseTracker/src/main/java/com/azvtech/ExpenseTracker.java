package com.azvtech;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The ExpenseTracker class manages a list of expenses, allowing users to add, update,
 * delete, and display expenses. The class also provides functionality to save and load
 * expenses from a JSON file and display summaries of expenses.
 */
public class ExpenseTracker
{
    /**
     * A list of strings used to add a new expense. The first element should be the amount (a double) and
     * the second element should be the description of the expense. Typically used with command-line
     * parameters for adding new expenses.
     *
     * Command-line usage:
     *  --add amount description
     *  -a amount description
     *
     * For example:
     *  --add 12.50 "Lunch at Restaurant"
     *  -a 18.75 "Office Supplies"
     */
    @Parameter(names = {"--add", "-a"}, description = "Add a new expense. Usage: --add amount description", arity = 2)
    List<String> addExpense = new ArrayList<>();

    /**
     * A list containing parameters for updating an existing expense.
     * Command-line arguments expected for this option are:
     * - id: The unique identifier of the expense to be updated (parsable as an integer)
     * - amount: The new amount for the expense (parsable as a double)
     * - description: The new description for the expense
     *
     * Example usage: --update id amount description
     *
     * This field is used in conjunction with other command-line arguments to
     * perform updates on the expenses list within the ExpenseTracker application.
     */
    @Parameter(names = {"--update", "-u"}, description = "Update an existing expense. Usage: --update id amount description", arity = 3)
    List<String> updateExpense = new ArrayList<>();

    /**
     * Stores command-line arguments for deleting an existing expense by its id.
     *
     * The list accepts a single argument that represents the id of the expense to be deleted.
     * This parameter can be specified using "--delete" or "-d" flags.
     *
     * The deletion functionality is triggered based on the value provided in this list.
     *
     * Expected Usage: --delete [id]
     *
     * Error Handling:
     * - Ensure that exactly one parameter (the id of the expense) is provided.
     */
    @Parameter(names = {"--delete", "-d"}, description = "Delete an existing expense by id. Usage: --delete id", arity = 1)
    List<String> deleteExpense = new ArrayList<>();

    /**
     * Flag to indicate whether to show the summary of expenses in the ExpenseTracker.
     * If this flag is set to true, a summary of the user's expenses will be displayed.
     * It can be set using the command line arguments "--summary" or "-s".
     */
    @Parameter(names = {"--summary", "-s"}, description = "Show the summary of expenses")
    boolean showSummary = false;

    /**
     * A command-line parameter that specifies whether to list all recorded expenses.
     *
     * This boolean flag is used to determine if the application should display all
     * recorded expenses when executed. It can be activated using either the
     * "--all" or "-l" options in the command-line input.
     *
     * The flag defaults to {@code false}, meaning that if it is not explicitly
     * set by the user, the application will not list all recorded expenses.
     */
    @Parameter(names = {"--all", "-l"}, description = "List all recorded expenses")
    boolean listAll = false;

    /**
     * A command-line parameter that specifies the month for which to show a summary
     * of expenses. Valid values are integers from 1 to 12, representing January to
     * December.
     */
    @Parameter(names = {"--month-summary", "-m"}, description = "Show summary of expenses for a specific month (1-12)")
    Integer monthSummary = null;

    /**
     * This boolean flag indicates whether help information should be displayed.
     * It can be triggered via the command line arguments "--help" or "-h".
     */
    @Parameter(names = {"--help", "-h"}, help = true, description = "Display help")
    private boolean help;

    /**
     * The constant file path where the expense data is stored in JSON format.
     *
     * This variable points to the location of the file used to persist and load
     * the expenses for the ExpenseTracker application.
     */
    private static final String EXPENSE_FILE = "expenses.json";
    /**
     * A list that holds all the expenses recorded in the ExpenseTracker application.
     *
     * This list is used by various methods in the ExpenseTracker class to manage
     * expenses such as adding, updating, deleting, listing, and summarizing them.
     * The list is instantiated as an ArrayList and is final, meaning it cannot be reassigned.
     */
    private final List<Expense> expenses = new ArrayList<>();

    /**
     * An instance of the Gson class, configured with a custom adapter for serializing
     * and deserializing LocalDate objects.
     *
     * This Gson instance is used for converting Expense objects to and from JSON format
     * within the ExpenseTracker application. The LocalDateAdapter ensures that LocalDate
     * fields are properly handled during the conversion process.
     */
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    /**
     * Entry point for the ExpenseTracker application. This method initializes the
     * ExpenseTracker, parses command line arguments using JCommander, and performs
     * actions based on the provided arguments such as adding, updating, deleting
     * expenses, and displaying summaries.
     *
     * @param args Command line arguments to specify different operations like add,
     *             update, delete expenses, and to show different types of summaries.
     */
    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        JCommander commander = JCommander.newBuilder()
                .addObject(tracker)
                .build();

        try {
            commander.parse(args);

            if (tracker.help) {
                commander.usage();
                return;
            }

            tracker.loadExpenses();

            if (!tracker.addExpense.isEmpty()) {
                tracker.addNewExpense();
            }

            if (!tracker.updateExpense.isEmpty()) {
                tracker.updateExpense();
            }

            if (!tracker.deleteExpense.isEmpty()) {
                tracker.deleteExpense();
            }

            if (tracker.showSummary) {
                tracker.showExpenseSummary();
            }

            if (tracker.listAll) {
                tracker.listAllExpenses();
            }

            if (tracker.monthSummary != null) {
                tracker.showMonthSummary(tracker.monthSummary);
            }

            tracker.saveExpenses();

        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            commander.usage();
        } catch (IOException e) {
            System.out.println("Error reading or writing to file: " + e.getMessage());
        }
    }

    /**
     * Adds a new expense to the expenses list.
     *
     * This method processes the first element in the addExpense list as the amount
     * of the expense and the second element as its description. It creates a new
     * Expense object and adds it to the expenses list. If the amount cannot be
     * parsed to a double, it catches a NumberFormatException and prints an error
     * message.
     *
     * Assumptions:
     * - addExpense is a list containing at least two elements.
     * - expenses is a list that contains Expense objects.
     *
     * Error Handling:
     * - Prints an error message if the amount is not in a valid format.
     */
    private void addNewExpense() {
        try {
            double amount = Double.parseDouble(addExpense.get(0));
            String description = addExpense.get(1);
            Expense expense = new Expense(amount, description);
            expenses.add(expense);
            System.out.println("Added expense: " + expense);
        } catch (NumberFormatException e) {
            System.err.println("Invalid amount format for expense: " + addExpense.get(0));
        }
    }

    /**
     * Updates an existing expense in the expenses list.
     *
     * This method retrieves the first element from the updateExpense list as the UUID of the expense,
     * the second element as the new amount, and the third element as the new description. It finds the
     * expense by its UUID and updates its amount and description.
     *
     * Assumptions:
     * - The updateExpense list contains at least three elements.
     * - The expenses list contains Expense objects.
     *
     * Error Handling:
     * - Prints an error message if the UUID or amount is not in a valid format.
     * - Prints an error message if an expense with the given UUID is not found.
     */
    private void updateExpense() {
        try {
            UUID id = UUID.fromString(updateExpense.get(0));
            double amount = Double.parseDouble(updateExpense.get(1));
            String description = updateExpense.get(2);

            Expense expenseToUpdate = findExpenseById(id);
            if (expenseToUpdate == null) {
                System.err.println("Expense with ID " + id + " not found.");
                return;
            }

            expenseToUpdate.setAmount(amount);
            expenseToUpdate.setDescription(description);
            System.out.println("Updated expense: " + expenseToUpdate);
        } catch (NumberFormatException e) {
            System.err.println("Invalid format: id=" + updateExpense.get(0) + ", amount=" + updateExpense.get(1));
        }
    }

    /**
     * Deletes an expense from the expenses list.
     *
     * This method retrieves and parses the first element from the deleteExpense list
     * as the UUID of the expense to be deleted. It finds the expense by the UUID, and if found,
     * removes the expense from the expenses list. If no expense is found with the given UUID,
     * it prints an error message.
     *
     * Assumptions:
     * - deleteExpense is a list containing at least one element.
     * - expenses is a list that contains Expense objects.
     *
     * Error Handling:
     * - Prints an error message if the UUID is not in a valid format.
     * - Prints an error message if an expense with the given UUID is not found.
     */
    private void deleteExpense() {
        try {
            UUID id = UUID.fromString(updateExpense.get(0));
            Expense expenseToRemove = findExpenseById(id);
            if (expenseToRemove == null) {
                System.err.println("Expense with ID " + id + " not found.");
                return;
            }
            expenses.remove(expenseToRemove);
            System.out.println("Deleted expense: " + expenseToRemove);
        } catch (NumberFormatException e) {
            System.err.println("Invalid format for id: " + deleteExpense.get(0));
        }
    }

    /**
     * Finds an expense by its unique identifier.
     *
     * This method searches through the list of expenses and returns the expense
     * that matches the provided UUID. If no match is found, it returns null.
     *
     * @param id The UUID of the expense to be found.
     * @return The expense with the matching UUID, or null if no match is found.
     */
    private Expense findExpenseById(UUID id) {
        return expenses.stream().filter(expense -> expense.getId() == id).findFirst().orElse(null);
    }

    /**
     * Lists all the recorded expenses.
     *
     * This method checks if there are any expenses recorded. If no expenses are found,
     * it prints a message indicating that no expenses have been recorded.
     * Otherwise, it iterates through the list of expenses and prints each expense.
     *
     * Error Handling:
     * - Prints "No recorded expenses." if the expenses list is empty.
     */
    private void listAllExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No recorded expenses.");
        } else {
            System.out.println("All Recorded Expenses:");
            for (Expense expense : expenses) {
                System.out.println(expense);
            }
        }
    }

    /**
     * Displays a summary of all recorded expenses in the expenses list.
     *
     * The method checks if there are any expenses recorded. If no expenses are found,
     * it prints a message indicating that no expenses have been recorded.
     * Otherwise, it iterates through the list of expenses, prints each expense, and
     * calculates the total amount of all expenses. Finally, it prints the total amount.
     *
     * Error Handling:
     * - Prints "No expenses recorded." if the expenses list is empty.
     */
    private void showExpenseSummary() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }

        double total = 0;
        System.out.println("Expense Summary:");
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        System.out.println("Total: " + total);
    }

    /**
     * Displays a summary of expenses for the specified month.
     *
     * The method filters the list of recorded expenses to find those that belong to the
     * specified month and then prints each expense along with the total amount spent
     * for that month.
     *
     * @param month The month for which the summary is to be displayed. The month is
     *              represented as an integer where 1 corresponds to January and 12
     *              corresponds to December.
     */
    private void showMonthSummary(int month) {
        Month specifiedMonth = Month.of(month);
        List<Expense> monthlyExpenses = expenses.stream()
                .filter(expense -> expense.getDate().getMonth() == specifiedMonth)
                .collect(Collectors.toList());

        if (monthlyExpenses.isEmpty()) {
            System.out.println("No recorded expenses for month: " + specifiedMonth);
        } else {
            double totalAmount = monthlyExpenses.stream().mapToDouble(Expense::getAmount).sum();
            System.out.printf("Expense Summary for %s:%n", specifiedMonth);
            for (Expense expense : monthlyExpenses) {
                System.out.println(expense);
            }
            System.out.printf("Total Expenses for %s: %.2f%n", specifiedMonth, totalAmount);
        }
    }

    /**
     * Loads expenses from a JSON file. If the expense file does not exist,
     * the method simply returns without performing any actions. If the file
     * exists, it reads the JSON content and converts it into a list of Expense
     * objects, which are then added to the existing list of expenses.
     *
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private void loadExpenses() throws IOException {
        File file = new File(EXPENSE_FILE);
        if (!file.exists()) {
            return;  // If no file exists, just return
        }

        try (Reader reader = new FileReader(file)) {
            Type expenseListType = new TypeToken<List<Expense>>() {}.getType();
            List<Expense> loadedExpenses = gson.fromJson(reader, expenseListType);
            if (loadedExpenses != null) {
                expenses.clear();
                expenses.addAll(loadedExpenses);
            }
        } catch (IOException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

    /**
     * Saves the current list of expenses to a JSON file.
     *
     * This method uses the Gson library to serialize the list of expenses into JSON format
     * and writes it to a file specified by the EXPENSE_FILE field.
     *
     * The file is overwritten if it already exists. If the file does not exist,
     * it will be created. The method handles all lower-level I/O operations using
     * a FileWriter wrapped in a try-with-resources statement to ensure the file
     * is properly closed after writing.
     *
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    // Save expenses to a JSON file
    private void saveExpenses() throws IOException {
        try (Writer writer = new FileWriter(EXPENSE_FILE)) {
            gson.toJson(expenses, writer);
        }
    }
}
