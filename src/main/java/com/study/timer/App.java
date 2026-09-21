package com.study.timer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class App extends Application {

    private int seconds = 25 * 60;
    private int sessionSeconds = 0;

    private Label timerLabel;
    private Label miniTimerLabel;

    private Timeline timeline;

    private TimerMode currentMode = TimerMode.STUDY;

    private Stage miniStage;

    private double mouseX;
    private double mouseY;

    @Override
    public void start(Stage stage) {

        Label title = new Label("StudyTimer");

        Label modeLabel = new Label("Mode: STUDY");

        timerLabel = new Label(formatTime(seconds));

        Button studyButton = new Button("Study");
        Button projectButton = new Button("Project");

        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");

        Button miniButton = new Button("Mini Timer");

        Button statsButton = new Button("Stats");
        Button calendarButton = new Button("Calendar");

        // Study mode
        studyButton.setOnAction(event -> {
            currentMode = TimerMode.STUDY;
            modeLabel.setText("Mode: STUDY");
        });

        // Project mode
        projectButton.setOnAction(event -> {
            currentMode = TimerMode.PROJECT;
            modeLabel.setText("Mode: PROJECT");
        });

        // Start
        startButton.setOnAction(event -> timeline.play());

        // Pause
        pauseButton.setOnAction(event -> timeline.pause());

        // Reset
        resetButton.setOnAction(event -> {
            timeline.stop();
seconds = 25 * 60;
sessionSeconds = 0;
updateTimerLabels();
        });

        // Mini timer
        miniButton.setOnAction(event -> showMiniTimer());

        // Stats
        statsButton.setOnAction(event -> showStats());
        calendarButton.setOnAction(event -> CalendarView.show());

        // Timer
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(1),
                event -> {

                   if (seconds > 0) {

    seconds--;
    sessionSeconds++;

    updateTimerLabels();
                    } else {

                        timeline.stop();

                       if (sessionSeconds > 0) {

    Session session = new Session(
            LocalDate.now(),
            currentMode,
            sessionSeconds
    );

    SessionManager.saveSession(session);

    sessionSeconds = 0;
}
                    }
                }
        );

        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);

        HBox modeButtons = new HBox(
                10,
                studyButton,
                projectButton
        );

        modeButtons.setAlignment(Pos.CENTER);

        HBox timerButtons = new HBox(
                10,
                startButton,
                pauseButton,
                resetButton
        );

        timerButtons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(
        15,
        title,
        modeLabel,
        modeButtons,
        timerLabel,
        timerButtons,
        miniButton,
        statsButton,
        calendarButton
);

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 400);

        stage.setTitle("StudyTimer");
        stage.setScene(scene);

        stage.show();
    }


    // =========================
    // STATS WINDOW
    // =========================

    private void showStats() {

        List<Session> sessions =
                SessionManager.getSessions();

        int totalSeconds = 0;
        int studySeconds = 0;
        int projectSeconds = 0;

        Set<LocalDate> activeDays =
                new HashSet<>();

        for (Session session : sessions) {

            totalSeconds += session.getSeconds();

            activeDays.add(session.getDate());

            if (session.getMode() == TimerMode.STUDY) {

                studySeconds += session.getSeconds();

            } else if (session.getMode() == TimerMode.PROJECT) {

                projectSeconds += session.getSeconds();
            }
        }

        int streak = calculateStreak(activeDays);

        Label title =
                new Label("StudyTimer Stats");

        Label total =
                new Label(
                        "Total Focused: " +
                        formatMinutes(totalSeconds)
                );

        Label study =
                new Label(
                        "Study Time: " +
                        formatMinutes(studySeconds)
                );

        Label project =
                new Label(
                        "Project Time: " +
                        formatMinutes(projectSeconds)
                );

        Label days =
                new Label(
                        "Active Days: " +
                        activeDays.size()
                );

        Label streakLabel =
                new Label(
                        "Current Streak: " +
                        streak + " days 🔥"
                );

        VBox statsLayout = new VBox(
                15,
                title,
                total,
                study,
                project,
                days,
                streakLabel
        );

        statsLayout.setAlignment(Pos.CENTER);

        Scene statsScene =
                new Scene(statsLayout, 350, 300);

        Stage statsStage =
                new Stage();

        statsStage.setTitle("StudyTimer Stats");

        statsStage.setScene(statsScene);

        statsStage.show();
    }


    // =========================
    // STREAK
    // =========================

    private int calculateStreak(Set<LocalDate> activeDays) {

        int streak = 0;

        LocalDate date = LocalDate.now();

        while (activeDays.contains(date)) {

            streak++;

            date = date.minusDays(1);
        }

        return streak;
    }


    // =========================
    // FORMAT MINUTES
    // =========================

    private String formatMinutes(int totalSeconds) {

        int minutes = totalSeconds / 60;

        int hours = minutes / 60;

        minutes = minutes % 60;

        if (hours > 0) {

            return hours + "h " + minutes + "m";

        } else {

            return minutes + "m";
        }
    }


    // =========================
    // MINI TIMER
    // =========================

    private void showMiniTimer() {

        if (miniStage != null) {

            miniStage.show();

            return;
        }

        miniStage = new Stage();

        miniStage.initStyle(
                StageStyle.UNDECORATED
        );

        miniStage.setAlwaysOnTop(true);

        miniTimerLabel =
                new Label(formatTime(seconds));

        miniTimerLabel.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10px;"
        );

        VBox miniLayout =
                new VBox(miniTimerLabel);

        miniLayout.setAlignment(Pos.CENTER);

        Scene miniScene =
                new Scene(
                        miniLayout,
                        120,
                        60
                );

        miniStage.setScene(miniScene);

        miniStage.setX(
                javafx.stage.Screen.getPrimary()
                        .getVisualBounds()
                        .getMaxX() - 140
        );

        miniStage.setY(
                javafx.stage.Screen.getPrimary()
                        .getVisualBounds()
                        .getMaxY() - 100
        );

        // Dragging
        miniLayout.setOnMousePressed(event -> {

            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        miniLayout.setOnMouseDragged(event -> {

            miniStage.setX(
                    event.getScreenX() - mouseX
            );

            miniStage.setY(
                    event.getScreenY() - mouseY
            );
        });

        miniStage.show();
    }


    // =========================
    // UPDATE TIMER
    // =========================

    private void updateTimerLabels() {

        String time = formatTime(seconds);

        timerLabel.setText(time);

        if (miniTimerLabel != null) {

            miniTimerLabel.setText(time);
        }
    }


    // =========================
    // FORMAT TIME
    // =========================

    private String formatTime(int totalSeconds) {

        int minutes = totalSeconds / 60;

        int seconds = totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }


    public static void main(String[] args) {

        launch();
    }
}