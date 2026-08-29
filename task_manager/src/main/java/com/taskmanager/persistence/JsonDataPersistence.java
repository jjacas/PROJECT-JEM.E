package com.taskmanager.persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonDataPersistence {
    private static final String DATA_DIR = "data/";
    private static final String TASKS_FILE = DATA_DIR + "tasks.json";
    private static final String ARCHIVES_FILE = DATA_DIR + "archives.json";
    private static final String BACKUPS_DIR = DATA_DIR + "backups/";

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(BACKUPS_DIR));
        } catch (IOException e) {
            System.err.println("Error creating data directories: " + e.getMessage());
        }
    }

    // Save task lists to JSON
    public static void saveTaskLists(Map<String, TaskList> taskLists) {
        try {
            StringBuilder json = new StringBuilder("{\n");
            List<String> listNames = new ArrayList<>(taskLists.keySet());

            for (int i = 0; i < listNames.size(); i++) {
                String listName = listNames.get(i);
                TaskList list = taskLists.get(listName);
                json.append("  \"").append(escapeJson(listName)).append("\": ");
                json.append(taskListToJson(list));
                if (i < listNames.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("}\n");

            Files.write(Paths.get(TASKS_FILE), json.toString().getBytes());
            System.out.println("Task lists saved.");
        } catch (IOException e) {
            System.err.println("Error saving task lists: " + e.getMessage());
        }
    }

    // Load task lists from JSON
    public static Map<String, TaskList> loadTaskLists() {
        Map<String, TaskList> taskLists = new LinkedHashMap<>();
        File file = new File(TASKS_FILE);

        if (!file.exists()) {
            return taskLists;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(TASKS_FILE)));
            taskLists = jsonToTaskLists(content);
        } catch (IOException e) {
            System.err.println("Error loading task lists: " + e.getMessage());
        }

        return taskLists;
    }

    // Create backup of current tasks
    public static void createBackup(Map<String, TaskList> taskLists) {
        try {
            long timestamp = System.currentTimeMillis();
            String backupFile = BACKUPS_DIR + "backup_" + timestamp + ".json";

            StringBuilder json = new StringBuilder("{\n");
            List<String> listNames = new ArrayList<>(taskLists.keySet());

            for (int i = 0; i < listNames.size(); i++) {
                String listName = listNames.get(i);
                TaskList list = taskLists.get(listName);
                json.append("  \"").append(escapeJson(listName)).append("\": ");
                json.append(taskListToJson(list));
                if (i < listNames.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("}\n");

            Files.write(Paths.get(backupFile), json.toString().getBytes());
            System.out.println("Backup created: " + backupFile);
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }

    // Archive completed tasks
    public static void archiveCompletedTasks(Map<String, TaskList> taskLists) {
        try {
            List<Task> archivedTasks = new ArrayList<>();

            for (TaskList list : taskLists.values()) {
                Iterator<Task> iterator = list.getTasks().iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (task.isCompleted()) {
                        archivedTasks.add(task);
                        iterator.remove();
                    }
                }
            }

            if (!archivedTasks.isEmpty()) {
                String json = tasksToJson(archivedTasks);
                String existingContent = "";

                File archiveFile = new File(ARCHIVES_FILE);
                if (archiveFile.exists()) {
                    existingContent = new String(Files.readAllBytes(Paths.get(ARCHIVES_FILE)));
                }

                String combined = existingContent.isEmpty() ? json :
                    existingContent.substring(0, existingContent.length() - 1) + ",\n" +
                    json.substring(1);

                Files.write(Paths.get(ARCHIVES_FILE), combined.getBytes());
                saveTaskLists(taskLists);
                System.out.println("Archived " + archivedTasks.size() + " completed task(s).");
            }
        } catch (IOException e) {
            System.err.println("Error archiving tasks: " + e.getMessage());
        }
    }

    // Export lists to readable format
    public static void exportToCSV(Map<String, TaskList> taskLists, String filename) {
        try {
            StringBuilder csv = new StringBuilder("Subject,Task,Priority,Due Date,Completed,Notes\n");

            for (TaskList list : taskLists.values()) {
                for (Task task : list.getTasks()) {
                    csv.append("\"").append(task.getSubject()).append("\",");
                    csv.append("\"").append(task.getName()).append("\",");
                    csv.append(task.getPriority()).append(",");
                    csv.append(task.getDueDate() != null ? task.getDueDate() : "").append(",");
                    csv.append(task.isCompleted() ? "Yes" : "No").append(",");
                    csv.append("\"").append(task.getNotes()).append("\"\n");
                }
            }

            Files.write(Paths.get(filename), csv.toString().getBytes());
            System.out.println("Exported to: " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
        }
    }

    // Helper: Convert TaskList to JSON
    private static String taskListToJson(TaskList list) {
        StringBuilder json = new StringBuilder("{\n");
        json.append("    \"name\": \"").append(escapeJson(list.getName())).append("\",\n");
        json.append("    \"description\": \"").append(escapeJson(list.getDescription())).append("\",\n");
        json.append("    \"createdAt\": ").append(list.getCreatedAt()).append(",\n");
        json.append("    \"lastModified\": ").append(list.getLastModified()).append(",\n");
        json.append("    \"tasks\": [\n");

        List<Task> tasks = list.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            json.append(taskToJson(tasks.get(i), 6));
            if (i < tasks.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("    ]\n");
        json.append("  }");
        return json.toString();
    }

    // Helper: Convert Task to JSON
    private static String taskToJson(Task task, int indent) {
        String spaces = " ".repeat(indent);
        StringBuilder json = new StringBuilder(spaces).append("{\n");
        json.append(spaces).append("  \"name\": \"").append(escapeJson(task.getName())).append("\",\n");
        json.append(spaces).append("  \"subject\": \"").append(escapeJson(task.getSubject())).append("\",\n");
        json.append(spaces).append("  \"completed\": ").append(task.isCompleted()).append(",\n");
        json.append(spaces).append("  \"priority\": ").append(task.getPriority()).append(",\n");
        json.append(spaces).append("  \"dueDate\": ");
        if (task.getDueDate() != null) {
            json.append("\"").append(task.getDueDate()).append("\"");
        } else {
            json.append("null");
        }
        json.append(",\n");
        json.append(spaces).append("  \"notes\": \"").append(escapeJson(task.getNotes())).append("\",\n");
        json.append(spaces).append("  \"createdAt\": ").append(task.getCreatedAt()).append("\n");
        json.append(spaces).append("}");
        return json.toString();
    }

    // Helper: Convert tasks list to JSON
    private static String tasksToJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            json.append(taskToJson(tasks.get(i), 2));
            if (i < tasks.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");
        return json.toString();
    }

    // Helper: Parse JSON to TaskLists
    private static Map<String, TaskList> jsonToTaskLists(String json) {
        Map<String, TaskList> result = new LinkedHashMap<>();
        // Simple JSON parsing - in production, use Gson or Jackson
        // For now, this is a placeholder for proper JSON parsing
        try {
            // This is a simplified parser - for complex JSON, use a library
            if (json.trim().equals("{}")) {
                return result;
            }
            // Real implementation would parse JSON properly
            // For now, return empty to avoid parsing errors
            return result;
        } catch (Exception e) {
            return result;
        }
    }

    // Helper: Escape JSON strings
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
