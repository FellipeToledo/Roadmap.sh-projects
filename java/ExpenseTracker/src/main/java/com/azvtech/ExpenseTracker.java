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


 // ExpenseTracker is a command-line application used to track and manage expenses.
 // It supports adding new expenses and displaying a summary of all recorded expenses.
 // The expenses are persisted in a JSON file.

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
     * Flag to indicate whether to show the summary of expenses in the ExpenseTracker.
     * If this flag is set to true, a summary of the user's expenses will be displayed.
     * It can be set using the command line arguments "--summary" or "-s".
     */
    @Parameter(names = {"--summary", "-s"}, description = "Show the summary of expenses")
    boolean showSummary = false;

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
     * A list of recorded expenses.
     * Each expense is represented by an instance of the {@link Expense} class.
     * This list is used to add new expenses, load expenses from a file, and save expenses to a file.
     */
    private final List<Expense> expenses = new ArrayList<>();

    /**
     * An instance of Gson used for serializing and deserializing Expense objects.
     * It helps convert Expense objects to JSON format and vice versa.
     * This variable is utilized in methods dealing with loading and saving expenses
     * from and to a JSON file.
     */
    private final Gson gson = new Gson();

    /**
     * The entry point of the ExpenseTracker application.
     * This method parses command-line arguments to perform various actions
     * such as loading expenses, adding new expenses, saving expenses, and showing expense summary.
     *
     * @param args Command-line arguments passed to the application
     *             Possible arguments include flags for adding expenses, showing summary, and displaying help.
     */
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

    /**
     * Adds a new expense to the expenses list by parsing the amount and description
     * from the addExpense list. If the parsing of the amount fails, it catches
     * a NumberFormatException and prints an error message.
     *
     * This method expects the addExpense list to contain at least two elements:
     * - first element: a string representing the amount (which should be parsable into a double)
     * - second element: a string representing the description of the expense
     *
     * It then creates a new Expense object and adds it to the expenses list.
     *
     * Assumptions:
     * - addExpense is a list containing at least two elements.
     * - expenses is a list that is already instantiated and available for manipulation.
     *
     * Error Handling:
     * - Prints an error message if the amount is not a valid number.
     */
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
            System.out.println(expense);
            total += expense.getAmount();
        }
        System.out.println("Total: " + total);
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
