package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarLogic {
    private YearMonth currentYearMonth;
    private Label monthYearLabel;
    private GridPane calendarGrid;

    public CalendarLogic(Label monthYearLabel, GridPane calendarGrid) {
        this.monthYearLabel = monthYearLabel;
        this.calendarGrid = calendarGrid;
        this.currentYearMonth = YearMonth.now();
    }

    public void initializeCalendar() {
        updateCalendar();
    }
    public void updateCalendar() {
        if (calendarGrid == null || monthYearLabel == null) return;
        calendarGrid.getChildren().clear();
        String monthName = currentYearMonth.getMonth().getDisplayName(
                TextStyle.FULL, new Locale("ru"));
        monthYearLabel.setText(monthName + " " + currentYearMonth.getYear());
        LocalDate firstDay = currentYearMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue();
        int day = 1;
        int totalDays = currentYearMonth.lengthOfMonth();
        for (int week = 0; week < 6; week++) {
            for (int weekday = 1; weekday <= 7; weekday++) {
                if ((week == 0 && weekday < dayOfWeek) || day > totalDays) {
                    continue;
                }
                Button dayButton = new Button(String.valueOf(day));
                dayButton.setPrefSize(40, 40);
                DayButtonHandler handler = new DayButtonHandler(day);
                dayButton.setOnAction(handler);
                calendarGrid.add(dayButton, weekday - 1, week);
                day++;
            }
        }
    }
    private class DayButtonHandler implements EventHandler<ActionEvent> {
        private int day;

        public DayButtonHandler(int day) {
            this.day = day;
        }
        @Override
        public void handle(ActionEvent event) {
            selectDay(day, event);
        }
    }
    private void selectDay(int day, ActionEvent actionEvent) {
        LocalDate selectedDate = currentYearMonth.atDay(day);
        String dateStr = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        TextSearchManager.setDateToOpen(dateStr);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AllDates.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }
    public void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }
    public void today() {
        currentYearMonth = YearMonth.now();
        updateCalendar();
    }
}