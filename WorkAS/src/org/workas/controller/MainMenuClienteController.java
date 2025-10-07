package org.workas.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.workas.system.Main;

/**
 *
 * @author informatica
 */
public class MainMenuClienteController implements Initializable {
    
    @FXML private Button btnCerrarSesion; 
    
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
}
