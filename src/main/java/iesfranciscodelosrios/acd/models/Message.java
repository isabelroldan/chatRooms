package iesfranciscodelosrios.acd.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String nickname;
    private String content;
    private LocalDateTime timestamp;

    public Message(String nickname, String content, LocalDateTime timestamp) {
        this.nickname = nickname;
        this.content = content;
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "Message{" +
                "nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}