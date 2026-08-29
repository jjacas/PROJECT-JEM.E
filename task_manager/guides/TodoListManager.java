import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

class Task {
    private String name;
    private String subject;
    private boolean completed;
    private int priority;
    private LocalDate dueDate;
    private String notes;

    public Task(String name, String subject) {
        this.name = name;
        this.subject = subject;
        this.completed = false;
        this.priority = 3;
        this.notes = "";
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return name + " [" + subject + "]" + (dueDate != null ? " Due: " + dueDate : "");
    }
}

class DataPersistence {
    private static final String FILE_NAME = "tasks.txt";

    public static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(FILE_NAME);
            if (!java.nio.file.Files.exists(path)) {
                return tasks;
            }

            List<String> lines = java.nio.file.Files.readAllLines(path);
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length < 6) {
                    continue;
                }

                Task task = new Task(parts[0], parts[1]);
                task.setCompleted(Boolean.parseBoolean(parts[2]));
                try {
                    task.setPriority(Integer.parseInt(parts[3]));
                } catch (NumberFormatException ignored) {
                    task.setPriority(3);
                }

                if (!parts[4].isEmpty()) {
                    try {
                        task.setDueDate(LocalDate.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE));
                    } catch (DateTimeParseException ignored) {
                        task.setDueDate(null);
                    }
                }
                task.setNotes(parts[5]);
                tasks.add(task);
            }
        } catch (java.io.IOException e) {
            System.out.println("Unable to load tasks.");
        }
        return tasks;
    }

    public static void saveTasks(List<Task> tasks) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(FILE_NAME);
            List<String> lines = new ArrayList<>();

            for (Task task : tasks) {
                String dueDate = task.getDueDate() == null ? "" : task.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
                lines.add(String.join("|",
                        task.getName(),
                        task.getSubject(),
                        String.valueOf(task.isCompleted()),
                        String.valueOf(task.getPriority()),
                        dueDate,
                        task.getNotes()));
            }

            java.nio.file.Files.write(path, lines);
        } catch (java.io.IOException e) {
            System.out.println("Unable to save tasks.");
        }
    }
}

public class TodoListManager {
    private final List<Task> tasks;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TodoListManager() {
        this.tasks = DataPersistence.loadTasks();
        this.scanner = new Scanner(System.in);
    }

    public void addTask(String name, String subject) {
        Task task = new Task(name, subject);
        tasks.add(task);
        System.out.println("Task added: " + name + " [" + subject + "]");
    }

    public void removeTask(int index) {
        if (isValidIndex(index)) {
            String removed = tasks.remove(index).getName();
            System.out.println("Task removed: " + removed);
            DataPersistence.saveTasks(tasks);
        } else {
            System.out.println("Invalid task number.");
        }
    }

    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks in your list.");
            return;
        }
        System.out.println("\n--- Your Tasks ---");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String status = task.isCompleted() ? "[DONE]" : "[ ]";
            System.out.println((i + 1) + ". " + status + " " + task);
        }
        System.out.println();
    }

    public void listTasksBySubject(String subject) {
        List<Task> filtered = tasks.stream()
                .filter(t -> t.getSubject().equalsIgnoreCase(subject))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No tasks found in subject: " + subject);
            return;
        }

        System.out.println("\n--- Tasks in '" + subject + "' ---");
        for (int i = 0; i < filtered.size(); i++) {
            Task task = filtered.get(i);
            String status = task.isCompleted() ? "[DONE]" : "[ ]";
            System.out.println((i + 1) + ". " + status + " " + task);
        }
        System.out.println();
    }

    public void listSubjects() {
        Set<String> subjects = tasks.stream()
                .map(Task::getSubject)
                .collect(Collectors.toSet());

        if (subjects.isEmpty()) {
            System.out.println("No subjects yet.");
            return;
        }

        System.out.println("\n--- Subjects ---");
        int i = 1;
        for (String subject : subjects) {
            long count = tasks.stream().filter(t -> t.getSubject().equalsIgnoreCase(subject)).count();
            System.out.println(i + ". " + subject + " (" + count + " tasks)");
            i++;
        }
        System.out.println();
    }

    public void markTaskComplete(int index) {
        if (isValidIndex(index)) {
            Task task = tasks.get(index);
            task.setCompleted(!task.isCompleted());
            String status = task.isCompleted() ? "marked complete" : "marked incomplete";
            System.out.println("Task " + status + ": " + task.getName());
            DataPersistence.saveTasks(tasks);
        } else {
            System.out.println("Invalid task number.");
        }
    }

    public void editTask(int index) {
        if (!isValidIndex(index)) {
            System.out.println("Invalid task number.");
            return;
        }

        Task task = tasks.get(index);
        System.out.println("\nEditing: " + task.getName());
        System.out.println("1. Edit name");
        System.out.println("2. Edit subject");
        System.out.println("3. Set priority (1-5)");
        System.out.println("4. Set due date (yyyy-MM-dd)");
        System.out.println("5. Add notes");
        System.out.println("0. Cancel");
        System.out.print("Choose option: ");

        int choice = getIntInput();
        scanner.nextLine(); // consume newline

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
                int priority = getIntInput();
                scanner.nextLine();
                task.setPriority(priority);
                break;
            case 4:
                System.out.print("Due date (yyyy-MM-dd): ");
                try {
                    LocalDate date = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);
                    task.setDueDate(date);
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

        System.out.println("Task updated.");
        DataPersistence.saveTasks(tasks);
    }

    public void run() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     Welcome to Todo List Manager       ║");
        System.out.println("╚════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Add task");
            System.out.println("2. View all tasks");
            System.out.println("3. View tasks by subject");
            System.out.println("4. List subjects");
            System.out.println("5. Mark task complete/incomplete");
            System.out.println("6. Edit task");
            System.out.println("7. Remove task");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Task name: ");
                    String taskName = scanner.nextLine();
                    System.out.print("Subject/Category: ");
                    String subject = scanner.nextLine();
                    addTask(taskName, subject);
                    break;

                case 2:
                    listTasks();
                    break;

                case 3:
                    System.out.print("Enter subject: ");
                    listTasksBySubject(scanner.nextLine());
                    break;

                case 4:
                    listSubjects();
                    break;

                case 5:
                    listTasks();
                    System.out.print("Enter task number to toggle: ");
                    int completeIndex = getIntInput() - 1;
                    scanner.nextLine();
                    markTaskComplete(completeIndex);
                    break;

                case 6:
                    listTasks();
                    System.out.print("Enter task number to edit: ");
                    int editIndex = getIntInput() - 1;
                    scanner.nextLine();
                    editTask(editIndex);
                    break;

                case 7:
                    listTasks();
                    System.out.print("Enter task number to remove: ");
                    int removeIndex = getIntInput() - 1;
                    scanner.nextLine();
                    removeTask(removeIndex);
                    break;

                case 8:
                    DataPersistence.saveTasks(tasks);
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
        scanner.close();
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    private int getIntInput() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine(); // clear invalid input
            return -1;
        }
    }
}
