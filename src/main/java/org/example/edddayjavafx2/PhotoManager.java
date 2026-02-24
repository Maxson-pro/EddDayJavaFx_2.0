package org.example.edddayjavafx2;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PhotoManager {
    private final ImageView galleryView;
    private File tempSelectedFile = null;
    private String dataFolder = "data";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static String dateToOpen = null;
  private static String galleryDateToLoad = null;

    public PhotoManager(ImageView galleryView) {
        this.galleryView = galleryView;
    }

    public static void setGalleryDateToLoad(String date) {
        galleryDateToLoad = date;
    }

    public void initializeGallery() {
        String dateToUse;
        if (galleryDateToLoad != null) {
            dateToUse = galleryDateToLoad;
        } else {
      dateToUse = LocalDate.now().format(dateFormatter);
        }
        File[] photos = loadPhotosForDate(dateToUse);
        if (photos != null && photos.length > 0 && galleryView != null) {
            try {
                Image image = new Image("file:" + photos[0].getAbsolutePath());
     galleryView.setImage(image);
            } catch (Exception e) {
                galleryView.setImage(null);
            }
        }
    }

    public void showPreviousPhoto() {
        File[] photos = getCurrentPhotos();
 Image currentImage = getCurrentImage();
        int currentIndex = findCurrentImageIndex(photos, currentImage);
        if (photos != null && photos.length > 0 && galleryView != null) {
            if (currentIndex <= 0) {
                currentIndex = photos.length - 1;
            } else {
                currentIndex--;
            }
            try {
                Image image = new Image("file:" + photos[currentIndex].getAbsolutePath());
                galleryView.setImage(image);
     } catch (Exception e) {}
        }
    }

    public void showNextPhoto() {
        File[] photos = getCurrentPhotos();
        Image currentImage = getCurrentImage();
        int currentIndex = findCurrentImageIndex(photos, currentImage);
        if (photos != null && photos.length > 0 && galleryView != null) {
            if (currentIndex >= photos.length - 1) {
                currentIndex = 0;
            } else {
                currentIndex++;
      }
            try {
                Image image = new Image("file:" + photos[currentIndex].getAbsolutePath());
                galleryView.setImage(image);
            } catch (Exception e) {}
        }
    }

    public void selectPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите фото");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Картинки", "*.jpg", "*.jpeg", "*.png", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            tempSelectedFile = selectedFile;
            try {
        Image image = new Image("file:" + selectedFile.getAbsolutePath());
                galleryView.setImage(image);
                AlertManager.showAlert("Выбрано", "Нажмите 'Сохранить'");
            } catch (Exception e) {
                AlertManager.showAlert("Ошибка", "Не открыть фото");
            }
        }
    }

    public void savePhotoToDisk() {
        if (tempSelectedFile == null) {
            File[] photos = getCurrentPhotos();
       if (photos != null && photos.length > 0) {
                AlertManager.showAlert("Информация", " фоток " + photos.length);
            } else {
                AlertManager.showAlert("Информация", "Нет фоток ");
            }
            return;
        }
        String currentDate = (galleryDateToLoad != null) ? galleryDateToLoad : LocalDate.now().format(dateFormatter);
        try {
File dateFolder = new File(dataFolder + File.separator + "photos" + File.separator + currentDate);
            if (!dateFolder.exists()) dateFolder.mkdirs();
            String fileName = System.currentTimeMillis() + getFileExtension(tempSelectedFile.getName());
            File destFile = new File(dateFolder, fileName);
            try (FileInputStream in = new FileInputStream(tempSelectedFile);
       FileOutputStream out = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[1024];
                int length;
   while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            if (galleryView != null) {
                Image image = new Image("file:" + destFile.getAbsolutePath());
                galleryView.setImage(image);
        }
            AlertManager.showAlert("ура", "Фото заружено для " + currentDate);
            tempSelectedFile = null;
        } catch (Exception e) {
            AlertManager.showAlert("Ошибка", "Не удалось загрузить фото");
        }
    }

    public void deletePhoto() {
     Image currentImage = getCurrentImage();
        if (currentImage == null) return;
     File[] photos = getCurrentPhotos();
      if (photos == null || photos.length == 0) return;
        File fileToDelete = null;
        String currentUrl = currentImage.getUrl();
        for (File photo : photos) {
            String fileUrl = "file:" + photo.getAbsolutePath();
     if (fileUrl.replace("\\", "/").equals(currentUrl.replace("\\", "/"))) {
                fileToDelete = photo;
                break;
            }
        }
        if (fileToDelete != null) {
            if (AlertManager.showConfirmation("Удалить фото", "Удалить фото", "фото " + fileToDelete.getName())) {
                if (fileToDelete.delete()) {
            AlertManager.showAlert("Удаление", "ФОТО удалено");
                    File[] remainingPhotos = getCurrentPhotos();
                    if (remainingPhotos != null && remainingPhotos.length > 0) {
             galleryView.setImage(new Image("file:" + remainingPhotos[0].getAbsolutePath()));
                    } else {
                        galleryView.setImage(null);
                    }
                }
            }
        }
    }

    private File[] loadPhotosForDate(String date) {
        File dateFolder = new File(dataFolder + File.separator + "photos" + File.separator + date);
        if (dateFolder.exists() && dateFolder.isDirectory()) {
         return dateFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
               String lower = name.toLowerCase();
                    return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                            lower.endsWith(".png") || lower.endsWith(".gif");
                }
            });
        }
        return new File[0];
    }

    private File[] getCurrentPhotos() {
        String date = (galleryDateToLoad != null) ? galleryDateToLoad : LocalDate.now().format(dateFormatter);
        return loadPhotosForDate(date);
    }

    private Image getCurrentImage() {
        return (galleryView != null) ? galleryView.getImage() : null;
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

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
         return fileName.substring(dotIndex);
        }
        return ".jpg";
    }
}