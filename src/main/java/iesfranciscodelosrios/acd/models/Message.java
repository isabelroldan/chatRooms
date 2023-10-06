package iesfranciscodelosrios.acd.models;

import java.time.LocalDateTime;
import java.util.Date;

public class Message {
    private String nickname;
    private String content;
    private LocalDateTime timestamp;
    private boolean isMine; // Indica si el mensaje es tuyo

    public Message(String nickname, String content, boolean isMine) {
        this.nickname = nickname;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isMine = isMine;
    }

    public String getNickname() {
        return nickname;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

