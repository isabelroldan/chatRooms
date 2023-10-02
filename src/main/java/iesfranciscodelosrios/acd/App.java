package iesfranciscodelosrios.acd;

import iesfranciscodelosrios.acd.controllers.IndexController;
import iesfranciscodelosrios.acd.server.ChatServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    private ChatServer chatServer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Obtener la instancia única de ChatServer
        /*chatServer = ChatServer.getInstance();*/

        // Crear una instancia de FXMLLoader para cargar la vista
        FXMLLoader loader = new FXMLLoader(getClass().getResource("index.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista
        IndexController indexController = loader.getController();

        // Establecer la referencia al ChatServer en el controlador
        indexController.setChatServer(chatServer);

        // Configurar la escena y mostrar la ventana principal
        Scene scene = new Scene(root, 1440, 900);
        primaryStage.setScene(scene);
        primaryStage.setTitle("index");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
