package org.example.edddayjavafx2;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TabManager {
    private TabPane tabPane;
    private TextArea textAr;
    private String dataFolder = "data";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TabManager(TabPane tabPane, TextArea textAr) {
        this.tabPane = tabPane;
        this.textAr = textAr;
    }

    public void initialize() {
        new File(dataFolder).mkdir();
        new File(dataFolder + File.separator + "photos").mkdir();
        loadAllTabs();
    }

    public void saveCurrentTab() {
        if (tabPane == null || textAr == null) return;
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            String date = currentTab.getText();
            String text = textAr.getText();
            try {
                File jsonFile = new File(dataFolder + File.separator + date + ".json");
                SimpleJSON.saveToFile(jsonFile.getAbsolutePath(), text);
                AlertManager.showAlert("Сохранено", "Текст сохранен " + date);
            } catch (IOException e) {
                AlertManager.showAlert("Ошибка", "Не удалось сохранить");
            }
        } else {
            AlertManager.showAlert("Ошибка", "Нет выбранной вкладки");
        }
    }

    public void deleteCurrentTab() {
        if (tabPane == null) return;
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            String date = currentTab.getText();
            if (AlertManager.showConfirmation("Удаление", "Удалить день " + date, "Все данные будут удалены")) {
                tabPane.getTabs().remove(currentTab);
                deleteJSON(date);
                AlertManager.showAlert("Удалено", "День " + date + " удален");
                if (tabPane.getTabs().isEmpty()) {
                    createTodayTab();
                }
            }
        } else {
            AlertManager.showAlert("Ошибка", "Нет выбранной вкладки");
        }
    }

    public String getSelectedTabDate() {
        if (tabPane != null) {
            Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
            if (currentTab != null) {
                return currentTab.getText();
            }
        }
        return null;
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
        if (DateSearchManager.getDateToOpen() != null) {
            openTabForDate(DateSearchManager.getDateToOpen());
            DateSearchManager.setDateToOpen(null);
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
        newTab.setOnSelectionChanged(event -> {
            if (newTab.isSelected()) {
                textAr = textArea;
            }
        });
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    private String loadFromJSON(String date) throws IOException {
        File jsonFile = new File(dataFolder + File.separator + date + ".json");
        if (jsonFile.exists()) {
            return SimpleJSON.loadFromFile(jsonFile.getAbsolutePath());
        }
        return "";
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
        deletePhotosForDate(date);
    }

    private void deletePhotosForDate(String date) {
        File photosDir = new File(dataFolder + File.separator + "photos" + File.separator + date);
        if (photosDir.exists() && photosDir.isDirectory()) {
            File[] files = photosDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            photosDir.delete();
        }
    }

    private void openTabForDate(String date) {
        if (tabPane == null) return;
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(date)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        createTab(date);
    }
}