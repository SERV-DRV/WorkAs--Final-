package org.workas.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.workas.db.Conexion;
import org.workas.model.Entrega;
import org.workas.model.Freelancers;
import org.workas.model.Proyectos;
import org.workas.system.Main;

public class EntregasFreeLanceController implements Initializable {

    private ObservableList<Entrega> listaEntregas = FXCollections.observableArrayList();
    private ObservableList<Proyectos> listaProyectos = FXCollections.observableArrayList();
    private ObservableList<Freelancers> listaFreelancers = FXCollections.observableArrayList();
    
    private Main principal;
    private Entrega modeloEntrega;

    private enum EstadoFormulario {
        AGREGAR, ACTUALIZAR, NINGUNA
    };
    private EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableView<Entrega> tableViewEntregas;
    @FXML
    private TableColumn<Entrega, Integer> colIdEntrega, colIdProyecto, colIdFreelancer;
    @FXML
    private TableColumn<Entrega, String> colArchivoUrl, colDescripcion;
    @FXML
    private TableColumn<Entrega, LocalDateTime> colFechaEntrega;
    @FXML
    private TextField txtIdEntrega, txtArchivoUrl, txtBuscar;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private ComboBox<Proyectos> cmbProyecto;
    @FXML
    private ComboBox<Freelancers> cmbFreelancer;
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableViewEntregas.setItems(listaEntregas);
        cmbProyecto.setItems(listaProyectos);
        cmbFreelancer.setItems(listaFreelancers);

        configurarColumnas();
        cargarDatosComboBox();
        cargarTablaEntregas();
        
        tableViewEntregas.setOnMouseClicked(event -> cargarEntregasEnComponentes());
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        colIdEntrega.setCellValueFactory(new PropertyValueFactory<>("idEntrega"));
        colIdProyecto.setCellValueFactory(new PropertyValueFactory<>("idProyecto"));
        colIdFreelancer.setCellValueFactory(new PropertyValueFactory<>("idFreelancer"));
        colArchivoUrl.setCellValueFactory(new PropertyValueFactory<>("archivoUrl"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
    }
    
    public void cargarDatosComboBox() {
        listaProyectos.setAll(listarProyectos());
        listaFreelancers.setAll(listarFreelancers());
    }

    public void cargarTablaEntregas() {
        listaEntregas.setAll(listarEntregas());
        tableViewEntregas.getSelectionModel().selectFirst();
        cargarEntregasEnComponentes();
    }

    public void cargarEntregasEnComponentes() {
        Entrega entregaSeleccionada = tableViewEntregas.getSelectionModel().getSelectedItem();
        if (entregaSeleccionada != null) {
            txtIdEntrega.setText(String.valueOf(entregaSeleccionada.getIdEntrega()));
            txtArchivoUrl.setText(entregaSeleccionada.getArchivoUrl());
            txtDescripcion.setText(entregaSeleccionada.getDescripcion());

            for (Proyectos p : cmbProyecto.getItems()) {
                if (p.getIdProyecto() == entregaSeleccionada.getIdProyecto()) {
                    cmbProyecto.setValue(p);
                    break;
                }
            }
            for (Freelancers f : cmbFreelancer.getItems()) {
                if (f.getIdFreelancer() == entregaSeleccionada.getIdFreelancer()) {
                    cmbFreelancer.setValue(f);
                    break;
                }
            }
        }
    }

    public ArrayList<Entrega> listarEntregas() {
        ArrayList<Entrega> entregas = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarentregas()");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                Timestamp ts = resultado.getTimestamp("fecha_entrega");
                entregas.add(new Entrega(
                        resultado.getInt("id_entrega"),
                        resultado.getInt("id_proyecto"),
                        resultado.getInt("id_freelancer"),
                        resultado.getString("archivo_url"),
                        resultado.getString("descripcion"),
                        ts != null ? ts.toLocalDateTime() : null
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return entregas;
    }
    
    public ArrayList<Proyectos> listarProyectos() {
        ArrayList<Proyectos> items = new ArrayList<>();
        try {
            CallableStatement statement = Conexion.getInstancia().getConexion().prepareCall("call sp_listarproyectos()");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                items.add(new Proyectos(rs.getInt("id_proyecto"), rs.getString("titulo"), null, null, null, null, null, null, null, null, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public ArrayList<Freelancers> listarFreelancers() {
        ArrayList<Freelancers> items = new ArrayList<>();
        try {
            CallableStatement statement = Conexion.getInstancia().getConexion().prepareCall("call sp_listarfreelancers()");
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                items.add(new Freelancers(rs.getInt("id_freelancer"), rs.getString("nombre"), rs.getString("apellido"), null, null, null, null, null, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private Entrega cargarModeloEntrega() {
        int idEntrega = txtIdEntrega.getText().isEmpty() ? 0 : Integer.parseInt(txtIdEntrega.getText());
        Proyectos proyecto = cmbProyecto.getSelectionModel().getSelectedItem();
        Freelancers freelancer = cmbFreelancer.getSelectionModel().getSelectedItem();

        if (proyecto == null || freelancer == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un Proyecto y un Freelancer.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (txtArchivoUrl.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "La URL del archivo es obligatoria.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Entrega(
            idEntrega,
            proyecto.getIdProyecto(),
            freelancer.getIdFreelancer(),
            txtArchivoUrl.getText(),
            txtDescripcion.getText(),
            null
        );
    }

    public void agregarEntrega() {
        modeloEntrega = cargarModeloEntrega();
        if (modeloEntrega == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_agregarentrega(?,?,?,?);");
            enunciado.setInt(1, modeloEntrega.getIdProyecto());
            enunciado.setInt(2, modeloEntrega.getIdFreelancer());
            enunciado.setString(3, modeloEntrega.getArchivoUrl());
            enunciado.setString(4, modeloEntrega.getDescripcion());
            if (enunciado.executeUpdate() > 0) {
                cargarTablaEntregas();
                cambiarEstado(EstadoFormulario.NINGUNA);
                JOptionPane.showMessageDialog(null, "Entrega agregada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar entrega: " + ex.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarEntrega() {
        modeloEntrega = cargarModeloEntrega();
        if (modeloEntrega == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_actualizarentrega(?,?,?,?);");
            enunciado.setInt(1, modeloEntrega.getIdEntrega());
            enunciado.setInt(2, modeloEntrega.getIdProyecto());
            enunciado.setString(3, modeloEntrega.getArchivoUrl());
            enunciado.setString(4, modeloEntrega.getDescripcion());
            enunciado.executeUpdate();
            cargarTablaEntregas();
            cambiarEstado(EstadoFormulario.NINGUNA);
            JOptionPane.showMessageDialog(null, "Entrega editada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al editar entrega: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarEntrega() {
        Entrega entregaSeleccionada = tableViewEntregas.getSelectionModel().getSelectedItem();
        if (entregaSeleccionada == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una entrega para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de eliminar la entrega con ID: " + entregaSeleccionada.getIdEntrega() + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("{call sp_eliminarentrega(?)}");
                enunciado.setInt(1, entregaSeleccionada.getIdEntrega());
                enunciado.execute();
                cargarTablaEntregas();
                limpiarTextField();
                JOptionPane.showMessageDialog(null, "Entrega eliminada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar entrega: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void limpiarTextField() {
        txtIdEntrega.clear();
        txtArchivoUrl.clear();
        txtDescripcion.clear();
        cmbProyecto.getSelectionModel().clearSelection();
        cmbFreelancer.getSelectionModel().clearSelection();
    }

    private void cambiarEstado(EstadoFormulario estado) {
        tipoDeAccion = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.ACTUALIZAR);

        txtArchivoUrl.setDisable(!activo);
        txtDescripcion.setDisable(!activo);
        cmbProyecto.setDisable(!activo);
        cmbFreelancer.setDisable(!activo);
        tableViewEntregas.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnAgregar.setText(activo ? "💾 Guardar" : "➕ Agregar");
        btnEliminar.setText(activo ? "❌ Cancelar" : "🗑 Eliminar");

        boolean itemSelected = tableViewEntregas.getSelectionModel().getSelectedItem() != null;
        btnActualizar.setDisable(activo || !itemSelected);

        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            if (itemSelected) {
                cargarEntregasEnComponentes();
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
                agregarEntrega();
                break;
            case ACTUALIZAR:
                actualizarEntrega();
                break;
        }
    }

    @FXML
    private void manejarBotonActualizar() {
        if (tableViewEntregas.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una entrega para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cambiarEstado(EstadoFormulario.ACTUALIZAR);
    }

    @FXML
    private void manejarBotonEliminarCancelar() {
        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            eliminarEntrega();
        } else {
            cambiarEstado(EstadoFormulario.NINGUNA);
        }
    }

    @FXML
    private void buscarEntrega() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        if (textoBusqueda.isEmpty()) {
            tableViewEntregas.setItems(listaEntregas);
        } else {
            ObservableList<Entrega> resultadoBusqueda = FXCollections.observableArrayList();
            for (Entrega e : this.listaEntregas) {
                if ((e.getArchivoUrl() != null && e.getArchivoUrl().toLowerCase().contains(textoBusqueda)) ||
                    (e.getDescripcion() != null && e.getDescripcion().toLowerCase().contains(textoBusqueda))) {
                    resultadoBusqueda.add(e);
                }
            }
            tableViewEntregas.setItems(resultadoBusqueda);
        }
        if (!tableViewEntregas.getItems().isEmpty()) {
            tableViewEntregas.getSelectionModel().selectFirst();
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
    private void clicManejoEvento2(ActionEvent event) {
    }
    
    @FXML
    private void clicManejoEvento(ActionEvent event) {
        if (principal != null) {
            principal.inicio();
        }
    }
}