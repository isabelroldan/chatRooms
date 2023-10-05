package iesfranciscodelosrios.acd.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class RoomController {
    @FXML
    private Label nicknameLabel;
    @FXML
    private TextField mensajeTextField;
    @FXML
    private Label numPersonasLabel;
    @FXML
    private Label numeroSalaLabel; // Añade una etiqueta en tu vista para mostrar el número de la sala

    private int numeroSala; // Agrega este atributo

    private String nickname;

    public void initialize() {
        mensajeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Actualizar la etiqueta con el texto del TextField
            mensajeTextField.setText(newValue);
        });
    }

    public void setNickname(String nickname) {
        nicknameLabel.setText(nickname);
    }

    public void setNumeroSala(int numeroSala) {
        this.numeroSala = numeroSala;
    }

    private int obtenerNumeroDePersonasEnSala(int sala) {
        try {
            File archivoXML = new File("ruta/a/tu/archivo.xml"); // Cambia la ruta al archivo XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document documento = dBuilder.parse(archivoXML);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("/users/user[room='" + sala + "']");
            NodeList listaUsuarios = (NodeList) expr.evaluate(documento, XPathConstants.NODESET);

            return listaUsuarios.getLength();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
