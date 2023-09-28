package iesfranciscodelosrios.acd.server;

import java.io.*;
import java.net.Socket;

public class UserHandler extends Thread {
    private Socket userSocket;
    private ChatServer server;
    private PrintWriter out;

    public UserHandler(Socket userSocket, ChatServer server) {
        this.userSocket = userSocket;
        this.server = server;

        try {
            out = new PrintWriter(userSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                // Aquí procesas el mensaje recibido del usuario
                // Por ejemplo, puedes enviarlo a todos los usuarios conectados
                server.broadcastMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos y manejar la desconexión del usuario
            try {
                userSocket.close();
                // Eliminar el usuario de la lista de usuarios conectados en el servidor
                server.removeUser(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para enviar un mensaje al usuario
    public void sendMessage(String message) {
        out.println(message);
    }
}

