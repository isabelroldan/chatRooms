package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.models.Message;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @FXML
    private TableView<Message> messageTableView;
    @FXML
    private TableColumn<Message, String> nicknameColumn;
    @FXML
    private TableColumn<Message, String> messageColumn;
    @FXML
    private TableColumn<Message, String> timestampColumn;

    private ObservableList<Message> messages = FXCollections.observableArrayList();

    private Socket socket = new Socket();

    private int numeroSala; // Agrega este atributo
    private static final String RUTA_XML_USUARIOS = "src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml";

    private String nickname;

    private ScheduledExecutorService executorService;

    // Cuando creas una instancia de ClientController, pasa la referencia del TableView
    ClientController clientController = new ClientController();

    public void initialize() {
        mensajeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Update the label with the text from the TextField
            mensajeTextField.setText(newValue);
        });

        //Load the users XML file and find the number of people in this room
        int numeroPersonasEnSala = obtenerNumeroPersonasEnSala(numeroSala);


        // Updates the view with the number of people in this room
        numPersonasLabel.setText(String.valueOf(numeroPersonasEnSala));


        // Initialize and configure the executorService to run the update every 5 seconds (adjust the interval according to your needs)
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::actualizarUsuariosEnSala, 0, 5, TimeUnit.SECONDS);


        // Configurar las columnas para mostrar los datos de los mensajes
        nicknameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNickname()));
        messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));
        timestampColumn.setCellValueFactory(cellData -> {
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")); // Formatear como desees
            return new SimpleStringProperty(formattedTimestamp);
        });

        // Asignar la lista de mensajes a la tabla
        messageTableView.setItems(messages);
    }

    /**
     * Sets the nickname for the user and updates the UI.
     *
     * @param nickname The new nickname to set.
     */
    public void setNickname(String nickname) {
        nicknameLabel.setText(nickname);
    }

    /**
     * Sets the room number and updates the UI.
     *
     * @param numeroSala The new room number to set.
     */
    public void setNumeroSala(int numeroSala) {
        numeroSalaLabel.setText(String.valueOf(numeroSala));

        // Actualiza el número de sala
        this.numeroSala = numeroSala;

        // Llama a actualizarUsuariosEnSala con el valor de numeroSala
        actualizarUsuariosEnSala();
    }

    /**
     *
     Gets the number of people per room
     * @param numeroSala
     * @return number of person conect to this room
     */
    private int obtenerNumeroPersonasEnSala(int numeroSala) {
        try {
            //Upload the users XML file
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            //Get list of user nodes
            NodeList userList = doc.getElementsByTagName("user");

            // Initialize the counter for the number of people in the room
            int contadorPersonas = 0;

            //Iterate through user nodes to count those in the specified room
            for (int i = 0; i < userList.getLength(); i++) {
                Node userNode = userList.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;

                    // Get the room number of the current node
                    String salaUsuario = userElement.getElementsByTagName("room").item(0).getTextContent();

                    // Check if this room matches the specified room
                    if (Integer.parseInt(salaUsuario) == numeroSala) {
                        contadorPersonas++;
                    }
                }
            }

            return contadorPersonas;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     *
     Update users in the room
     */
    private void actualizarUsuariosEnSala() {
        //Clear the TableView before loading new data
        usersTableView.getItems().clear();

        // Get your nickname from the nicknameLabel
        String tuNombre = nicknameLabel.getText();
        int numeroSalaReal = Integer.parseInt(numeroSalaLabel.getText());
        System.out.println("numero de sala real       :          "+numeroSalaReal);

        // Reload usernames into the room with both sala number and your nickname
        cargarNombresUsuariosEnSala(numeroSalaReal, tuNombre);
    }

    /**
     *
     Load the user's nickname in the table on the right
     * @param numeroSala
     */
    /**
     * Cargar el apodo del usuario en la tabla de la derecha
     * @param numeroSala
     */
    private void cargarNombresUsuariosEnSala(int numeroSala, String tuNombre) {
        try {
            // Upload the users XML file
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Get the root element of the XML, which is <Users>
            Element root = doc.getDocumentElement();

            // Get a list of all <User> elements under <Users>
            NodeList userList = root.getElementsByTagName("User");

            // Create a list to store the names of users in the room
            List<String> usuariosEnSala = new ArrayList<>();

            // Iterate through the <User> elements to get the names of users in the specified room
            for (int i = 0; i < userList.getLength(); i++) {
                Node userNode = userList.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;

                    // Obtener el elemento <room> del usuario actual
                    Element roomElement = (Element) userElement.getElementsByTagName("room").item(0);

                    // Verificar si esta sala coincide con la sala especificada
                    if (roomElement != null && roomElement.getTextContent() != null) {
                        String salaUsuario = roomElement.getTextContent();
                        if (Integer.parseInt(salaUsuario) == numeroSala) {
                            // Obtener el elemento <Nickname> del usuario actual
                            Element nicknameElement = (Element) userElement.getElementsByTagName("Nickname").item(0);

                            // Verificar si el elemento <Nickname> existe y tiene contenido de texto
                            if (nicknameElement != null && nicknameElement.getTextContent() != null) {
                                String nombreUsuario = nicknameElement.getTextContent();
                                usuariosEnSala.add(nombreUsuario);
                            }
                        }
                    }
                }
            }

            // Agrega tu propio nombre si estás en la sala actual
            usuariosEnSala.add(tuNombre);

            // Clear the TableView before adding the new data
            usersTableView.getItems().clear();

            // Add user names to the TableView
            usersTableView.getItems().addAll(usuariosEnSala);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to go back to the previous view
     */
    public void retrocederAVistaAnterior() {
        //Delete the current room of the current user
        eliminarSalaDelUsuarioActual();

        //Get the current window
        Stage stage = (Stage) backButton.getScene().getWindow();

        try {
            //Load the previous view (replace "PreviousView.fxml" with the name of your previous FXML file)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iesfranciscodelosrios/acd/board.fxml"));
            Parent root = loader.load();

            //Get the "Board" view controller
            BoardController boardController = loader.getController();

            // Set the nickname in the "board" view controller
            boardController.setNickname(nicknameLabel.getText());

            //Set up the new scene with the "board" view
            Scene scene = new Scene(root);

            // Set the new scene on the stage
            stage.setScene(scene);

            // Shows the new "board" view
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     Delete the room that appears in the xml document
     */
    private void eliminarSalaDelUsuarioActual() {
        try {
            File file = new File(RUTA_XML_USUARIOS);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            // Search for the user with the current nickname
            String nicknameActual = nicknameLabel.getText();
            String expression = String.format("//User[Nickname='%s']/room", nicknameActual);
            XPathExpression xPathExpr = xpath.compile(expression);
            Node roomNode = (Node) xPathExpr.evaluate(doc, XPathConstants.NODE);

            // If the roomNode is found, remove it
            if (roomNode != null) {
                roomNode.getParentNode().removeChild(roomNode);

                // Save changes to the XML file
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

    /**
     * Adds a message to the list of messages and updates the TableView to display the new message.
     *
     * @param message The message to be added.
     */
    public void addMessage(Message message) {
        // Agrega el mensaje a la lista de mensajes
        messages.add(message);

        // Actualiza el TableView para mostrar el nuevo mensaje
        messageTableView.getItems().add(message);
    }

    /**
     * Método para enviar mensajes al servidor
     */
    @FXML
    public void enviarMensaje() {
        // Obtén el contenido del mensaje desde el TextField
        String contenidoMensaje = mensajeTextField.getText();

        // Obtén el texto del nicknameLabel
        String nicknameUsuario = nicknameLabel.getText();

        // Obtén la hora actual
        LocalDateTime horaEnvio = LocalDateTime.now();

        // Crea un objeto Message con los datos
        Message mensaje = new Message(nicknameUsuario, contenidoMensaje, horaEnvio);

        // Limpia el TextField después de enviar el mensaje
        mensajeTextField.clear();

        // Actualiza el TableView para mostrar el nuevo mensaje
        messageTableView.getItems().add(mensaje);

        // Utiliza el ClientController para enviar el mensaje al servidor
        clientController.sendMessageToServer(mensaje);

        clientController.getMessageFromServer();
    }





    /**
     * Receives a message and adds it to the list of messages. It also updates the TableView to display the new message.
     *
     * @param message The message to be received and added.
     */
    public void recibirMensaje(Message message) {
        // Agrega el mensaje recibido a la lista de mensajes
        messages.add(message);

        // Actualiza el TableView para mostrar el nuevo mensaje
        messageTableView.getItems().add(message);
    }




}

