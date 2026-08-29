package com.taskmanager.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataPersistence {
    private static final String DATA_FILE = "tasks.dat";

    public static void saveTasks(List<Task> tasks) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks);
            System.out.println("Tasks saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Task> loadTasks() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            return (List<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
