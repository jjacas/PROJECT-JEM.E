package com.taskmanager.ui;

public class TaskManagerApplication {
    public static void main(String[] args) {
        // Launch the modern Swing GUI on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> new TaskManagerGUIApplication());
    }
}
