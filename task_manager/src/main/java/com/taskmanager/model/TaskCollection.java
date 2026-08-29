package com.taskmanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TodoTaskCollection implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private List<TodoTask> tasks;

    private long createdAt;

    private long lastModified;

    public TodoTaskCollection(String name) {
        this.name = name;
        this.description = "";
        this.tasks = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateLastModifiedTime();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        updateLastModifiedTime();
    }

    public List<TodoTask> getTasks() {
        return tasks;
    }

    public void addTask(TodoTask task) {
        tasks.add(task);
        updateLastModifiedTime();
    }

    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            updateLastModifiedTime();
        }
    }

    public TodoTask getTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            return tasks.get(index);
        }
        return null;
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public int getCompletedCount() {
        return (int) tasks.stream().filter(TodoTask::isCompleted).count();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void updateLastModifiedTime() {
        this.lastModified = System.currentTimeMillis();
    }
}
