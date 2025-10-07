package org.workas.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.workas.db.Conexion;
import org.workas.system.Main;

public class InicioSesionController implements Initializable {

    @FXML private Button btnRegistrarCliente;
    @FXML private Button btnRegistrarFreelancer;

    private Main principal;

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtContraseña;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (principal == null) {
            principal = Main.getInstancia();
        }
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public Main getPrincipal() {
        return principal;
    }

    @FXML
    private void PaginaPrincipal() {
        principal.inicio();
    }

    @FXML
    private void btnIniciarSesionAction() {
        String email = txtEmail.getText().trim();
        String contraseña = txtContraseña.getText().trim();

        if (email.isEmpty() || contraseña.isEmpty()) {
            mostrarAlerta("Debe llenar todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        try {
            PreparedStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_verificarEmail(?, ?);");
            enunciado.setString(1, email);
            enunciado.setString(2, contraseña);

            ResultSet resultado = enunciado.executeQuery();

            if (resultado.next()) {
                int idCliente = resultado.getInt("id_cliente");
                String nombre = resultado.getString("nombre");
                String correo = resultado.getString("email");

                principal.setIdClienteActual(idCliente);

                mostrarAlerta("Inicio de sesión exitoso", Alert.AlertType.INFORMATION);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/workas/view/MainMenuCliente.fxml"));
                Parent root = loader.load();

                MainMenuClienteController controlador = loader.getController();
                controlador.setDatosCliente(nombre, correo);

                Stage stage = (Stage) txtEmail.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } else {
                mostrarAlerta("Email o contraseña incorrectos", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al iniciar sesión", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Inicio de Sesión");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void clicManejoEvento(ActionEvent evento) {
        if (principal == null) {
            principal = Main.getInstancia();
        }
        if (evento.getSource() == btnRegistrarCliente) {
            principal.registrarCliente();
        }
        if (evento.getSource() == btnRegistrarFreelancer) {
            principal.registerFreelance();
        }
    }
}