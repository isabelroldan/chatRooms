package iesfranciscodelosrios.acd.server;

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
    String serverIp = "192.168.18.13";
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
                // Enviar el contenido del XML al cliente
              //  sendXmlFile(clientSocket);

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

    private static void sendXmlFile(Socket clientSocket) throws IOException {
        File xmlFile = new File(XML_FILE_PATH);
        if (!xmlFile.exists()) {
            System.err.println("El archivo XML no existe en la ruta especificada.");
            return;
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(xmlFile));
             OutputStream os = clientSocket.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            // Leer del archivo y enviar al cliente en bloques
            while ((bytesRead = bis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                os.flush();
            }

            System.out.println("Archivo XML enviado al cliente.");
        }
    }

    public static void saveUserInXml(User user) {
        try {
            // Leer el archivo XML existente
            File xmlFile = new File(XML_FILE_PATH);
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Users users = (Users) unmarshaller.unmarshal(xmlFile);

            // Agregar el nuevo usuario
            if (users != null) {
                ArrayList<User> usersList = users.getUsers();
                usersList.add(user);
                users.setUsers(usersList);
            }

            // Marshalling y guardar en el archivo XML
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(users, xmlFile);

            System.out.println("Usuario guardado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ClientHandlerAux extends Thread {
        private Socket clientSocket;

        @Override
        public void run() {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream())) {
                // Recibir el objeto User del cliente
                User receivedUser = (User) objectInputStream.readObject();
                System.out.println("Usuario recibido del cliente: " + receivedUser);

                // Guardar el usuario en el archivo XML
                saveUserInXml(receivedUser);

                // Otros procesos
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Agrega un método para verificar si un nickname está en uso
    public synchronized boolean isNicknameAvailable(String nickname) {
        return usedNicknames.contains(nickname);
    }

    // Agrega un método para registrar un nuevo nickname
    public synchronized void registerNickname(String nickname) {
        usedNicknames.add(nickname);
    }

    // Agrega un método para retirar un nickname cuando un usuario se desconecta
    public synchronized void unregisterNickname(String nickname) {
        usedNicknames.remove(nickname);
    }

    /**
     *  Crea una instancia del servidor llamando al constructor new ChatServer(), lo que inicia el servidor y lo pone en espera de conexiones entrantes.
     * @param args argumento
     */
    public static void main(String[] args) {
        new ChatServer(); // Crea una instancia del servidor
    }
}

