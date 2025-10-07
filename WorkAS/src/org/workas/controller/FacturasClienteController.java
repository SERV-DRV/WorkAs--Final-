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
import org.workas.model.Factura;
import org.workas.system.Main;

public class FacturasClienteController implements Initializable {

    private ObservableList<Factura> listaFacturas;

    private Main principal;
    private Factura modeloFactura;

    private final String[] estadosFactura = {"pendiente", "pagado", "cancelado"};

    private enum EstadoFormulario {
        AGREGAR, ELIMINAR, ACTUALIZAR, NINGUNA
    };
    EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableView<Factura> tablaFacturas;
    @FXML
    private TableColumn colIDFactura, colIDCliente, colFecha, colTotal, colEstado;
    @FXML
    private TextField txtIDFactura, txtBuscar, txtIDCliente, txtTotal;

    @FXML
    private ComboBox<String> cmbEstado;

    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar, btnGuardar, btnCancelar;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTablaFacturas();

        cmbEstado.setItems(FXCollections.observableArrayList(estadosFactura));

        configurarColumnas();

        tablaFacturas.setOnMouseClicked(eventHandler -> cargarFacturaEnComponentes());

        colIDFactura.prefWidthProperty().bind(tablaFacturas.widthProperty().multiply(0.10));
        colIDCliente.prefWidthProperty().bind(tablaFacturas.widthProperty().multiply(0.10));
        colFecha.prefWidthProperty().bind(tablaFacturas.widthProperty().multiply(0.25));
        colTotal.prefWidthProperty().bind(tablaFacturas.widthProperty().multiply(0.25));
        colEstado.prefWidthProperty().bind(tablaFacturas.widthProperty().multiply(0.30));

        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        colIDFactura.setCellValueFactory(new PropertyValueFactory<Factura, Integer>("idFactura"));
        colIDCliente.setCellValueFactory(new PropertyValueFactory<Factura, Integer>("idCliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<Factura, LocalDateTime>("fecha"));
        colTotal.setCellValueFactory(new PropertyValueFactory<Factura, Double>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Factura, String>("estado"));
    }

    public void cargarTablaFacturas() {
        listaFacturas = FXCollections.observableArrayList(listarFacturas());
        tablaFacturas.setItems(listaFacturas);
        tablaFacturas.getSelectionModel().selectFirst();
        cargarFacturaEnComponentes();
    }

    public void cargarFacturaEnComponentes() {
        Factura facturaSeleccionada = tablaFacturas.getSelectionModel().getSelectedItem();
        if (facturaSeleccionada != null) {
            txtIDFactura.setText(String.valueOf(facturaSeleccionada.getIdFactura()));
            txtIDCliente.setText(String.valueOf(facturaSeleccionada.getIdCliente()));
            txtTotal.setText(String.valueOf(facturaSeleccionada.getTotal()));
            cmbEstado.setValue(facturaSeleccionada.getEstado());
        }
    }

    public ArrayList<Factura> listarFacturas() {
        ArrayList<Factura> facturas = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarFacturas()");
            ResultSet resultado = enunciado.executeQuery();

            while (resultado.next()) {
                Date sqlDate = resultado.getDate("fecha");
                LocalDateTime fechaFactura = null;
                if (sqlDate != null) {
                    fechaFactura = sqlDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
                }

                facturas.add(new Factura(
                        resultado.getInt("id_factura"),
                        resultado.getInt("id_cliente"),
                        fechaFactura,
                        resultado.getDouble("total"),
                        resultado.getString("estado")
                ));
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar Facturas: " + ex.getMessage());
            ex.printStackTrace();
        }
        return facturas;
    }

    private Factura cargarModeloFactura() {
        int idFactura = txtIDFactura.getText().isEmpty() ? 0 : Integer.parseInt(txtIDFactura.getText());

        if (txtIDCliente.getText().trim().isEmpty() || txtTotal.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Los campos ID Cliente y Total son obligatorios.");
            return null;
        }

        int idCliente;
        double total;
        try {
            idCliente = Integer.parseInt(txtIDCliente.getText());
            total = Double.parseDouble(txtTotal.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID Cliente y Total deben ser valores numéricos válidos.");
            return null;
        }

        return new Factura(
                idFactura,
                idCliente,
                null,
                total,
                cmbEstado.getValue()
        );
    }

    public void agregarFactura() {
        modeloFactura = cargarModeloFactura();
        if (modeloFactura == null) {
            return;
        }

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_agregarFactura(?,?,?);");

            enunciado.setInt(1, modeloFactura.getIdCliente());
            enunciado.setDouble(2, modeloFactura.getTotal());
            enunciado.setString(3, "pendiente");

            int registrosAgregados = enunciado.executeUpdate();
            if (registrosAgregados > 0) {
                cargarTablaFacturas();
                cambiarEstado(EstadoFormulario.NINGUNA);
                JOptionPane.showMessageDialog(null, "Factura agregada con éxito.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar factura: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void actualizarFactura() {
        modeloFactura = cargarModeloFactura();
        if (modeloFactura == null) {
            return;
        }

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_actualizarFactura(?,?,?);");
            enunciado.setInt(1, modeloFactura.getIdFactura());
            enunciado.setDouble(2, modeloFactura.getTotal());
            enunciado.setString(3, modeloFactura.getEstado());

            enunciado.executeUpdate();
            cargarTablaFacturas();
            cambiarEstado(EstadoFormulario.NINGUNA);
            JOptionPane.showMessageDialog(null, "Factura actualizada con éxito.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar factura: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarFactura() {
        Factura facturaSeleccionada = tablaFacturas.getSelectionModel().getSelectedItem();
        if (facturaSeleccionada == null) {
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar la factura de ID: " + facturaSeleccionada.getIdFactura() + "?", "Eliminar Factura", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().
                        prepareCall("{call sp_eliminarFactura(?)}");
                enunciado.setInt(1, facturaSeleccionada.getIdFactura());
                enunciado.execute();
                cargarTablaFacturas();
                JOptionPane.showMessageDialog(null, "Factura eliminada con éxito.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar factura: " + e.getMessage());
                e.printStackTrace();
            }
        }
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void limpiarTextField() {
        txtIDFactura.clear();
        txtIDCliente.clear();
        txtTotal.clear();
        cmbEstado.getSelectionModel().clearSelection();
    }

    private void cambiarEstado(EstadoFormulario estado) {
        tipoDeAccion = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.ACTUALIZAR);

        txtIDCliente.setDisable(!activo);
        txtTotal.setDisable(!activo);

        cmbEstado.setDisable(!activo);

        tablaFacturas.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnAgregar.setText(activo ? "💾 Guardar" : "➕ Agregar");
        btnEliminar.setText(activo ? "❌ Cancelar" : "🗑 Eliminar");

        boolean itemSelected = tablaFacturas.getSelectionModel().getSelectedItem() != null;
        btnActualizar.setDisable(activo || !itemSelected);

        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            if (itemSelected) {
                cargarFacturaEnComponentes();
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
                cmbEstado.getSelectionModel().select("pendiente");
                cambiarEstado(EstadoFormulario.AGREGAR);
                break;
            case AGREGAR:
                agregarFactura();
                break;
            case ACTUALIZAR:
                actualizarFactura();
                break;
        }
    }

    @FXML
    private void manejarBotonActualizar() {
        if (tablaFacturas.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una factura para editar.");
            return;
        }
        cambiarEstado(EstadoFormulario.ACTUALIZAR);
    }

    @FXML
    private void manejarBotonEliminarCancelar() {
        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            eliminarFactura();
        } else {
            cambiarEstado(EstadoFormulario.NINGUNA);
        }
    }

    @FXML
    private void buscarPorTotal() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        ArrayList<Factura> resultadoBusqueda = new ArrayList<>();

        if (textoBusqueda.isEmpty()) {
            cargarTablaFacturas();
            return;
        }

        for (Factura f : listaFacturas) {
            if (String.valueOf(f.getTotal()).contains(textoBusqueda)
                    || f.getEstado().toLowerCase().contains(textoBusqueda)) {
                resultadoBusqueda.add(f);
            }
        }

        tablaFacturas.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (!resultadoBusqueda.isEmpty()) {
            tablaFacturas.getSelectionModel().selectFirst();
            cargarFacturaEnComponentes();
        } else {
            limpiarTextField();
        }
    }

    @FXML
    public void escenaMenuPrincipal() {
        principal.mainMenuCliente();
    }
}
