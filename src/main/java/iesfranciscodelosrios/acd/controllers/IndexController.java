package iesfranciscodelosrios.acd.controllers;



import iesfranciscodelosrios.acd.client.ChatClient;
import iesfranciscodelosrios.acd.server.ChatServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class IndexController {

    @FXML
    private TextField nicknameTextField;

    @FXML
    private Label nicknameInUseLabel;

    private String nickname;

    private ChatServer chatServer;

    public void setChatServer(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    @FXML
    void handleJoinButtonClick(ActionEvent event) throws IOException {
        // Obtener el nickname ingresado por el usuario
         nickname = nicknameTextField.getText();

        // Verificar si el nickname está en uso
        boolean isNicknameInUse = isNicknameInUse(nickname);

        // Mostrar un mensaje en el Label según el resultado
        if (isNicknameInUse) {
            nicknameInUseLabel.setText("El nickname ya está en uso. Por favor, elige otro.");
            nicknameInUseLabel.setVisible(true);
        } else {
            nicknameInUseLabel.setText("Nickname disponible.");
            nicknameInUseLabel.setVisible(true);

            // Puedes continuar con la lógica para iniciar el ChatClient aquí
            // Por ejemplo, iniciar un nuevo ChatClient con el nickname
            startChatClient(nickname);



        }
    }

    // Implementa la lógica para verificar si el nickname está en uso
    private boolean isNicknameInUse(String nickname) {
        // Verificar si el nickname está en uso consultando al servidor
        return chatServer.isNicknameAvailable(nickname);
    }

    // Implementa la lógica para iniciar el ChatClient con el nickname
    private void startChatClient(String nickname) throws IOException {
        // Crear una instancia de ChatClient y pasar el nickname como argumento
        ChatClient chatClient = new ChatClient(nickname);

        // Llamar al método main de ChatClient
        chatClient.main(new String[]{});
    }



}
