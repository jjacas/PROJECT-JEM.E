package com.taskmanager.model;

import java.io.Serializable;
import java.time.LocalDate;

public class TodoTask implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String subject;
    private boolean completed;
    private int priority;
    private LocalDate dueDate;
    private String notes;
    private long createdAt;
    private RecurrencePattern recurrence;
    private Reminder reminder;

    public TodoTask(String name, String subject) {
        this.name = name;
        this.subject = subject;
        this.completed = false;
        this.priority = 3;
        this.dueDate = null;
        this.notes = "";
        this.createdAt = System.currentTimeMillis();
        this.recurrence = new RecurrencePattern();
        this.reminder = new Reminder(hashCode());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if (priority >= 1 && priority <= 5) {
            this.priority = priority;
        }
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public RecurrencePattern getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(RecurrencePattern recurrence) {
        this.recurrence = recurrence;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (completed) {
            sb.append(" (done)");
        }
        sb.append(" [").append(subject).append("]");
        sb.append(" (Priority: ").append(priority).append("/5)");
        if (dueDate != null) {
            sb.append(" - Due: ").append(dueDate);
        }
        if (!notes.isEmpty()) {
            sb.append(" - Notes: ").append(notes);
        }
        return sb.toString();
    }
}
