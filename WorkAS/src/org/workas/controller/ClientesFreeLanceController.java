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
import org.workas.model.Clientes;
import org.workas.system.Main;

public class ClientesFreeLanceController implements Initializable {

    private ObservableList<Clientes> listaClientes = FXCollections.observableArrayList();
    private Main principal;
    private Clientes modeloCliente;

    private enum EstadoFormulario {
        AGREGAR, ACTUALIZAR, NINGUNA
    };
    private EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableView<Clientes> tableViewClientes;
    @FXML
    private TableColumn<Clientes, Integer> colIdCliente;
    @FXML
    private TableColumn<Clientes, String> colNombre, colApellido, colEmail, colTelefono, colFechaRegistro;
    @FXML
    private TextField txtIdCliente, txtNombre, txtApellido, txtEmail, txtTelefono, txtBuscar;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private DatePicker dpFechaRegistro;
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public Main getPrincipal() {
        return principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableViewClientes.setItems(listaClientes);
        configurarColumnas();
        cargarTablaClientes();
        tableViewClientes.setOnMouseClicked(event -> cargarClientesEnComponentes());
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
    }

    public void cargarTablaClientes() {
        listaClientes.setAll(listarClientes());
        tableViewClientes.getSelectionModel().selectFirst();
        cargarClientesEnComponentes();
    }

    public void cargarClientesEnComponentes() {
        Clientes clienteSeleccionado = tableViewClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            txtIdCliente.setText(String.valueOf(clienteSeleccionado.getIdCliente()));
            txtNombre.setText(clienteSeleccionado.getNombre());
            txtApellido.setText(clienteSeleccionado.getApellido());
            txtEmail.setText(clienteSeleccionado.getEmail());
            txtTelefono.setText(clienteSeleccionado.getTelefono());
            txtContrasena.clear();

            String fechaStr = clienteSeleccionado.getFechaRegistro();
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

    public ArrayList<Clientes> listarClientes() {
        ArrayList<Clientes> clientes = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarclientes()");
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
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return clientes;
    }

    private Clientes cargarModeloCliente() {
        int idCliente = txtIdCliente.getText().isEmpty() ? 0 : Integer.parseInt(txtIdCliente.getText());

        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre, Apellido y Email son obligatorios.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (tipoDeAccion == EstadoFormulario.AGREGAR && txtContrasena.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "La contraseña es obligatoria al crear un nuevo cliente.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Clientes(
            idCliente,
            txtNombre.getText(),
            txtApellido.getText(),
            txtEmail.getText(),
            txtContrasena.getText(),
            txtTelefono.getText(),
            null
        );
    }

    public void agregarCliente() {
        modeloCliente = cargarModeloCliente();
        if (modeloCliente == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_agregarcliente(?,?,?,?,?);");
            enunciado.setString(1, modeloCliente.getNombre());
            enunciado.setString(2, modeloCliente.getApellido());
            enunciado.setString(3, modeloCliente.getEmail());
            enunciado.setString(4, modeloCliente.getContraseña());
            enunciado.setString(5, modeloCliente.getTelefono());
            if (enunciado.executeUpdate() > 0) {
                cargarTablaClientes();
                cambiarEstado(EstadoFormulario.NINGUNA);
                JOptionPane.showMessageDialog(null, "Cliente agregado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar cliente: " + ex.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarCliente() {
        modeloCliente = cargarModeloCliente();
        if (modeloCliente == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_actualizarcliente(?,?,?,?,?,?);");
            enunciado.setInt(1, modeloCliente.getIdCliente());
            enunciado.setString(2, modeloCliente.getNombre());
            enunciado.setString(3, modeloCliente.getApellido());
            enunciado.setString(4, modeloCliente.getEmail());
            enunciado.setString(5, modeloCliente.getContraseña());
            enunciado.setString(6, modeloCliente.getTelefono());
            enunciado.executeUpdate();
            cargarTablaClientes();
            cambiarEstado(EstadoFormulario.NINGUNA);
            JOptionPane.showMessageDialog(null, "Cliente editado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al editado cliente: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarCliente() {
        Clientes clienteSeleccionado = tableViewClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un cliente para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(null, 
                "¿Está seguro de eliminar al cliente: " + clienteSeleccionado.getNombre() + "?", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("{call sp_eliminarcliente(?)}");
                enunciado.setInt(1, clienteSeleccionado.getIdCliente());
                enunciado.execute();
                cargarTablaClientes();
                limpiarTextField();
                JOptionPane.showMessageDialog(null, "Cliente eliminado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar cliente: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void limpiarTextField() {
        txtIdCliente.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        txtContrasena.clear();
        txtTelefono.clear();
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
        dpFechaRegistro.setDisable(true);

        tableViewClientes.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnAgregar.setText(activo ? "💾 Guardar" : "➕ Agregar");
        btnEliminar.setText(activo ? "❌ Cancelar" : "🗑 Eliminar");

        boolean itemSelected = tableViewClientes.getSelectionModel().getSelectedItem() != null;
        btnActualizar.setDisable(activo || !itemSelected);

        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            if (itemSelected) {
                cargarClientesEnComponentes();
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
                agregarCliente();
                break;
            case ACTUALIZAR:
                actualizarCliente();
                break;
        }
    }

    @FXML
    private void manejarBotonActualizar() {
        if (tableViewClientes.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un cliente para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cambiarEstado(EstadoFormulario.ACTUALIZAR);
    }

    @FXML
    private void manejarBotonEliminarCancelar() {
        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            eliminarCliente();
        } else {
            cambiarEstado(EstadoFormulario.NINGUNA);
        }
    }

    @FXML
    private void buscarCliente() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        if (textoBusqueda.isEmpty()) {
            tableViewClientes.setItems(listaClientes);
        } else {
            ObservableList<Clientes> resultadoBusqueda = FXCollections.observableArrayList();
            for (Clientes c : this.listaClientes) {
                if (c.getNombre().toLowerCase().contains(textoBusqueda) ||
                    c.getApellido().toLowerCase().contains(textoBusqueda) ||
                    c.getEmail().toLowerCase().contains(textoBusqueda)) {
                    resultadoBusqueda.add(c);
                }
            }
            tableViewClientes.setItems(resultadoBusqueda);
        }
        if (!tableViewClientes.getItems().isEmpty()) {
            tableViewClientes.getSelectionModel().selectFirst();
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