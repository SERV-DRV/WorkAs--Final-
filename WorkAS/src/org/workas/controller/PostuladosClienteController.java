package org.workas.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime; // Necesario para el modelo Postulacion
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.workas.model.Postulacion;
import org.workas.model.Freelancers;
import org.workas.system.Main;
import java.sql.Date;
import java.time.ZoneId;

public class PostuladosClienteController implements Initializable {

    private ObservableList<Postulacion> listaPostulaciones;
    private ObservableList<Proyectos> listaProyectos;
    private ObservableList<Freelancers> listaFreelancers;

    private Main principal;
    private Postulacion modeloPostulacion;

    private final String[] estadosPostulacion = {"pendiente", "aceptado", "rechazado", "retirado"};

    private enum EstadoFormulario {
        AGREGAR, ELIMINAR, ACTUALIZAR, NINGUNA
    };
    EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableView<Postulacion> tablaPostulados;
    @FXML
    private TableColumn colIDPostulacion, colIDProyecto, colIDFreelancer, colMensaje, colMontoOfrecido, colFechaPostulacion, colEstado;
    @FXML
    private TextField txtIDPostulacion, txtBuscar, txtMontoOfrecido, txtMensaje;

    @FXML
    private ComboBox<Proyectos> cmbProyecto;
    @FXML
    private ComboBox<Freelancers> cmbFreelancer;

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
        cargarTablaPostulados();
        cmbEstado.setItems(FXCollections.observableArrayList(estadosPostulacion));
        configurarColumnas();

        tablaPostulados.setOnMouseClicked(eventHandler -> cargarPostulacionesEnComponentes());

        // --- Configuración de Columnas (Ajustadas a los nuevos nombres de getters y tipos) ---
        colIDPostulacion.prefWidthProperty().bind(tablaPostulados.widthProperty().multiply(0.05));
        colIDProyecto.prefWidthProperty().bind(tablaPostulados.widthProperty().multiply(0.10));
        colIDFreelancer.prefWidthProperty().bind(tablaPostulados.widthProperty().multiply(0.20));
        colMontoOfrecido.prefWidthProperty().bind(tablaPostulados.widthProperty().multiply(0.15));
        colMensaje.prefWidthProperty().bind(tablaPostulados.widthProperty().multiply(0.30));
        colFechaPostulacion.prefWidthProperty().bind(tablaPostulados.widthProperty().multiply(0.10));
        colEstado.prefWidthProperty().bind(tablaPostulados.widthProperty().multiply(0.10));

        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        // Se usan los nombres de los getters del modelo Postulacion proporcionado
        colIDPostulacion.setCellValueFactory(new PropertyValueFactory<Postulacion, Integer>("idPostulacion"));
        colIDProyecto.setCellValueFactory(new PropertyValueFactory<Postulacion, Integer>("idProyecto")); // Usando el ID
        colIDFreelancer.setCellValueFactory(new PropertyValueFactory<Postulacion, Integer>("idFreelancer")); // Usando el ID
        colMontoOfrecido.setCellValueFactory(new PropertyValueFactory<Postulacion, Double>("montoOfrecido")); // Usando double
        colMensaje.setCellValueFactory(new PropertyValueFactory<Postulacion, String>("mensaje"));
        colFechaPostulacion.setCellValueFactory(new PropertyValueFactory<Postulacion, LocalDateTime>("fechaPostulacion")); // Usando LocalDateTime
        colEstado.setCellValueFactory(new PropertyValueFactory<Postulacion, String>("estado"));
    }

    // --- Lógica de Carga de Datos de ComboBox (Mantenida) ---
    // NOTA: Se asume que Proyectos y Freelancers tienen un toString() adecuado para el ComboBox.
    private ArrayList<Proyectos> cargarModeloProyectos() {
        ArrayList<Proyectos> proyectos = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_listarproyectos();");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                // Constructor asumido (id, titulo, ...)
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

    private ArrayList<Freelancers> cargarModeloFreelancers() {
        ArrayList<Freelancers> freelancers = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_listarfreelancers();");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                // Constructor del modelo Freelancers: (id, nombre, apellido, email, contrasena, telefono, especialidad, portafolioUrl, fechaRegistro)
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

    // --- Lógica de la Tabla y Componentes ---
    public void cargarTablaPostulados() {
        listaPostulaciones = FXCollections.observableArrayList(listarPostulaciones());
        tablaPostulados.setItems(listaPostulaciones);
        tablaPostulados.getSelectionModel().selectFirst();
        cargarPostulacionesEnComponentes();
    }

    public void cargarPostulacionesEnComponentes() {
        Postulacion postulacionSeleccionada = tablaPostulados.getSelectionModel().getSelectedItem();
        if (postulacionSeleccionada != null) {
            txtIDPostulacion.setText(String.valueOf(postulacionSeleccionada.getIdPostulacion()));

            // Usando los nuevos campos: mensaje y montoOfrecido
            txtMontoOfrecido.setText(String.valueOf(postulacionSeleccionada.getMontoOfrecido()));
            txtMensaje.setText(postulacionSeleccionada.getMensaje());
            cmbEstado.setValue(postulacionSeleccionada.getEstado());

            // Seleccionar el OBJETO Proyecto en el ComboBox usando el ID
            for (Proyectos p : cmbProyecto.getItems()) {
                if (p.getIdProyecto() == postulacionSeleccionada.getIdProyecto()) {
                    cmbProyecto.setValue(p);
                    break;
                }
            }

            // Seleccionar el OBJETO Freelancer en el ComboBox usando el ID
            for (Freelancers f : cmbFreelancer.getItems()) {
                if (f.getIdFreelancer() == postulacionSeleccionada.getIdFreelancer()) {
                    cmbFreelancer.setValue(f);
                    break;
                }
            }
        }
    }

    public ArrayList<Postulacion> listarPostulaciones() {
        ArrayList<Postulacion> postulaciones = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarPostulaciones()");
            ResultSet resultado = enunciado.executeQuery();

            while (resultado.next()) {
                Date sqlDate = resultado.getDate("fecha_postulacion");
                LocalDateTime fechaPostulacion = null;
                if (sqlDate != null) {
                    fechaPostulacion = sqlDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
                }

                // Constructor del modelo Postulacion (id, idProyecto, idFreelancer, mensaje, montoOfrecido, estado, fechaPostulacion)
                postulaciones.add(new Postulacion(
                        resultado.getInt("id_postulacion"),
                        resultado.getInt("id_proyecto"),
                        resultado.getInt("id_freelancer"),
                        resultado.getString("mensaje"), 
                        resultado.getDouble("monto_ofrecido"),
                        resultado.getString("estado"),
                        fechaPostulacion
                ));
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar Postulaciones: " + ex.getMessage());
            ex.printStackTrace();
        }
        return postulaciones;
    }

    private Postulacion cargarModeloPostulacion() {
        int idPostulacion = txtIDPostulacion.getText().isEmpty() ? 0 : Integer.parseInt(txtIDPostulacion.getText());

        // Obtener OBJETOS seleccionados para obtener sus IDs
        Proyectos proyectoSeleccionado = cmbProyecto.getSelectionModel().getSelectedItem();
        Freelancers freelancerSeleccionado = cmbFreelancer.getSelectionModel().getSelectedItem();

        if (proyectoSeleccionado == null || freelancerSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un Proyecto y un Freelancer.");
            return null;
        }

        if (txtMensaje.getText().trim().isEmpty() || txtMontoOfrecido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Monto Ofrecido y Mensaje son obligatorios.");
            return null;
        }

        double montoOfrecido;
        try {
            montoOfrecido = Double.parseDouble(txtMontoOfrecido.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Monto Ofrecido debe ser un valor numérico válido.");
            return null;
        }

        // Constructor del modelo Postulacion (id, idProyecto, idFreelancer, mensaje, montoOfrecido, estado, fechaPostulacion)
        return new Postulacion(
                idPostulacion,
                proyectoSeleccionado.getIdProyecto(),
                freelancerSeleccionado.getIdFreelancer(),
                txtMensaje.getText(), // Usando Mensaje
                montoOfrecido, // Usando double
                cmbEstado.getValue(),
                null // La fecha se maneja en la DB al agregar o se recupera al actualizar
        );
    }

    // --- Métodos CRUD ---
    public void agregarPostulacion() {
        modeloPostulacion = cargarModeloPostulacion();
        if (modeloPostulacion == null) {
            return;
        }

        try {
            // Asumo un SP de agregar postulación que recibe los IDs, mensaje y monto
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_agregarpostulacion(?,?,?,?,?);");

            enunciado.setInt(1, modeloPostulacion.getIdProyecto());
            enunciado.setInt(2, modeloPostulacion.getIdFreelancer());

            enunciado.setString(3, modeloPostulacion.getMensaje()); // Usando mensaje
            enunciado.setDouble(4, modeloPostulacion.getMontoOfrecido()); // Usando double
            enunciado.setString(5, "pendiente");

            int registrosAgregados = enunciado.executeUpdate();
            if (registrosAgregados > 0) {
                System.out.println("Postulación agregada correctamente");
                cargarTablaPostulados();
                cambiarEstado(EstadoFormulario.NINGUNA);
                JOptionPane.showMessageDialog(null, "Postulación agregada con éxito.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar postulación: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void actualizarPostulacion() {
        modeloPostulacion = cargarModeloPostulacion();
        if (modeloPostulacion == null) {
            return;
        }

        try {
            // Asumo un SP de actualizar postulación (solo actualiza mensaje, monto y estado)
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_actualizarpostulacion(?,?,?,?);");
            enunciado.setInt(1, modeloPostulacion.getIdPostulacion());
            enunciado.setString(2, modeloPostulacion.getMensaje()); // Usando mensaje
            enunciado.setDouble(3, modeloPostulacion.getMontoOfrecido()); // Usando double
            enunciado.setString(4, modeloPostulacion.getEstado());

            enunciado.executeUpdate();
            cargarTablaPostulados();
            cambiarEstado(EstadoFormulario.NINGUNA);
            JOptionPane.showMessageDialog(null, "Postulación actualizada con éxito.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar postulación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarPostulacion() {
        Postulacion postulacionSeleccionada = tablaPostulados.getSelectionModel().getSelectedItem();
        if (postulacionSeleccionada == null) {
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar la postulación de ID: " + postulacionSeleccionada.getIdPostulacion() + "?", "Eliminar Postulación", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().
                        prepareCall("{call sp_eliminarpostulacion(?)}");
                enunciado.setInt(1, postulacionSeleccionada.getIdPostulacion());
                enunciado.execute();
                cargarTablaPostulados();
                JOptionPane.showMessageDialog(null, "Postulación eliminada con éxito.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar postulación: " + e.getMessage());
                e.printStackTrace();
            }
        }
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void limpiarTextField() {
        txtIDPostulacion.clear();
        txtMontoOfrecido.clear();
        txtMensaje.clear();
        cmbProyecto.getSelectionModel().clearSelection();
        cmbFreelancer.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().clearSelection();
    }

    // --- Lógica del Estado y Botones ---
    private void cambiarEstado(EstadoFormulario estado) {
        tipoDeAccion = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.ACTUALIZAR);

        txtMontoOfrecido.setDisable(!activo);
        txtMensaje.setDisable(!activo);
        cmbProyecto.setDisable(estado != EstadoFormulario.AGREGAR);
        cmbFreelancer.setDisable(estado != EstadoFormulario.AGREGAR);
        cmbEstado.setDisable(estado != EstadoFormulario.ACTUALIZAR);

        tablaPostulados.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnAgregar.setText(activo ? "💾 Guardar" : "➕ Agregar");
        btnEliminar.setText(activo ? "❌ Cancelar" : "🗑 Eliminar");

        boolean itemSelected = tablaPostulados.getSelectionModel().getSelectedItem() != null;
        btnActualizar.setDisable(activo || !itemSelected);

        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            if (itemSelected) {
                cargarPostulacionesEnComponentes();
            } else {
                limpiarTextField();
            }
        }
    }

    @FXML
    private void manejarBotonAgregarGuardar() {
        switch (tipoDeAccion) {
            case NINGUNA:
                System.out.println("Voy a crear un registro para Postulación");
                limpiarTextField();
                cambiarEstado(EstadoFormulario.AGREGAR);
                break;
            case AGREGAR:
                System.out.println("Voy a guardar los datos ingresados");
                agregarPostulacion();
                break;
            case ACTUALIZAR:
                System.out.println("Voy a guardar la edición indicada");
                actualizarPostulacion();
                break;
        }
    }

    @FXML
    private void manejarBotonActualizar() {
        if (tablaPostulados.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una postulación para editar.");
            return;
        }
        cambiarEstado(EstadoFormulario.ACTUALIZAR);
    }

    @FXML
    private void manejarBotonEliminarCancelar() {
        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            System.out.println("Voy a eliminar el registro");
            eliminarPostulacion();
        } else {
            cambiarEstado(EstadoFormulario.NINGUNA);
        }
    }

    @FXML
    private void buscarPorMonto() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        ArrayList<Postulacion> resultadoBusqueda = new ArrayList<>();

        if (textoBusqueda.isEmpty()) {
            cargarTablaPostulados();
            return;
        }

        for (Postulacion p : listaPostulaciones) {
            if (p.getMensaje().toLowerCase().contains(textoBusqueda)
                    || String.valueOf(p.getMontoOfrecido()).contains(textoBusqueda)) {
                resultadoBusqueda.add(p);
            }
        }

        tablaPostulados.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (!resultadoBusqueda.isEmpty()) {
            tablaPostulados.getSelectionModel().selectFirst();
            cargarPostulacionesEnComponentes();
        } else {
            limpiarTextField();
        }
    }

    @FXML
    public void escenaMenuPrincipal() {
        principal.mainMenuCliente();
    }
}
