package iesfranciscodelosrios.acd.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Messages implements Serializable {

    private List<Message> messages;

    public Messages(){
        this.messages= new ArrayList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

}
