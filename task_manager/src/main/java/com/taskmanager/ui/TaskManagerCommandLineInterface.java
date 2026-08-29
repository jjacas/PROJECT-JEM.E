package com.taskmanager.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TaskManagerCommandLineInterface {

    private Map<String, TodoTaskCollection> taskLists;

    private String currentListName;

    private final Scanner scanner;

    private TaskSearchEngine searchEngine;

    private TaskAnalyticsDashboard statistics;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TaskManagerCommandLineInterface() {
        this.taskLists = TaskPersistenceManager.loadTaskLists();
        if (this.taskLists.isEmpty()) {
            this.taskLists.put("Default", new TodoTaskCollection("Default"));
            this.currentListName = "Default";
        } else {
            this.currentListName = this.taskLists.keySet().iterator().next();
        }
        this.scanner = new Scanner(System.in);
        this.searchEngine = new TaskSearchEngine(taskLists);
        this.statistics = new TaskAnalyticsDashboard(taskLists);
    }

    private TodoTaskCollection getCurrentList() {
        return taskLists.get(currentListName);
    }

    private boolean isValidIndex(int index, TodoTaskCollection list) {
        return index >= 0 && index < list.getTaskCount();
    }

    private int getIntInput() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.print("Invalid input. Please enter a number: ");
            return getIntInput();
        }
    }

    public void addTask(String name, String subject) {
        TodoTaskCollection list = getCurrentList();
        TodoTask task = new TodoTask(name, subject);
        list.addTask(task);
        TaskPersistenceManager.saveTaskLists(taskLists);
        System.out.println("Task added to [" + currentListName + "]: " + name);
    }

    public void removeTask(int index) {
        TodoTaskCollection list = getCurrentList();
        if (isValidIndex(index, list)) {
            String removed = list.getTasks().get(index).getName();
            list.removeTask(index);
            TaskPersistenceManager.saveTaskLists(taskLists);
            System.out.println("Task removed: " + removed);
        } else {
            System.out.println("Invalid task number.");
        }
    }

    public void markTaskComplete(int index) {
        TodoTaskCollection list = getCurrentList();
        if (isValidIndex(index, list)) {
            TodoTask task = list.getTasks().get(index);
            task.setCompleted(!task.isCompleted());
            String status = task.isCompleted() ? "marked complete" : "marked incomplete";
            System.out.println("Task " + status + ": " + task.getName());
            list.updateLastModifiedTime();
            TaskPersistenceManager.saveTaskLists(taskLists);
        } else {
            System.out.println("Invalid task number.");
        }
    }

    public void editTask(int index) {
        TodoTaskCollection list = getCurrentList();
        if (!isValidIndex(index, list)) {
            System.out.println("Invalid task number.");
            return;
        }

        TodoTask task = list.getTasks().get(index);
        System.out.println("\nEditing: " + task.getName());
        System.out.println("1. Edit name");
        System.out.println("2. Edit subject");
        System.out.println("3. Set priority (1-5)");
        System.out.println("4. Set due date (yyyy-MM-dd)");
        System.out.println("5. Add notes");
        System.out.println("0. Cancel");
        System.out.print("Choose option: ");

        int choice = getIntInput();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("New name: ");
                task.setName(scanner.nextLine());
                break;
            case 2:
                System.out.print("New subject: ");
                task.setSubject(scanner.nextLine());
                break;
            case 3:
                System.out.print("Priority (1-5): ");
                task.setPriority(getIntInput());
                scanner.nextLine();
                break;
            case 4:
                System.out.print("Due date (yyyy-MM-dd): ");
                try {
                    String dateStr = scanner.nextLine();
                    task.setDueDate(LocalDate.parse(dateStr, DATE_FORMATTER));
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                }
                break;
            case 5:
                System.out.print("Notes: ");
                task.setNotes(scanner.nextLine());
                break;
            case 0:
                System.out.println("Cancelled.");
                return;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        TaskPersistenceManager.saveTaskLists(taskLists);
        System.out.println("Task updated.");
    }

    public void createNewList() {
        System.out.print("New list name: ");
        String name = scanner.nextLine();

        if (taskLists.containsKey(name)) {
            System.out.println("List already exists.");
            return;
        }

        TodoTaskCollection newList = new TodoTaskCollection(name);
        taskLists.put(name, newList);
        TaskPersistenceManager.saveTaskLists(taskLists);
        System.out.println("List created: " + name);
    }

    public void switchList() {
        System.out.println("\nAvailable lists:");
        List<String> listNames = new ArrayList<>(taskLists.keySet());
        for (int i = 0; i < listNames.size(); i++) {
            String marker = listNames.get(i).equals(currentListName) ? " [current]" : "";
            System.out.println((i + 1) + ". " + listNames.get(i) + marker);
        }

        System.out.print("Select list: ");
        int choice = getIntInput() - 1;
        scanner.nextLine();

        if (choice >= 0 && choice < listNames.size()) {
            currentListName = listNames.get(choice);
            System.out.println("Switched to: " + currentListName);
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public void listTasks() {
        TodoTaskCollection list = getCurrentList();
        System.out.println("\n--- Tasks in [" + currentListName + "] (" + list.getTaskCount() + " total) ---");

        List<TodoTask> tasks = list.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks.");
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            TodoTask task = tasks.get(i);
            String status = task.isCompleted() ? "[]" : "[ ]";
            System.out.println((i + 1) + ". " + status + " " + task);
        }
        System.out.println();
    }

    public void listTasksBySubject() {
        System.out.println("\n--- Tasks by Subject in [" + currentListName + "] ---\n");

        Map<String, List<TodoTask>> bySubject = new LinkedHashMap<>();
        for (TodoTask task : getCurrentList().getTasks()) {
            bySubject.computeIfAbsent(task.getSubject(), k -> new ArrayList<>()).add(task);
        }

        for (Map.Entry<String, List<TodoTask>> entry : bySubject.entrySet()) {
            System.out.println("--- " + entry.getKey() + " ---");
            List<TodoTask> filtered = entry.getValue();
            for (int i = 0; i < filtered.size(); i++) {
                TodoTask task = filtered.get(i);
                String status = task.isCompleted() ? "[]" : "[ ]";
                System.out.println((i + 1) + ". " + status + " " + task);
            }
            System.out.println();
        }
    }

    public void searchTasks() {
        System.out.println("\n--- Search Options ---");
        System.out.println("1. Search by keyword");
        System.out.println("2. View high priority tasks");
        System.out.println("3. View overdue tasks");
        System.out.println("4. View tasks due soon (7 days)");
        System.out.println("5. View completed tasks");
        System.out.print("Choose option: ");

        int choice = getIntInput();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Keyword: ");
                List<TodoTask> results = searchEngine.searchByKeyword(scanner.nextLine());
                searchEngine.printSearchResults(results, "Keyword search");
                break;
            case 2:
                List<TodoTask> highPriority = searchEngine.getHighPriorityTasks();
                searchEngine.printSearchResults(highPriority, "High Priority Tasks");
                break;
            case 3:
                List<TodoTask> overdue = searchEngine.getOverdueTasks();
                searchEngine.printSearchResults(overdue, "Overdue Tasks");
                break;
            case 4:
                List<TodoTask> dueSoon = searchEngine.getTasksDueSoon(7);
                searchEngine.printSearchResults(dueSoon, "Due in 7 Days");
                break;
            case 5:
                List<TodoTask> completed = searchEngine.getCompletedTasks();
                searchEngine.printSearchResults(completed, "Completed Tasks");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    public void showStatistics() {
        statistics = new TaskAnalyticsDashboard(taskLists);
        statistics.printStatistics();
    }

    public void createBackup() {
        TaskPersistenceManager.createBackup(taskLists);
    }

    public void archiveCompletedTasks() {
        TaskPersistenceManager.archiveCompletedTasks(taskLists);
    }

    public void exportToCSV() {
        String filename = "tasks_export_" + System.currentTimeMillis() + ".csv";
        TaskPersistenceManager.exportToCSV(taskLists, filename);
        System.out.println("Exported to: " + filename);
    }

    public void run() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   Todo List Manager CLI Interface      ║");
        System.out.println("╚════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            System.out.println("\n[" + currentListName + "] Main Menu");
            System.out.println("--- Task Management (1-6) ---");
            System.out.println("1. Add task");
            System.out.println("2. View tasks");
            System.out.println("3. View by subject");
            System.out.println("4. Mark complete/incomplete");
            System.out.println("5. Edit task");
            System.out.println("6. Remove task");
            System.out.println("--- Search & Filter (7-8) ---");
            System.out.println("7. Search tasks");
            System.out.println("8. View statistics");
            System.out.println("--- Manage Lists (9-10) ---");
            System.out.println("9. Create new list");
            System.out.println("10. Switch list");
            System.out.println("--- Data Management (11-13) ---");
            System.out.println("11. Backup data");
            System.out.println("12. Archive completed tasks");
            System.out.println("13. Export to CSV");
            System.out.println("14. Exit");
            System.out.print("Choose option: ");

            int choice = getIntInput();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Task name: ");
                    String name = scanner.nextLine();
                    System.out.print("Subject: ");
                    String subject = scanner.nextLine();
                    addTask(name, subject);
                    break;

                case 2:
                    listTasks();
                    break;

                case 3:
                    listTasksBySubject();
                    break;

                case 4:
                    listTasks();
                    System.out.print("Task number: ");
                    markTaskComplete(getIntInput() - 1);
                    scanner.nextLine();
                    break;

                case 5:
                    listTasks();
                    System.out.print("Task number: ");
                    editTask(getIntInput() - 1);
                    break;

                case 6:
                    listTasks();
                    System.out.print("Task number: ");
                    removeTask(getIntInput() - 1);
                    scanner.nextLine();
                    break;

                case 7:
                    searchTasks();
                    break;

                case 8:
                    showStatistics();
                    break;

                case 9:
                    createNewList();
                    break;

                case 10:
                    switchList();
                    break;

                case 11:
                    createBackup();
                    break;

                case 12:
                    archiveCompletedTasks();
                    break;

                case 13:
                    exportToCSV();
                    break;

                case 14:
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public static void main(String[] args) {
        TaskManagerCommandLineInterface cli = new TaskManagerCommandLineInterface();
        cli.run();
    }
}
