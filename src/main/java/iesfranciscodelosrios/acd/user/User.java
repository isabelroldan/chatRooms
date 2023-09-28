package iesfranciscodelosrios.acd.user;

import java.io.*;
import java.net.*;

public class User {
    public static void main(String[] args) {
        try {
            String serverIp = "172.16.16.176"; // Dirección IP del servidor
            int serverPort = 8081; // Puerto del servidor

            Socket clientSocket = new Socket(serverIp, serverPort); // Conecta al servidor
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Solicitar al usuario un nickname (reemplaza esta lógica con tu propia implementación)
            System.out.print("Ingresa tu nickname: ");
            String nickname = userInput.readLine();

            // Enviar el nickname al servidor
            out.println(nickname);

            // Iniciar un hilo para recibir mensajes del servidor
            new Thread(new UserMessageReceiver(clientSocket, nickname)).start();

            // Leer y enviar mensajes al servidor
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class UserMessageReceiver implements Runnable {
        private Socket clientSocket;
        private String clientNickname; // Almacena el nombre de usuario del cliente

        public UserMessageReceiver(Socket clientSocket, String clientNickname) {
            this.clientSocket = clientSocket;
            this.clientNickname = clientNickname;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message;
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