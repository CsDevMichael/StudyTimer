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
    private Timeline timeline;

    @Override
    public void start(Stage stage) {

        Label title = new Label("StudyTimer");

        timerLabel = new Label(formatTime(seconds));

        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");

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
                timerLabel,
                startButton,
                pauseButton,
                resetButton
        );

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 300);

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