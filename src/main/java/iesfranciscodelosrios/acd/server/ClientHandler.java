package iesfranciscodelosrios.acd.server;

import iesfranciscodelosrios.acd.models.Message;
import iesfranciscodelosrios.acd.models.User;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread  {
    /**
     * The file path to the XML file that stores user data.
     */
    private static final String XML_FILE_PATH = "src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml";

    /**
     * Represents the XML file that stores user data.
     */
    File xmlFile = new File(XML_FILE_PATH);

    /**
     * A BufferedReader used for reading data from an input source.
     */
    private BufferedReader in; // Declarar la variable 'in' para lectura

    /**
     * A PrintWriter used for writing data to an output destination.
     */
    private PrintWriter out; // Declarar la variable 'out' en la clase

    /**
     * A Socket object representing the client's socket connection.
     */
    private Socket clientSocket;

    /**
     * A reference to the ChatServer instance associated with this client handler.
     */
    private ChatServer chatServer;

    /**
     * An ObjectInputStream used for deserializing objects received from the client.
     */
    private ObjectInputStream objectInputStream; // Declarar la variable 'objectInputStream'

    /**
     * Initializes a new instance of the ClientHandler class with the provided Socket and ChatServer references.
     *
     * @param socket The Socket representing the client's socket connection.
     * @param server A reference to the ChatServer instance associated with this client handler.
     */
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

    /**
     * Overrides the run method of the Thread class to handle communication with the client.
     * Receives objects from the client and processes them based on their type (Message or User).
     */
    public void run() {
        try {
            // Recibir el objeto del cliente
            Object receivedObject = objectInputStream.readObject();

            // Distinguir entre objetos Message y User
            String receivedObjectType = receivedObject.getClass().getName();
            switch (receivedObjectType) {
                case "iesfranciscodelosrios.acd.models.Message":
                    Message clientMessage = (Message) receivedObject;
                    System.out.println("Client message received  \n" + clientMessage.getNickname()+":"+clientMessage.getContent());
                    if (clientMessage != null) {
                        // Crear un objeto Message y agregarlo a la lista de mensajes en el servidor
                        chatServer.addMessage(clientMessage);
                        System.out.println("Mensaje recibido: " + clientMessage);

                        // Enviar el mensaje a todos los clientes conectados
                        chatServer.broadcastMessage(clientMessage);
                    }
                    break;
                case "iesfranciscodelosrios.acd.models.User":
                    User receivedUser = (User) receivedObject;
                    System.out.println("Usuario recibido del cliente: " + receivedUser);

                    // Guardar el usuario en el archivo XML
                    ChatServer.saveUserInXml(receivedUser);
                    break;
                default:
                    System.out.println("Objeto recibido del cliente desconocido.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an XML file to the specified OutputStream.
     *
     * @param out The OutputStream to which the XML file will be sent.
     * @throws IOException If an I/O error occurs while sending the file.
     */
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

    /**
     * Sends a message to the client using the PrintWriter 'out'.
     *
     * @param message The Message object to be sent to the client.
     */
    public void sendMessageToClient(Message message) {
        try {
            // Aquí puedes usar el objeto PrintWriter 'out' para enviar el mensaje al cliente
            if (out != null) {
                out.println(message); // Supongo que el mensaje se puede representar como una cadena (toString).
                out.flush();
                System.out.println("Mensaje repartido");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

