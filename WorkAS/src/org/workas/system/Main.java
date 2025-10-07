package org.workas.system;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.workas.controller.InicioSesionController;
import org.workas.controller.MainMenuClienteController;
import org.workas.controller.RegistroClienteController;
import org.workas.controller.RegistroFreelancersController;

public class Main extends Application {

    private static Main instancia;
    
    private static final String URL_VIEW = "/org/workas/view/";
    private static final String URL = "/org/workas/";
    
    private Stage escenarioPrincipal;
    private Scene escena;

    public static void main(String[] args) {
        launch(args);
    }
    
    private static int idClienteActual;

    public static void setIdClienteActual(int idCliente) {
        idClienteActual = idCliente;
    }

    public static int getIdClienteActual() {
        return idClienteActual;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        instancia = this;
        this.escenarioPrincipal = stage;

        inicio(); 

        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.getIcons().add(new Image(URL + "image/LogoPuro.png"));
        escenarioPrincipal.setResizable(false);
        escenarioPrincipal.setTitle("WorkAS");
        escenarioPrincipal.show();
    }

    public static Main getInstancia() {
        return instancia;
    }

    public Initializable cambiarEscena(String fxml, double ancho, double alto) throws Exception {
        FXMLLoader cargadorFXML = new FXMLLoader();

        InputStream archivoFXML = Main.class.getResourceAsStream(URL_VIEW + fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Main.class.getResource(URL_VIEW + fxml));

        escena = new Scene(cargadorFXML.load(archivoFXML), ancho, alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.setResizable(true);
        escenarioPrincipal.sizeToScene();

        return cargadorFXML.getController();
    }

    public void inicio() {
        try {
            InicioSesionController ic = (InicioSesionController) cambiarEscena("InicioSesion.fxml", 813, 588);
            ic.setPrincipal(this);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void registrarCliente() {
        try {
            RegistroClienteController rc = (RegistroClienteController) cambiarEscena("RegisterCliente.fxml", 813, 588);
            rc.setPrincipal(this);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void registerFreelance() {
        try {
            RegistroFreelancersController flc = (RegistroFreelancersController) cambiarEscena("RegisterFreeLance.fxml", 813, 588);
            flc.setPrincipal(this);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void mainMenuCliente() {
        try{
            MainMenuClienteController mmc = (MainMenuClienteController) cambiarEscena("MainMenuCliente.fxml", 1000, 650);
            mmc.setPrincipal(this);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
