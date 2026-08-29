package com.taskmanager.scheduling;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaskReminderNotificationService {

    private Map<String, TodoTaskCollection> taskLists;
    private static final String LOG_FILE = "reminders.log";
    private Set<Long> notifiedTasks;

    public TaskReminderNotificationService(Map<String, TodoTaskCollection> taskLists) {
        this.taskLists = taskLists;
        this.notifiedTasks = new HashSet<>();
        loadNotificationLog();
    }

    public List<String> checkReminders() {
        List<String> alerts = new ArrayList<>();

        for (TodoTaskCollection list : taskLists.values()) {
            for (TodoTask task : list.getTasks()) {
                if (task.getReminder() != null && task.getReminder().shouldTrigger(task)) {
                    long taskId = System.identityHashCode(task);
                    if (!notifiedTasks.contains(taskId)) {
                        String alert = formatAlert(task);
                        alerts.add(alert);
                        notifiedTasks.add(taskId);
                        logReminder(task);
                    }
                }
            }
        }

        return alerts;
    }

    private String formatAlert(TodoTask task) {
        return "REMINDER: " + task.getName() +
               " [" + task.getSubject() + "] " +
               "Due: " + task.getDueDate() +
               " (Priority: " + task.getPriority() + "/5)";
    }

    private void logReminder(TodoTask task) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            bw.write("[" + now.format(formatter) + "] " + task.getName() + "\n");
        } catch (IOException e) {
            System.err.println("Error logging reminder: " + e.getMessage());
        }
    }

    private void loadNotificationLog() {
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // not parsing old log entries yet - notifiedTasks just starts empty each run
            }
        } catch (IOException e) {
            System.err.println("Error loading notification log: " + e.getMessage());
        }
    }

    public List<TodoTask> getUpcomingReminders() {
        List<TodoTask> upcoming = new ArrayList<>();

        for (TodoTaskCollection list : taskLists.values()) {
            for (TodoTask task : list.getTasks()) {
                if (task.getDueDate() != null && !task.isCompleted()) {
                    long daysUntilDue = java.time.temporal.ChronoUnit.DAYS
                            .between(java.time.LocalDate.now(), task.getDueDate());

                    if (daysUntilDue >= 0 && daysUntilDue <= 7) {
                        upcoming.add(task);
                    }
                }
            }
        }

        return upcoming;
    }
}
