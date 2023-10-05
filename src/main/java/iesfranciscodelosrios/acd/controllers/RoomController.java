package iesfranciscodelosrios.acd.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
    private Label numeroSalaLabel;

    private int numeroSala; // Agrega este atributo
    private static final String RUTA_XML_USUARIOS = "src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml";

    private String nickname;

    public void initialize() {
        mensajeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Actualizar la etiqueta con el texto del TextField
            mensajeTextField.setText(newValue);
        });


        // Carga el archivo XML de usuarios y busca el número de personas en esta sala
        int numeroPersonasEnSala = obtenerNumeroPersonasEnSala(numeroSala);

        // Actualiza la vista con el número de personas en esta sala
        numPersonasLabel.setText(String.valueOf(numeroPersonasEnSala));

    }

    public void setNickname(String nickname) {
        nicknameLabel.setText(nickname);
    }

    public void setNumeroSala(int numeroSala) {
        numeroSalaLabel.setText(String.valueOf(numeroSala));
    }

    private int obtenerNumeroPersonasEnSala(int numeroSala) {
        try {
            // Cargar el archivo XML de usuarios
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Obtener la lista de nodos de usuario
            NodeList userList = doc.getElementsByTagName("user");

            // Inicializar el contador para el número de personas en la sala
            int contadorPersonas = 0;

            // Iterar a través de los nodos de usuario para contar aquellos en la sala especificada
            for (int i = 0; i < userList.getLength(); i++) {
                Node userNode = userList.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;

                    // Obtener el número de sala del nodo actual
                    String salaUsuario = userElement.getElementsByTagName("room").item(0).getTextContent();

                    // Verificar si esta sala coincide con la sala especificada
                    if (Integer.parseInt(salaUsuario) == numeroSala) {
                        contadorPersonas++;
                    }
                }
            }

            return contadorPersonas;
        } catch (Exception e) {
            // Manejo de excepciones en caso de error al cargar el archivo XML, etc.
            e.printStackTrace();
            // En caso de error, devolver 0 como número de personas
            return 0;
        }
    }
}
