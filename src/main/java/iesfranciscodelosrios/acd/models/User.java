package iesfranciscodelosrios.acd.models;

public class User {
    private String nickname; // Nombre de usuario
    private String ipAddress; // Dirección IP del usuario
    private Room currentRoom; // Sala actual del usuario

    public User(String nickname, String ipAddress, Room currentRoom) {
        this.nickname = nickname;
        this.ipAddress = ipAddress;
        this.currentRoom = currentRoom;
    }

    // Getter para obtener el nombre de usuario
    public String getNickname() {
        return nickname;
    }

    // Getter para obtener la dirección IP del usuario
    public String getIpAddress() {
        return ipAddress;
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
