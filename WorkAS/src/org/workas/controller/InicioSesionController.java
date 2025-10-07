package org.workas.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.workas.system.Main;

public class InicioSesionController implements Initializable {

    private Main principal;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @FXML
    private void irRegistroClientes() {
        if (principal != null) {
            try {
                principal.cambiarEscena("RegisterCliente.fxml", 813, 588);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void irRegistroFreelancers() {
        if (principal != null) {
            try {
                principal.cambiarEscena("RegisterFreeLance.fxml", 813, 588);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
