package iesfranciscodelosrios.acd.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class BoardController {

    @FXML
    private Rectangle sala1Rect;
    @FXML
    private Rectangle sala2Rect;
    @FXML
    private Rectangle sala3Rect;
    @FXML
    private Rectangle sala4Rect;
    @FXML
    private Rectangle sala5Rect;

    private Rectangle rectanguloSeleccionado = null;

    @FXML
    public void initialize() {
        // Agrega un controlador de eventos de clic para cada rectángulo
        sala1Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala2Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala3Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala4Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala5Rect.setOnMouseClicked(this::cambiarColorRectangulo);
    }

    // Método para cambiar el color del rectángulo cuando se hace clic
    private void cambiarColorRectangulo(MouseEvent event) {
        Rectangle rectangulo = (Rectangle) event.getSource();

        if (rectangulo != rectanguloSeleccionado) {
            // Si se hace clic en un rectángulo diferente al seleccionado, lo seleccionamos
            if (rectanguloSeleccionado != null) {
                rectanguloSeleccionado.setFill(Color.WHITE); // Deseleccionamos el rectángulo previamente seleccionado
            }
            rectanguloSeleccionado = rectangulo;
            rectangulo.setFill(Color.LIGHTGRAY); // Seleccionamos el nuevo rectángulo
        } else {
            // Si se hace clic en el rectángulo ya seleccionado, lo deseleccionamos
            rectanguloSeleccionado.setFill(Color.WHITE);
            rectanguloSeleccionado = null;
        }
    }

}