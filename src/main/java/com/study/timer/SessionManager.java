package com.study.timer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static final Path FILE =
            Path.of(
                    System.getProperty("user.home"),
                    "StudyTimerSessions.csv"
            );

    public static void saveSession(Session session) {

        String line =
                session.getDate() + "," +
                session.getMode() + "," +
                session.getSeconds() +
                System.lineSeparator();

        try {

            Files.writeString(
                    FILE,
                    line,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveGoalSession(
            GoalSession session
    ) {

        String line =
                "GOAL," +
                session.getDate() + "," +
                session.getMode() + "," +
                session.getGoalSeconds() + "," +
                session.getFocusedSeconds() + "," +
                session.isCompleted() +
                System.lineSeparator();

        try {

            Files.writeString(
                    FILE,
                    line,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Session> getSessions() {

        List<Session> sessions =
                new ArrayList<>();

        if (!Files.exists(FILE)) {
            return sessions;
        }

        try {

            List<String> lines =
                    Files.readAllLines(FILE);

            for (String line : lines) {

                String[] parts =
                        line.split(",");

                // Old session format
                if (parts.length == 3) {

                    LocalDate date =
                            LocalDate.parse(parts[0]);

                    TimerMode mode =
                            TimerMode.valueOf(parts[1]);

                    int seconds =
                            Integer.parseInt(parts[2]);

                    sessions.add(
                            new Session(
                                    date,
                                    mode,
                                    seconds
                            )
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public static List<GoalSession> getGoalSessions() {

        List<GoalSession> sessions =
                new ArrayList<>();

        if (!Files.exists(FILE)) {
            return sessions;
        }

        try {

            List<String> lines =
                    Files.readAllLines(FILE);

            for (String line : lines) {

                String[] parts =
                        line.split(",");

                if (parts.length == 6
                        && parts[0].equals("GOAL")) {

                    LocalDate date =
                            LocalDate.parse(parts[1]);

                    TimerMode mode =
                            TimerMode.valueOf(parts[2]);

                    int goalSeconds =
                            Integer.parseInt(parts[3]);

                    int focusedSeconds =
                            Integer.parseInt(parts[4]);

                    boolean completed =
                            Boolean.parseBoolean(parts[5]);

                    sessions.add(
                            new GoalSession(
                                    date,
                                    mode,
                                    goalSeconds,
                                    focusedSeconds,
                                    completed
                            )
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessions;
    }
    public static void resetStats() {

    try {
        Files.deleteIfExists(FILE);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
public static void resetToday() {

    if (!Files.exists(FILE)) {
        return;
    }

    try {

        List<String> lines =
                Files.readAllLines(FILE);

        List<String> remaining =
                new ArrayList<>();

        String today =
                LocalDate.now().toString();

        for (String line : lines) {

            String[] parts =
                    line.split(",");

            // GOAL format:
            // GOAL,date,mode,goal,focused,completed
            if (parts.length == 6
                    && parts[0].equals("GOAL")
                    && parts[1].equals(today)) {

                continue;
            }

            remaining.add(line);
        }

        Files.write(
                FILE,
                remaining,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

    } catch (IOException e) {
        e.printStackTrace();
    }
}
}