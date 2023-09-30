package iesfranciscodelosrios.acd.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    /**
     * Un conjunto (Set) que almacena los nombres de usuario conectados. Utiliza un conjunto para garantizar que no haya nombres duplicados.
     */
    private Set<String> connectedNicknames = new HashSet<>();
    private List<ClientHandler> clientHandlers = new ArrayList<>();

    /**
     * La dirección IP del servidor.
     */
    String serverIp = "172.16.16.176";
    /**
     * El puerto en el que el servidor escuchará las conexiones entrantes.
     */
    int serverPort = 8081; // Puerto del servidor

    public ChatServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort); // Puerto del servidor. Se crea un socket de servidor (ServerSocket) que escucha en el puerto serverPort especificado
            System.out.println("El servidor está escuchando en la dirección IP " + serverIp + " y el puerto " + serverPort);

            // Aceptar conexiones entrantes en un bucle infinito
            while (true) { //Entra en un bucle infinito para aceptar conexiones entrantes de clientes.
                Socket clientSocket = serverSocket.accept();
                // Crea un nuevo hilo o clase para manejar la comunicación con el cliente (UserHandler)
                // y pasa el socket del cliente y una referencia al servidor al nuevo hilo o clase.
                // Esto permite manejar múltiples clientes simultáneamente.
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clientHandlers.add(clientHandler);
                clientHandler.start();
    public synchronized boolean isNicknameAvailable(String nickname) {
        return !connectedNicknames.contains(nickname);
    }

    // Agrega un método para registrar un nuevo nickname
    public synchronized void registerNickname(String nickname) {
        connectedNicknames.add(nickname);
    }

    // Agrega un método para retirar un nickname cuando un usuario se desconecta
    public synchronized void unregisterNickname(String nickname) {
        connectedNicknames.remove(nickname);
    }

    // Agrega un método para enviar un mensaje a todos los usuarios conectados
    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    // Agrega un método para eliminar un usuario de la lista de usuarios conectados
    public synchronized void removeUser(ClientHandler clientHandler) {
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
