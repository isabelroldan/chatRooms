package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.App;
import iesfranciscodelosrios.acd.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;

public class IndexController2 {

    @FXML
    private TextField nicknameTextField;

    @FXML
    private Label nicknameInUseLabel;

    @FXML
    private Button joinButton;

    private ClientController c = new ClientController();

    private User client = new User(null, null, null);


    @FXML
    void ButtonJoin(ActionEvent event) throws IOException {
        // Obtener el nickname ingresado por el usuario
        client.setNickname(nicknameTextField.getText());
        // Verificar si el nickname está en uso
        // Mostrar un mensaje en el Label según el resultado
        if (c.isUserLogedIn(client.getNickname())) {
            nicknameInUseLabel.setText("Nickname no disponible elija otro");
            nicknameInUseLabel.setTextFill(Color.RED);
            nicknameInUseLabel.setVisible(true);
        } else {
            nicknameInUseLabel.setText("Nickname disponible.");
            nicknameInUseLabel.setVisible(true);
            c.saveUserToXml(client);//Crear este metodo
            c.connectToServer();//Modificarlo para la interfaz grafica
            App.setRoot("board");
        }
    }

}