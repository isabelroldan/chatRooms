package iesfranciscodelosrios.acd.server;

import iesfranciscodelosrios.acd.models.Message;
import iesfranciscodelosrios.acd.models.User;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread  {
    private static final String XML_FILE_PATH = "src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml";
    File xmlFile = new File(XML_FILE_PATH);

    private BufferedReader in; // Declarar la variable 'in' para lectura
    private PrintWriter out; // Declarar la variable 'out' en la clase

    private Socket clientSocket;
    private ChatServer chatServer;
    private ObjectInputStream objectInputStream; // Declarar la variable 'objectInputStream'

    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.chatServer = server;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.objectInputStream = new ObjectInputStream(socket.getInputStream()); // Inicializar 'objectInputStream'
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Inicializar 'in' para lectura
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // ... (Código existente)

            // Recibir el objeto User del cliente
            User receivedUser = (User) objectInputStream.readObject();
            System.out.println("Usuario recibido del cliente: " + receivedUser);

            // Guardar el usuario en el archivo XML
            ChatServer.saveUserInXml(receivedUser);

            // Escuchar y transmitir mensajes
            while (true) {
                String clientMessage = in.readLine();
                if (clientMessage != null) {
                    // Crear un objeto Message y agregarlo a la lista de mensajes en el servidor
                    Message message = new Message(receivedUser.getNickname(), clientMessage, null);
                    chatServer.addMessage(message);
                    System.out.println("Mensaje recibido de " + receivedUser.getNickname() + ": " + clientMessage);

                    // Enviar el mensaje a todos los clientes conectados
                    chatServer.broadcastMessage(message);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendXmlFile(OutputStream out) throws IOException {
        File xmlFile = new File(XML_FILE_PATH);
        if (!xmlFile.exists()) {
            System.err.println("El archivo XML no existe en la ruta especificada.");
            return;
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(xmlFile))) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            // Leer del archivo y enviar al cliente en bloques
            while ((bytesRead = bis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }

            System.out.println("Archivo XML enviado al cliente.");
        }
    }

    public void sendMessageToClient(Message message) {
        try {
            // Aquí puedes usar el objeto PrintWriter 'out' para enviar el mensaje al cliente
            if (out != null) {
                out.println(message); // Supongo que el mensaje se puede representar como una cadena (toString).
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

