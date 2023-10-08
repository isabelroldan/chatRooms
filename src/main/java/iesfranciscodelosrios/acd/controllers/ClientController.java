package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.models.Message;
import iesfranciscodelosrios.acd.models.User;
import iesfranciscodelosrios.acd.models.Users;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientController {
    // Cambiar por GetIP si se pudiera y da tiempo
    //Se establece la dirección IP y el puerto del servidor al que se conectará el cliente
    protected String serverIp = "192.168.18.13";
    protected int serverPort = 8081;

    Socket clientSocket;

    {
        try {
            clientSocket = new Socket(serverIp, serverPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PrintWriter out;
    private BufferedReader in;
    private Thread messageReceiverThread;
    private File xmlFile = new File("src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml");

    private User clientUser; // Declarar la variable clientUser

    private TableView<Message> messageTableView;

    public ClientController() {
    }

    /**
     * Checks if a user with the given nickname is logged in.
     *
     * @param nickname The nickname of the user to check.
     * @return True if a user with the given nickname is logged in; otherwise, false.
     * @throws IOException If there is an error while reading user data from the XML file.
     */
    public boolean isUserLogedIn(String nickname) throws IOException {
        boolean result = false;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Users users = (Users) unmarshaller.unmarshal(xmlFile);

            if (users != null) {
                List<User> userList = users.getUsers();
                if (userList != null) {
                    for (User user : userList) {
                        if (user.getNickname().equals(nickname)) {
                            return true;
                        }
                    }
                } else {
                    System.out.println("La lista está vacía");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Loads existing user data from an XML file, adds a new user, and returns the updated user list.
     *
     * @param newUser The user to be added.
     * @return An updated Users object containing the new user and existing users.
     */
    public Users loadUsersAndAddNewUser(User newUser) {
        Users updatedUsers = new Users();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            if (xmlFile.exists()) {
                Users users = (Users) unmarshaller.unmarshal(xmlFile);

                if (users != null) {
                    List<User> existingUsers = users.getUsers();
                    if (existingUsers != null) {
                        updatedUsers.getUsers().addAll(existingUsers);
                    }
                }
            }

            // Agregar el nuevo usuario
            updatedUsers.getUsers().add(newUser);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return updatedUsers;
    }

    /**
     * Method that save the user provided to an XML file
     *
     * @param users
     */
    public void saveUsersToXml(Users users) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(users, new FileWriter(xmlFile)); //Crea el xml
            System.out.println("Usuario guardado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the server with the specified client user and starts a thread to receive messages from the server.
     *
     * @param client The client user to connect with.
     */
    public void connectToServer(User client) {
        try {


            if (isUserLogedIn(client.getNickname()) == false) {
                // Enviar la solicitud al servidor
                sendUserToServer(clientSocket, client);

                // Iniciar el hilo para recibir mensajes del servidor
                messageReceiverThread = new Thread(() -> {
                    try {
                        // Verificar si el socket está cerrado
                        if (!clientSocket.isClosed()) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                            // Asignar el objeto User del cliente actual a clientUser
                            clientUser = client;
                            while (true) {
                                String serverMessage = in.readLine();
                                if (serverMessage != null) {
                                    // Agregar el mensaje al TableView (messageTableView) aquí
                                    Platform.runLater(() -> {
                                        // Crear un nuevo mensaje
                                        Message receivedMessage = new Message(clientUser.getNickname(), serverMessage, null);

                                        // Agregar el mensaje al TableView
                                        messageTableView.getItems().add(receivedMessage);
                                    });
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                messageReceiverThread.start();
            } else {
                disconnectFromServer();
                System.out.println("Nickname en uso");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the User object to the server for saving it in the XML file.
     *
     * @param socket The socket used for communication with the server.
     * @param user   The User object to send to the server.
     */
    private void sendUserToServer(Socket socket, User user) {
        try {
            // Enviar el objeto User al servidor
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                objectOutputStream.writeObject(user);
                objectOutputStream.flush();
                System.out.println("User pushed to server to save it in the XML file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void sendMessageToServer(String message) {
        if (out != null) {
            out.println(message);
        }
    }*/

    /**
     * Disconnects the client from the server by interrupting the messageReceiverThread, closing input and output streams, and closing the clientSocket.
     */
    public void disconnectFromServer() {
        try {
            if (messageReceiverThread != null && messageReceiverThread.isAlive()) {
                messageReceiverThread.interrupt();
            }

            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ScheduledExecutorService messageUpdater;

    /**
     * Constructs a new instance of the ClientController with a reference to the messageTableView.
     *
     * @param messageTableView The TableView used for displaying messages in the user interface.
     */
    public ClientController(TableView<Message> messageTableView) {
        this.messageTableView = messageTableView;
        messageUpdater = Executors.newSingleThreadScheduledExecutor();
        startMessageUpdater();
    }

    /**
     * Starts a scheduled task to periodically update the message table view with messages from the server.
     */
    private void startMessageUpdater() {
        messageUpdater.scheduleAtFixedRate(() -> {
            // Obtener la lista de mensajes actualizada del servidor
            List<Message> updatedMessages = getMessagesFromServer();

            // Actualizar la tabla de mensajes en la interfaz de usuario
            Platform.runLater(() -> {
                messageTableView.getItems().clear();
                messageTableView.getItems().addAll(updatedMessages);
            });
        }, 0, 1, TimeUnit.SECONDS); // Actualizar cada segundo
    }

    /**
     * Requests messages from the server, receives and deserializes them, and returns the list of messages received from the server.
     *
     * @return A list of messages received from the server.
     */
    private List<Message> getMessagesFromServer() {
        List<Message> messagesFromServer = new ArrayList<>();

        try {
            // Enviar una solicitud al servidor para obtener los mensajes.
            out.println("GET_MESSAGES"); // Envía una cadena al servidor para solicitar los mensajes

            // Configura un objeto ObjectInputStream para leer objetos serializados desde el servidor.
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

            // Leer y deserializar los mensajes del servidor.
            while (true) {
                try {
                    // Lee un objeto Message del servidor.
                    Message message = (Message) objectInputStream.readObject();

                    // Agrega el mensaje a la lista.
                    messagesFromServer.add(message);
                } catch (EOFException e) {
                    // EOFException se lanza cuando no hay más objetos para leer.
                    break; // Sal del bucle si no hay más mensajes.
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return messagesFromServer;
    }

    /**
     * Sends a message to the server by serializing and sending the Message object to the server's output stream.
     *
     * @param message The message to be sent to the server.
     */
    public void sendMessageToServer(Message message) {
        if (clientSocket != null) {
            try {
                // Enviar el objeto Message al servidor
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
                System.out.println("Message sent to server");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
