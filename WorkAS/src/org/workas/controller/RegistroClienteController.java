package org.workas.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import org.workas.db.Conexion;
import org.workas.model.Clientes;
import org.workas.system.Main;

public class RegistroClienteController implements Initializable {

    @FXML private Button btnRegresarAInicio;
    @FXML private Button btnInicioSesion;

    private enum Acciones { AGREGAR, NINGUNA }
    private Acciones tipoAccion = Acciones.NINGUNA;

    private Main principal;
    private Clientes modeloCliente;

    @FXML private TextField txtIdCliente, txtNombre, txtApellido, txtEmail, txtTelefono;
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
        if (principal != null) {
            principal.inicio();
        } else {
            System.err.println("Error: principal es null en PaginaPrincipal");
        }
    }

    public Clientes obtenerModeloCliente() {
        int idCliente = 0;
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText();
        String contraseña = txtContraseña.getText();
        String telefono = txtTelefono.getText();

        return new Clientes(idCliente, nombre, apellido, email, contraseña, telefono, null);
    }

    public void agregarCliente() {
        modeloCliente = obtenerModeloCliente();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_agregarCliente(?,?,?,?,?);");
            stmt.setString(1, modeloCliente.getNombre());
            stmt.setString(2, modeloCliente.getApellido());
            stmt.setString(3, modeloCliente.getEmail());
            stmt.setString(4, modeloCliente.getContraseña());
            stmt.setString(5, modeloCliente.getTelefono());
            stmt.execute();
        } catch (Exception e) {
            System.out.println("Error al agregar cliente...");
            e.printStackTrace();
        }
    }

    @FXML
    private void btnRegistrarAction() {
        if (txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty() || txtEmail.getText().isEmpty()
                || txtContraseña.getText().isEmpty() || txtTelefono.getText().isEmpty()) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Campos obligatorios");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, llene todos los campos obligatorios.");
            alerta.showAndWait();

        } else {
            agregarCliente();

            Alert alerta = new Alert(AlertType.INFORMATION);
            alerta.setTitle("Registro exitoso");
            alerta.setHeaderText(null);
            alerta.setContentText("Cliente registrado correctamente.");
            alerta.showAndWait();

            if (principal != null) {
                principal.inicio();
            } else {
                System.err.println("Error: principal es null al regresar al inicio");
            }
        }
    }

    @FXML
    public void clicManejoEvento(ActionEvent evento) {
        if (principal == null) {
            principal = Main.getInstancia();
        }

        if (evento.getSource() == btnRegresarAInicio) {
            try {
                principal.cambiarEscena("InicioSesion.fxml", 813, 588);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (evento.getSource() == btnInicioSesion) {
            principal.inicio();
        }
    }
}