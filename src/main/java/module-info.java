module iesfranciscodelosrios.acd {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.xml.bind;

    /*opens iesfranciscodelosrios.acd to javafx.fxml;*/
    exports iesfranciscodelosrios.acd;
    exports iesfranciscodelosrios.acd.controllers;
    opens iesfranciscodelosrios.acd.controllers to javafx.fxml;
    opens iesfranciscodelosrios.acd.models to jakarta.xml.bind;
}
