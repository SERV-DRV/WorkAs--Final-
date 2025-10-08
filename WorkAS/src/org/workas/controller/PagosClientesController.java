package org.workas.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.workas.db.Conexion;
import org.workas.model.Proyectos;
import org.workas.model.Freelancers;
import org.workas.model.Pago;
import org.workas.system.Main;

public class PagosClientesController implements Initializable {

    private ObservableList<Pago> listaPagos;
    private ObservableList<Proyectos> listaProyectos;
    private ObservableList<Freelancers> listaFreelancers;
    
    private Main principal;
    private Pago modeloPago;
    
    private final String[] estadosPago = {"pendiente", "completado", "fallido"};
    private final String[] metodosPago = {"tarjeta", "transferencia", "paypal"};

    private enum EstadoFormulario {
        AGREGAR, ELIMINAR, ACTUALIZAR, NINGUNA
    };
    EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableView<Pago> tablaPagos; 
    @FXML
    private TableColumn colIDPago, colIDProyecto, colIDFreelancer, colMonto, colFechaPago, colMetodoPago, colEstado;
    @FXML
    private TextField txtIDPago, txtBuscar, txtMonto;
    
    @FXML
    private ComboBox<Proyectos> cmbProyecto;
    @FXML
    private ComboBox<Freelancers> cmbFreelancer;
    @FXML
    private ComboBox<String> cmbMetodoPago;
    @FXML
    private ComboBox<String> cmbEstado;
    
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar, btnGuardar, btnCancelar;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarProyectos();
        cargarFreelancers();
        
        cargarTablaPagos();
        
        cmbEstado.setItems(FXCollections.observableArrayList(estadosPago));
        cmbMetodoPago.setItems(FXCollections.observableArrayList(metodosPago));
        
        configurarColumnas();
        
        tablaPagos.setOnMouseClicked(eventHandler -> cargarPagoEnComponentes());
        
        colIDPago.prefWidthProperty().bind(tablaPagos.widthProperty().multiply(0.05));
        colIDProyecto.prefWidthProperty().bind(tablaPagos.widthProperty().multiply(0.10));
        colIDFreelancer.prefWidthProperty().bind(tablaPagos.widthProperty().multiply(0.20));
        colMonto.prefWidthProperty().bind(tablaPagos.widthProperty().multiply(0.15));
        colMetodoPago.prefWidthProperty().bind(tablaPagos.widthProperty().multiply(0.20));
        colEstado.prefWidthProperty().bind(tablaPagos.widthProperty().multiply(0.15));
        colFechaPago.prefWidthProperty().bind(tablaPagos.widthProperty().multiply(0.15));
        
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        colIDPago.setCellValueFactory(new PropertyValueFactory<Pago, Integer>("idPago")); 
        colIDProyecto.setCellValueFactory(new PropertyValueFactory<Pago, Integer>("idProyecto")); 
        colIDFreelancer.setCellValueFactory(new PropertyValueFactory<Pago, Integer>("idFreelancer")); 
        colMonto.setCellValueFactory(new PropertyValueFactory<Pago, Double>("monto")); 
        colFechaPago.setCellValueFactory(new PropertyValueFactory<Pago, LocalDateTime>("fechaPago"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<Pago, String>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Pago, String>("estado"));
    }
    
    private ArrayList<Proyectos> cargarModeloProyectos(){
        ArrayList<Proyectos> proyectos = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_listarproyectos();");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                Proyectos p = new Proyectos(
                         resultado.getInt("id_proyecto"),
                         resultado.getString("titulo"),
                         null, null, null, null, null, null, null, null, null
                );
                proyectos.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar proyectos: " + e.getMessage());
        }
        return proyectos;
    }
    
    private void cargarProyectos() {
        listaProyectos = FXCollections.observableArrayList(cargarModeloProyectos());
        cmbProyecto.setItems(listaProyectos);
    }
    
    private ArrayList<Freelancers> cargarModeloFreelancers(){
        ArrayList<Freelancers> freelancers = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_listarfreelancers();"); 
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                Freelancers f = new Freelancers(
                         resultado.getInt("id_freelancer"),
                         resultado.getString("nombre"),
                         resultado.getString("apellido"),
                         null, null, null, null, null, null 
                );
                freelancers.add(f);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar freelancers: " + e.getMessage());
        }
        return freelancers;
    }
    
    private void cargarFreelancers() {
        listaFreelancers = FXCollections.observableArrayList(cargarModeloFreelancers());
        cmbFreelancer.setItems(listaFreelancers);
    }

    public void cargarTablaPagos() {
        listaPagos = FXCollections.observableArrayList(listarPagos());
        tablaPagos.setItems(listaPagos);
        tablaPagos.getSelectionModel().selectFirst();
        cargarPagoEnComponentes();
    }

    public void cargarPagoEnComponentes() {
        Pago pagoSeleccionado = tablaPagos.getSelectionModel().getSelectedItem();
        if (pagoSeleccionado != null) {
            txtIDPago.setText(String.valueOf(pagoSeleccionado.getIdPago()));
            txtMonto.setText(String.valueOf(pagoSeleccionado.getMonto()));
            cmbEstado.setValue(pagoSeleccionado.getEstado());
            cmbMetodoPago.setValue(pagoSeleccionado.getMetodoPago());

            for (Proyectos p : cmbProyecto.getItems()) {
                if (p.getIdProyecto() == pagoSeleccionado.getIdProyecto()) {
                    cmbProyecto.setValue(p);
                    break;
                }
            }
            
            for (Freelancers f : cmbFreelancer.getItems()) {
                if (f.getIdFreelancer() == pagoSeleccionado.getIdFreelancer()) {
                    cmbFreelancer.setValue(f);
                    break;
                }
            }
        }
    }

    public ArrayList<Pago> listarPagos() { 
        ArrayList<Pago> pagos = new ArrayList<>(); 
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarPagos()"); 
            ResultSet resultado = enunciado.executeQuery();
            
            while (resultado.next()) {
                Date sqlDate = resultado.getDate("fecha_pago");
                LocalDateTime fechaPago = null;
                if (sqlDate != null) {
                    fechaPago = sqlDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
                }

                pagos.add(new Pago(
                         resultado.getInt("id_pago"), 
                         resultado.getInt("id_proyecto"),
                         resultado.getInt("id_freelancer"),
                         resultado.getDouble("monto"),
                         fechaPago,
                         resultado.getString("metodo_pago"),
                         resultado.getString("estado")
                ));
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar Pagos: " + ex.getMessage());
            ex.printStackTrace();
        }
        return pagos;
    }
    
    private Pago cargarModeloPago() { 
        int idPago = txtIDPago.getText().isEmpty() ? 0 : Integer.parseInt(txtIDPago.getText());
        
        Proyectos proyectoSeleccionado = cmbProyecto.getSelectionModel().getSelectedItem();
        Freelancers freelancerSeleccionado = cmbFreelancer.getSelectionModel().getSelectedItem(); 

        if (proyectoSeleccionado == null || freelancerSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un Proyecto y un Freelancer.");
            return null;
        }
        
        if (txtMonto.getText().trim().isEmpty()) {
             JOptionPane.showMessageDialog(null, "El Monto es obligatorio.");
            return null;
        }
        
        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El Monto debe ser un valor numérico válido.");
            return null;
        }
        
        return new Pago(
            idPago, 
            proyectoSeleccionado.getIdProyecto(),   
            freelancerSeleccionado.getIdFreelancer(), 
            monto,
            null, 
            cmbMetodoPago.getValue(),
            cmbEstado.getValue()
        );
    }
    
    public void agregarPago() {
        modeloPago = cargarModeloPago();
        if (modeloPago == null) return;
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_agregarPago(?,?,?,?,?);");
            
            enunciado.setInt(1, modeloPago.getIdProyecto()); 
            enunciado.setInt(2, modeloPago.getIdFreelancer()); 
            
            enunciado.setDouble(3, modeloPago.getMonto());
            enunciado.setString(4, modeloPago.getMetodoPago());
            enunciado.setString(5, "pendiente");
            
            int registrosAgregados = enunciado.executeUpdate();
            if (registrosAgregados > 0) {
                cargarTablaPagos();
                cambiarEstado(EstadoFormulario.NINGUNA);
                JOptionPane.showMessageDialog(null, "Pago agregado con éxito.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar pago: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void actualizarPago() { 
        modeloPago = cargarModeloPago(); 
        if (modeloPago == null) return;
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_actualizarPago(?,?,?,?);"); 
            enunciado.setInt(1, modeloPago.getIdPago()); 
            enunciado.setDouble(2, modeloPago.getMonto()); 
            enunciado.setString(3, modeloPago.getMetodoPago());
            enunciado.setString(4, modeloPago.getEstado());
            
            enunciado.executeUpdate();
            cargarTablaPagos();
            cambiarEstado(EstadoFormulario.NINGUNA);
            JOptionPane.showMessageDialog(null, "Pago actualizado con éxito.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar pago: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarPago() { 
        Pago pagoSeleccionado = tablaPagos.getSelectionModel().getSelectedItem();
        if (pagoSeleccionado == null) return;

        int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el pago de ID: " + pagoSeleccionado.getIdPago() + "?", "Eliminar Pago", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().
                        prepareCall("{call sp_eliminarPago(?)}");
                enunciado.setInt(1, pagoSeleccionado.getIdPago());
                enunciado.execute();
                cargarTablaPagos();
                JOptionPane.showMessageDialog(null, "Pago eliminado con éxito.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar pago: " + e.getMessage());
                e.printStackTrace();
            }
        }
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void limpiarTextField() {
        txtIDPago.clear();
        txtMonto.clear();
        cmbProyecto.getSelectionModel().clearSelection();
        cmbFreelancer.getSelectionModel().clearSelection();
        cmbMetodoPago.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().clearSelection();
    }
    
    private void cambiarEstado(EstadoFormulario estado) {
        tipoDeAccion = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.ACTUALIZAR);
        
        txtMonto.setDisable(!activo);
        
        cmbProyecto.setDisable(estado != EstadoFormulario.AGREGAR);
        cmbFreelancer.setDisable(estado != EstadoFormulario.AGREGAR);
        
        cmbMetodoPago.setDisable(!activo); 
        cmbEstado.setDisable(!activo); 

        tablaPagos.setDisable(activo);
        txtBuscar.setDisable(activo);
        
        btnAgregar.setText(activo ? "💾 Guardar" : "➕ Agregar");
        btnEliminar.setText(activo ? "❌ Cancelar" : "🗑 Eliminar");
        
        boolean itemSelected = tablaPagos.getSelectionModel().getSelectedItem() != null;
        btnActualizar.setDisable(activo || !itemSelected);
        
        if(tipoDeAccion == EstadoFormulario.NINGUNA){
             if (itemSelected) {
                cargarPagoEnComponentes();
            } else {
                limpiarTextField();
            }
        }
    }

    @FXML
    private void manejarBotonAgregarGuardar() {
        switch (tipoDeAccion) {
            case NINGUNA:
                limpiarTextField();
                cambiarEstado(EstadoFormulario.AGREGAR);
                break;
            case AGREGAR:
                agregarPago();
                break;
            case ACTUALIZAR:
                actualizarPago();
                break;
        }
    }

    @FXML
    private void manejarBotonActualizar() {
        if (tablaPagos.getSelectionModel().getSelectedItem() == null) {
             JOptionPane.showMessageDialog(null, "Debe seleccionar un pago para editar.");
             return;
        }
        cambiarEstado(EstadoFormulario.ACTUALIZAR);
    }

    @FXML
    private void manejarBotonEliminarCancelar() {
        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            eliminarPago();
        } else {
            cambiarEstado(EstadoFormulario.NINGUNA);
        }
    }

    @FXML
    private void buscarPorMonto() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        ArrayList<Pago> resultadoBusqueda = new ArrayList<>(); 

        if (textoBusqueda.isEmpty()) {
            cargarTablaPagos();
            return;
        }
        
        for (Pago p : listaPagos) { 
            if (String.valueOf(p.getMonto()).contains(textoBusqueda) || 
                p.getMetodoPago().toLowerCase().contains(textoBusqueda) ||
                p.getEstado().toLowerCase().contains(textoBusqueda)) {
                resultadoBusqueda.add(p);
            }
        }

        tablaPagos.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (!resultadoBusqueda.isEmpty()) {
            tablaPagos.getSelectionModel().selectFirst();
            cargarPagoEnComponentes();
        } else {
            limpiarTextField();
        }
    }
    
    @FXML
    public void escenaMenuPrincipal() {
        principal.mainMenuCliente(); 
    }
    
    @FXML private Button btnProyectos,btnFacturas,btnCerrarSesion,btnPostulados;
    @FXML
    public void clicManejoEvento2(ActionEvent evento) {
        if (principal == null) {
            principal = Main.getInstancia();
        }
        if (evento.getSource() == btnProyectos) {
            principal.proyectosCliente();
        }
        if (evento.getSource() == btnPostulados) {
            principal.postuladosCliente();
        }
        if (evento.getSource() == btnFacturas) {
            principal.facturasCliente();
        }
        if (evento.getSource() == btnCerrarSesion) {
            principal.inicio();
        }
    }
}