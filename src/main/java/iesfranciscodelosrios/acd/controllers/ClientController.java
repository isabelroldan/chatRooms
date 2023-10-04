package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.models.User;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
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
            File file = new File("src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            User users = (User) unmarshaller.unmarshal(file);

            if (users != null) {
                List<User> userList = users.getUsers();
                if (userList != null) {
                    for (User user : userList) {
                        if (user.getNickname().equals(nickname)) {
                            return true;
                        }
                    }
                } else {
                    System.out.println("La lista está vacía");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void saveUserToXml(User user) {
        try {
            File file = new File("src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(User.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(user, new FileWriter(file));
            System.out.println("Usuario guardado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cambiar por GetIP si se pudiera y da tiempo
    //Se establece la dirección IP y el puerto del servidor al que se conectará el cliente
    protected String serverIp = "192.16.16.108";
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
