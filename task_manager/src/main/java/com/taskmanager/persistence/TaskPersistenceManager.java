package com.taskmanager.persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TaskPersistenceManager {
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

    public static void saveTaskLists(Map<String, TodoTaskCollection> taskLists) {
        try {
            StringBuilder json = new StringBuilder("{\n");
            List<String> listNames = new ArrayList<>(taskLists.keySet());

            for (int i = 0; i < listNames.size(); i++) {
                String listName = listNames.get(i);
                TodoTaskCollection list = taskLists.get(listName);
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

    public static Map<String, TodoTaskCollection> loadTaskLists() {
        Map<String, TodoTaskCollection> taskLists = new LinkedHashMap<>();
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

    public static void createBackup(Map<String, TodoTaskCollection> taskLists) {
        try {
            long timestamp = System.currentTimeMillis();
            String backupFile = BACKUPS_DIR + "backup_" + timestamp + ".json";

            StringBuilder json = new StringBuilder("{\n");
            List<String> listNames = new ArrayList<>(taskLists.keySet());

            for (int i = 0; i < listNames.size(); i++) {
                String listName = listNames.get(i);
                TodoTaskCollection list = taskLists.get(listName);
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

    public static void archiveCompletedTasks(Map<String, TodoTaskCollection> taskLists) {
        try {
            List<TodoTask> archivedTasks = new ArrayList<>();

            for (TodoTaskCollection list : taskLists.values()) {
                Iterator<TodoTask> iterator = list.getTasks().iterator();
                while (iterator.hasNext()) {
                    TodoTask task = iterator.next();
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

    public static void exportToCSV(Map<String, TodoTaskCollection> taskLists, String filename) {
        try {
            StringBuilder csv = new StringBuilder("Subject,Task,Priority,Due Date,Completed,Notes\n");

            for (TodoTaskCollection list : taskLists.values()) {
                for (TodoTask task : list.getTasks()) {
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

    private static String taskListToJson(TodoTaskCollection list) {
        StringBuilder json = new StringBuilder("{\n");
        json.append("    \"name\": \"").append(escapeJson(list.getName())).append("\",\n");
        json.append("    \"description\": \"").append(escapeJson(list.getDescription())).append("\",\n");
        json.append("    \"createdAt\": ").append(list.getCreatedAt()).append(",\n");
        json.append("    \"lastModified\": ").append(list.getLastModified()).append(",\n");
        json.append("    \"tasks\": [\n");

        List<TodoTask> tasks = list.getTasks();
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

    private static String taskToJson(TodoTask task, int indent) {
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

    private static String tasksToJson(List<TodoTask> tasks) {
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

    private static Map<String, TodoTaskCollection> jsonToTaskLists(String json) {
        Map<String, TodoTaskCollection> result = new LinkedHashMap<>();
        try {
            if (json.trim().equals("{}")) {
                return result;
            }
            // TODO: real JSON parsing (Gson/Jackson) - just avoiding crashes for now
            return result;
        } catch (Exception e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            return result;
        }
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
