package com.study.timer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private int seconds = 25 * 60;

    private Label timerLabel;
    private Label modeLabel;
    private Timeline timeline;

    private TimerMode currentMode = TimerMode.STUDY;

    @Override
    public void start(Stage stage) {

        Label title = new Label("StudyTimer");

        modeLabel = new Label("Mode: STUDY");

        timerLabel = new Label(formatTime(seconds));

        Button studyButton = new Button("Study");
        Button projectButton = new Button("Project");

        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");

        studyButton.setOnAction(event -> {
            currentMode = TimerMode.STUDY;
            modeLabel.setText("Mode: STUDY");
        });

        projectButton.setOnAction(event -> {
            currentMode = TimerMode.PROJECT;
            modeLabel.setText("Mode: PROJECT");
        });

        startButton.setOnAction(event -> timeline.play());

        pauseButton.setOnAction(event -> timeline.pause());

        resetButton.setOnAction(event -> {
            timeline.stop();
            seconds = 25 * 60;
            timerLabel.setText(formatTime(seconds));
        });

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {

            if (seconds > 0) {
                seconds--;
                timerLabel.setText(formatTime(seconds));
            } else {
                timeline.stop();
            }
        });

        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);

        VBox layout = new VBox(
                15,
                title,
                modeLabel,
                studyButton,
                projectButton,
                timerLabel,
                startButton,
                pauseButton,
                resetButton
        );

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 400);

        stage.setTitle("StudyTimer");
        stage.setScene(scene);
        stage.show();
    }

    private String formatTime(int totalSeconds) {

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static void main(String[] args) {
        launch();
    }
}