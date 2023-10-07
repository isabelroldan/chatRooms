package iesfranciscodelosrios.acd.models;

import java.util.Date;

public class Message {

    private String nickname;
    private String text;
    private Date date;

    public Message(String nickname, String text, Date date) {
        this.nickname = nickname;
        this.text = text;
        this.date = date;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

