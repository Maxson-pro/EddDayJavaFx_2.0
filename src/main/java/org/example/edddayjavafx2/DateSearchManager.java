package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateSearchManager {
    private static String dataFolder = "data";
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static String dateToOpen = null;

    public static void search(String searchDate, ActionEvent actionEvent) {
        if (searchDate.isEmpty()) {
            AlertManager.showAlert("Ошибка", "Введите дату для поиска");
            return;
        }
        if (!isValidDate(searchDate)) {
            AlertManager.showAlert("Ошибка", "Неверный формат даты, формат yyyy-MM-dd");
            return;
        }
        File jsonFile = new File(dataFolder + File.separator + searchDate + ".json");
        if (!jsonFile.exists()) {
            AlertManager.showAlert("Не найдено", "Даты " + searchDate + " не существует.");
            return;
        }

        dateToOpen = searchDate;
        try {
            Parent root = FXMLLoader.load(DateSearchManager.class.getResource("AllDates.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            AlertManager.showAlert("Ошибка", "Не удалось переключиться на список даты");
        }
    }
    public static String getDateToOpen() {
        return dateToOpen;
    }
    public static void setDateToOpen(String date) {
        dateToOpen = date;
    }
    private static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, dateFormatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}