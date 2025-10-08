package org.workas.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.workas.system.Main;

public class MainMenuFreeLancerController implements Initializable {
    
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


    @FXML
    public void clicManejoEvento(ActionEvent evento) {
        if (principal == null) {
            principal = Main.getInstancia();
        }
        if (evento.getSource() == btnCerrarSesion) {
            principal.inicio();
        }
    }

    @FXML private Button btnInicioF, btnCerrarSesionS,btnClientes,btnFreeLancers,btnCategorias,btnEntregas,btnProyectos;
    @FXML 
    public void clicManejoEvento2(ActionEvent evento) {
        if (principal == null) {
            principal = Main.getInstancia();
        }
        if (evento.getSource() == btnClientes) {
            principal.clientesLancers();
        }
        if (evento.getSource() == btnFreeLancers) {
            principal.freeLancersLancers();
        }
        if (evento.getSource() == btnCategorias) {
            principal.categoriasLancers();
        }
        if (evento.getSource() == btnEntregas) {
            principal.entregasLancers();
        }
        if (evento.getSource() == btnProyectos) {
            principal.proyectosFreelancer();
        }
    }
} 