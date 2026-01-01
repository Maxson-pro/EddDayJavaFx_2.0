package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HelloController {
    @FXML
    private TextField addText;
    @FXML
    private TabPane tabPane;

    @FXML
    private TextArea textAr;

    private String dataFolder = "data";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static String dateToOpen = null;

    // Вспомогательные методы
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void switchScene(String fxmlFile, ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String loadFromJSON(String date) throws IOException {
        File jsonFile = new File(dataFolder + File.separator + date + ".json");
        if (jsonFile.exists()) {
            return SimpleJSON.loadFromFile(jsonFile.getAbsolutePath());
        }
        return "";
    }

    private void saveToJSON(String date, String text) throws IOException {
        File jsonFile = new File(dataFolder + File.separator + date + ".json");
        SimpleJSON.saveToFile(jsonFile.getAbsolutePath(), text);
    }

    private void loadAllTabs() {
        if (tabPane == null) return;
        createTodayTab();
        File dataDir = new File(dataFolder);
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        String fileName = file.getName();
                        String date = fileName.substring(0, fileName.length() - 5);

                        if (isValidDate(date)) {
                            String today = LocalDate.now().format(dateFormatter);
                            if (!date.equals(today)) {
                                createTab(date);
                            }
                        }
                    }
                }
            }
        }
        if (dateToOpen != null) {
            openTabForDate(dateToOpen);
            dateToOpen = null;
        }
    }

    private void createTodayTab() {
        String today = LocalDate.now().format(dateFormatter);
        createTab(today);
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(today)) {
                tabPane.getSelectionModel().select(tab);
                break;
            }
        }
    }

    private void createTab(String date) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(date)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        Tab newTab = new Tab(date);
        TextArea textArea = new TextArea();
        textArea.setPrefHeight(325);
        textArea.setPrefWidth(600);
        try {
            String savedText = loadFromJSON(date);
            textArea.setText(savedText);
        } catch (IOException e) {
            textArea.setText("");
        }
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(textArea);
        newTab.setContent(pane);
        newTab.setOnSelectionChanged(new javafx.event.EventHandler<javafx.event.Event>() {
            public void handle(javafx.event.Event event) {
                if (newTab.isSelected()) {
                    textAr = textArea;
                }
            }
        });
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, dateFormatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void deleteJSON(String date) {
        File jsonFile = new File(dataFolder + File.separator + date + ".json");
        if (jsonFile.exists()) {
            jsonFile.delete();
        }
    }

    // Основные методы
    @FXML
    public void initialize() {
        new File(dataFolder).mkdir();
        loadAllTabs();
    }

    public void Scene_1(ActionEvent actionEvent) {
        switchScene("AllDates.fxml", actionEvent);
    }

    public void Scene_2(ActionEvent actionEvent) {
        switchScene("Searchdate.fxml", actionEvent);
    }

    @FXML
    public void Back(ActionEvent actionEvent) {
        switchScene("hello-view.fxml", actionEvent);
    }

    @FXML
    public void Exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void Photo(ActionEvent actionEvent) {
    }

    @FXML
    public void Save(ActionEvent actionEvent) {
        if (tabPane == null || textAr == null) return;

        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            String date = currentTab.getText();
            String text = textAr.getText();

            try {
                saveToJSON(date, text);
                showAlert("Сохранено", "Текст сохране " + date);
            } catch (IOException e) {
                showAlert("О шибка", "Не удалось сохранить");
            }
        } else {
            showAlert("еррор", "Нет выбранной вкладки");
        }
    }

    @FXML
    public void Delete(ActionEvent actionEvent) {
        if (tabPane == null) return;

        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            String date = currentTab.getText();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Удалние");
            confirm.setHeaderText("Удалить день" + date);
            confirm.setContentText("Все данные будут удален");

            ButtonType result = confirm.showAndWait().orElse(ButtonType.CANCEL);

            if (result == ButtonType.OK) {
                tabPane.getTabs().remove(currentTab);
                deleteJSON(date);
                showAlert("удалено", "Дуень " + date + " ууален");

                if (tabPane.getTabs().isEmpty()) {
                    createTodayTab();
                }
            }
        } else {
            showAlert("ошибка", "нет выбранной вкладки");
        }
    }
    @FXML
    public void Search(ActionEvent actionEvent) {
        String searchDate = addText.getText().trim();
        if (searchDate.isEmpty()) {
            showAlert("ошибка", "Введите дату для поиска");
            return;
        }
        if (!isValidDate(searchDate)) {
            showAlert("Ошибка", "Неверный формат даты! Используйте формат yyyy-MM-dd");
            return;
        }
        File jsonFile = new File(dataFolder + File.separator + searchDate + ".json");
        if (!jsonFile.exists()) {
            showAlert("Не найдено", "даты " + searchDate + " не существует.");
            return;
        }
        dateToOpen = searchDate;
        switchScene("AllDates.fxml", actionEvent);
    }
    //для Искать
    public void openTabForDate(String date) {
        if (tabPane == null) {
            return;
        }
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(date)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        createTab(date);
    }
}