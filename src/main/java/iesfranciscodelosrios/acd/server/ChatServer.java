package iesfranciscodelosrios.acd.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private Set<String> connectedNicknames = new HashSet<>();
    private List<UserHandler> userHandlers = new ArrayList<>();

    String serverIp = "172.16.16.176"; // Dirección IP del servidor
    int serverPort = 8081; // Puerto del servidor

    public ChatServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort); // Puerto del servidor
            System.out.println("El servidor está escuchando en la dirección IP " + serverIp + " y el puerto " + serverPort);

            // Aceptar conexiones entrantes en un bucle infinito
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Crea un nuevo hilo o clase para manejar la comunicación con el cliente (UserHandler)
                // y pasa el socket del cliente y una referencia al servidor al nuevo hilo o clase.
                // Esto permite manejar múltiples clientes simultáneamente.
                UserHandler userHandler = new UserHandler(clientSocket, this);
                userHandlers.add(userHandler);
                userHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Agrega un método para verificar si un nickname está en uso
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
        for (UserHandler userHandler : userHandlers) {
            userHandler.sendMessage(message);
        }
    }

    // Agrega un método para eliminar un usuario de la lista de usuarios conectados
    public synchronized void removeUser(UserHandler userHandler) {
        userHandlers.remove(userHandler);
    }

    public static void main(String[] args) {
        new ChatServer(); // Crea una instancia del servidor
    }
}
