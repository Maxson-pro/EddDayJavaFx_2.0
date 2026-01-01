module org.example.edddayjavafx2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.edddayjavafx2 to javafx.fxml;
    exports org.example.edddayjavafx2;
}