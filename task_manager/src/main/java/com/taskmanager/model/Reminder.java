package com.taskmanager.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reminder implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ReminderTiming {
        AT_TIME, MINUTES_BEFORE, HOURS_BEFORE, DAYS_BEFORE
    }

    private long taskId;  // Reference to task
    private ReminderTiming timing;
    private int value;  // Minutes, hours, or days
    private boolean enabled;
    private LocalTime reminderTime;  // Time of day for the reminder
    private boolean notified;  // Flag to track if already notified

    public Reminder(long taskId) {
        this.taskId = taskId;
        this.timing = ReminderTiming.DAYS_BEFORE;
        this.value = 1;
        this.enabled = true;
        this.reminderTime = LocalTime.of(9, 0);  // 9 AM default
        this.notified = false;
    }

    // Getters and Setters
    public long getTaskId() {
        return taskId;
    }

    public ReminderTiming getTiming() {
        return timing;
    }

    public void setTiming(ReminderTiming timing) {
        this.timing = timing;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = Math.max(0, value);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime time) {
        this.reminderTime = time;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    // Check if reminder should trigger
    public boolean shouldTrigger(TodoTask task) {
        if (!enabled || task.isCompleted() || task.getDueDate() == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = task.getDueDate();

        switch (timing) {
            case AT_TIME:
                return today.equals(dueDate);
            case MINUTES_BEFORE:
                return today.equals(dueDate);  // Simplified for CLI
            case HOURS_BEFORE:
                return today.equals(dueDate);  // Simplified for CLI
            case DAYS_BEFORE:
                return today.equals(dueDate.minusDays(value));
        }
        return false;
    }

    public String getDescription() {
        switch (timing) {
            case AT_TIME:
                return "At task time";
            case MINUTES_BEFORE:
                return value + " minute(s) before";
            case HOURS_BEFORE:
                return value + " hour(s) before";
            case DAYS_BEFORE:
                return value + " day(s) before";
            default:
                return "Unknown";
        }
    }
}
