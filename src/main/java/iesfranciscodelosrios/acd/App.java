package iesfranciscodelosrios.acd;

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

    /**
     * Metodo para iniciar la aplicación en interfaz gráfica
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("index"), 1440, 900);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    /**
     * Carga el fxml
     * @param fxml
     * @throws IOException
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Carga el la vista en fxml
     * @param fxml
     * @return Vista fxml
     * @throws IOException
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /*@Override
    public void start(Stage primaryStage) throws Exception {

        // Crear una instancia de FXMLLoader para cargar la vista
        FXMLLoader loader = new FXMLLoader(getClass().getResource("index.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista
        IndexController indexController = loader.getController();

        // Configurar la escena y mostrar la ventana principal
        Scene scene = new Scene(root, 1440, 900);
        primaryStage.setScene(scene);
        primaryStage.setTitle("index");
        primaryStage.show();
    }*/

    /**\
     * Lanzamiento de la App
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}
