package org.example.edddayjavafx2;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertManager {
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(header);
        confirm.setContentText(content);
        return confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
