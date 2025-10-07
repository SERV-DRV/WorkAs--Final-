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

/**
 *
 * @author enriq
 */
public class Main extends Application {
    private static String URL_VIEW = "/org/workas/view/";
    private static String URL = "/org/workas/";
    private Stage escenarioPrincipal;
    private Scene escena;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.escenarioPrincipal = stage;
        
        inicio();
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.getIcons().add(new Image(URL + "image/Logo.png"));
        escenarioPrincipal.setResizable(false);
        escenarioPrincipal.setTitle("WorkAS");
        escenarioPrincipal.show();       
    }  
    
    public Initializable cambiarEscena(String fxml, double ancho, double alto) throws Exception{
        Initializable interfazCargada = null;
        
        FXMLLoader cargadorFXML = new FXMLLoader();
        
        InputStream archivoFXML = Main.class.getResourceAsStream(URL_VIEW+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Main.class.getResource(URL_VIEW+fxml));                                
        
        escena = new Scene(cargadorFXML.load(archivoFXML), ancho, alto);        
        escenarioPrincipal.setScene(escena);        
        escenarioPrincipal.setResizable(true);
        escenarioPrincipal.sizeToScene();
        escenarioPrincipal.sizeToScene();
        
        interfazCargada = cargadorFXML.getController();
        return interfazCargada;
    }
    
    public void inicio(){
        try{
            InicioSesionController ic = (InicioSesionController)cambiarEscena("InicioSesion.fxml", 813, 588);        
            ic.setPrincipal(this);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
