package iesfranciscodelosrios.acd.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

    @FXML
    private Label room1Label;
    @FXML
    private Label room2Label;
    @FXML
    private Label room3Label;
    @FXML
    private Label room4Label;
    @FXML
    private Label room5Label;




    private Rectangle rectanguloSeleccionado = null;

    @FXML
    public void initialize() {
        // Agrega un controlador de eventos de clic para cada rectángulo
        sala1Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala2Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala3Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala4Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala5Rect.setOnMouseClicked(this::cambiarColorRectangulo);

        //Cargar nombres de salas desde el archivo XML
        loadRoomNamesFromXML();
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

    private void loadRoomNamesFromXML() {
        try {
            // Cargar el archivo XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("src/main/resources/iesfranciscodelosrios/acd/Xmls/Rooms.xml");

            // Obtener la lista de elementos de sala
            NodeList boards = doc.getElementsByTagName("room");

            // Asignar nombres de salas a las etiquetas en la vista
            room1Label.setText(boards.item(0).getTextContent());
            room2Label.setText(boards.item(1).getTextContent());
            room3Label.setText(boards.item(2).getTextContent());
            room4Label.setText(boards.item(3).getTextContent());
            room5Label.setText(boards.item(4).getTextContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}