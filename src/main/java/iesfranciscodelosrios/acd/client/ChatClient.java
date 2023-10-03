package iesfranciscodelosrios.acd.client;

import iesfranciscodelosrios.acd.controllers.ClientController;

import java.io.*;

public class  ChatClient {
    private String nickname;
    public ChatClient(String nickname) {
        this.nickname = nickname;
    }

    public static void main(String[] args) throws IOException {
        ClientController c = new ClientController();
        c.connectToServer();
    }
}