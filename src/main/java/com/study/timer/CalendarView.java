package com.study.timer;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarView {

    public static void show() {

        List<Session> sessions =
                SessionManager.getSessions();

        Set<LocalDate> activeDays =
                new HashSet<>();

        for (Session session : sessions) {
            activeDays.add(session.getDate());
        }

        LocalDate today = LocalDate.now();

        YearMonth currentMonth =
                YearMonth.from(today);

        Label title =
                new Label(
                        currentMonth.getMonth() +
                        " " +
                        currentMonth.getYear()
                );

        GridPane calendar =
                new GridPane();

        calendar.setHgap(10);
        calendar.setVgap(10);

        calendar.setAlignment(Pos.CENTER);

        // Day names
        String[] days = {
                "Mon", "Tue", "Wed",
                "Thu", "Fri", "Sat", "Sun"
        };

        for (int i = 0; i < days.length; i++) {

            Label dayLabel =
                    new Label(days[i]);

            calendar.add(
                    dayLabel,
                    i,
                    0
            );
        }

        LocalDate firstDay =
                currentMonth.atDay(1);

        int startColumn =
                firstDay.getDayOfWeek()
                        .getValue() - 1;

        int daysInMonth =
                currentMonth.lengthOfMonth();

        for (int day = 1;
             day <= daysInMonth;
             day++) {

            LocalDate date =
                    currentMonth.atDay(day);

            Label dateLabel =
                    new Label(String.valueOf(day));

            dateLabel.setMinSize(35, 35);

            dateLabel.setAlignment(
                    Pos.CENTER
            );

           if (activeDays.contains(date) && date.equals(today)) {

    dateLabel.setStyle(
            "-fx-background-color: lightgreen;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 2px;" +
            "-fx-font-weight: bold;"
    );

} else if (activeDays.contains(date)) {

    dateLabel.setStyle(
            "-fx-background-color: lightgreen;" +
            "-fx-font-weight: bold;"
    );

} else if (date.equals(today)) {

    dateLabel.setStyle(
            "-fx-border-color: black;" +
            "-fx-border-width: 2px;" +
            "-fx-font-weight: bold;"
    );
}

            int position =
                    startColumn + day - 1;

            int row =
                    position / 7 + 1;

            int column =
                    position % 7;

            calendar.add(
                    dateLabel,
                    column,
                    row
            );
        }

        Label legend =
                new Label(
                        "Green = completed session"
                );

        VBox layout =
                new VBox(
                        20,
                        title,
                        calendar,
                        legend
                );

        layout.setAlignment(
                Pos.CENTER
        );

        Scene scene =
                new Scene(
                        layout,
                        400,
                        400
                );

        Stage stage =
                new Stage();

        stage.setTitle(
                "StudyTimer Calendar"
        );

        stage.setScene(scene);

        stage.show();
    }
}