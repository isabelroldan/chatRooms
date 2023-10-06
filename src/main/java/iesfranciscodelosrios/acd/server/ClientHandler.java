package iesfranciscodelosrios.acd.server;

import iesfranciscodelosrios.acd.models.User;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread  {
    private static final String XML_FILE_PATH = "src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml";
    File xmlFile = new File(XML_FILE_PATH);

    private Socket clientSocket;
    private ChatServer chatServer;

    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.chatServer = server;
    }

    public void run() {
        try {
            // Crear flujos de entrada y salida para comunicarse con el cliente
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);

            //envío xml
            if (xmlFile.exists()){
                sendXmlFile(out);
            } else {
                
            }

            // Recibir el objeto User del cliente
            User receivedUser = (User) objectInputStream.readObject();
            System.out.println("Usuario recibido del cliente: " + receivedUser);

            // Guardar el usuario en el archivo XML
            ChatServer.saveUserInXml(receivedUser);


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
}

