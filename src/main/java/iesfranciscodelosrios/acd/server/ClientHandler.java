package iesfranciscodelosrios.acd.server;

import iesfranciscodelosrios.acd.models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread  {
    private Socket clientSocket;
    private ChatServer chatServer;

    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.chatServer = server;
    }

    public void run() {
        try {
            // Crear flujos de entrada y salida para comunicarse con el cliente
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            // Recibir el objeto User del cliente
            User receivedUser = (User) objectInputStream.readObject();
            System.out.println("Usuario recibido del cliente: " + receivedUser);

            // Guardar el usuario en el archivo XML
            ChatServer.saveUserInXml(receivedUser);

            //Cerrar flujos
            objectInputStream.close();
            objectOutputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

