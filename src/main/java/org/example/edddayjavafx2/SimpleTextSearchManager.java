package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;

public class SimpleTextSearchManager {
    private static String searchText;

    public static void search(String text, ActionEvent actionEvent) {
        searchText = text.toLowerCase().trim();
        if (searchText.isEmpty()) return;
        createResultsScene(actionEvent);
    }

    private static void createResultsScene(ActionEvent actionEvent) {
        try {
            VBox vbox = new VBox(10);
            File dataDir = new File("data");
            String[] searchWords = searchText.split(" ");

        if (dataDir.exists() && dataDir.isDirectory()) {
                File[] files = dataDir.listFiles();
                if (files != null) {
                 for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (file.isFile() && file.getName().endsWith(".json")) {
                            String content = SimpleJSON.loadFromFile(file.getAbsolutePath()).toLowerCase();
                   boolean found = false;
                            for (int j = 0; j < searchWords.length; j++) {
                                if (content.contains(searchWords[j])) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                String date = file.getName().substring(0, file.getName().length() - 5);
                                Button dateBtn = new Button("Запись: " + date);
                                dateBtn.setPrefWidth(200);
                                dateBtn.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent e) {
                                        openDay(date, e);
                                    }
                                });
                                vbox.getChildren().add(dateBtn);
                            }
                        }
                    }
                }
            }

            Button backBtn = new Button("Назад");
            backBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                    Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
                   Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                     stage.setScene(new Scene(root));
                    } catch (Exception ex) {}
                }
            });

            BorderPane root = new BorderPane();
      root.setCenter(new ScrollPane(vbox));
            root.setBottom(backBtn);
      Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openDay(String date, ActionEvent actionEvent) {
        try {
            TextSearchManager.setDateToOpen(date);
            Parent root = FXMLLoader.load(SimpleTextSearchManager.class.getResource("AllDates.fxml"));
     Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {}
    }
}