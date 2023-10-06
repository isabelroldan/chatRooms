package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.models.User;

import iesfranciscodelosrios.acd.models.Users;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientController {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread messageReceiverThread;
    private File xmlFile = new File("src/main/resources/iesfranciscodelosrios/acd/Xmls/Users.xml");

    public boolean isUserLogedIn(String nickname) throws IOException {
        boolean result = false;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Users users = (Users) unmarshaller.unmarshal(xmlFile);

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

    public Users loadUsersAndAddNewUser(User newUser) {
        Users updatedUsers = new Users();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            if (xmlFile.exists()) {
                Users users = (Users) unmarshaller.unmarshal(xmlFile);

                if (users != null) {
                    List<User> existingUsers = users.getUsers();
                    if (existingUsers != null) {
                        updatedUsers.getUsers().addAll(existingUsers);
                    }
                }
            }

            // Agregar el nuevo usuario
            updatedUsers.getUsers().add(newUser);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return updatedUsers;
    }

    public void saveUsersToXml(Users users) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(users, new FileWriter(xmlFile)); //Crea el xml
            System.out.println("Usuario guardado exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cambiar por GetIP si se pudiera y da tiempo
    //Se establece la dirección IP y el puerto del servidor al que se conectará el cliente
    protected String serverIp = "192.168.18.13";
    protected int serverPort = 8081;

    public void connectToServer(User client) {
        try {
            Socket clientSocket = new Socket(serverIp, serverPort);

            if(isUserLogedIn(client.getNickname()) == false) {
                //Enviar la solicitud al servidor
                sendUserToServer(clientSocket, client);
            }else{
                disconnectFromServer();
                System.out.println("Nickname en uso");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUserToServer(Socket socket,User user) {
         try {
            // Enviar el objeto User al servidor
           try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                objectOutputStream.writeObject(user);
                objectOutputStream.flush();
                System.out.println("User pushed to server to save it in the XML file");
            }
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
}
