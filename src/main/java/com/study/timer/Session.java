package com.study.timer;

import java.time.LocalDate;

public class Session {

    private LocalDate date;
    private TimerMode mode;
    private int seconds;

    public Session(LocalDate date, TimerMode mode, int seconds) {
        this.date = date;
        this.mode = mode;
        this.seconds = seconds;
    }

    public LocalDate getDate() {
        return date;
    }

    public TimerMode getMode() {
        return mode;
    }

    public int getSeconds() {
        return seconds;
    }
}