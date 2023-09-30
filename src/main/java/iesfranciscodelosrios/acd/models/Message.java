package iesfranciscodelosrios.acd.models;

import java.util.Date;

public class Message {
    private String sender;
    private String content;
    private Date shippingTime;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.shippingTime = new Date(); // La hora se establece automáticamente al crear el mensaje
    }

    // Getters para acceder a los atributos

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return shippingTime;
    }
}

