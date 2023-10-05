package iesfranciscodelosrios.acd.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @FXML
    private Label room1ConnectedLabel;

    @FXML
    private Label room2ConnectedLabel;

    @FXML
    private Label room3ConnectedLabel;

    @FXML
    private Label room4ConnectedLabel;

    @FXML
    private Label room5ConnectedLabel;


    @FXML
    private Label nicknameLabel;

    private Rectangle rectanguloSeleccionado = null;

    // Ruta al archivo XML de usuarios
    private static final String RUTA_XML_USUARIOS = "src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml";

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

        // Actualizar el número de personas conectadas al inicializar la vista
        actualizarNumeroConectados();
    }

    public void setNickname(String nickname) {
        nicknameLabel.setText(nickname);
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

    // Este método obtendrá el número de personas conectadas a varias salas
    public Map<String, Integer> obtenerNumeroConectados(List<String> nombresSalas) {
        Map<String, Integer> numeroConectadosPorSala = new HashMap<>();

        try {
            // Cargar el archivo XML de usuarios
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Obtener la lista de nodos de usuario
            NodeList userList = doc.getElementsByTagName("user");

            // Inicializar el contador para cada sala
            for (String nombreSala : nombresSalas) {
                numeroConectadosPorSala.put(nombreSala, 0);
            }

            // Recorrer la lista de usuarios y contar aquellos conectados a las salas especificadas
            for (int i = 0; i < userList.getLength(); i++) {
                Node userNode = userList.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;

                    // Obtener el nombre de las salas a las que está conectado este usuario
                    NodeList roomList = userElement.getElementsByTagName("room");
                    for (int j = 0; j < roomList.getLength(); j++) {
                        Node roomNode = roomList.item(j);
                        if (roomNode.getNodeType() == Node.ELEMENT_NODE) {
                            String salaUsuario = roomNode.getTextContent();

                            // Verificar si esta sala está en la lista de salas proporcionada
                            if (nombresSalas.contains(salaUsuario)) {
                                // Incrementar el contador para esta sala
                                int contadorSala = numeroConectadosPorSala.get(salaUsuario);
                                numeroConectadosPorSala.put(salaUsuario, contadorSala + 1);
                            }
                        }
                    }
                }
            }

            return numeroConectadosPorSala;
        } catch (Exception e) {
            // Manejo de excepciones en caso de error al cargar el archivo XML, etc.
            e.printStackTrace();
            // Devolver 0 para todas las salas en caso de error
            for (String nombreSala : nombresSalas) {
                numeroConectadosPorSala.put(nombreSala, 0);
            }
            return numeroConectadosPorSala;
        }
    }

    public void actualizarNumeroConectados() {
        List<String> nombresSalas = Arrays.asList("Room 1", "Room 2", "Room 3", "Room 4", "Room 5");
        Map<String, Integer> numeroConectadosPorSala = obtenerNumeroConectados(nombresSalas);

        // Actualizar las etiquetas con el número de personas conectadas
        room1ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 1")));
        room2ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 2")));
        room3ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 3")));
        room4ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 4")));
        room5ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 5")));
    }

    @FXML
    private void unirseASala() {
        if (rectanguloSeleccionado != null) {
            int nombreSala = obtenerNumeroSala(rectanguloSeleccionado);
            agregarUsuarioASala(String.valueOf(nombreSala));

            // Obtén la escena actual
            Scene scene = rectanguloSeleccionado.getScene();

            // Cargar la vista de sala (room.fxml) y establecer el controlador
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iesfranciscodelosrios/acd/room.fxml"));
            Parent root;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                return; // Manejar el error de carga de vista
            }

            // Cambia la escena actual a la vista de sala
            scene.setRoot(root);
        }
    }

    private int obtenerNumeroSala(Rectangle rectangulo) {
        // Obtener el ID del rectángulo
        String id = rectangulo.getId();

        // Verificar el ID y devolver el nombre de la sala correspondiente
        if ("sala1Rect".equals(id)) {
            return 1;
        } else if ("sala2Rect".equals(id)) {
            return 2;
        } else if ("sala3Rect".equals(id)) {
            return 3;
        } else if ("sala4Rect".equals(id)) {
            return 4;
        } else if ("sala5Rect".equals(id)) {
            return 5;
        } else {
            // Si el ID no coincide con ningún rectángulo conocido, puedes devolver un valor predeterminado o manejarlo según tus necesidades.
            return 0;
        }
    }

    private void agregarUsuarioASala(String nombreSala) {
        try {
            // Cargar el archivo XML de usuarios
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Obtener la lista de nodos de usuario
            NodeList userList = doc.getElementsByTagName("user");

            // Iterar a través de los nodos de usuario para encontrar el usuario actual (basado en su nombre de usuario)
            for (int i = 0; i < userList.getLength(); i++) {
                Node userNode = userList.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;

                    // Obtener el nombre de usuario del nodo actual
                    String nombreUsuario = userElement.getElementsByTagName("nickname").item(0).getTextContent();

                    // Comparar el nombre de usuario actual con el nombre de usuario del usuario que está en sesión
                    if (nombreUsuario.equals(nicknameLabel.getText())) {
                        // Crear un nuevo nodo "room" para la sala y asignarle el nombre de la sala
                        Element roomElement = doc.createElement("room");
                        roomElement.appendChild(doc.createTextNode(nombreSala));

                        // Agregar la sala al nodo del usuario
                        userElement.appendChild(roomElement);

                        // Guardar los cambios en el archivo XML
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(file);
                        transformer.transform(source, result);

                        // Salir del bucle después de encontrar el usuario actual y agregar la sala
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Manejo de excepciones en caso de error al cargar o modificar el archivo XML, etc.
            e.printStackTrace();
        }
    }

}