package iesfranciscodelosrios.acd.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RoomController {
    @FXML
    private Label nicknameLabel;
    @FXML
    private TextField mensajeTextField;
    @FXML
    private Label numPersonasLabel;
    @FXML
    private Label numeroSalaLabel;
    @FXML
    private TableView<String> usersTableView;
    @FXML
    private Button backButton; // Agrega el botón

    private int numeroSala; // Agrega este atributo
    private static final String RUTA_XML_USUARIOS = "src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml";

    private String nickname;

    private ScheduledExecutorService executorService;

    public void initialize() {
        mensajeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Actualizar la etiqueta con el texto del TextField
            mensajeTextField.setText(newValue);
        });


        // Carga el archivo XML de usuarios y busca el número de personas en esta sala
        int numeroPersonasEnSala = obtenerNumeroPersonasEnSala(numeroSala);

        // Actualiza la vista con el número de personas en esta sala
        numPersonasLabel.setText(String.valueOf(numeroPersonasEnSala));


        // Inicializa y configura el executorService para ejecutar la actualización cada 5 segundos (ajusta el intervalo según tus necesidades)
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::actualizarUsuariosEnSala, 0, 5, TimeUnit.SECONDS);

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

    private void actualizarUsuariosEnSala() {
        // Limpia la TableView antes de cargar los nuevos datos
        usersTableView.getItems().clear();

        // Vuelve a cargar los nombres de usuarios en la sala
        cargarNombresUsuariosEnSala(numeroSala);
    }

    private void cargarNombresUsuariosEnSala(int numeroSala) {
        try {
            // Cargar el archivo XML de usuarios
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Obtener la lista de nodos de usuario
            NodeList userList = doc.getElementsByTagName("user");

            // Crear una lista para almacenar los nombres de los usuarios en la sala
            List<String> usuariosEnSala = new ArrayList<>();

            // Iterar a través de los nodos de usuario para obtener los nombres de los usuarios en la sala especificada
            for (int i = 0; i < userList.getLength(); i++) {
                Node userNode = userList.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;

                    // Obtener el número de sala del nodo actual
                    String salaUsuario = userElement.getElementsByTagName("room").item(0).getTextContent();

                    // Verificar si esta sala coincide con la sala especificada
                    if (Integer.parseInt(salaUsuario) == numeroSala) {
                        String nombreUsuario = userElement.getElementsByTagName("nickname").item(0).getTextContent();
                        usuariosEnSala.add(nombreUsuario);
                    }
                }
            }

            // Agregar los nombres de los usuarios a la TableView
            usersTableView.getItems().addAll(usuariosEnSala);
        } catch (Exception e) {
            // Manejo de excepciones en caso de error al cargar el archivo XML, etc.
            e.printStackTrace();
        }
    }

    // Método para retroceder a la vista anterior
    public  void retrocederAVistaAnterior() {
        // Elimina la sala actual del usuario actual
        eliminarSalaDelUsuarioActual();

        // Obtén la ventana actual
        Stage stage = (Stage) backButton.getScene().getWindow();

        try {
            // Carga la vista anterior (reemplaza "VistaAnterior.fxml" con el nombre de tu archivo FXML anterior)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iesfranciscodelosrios/acd/board.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eliminarSalaDelUsuarioActual() {
        try {
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            // Busca el usuario con el nickname actual
            String nicknameActual = nicknameLabel.getText();
            String expression = String.format("//user[nickname='%s']", nicknameActual);
            XPathExpression xPathExpr = xpath.compile(expression);
            Node userNode = (Node) xPathExpr.evaluate(doc, XPathConstants.NODE);

            // Si se encuentra el usuario, elimina la etiqueta <room> de ese usuario
            if (userNode != null) {
                Element userElement = (Element) userNode;
                NodeList roomNodes = userElement.getElementsByTagName("room");

                if (roomNodes.getLength() > 0) {
                    Node roomNode = roomNodes.item(0);
                    userElement.removeChild(roomNode);
                }

                // Guarda los cambios en el archivo XML
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
