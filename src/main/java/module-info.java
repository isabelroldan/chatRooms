module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens iesfranciscodelosrios.acd to javafx.fxml;
    exports iesfranciscodelosrios.acd;
    exports iesfranciscodelosrios.acd.controllers;
    opens iesfranciscodelosrios.acd.controllers to javafx.fxml;
}
