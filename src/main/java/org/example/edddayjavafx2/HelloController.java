package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HelloController {
    @FXML
    private TextField addText;
    @FXML
    private TabPane tabPane;
    @FXML
    private TextArea textAr;
    @FXML
    private ImageView Galery;

    private String dataFolder = "data";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static String dateToOpen = null;
    private static String galleryDateToLoad = null;

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
        newTab.setOnSelectionChanged(event -> {
            if (newTab.isSelected()) {
                textAr = textArea;
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

    @FXML
    public void initialize() {
        new File(dataFolder).mkdir();
        new File(dataFolder + File.separator + "photos").mkdir();
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
        if (tabPane != null) {
            Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
            if (currentTab != null) {
                galleryDateToLoad = currentTab.getText();
                switchScene("Galery.fxml", actionEvent);
            } else {
                showAlert("Ошибка", "начала выберите день");
            }
        }
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
                showAlert("Сохран ено", "текст сохранен " + date);
            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось сохранить");
            }
        } else {
            showAlert("Ошибка", "Нет выбранной вкладки");
        }
    }
    @FXML
    public void Delete(ActionEvent actionEvent) {
        if (tabPane == null) return;
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            String date = currentTab.getText();
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Удаление");
            confirm.setHeaderText("Удалить день " + date);
            confirm.setContentText("Все данные будут удалены");
            ButtonType result = confirm.showAndWait().orElse(ButtonType.CANCEL);
            if (result == ButtonType.OK) {
                tabPane.getTabs().remove(currentTab);
                deleteJSON(date);
                showAlert("У далено", "День " + date + " удалн");
                if (tabPane.getTabs().isEmpty()) {
                    createTodayTab();
                }
            }
        } else {
            showAlert("Ошибка", "Нет выбранной вкладки");
        }
    }
    @FXML
    public void Search(ActionEvent actionEvent) {
        String searchDate = addText.getText().trim();
        if (searchDate.isEmpty()) {
            showAlert("Ошилбка", "Введите дату для поиска");
            return;
        }
        if (!isValidDate(searchDate)) {
            showAlert("Ошибка", "Неверный формаът даты, формат yyyy-MM-dd");
            return;
        }
        File jsonFile = new File(dataFolder + File.separator + searchDate + ".json");
        if (!jsonFile.exists()) {
            showAlert("Не найдъено", "Дат " + searchDate + " не существует.");
            return;
        }
        dateToOpen = searchDate;
        switchScene("AllDates.fxml", actionEvent);
    }
    public void openTabForDate(String date) {
        if (tabPane == null) return;
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(date)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        createTab(date);
    }
    @FXML
    public void initializeGalery() {
        String dateToUse;
        if (galleryDateToLoad != null) {
            dateToUse = galleryDateToLoad;
        } else {
            dateToUse = LocalDate.now().format(dateFormatter);
        }
        File[] photos = loadPhotosForDate(dateToUse);
        if (photos != null && photos.length > 0 && Galery != null) {
            try {
                Image image = new Image("file:" + photos[0].getAbsolutePath());
                Galery.setImage(image);
            } catch (Exception e) {
                Galery.setImage(null);
            }
        }
    }
    private File[] loadPhotosForDate(String date) {
        File dateFolder = new File(dataFolder + File.separator + "photos" + File.separator + date);
        if (dateFolder.exists() && dateFolder.isDirectory()) {
            return dateFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    String lower = name.toLowerCase();
                    return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif");
                }
            });
        }
        return new File[0];
    }
    private File[] getCurrentPhotos() {
        String date;
        if (galleryDateToLoad != null) {
            date = galleryDateToLoad;
        } else {
            date = LocalDate.now().format(dateFormatter);
        }
        return loadPhotosForDate(date);
    }
    private Image getCurrentImage() {
        if (Galery != null) {
            return Galery.getImage();
        }
        return null;
    }
    private int findCurrentImageIndex(File[] photos, Image currentImage) {
        if (currentImage == null || photos == null || photos.length == 0) return 0;
        String currentUrl = currentImage.getUrl();
        for (int i = 0; i < photos.length; i++) {
            String fileUrl = "file:" + photos[i].getAbsolutePath();
            if (fileUrl.replace("\\", "/").equals(currentUrl.replace("\\", "/"))) {
                return i;
            }
        }
        return 0;
    }
    @FXML
    public void BackPhoto(ActionEvent actionEvent) {
        File[] photos = getCurrentPhotos();
        Image currentImage = getCurrentImage();
        int currentIndex = findCurrentImageIndex(photos, currentImage);
        if (photos != null && photos.length > 0 && Galery != null) {
            if (currentIndex <= 0) {
                currentIndex = photos.length - 1;
            } else {
                currentIndex--;
            }
            try {
                Image image = new Image("file:" + photos[currentIndex].getAbsolutePath());
                Galery.setImage(image);
            } catch (Exception e) {
            }
        }
    }
    @FXML
    public void Forward(ActionEvent actionEvent) {
        File[] photos = getCurrentPhotos();
        Image currentImage = getCurrentImage();
        int currentIndex = findCurrentImageIndex(photos, currentImage);
        if (photos != null && photos.length > 0 && Galery != null) {
            if (currentIndex >= photos.length - 1) {
                currentIndex = 0;
            } else {
                currentIndex++;
            }
            try {
                Image image = new Image("file:" + photos[currentIndex].getAbsolutePath());
                Galery.setImage(image);
            } catch (Exception e) {
            }
        }
    }
    @FXML
    public void Save1(ActionEvent actionEvent) {
        File[] photos = getCurrentPhotos();
        if (photos != null && photos.length > 0) {
            showAlert("Информация", " фоток " + photos.length);
        } else {
            showAlert("Информация", "Нет фоток ");
        }
    }
    @FXML
    public void Download(ActionEvent actionEvent) {
        String currentDate;
        if (galleryDateToLoad != null) {
            currentDate = galleryDateToLoad;
        } else {
            currentDate = LocalDate.now().format(dateFormatter);
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите фото");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Картинки", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                File dateFolder = new File(dataFolder + File.separator + "photos" + File.separator + currentDate);
                if (!dateFolder.exists()) dateFolder.mkdirs();
                String fileName = System.currentTimeMillis() + getFileExtension(selectedFile.getName());
                File destFile = new File(dateFolder, fileName);
                try (FileInputStream in = new FileInputStream(selectedFile);
                     FileOutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
                if (Galery != null) {
                    Image image = new Image("file:" + destFile.getAbsolutePath());
                    Galery.setImage(image);
                }
                showAlert("ура", "Фото загружено для " + currentDate);

            } catch (Exception e) {
                showAlert("Ошибка", "Не удалось загрузить фото");
            }
        }
    }
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return ".jpg";
    }
    @FXML
    public void bakc1(ActionEvent actionEvent) {
        if (galleryDateToLoad != null) {
            dateToOpen = galleryDateToLoad;
        }
        switchScene("AllDates.fxml", actionEvent);
    }
}