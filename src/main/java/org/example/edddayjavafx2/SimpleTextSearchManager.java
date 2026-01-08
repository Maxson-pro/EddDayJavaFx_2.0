package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static org.example.edddayjavafx2.AlertManager.showAlert;

public class SimpleTextSearchManager {
    private static String searchText;
    public static void search(String text, ActionEvent actionEvent) {
        searchText = text.toLowerCase().trim();
        if (searchText.isEmpty()) {
            showAlert("Ошика", "Введите текамт");
            return;
        }
        createResultsScene(actionEvent);
    }
    private static void createResultsScene(ActionEvent actionEvent) {
        try {
            VBox vbox = new VBox(10);
            File dataDir = new File("data");
            int foundCount = 0;
            if (dataDir.exists() && dataDir.isDirectory()) {
                File[] files = dataDir.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (file.isFile() && file.getName().endsWith(".json")) {
                            String content = SimpleJSON.loadFromFile(file.getAbsolutePath());
                            if (content != null && content.toLowerCase().contains(searchText)) {
                                foundCount++;
                            }
                        }
                    }
                    String[] foundDates = new String[foundCount];
                    int index = 0;
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (file.isFile() && file.getName().endsWith(".json")) {
                            String content = SimpleJSON.loadFromFile(file.getAbsolutePath());
                            if (content != null && content.toLowerCase().contains(searchText)) {
                                String date = file.getName().substring(0, file.getName().length() - 5);
                                foundDates[index] = date;
                                index++;
                            }
                        }
                    }
                    if (foundCount == 0) {
                        Button noResults = new Button("Ничего не найдено для: " + searchText);
                        noResults.setDisable(true);
                        vbox.getChildren().add(noResults);
                    } else {
                        for (int i = 0; i < foundDates.length; i++) {
                            Button dateButton = new Button(foundDates[i]);
                            dateButton.setPrefWidth(200);
                            final String date = foundDates[i];
                            dateButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    openDay(date, e);
                                }
                            });

                            vbox.getChildren().add(dateButton);
                        }
                    }
                }
            } else {
                Button noData = new Button("Нет сохраненных дней");
                noData.setDisable(true);
                vbox.getChildren().add(noData);
            }
            Button backButton = new Button("Назад");
            backButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
                        Scene scene = new Scene(root);
                        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        stage.setScene(scene);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            BorderPane root = new BorderPane();
            root.setCenter(new ScrollPane(vbox));
            root.setBottom(backButton);
            Scene scene = new Scene(root, 600, 400);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void openDay(String date, ActionEvent actionEvent) {
        try {
            TextSearchManager.setDateToOpen(date);
            Parent root = FXMLLoader.load(SimpleTextSearchManager.class.getResource("AllDates.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}