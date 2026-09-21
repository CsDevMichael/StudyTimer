package com.study.timer;

import java.time.LocalDate;

public class GoalSession {

    private LocalDate date;
    private TimerMode mode;
    private int goalSeconds;
    private int focusedSeconds;
    private boolean completed;

    public GoalSession(
            LocalDate date,
            TimerMode mode,
            int goalSeconds,
            int focusedSeconds,
            boolean completed
    ) {
        this.date = date;
        this.mode = mode;
        this.goalSeconds = goalSeconds;
        this.focusedSeconds = focusedSeconds;
        this.completed = completed;
    }

    public LocalDate getDate() {
        return date;
    }

    public TimerMode getMode() {
        return mode;
    }

    public int getGoalSeconds() {
        return goalSeconds;
    }

    public int getFocusedSeconds() {
        return focusedSeconds;
    }

    public boolean isCompleted() {
        return completed;
    }
}