package org.workas.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.workas.system.Main;

/**
 *
 * @author informatica
 */
public class MainMenuClienteController implements Initializable {

    @FXML
    private Label lblNombreCliente;
    @FXML
    private Label lblCorreoCliente;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private Button btnMisProyectos;
    @FXML
    private Button btnPostulados;
    @FXML
    private Button btnPagos;
    @FXML
    private Button btnFacturas;

    private Main principal;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public Main getPrincipal() {
        return principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (principal == null) {
            principal = Main.getInstancia();
        }
    }

    public void setDatosCliente(String nombre, String email) {
        lblNombreCliente.setText(nombre);
        lblCorreoCliente.setText(email);
    }

    @FXML
    public void clicManejoEvento(ActionEvent evento) {
        if (principal == null) {
            principal = Main.getInstancia();
        }
        if (evento.getSource() == btnCerrarSesion) {
            principal.inicio();
        }
    }

    @FXML
    public void clicManejoEvento2(ActionEvent evento) {
        if (principal == null) {
            principal = Main.getInstancia();
        }
        if (evento.getSource() == btnMisProyectos) {
            principal.proyectosCliente();
        }
        if (evento.getSource() == btnPostulados) {
            principal.postuladosCliente();
        }
        if (evento.getSource() == btnPagos) {
            principal.pagosCliente();
        }
        if (evento.getSource() == btnFacturas) {
            principal.facturasCliente();
        }
    }
}
