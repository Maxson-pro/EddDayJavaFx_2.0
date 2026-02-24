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
    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static String dateToOpen = null;

    public static void search(String searchDate, ActionEvent actionEvent) {
        if (searchDate.isEmpty()) {
      AlertManager.showAlert("Ошибка", "Введите дату");
            return;
        }
        if (!isValidDate(searchDate)) {
       AlertManager.showAlert("Ошибка", "Формат yyyy-MM-dd");
            return;
        }
        File jsonFile = new File(dataFolder + File.separator + searchDate + ".json");
        if (!jsonFile.exists()) {
        AlertManager.showAlert("Не найдено", "Даты нет");
            return;
        }

        dateToOpen = searchDate;
        try {
       Parent root = FXMLLoader.load(DateSearchManager.class.getResource("AllDates.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 public static String getDateToOpen() {
        return dateToOpen;
    }

    public static void setDateToOpen(String date) {
        dateToOpen = date;
    }

    static boolean isValidDate(String dateStr) {
        try {
      LocalDate.parse(dateStr, dateFormatter);
  return true;
        } catch (Exception e) {
            return false;
        }
    }
}
