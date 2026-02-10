package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HelloController {

    @FXML
    private StackPane calendarContainer;
    @FXML
    private TextField searchField;
    @FXML
    private TabPane tabPane;
    @FXML
    private TextArea mainTextArea;
    @FXML
    private ImageView galleryView;
    @FXML
    private TextField textSearchField;

    private DatePicker datePicker;
    private TabManager tabManager;
    private PhotoManager photoManager;

    @FXML
    public void initialize() {
        if (calendarContainer != null) {
            datePicker = new DatePicker(LocalDate.now());
            DatePickerSkin skin = new DatePickerSkin(datePicker);
            Node calendarVisual = skin.getPopupContent();
            calendarContainer.getChildren().add(calendarVisual);

            datePicker.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    handleDateSelection(event);
                }
            });
        }

        if (tabPane != null) {
            tabManager = new TabManager(tabPane, mainTextArea);
            tabManager.initialize();
        }
        if (galleryView != null) {
            photoManager = new PhotoManager(galleryView);
        }
    }

    @FXML
    public void showAllDates(ActionEvent event) {
        switchScene("AllDates.fxml", event);
    }

    @FXML
    public void showSearchDate(ActionEvent event) {
        switchScene("Searchdate.fxml", event);
    }

    @FXML
    public void showSearchText(ActionEvent event) {
        switchScene("SearchByText.fxml", event);
    }

    @FXML
    public void openCalendar(ActionEvent event) {
        switchScene("Calendar.fxml", event);
    }

    @FXML
    public void openGallery(ActionEvent event) {
        if (tabManager != null) {
            String selectedDate = tabManager.getSelectedTabDate();
            if (selectedDate != null) {
                PhotoManager.setGalleryDateToLoad(selectedDate);
                switchScene("Galery.fxml", event);
            } else {
                AlertManager.showAlert("Ошибка", "Сначала выберите день");
            }
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        switchScene("hello-view.fxml", event);
    }

    @FXML
    public void backToMainList(ActionEvent event) {
        switchScene("AllDates.fxml", event);
    }

    @FXML
    public void exitApp(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void resetToToday(ActionEvent event) {
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }
    }

    @FXML
    public void searchByDate(ActionEvent event) {
        if (searchField != null) {
            DateSearchManager.search(searchField.getText().trim(), event);
        }
    }

    @FXML
    public void searchByText(ActionEvent event) {
        String text = textSearchField.getText().trim();
        if (!text.isEmpty()) {
            SimpleTextSearchManager.search(text, event);
        } else {
            AlertManager.showAlert("Ошибка", "Введите текст для поиска");
        }
    }

    private void handleDateSelection(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            TextSearchManager.setDateToOpen(selectedDate.toString());
            if (calendarContainer.getScene() != null) {
                switchScene("AllDates.fxml", new ActionEvent(calendarContainer, null));
            }
        }
    }

    @FXML
    public void saveTab(ActionEvent event) {
        if (tabManager != null) tabManager.saveCurrentTab();
    }

    @FXML
    public void deleteTab(ActionEvent event) {
        if (tabManager != null) tabManager.deleteCurrentTab();
    }

    @FXML
    public void initGallery() {
        if (photoManager != null) photoManager.initializeGallery();
    }

    @FXML
    public void prevPhoto(ActionEvent event) {
        if (photoManager != null) photoManager.showPreviousPhoto();
    }

    @FXML
    public void nextPhoto(ActionEvent event) {
        if (photoManager != null) photoManager.showNextPhoto();
    }

    @FXML
    public void selectPhotoFromFile(ActionEvent event) {
        if (photoManager != null) photoManager.selectPhoto();
    }

    @FXML
    public void saveSelectedPhoto(ActionEvent event) {
        if (photoManager != null) photoManager.savePhotoToDisk();
    }

    @FXML
    public void deleteCurrentPhoto(ActionEvent event) {
        if (photoManager != null) photoManager.deletePhoto();
    }

    @FXML
    public void exportAndClearData(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Скачивание и полная очистка");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP-архив", "*.zip"));
        fileChooser.setInitialFileName("Recordings_" + LocalDate.now() + ".zip");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File zipFile = fileChooser.showSaveDialog(stage);

        if (zipFile != null) {
            try {
                File sourceFolder = new File("data");
                zipDirectory(sourceFolder, zipFile);
                deleteDirectory(sourceFolder);
                if (tabPane != null) {
                    tabPane.getTabs().clear();
                }
                AlertManager.showAlert("ура", "Данные выгружены в архив, локальные копии удалены.");
            } catch (IOException e) {
                AlertManager.showAlert("Ошибка", "Произошла ошибка: " + e.getMessage());
            }
        }
    }

    private void switchScene(String fxmlFile, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Node sourceNode = (Node) event.getSource();
            Stage stage;
            if (sourceNode.getScene() != null) {
                stage = (Stage) sourceNode.getScene().getWindow();
            } else {
                stage = (Stage) calendarContainer.getScene().getWindow();
            }
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            directory.delete();
        }
    }

    private void zipDirectory(File folder, File zipFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        addFolderToZip("", folder, zos);
        zos.close();
        fos.close();
    }

    private void addFolderToZip(String path, File srcFolder, ZipOutputStream zos) throws IOException {
        String[] files = srcFolder.list();
        if (files == null) return;
        for (int i = 0; i < files.length; i++) {
            String currentPath = path.isEmpty() ? files[i] : path + "/" + files[i];
            File file = new File(srcFolder, files[i]);
            if (file.isDirectory()) {
                addFolderToZip(currentPath, file, zos);
            } else {
                byte[] buf = new byte[1024];
                FileInputStream in = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(currentPath));
                int len;
                while ((len = in.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        }
    }

    @FXML
    public void uploadDirec(ActionEvent event) {
        String token = "y0__xChsowQGNuWAyC62bWnFjDHrPemCD-V6KePdrM_QyX7R9tvAGJkYMYI";
        File dataFolder = new File("data");
        new Thread(() -> {
            try {
                CloudService.uploadFolder(dataFolder, token);
                javafx.application.Platform.runLater(() -> {
                    AlertManager.showAlert("Гоотово", "на Янекс фДиске.");
                });

            }catch(Exception e){
                    javafx.application.Platform.runLater(() -> {
                        AlertManager.showAlert("Ошибка", "иди нахуй " + e.getMessage());
                    });
                }
        }).start();
    }

    @FXML
    public void UploadFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("ИПОРТ ИЗ ZИП");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip-архив", "*.zip"));

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        File zipF = fileChooser.showOpenDialog(stage);

        if (zipF != null) {
            List<String> errorsRep = new ArrayList<>();
            try {
                ArchiveManager.ZipReporting(zipF, ".", errorsRep);

                if (tabPane != null) {
                    tabPane.getTabs().clear();
                }

                if (errorsRep.isEmpty()) {

                    AlertManager.showAlert("Ура", "Пососите");
                } else {

                    StringBuilder rep = new StringBuilder("Загрузка есть но пару фалов нет");
                    for (String error : errorsRep) {
                        rep.append("- ").append(error).append("");
                    }
                    AlertManager.showAlert("Внимание говорит Москва", rep.toString());
                }

                switchScene("hello-view.fxml", actionEvent);

            } catch (IOException e) {

                AlertManager.showAlert("Aшiбачка", "пизда тебе" + e.getMessage());
            }
        }
    }
}