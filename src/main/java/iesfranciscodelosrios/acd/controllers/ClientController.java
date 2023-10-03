package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.models.User;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.*;
import java.util.List;

public class ClientController {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread messageReceiverThread;

    public boolean isUserLogedIn(String nickname) throws IOException {
        boolean result = false;
        try {
            File file = new File("../../../resources/xmls/Users.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            User users = (User) unmarshaller.unmarshal(file);

            List<User> userList = users.getUsers();
            for (User user : userList) {
                if (user.getNickname().equals(nickname)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // Cambiar por GetIP si se pudiera y da tiempo
    //Se establece la dirección IP y el puerto del servidor al que se conectará el cliente
    protected String serverIp = "192.168.18.13";
    protected int serverPort = 8081;

    public void connectToServer() {
        try {
            clientSocket = new Socket(serverIp, serverPort);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Iniciar un hilo para recibir mensajes del servidor
            messageReceiverThread = new Thread(new UserMessageReceiver());
            messageReceiverThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToServer(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public static void saveUser(User user, String filePath) {
        try {
            File file = new File(filePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(UserWrapper.class);

            // Obtener los usuarios existentes si el archivo ya existe
            UserWrapper usersWrapper;
            if (file.exists()) {
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                usersWrapper = (UserWrapper) unmarshaller.unmarshal(file);
            } else {
                userWrapper = new UserWrapper();
            }

            // Agregar el nuevo usuario
            userWrapper.getUser().add(user);

            // Guardar la lista actualizada de usuarios
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(usersWrapper, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


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

    /**
     * Representa el hilo que escucha los mensajes del servidor y los muestra en la consola del cliente.
     */
    private class UserMessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // Manejar el mensaje recibido del servidor
                    System.out.println("Mensaje del servidor: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
