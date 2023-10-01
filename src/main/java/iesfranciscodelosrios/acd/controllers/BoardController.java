package iesfranciscodelosrios.acd.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BoardController {

    @FXML
    private Label nicknameLabel;

    // Método para establecer el nickname en el Label
    public void setNickname(String nickname) {
        nicknameLabel.setText("Welcome, " + nickname + "!");
    }
}