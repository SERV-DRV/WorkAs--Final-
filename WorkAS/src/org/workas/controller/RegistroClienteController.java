package org.workas.controller;

/**
 *
 * @author PC
 */
import java.net.URL;
import java.sql.CallableStatement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.workas.db.Conexion;
import org.workas.model.Clientes;
import org.workas.system.Main;
import javafx.scene.control.Alert.AlertType;

public class RegistroClienteController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private enum acciones { AGREGAR, NINGUNA }
    acciones tipoAccion = acciones.NINGUNA;
    
    private Main principal;
    private Clientes modeloCliente;

    @FXML
    private TextField txtIdCliente, txtNombre, txtApellido, txtEmail, txtTelefono;
    @FXML
    private PasswordField txtContraseña;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @FXML
    private void PaginaPrincipal() {
        principal.inicio();
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

            principal.inicio();
        }
    }
    
    @FXML
    private void regresarInicioSesion() {
        if (principal != null) {
            principal.inicio();
        }
    }
}