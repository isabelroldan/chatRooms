package iesfranciscodelosrios.acd.server;

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
    String serverIp = "192.168.16.108";
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

    private ChatServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort); // Puerto del servidor. Se crea un socket de servidor (ServerSocket) que escucha en el puerto serverPort especificado
            System.out.println("El servidor está escuchando en la dirección IP " + serverIp + " y el puerto " + serverPort);

            // Aceptar conexiones entrantes en un bucle infinito
            while (true) { //Entra en un bucle infinito para aceptar conexiones entrantes de clientes.
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexión aceptada");
                // Enviar el contenido del XML al cliente
               sendXmlFile(clientSocket);

                // Crea un nuevo hilo o clase para manejar la comunicación con el cliente (ClientHandler)
                // y pasa el socket del cliente y una referencia al servidor al nuevo hilo o clase.
                // Esto permite manejar múltiples clientes simultáneamente.
                //Es decir, Por cada nueva conexión, crea una instancia de ClientHandler, pasa el socket del cliente
                // y una referencia al servidor a esta instancia, y la inicia. Esto permite que múltiples clientes se conecten y se comuniquen simultáneamente.
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

    // Agrega un método para enviar un mensaje a todos los usuarios conectados
    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    // Agrega un método para eliminar un usuario de la lista de usuarios conectados
    public synchronized void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    /**
     *  Crea una instancia del servidor llamando al constructor new ChatServer(), lo que inicia el servidor y lo pone en espera de conexiones entrantes.
     * @param args argumento
     */
    public static void main(String[] args) {
        new ChatServer(); // Crea una instancia del servidor
    }
}