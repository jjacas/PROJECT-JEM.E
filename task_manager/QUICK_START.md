# Quick Start Guide - Todo List Manager

## Installation & Running

### 1. Navigate to Project Directory

```bash
cd "/Users/joshua/PROJECT JEM.E"
```

### 2. Compile (if needed)

```bash
javac *.java
```

### 3. Launch Application

**Option A: Modern GUI (Recommended)**

```bash
java main
```

 Beautiful Swing interface with modern colors and design

**Option B: Command Line Interface**

```bash
java TodoListManagerV2
```

 Full-featured CLI with 14-menu options

**Option C: Run Tests**

```bash
java TestCases
```

 Comprehensive test suite (36 tests, 100% pass rate)

---

## Feature Checklist

### Core Features

-[ ] Create tasks
-[ ] Mark tasks complete
-[ ] Edit task details
-[ ] Delete tasks
-[ ] Set priorities (1-5)
-[ ] Add due dates
-[ ] Add notes

### Advanced Features

-[ ] Create multiple lists
-[ ] Switch between lists
-[ ] Set up reminders (4 timing options)
-[ ] Configure recurrence (5 pattern types)
-[ ] Search by keyword
-[ ] Filter by priority
-[ ] View statistics

### Data Management

-[ ] Automatic JSON save
-[ ] Create backups (timestamp automat)
-[ ] Export to CSV
-[ ] Archive completed tasks

---

## Key Shortcuts (GUI)

| Action        | How                             |
| ------------- | ------------------------------- |
| Add Task      | Click [Add Task] button         |
| Edit Task     | Double-click on task row        |
| Mark Complete | Select task + [Mark Complete]   |
| Delete Task   | Select task + [Delete]          |
| Search        | Type in search box, press Enter |
| Create List   | Click [New List] button         |
| Switch List   | Select from dropdown            |
| Create Backup | Click [Backup] button           |
| Export Data   | Click [Export] button           |

---

## Menu Options (CLI)

```
1. Add Task
2. View All Tasks
3. Mark Task Complete
4. Delete Task
5. Edit Task
6. Search by Keyword
7. View Statistics
8. Create Backup
9. Export to CSV
10. Archive Completed Tasks
11. Create New List
12. Switch List
13. Set Reminder
14. Exit
```

---

## Data Storage

**Location:** `data/` directory

| File            | Purpose                  |
| --------------- | ------------------------ |
| `tasks.json`    | Main task database       |
| `backups/`      | Timestamped backup files |
| `archives.json` | Archived completed tasks |
| `*.csv`         | Exported task data       |

---

## Color Reference

| Color  | Usage                   | Hex     |
| ------ | ----------------------- | ------- |
| Purple | Headers, Primary accent | #483CD4 |
| Coral  | Action buttons          | #F43F5E |
| Green  | Completed tasks         | #22C55E |
| Orange | Due soon indicator      | #FB923C |
| Red    | Overdue tasks, Danger   | #EF4444 |

---

## Test Results

 **36 Tests - 100% Pass Rate**

**Coverage:**

-Task creation & management
-Multiple lists
-Priority system
-Due dates
-Reminders & recurrence
-Search & filtering
-Statistics
-Data persistence
-Input validation
-Edge cases
-Performance (500+ tasks)

**Performance:**

-500 tasks added in 3ms
-500 tasks searched in 2ms

---

## Troubleshooting

### GUI won't launch

```bash
# Check Java version
java -version  # Should be 1.8 or higher

# Verify all .class files exist
ls -la *.class

# Recompile if needed
javac *.java
```

### Data not saving

```bash
# Check data directory exists
ls -la data/

# Verify write permissions
touch data/test.txt && rm data/test.txt
```

### Search not working

-Verify task name spelling
-Check subject field (case-insensitive)
-Try searching with partial words

### Reminders not triggering

-Check reminder is enabled
-Verify system time is correct
-Check ReminderManager is running

---

## File Descriptions

| File                       | Purpose                   |
| -------------------------- | ------------------------- |
| `main.java`                | Entry point, launches GUI |
| `Task.java`                | Task data model           |
| `TaskList.java`            | List container            |
| `TodoAppGUI.java`          | Modern Swing interface    |
| `TodoListManagerV2.java`   | CLI interface             |
| `TaskSearch.java`          | Search engine             |
| `TaskStatistics.java`      | Analytics                 |
| `ReminderManager.java`     | Reminder system           |
| `RecurrencePattern.java`   | Recurring tasks           |
| `JsonDataPersistence.java` | File storage              |
| `Reminder.java`            | Reminder config           |
| `TestCases.java`           | Test suite                |

---

## Tips & Tricks

1. **Bulk Operations:** Select multiple tasks to perform actions in batch
2. **Filter Quickly:** Use search box for instant filtering
3. **Auto-backup:** System creates timestamped backups automatically
4. **Priority Stars:** Higher priority shows more stars in task table
5. **Color Coding:** Green = done, Red = overdue, Orange = high priority
6. **CSV Export:** Open in Excel for analysis and reporting
7. **Reminders:** Set multiple reminders per task
8. **Recurring Tasks:** Set once, runs on schedule automatically

---

## Examples

### Create a Weekly Team Meeting Task

1. Click "Add Task"
2. Name: "Weekly Team Meeting"
3. Priority: 4
4. Due Date: Friday
5. Set Recurrence: Weekly
6. Set Reminder: 1 Day Before
7. Subject: "Work"
8. Click "Save"

### Find All Overdue Work Tasks

1. Subject Filter: Select "Work"
2. Status Filter: Select "Overdue"
3. Click "Search"
4. Results show all overdue work tasks

### Export Month's Completed Tasks

1. Filter: Status = Completed
2. Click "Export to CSV"
3. Opens in Excel with task details
4. Can archive after review

---

## Support Resources

-**Test Suite:** Run `java TestCases` to verify system health
-**Documentation:** See PROJECT_SUMMARY.md for detailed info
-**Source Code:** Well-commented Java files included
-**GUI Tooltips:** Hover over buttons for hints

---

**Happy Task Managing! **

For more details, see PROJECT_SUMMARY.md
