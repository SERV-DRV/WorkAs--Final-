package org.workas.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.workas.db.Conexion;
import org.workas.model.Categoria;
import org.workas.model.Clientes;
import org.workas.model.Proyectos;
import org.workas.system.Main;

public class ProyectosClienteController implements Initializable {

    private ObservableList<Proyectos> listaProyectos = FXCollections.observableArrayList();
    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private ObservableList<Clientes> listaClientes = FXCollections.observableArrayList();

    private Main principal;
    private Proyectos modeloProyecto;
    
    private final String[] estadosProyecto = {"publicado", "en curso", "finalizado", "cancelado"};

    private enum EstadoFormulario {
        AGREGAR, ACTUALIZAR, NINGUNA
    };
    private EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableView<Proyectos> tablaProyectos;
    @FXML
    private TableColumn<Proyectos, ?> colIDProyecto, colTitulo, colDescripcion, colCategoria, colPresupuesto, colFechaEntrega, colEstado;
    @FXML
    private TextField txtIDProyecto, txtBuscar, txtTitulo, txtDescripcion, txtPresupuesto, txtMontoAcordado;
    @FXML
    private ComboBox<Clientes> cmbCliente;
    @FXML
    private ComboBox<Categoria> cmbCategoria; 
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private DatePicker dpFechaEntrega;
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar, btnPostulados, btnPagos, btnFacturas, btnCerrarSesion;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tablaProyectos.setItems(listaProyectos);
        cmbCliente.setItems(listaClientes);
        cmbCategoria.setItems(listaCategorias);
        cmbEstado.setItems(FXCollections.observableArrayList(estadosProyecto));
        
        configurarColumnas();
        cargarDatosComboBox();
        cargarTablaProyectos();
        
        tablaProyectos.setOnMouseClicked(event -> cargarProyectosEnComponentes());
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        colIDProyecto.setCellValueFactory(new PropertyValueFactory<>("idProyecto"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria")); 
        colPresupuesto.setCellValueFactory(new PropertyValueFactory<>("presupuesto"));
        colFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    public void cargarDatosComboBox(){
        listaClientes.setAll(listarTodosLosClientes());
        listaCategorias.setAll(listarTodasLasCategorias());
    }
    
    public void cargarTablaProyectos() {
        listaProyectos.setAll(listarProyectos());
        tablaProyectos.getSelectionModel().selectFirst();
        cargarProyectosEnComponentes();
    }

    public void cargarProyectosEnComponentes() {
        Proyectos proyecto = tablaProyectos.getSelectionModel().getSelectedItem();
        if (proyecto != null) {
            txtIDProyecto.setText(String.valueOf(proyecto.getIdProyecto()));
            txtTitulo.setText(proyecto.getTitulo());
            txtDescripcion.setText(proyecto.getDescripcion());
            txtPresupuesto.setText(String.valueOf(proyecto.getPresupuesto()));
            txtMontoAcordado.setText(proyecto.getMontoAcordado() != null ? String.valueOf(proyecto.getMontoAcordado()) : "");
            
            if (proyecto.getFechaEntrega() != null) {
                dpFechaEntrega.setValue(proyecto.getFechaEntrega().toLocalDate());
            } else {
                 dpFechaEntrega.setValue(null);
            }
            cmbEstado.setValue(proyecto.getEstado());
            cmbCliente.setValue(proyecto.getCliente());
            cmbCategoria.setValue(proyecto.getCategoria());
        }
    }

    public ArrayList<Proyectos> listarProyectos() {
        ArrayList<Proyectos> proyectos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarproyectos()");
            ResultSet resultado = enunciado.executeQuery();
            
            while (resultado.next()) {
                int idCliente = resultado.getInt("id_cliente");
                int idCategoria = resultado.getInt("id_categoria");

                Clientes clienteEncontrado = null;
                for(Clientes c : listaClientes){
                    if(c.getIdCliente() == idCliente){
                        clienteEncontrado = c;
                        break;
                    }
                }

                Categoria categoriaEncontrada = null;
                for(Categoria cat : listaCategorias){
                    if(cat.getIdCategoria() == idCategoria){
                        categoriaEncontrada = cat;
                        break;
                    }
                }
                
                proyectos.add(new Proyectos(
                         resultado.getInt("id_proyecto"),
                         resultado.getString("titulo"),
                         resultado.getString("descripcion"),
                         categoriaEncontrada,
                         clienteEncontrado,
                         resultado.getObject("id_freelancer") != null ? resultado.getInt("id_freelancer") : null,
                         resultado.getBigDecimal("presupuesto"),
                         resultado.getBigDecimal("monto_acordado"),
                         resultado.getString("estado"),
                         resultado.getDate("fecha_creacion"),
                         resultado.getDate("fecha_entrega")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return proyectos;
    }
    
    public ArrayList<Clientes> listarTodosLosClientes(){
        ArrayList<Clientes> clientes = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_listarclientes();");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                clientes.add(new Clientes(
                         resultado.getInt("id_cliente"),
                         resultado.getString("nombre"),
                         resultado.getString("apellido"),
                         resultado.getString("email"),
                         resultado.getString("contraseña"),
                         resultado.getString("telefono"),
                         resultado.getString("fecha_registro")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
    
    public ArrayList<Categoria> listarTodasLasCategorias(){
        ArrayList<Categoria> categorias = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_listarcategorias();"); 
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                categorias.add(new Categoria(
                         resultado.getInt("id_categoria"),
                         resultado.getString("nombre"),
                         resultado.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }
    
    private Proyectos cargarModeloProyecto() {
        int idProyecto = txtIDProyecto.getText().isEmpty() ? 0 : Integer.parseInt(txtIDProyecto.getText());
        Clientes cliente = cmbCliente.getSelectionModel().getSelectedItem();
        Categoria categoria = cmbCategoria.getSelectionModel().getSelectedItem();

        if (cliente == null || categoria == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un Cliente y una Categoría.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (txtTitulo.getText().trim().isEmpty() || dpFechaEntrega.getValue() == null) {
             JOptionPane.showMessageDialog(null, "Título y Fecha de Entrega son obligatorios.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        BigDecimal presupuesto;
        try {
            presupuesto = new BigDecimal(txtPresupuesto.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Presupuesto debe ser un valor numérico válido.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        return new Proyectos(idProyecto, txtTitulo.getText(), txtDescripcion.getText(), categoria, cliente, null, presupuesto, null, cmbEstado.getValue(), null, Date.valueOf(dpFechaEntrega.getValue()));
    }
    
    public void agregarProyecto() {
        modeloProyecto = cargarModeloProyecto();
        if (modeloProyecto == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_agregarproyecto(?,?,?,?,?,?,?);");
            enunciado.setInt(1, modeloProyecto.getIdCliente()); 
            enunciado.setInt(2, modeloProyecto.getIdCategoria()); 
            enunciado.setString(3, modeloProyecto.getTitulo());
            enunciado.setString(4, modeloProyecto.getDescripcion());
            enunciado.setBigDecimal(5, modeloProyecto.getPresupuesto());
            enunciado.setDate(6, modeloProyecto.getFechaEntrega());
            enunciado.setString(7, "publicado"); 
            
            if (enunciado.executeUpdate() > 0) {
                cargarTablaProyectos();
                cambiarEstado(EstadoFormulario.NINGUNA);
                JOptionPane.showMessageDialog(null, "Proyecto agregado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar proyecto: " + ex.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarProyecto() {
        modeloProyecto = cargarModeloProyecto();
        if (modeloProyecto == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_actualizarproyecto(?,?,?,?,?,?);"); 
            enunciado.setInt(1, modeloProyecto.getIdProyecto());
            enunciado.setString(2, modeloProyecto.getTitulo());
            enunciado.setString(3, modeloProyecto.getDescripcion());
            enunciado.setBigDecimal(4, modeloProyecto.getPresupuesto());
            enunciado.setDate(5, Date.valueOf(modeloProyecto.getFechaEntrega().toLocalDate()));
            enunciado.setString(6, modeloProyecto.getEstado());
            
            enunciado.executeUpdate();
            cargarTablaProyectos();
            cambiarEstado(EstadoFormulario.NINGUNA);
            JOptionPane.showMessageDialog(null, "Proyecto actualizado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar proyecto: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarProyecto() {
        Proyectos proyectoSeleccionado = tablaProyectos.getSelectionModel().getSelectedItem();
        if (proyectoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un proyecto para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el proyecto: " + proyectoSeleccionado.getTitulo() + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("{call sp_eliminarproyecto(?)}");
                enunciado.setInt(1, proyectoSeleccionado.getIdProyecto());
                enunciado.execute();
                cargarTablaProyectos();
                JOptionPane.showMessageDialog(null, "Proyecto eliminado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar proyecto: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void limpiarTextField() {
        txtIDProyecto.clear();
        txtTitulo.clear();
        txtDescripcion.clear();
        txtPresupuesto.clear();
        txtMontoAcordado.clear();
        dpFechaEntrega.setValue(null);
        cmbCliente.getSelectionModel().clearSelection();
        cmbCategoria.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().clearSelection();
    }
    
    private void cambiarEstado(EstadoFormulario estado) {
        tipoDeAccion = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.ACTUALIZAR);
        
        txtTitulo.setDisable(!activo);
        txtDescripcion.setDisable(!activo);
        txtPresupuesto.setDisable(!activo);
        cmbCliente.setDisable(!activo);
        cmbCategoria.setDisable(!activo);
        dpFechaEntrega.setDisable(!activo);
        txtMontoAcordado.setDisable(estado != EstadoFormulario.ACTUALIZAR);
        cmbEstado.setDisable(estado != EstadoFormulario.ACTUALIZAR);
        tablaProyectos.setDisable(activo);
        txtBuscar.setDisable(activo);
        
        btnAgregar.setText(activo ? "💾 Guardar" : "➕ Agregar");
        btnEliminar.setText(activo ? "❌ Cancelar" : "🗑 Eliminar");
        
        boolean itemSelected = tablaProyectos.getSelectionModel().getSelectedItem() != null;
        btnActualizar.setDisable(activo || !itemSelected);
        
        if(tipoDeAccion == EstadoFormulario.NINGUNA){
             if (itemSelected) {
                cargarProyectosEnComponentes();
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
                agregarProyecto();
                break;
            case ACTUALIZAR:
                actualizarProyecto();
                break;
        }
    }

    @FXML
    private void manejarBotonActualizar() {
        if (tablaProyectos.getSelectionModel().getSelectedItem() == null) {
             JOptionPane.showMessageDialog(null, "Debe seleccionar un proyecto para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
             return;
        }
        cambiarEstado(EstadoFormulario.ACTUALIZAR);
    }

    @FXML
    private void manejarBotonEliminarCancelar() {
        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            eliminarProyecto();
        } else {
            cambiarEstado(EstadoFormulario.NINGUNA);
        }
    }

    @FXML
    private void buscarPorTitulo() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        if (textoBusqueda.isEmpty()) {
            tablaProyectos.setItems(listaProyectos);
            return;
        }
        
        ObservableList<Proyectos> resultadoBusqueda = FXCollections.observableArrayList();
        for (Proyectos p : listaProyectos) {
            if (p.getTitulo().toLowerCase().contains(textoBusqueda)) {
                resultadoBusqueda.add(p);
            }
        }

        tablaProyectos.setItems(resultadoBusqueda);
        if (!resultadoBusqueda.isEmpty()) {
            tablaProyectos.getSelectionModel().selectFirst();
        } else {
            limpiarTextField();
        }
    }
    
    @FXML
    public void escenaMenuPrincipal() {
        if (principal != null) {
            principal.mainMenuCliente();
        }
    }
    
    @FXML
    public void clicManejoEvento2(ActionEvent evento) {
        if (principal == null) {
            principal = Main.getInstancia();
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
        if (evento.getSource() == btnCerrarSesion) {
            principal.inicio();
        }
    }
}