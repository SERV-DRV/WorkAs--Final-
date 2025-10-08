package org.workas.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.workas.db.Conexion;
import org.workas.model.Freelancers;
import org.workas.system.Main;

public class FreelancersFreeLanceController implements Initializable {

    private ObservableList<Freelancers> listaFreelancers = FXCollections.observableArrayList();
    private Main principal;
    private Freelancers modeloFreelancer;

    private enum EstadoFormulario {
        AGREGAR, ACTUALIZAR, NINGUNA
    };
    private EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableView<Freelancers> tableViewFreelancers;
    @FXML
    private TableColumn<Freelancers, Integer> colIdFreelancer;
    @FXML
    private TableColumn<Freelancers, String> colNombre, colApellido, colEmail, colTelefono, colEspecialidad, colPortafolio, colFechaRegistro;
    @FXML
    private TextField txtIdFreelancer, txtNombre, txtApellido, txtEmail, txtTelefono, txtEspecialidad, txtPortafolio, txtBuscar;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private DatePicker dpFechaRegistro;
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableViewFreelancers.setItems(listaFreelancers);
        configurarColumnas();
        cargarTablaFreelancers();
        tableViewFreelancers.setOnMouseClicked(event -> cargarFreelancersEnComponentes());
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        colIdFreelancer.setCellValueFactory(new PropertyValueFactory<>("idFreelancer"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colPortafolio.setCellValueFactory(new PropertyValueFactory<>("portafolioUrl"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
    }

    public void cargarTablaFreelancers() {
        listaFreelancers.setAll(listarFreelancers());
        tableViewFreelancers.getSelectionModel().selectFirst();
        cargarFreelancersEnComponentes();
    }

    public void cargarFreelancersEnComponentes() {
        Freelancers freelancerSeleccionado = tableViewFreelancers.getSelectionModel().getSelectedItem();
        if (freelancerSeleccionado != null) {
            txtIdFreelancer.setText(String.valueOf(freelancerSeleccionado.getIdFreelancer()));
            txtNombre.setText(freelancerSeleccionado.getNombre());
            txtApellido.setText(freelancerSeleccionado.getApellido());
            txtEmail.setText(freelancerSeleccionado.getEmail());
            txtTelefono.setText(freelancerSeleccionado.getTelefono());
            txtEspecialidad.setText(freelancerSeleccionado.getEspecialidad());
            txtPortafolio.setText(freelancerSeleccionado.getPortafolioUrl());
            txtContrasena.clear();

            String fechaStr = freelancerSeleccionado.getFechaRegistro();
            if (fechaStr != null && !fechaStr.isEmpty()) {
                try {
                    LocalDate fecha = LocalDate.parse(fechaStr.split(" ")[0]);
                    dpFechaRegistro.setValue(fecha);
                } catch (DateTimeParseException e) {
                    dpFechaRegistro.setValue(null);
                }
            } else {
                dpFechaRegistro.setValue(null);
            }
        }
    }

    public ArrayList<Freelancers> listarFreelancers() {
        ArrayList<Freelancers> freelancers = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarfreelancers()");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                freelancers.add(new Freelancers(
                        resultado.getInt("id_freelancer"),
                        resultado.getString("nombre"),
                        resultado.getString("apellido"),
                        resultado.getString("email"),
                        resultado.getString("contraseña"),
                        resultado.getString("telefono"),
                        resultado.getString("especialidad"),
                        resultado.getString("portafolio_url"),
                        resultado.getString("fecha_registro")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return freelancers;
    }

    private Freelancers cargarModeloFreelancer() {
        int idFreelancer = txtIdFreelancer.getText().isEmpty() ? 0 : Integer.parseInt(txtIdFreelancer.getText());

        if (txtNombre.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre y Email son obligatorios.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (tipoDeAccion == EstadoFormulario.AGREGAR && txtContrasena.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "La contraseña es obligatoria al crear un nuevo freelancer.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Freelancers(
            idFreelancer,
            txtNombre.getText(),
            txtApellido.getText(),
            txtEmail.getText(),
            txtContrasena.getText(),
            txtTelefono.getText(),
            txtEspecialidad.getText(),
            txtPortafolio.getText(),
            null
        );
    }

    public void agregarFreelancer() {
        modeloFreelancer = cargarModeloFreelancer();
        if (modeloFreelancer == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_agregarfreelancer(?,?,?,?,?,?,?);");
            enunciado.setString(1, modeloFreelancer.getNombre());
            enunciado.setString(2, modeloFreelancer.getApellido());
            enunciado.setString(3, modeloFreelancer.getEmail());
            enunciado.setString(4, modeloFreelancer.getContraseña());
            enunciado.setString(5, modeloFreelancer.getTelefono());
            enunciado.setString(6, modeloFreelancer.getEspecialidad());
            enunciado.setString(7, modeloFreelancer.getPortafolioUrl());
            if (enunciado.executeUpdate() > 0) {
                cargarTablaFreelancers();
                cambiarEstado(EstadoFormulario.NINGUNA);
                JOptionPane.showMessageDialog(null, "Freelancer agregado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar freelancer: " + ex.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarFreelancer() {
        modeloFreelancer = cargarModeloFreelancer();
        if (modeloFreelancer == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_actualizarfreelancer(?,?,?,?,?,?,?,?);");
            enunciado.setInt(1, modeloFreelancer.getIdFreelancer());
            enunciado.setString(2, modeloFreelancer.getNombre());
            enunciado.setString(3, modeloFreelancer.getApellido());
            enunciado.setString(4, modeloFreelancer.getEmail());
            enunciado.setString(5, modeloFreelancer.getContraseña());
            enunciado.setString(6, modeloFreelancer.getTelefono());
            enunciado.setString(7, modeloFreelancer.getEspecialidad());
            enunciado.setString(8, modeloFreelancer.getPortafolioUrl());
            enunciado.executeUpdate();
            cargarTablaFreelancers();
            cambiarEstado(EstadoFormulario.NINGUNA);
            JOptionPane.showMessageDialog(null, "Freelancer editado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al editar freelancer: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarFreelancer() {
        Freelancers freelancerSeleccionado = tableViewFreelancers.getSelectionModel().getSelectedItem();
        if (freelancerSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un freelancer para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de eliminar al freelancer: " + freelancerSeleccionado.getNombre() + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("{call sp_eliminarfreelancer(?)}");
                enunciado.setInt(1, freelancerSeleccionado.getIdFreelancer());
                enunciado.execute();
                cargarTablaFreelancers();
                limpiarTextField();
                JOptionPane.showMessageDialog(null, "Freelancer eliminado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar freelancer: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void limpiarTextField() {
        txtIdFreelancer.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        txtContrasena.clear();
        txtTelefono.clear();
        txtEspecialidad.clear();
        txtPortafolio.clear();
        dpFechaRegistro.setValue(null);
    }

    private void cambiarEstado(EstadoFormulario estado) {
        tipoDeAccion = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.ACTUALIZAR);

        txtNombre.setDisable(!activo);
        txtApellido.setDisable(!activo);
        txtEmail.setDisable(!activo);
        txtContrasena.setDisable(!activo);
        txtTelefono.setDisable(!activo);
        txtEspecialidad.setDisable(!activo);
        txtPortafolio.setDisable(!activo);
        dpFechaRegistro.setDisable(true);

        tableViewFreelancers.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnAgregar.setText(activo ? "💾 Guardar" : "➕ Agregar");
        btnEliminar.setText(activo ? "❌ Cancelar" : "🗑 Eliminar");

        boolean itemSelected = tableViewFreelancers.getSelectionModel().getSelectedItem() != null;
        btnActualizar.setDisable(activo || !itemSelected);

        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            if (itemSelected) {
                cargarFreelancersEnComponentes();
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
                agregarFreelancer();
                break;
            case ACTUALIZAR:
                actualizarFreelancer();
                break;
        }
    }

    @FXML
    private void manejarBotonActualizar() {
        if (tableViewFreelancers.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un freelancer para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cambiarEstado(EstadoFormulario.ACTUALIZAR);
    }

    @FXML
    private void manejarBotonEliminarCancelar() {
        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            eliminarFreelancer();
        } else {
            cambiarEstado(EstadoFormulario.NINGUNA);
        }
    }

    @FXML
    private void buscarFreelancer() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        if (textoBusqueda.isEmpty()) {
            tableViewFreelancers.setItems(listaFreelancers);
        } else {
            ObservableList<Freelancers> resultadoBusqueda = FXCollections.observableArrayList();
            for (Freelancers f : this.listaFreelancers) {
                if (f.getNombre().toLowerCase().contains(textoBusqueda) ||
                    f.getEmail().toLowerCase().contains(textoBusqueda) ||
                    f.getEspecialidad().toLowerCase().contains(textoBusqueda)) {
                    resultadoBusqueda.add(f);
                }
            }
            tableViewFreelancers.setItems(resultadoBusqueda);
        }
        if (!tableViewFreelancers.getItems().isEmpty()) {
            tableViewFreelancers.getSelectionModel().selectFirst();
        } else {
            limpiarTextField();
        }
    }

    @FXML
    public void clicManejoEvento(ActionEvent evento) {
        if (principal == null) {
            principal = Main.getInstancia();
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
        if (evento.getSource() == btnInicioF) {
            principal.mainMenuFreelancer();
        }
        if (evento.getSource() == btnCerrarSesionS) {
            principal.inicio();
        }
    }
}