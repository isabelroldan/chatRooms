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
     * Method to start the application in graphical interface
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
     * Load the fxml
     * @throws IOException
     */
    public static void setRoot(Parent root) {
        scene.setRoot(root);
    }

    /**
     * Load the view in fxml
     * @param fxml
     * @return fxml view
     * @throws IOException
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * App launch
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}
