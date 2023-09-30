package iesfranciscodelosrios.acd.models;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name; // Nombre de la sala
    private List<Message> messages; // Lista de mensajes en la sala

    public Room(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
    }

    // Getter para obtener el nombre de la sala
    public String getName() {
        return name;
    }

    // Getter para obtener la lista de mensajes en la sala
    public List<Message> getMessages() {
        return messages;
    }

    // Método para agregar un mensaje a la sala
    public void addMessage(Message message) {
        messages.add(message);
    }
}
