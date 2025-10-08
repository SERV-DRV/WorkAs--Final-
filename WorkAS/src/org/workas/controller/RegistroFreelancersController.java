package org.workas.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.workas.db.Conexion;
import org.workas.model.Freelancers;
import org.workas.system.Main;
import javafx.scene.control.Alert.AlertType;

public class RegistroFreelancersController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private enum acciones {
        AGREGAR, NINGUNA
    }
    acciones tipoAccion = acciones.NINGUNA;

    private Main principal;
    private Freelancers modeloFreelancer;

    @FXML
    private TextField txtNombre, txtApellido, txtEmail, txtTelefono, txtEspecialidad, txtPortafolio;
    @FXML
    private PasswordField txtContraseña;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @FXML
    private void PaginaPrincipal() {
        principal.inicio();
    }

    public Freelancers obtenerModeloFreelancer() {
        int idFreelancer = 0;
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText();
        String contraseña = txtContraseña.getText();
        String telefono = txtTelefono.getText();
        String especialidad = txtEspecialidad.getText();
        String portafolio = txtPortafolio.getText();

        return new Freelancers(idFreelancer, nombre, apellido, email, contraseña, telefono, especialidad, portafolio, null);
    }

    public void agregarFreelancer() {
        modeloFreelancer = obtenerModeloFreelancer();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_agregarFreelancer(?,?,?,?,?,?,?);");
            stmt.setString(1, modeloFreelancer.getNombre());
            stmt.setString(2, modeloFreelancer.getApellido());
            stmt.setString(3, modeloFreelancer.getEmail());
            stmt.setString(4, modeloFreelancer.getContraseña());
            stmt.setString(5, modeloFreelancer.getTelefono());
            stmt.setString(6, modeloFreelancer.getEspecialidad());
            stmt.setString(7, modeloFreelancer.getPortafolioUrl());
            stmt.execute();
        } catch (Exception e) {
            System.out.println("Error al agregar freelancer...");
            e.printStackTrace();
        }
    }

    @FXML
    private void btnRegistrarAction() {
        if (txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty() || txtEmail.getText().isEmpty()
                || txtContraseña.getText().isEmpty() || txtTelefono.getText().isEmpty() || txtEspecialidad.getText().isEmpty()) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Campos obligatorios");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, llene todos los campos obligatorios.");
            alerta.showAndWait();

        } else {
            agregarFreelancer();

            Alert alerta = new Alert(AlertType.INFORMATION);
            alerta.setTitle("Registro exitoso");
            alerta.setHeaderText(null);
            alerta.setContentText("Freelancer registrado correctamente.");
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
