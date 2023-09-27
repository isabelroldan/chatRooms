package iesfranciscodelosrios.acd;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class IndexController {

    @FXML
    private TextField nicknameTextField;

    @FXML
    private Button entrarButton;

    public void handleEntrarButton() {
        String nickname = nicknameTextField.getText();
        // Envía el nickname al servidor para su verificación
        boolean isNicknameAvailable = verificarNicknameEnServidor(nickname);
        if (isNicknameAvailable) {
            // El nickname es válido, permitir acceso a la sala
            // Puedes abrir la ventana de la sala o realizar la acción correspondiente aquí
        } else {
            // El nickname ya está en uso, muestra un mensaje de error al usuario
            System.out.println("El nickname ya está en uso. Elige otro.");
            // Puedes mostrar un mensaje de error en la interfaz de usuario si lo deseas
        }
    }

    // Método para enviar el nickname al servidor y verificar su disponibilidad
    private boolean verificarNicknameEnServidor(String nickname) {
        // Lógica para verificar si el nickname ya está en uso en el servidor
        // Debes implementar esto en tu servidor
        return true; // Devuelve true si el nickname está disponible, false si no lo está
    }
}
