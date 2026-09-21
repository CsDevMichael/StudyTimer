package com.study.timer;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.*;

public class App extends Application {

    private int goalSeconds = 1500;
    private int focusedSeconds = 0;
    private int savedSeconds = 0;

    private long startTime;
    private boolean running = false;
    private boolean goalStarted = false;

    private TimerMode mode = TimerMode.STUDY;

    private Label timerLabel;
    private Label miniTimerLabel;
    private Timeline timeline;
    private Stage miniStage;

    private double mouseX;
    private double mouseY;

    @Override
    public void start(Stage stage) {

        Label title = new Label("StudyTimer");
        Label modeLabel = new Label("Mode: STUDY");

        TextField goalInput = new TextField("25");
        goalInput.setMaxWidth(80);

        timerLabel = new Label(formatTime(goalSeconds));

        Button study = new Button("Study");
        Button project = new Button("Project");
        Button setGoal = new Button("Set Goal");

        Button start = new Button("Start");
        Button pause = new Button("Pause");
        Button reset = new Button("Reset");

        Button mini = new Button("Mini Timer");
        Button stats = new Button("Stats");
        Button calendar = new Button("Calendar");

        study.setOnAction(e -> {
            mode = TimerMode.STUDY;
            modeLabel.setText("Mode: STUDY");
        });

        project.setOnAction(e -> {
            mode = TimerMode.PROJECT;
            modeLabel.setText("Mode: PROJECT");
        });

        setGoal.setOnAction(e -> {
            try {
                int minutes = Integer.parseInt(goalInput.getText());

                if (minutes > 0) {
                    timeline.stop();

                    goalSeconds = minutes * 60;
                    focusedSeconds = 0;
                    savedSeconds = 0;

                    running = false;
                    goalStarted = false;

                    updateLabels();
                }
            } catch (NumberFormatException ignored) {
            }
        });

        start.setOnAction(e -> {

            if (!goalStarted) {
                goalStarted = true;
                focusedSeconds = 0;
                savedSeconds = 0;
            }

            startTime = System.nanoTime();
            running = true;
            timeline.play();
        });

        pause.setOnAction(e -> {

            if (!running) return;

            updateElapsed();

            running = false;
            timeline.pause();

            saveProgress(false);
        });

        reset.setOnAction(e -> {

            if (running) updateElapsed();

            saveProgress(false);
            timeline.stop();

            focusedSeconds = 0;
            savedSeconds = 0;

            running = false;
            goalStarted = false;

            updateLabels();
        });

        timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(200),
                        e -> {

                            if (!running) return;

                            updateElapsed();

                            if (focusedSeconds >= goalSeconds) {

                                focusedSeconds = goalSeconds;
                                running = false;

                                timeline.stop();
                                saveProgress(true);

                                goalStarted = false;
                                updateLabels();
                            }
                        }
                )
        );

        timeline.setCycleCount(Animation.INDEFINITE);

        mini.setOnAction(e -> showMiniTimer());
        stats.setOnAction(e -> showStats());
        calendar.setOnAction(e -> CalendarView.show());

        HBox modes = new HBox(10, study, project);
        HBox goal = new HBox(
                10,
                new Label("Goal (minutes)"),
                goalInput,
                setGoal
        );
        HBox controls = new HBox(
                10,
                start,
                pause,
                reset
        );

        modes.setAlignment(Pos.CENTER);
        goal.setAlignment(Pos.CENTER);
        controls.setAlignment(Pos.CENTER);

        VBox layout = new VBox(
                15,
                title,
                modeLabel,
                modes,
                goal,
                timerLabel,
                controls,
                mini,
                stats,
                calendar
        );

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 450, 450);

        scene.getStylesheets().add(
                getClass()
                        .getResource("/style.css")
                        .toExternalForm()
        );

        stage.setTitle("StudyTimer");
        stage.setScene(scene);
        stage.show();
    }

    private void updateElapsed() {

        if (!running) return;

        long now = System.nanoTime();

        int elapsed = (int)
                ((now - startTime) / 1_000_000_000L);

        focusedSeconds = savedSeconds + elapsed;

        if (focusedSeconds > goalSeconds) {
            focusedSeconds = goalSeconds;
        }

        updateLabels();
    }

    private void saveProgress(boolean completed) {

        int unsaved = focusedSeconds - savedSeconds;

        if (!goalStarted || unsaved <= 0) {
            return;
        }

        GoalSession session = new GoalSession(
                LocalDate.now(),
                mode,
                goalSeconds,
                unsaved,
                completed
        );

        SessionManager.saveGoalSession(session);

        savedSeconds = focusedSeconds;
    }

    private void updateLabels() {

        int remaining =
                Math.max(0, goalSeconds - focusedSeconds);

        String time = formatTime(remaining);

        timerLabel.setText(time);

        if (miniTimerLabel != null) {
            miniTimerLabel.setText(time);
        }
    }

    private void showStats() {

        List<GoalSession> sessions =
                SessionManager.getGoalSessions();

        int total = 0;
        int study = 0;
        int project = 0;
        int completed = 0;

        Set<LocalDate> activeDays = new HashSet<>();

        for (GoalSession s : sessions) {

            int seconds = s.getFocusedSeconds();

            total += seconds;
            activeDays.add(s.getDate());

            if (s.getMode() == TimerMode.STUDY) {
                study += seconds;
            } else {
                project += seconds;
            }

            if (s.isCompleted()) {
                completed++;
            }
        }

        VBox layout = new VBox(
                15,
                new Label("StudyTimer Stats"),
                new Label(
                        "Total Focused: " +
                        formatDuration(total)
                ),
                new Label(
                        "Study Time: " +
                        formatDuration(study)
                ),
                new Label(
                        "Project Time: " +
                        formatDuration(project)
                ),
                new Label(
                        "Goals Completed: " + completed
                ),
                new Label(
                        "Active Days: " +
                        activeDays.size()
                ),
                new Label(
                        "Current Streak: " +
                        streak(activeDays) +
                        " days 🔥"
                )
        );

        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("stats-root");

        Scene scene = new Scene(
                layout,
                350,
                330
        );

        scene.getStylesheets().add(
                getClass()
                        .getResource("/style.css")
                        .toExternalForm()
        );

        Stage window = new Stage();

        window.setTitle("StudyTimer Stats");
        window.setScene(scene);
        window.show();
    }

    private int streak(Set<LocalDate> days) {

        int count = 0;
        LocalDate date = LocalDate.now();

        while (days.contains(date)) {
            count++;
            date = date.minusDays(1);
        }

        return count;
    }

    private void showMiniTimer() {

        if (miniStage != null) {
            miniStage.show();
            return;
        }

        miniStage = new Stage();
        miniStage.initStyle(StageStyle.UNDECORATED);
        miniStage.setAlwaysOnTop(true);

        miniTimerLabel = new Label(
                formatTime(
                        goalSeconds - focusedSeconds
                )
        );

        miniTimerLabel.setStyle(
                "-fx-font-size:38px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:white;"
        );

        VBox box = new VBox(miniTimerLabel);
        box.setAlignment(Pos.CENTER);

        Scene scene = new Scene(box, 200, 90);

        miniStage.setScene(scene);

        miniStage.setX(
                Screen.getPrimary()
                        .getVisualBounds()
                        .getMaxX() - 220
        );

        miniStage.setY(
                Screen.getPrimary()
                        .getVisualBounds()
                        .getMaxY() - 130
        );

        box.setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        box.setOnMouseDragged(e -> {
            miniStage.setX(
                    e.getScreenX() - mouseX
            );

            miniStage.setY(
                    e.getScreenY() - mouseY
            );
        });

        miniStage.show();
    }

    private String formatTime(int seconds) {

        return String.format(
                "%02d:%02d",
                seconds / 60,
                seconds % 60
        );
    }

    private String formatDuration(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        if (hours > 0) {
            return hours + "h " +
                    minutes + "m " +
                    secs + "s";
        }

        if (minutes > 0) {
            return minutes + "m " +
                    secs + "s";
        }

        return secs + "s";
    }

    public static void main(String[] args) {
        launch();
    }
}