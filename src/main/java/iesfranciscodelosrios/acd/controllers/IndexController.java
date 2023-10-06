package iesfranciscodelosrios.acd.controllers;

import iesfranciscodelosrios.acd.App;
import iesfranciscodelosrios.acd.models.User;
import iesfranciscodelosrios.acd.models.Users;
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
import java.util.List;

public class IndexController {

    @FXML
    private TextField nicknameTextField;

    @FXML
    private Label nicknameInUseLabel;

    @FXML
    private Button joinButton;

    private Users users; //Rellenar esta lista para que funcione correctamente

    private ClientController c = new ClientController();

    private User client = new User(null, null, null);

    public IndexController() throws IOException {
    }

    /**
     * When you click on the button, it should return if the nickname has already been used and if not,
     * it will let you continue to the next view. Also passes the nickname to the other view
     * @param event
     * @throws IOException
     */
    @FXML
    void ButtonJoin(ActionEvent event) throws IOException {
        //  Get the nickname entered by the user
        client.setNickname(nicknameTextField.getText());

        //Connect to server
        c.connectToServer(client);

        // Check if the nickname is in use
        //Show a message in the Label according to the result
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



            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iesfranciscodelosrios/acd/board.fxml"));
            Parent root = loader.load();
            BoardController boardController = loader.getController();
            boardController.setNickname(client.getNickname());

            // Set the loaded view as application root
            App.setRoot(root);

        }
    }
}