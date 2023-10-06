package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.App;
import iesfranciscodelosrios.acd.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.ArrayList;

public class IndexController {

    @FXML
    private TextField nicknameTextField;

    @FXML
    private Label nicknameInUseLabel;

    @FXML
    private Button joinButton;

    private ArrayList<User> users = new ArrayList<User>(); //Rellenar esta lista para que funcione correctamente

    private ClientController c = new ClientController();

    private User client = new User(null, null, null);

    public IndexController() throws IOException {
    }

    @FXML
    void ButtonJoin(ActionEvent event) throws IOException {
        // Obtener el nickname ingresado por el usuario
        client.setNickname(nicknameTextField.getText());

        //Conectar al servidor
        c.connectToServer(client);

        // Verificar si el nickname está en uso
        // Mostrar un mensaje en el Label según el resultado
        if (c.isUserLogedIn(client.getNickname())) {
            nicknameInUseLabel.setText("Nickname no disponible elija otro");
            nicknameInUseLabel.setTextFill(Color.RED);
            nicknameInUseLabel.setVisible(true);
            c.disconnectFromServer();
        } else {
            nicknameInUseLabel.setText("Nickname disponible.");
            nicknameInUseLabel.setTextFill(Color.GREEN);
            nicknameInUseLabel.setVisible(true);

            users = c.loadUsersAndAddNewUser(client);

            //c.saveUserToXml(client);
            c.saveUsersToXml(users);//Crear este metodo

            // Obtener el nickname ingresado por el usuario
            client.setNickname(nicknameTextField.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iesfranciscodelosrios/acd/board.fxml"));
            Parent root = loader.load();
            BoardController boardController = loader.getController();
            boardController.setNickname(client.getNickname());

            // Establecer la vista cargada como raíz de la aplicación
            App.setRoot(root);

        }
    }
}