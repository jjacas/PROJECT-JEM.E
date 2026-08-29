# Task Manager Project Structure

After refactoring, the project is now organized as follows with logical separation by functionality.

## Directory Organization

```
task_manager/
├── src/main/java/com/taskmanager/
│   ├── model/                           # Data models and core objects
│   │   ├── Task.java                   # Individual task/todo item (formerly TodoTask)
│   │   ├── TaskCollection.java         # Collection of tasks (formerly TodoTaskCollection)
│   │   ├── RecurrencePattern.java      # Recurring task patterns (daily, weekly, etc.)
│   │   ├── Reminder.java               # Reminder configuration
│   │   └── TaskReminder.java           # Task reminder handling
│   │
│   ├── persistence/                     # File I/O and data storage
│   │   ├── TaskPersistenceManager.java # Save/load tasks to JSON
│   │   ├── JsonDataPersistence.java    # JSON serialization
│   │   ├── DataPersistence.java        # Interface for persistence
│   │   └── FileRenamingGuide.java      # Legacy reference guide
│   │
│   ├── search/                          # Search, filtering, and analytics
│   │   ├── TaskSearchEngine.java       # Search and filter tasks
│   │   ├── TaskSearch.java             # Search utilities
│   │   ├── TaskAnalyticsDashboard.java # Statistics and analytics
│   │   └── TaskStatistics.java         # Statistics calculations
│   │
│   ├── scheduling/                      # Reminders and scheduling
│   │   ├── TaskReminderNotificationService.java  # Background reminder service
│   │   ├── TaskRecurrenceScheduler.java         # Schedule recurring tasks
│   │   └── ReminderManager.java         # Reminder management
│   │
│   └── ui/                              # User interfaces and entry points
│       ├── TaskManagerApplication.java         # GUI application entry point
│       ├── TaskManagerGUIApplication.java      # Main Swing GUI implementation
│       ├── TaskManagerCommandLineInterface.java # CLI interface
│       └── TaskManagerTestSuite.java           # Test suite
│
├── src/test/java/com/taskmanager/      # Test files (placeholder)
│
├── docs/                                # Documentation
│
├── guides/                              # Guides and references
│   └── TodoListManager.java             # Legacy guide
│
├── data/                                # Runtime data
│   └── backups/                         # Backup files
│
├── PROJECT_SUMMARY.md                  # Project overview
├── QUICK_START.md                       # Quick start guide
├── PROJECT_STRUCTURE.md                 # This file
├── PROJECT.md                           # Main documentation
├── export_*.csv                         # Exported task data
└── test_export.csv                      # Test export file
```

## File Renaming Summary

| Old Name                  | New Name                               | Package      | Purpose                     |
| ------------------------- | -------------------------------------- | ------------ | --------------------------- |
| `TodoTask.java`           | `Task.java`                            | `model`      | Core task data model        |
| `TodoTaskCollection.java` | `TaskCollection.java`                  | `model`      | Task collection container   |
| `main.java`               | `TaskManagerApplication.java`          | `ui`         | GUI application entry point |
| `TodoAppGUI.java`         | `TaskManagerGUIApplication.java`       | `ui`         | Swing GUI implementation    |
| `TodoListManagerV2.java`  | `TaskManagerCommandLineInterface.java` | `ui`         | CLI interface               |
| `TestCases.java`          | `TaskManagerTestSuite.java`            | `ui`         | Test suite                  |
| `ReminderManager.java`    | `TaskReminderNotificationService.java` | `scheduling` | Reminder service            |

## Package Organization

### `com.taskmanager.model`

Core data structures representing the domain model.

-Task entities
-Collections and groupings
-Reminders and recurrence patterns

### `com.taskmanager.persistence`

Handles data storage and retrieval.

-JSON serialization/deserialization
-File I/O operations
-Backup and export functionality

### `com.taskmanager.search`

Search, filter, and analytics functionality.

-Task searching by keyword
-Filtering by priority, date, completion status
-Statistics and dashboard data

### `com.taskmanager.scheduling`

Reminder and recurring task scheduling.

-Background reminder checking
-Notification management
-Recurrence pattern scheduling

### `com.taskmanager.ui`

User interfaces and application entry points.

-GUI application (Swing)
-CLI application
-Application launcher
-Test suite

## Compiling

To compile all Java files with the new structure:

```bash
cd src/main/java
javac -d ../../../../bin com/taskmanager/**/*.java
cd ../../../../
java -cp bin com.taskmanager.ui.TaskManagerApplication  # Run GUI
java -cp bin com.taskmanager.ui.TaskManagerCommandLineInterface  # Run CLI
```

Or from the task_manager root:

```bash
javac src/main/java/com/taskmanager/**/*.java -d bin
java -cp bin com.taskmanager.ui.TaskManagerApplication
```

## Benefits of This Structure

1. **Clear Separation of Concerns**: Each package has a single responsibility
2. **Easier Navigation**: Knowing functionality helps you find the right file
3. **Maintainability**: Related code is grouped together
4. **Scalability**: Easy to add new features in the appropriate package
5. **Testing**: Can easily add unit tests in `src/test/java` with matching structure
6. **Standard Maven Layout**: Follows `src/main/java` convention for better integration

## Notes

-All classes are in the `com.taskmanager` namespace with subpackages
-Package declarations are automatically added to each file
-The structure supports both IDE integration (IntelliJ, Eclipse) and Maven builds
-No class files are committed to version control
