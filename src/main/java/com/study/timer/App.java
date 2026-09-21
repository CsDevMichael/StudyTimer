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

public class App extends Application {

    private int seconds = 25 * 60;

    private Label timerLabel;
    private Label miniTimerLabel;

    private Timeline timeline;

    private TimerMode currentMode = TimerMode.STUDY;

    private Stage miniStage;

    private double mouseX;
    private double mouseY;

    @Override
    public void start(Stage stage) {

        // =========================
        // MAIN WINDOW
        // =========================

        Label title = new Label("StudyTimer");

        Label modeLabel = new Label("Mode: STUDY");

        timerLabel = new Label(formatTime(seconds));

        Button studyButton = new Button("Study");
        Button projectButton = new Button("Project");

        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");

        Button miniButton = new Button("Mini Timer");

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
            updateTimerLabels();
        });

        // Open mini timer
        miniButton.setOnAction(event -> showMiniTimer());

        // Timer runs every second
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(1),
                event -> {

                    if (seconds > 0) {

                        seconds--;

                        updateTimerLabels();

                    } else {

                        timeline.stop();
                    }
                }
        );

        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Buttons in one row
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
                miniButton
        );

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 350);

        stage.setTitle("StudyTimer");
        stage.setScene(scene);

        stage.show();
    }


    // =========================
    // MINI TIMER
    // =========================

    private void showMiniTimer() {

        // If mini timer already exists, just show it
        if (miniStage != null) {
            miniStage.show();
            return;
        }

        miniStage = new Stage();

        // Remove normal window border
        miniStage.initStyle(StageStyle.UNDECORATED);

        // Keep above other windows
        miniStage.setAlwaysOnTop(true);

        miniTimerLabel = new Label(formatTime(seconds));

        miniTimerLabel.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10px;"
        );

        VBox miniLayout = new VBox(
                miniTimerLabel
        );

        miniLayout.setAlignment(Pos.CENTER);

        Scene miniScene = new Scene(
                miniLayout,
                120,
                60
        );

        miniStage.setScene(miniScene);

        // Put mini timer near bottom-right
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

        // =========================
        // DRAGGING
        // =========================

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