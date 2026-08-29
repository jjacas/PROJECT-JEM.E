package com.taskmanager.search;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskSearchEngine {
    private Map<String, TodoTaskCollection> taskLists;

    public TaskSearchEngine(Map<String, TodoTaskCollection> taskLists) {
        this.taskLists = taskLists;
    }

    public List<TodoTask> searchByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> task.getName().toLowerCase().contains(lowerKeyword) ||
                               task.getNotes().toLowerCase().contains(lowerKeyword) ||
                               task.getSubject().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<TodoTask> getTasksByPriority(int priority) {
        return taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> task.getPriority() == priority && !task.isCompleted())
                .collect(Collectors.toList());
    }

    public List<TodoTask> getHighPriorityTasks() {
        return taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> task.getPriority() >= 4 && !task.isCompleted())
                .sorted((t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority()))
                .collect(Collectors.toList());
    }

    public List<TodoTask> getTasksDueOn(LocalDate date) {
        return taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> task.getDueDate() != null &&
                               task.getDueDate().equals(date) &&
                               !task.isCompleted())
                .collect(Collectors.toList());
    }

    public List<TodoTask> getOverdueTasks() {
        LocalDate today = LocalDate.now();
        return taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> task.getDueDate() != null &&
                               task.getDueDate().isBefore(today) &&
                               !task.isCompleted())
                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .collect(Collectors.toList());
    }

    public List<TodoTask> getTasksDueSoon(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(daysAhead);
        return taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(task -> task.getDueDate() != null &&
                               !task.getDueDate().isBefore(today) &&
                               !task.getDueDate().isAfter(deadline) &&
                               !task.isCompleted())
                .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                .collect(Collectors.toList());
    }

    public List<TodoTask> getCompletedTasks() {
        return taskLists.values().stream()
                .flatMap(list -> list.getTasks().stream())
                .filter(TodoTask::isCompleted)
                .collect(Collectors.toList());
    }

    public List<TodoTask> getTasksInCollection(String listName) {
        TodoTaskCollection list = taskLists.get(listName);
        return list != null ? list.getTasks() : List.of();
    }

    public void printSearchResults(List<TodoTask> results, String description) {
        if (results.isEmpty()) {
            System.out.println("No results found for: " + description);
            return;
        }

        System.out.println("\n--- Search Results: " + description + " (" + results.size() + ") ---");
        for (int i = 0; i < results.size(); i++) {
            TodoTask task = results.get(i);
            String status = task.isCompleted() ? "[DONE]" : "[ ]";
            System.out.println((i + 1) + ". " + status + " " + task);
        }
        System.out.println();
    }
}
