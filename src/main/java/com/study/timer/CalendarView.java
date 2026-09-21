package com.study.timer;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarView {

    private static YearMonth currentMonth =
            YearMonth.now();

    public static void show() {

        List<GoalSession> sessions =
                SessionManager.getGoalSessions();

        Set<LocalDate> completedDays =
                new HashSet<>();

        Set<LocalDate> activeDays =
                new HashSet<>();

        for (GoalSession session : sessions) {

            activeDays.add(session.getDate());

            if (session.isCompleted()) {
                completedDays.add(session.getDate());
            }
        }

        Label title = new Label();
        title.getStyleClass().add("title");

        GridPane calendar = new GridPane();

        calendar.setHgap(10);
        calendar.setVgap(10);
        calendar.setAlignment(Pos.CENTER);

        Button previous = new Button("←");
        Button next = new Button("→");

        HBox navigation =
                new HBox(
                        20,
                        previous,
                        title,
                        next
                );

        navigation.setAlignment(Pos.CENTER);

        VBox layout =
                new VBox(
                        20,
                        navigation,
                        calendar
                );

        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("calendar-root");

        Scene scene =
                new Scene(
                        layout,
                        400,
                        400
                );

        scene.getStylesheets().add(
                CalendarView.class
                        .getResource("/style.css")
                        .toExternalForm()
        );

        Stage stage = new Stage();

        stage.setTitle("StudyTimer Calendar");
        stage.setScene(scene);

        Runnable refresh = () -> {

            title.setText(
                    currentMonth.getMonth()
                            + " "
                            + currentMonth.getYear()
            );

            calendar.getChildren().clear();

            String[] days = {
                    "Mon", "Tue", "Wed",
                    "Thu", "Fri", "Sat", "Sun"
            };

            for (int i = 0; i < 7; i++) {

                calendar.add(
                        new Label(days[i]),
                        i,
                        0
                );
            }

            int startColumn =
                    currentMonth.atDay(1)
                            .getDayOfWeek()
                            .getValue() - 1;

            for (int day = 1;
                 day <= currentMonth.lengthOfMonth();
                 day++) {

                LocalDate date =
                        currentMonth.atDay(day);

                Label label =
                        new Label(
                                String.valueOf(day)
                        );

                label.setMinSize(35, 35);
                label.setAlignment(Pos.CENTER);

                label.getStyleClass()
                        .add("calendar-date");

                // GOAL COMPLETED = GREEN
                if (completedDays.contains(date)) {

    label.setStyle(
            "-fx-background-color: #22c55e;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;"
    );

} else if (date.equals(LocalDate.now())) {

    label.getStyleClass()
            .add("calendar-today");
}

                int position =
                        startColumn + day - 1;

                calendar.add(
                        label,
                        position % 7,
                        position / 7 + 1
                );
            }
        };

        previous.setOnAction(e -> {

            currentMonth =
                    currentMonth.minusMonths(1);

            refresh.run();
        });

        next.setOnAction(e -> {

            currentMonth =
                    currentMonth.plusMonths(1);

            refresh.run();
        });

        refresh.run();

        stage.show();
    }
}