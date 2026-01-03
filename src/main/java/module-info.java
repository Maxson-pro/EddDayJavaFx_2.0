module org.example.edddayjavafx2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.edddayjavafx2 to javafx.fxml;
    exports org.example.edddayjavafx2;
}