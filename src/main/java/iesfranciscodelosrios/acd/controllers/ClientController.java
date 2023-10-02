package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.client.ChatClient;
import iesfranciscodelosrios.acd.models.Room;
import iesfranciscodelosrios.acd.models.User;

import java.io.*;
import java.net.*;

public class ClientController {
    //Valores por defecto para el usuario
    private String nickname = null;
    private String ip = null;
    private Room room = null;

    private Object client(String nickname, String ip, Room room){
        nickname = this.nickname;
        ip = this.ip;
        room = this.room;
        User user = new User(nickname, ip, room);
        return user;
    }

    // Cambiar por GetIP si se pudiera y da tiempo
    //Se establece la dirección IP y el puerto del servidor al que se conectará el cliente
    protected String serverIp = "172.16.16.176";
    protected int serverPort = 8081;

    private void ClientConnection() throws IOException {
        try {
            //Se crea un socket clientSocket para conectarse al servidor utilizando la dirección IP y el puerto proporcionados.
            Socket clientSocket = new Socket(serverIp, serverPort); // Conecta al servidor

            //Se crea un BufferedReader llamado userInput para leer la entrada del usuario desde la consola.
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            //Se crea un PrintWriter llamado out para enviar mensajes al servidor a través del socket.
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Solicitar al usuario un nickname
            System.out.print("Ingresa tu nickname: ");

            //El nickname ingresado por el usuario se envía al servidor utilizando el objeto out.
            // Esto permite que el servidor identifique al cliente por su nombre de usuario.
            String nickname = userInput.readLine();

            // Enviar el nickname al servidor
            out.println(nickname);

            // Iniciar un hilo para recibir y mostrar mensajes del servidor
            new Thread(new ChatClient.UserMessageReceiver(clientSocket, nickname)).start(); //Se pasa el socket del cliente (clientSocket) y el nickname del cliente (nickname) al constructor de UserMessageReceiver.

            // Leer y enviar mensajes al servidor
            String message;

            //El cliente entra en un bucle donde lee mensajes desde la consola (userInput)
            // y los envía al servidor a través de out. Esto permite al cliente enviar mensajes al servidor y, por lo tanto, al chat.
            while ((message = userInput.readLine()) != null) {
                out.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * Representa el hilo que escucha los mensajes del servidor y los muestra en la consola del cliente.
         */
        class UserMessageReceiver implements Runnable {
            private Socket clientSocket;
            private String clientNickname; // Almacena el nombre de usuario del cliente

            public UserMessageReceiver(Socket clientSocket, String clientNickname) {
                this.clientSocket = clientSocket;
                this.clientNickname = clientNickname;
            }

            @Override
            public void run() {
                try {
                    //BufferedReader para leer mensajes del servidor a través del socket.
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String message;

                    //Dentro de un bucle, se lee continuamente desde el servidor y se muestra en la consola del cliente, a menos que el mensaje provenga del propio cliente (identificado por el nickname).
                    // De esta manera, los mensajes enviados por el propio cliente no se muestran en su propia consola.
                    while ((message = in.readLine()) != null) {
                        // Verifica si el mensaje no proviene del propio cliente
                        if (!message.startsWith(clientNickname + ":")) {
                            System.out.println(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
