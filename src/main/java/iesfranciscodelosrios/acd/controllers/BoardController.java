package iesfranciscodelosrios.acd.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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

    // Path to the XML file for users
    private static final String RUTA_XML_USUARIOS = "src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml";

    @FXML
    public void initialize() {
        // Add a click event handler for each rectangle
        sala1Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala2Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala3Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala4Rect.setOnMouseClicked(this::cambiarColorRectangulo);
        sala5Rect.setOnMouseClicked(this::cambiarColorRectangulo);

        // Load room names from the XML file
        loadRoomNamesFromXML();

        // Update the number of connected users when initializing the view
        actualizarNumeroConectados();
    }

    /**
     * Set the nickname label with the provided nickname.
     *
     * @param nickname The nickname to display.
     */
    public void setNickname(String nickname) {
        nicknameLabel.setText(nickname);
    }

    // Method to change the color of the rectangle when clicked
    private void cambiarColorRectangulo(MouseEvent event) {
        Rectangle rectangulo = (Rectangle) event.getSource();

        if (rectangulo != rectanguloSeleccionado) {
            // If a different rectangle is clicked, select it
            if (rectanguloSeleccionado != null) {
                rectanguloSeleccionado.setFill(Color.WHITE); // Deselect the previously selected rectangle
            }
            rectanguloSeleccionado = rectangulo;
            rectangulo.setFill(Color.LIGHTGRAY); // Select the new rectangle
        } else {
            // If the already selected rectangle is clicked again, deselect it
            rectanguloSeleccionado.setFill(Color.WHITE);
            rectanguloSeleccionado = null;
        }
    }

    /**
     * Load the names of room
     */
    private void loadRoomNamesFromXML() {
        try {
            // Load the XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("src/main/resources/iesfranciscodelosrios/acd/Xmls/Rooms.xml");

            // Get the list of room elements
            NodeList boards = doc.getElementsByTagName("room");

            // Assign room names to the labels in the view
            room1Label.setText(boards.item(0).getTextContent());
            room2Label.setText(boards.item(1).getTextContent());
            room3Label.setText(boards.item(2).getTextContent());
            room4Label.setText(boards.item(3).getTextContent());
            room5Label.setText(boards.item(4).getTextContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will get the number of users connected to various rooms
     * @param nombresSalas
     * @return Number of person conect
     */
    public Map<String, Integer> obtenerNumeroConectados(List<String> nombresSalas) {
        Map<String, Integer> numeroConectadosPorSala = new HashMap<>();

        try {
            // Load the users XML file
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Get list of user nodes
            NodeList userList = doc.getElementsByTagName("user");

            // Initialize the counter for each room
            for (String nombreSala : nombresSalas) {
                numeroConectadosPorSala.put(nombreSala, 0);
            }

            //Scroll through the list of users and count those connected to the specified rooms
            for (int i = 0; i < userList.getLength(); i++) {
                Node userNode = userList.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;

                    //Get the name of the rooms this user is connected to
                    NodeList roomList = userElement.getElementsByTagName("room");
                    for (int j = 0; j < roomList.getLength(); j++) {
                        Node roomNode = roomList.item(j);
                        if (roomNode.getNodeType() == Node.ELEMENT_NODE) {
                            String salaUsuario = roomNode.getTextContent();

                            // Check if this room is in the provided room list
                            if (nombresSalas.contains(salaUsuario)) {
                                // Increase the counter for this room
                                int contadorSala = numeroConectadosPorSala.get(salaUsuario);
                                numeroConectadosPorSala.put(salaUsuario, contadorSala + 1);
                            }
                        }
                    }
                }
            }

            return numeroConectadosPorSala;
        } catch (Exception e) {
            // Exception handling in case of error loading XML file, etc.
            e.printStackTrace();
            // Return 0 for all rooms on error
            for (String nombreSala : nombresSalas) {
                numeroConectadosPorSala.put(nombreSala, 0);
            }
            return numeroConectadosPorSala;
        }
    }

    /**
     *
     Update numbers of connected people
     */
    public void actualizarNumeroConectados() {
        List<String> nombresSalas = Arrays.asList("Room 1", "Room 2", "Room 3", "Room 4", "Room 5");
        Map<String, Integer> numeroConectadosPorSala = obtenerNumeroConectados(nombresSalas);

        // Update tags with the number of people connected
        room1ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 1")));
        room2ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 2")));
        room3ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 3")));
        room4ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 4")));
        room5ConnectedLabel.setText(String.valueOf(numeroConectadosPorSala.get("Room 5")));
    }

    /**
     *
     Saves the room to which you have connected in the XML
     */
    @FXML
    private void unirseASala() {
        if (rectanguloSeleccionado != null) {
            int nombreSala = obtenerNumeroSala(rectanguloSeleccionado);
            agregarUsuarioASala(String.valueOf(nombreSala));

            // Get the current scene
            Scene scene = rectanguloSeleccionado.getScene();

            //Load the room view (room.fxml) and set the controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iesfranciscodelosrios/acd/room.fxml"));
            try {
                // Load the "room" view
                Parent roomParent = loader.load();

                //Get the "room" view controller
                RoomController roomController = loader.getController();

                //Pass the nickname to the "room" view controller
                roomController.setNickname(nicknameLabel.getText());

                // Pass the nickname to the "room" view controller
                roomController.setNumeroSala(nombreSala);

                // Create a new scene with the "room" view
                Scene roomScene = new Scene(roomParent);

                //Get the current scenario
                Stage stage = (Stage) sala1Rect.getScene().getWindow();

                // Set the new scene on the stage
                stage.setScene(roomScene);

                //Show the new "room" view
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the room number
     * @param rectangulo contends id
     * @return number room
     */
    private int obtenerNumeroSala(Rectangle rectangulo) {
        // Get the ID of the rectangle
        String id = rectangulo.getId();

        // Verify ID and return corresponding room name
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

            return 0;
        }
    }

    /**
     *
     Add in the xml the number of the room to which you have connected
     * @param nombreSala
     */
    private void agregarUsuarioASala(String nombreSala) {
        try {
            // Upload the users XML file
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Get list of user nodes
            NodeList userList = doc.getElementsByTagName("user");

            // Iterate through the user nodes to find the current user (based on their username)
            for (int i = 0; i < userList.getLength(); i++) {
                Node userNode = userList.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;

                    //Get username of current node
                    String nombreUsuario = userElement.getElementsByTagName("nickname").item(0).getTextContent();

                    // Compare the current username with the username of the currently logged in user
                    if (nombreUsuario.equals(nicknameLabel.getText())) {
                        //Create a new "room" node for the room and assign it the name of the room
                        Element roomElement = doc.createElement("room");
                        roomElement.appendChild(doc.createTextNode(nombreSala));

                        //Add the room to the user node
                        userElement.appendChild(roomElement);

                        // Save changes to XML file
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(file);
                        transformer.transform(source, result);

                        // Exit loop after finding current user and adding room
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}