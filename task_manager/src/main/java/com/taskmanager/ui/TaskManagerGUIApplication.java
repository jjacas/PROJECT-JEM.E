package com.taskmanager.ui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class TaskManagerGUIApplication extends JFrame {

    private Map<String, TodoTaskCollection> taskLists;
    private String currentListName;
    private TaskSearchEngine searchEngine;
    private TaskAnalyticsDashboard statistics;
    private TaskReminderNotificationService reminderManager;

    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> listSelector;
    private JTextArea statsTextArea;
    private JTextArea remindersTextArea;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Color PRIMARY_COLOR = new Color(72, 52, 212);
    private static final Color ACCENT_COLOR = new Color(244, 63, 94);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(251, 146, 60);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BG_DARK = new Color(12, 45, 82);
    private static final Color BG_LIGHT = new Color(223, 238, 255);
    private static final Color TEXT_DARK = Color.BLACK;
    private static final Color TEXT_LIGHT = new Color(20, 20, 20);

    public TaskManagerGUIApplication() {
        taskLists = TaskPersistenceManager.loadTaskLists();
        if (taskLists.isEmpty()) {
            taskLists.put("Default", new TodoTaskCollection("Default"));
            currentListName = "Default";
        } else {
            currentListName = taskLists.keySet().iterator().next();
        }

        searchEngine = new TaskSearchEngine(taskLists);
        statistics = new TaskAnalyticsDashboard(taskLists);
        reminderManager = new TaskReminderNotificationService(taskLists);

        setTitle("Todo List Manager - Modern UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        setLookAndFeel();

        setupUI();
        setVisible(true);

        startReminderChecker();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupUI() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.setBackground(BG_LIGHT);
        ((JPanel) contentPane).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = createTopPanel();
        JPanel sidebarPanel = createSidebarPanel();
        JPanel taskPanel = createTaskPanel();

        contentPane.add(topPanel, BorderLayout.NORTH);

        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplit.setLeftComponent(taskPanel);
        centerSplit.setRightComponent(sidebarPanel);
        centerSplit.setDividerLocation(800);
        centerSplit.setDividerSize(8);
        centerSplit.setBackground(BG_LIGHT);
        contentPane.add(centerSplit, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        refreshTaskList();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(75, 0, 130), getWidth(), 0, new Color(244, 63, 94)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Todo List Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JPanel listPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        listPanel.setBackground(ACCENT_COLOR);

        JLabel listLabel = new JLabel("Task List:");
        listLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listLabel.setForeground(Color.WHITE);

        listSelector = new JComboBox<>(new Vector<>(taskLists.keySet()));
        listSelector.setSelectedItem(currentListName);
        listSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listSelector.setForeground(Color.BLACK);
        listSelector.setBackground(Color.WHITE);
        listSelector.addActionListener(e -> switchList((String) listSelector.getSelectedItem()));
        listSelector.setPreferredSize(new Dimension(240, 34));

        JButton newListBtn = createStyledButton("Create Task List", ACCENT_COLOR);
        newListBtn.setPreferredSize(new Dimension(260, 36));
        newListBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newListBtn.addActionListener(e -> showCreateListDialog());

        listPanel.add(listLabel);
        listPanel.add(listSelector);
        listPanel.add(newListBtn);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(listPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel headerLabel = new JLabel("Tasks");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(TEXT_DARK);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setForeground(TEXT_DARK);
        searchField.setToolTipText("Search tasks by name, subject, or notes");
        styleTextField(searchField);

        JButton searchBtn = createStyledButton("Search Tasks", PRIMARY_COLOR);
        searchBtn.addActionListener(e -> performSearch(searchField.getText()));

        JButton clearBtn = createStyledButton("Clear", new Color(148, 163, 184));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            refreshTaskList();
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        String[] columnNames = {"Status", "Task Name", "Subject", "Priority", "Due Date", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    TodoTask task = (TodoTask) getValueAt(row, 0);
                    if (task != null && task.isCompleted()) {
                        c.setBackground(SUCCESS_COLOR);
                        c.setForeground(Color.WHITE);
                    } else if (task != null && task.getDueDate() != null) {
                        LocalDate today = LocalDate.now();
                        if (task.getDueDate().isBefore(today) && task.getPriority() >= 4) {
                            c.setBackground(DANGER_COLOR);
                            c.setForeground(Color.WHITE);
                        } else if (task.getDueDate().isAfter(today) && task.getDueDate().isBefore(today.plusDays(3))) {
                            c.setBackground(WARNING_COLOR);
                            c.setForeground(Color.BLACK);
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(TEXT_DARK);
                        }
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(TEXT_DARK);
                    }
                } else {
                    c.setBackground(PRIMARY_COLOR);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };

        taskTable.setRowHeight(25);
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        taskTable.setGridColor(new Color(200, 200, 200));
        taskTable.setSelectionBackground(PRIMARY_COLOR);
        taskTable.setSelectionForeground(Color.WHITE);

        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = taskTable.getSelectedRow();
                    if (row >= 0) {
                        TodoTask task = (TodoTask) tableModel.getValueAt(row, 0);
                        showEditTaskDialog(task);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        statsTextArea = new JTextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statsTextArea.setForeground(TEXT_DARK);
        JScrollPane statsScroll = new JScrollPane(statsTextArea);
        statsPanel.add(statsScroll, BorderLayout.CENTER);

        JPanel remindersPanel = new JPanel(new BorderLayout());
        remindersPanel.setBackground(Color.WHITE);
        remindersPanel.setBorder(BorderFactory.createTitledBorder("Upcoming (7 days)"));

        remindersTextArea = new JTextArea();
        remindersTextArea.setEditable(false);
        remindersTextArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        remindersTextArea.setForeground(TEXT_DARK);
        JScrollPane remindersScroll = new JScrollPane(remindersTextArea);
        remindersPanel.add(remindersScroll, BorderLayout.CENTER);

        panel.add(statsPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(remindersPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton addBtn = createStyledButton("+ Add Task", SUCCESS_COLOR);
        addBtn.addActionListener(e -> showAddTaskDialog());

        JButton editBtn = createStyledButton("Edit", PRIMARY_COLOR);
        editBtn.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row >= 0) {
                TodoTask task = (TodoTask) tableModel.getValueAt(row, 0);
                showEditTaskDialog(task);
            }
        });

        JButton deleteBtn = createStyledButton("Delete", DANGER_COLOR);
        deleteBtn.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row >= 0) {
                TodoTaskCollection list = taskLists.get(currentListName);
                list.removeTask(row);
                TaskPersistenceManager.saveTaskLists(taskLists);
                refreshTaskList();
            }
        });

        JButton listBtn = createStyledButton("Manage Lists", ACCENT_COLOR);
        listBtn.addActionListener(e -> showCreateListDialog());

        JButton archiveBtn = createStyledButton("Archive", WARNING_COLOR);
        archiveBtn.addActionListener(e -> {
            TaskPersistenceManager.archiveCompletedTasks(taskLists);
            refreshTaskList();
        });

        JButton exportBtn = createStyledButton("Export CSV", new Color(59, 130, 246));
        exportBtn.addActionListener(e -> {
            String filename = "tasks_export_" + System.currentTimeMillis() + ".csv";
            TaskPersistenceManager.exportToCSV(taskLists, filename);
            showNotification("Tasks exported to: " + filename);
        });

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(new JSeparator(JSeparator.VERTICAL));
        panel.add(listBtn);
        panel.add(archiveBtn);
        panel.add(exportBtn);

        return panel;
    }

    private void styleTextField(JTextField field) {
        field.setMargin(new Insets(8, 8, 8, 8));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private void showAddTaskDialog() {
        // TODO: form fields + validation
    }

    private void showEditTaskDialog(TodoTask task) {
        // TODO: pre-populate form with task data
    }

    private void showCreateListDialog() {
        // TODO: dialog for naming a new list
    }

    private void refreshTaskList() {
        updateStatistics();
        updateReminders();
    }

    private void updateStatistics() {
        // TODO: pull stats from TaskAnalyticsDashboard into statsTextArea
    }

    private void updateReminders() {
        // TODO: pull upcoming reminders into remindersTextArea
    }

    private void performSearch(String keyword) {
        // TODO: filter table using searchEngine
    }

    private void switchList(String listName) {
        currentListName = listName;
        refreshTaskList();
    }

    private void showNotification(String message) {
        JOptionPane notification = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = notification.createDialog(this, "Todo List Manager");
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
    }

    private void startReminderChecker() {
        Thread reminderThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000);

                    java.util.List<String> alerts = reminderManager.checkReminders();
                    if (!alerts.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            for (String alert : alerts) {
                                System.out.println(alert);
                            }
                            updateReminders();
                        });
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        reminderThread.setDaemon(true);
        reminderThread.start();
    }
}
