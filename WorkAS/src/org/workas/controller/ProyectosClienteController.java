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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.workas.db.Conexion;
import org.workas.model.Proyectos;
import org.workas.system.Main;

public class ProyectosClienteController implements Initializable {

    private Main principal;

    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar;

    @FXML
    private TextField txtID, txtBuscar, txtTitulo, txtPresupuesto, txtIdCliente; // txtIdCliente puede ser fijo/oculto

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private ComboBox<String> cmbEstado; 

    @FXML
    private ComboBox<Integer> cmbIdCategoria;

    @FXML
    private DatePicker dpFechaEntrega;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public Main getPrincipal() {
        return principal;
    }

    @FXML
    private TableView<Proyectos> tablaProyectos;

    private Proyectos modeloProyecto;

    private enum EstadoFormulario {
        AGREGAR, ELIMINAR, ACTUALIZAR, NINGUNA
    };
    EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableColumn colIDProyecto, colTitulo, colDescripcion, colCategoria, colPresupuesto, colFechaEntrega, colEstado;

    private ObservableList<Proyectos> listaProyectos;
    private ObservableList<Integer> listaCategoriasIDs;
    private final String[] estadosProyecto = {"publicado", "en curso", "finalizado", "cancelado"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumna();
        cargarListaCategorias(); 
        cargarTablaProyectos();
        cmbEstado.setItems(FXCollections.observableArrayList(estadosProyecto));

        tablaProyectos.setOnMouseClicked(eventHandler -> {
            if (tablaProyectos.getSelectionModel().getSelectedItem() != null) {
                cargarProyectosEnComponentes();
            }
        });

        colIDProyecto.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.05));
        colTitulo.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.18));
        colDescripcion.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.20));
        colCategoria.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.10));
        colPresupuesto.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.12));
        colFechaEntrega.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.15));
        colEstado.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.10));

        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumna() {
        colIDProyecto.setCellValueFactory(new PropertyValueFactory<Proyectos, Integer>("idProyecto"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<Proyectos, String>("titulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Proyectos, String>("descripcion"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<Proyectos, Integer>("idCategoria"));
        colPresupuesto.setCellValueFactory(new PropertyValueFactory<Proyectos, BigDecimal>("presupuesto"));
        colFechaEntrega.setCellValueFactory(new PropertyValueFactory<Proyectos, Date>("fechaEntrega"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Proyectos, String>("estado"));
    }

    public void cargarListaCategorias() {
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarcategorias()");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                ids.add(resultado.getInt("id_categoria"));
            }
        } catch (SQLException ex) {
            System.out.println("Error al cargar IDs de Categorías");
            ex.printStackTrace();
        }
        listaCategoriasIDs = FXCollections.observableArrayList(ids);
        cmbIdCategoria.setItems(listaCategoriasIDs);
    }

    public void cargarTablaProyectos() {
        listaProyectos = FXCollections.observableArrayList(listarProyectos());
        tablaProyectos.setItems(listaProyectos);
        if (!listaProyectos.isEmpty()) {
            tablaProyectos.getSelectionModel().selectFirst();
            cargarProyectosEnComponentes();
        } else {
            limpiarComponentes();
        }
    }

    public void cargarProyectosEnComponentes() {
        Proyectos proyectoSeleccionado = tablaProyectos.getSelectionModel().getSelectedItem();
        if (proyectoSeleccionado != null) {
            txtID.setText(String.valueOf(proyectoSeleccionado.getIdProyecto()));
            txtTitulo.setText(proyectoSeleccionado.getTitulo());
            txtDescripcion.setText(proyectoSeleccionado.getDescripcion());
            txtPresupuesto.setText(String.valueOf(proyectoSeleccionado.getPresupuesto()));
            cmbIdCategoria.getSelectionModel().select(Integer.valueOf(proyectoSeleccionado.getIdCategoria()));
            dpFechaEntrega.setValue(proyectoSeleccionado.getFechaEntrega().toLocalDate());
            cmbEstado.getSelectionModel().select(proyectoSeleccionado.getEstado());
            txtIdCliente.setText(String.valueOf(proyectoSeleccionado.getIdCliente()));
        }
    }

    public ArrayList<Proyectos> listarProyectos() {
        ArrayList<Proyectos> proyectos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "call sp_listarproyectos()";
            CallableStatement enunciado = conexion.prepareCall(sql);
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                proyectos.add(new Proyectos(
                        resultado.getInt("id_proyecto"),
                        resultado.getString("titulo"),
                        resultado.getString("descripcion"),
                        resultado.getInt("id_categoria"),
                        resultado.getInt("id_cliente"),
                        resultado.getObject("id_freelancer") != null ? resultado.getInt("id_freelancer") : null,
                        resultado.getBigDecimal("presupuesto"),
                        resultado.getBigDecimal("monto_acordado"),
                        resultado.getString("estado"),
                        resultado.getDate("fecha_creacion"),
                        resultado.getDate("fecha_entrega")
                ));
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar Proyectos: " + ex.getMessage());
            ex.printStackTrace();
        }
        return proyectos;
    }

    public Proyectos obtenerModeloProyecto() {
    int idProyecto = 0;
    int idCliente = 0; 
    int idCategoria = 0;
    BigDecimal presupuesto = BigDecimal.ZERO;
    String titulo = txtTitulo.getText();
    String descripcion = txtDescripcion.getText();
    Date fechaEntrega = null;
    String estado = cmbEstado.getSelectionModel().getSelectedItem();

    try {
        if (tipoDeAccion == EstadoFormulario.ACTUALIZAR || tipoDeAccion == EstadoFormulario.ELIMINAR) {
            idProyecto = Integer.parseInt(txtID.getText().trim());
        }
    } catch (Exception e) {}
    
    if (tipoDeAccion == EstadoFormulario.ELIMINAR) {       
        return new Proyectos(
            idProyecto,        // int - ID
            "",                // String - Título
            "",                // String - Descripción
            0,                 // int - idCategoria 
            0,                 // int - idCliente 
            null,              // Integer - idFreelancer 
            BigDecimal.ZERO,   // BigDecimal - Presupuesto 
            null,              // BigDecimal - MontoAcordado (
            "",                // String - Estado
            null,              // Date - fechaCreacion
            null               // Date - fechaEntrega
        );
    }
    
    try {
        idCliente = Integer.parseInt(txtIdCliente.getText().trim()); 
    } catch (Exception e) {
         JOptionPane.showMessageDialog(null, "ID de Cliente no válido o no asignado.");
         return null;
    }

    if (tipoDeAccion == EstadoFormulario.AGREGAR) {
        return new Proyectos(idCliente, idCategoria, titulo, descripcion, presupuesto, fechaEntrega);
    } else if (tipoDeAccion == EstadoFormulario.ACTUALIZAR) {
        return new Proyectos(idProyecto, titulo, descripcion, presupuesto, fechaEntrega, estado);
    } else {
        return null;
    }
}

    // --- CRUD Funcionalidad ---
    public void agregarProyecto() {
        modeloProyecto = obtenerModeloProyecto();
        if (modeloProyecto == null) {
            return;
        }

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_agregarproyecto(?,?,?,?,?,?,?);");
            enunciado.setInt(1, modeloProyecto.getIdCliente());
            enunciado.setInt(2, modeloProyecto.getIdCategoria());
            enunciado.setString(3, modeloProyecto.getTitulo());
            enunciado.setString(4, modeloProyecto.getDescripcion());
            enunciado.setBigDecimal(5, modeloProyecto.getPresupuesto());
            enunciado.setDate(6, modeloProyecto.getFechaEntrega());
            enunciado.setString(7, "publicado");

            enunciado.execute();
            cargarTablaProyectos();
            cambiarEstado(EstadoFormulario.NINGUNA);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar proyecto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void actualizarProyecto() {
        modeloProyecto = obtenerModeloProyecto();
        if (modeloProyecto == null) {
            return;
        }
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_actualizarproyecto(?,?,?,?,?,?);");
            enunciado.setInt(1, modeloProyecto.getIdProyecto());
            enunciado.setString(2, modeloProyecto.getTitulo());
            enunciado.setString(3, modeloProyecto.getDescripcion());
            enunciado.setBigDecimal(4, modeloProyecto.getPresupuesto());
            enunciado.setDate(5, modeloProyecto.getFechaEntrega());
            enunciado.setString(6, modeloProyecto.getEstado());

            enunciado.executeUpdate();
            cargarTablaProyectos();
            cambiarEstado(EstadoFormulario.NINGUNA);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar proyecto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarProyecto() {
        Proyectos proyectoSeleccionado = tablaProyectos.getSelectionModel().getSelectedItem();
        if (proyectoSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un proyecto para eliminar.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de eliminar el proyecto: " + proyectoSeleccionado.getTitulo() + "?",
                "Eliminar Proyecto", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_eliminarproyecto(?);");
                enunciado.setInt(1, proyectoSeleccionado.getIdProyecto());
                enunciado.execute();
                cargarTablaProyectos();
                JOptionPane.showMessageDialog(null, "Proyecto eliminado con éxito.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar proyecto. Asegúrese de que no tenga postulaciones o pagos asociados. " + e.getMessage());
                e.printStackTrace();
            }
        }
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void limpiarComponentes() {
        txtID.clear();
        txtTitulo.clear();
        txtDescripcion.clear();
        txtPresupuesto.clear();
        dpFechaEntrega.setValue(null);
        cmbIdCategoria.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().clearSelection();
    }

    private void cambiarEstado(EstadoFormulario estado) {
        tipoDeAccion = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.ACTUALIZAR);

        txtTitulo.setDisable(!activo);
        txtDescripcion.setDisable(!activo);
        txtPresupuesto.setDisable(!activo);
        cmbIdCategoria.setDisable(!activo);
        dpFechaEntrega.setDisable(!activo);
        cmbEstado.setDisable(estado != EstadoFormulario.ACTUALIZAR);

        tablaProyectos.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnAgregar.setText(activo ? "💾 Guardar" : "➕ Agregar");
        btnEliminar.setText((tipoDeAccion == EstadoFormulario.NINGUNA && tablaProyectos.getSelectionModel().getSelectedItem() != null) ? "🗑 Eliminar" : "❌ Cancelar");
        btnActualizar.setDisable(activo || tablaProyectos.getSelectionModel().getSelectedItem() == null);

        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            if (tablaProyectos.getSelectionModel().getSelectedItem() != null) {
                cargarProyectosEnComponentes();
            } else {
                limpiarComponentes();
            }
        }
    }

    @FXML
    private void manejarBotonAgregarGuardar() {
        switch (tipoDeAccion) {
            case NINGUNA:
                limpiarComponentes();
                
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
            JOptionPane.showMessageDialog(null, "Debe seleccionar un proyecto para editar.");
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
    private void buscarProyecto() {
        ArrayList<Proyectos> resultadoBusqueda = new ArrayList<>();
        String textoBusqueda = txtBuscar.getText().toLowerCase();

        if (textoBusqueda.isEmpty()) {
            cargarTablaProyectos();
            return;
        }

        for (Proyectos proyecto : listaProyectos) {
            if (proyecto.getTitulo().toLowerCase().contains(textoBusqueda)
                    || proyecto.getDescripcion().toLowerCase().contains(textoBusqueda)) {
                resultadoBusqueda.add(proyecto);
            }
        }

        tablaProyectos.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (!resultadoBusqueda.isEmpty()) {
            tablaProyectos.getSelectionModel().selectFirst();
            cargarProyectosEnComponentes();
        } else {
            limpiarComponentes();
        }
    }

    @FXML
    private void volverAlMenu() {
        principal.mainMenuCliente();
    }
    
    
}
