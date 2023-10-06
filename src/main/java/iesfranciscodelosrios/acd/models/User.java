package iesfranciscodelosrios.acd.models;

import jakarta.xml.bind.annotation.*;

import java.io.Serializable;
import java.util.List;


@XmlRootElement(name = "User")
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Serializable {
    @XmlElement(name = "Nickname")
    private String nickname; // Nombre de usuario
    @XmlElement(name = "CurrentRoom")
    private Integer currentRoom; // Sala actual del usuario

    public User(String nickname, String ipAddress, Integer currentRoom) {
        this.nickname = nickname;
        this.currentRoom = currentRoom;
    }

    public User(){

    }

    // Getter para obtener el nombre de usuario
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // Getter para obtener la sala actual del usuario
    public int getCurrentRoom() {
        return currentRoom;
    }

    // Setter para establecer la sala actual del usuario
    public void setCurrentRoom(int room) {
        currentRoom = room;
    }

}
