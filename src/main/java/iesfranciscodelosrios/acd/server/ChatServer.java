package iesfranciscodelosrios.acd.server;

import iesfranciscodelosrios.acd.models.Message;
import iesfranciscodelosrios.acd.models.User;
import iesfranciscodelosrios.acd.models.Users;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    /**
     * Un conjunto (Set) que almacena los nombres de usuario conectados. Utiliza un conjunto para garantizar que no haya nombres duplicados.
     */
    private Set<String> connectedNicknames = new HashSet<>();
    /**
     * Una lista (List) que almacena instancias de la clase ClientHandler. Cada instancia manejará la comunicación con un usuario cliente.
     */
    private List<ClientHandler> clientHandlers = new ArrayList<>();

    /**
     * Nicknames in use
     */
    private Set<String> usedNicknames = new HashSet<>();

    /**
     * La dirección IP del servidor.
     */
    String serverIp = "192.168.0.195";
    /**
     * El puerto en el que el servidor escuchará las conexiones entrantes.
     */
    int serverPort = 8081; // Puerto del servidor

    private static final String XML_FILE_PATH = "src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml";

    // Atributo estático para almacenar la única instancia de ChatServer
    private static ChatServer instance;

    // Método estático para obtener la instancia única de ChatServer
    public static ChatServer getInstance() {
        if (instance == null) {
            instance = new ChatServer();
        }
        return instance;
    }

    public ChatServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort); // Puerto del servidor. Se crea un socket de servidor (ServerSocket) que escucha en el puerto serverPort especificado
            System.out.println("El servidor está escuchando en la dirección IP " + serverIp + " y el puerto " + serverPort);

            // Aceptar conexiones entrantes en un bucle infinito
            while (true) { //Entra en un bucle infinito para aceptar conexiones entrantes de clientes.
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexión aceptada");

                // Crea un nuevo hilo o clase para manejar la comunicación con el cliente (ClientHandler)
                // y pasa el socket del cliente y una referencia al servidor al nuevo hilo o clase.
                // Esto permite manejar múltiples clientes simultáneamente.
                //Es decir, Por cada nueva conexión, crea una instancia de ClientHandler, pasa el socket del cliente
                // y una referencia al servidor a esta instancia, y la inicia.
                // Esto permite que múltiples clientes se conecten y se comuniquen simultáneamente.
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserInXml(User user) {
        try {
            File file = new File(XML_FILE_PATH);
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);

            Users users;
            if (file.exists()) {
                // Si el archivo ya existe, carga los usuarios existentes
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                users = (Users) unmarshaller.unmarshal(file);
            } else {
                // Si el archivo no existe, crea una nueva lista de usuarios
                users = new Users();
            }

            // Agrega el nuevo usuario a la lista
            users.getUsers().add(user);

            // Guarda la lista actualizada en el archivo XML
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(users, new FileWriter(file));

            System.out.println("Usuario guardado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void broadcastMessage(Message message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessageToClient(message);
        }
    }

    /**
     *  Crea una instancia del servidor llamando al constructor new ChatServer(), lo que inicia el servidor y lo pone en espera de conexiones entrantes.
     * @param args argumento
     */
    public static void main(String[] args) {
        new ChatServer(); // Crea una instancia del servidor
    }
}

