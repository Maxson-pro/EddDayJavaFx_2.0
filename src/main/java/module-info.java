module org.example.edddayjavafx2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;


    opens org.example.edddayjavafx2 to javafx.fxml;
    exports org.example.edddayjavafx2;
}