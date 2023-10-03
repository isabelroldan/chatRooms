package iesfranciscodelosrios.acd.models;

import javax.xml.bind.annotation.*;
import java.util.List;


@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
    private String nickname; // Nombre de usuario
    private String ipAddress; // Dirección IP del usuario
    private Room currentRoom; // Sala actual del usuario

    @XmlElementWrapper(name = "users")
    @XmlElement(name = "user")
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User(String nickname, String ipAddress, Room currentRoom) {
        this.nickname = nickname;
        this.ipAddress = ipAddress;
        this.currentRoom = currentRoom;
    }

    // Getter para obtener el nombre de usuario
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // Getter para obtener la dirección IP del usuario
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // Getter para obtener la sala actual del usuario
    public Room getCurrentRoom() {
        return currentRoom;
    }

    // Setter para establecer la sala actual del usuario
    public void setCurrentRoom(Room room) {
        currentRoom = room;
    }

    // Método para enviar un mensaje en la sala actual del usuario
    public void sendMessage(String content) {
        if (currentRoom != null) {
            Message message = new Message(nickname, content);
            currentRoom.addMessage(message);
        } else {
            System.out.println("Usuario no está en una sala.");
        }
    }
}
