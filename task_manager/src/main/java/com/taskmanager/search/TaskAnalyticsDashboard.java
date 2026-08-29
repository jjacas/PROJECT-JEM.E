package com.taskmanager.search;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskAnalyticsDashboard {
    private Map<String, TodoTaskCollection> taskLists;

    public TaskAnalyticsDashboard(Map<String, TodoTaskCollection> taskLists) {
        this.taskLists = taskLists;
    }

    public int getTotalTasks() {
        return taskLists.values().stream()
                .mapToInt(TodoTaskCollection::getTaskCount)
                .sum();
    }

    public int getCompletedTasks() {
        return taskLists.values().stream()
                .mapToInt(TodoTaskCollection::getCompletedCount)
                .sum();
    }

    public int getActiveTasks() {
        return getTotalTasks() - getCompletedTasks();
    }

    public double getCompletionPercentage() {
        int total = getTotalTasks();
        if (total == 0) return 0;
        return (double) getCompletedTasks() / total * 100;
    }

    public int getOverdueTasks() {
        LocalDate today = LocalDate.now();
        return (int) taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> !task.isCompleted() &&
                               task.getDueDate() != null &&
                               task.getDueDate().isBefore(today))
                .count();
    }

    public int getHighPriorityTasks() {
        return (int) taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> !task.isCompleted() && task.getPriority() >= 4)
                .count();
    }

    public Map<String, Integer> getTasksBySubject() {
        return taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> !task.isCompleted())
                .collect(Collectors.groupingBy(
                        TodoTask::getSubject,
                        Collectors.summingInt(t -> 1)
                ));
    }

    public Map<Integer, Integer> getTasksByPriority() {
        return taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> !task.isCompleted())
                .collect(Collectors.groupingBy(
                        TodoTask::getPriority,
                        Collectors.summingInt(t -> 1)
                ));
    }

    public void printStatistics() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           Task Statistics              ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Total Tasks: " + getTotalTasks());
        System.out.println("Completed: " + getCompletedTasks());
        System.out.println("Active: " + getActiveTasks());
        System.out.printf("Completion Rate: %.1f%%\n", getCompletionPercentage());
        System.out.println("Overdue Tasks: " + getOverdueTasks());
        System.out.println("High Priority (4-5): " + getHighPriorityTasks());

        System.out.println("\n--- Tasks by Subject ---");
        Map<String, Integer> bySubject = getTasksBySubject();
        if (bySubject.isEmpty()) {
            System.out.println("No active tasks.");
        } else {
            bySubject.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .forEach(entry ->
                        System.out.println("  " + entry.getKey() + ": " + entry.getValue())
                    );
        }

        System.out.println("\n--- Tasks by Priority ---");
        Map<Integer, Integer> byPriority = getTasksByPriority();
        for (int p = 5; p >= 1; p--) {
            int count = byPriority.getOrDefault(p, 0);
            System.out.println("  Priority " + p + ": " + count);
        }
        System.out.println();
    }
}
