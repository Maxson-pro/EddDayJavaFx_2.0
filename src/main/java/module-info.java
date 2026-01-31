module org.example.edddayjavafx2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.swing;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;

    opens org.example.edddayjavafx2 to javafx.fxml, com.google.gson;
    exports org.example.edddayjavafx2;
}