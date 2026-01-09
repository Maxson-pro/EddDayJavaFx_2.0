package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import static org.example.edddayjavafx2.AlertManager.showAlert;

public class HelloController {
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    private CalendarLogic calendarLogic;
    @FXML private TextField addText;
    @FXML private TabPane tabPane;
    @FXML private TextArea textAr;
    @FXML private ImageView Galery;
    @FXML private TextField findtext1;

    private TabManager tabManager;
    private PhotoManager photoManager;
    @FXML
    public void initializeCalendar() {
        if (monthYearLabel != null && calendarGrid != null) {
            calendarLogic = new CalendarLogic(monthYearLabel, calendarGrid);
            calendarLogic.initializeCalendar();
        }
    }
    @FXML
    public void previousMonth(ActionEvent actionEvent) {
        if (calendarLogic != null) {
            calendarLogic.previousMonth();
        }
    }
    @FXML
    public void nextMonth(ActionEvent actionEvent) {
        if (calendarLogic != null) {
            calendarLogic.nextMonth();
        }
    }
    @FXML
    public void today(ActionEvent actionEvent) {
        if (calendarLogic != null) {
            calendarLogic.today();
        }
    }
    @FXML
    public void backToMain(ActionEvent actionEvent) {
        switchScene("AllDates.fxml", actionEvent);
    }
    @FXML
    public void EddDay(ActionEvent actionEvent) {
        switchScene("Calendar.fxml", actionEvent);
    }
    @FXML
    public void initialize() {
        if (monthYearLabel != null && calendarGrid != null) {
            initializeCalendar();
        }
        if (tabPane != null) {
            tabManager = new TabManager(tabPane, textAr);
            tabManager.initialize();
        }
        if (Galery != null) {
            photoManager = new PhotoManager(Galery);
        }
    }

    @FXML
    public void FindText1(ActionEvent actionEvent) {
        FindText(actionEvent);
    }

    @FXML
    public void FindText(ActionEvent actionEvent) {
        String searchText = findtext1.getText().trim();
        if (!searchText.isEmpty()) {
            SimpleTextSearchManager.search(searchText, actionEvent);
        } else {
            showAlert("Ошибка", "Введите текст для поиска");
        }
    }

    @FXML public void Scene_1(ActionEvent actionEvent) {
        switchScene("AllDates.fxml", actionEvent);
    }

    @FXML public void Scene_2(ActionEvent actionEvent) {
        switchScene("Searchdate.fxml", actionEvent);
    }

    @FXML public void SearchText(ActionEvent actionEvent) {
        switchScene("SearchByText.fxml", actionEvent);
    }

    @FXML public void Back(ActionEvent actionEvent) {
        switchScene("hello-view.fxml", actionEvent);
    }

    @FXML public void Exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML public void Photo(ActionEvent actionEvent) {
        if (tabManager != null) {
            String selectedDate = tabManager.getSelectedTabDate();
            if (selectedDate != null) {
                PhotoManager.setGalleryDateToLoad(selectedDate);
                switchScene("Galery.fxml", actionEvent);
            } else {
                showAlert("Ошибка", "Сначала выберите день");
            }
        }
    }

    @FXML public void Save(ActionEvent actionEvent) {
        if (tabManager != null) {
            tabManager.saveCurrentTab();
        }
    }

    @FXML public void Delete(ActionEvent actionEvent) {
        if (tabManager != null) {
            tabManager.deleteCurrentTab();
        }
    }

    @FXML public void Search(ActionEvent actionEvent) {
        if (addText != null) {
            DateSearchManager.search(addText.getText().trim(), actionEvent);
        }
    }

    @FXML public void initializeGalery() {
        if (photoManager != null) {
            photoManager.initializeGalery();
        }
    }

    @FXML public void BackPhoto(ActionEvent actionEvent) {
        if (photoManager != null) {
            photoManager.backPhoto();
        }
    }

    @FXML public void Forward(ActionEvent actionEvent) {
        if (photoManager != null) {
            photoManager.forward();
        }
    }

    @FXML public void Download(ActionEvent actionEvent) {
        if (photoManager != null) {
            photoManager.download();
        }
    }

    @FXML public void Save1(ActionEvent actionEvent) {
        if (photoManager != null) {
            photoManager.savePhoto();
        }
    }

    @FXML public void DeletePhoto(ActionEvent actionEvent) {
        if (photoManager != null) {
            photoManager.deletePhoto();
        }
    }

    @FXML public void bakc1(ActionEvent actionEvent) {
        PhotoManager.backToMain(actionEvent);
    }

    private void switchScene(String fxmlFile, ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}