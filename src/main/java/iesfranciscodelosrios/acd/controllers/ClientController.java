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
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread messageReceiverThread;
    private File xmlFile = new File("src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml");

    private User clientUser; // Declarar la variable clientUser

    private TableView<Message> messageTableView;
    public ClientController() {

    }

    //public ClientController(TableView<Message> messageTableView) {
        //this.messageTableView = messageTableView;
    //}

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

    // Cambiar por GetIP si se pudiera y da tiempo
    //Se establece la dirección IP y el puerto del servidor al que se conectará el cliente
    protected String serverIp = "192.168.0.195";
    protected int serverPort = 8081;

    public void connectToServer(User client) {
        try {
            Socket clientSocket = new Socket(serverIp, serverPort);

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

    private void sendUserToServer(Socket socket,User user) {
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

    public ClientController(TableView<Message> messageTableView) {
        this.messageTableView = messageTableView;
        messageUpdater = Executors.newSingleThreadScheduledExecutor();
        startMessageUpdater();
    }

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

    public void sendMessageToServer(Message message) {
        if (clientSocket != null && out != null) {
            try {
                // Enviar el objeto Message al servidor
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
