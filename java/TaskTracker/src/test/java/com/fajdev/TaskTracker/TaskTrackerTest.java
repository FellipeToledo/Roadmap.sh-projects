package com.fajdev.TaskTracker;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



/**
 * Test class for TaskTracker.
 *
 * Contains unit tests for various commands supported by the TaskTracker application.
 */
public class TaskTrackerTest {

    /**
     * Retrieves the file path for the task file used in the TaskTracker.
     *
     * @return the Path object representing the file path of the TASKS_FILE.
     * @throws RuntimeException if there is an error accessing the TASKS_FILE field.
     */
    private static Path getTaskFilePath() {
        try {
            Field field = TaskTracker.class.getDeclaredField("TASKS_FILE");
            field.setAccessible(true);
            return Path.of((String) field.get(null));
        } catch (Exception e) {
            throw new RuntimeException("Unable to access TASKS_FILE", e);
        }
    }

    private static final Path TASKS_FILE_PATH = getTaskFilePath();
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeAll
    static void setUp() throws Exception {
        if (Files.exists(TASKS_FILE_PATH)) {
            Files.delete(TASKS_FILE_PATH);
        }
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    /**
     * Tests the `main` method of the `TaskTracker` class with no arguments.
     *
     * This test verifies that when no arguments are passed to the `main` method,
     * the system outputs the appropriate message indicating that a command needs
     * to be provided.
     *
     * Steps:
     * Redirects the standard output to a custom PrintStream for capturing output.
     * Invokes the `main` method of `TaskTracker` with an empty array of arguments.
     * Asserts that the captured output matches the expected message.
     */
    @Test
    public void testMainWithoutArgs() {
        System.setOut(new PrintStream(outputStreamCaptor));
        TaskTracker.main(new String[]{});
        assertEquals("Please provide a command (add, update, delete, list, etc.)", outputStreamCaptor.toString().trim());
    }

    /**
     * Tests the `main` method of the `TaskTracker` class when the `add` command is
     * provided without a task name.
     *
     * This test verifies that the `main` method correctly handles the situation where
     * the `add` command is given without any additional arguments and outputs the
     * appropriate message indicating that a task name must be provided.
     *
     * Steps:
     * 1. Redirects the standard output to a custom PrintStream for capturing output.
     * 2. Invokes the `main` method of `TaskTracker` with the `add` command.
     * 3. Asserts that the captured output matches the expected message.
     */
    @Test
    public void testMainWithAddCommandAndNoTaskName() {
        System.setOut(new PrintStream(outputStreamCaptor));
        TaskTracker.main(new String[]{"add"});
        assertEquals("Please provide a task name.", outputStreamCaptor.toString().trim());
    }

    /**
     * Tests the `main` method of the `TaskTracker` class with the `add` command and a task name.
     *
     * This test verifies that when the `add` command is provided along with a task name, the system:
     * 1. Adds the task to the task file.
     * 2. Outputs the appropriate message indicating that the task has been added.
     * 3. Confirms that the task file exists after the command is executed.
     *
     * Steps:
     * 1. Redirects the standard output to a custom PrintStream for capturing output.
     * 2. Invokes the `main` method of `TaskTracker` with the `add` command and a task name "Test Task".
     * 3. Asserts that the task file exists.
     * 4. Asserts that the captured output starts with "Task added".
     * 5. Asserts that the captured output contains the task name "Test Task".
     *
     * @throws Exception if any error occurs during the execution of the test
     */
    @Test
    public void testMainWithAddCommandAndTaskName() throws Exception {
        System.setOut(new PrintStream(outputStreamCaptor));
        TaskTracker.main(new String[]{"add", "Test Task"});
        assertTrue(Files.exists(TASKS_FILE_PATH));

        String output = outputStreamCaptor.toString().trim();
        assertTrue(output.startsWith("Task added"));
        assertTrue(output.contains("Test Task"));
    }

    /**
     * Tests the `main` method of the `TaskTracker` class with the `update` command but no ID or status provided.
     *
     * This test verifies that when the `update` command is given without both a task ID and new status,
     * the system outputs an appropriate message indicating that both the task ID and new status must be provided.
     *
     * Steps:
     * 1. Redirects the standard output to a custom PrintStream for capturing output.
     * 2. Invokes the `main` method of `TaskTracker` with the `update` command alone.
     * 3. Asserts that the captured output matches the expected message.
     */
    @Test
    public void testMainWithUpdateCommandAndNoIDOrStatus() {
        System.setOut(new PrintStream(outputStreamCaptor));
        TaskTracker.main(new String[]{"update"});
        assertEquals("Please provide a task ID and new status (in-progress/done).", outputStreamCaptor.toString().trim());
    }

    /**
     * Tests the `main` method of the `TaskTracker` class with the `update` command and a task ID but no status.
     *
     * This test ensures that when the `update` command is given with a task ID but without specifying a new status,
     * the system correctly outputs a message indicating that both a task ID and a new status must be provided.
     *
     * Steps:
     * 1. Redirects the standard output to a custom PrintStream for capturing output.
     * 2. Invokes the `main` method of `TaskTracker` with the `update` command and a task ID "123".
     * 3. Asserts that the captured output matches the expected message.
     */
    @Test
    public void testMainWithUpdateCommandAndIDButNoStatus() {
        System.setOut(new PrintStream(outputStreamCaptor));
        TaskTracker.main(new String[]{"update", "123"});
        assertEquals("Please provide a task ID and new status (in-progress/done).", outputStreamCaptor.toString().trim());
    }

    /**
     * Tests the `main` method of the `TaskTracker` class with the `delete` command but no task ID provided.
     *
     * This test verifies that when the `delete` command is given without specifying a task ID, the system
     * outputs an appropriate message indicating that a task ID must be provided.
     *
     * Steps:
     * 1. Redirects the standard output to a custom PrintStream for capturing output.
     * 2. Invokes the `main` method of `TaskTracker` with the `delete` command.
     * 3. Asserts that the captured output matches the expected message.
     */
    @Test
    public void testMainWithDeleteCommandAndNoID() {
        System.setOut(new PrintStream(outputStreamCaptor));
        TaskTracker.main(new String[]{"delete"});
        assertEquals("Please provide a task ID.", outputStreamCaptor.toString().trim());
    }

    /**
     * Tests the `main` method of the `TaskTracker` class with an invalid command.
     *
     * This test verifies that when an invalid command is passed to the `main` method,
     * the system outputs an appropriate message indicating that the command is invalid.
     *
     * Steps:
     * 1. Redirects the standard output to a custom PrintStream for capturing output.
     * 2. Invokes the `main` method of `TaskTracker` with an "invalid" command.
     * 3. Asserts that the captured output matches the expected "Invalid command." message.
     */
    @Test
    public void testMainWithInvalidCommand() {
        System.setOut(new PrintStream(outputStreamCaptor));
        TaskTracker.main(new String[]{"invalid"});
        assertEquals("Invalid command.", outputStreamCaptor.toString().trim());
    }
}