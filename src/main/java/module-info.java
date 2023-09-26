module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens iesfranciscodelosrios.acd to javafx.fxml;
    exports iesfranciscodelosrios.acd;
}
