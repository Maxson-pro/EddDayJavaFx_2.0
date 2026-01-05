package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class HelloController {
    @FXML private TextField addText;
    @FXML private TabPane tabPane;
    @FXML private TextArea textAr;
    @FXML private ImageView Galery;

    private TabManager tabManager;
    private PhotoManager photoManager;
    @FXML
    public void initialize() {
        tabManager = new TabManager(tabPane, textAr);
        photoManager = new PhotoManager(Galery);
        tabManager.initialize();
    }
    @FXML public void Scene_1(ActionEvent actionEvent) {
        switchScene("AllDates.fxml", actionEvent);
    }
    @FXML public void Scene_2(ActionEvent actionEvent) {
        switchScene("Searchdate.fxml", actionEvent);
    }
    @FXML public void Back(ActionEvent actionEvent) {
        switchScene("hello-view.fxml", actionEvent);
    }
    @FXML public void Exit(ActionEvent actionEvent) {
        System.exit(0);
    }


    @FXML public void Photo(ActionEvent actionEvent) {
        String selectedDate = tabManager.getSelectedTabDate();
        if (selectedDate != null) {
            PhotoManager.setGalleryDateToLoad(selectedDate);
            switchScene("Galery.fxml", actionEvent);
        } else {
            AlertManager.showAlert("Ошибка", "Сначала выберите день");
        }
    }

    @FXML public void Save(ActionEvent actionEvent) {
        tabManager.saveCurrentTab();
    }
    @FXML public void Delete(ActionEvent actionEvent) {
        tabManager.deleteCurrentTab();
    }
    @FXML public void Search(ActionEvent actionEvent) {
        DateSearchManager.search(addText.getText().trim(), actionEvent);
    }
    @FXML public void initializeGalery() {
        photoManager.initializeGalery();
    }
    @FXML public void BackPhoto(ActionEvent actionEvent) {
        photoManager.backPhoto();
    }
    @FXML public void Forward(ActionEvent actionEvent) {
        photoManager.forward();
    }
    @FXML public void Download(ActionEvent actionEvent) {
        photoManager.download();
    }
    @FXML public void Save1(ActionEvent actionEvent) {
        photoManager.savePhoto();
    }
    @FXML public void DeletePhoto(ActionEvent actionEvent) {
        photoManager.deletePhoto();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}