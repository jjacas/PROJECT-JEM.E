package com.taskmanager.scheduling;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class TaskRecurrenceScheduler implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum RecurrenceType {
        NONE, DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    }

    private RecurrenceType type;
    private int customIntervalDays;
    private Set<DayOfWeek> daysOfWeek;
    private int dayOfMonth;
    private LocalDate endDate;

    public TaskRecurrenceScheduler() {
        this.type = RecurrenceType.NONE;
        this.customIntervalDays = 1;
        this.daysOfWeek = new HashSet<>();
        this.dayOfMonth = -1;
        this.endDate = null;
    }

    public RecurrenceType getType() {
        return type;
    }

    public void setType(RecurrenceType type) {
        this.type = type;
    }

    public int getCustomIntervalDays() {
        return customIntervalDays;
    }

    public void setCustomIntervalDays(int days) {
        this.customIntervalDays = Math.max(1, days);
    }

    public Set<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(Set<DayOfWeek> days) {
        this.daysOfWeek = new HashSet<>(days);
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int day) {
        this.dayOfMonth = day > 0 && day <= 31 ? day : -1;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate date) {
        this.endDate = date;
    }

    public LocalDate getNextOccurrence(LocalDate fromDate) {
        if (type == RecurrenceType.NONE || (endDate != null && fromDate.isAfter(endDate))) {
            return null;
        }

        LocalDate next = fromDate.plusDays(1);

        switch (type) {
            case DAILY:
                break;

            case WEEKLY:
                while (!daysOfWeek.contains(next.getDayOfWeek())) {
                    next = next.plusDays(1);
                }
                break;

            case MONTHLY:
                if (dayOfMonth > 0) {
                    while (next.getDayOfMonth() != dayOfMonth) {
                        next = next.plusDays(1);
                        if (next.getDayOfMonth() < dayOfMonth && next.plusDays(1).getDayOfMonth() == 1) {
                            next = next.withDayOfMonth(dayOfMonth);
                        }
                    }
                }
                break;

            case YEARLY:
                next = next.withMonth(fromDate.getMonth().getValue())
                           .withDayOfMonth(fromDate.getDayOfMonth());
                if (next.isBefore(fromDate)) {
                    next = next.plusYears(1);
                }
                break;

            case CUSTOM:
                next = fromDate.plusDays(customIntervalDays);
                break;
        }

        if (endDate != null && next.isAfter(endDate)) {
            return null;
        }

        return next;
    }

    public String getDescription() {
        switch (type) {
            case NONE:
                return "Non-recurring";
            case DAILY:
                return "Daily";
            case WEEKLY:
                return "Weekly on " + daysOfWeek;
            case MONTHLY:
                return "Monthly on day " + dayOfMonth;
            case YEARLY:
                return "Yearly";
            case CUSTOM:
                return "Every " + customIntervalDays + " day(s)";
            default:
                return "Unknown";
        }
    }
}
