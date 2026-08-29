# Todo List Manager

A Java task manager with both a desktop GUI (Swing) and a command-line interface. Supports multiple task lists, priorities, due dates, recurring tasks, reminders, search, and basic analytics.

## Features

- Create, edit, complete, and delete tasks
- Priority levels (1-5) with validation
- Due dates and free-text notes per task
- Multiple named task lists (e.g. Work, Personal)
- Recurring tasks (daily, weekly, monthly, yearly, or a custom interval)
- Reminders with configurable timing
- Keyword search, plus filtered views (high priority, overdue, due soon, completed)
- Basic stats: completion rate, breakdown by subject, breakdown by priority
- JSON-based save/load, timestamped backups, CSV export

## Requirements

- Java 21 or later
- No external dependencies (uses only the standard library and Swing)

## Project Structure

```
task_manager/
├── src/main/java/com/taskmanager/
│   ├── model/          # Task, TaskCollection, Reminder, RecurrencePattern, TaskReminder
│   ├── persistence/    # TaskPersistenceManager (JSON save/load, backups, CSV export)
│   ├── scheduling/     # TaskRecurrenceScheduler, TaskReminderNotificationService
│   ├── search/         # TaskSearchEngine, TaskAnalyticsDashboard
│   └── ui/             # TaskManagerApplication (GUI entry point),
│                       # TaskManagerCommandLineInterface (CLI entry point),
│                       # TaskManagerGUIApplication, TaskManagerTestSuite
├── data/                # Saved task lists and backups (created at runtime)
├── guides/              # Older standalone reference version, not part of the app
└── *.md                 # Docs
```

A few files under `persistence/` and `search/` (`DataPersistence.java`, `JsonDataPersistence.java`, `TaskSearch.java`, `TaskStatistics.java`, `FileRenamingGuide.java`) are leftovers from an earlier version of the project and aren't wired into the app. They're safe to remove if you want a leaner tree.

## Building and Running

Compile everything:

```bash
javac -d bin $(find src/main/java -name "*.java")
```

Run the GUI:

```bash
java -cp bin com.taskmanager.ui.TaskManagerApplication
```

Run the CLI:

```bash
java -cp bin com.taskmanager.ui.TaskManagerCommandLineInterface
```

Run the test suite:

```bash
java -cp bin com.taskmanager.ui.TaskManagerTestSuite
```

## Data Storage

Task lists are saved as JSON under `data/tasks.json`. Backups are written to `data/backups/` with a timestamped filename each time a backup is triggered. CSV exports are written to the working directory.

## Notes on the GUI

The Swing GUI (`TaskManagerGUIApplication`) currently builds and displays the full layout (task table, sidebar stats, search bar, action buttons), but a few of the dialog and refresh methods (`showAddTaskDialog`, `showEditTaskDialog`, `showCreateListDialog`, `updateStatistics`, `updateReminders`, `performSearch`) are stubs pending implementation. The CLI is fully functional end to end.
