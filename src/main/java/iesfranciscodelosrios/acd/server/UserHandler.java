package iesfranciscodelosrios.acd.server;

import java.io.*;
import java.net.Socket;

public class UserHandler extends Thread {
    /**
     * Representa el socket de comunicación con el cliente. El servidor utiliza este socket para recibir y enviar mensajes al cliente.
     */
    private Socket userSocket;
    /**
     * Es una referencia al servidor de chat al que está conectado este cliente. Permite que el UserHandler se comunique con otros clientes a través del servidor.
     */
    private ChatServer server;
    /**
     *  Es un objeto que permite enviar mensajes al cliente a través del socket.
     */
    private PrintWriter out;

    public UserHandler(Socket userSocket, ChatServer server) {
        this.userSocket = userSocket;
        this.server = server;

        /**
         * Se inicializa el campo out para enviar mensajes al cliente a través del socket.
         */
        try {
            out = new PrintWriter(userSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Este método se ejecuta cuando se inicia un nuevo hilo UserHandler. Es el punto de entrada para la lógica de manejo de mensajes del cliente.
     */
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(userSocket.getInputStream())); //Para leer mensajes enviados por el cliente a través del socket.

            String message;

            //Se inicia un bucle que escucha continuamente los mensajes del cliente mientras la conexión esté activa.
            while ((message = in.readLine()) != null) {
                // Aquí procesas el mensaje recibido del usuar en nuestro caso lo enviarmos a todos los usuarios conectados
                server.broadcastMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos y manejar la desconexión del usuario
            try {
                userSocket.close(); // Se cierra el socket del cliente
                // Eliminar el usuario de la lista de usuarios conectados en el servidor
                server.removeUser(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para enviar un mensaje al usuario
    public void sendMessage(String message) {
        out.println(message); //Se utiliza el PrintWriter out para enviar el mensaje al cliente.
    }
}

