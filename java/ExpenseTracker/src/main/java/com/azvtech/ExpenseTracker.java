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
import java.util.*;
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
    @Parameter(names = {"--add", "-a"}, description = "Add a new expense. Usage: --add amount description category", arity = 3)
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
    @Parameter(names = {"--update", "-u"}, description = "Update an existing expense. Usage: --update id amount description category", arity = 4)
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
     * Filter expenses by category.
     *
     * This parameter allows the user to specify a category by which to filter
     * the listed expenses. When provided, the application will only display
     * expenses that match the given category.
     *
     * Usage: --category-filter category
     */
    @Parameter(names = {"--category-filter", "-c"}, description = "Filter expenses by category. Usage: --category-filter category")
    String categoryFilter;

    /**
     * Command-line parameter for setting a budget for a specific month.
     *
     * This variable is used to specify the budget for a given month through the command-line interface.
     * The user is expected to provide two arguments: the month (as an integer) and the budget amount.
     *
     * Example command: --set-budget 5 1000
     *
     * Arity: 2
     *
     * Format:
     * - month (int): The month for which the budget is being set (1 for January, 12 for December).
     * - amount (String): The budget amount in the specified month.
     *
     * Used for:
     * - Managing monthly budgets in an expense tracking application.
     *
     * Error Handling:
     * - The application should handle cases where the input is not in the correct format or the values provided are invalid.
     */
    @Parameter(names = {"--set-budget", "-b"}, description = "Set a budget for a specific month. Usage: --set-budget month amount", arity = 2)
    List<String> setBudget = new ArrayList<>();

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
     * A mapping of monthly budgets where each key represents a month and the
     * corresponding value represents the budget allocated for that month.
     */
    private Map<Month, Double> monthlyBudgets = new HashMap<>();

    /**
     * Entry point for the ExpenseTracker application.
     *
     * This method initializes the ExpenseTracker instance, parses command-line arguments,
     * and invokes various functionalities like loading, adding, updating, and deleting expenses.
     * It also handles displaying summaries and setting budgets based on user inputs.
     *
     * @param args Command-line arguments passed to the application.
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

            if (tracker.categoryFilter != null)
            {
                tracker.filterExpensesByCategory();
            }

            if (!tracker.setBudget.isEmpty()) {
                Month month = Month.valueOf(tracker.setBudget.get(0).toUpperCase());
                double amount = Double.parseDouble(tracker.setBudget.get(1));
                tracker.setMonthlyBudget(month, amount);
                System.out.println("Budget set for " + month + ": $" + amount);
            }

            // Example of checking the budget for a particular month (e.g., the current month)
            tracker.checkBudget(LocalDate.now().getMonth());

            tracker.saveExpenses();

        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            commander.usage();
        } catch (IOException e) {
            System.out.println("Error reading or writing to file: " + e.getMessage());
        }
    }

    /**
     * Adds a new expense to the expense list.
     *
     * This method retrieves the first element from the addExpense list as the amount,
     * the second element as the description, and the third element as the category.
     * It parses and validates the amount, checks for a valid category, and creates a new
     * Expense object which is then added to the expenses list.
     *
     * Assumptions:
     * - The addExpense list contains at least three elements.
     * - The expenses list contains Expense objects.
     * - The valid categories are defined in the ExpenseCategory enum.
     *
     * Error Handling:
     * - Parses the amount from the first element of addExpense and handles NumberFormatException if the format is invalid.
     * - Parses the category from the third element of addExpense and handles IllegalArgumentException if the category is invalid by defaulting to ExpenseCategory.OTHER.
     *
     * This method prints appropriate messages to the console to indicate the success or failure of adding the new expense.
     */
    private void addNewExpense() {
        try {
            double amount = Double.parseDouble(addExpense.get(0));
            String description = addExpense.get(1);
            ExpenseCategory category;

            try {
                category = ExpenseCategory.valueOf(addExpense.get(2).toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid category specified. Defaulting to OTHER.");
                category = ExpenseCategory.OTHER;
            }
            Expense expense = new Expense(amount, description, category);
            expenses.add(expense);
            System.out.println("Added " + expense);
        } catch (NumberFormatException e) {
            System.err.println("Invalid amount format for expense: " + addExpense.get(0));
        }
    }

    /**
     * Updates an existing expense in the expenses list.
     *
     * This method fetches the expense details from the `updateExpense` field, parses the values,
     * and updates the corresponding expense if found.
     *
     * Assumptions:
     * - The `updateExpense` list contains at least four elements.
     * - The `expenses` list contains `Expense` objects.
     * - Valid categories are defined in the `ExpenseCategory` enum.
     *
     * Error Handling:
     * - Prints error messages if the UUID or amount is in an invalid format.
     * - Prints a message if no expense is found with the specified UUID.
     */
    private void updateExpense() {
        try {
            UUID id = UUID.fromString(updateExpense.get(0));
            double amount = Double.parseDouble(updateExpense.get(1));
            String description = updateExpense.get(2);
            ExpenseCategory category = ExpenseCategory.valueOf(updateExpense.get(3).toUpperCase());
            Expense expenseToUpdate = findExpenseById(id);
            if (expenseToUpdate == null) {
                System.err.println("Expense with ID " + id + " not found.");
                return;
            }

            expenseToUpdate.setAmount(amount);
            expenseToUpdate.setDescription(description);
            expenseToUpdate.setCategory(category);
            System.out.println("Updated  " + expenseToUpdate);
        } catch (NumberFormatException e) {
            System.err.println("Invalid format: \n " +
                    "id=" + updateExpense.get(0) + " \n" +
                    "amount=" + updateExpense.get(1) + " \n" +
                    "description=" + updateExpense.get(2) + " \n" +
                    "category=" + updateExpense.get(3));
        }
    }

    /**
     * Deletes an expense from the list of expenses.
     *
     * This method retrieves the expense ID from the `deleteExpense` list, converts it to a UUID,
     * and attempts to find the corresponding expense. If the expense is found, it removes it from the list.
     * The method handles various error scenarios, including an empty `deleteExpense` list, an invalid UUID format,
     * and other exceptions that might occur during the process.
     *
     * Assumptions:
     * - The `deleteExpense` list contains at least one element, which is the ID of the expense to be deleted.
     * - The `expenses` list contains `Expense` objects.
     *
     * Error Handling:
     * - Prints an error message if the `deleteExpense` list is empty.
     * - Prints an error message if the UUID format is invalid.
     * - Prints an error message if the expense with the specified UUID is not found.
     * - Prints a general error message if any other exception occurs during the deletion process.
     */
    private void deleteExpense() {
        if (deleteExpense.isEmpty()) {
            System.err.println("No expense ID provided.");
            return;
        }

        try {
            UUID id = UUID.fromString(deleteExpense.get(0));
            Expense expenseToRemove = findExpenseById(id);
            if (expenseToRemove == null) {
                System.err.println("Expense with ID " + id + " not found.");
                return;
            }
            expenses.remove(expenseToRemove);
            System.out.println("Deleted " + expenseToRemove);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid format for id: " + deleteExpense.get(0));
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Finds an expense in the list of expenses by its unique identifier.
     *
     * @param id The unique identifier (UUID) of the expense to find.
     * @return The Expense object if found; otherwise, returns null.
     */
    private Expense findExpenseById(UUID id) {
        for (Expense expense : expenses) {
            if (expense.getId().equals(id)) {
                return expense;
            }
        }
        return null;
    }

    /**
     * Lists all recorded expenses.
     *
     * This method checks if there are any expenses recorded. If no expenses are found,
     * it prints a message indicating that no expenses have been recorded. Otherwise,
     * it iterates through the list of expenses and prints each expense.
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
     * Filters the list of expenses based on the specified category and prints the filtered expenses.
     *
     * This method attempts to parse the specified category filter from the `categoryFilter` field.
     * If the category is valid, it filters the expenses list to include only those expenses
     * that match the specified category. The filtered expenses are then printed to the console.
     *
     * Error Handling:
     * - If the category specified in `categoryFilter` is invalid, an error message is
     *   printed to the standard error stream, and no expenses are filtered.
     */
    private void filterExpensesByCategory()
    {
        ExpenseCategory category;
        try {
            category = ExpenseCategory.valueOf(categoryFilter.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid category specified. No expenses filtered.");
            return;
        }

        List<Expense> filteredExpenses = expenses.stream()
                .filter(expense -> expense.getCategory() == category)
                .collect(Collectors.toList());
        System.out.println("Filtered expenses by category '" + category + "':");
        filteredExpenses.forEach(System.out::println);
    }

    /**
     * Sets the monthly budget for a specified month.
     *
     * This method updates the budget for the given month with the specified amount.
     *
     * @param month  The month for which the budget is to be set.
     * @param amount The budget amount to be set for the specified month.
     */
    private void setMonthlyBudget(Month month, double amount) {
        monthlyBudgets.put(month, amount);
    }

    /**
     * Computes the total expenses for a specified month.
     *
     * @param month The month for which the expenses are to be calculated.
     * @return The total amount of expenses for the given month.
     */
    private double getMonthlyExpenses(Month month) {
        return expenses.stream()
                .filter(expense -> expense.getDate().getMonth() == month)
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Checks if the expenses for the given month exceed the set budget.
     *
     * This method calculates the total expenses for the specified month and
     * compares it with the pre-defined budget for that month. If the expenses
     * exceed the budget, a warning message is printed.
     *
     * @param month The month for which the budget check is performed.
     */
    private void checkBudget(Month month) {
        double expenses = getMonthlyExpenses(month);
        if (monthlyBudgets.containsKey(month) && expenses > monthlyBudgets.get(month)) {
            System.out.println("Warning: You have exceeded your budget for " + month);
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
