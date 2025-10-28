module com.example.platformer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    exports com.example.platformer;
    opens com.example.platformer to javafx.fxml;
}