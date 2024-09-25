package com.fajdev.TaskTracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * The TaskTracker class provides a command-line tool for managing tasks. It allows users to add, update, delete,
 * and list tasks which are stored in a JSON file.
 */
public class TaskTracker {
    private static final String TASKS_FILE = "tasks.json";
    private static final LocalDateTime now = LocalDateTime.now();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a command (add, update, delete, list, etc.)");
            return;
        }

        String command = args[0];

        switch (command) {
            case "add":
                if (args.length < 2) {
                    System.out.println("Please provide a task name.");
                } else {
                    addTask(args[1]);
                }
                break;

            case "update":
                if (args.length < 3) {
                    System.out.println("Please provide a task ID and new status (in-progress/done).");
                } else {
                    updateTaskStatus(args[1], args[2]);
                }
                break;

            case "delete":
                if (args.length < 2) {
                    System.out.println("Please provide a task ID.");
                } else {
                    deleteTask(args[1]);
                }
                break;

            case "list":
                if (args.length == 1) {
                    listTasks("all");
                } else {
                    listTasks(args[1]);
                }
                break;

            default:
                System.out.println("Invalid command.");
        }
    }

    // Adds a new task to the JSON file
    private static void addTask(String taskName) {
        JSONArray tasks = loadTasks();
        JSONObject newTask = new JSONObject();
        newTask.put("id", UUID.randomUUID().toString());
        newTask.put("name", taskName);
        newTask.put("status", "not done");
        newTask.put("createdAt", now.toString());
        newTask.put("updatedAt", now.toString());

        tasks.put(newTask);
        saveTasks(tasks);
        System.out.println("Task added: " + taskName);
    }

    // Updates a task's status
    private static void updateTaskStatus(String taskId, String newStatus) {
        JSONArray tasks = loadTasks();
        boolean updated = false;

        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            if (task.getString("id").equals(taskId)) {
                task.put("status", newStatus);
                task.put("updatedAt", now.toString());
                updated = true;
                break;
            }
        }

        if (updated) {
            saveTasks(tasks);
            System.out.println("Task " + taskId + " updated to " + newStatus);
        } else {
            System.out.println("Task not found.");
        }
    }

    // Deletes a task
    private static void deleteTask(String taskId) {
        JSONArray tasks = loadTasks();
        boolean removed = false;

        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            if (task.getString("id").equals(taskId)) {
                tasks.remove(i);
                removed = true;
                break;
            }
        }

        if (removed) {
            saveTasks(tasks);
            System.out.println("Task " + taskId + " deleted.");
        } else {
            System.out.println("Task not found.");
        }
    }

    // Lists tasks
    private static void listTasks(String filter) {
        JSONArray tasks = loadTasks();

        System.out.println("Tasks:");
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            String status = task.getString("status");

            if (filter.equals("all") || status.equals(filter)) {
                System.out.println("Task ID: " + task.getString("id"));
                System.out.println("Name: " + task.getString("name"));
                System.out.println("Status: " + status);
                System.out.println("Created At: " + task.getString("createdAt"));
                System.out.println("Updated At: " + task.getString("updatedAt"));
                System.out.println("----------");
            }
        }
    }

    // Loads tasks from the JSON file
    private static JSONArray loadTasks() {
        JSONArray tasks = new JSONArray();
        try {
            File file = new File(TASKS_FILE);
            Path path = Paths.get(TASKS_FILE);
            if (!file.exists()) {
                file.createNewFile();
                Files.write(path, "[]".getBytes());
            }

            String content = new String(Files.readAllBytes(path));
            tasks = new JSONArray(content);
        } catch (Exception e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    // Saves tasks to the JSON file
    private static void saveTasks(JSONArray tasks) {
        try (FileWriter file = new FileWriter(TASKS_FILE)) {
            file.write(tasks.toString());
            file.flush();
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }
}
