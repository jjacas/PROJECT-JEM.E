package com.taskmanager.ui;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class TaskManagerTestSuite {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("  Todo List Manager - Test Suite");
        System.out.println("======================================\n");

        testTaskCreation();
        testTaskListManagement();
        testCompletionStatus();
        testRecurrencePatterns();
        testReminders();
        testSearch();
        testStatistics();
        testDataPersistence();
        testInputValidation();
        testEdgeCases();
        testPerformance();

        System.out.println("\n======================================");
        System.out.println("  Test Results");
        System.out.println("======================================");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total: " + (testsPassed + testsFailed));
        System.out.println("Rate: " + String.format("%.1f%%", (testsPassed * 100.0 / (testsPassed + testsFailed))));
        System.out.println("======================================\n");
    }

    private static void testTaskCreation() {
        System.out.println("Testing Task Creation...");
        try {
            TodoTask task = new TodoTask("Buy groceries", "Shopping");
            check(task.getName().equals("Buy groceries"));
            check(task.getSubject().equals("Shopping"));
            check(!task.isCompleted());
            check(task.getPriority() == 3);
            pass("Create basic task");

            task.setPriority(5);
            task.setDueDate(LocalDate.now().plusDays(3));
            check(task.getPriority() == 5);
            pass("Set task priority and due date");

            task.setPriority(10);
            check(task.getPriority() == 5);
            pass("Priority validation");

        } catch (Exception e) {
            fail("Task creation", e);
        }
    }

    private static void testTaskListManagement() {
        System.out.println("Testing Task List Management...");
        try {
            TodoTaskCollection list = new TodoTaskCollection("TestList");
            check(list.getName().equals("TestList"));
            check(list.getTaskCount() == 0);
            pass("Create task collection");

            TodoTask task1 = new TodoTask("Task 1", "Work");
            TodoTask task2 = new TodoTask("Task 2", "Home");
            list.addTask(task1);
            list.addTask(task2);
            check(list.getTaskCount() == 2);
            pass("Add tasks to collection");

            list.removeTask(0);
            check(list.getTaskCount() == 1);
            pass("Remove task from collection");

            task2.setCompleted(true);
            check(list.getCompletedCount() == 1);
            pass("Get completed task count");

        } catch (Exception e) {
            fail("Task collection management", e);
        }
    }

    private static void testCompletionStatus() {
        System.out.println("Testing Completion Status...");
        try {
            TodoTask task = new TodoTask("Test", "Test");
            check(!task.isCompleted());
            pass("Create incomplete task");

            task.setCompleted(true);
            check(task.isCompleted());
            pass("Mark task complete");

            task.setCompleted(false);
            check(!task.isCompleted());
            pass("Mark task incomplete");

        } catch (Exception e) {
            fail("Completion status", e);
        }
    }

    private static void testRecurrencePatterns() {
        System.out.println("Testing Recurrence Patterns...");
        try {
            TaskRecurrenceScheduler pattern = new TaskRecurrenceScheduler();
            pattern.setType(TaskRecurrenceScheduler.RecurrenceType.DAILY);
            check(pattern.getType() == TaskRecurrenceScheduler.RecurrenceType.DAILY);
            pass("Create daily recurrence");

            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            LocalDate nextOccurrence = pattern.getNextOccurrence(today);
            check(nextOccurrence.equals(tomorrow));
            pass("Daily recurrence calculation");

            pattern = new TaskRecurrenceScheduler();
            pattern.setType(TaskRecurrenceScheduler.RecurrenceType.WEEKLY);
            pattern.setDaysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
            pass("Weekly recurrence with days");

            pattern = new TaskRecurrenceScheduler();
            pattern.setType(TaskRecurrenceScheduler.RecurrenceType.MONTHLY);
            pattern.setDayOfMonth(15);
            check(pattern.getDayOfMonth() == 15);
            pass("Monthly recurrence");

        } catch (Exception e) {
            fail("Recurrence patterns", e);
        }
    }

    private static void testReminders() {
        System.out.println("Testing Reminders...");
        try {
            TaskReminder reminder = new TaskReminder(1L);
            check(reminder.getTiming() == TaskReminder.ReminderTiming.DAYS_BEFORE);
            pass("Create default reminder (1 day before)");

            reminder.setTiming(TaskReminder.ReminderTiming.HOURS_BEFORE);
            reminder.setValue(2);
            check(reminder.getValue() == 2);
            pass("Create hours-before reminder");

            check(!reminder.isNotified());
            pass("Reminder not yet notified");

        } catch (Exception e) {
            fail("Reminders", e);
        }
    }

    private static void testSearch() {
        System.out.println("Testing Search...");
        try {
            Map<String, TodoTaskCollection> taskLists = new HashMap<>();
            TodoTaskCollection list = new TodoTaskCollection("Work");
            TodoTask task1 = new TodoTask("Finish report", "Work");
            task1.setPriority(5);
            list.addTask(task1);

            TodoTask task2 = new TodoTask("Buy milk", "Shopping");
            list.addTask(task2);
            taskLists.put("Work", list);

            TaskSearchEngine search = new TaskSearchEngine(taskLists);
            List<TodoTask> results = search.searchByKeyword("report");
            check(results.size() == 1);
            check(results.get(0).getName().equals("Finish report"));
            pass("Keyword search");

            results = search.getHighPriorityTasks();
            check(results.size() == 1);
            pass("High priority filter");

        } catch (Exception e) {
            fail("Search", e);
        }
    }

    private static void testStatistics() {
        System.out.println("Testing Statistics...");
        try {
            Map<String, TodoTaskCollection> taskLists = new HashMap<>();
            TodoTaskCollection list = new TodoTaskCollection("Test");
            for (int i = 0; i < 10; i++) {
                TodoTask task = new TodoTask("Task " + i, "Test");
                list.addTask(task);
            }
            taskLists.put("Test", list);

            TaskAnalyticsDashboard stats = new TaskAnalyticsDashboard(taskLists);
            check(stats.getTotalTasks() == 10);
            pass("Total task count");

            check(stats.getActiveTasks() == 10);
            pass("Active task count");

            check(stats.getCompletionPercentage() == 0);
            pass("Completion percentage");

        } catch (Exception e) {
            fail("Statistics", e);
        }
    }

    private static void testDataPersistence() {
        System.out.println("Testing Data Persistence...");
        try {
            Map<String, TodoTaskCollection> taskLists = new HashMap<>();
            TodoTaskCollection list = new TodoTaskCollection("Test");
            TodoTask task = new TodoTask("Test task", "Test");
            list.addTask(task);
            taskLists.put("Test", list);

            TaskPersistenceManager.saveTaskLists(taskLists);
            pass("Save to JSON");

            TaskPersistenceManager.createBackup(taskLists);
            pass("Create backup");

            TaskPersistenceManager.exportToCSV(taskLists, "test_export.csv");
            pass("Export to CSV");

        } catch (Exception e) {
            fail("Data persistence", e);
        }
    }

    private static void testInputValidation() {
        System.out.println("Testing Input Validation...");
        try {
            TodoTask task = new TodoTask("Test", "Test");

            task.setPriority(-1);
            check(task.getPriority() > 0);
            pass("Negative priority rejected");

            task.setPriority(10);
            check(task.getPriority() <= 5);
            pass("Priority over 5 rejected");

        } catch (Exception e) {
            fail("Input validation", e);
        }
    }

    private static void testEdgeCases() {
        System.out.println("Testing Edge Cases...");
        try {
            TodoTaskCollection list = new TodoTaskCollection("Empty");
            check(list.getTaskCount() == 0);
            pass("Empty collection");

            TodoTask task = new TodoTask("", "");
            list.addTask(task);
            check(list.getTaskCount() == 1);
            pass("Empty properties handling");

            String longName = "A".repeat(500);
            task = new TodoTask(longName, "Test");
            check(task.getName().length() == 500);
            pass("Very long task name");

            task = new TodoTask("Test™ with special chars ñ€", "Test");
            list.addTask(task);
            pass("Special characters in name");

        } catch (Exception e) {
            fail("Edge cases", e);
        }
    }

    private static void testPerformance() {
        System.out.println("Testing Performance...");
        try {
            TodoTaskCollection list = new TodoTaskCollection("Large");
            long start = System.currentTimeMillis();

            for (int i = 0; i < 500; i++) {
                TodoTask task = new TodoTask("Task " + i, "Category " + (i % 10));
                list.addTask(task);
            }

            long elapsed = System.currentTimeMillis() - start;
            pass("Create 500 tasks (" + elapsed + "ms)");

            Map<String, TodoTaskCollection> taskLists = new HashMap<>();
            taskLists.put("Large", list);
            TaskSearchEngine search = new TaskSearchEngine(taskLists);

            start = System.currentTimeMillis();
            List<TodoTask> results = search.searchByKeyword("Task 250");
            elapsed = System.currentTimeMillis() - start;

            check(results.size() > 0);
            pass("Search 500 tasks (" + elapsed + "ms)");

        } catch (Exception e) {
            fail("Performance", e);
        }
    }

    private static void pass(String testName) {
        System.out.println("[PASS] " + testName);
        testsPassed++;
    }

    private static void fail(String testName, Exception e) {
        System.out.println("[FAIL] " + testName + " - " + e.getMessage());
        testsFailed++;
    }

    private static void check(boolean condition) throws AssertionError {
        if (!condition) {
            throw new AssertionError("Check failed");
        }
    }
}
