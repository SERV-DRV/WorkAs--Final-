package org.workas.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.workas.db.Conexion;
import org.workas.model.Categoria;
import org.workas.system.Main;

public class CategoriasFreeLanceController implements Initializable {

    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private Main principal;
    private Categoria modeloCategoria;

    private enum EstadoFormulario {
        AGREGAR, ACTUALIZAR, NINGUNA
    };
    private EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableView<Categoria> tableViewCategorias;
    @FXML
    private TableColumn<Categoria, Integer> colIdCategoria;
    @FXML
    private TableColumn<Categoria, String> colNombre, colDescripcion;
    @FXML
    private TextField txtIdCategoria, txtNombre, txtBuscar;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableViewCategorias.setItems(listaCategorias);
        configurarColumnas();
        cargarTablaCategorias();
        tableViewCategorias.setOnMouseClicked(event -> cargarCategoriasEnComponentes());
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        colIdCategoria.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    public void cargarTablaCategorias() {
        listaCategorias.setAll(listarCategorias());
        tableViewCategorias.getSelectionModel().selectFirst();
        cargarCategoriasEnComponentes();
    }

    public void cargarCategoriasEnComponentes() {
        Categoria categoriaSeleccionada = tableViewCategorias.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada != null) {
            txtIdCategoria.setText(String.valueOf(categoriaSeleccionada.getIdCategoria()));
            txtNombre.setText(categoriaSeleccionada.getNombre());
            txtDescripcion.setText(categoriaSeleccionada.getDescripcion());
        }
    }

    public ArrayList<Categoria> listarCategorias() {
        ArrayList<Categoria> categorias = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarcategorias()");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                categorias.add(new Categoria(
                        resultado.getInt("id_categoria"),
                        resultado.getString("nombre"),
                        resultado.getString("descripcion")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return categorias;
    }

    private Categoria cargarModeloCategoria() {
        int idCategoria = txtIdCategoria.getText().isEmpty() ? 0 : Integer.parseInt(txtIdCategoria.getText());
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El nombre de la categoría es obligatorio.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new Categoria(
            idCategoria,
            txtNombre.getText(),
            txtDescripcion.getText()
        );
    }

    public void agregarCategoria() {
        modeloCategoria = cargarModeloCategoria();
        if (modeloCategoria == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_agregarcategoria(?,?);");
            enunciado.setString(1, modeloCategoria.getNombre());
            enunciado.setString(2, modeloCategoria.getDescripcion());
            if (enunciado.executeUpdate() > 0) {
                cargarTablaCategorias();
                cambiarEstado(EstadoFormulario.NINGUNA);
                JOptionPane.showMessageDialog(null, "Categoría agregada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar categoría: " + ex.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarCategoria() {
        modeloCategoria = cargarModeloCategoria();
        if (modeloCategoria == null) return;
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_actualizarcategoria(?,?,?);");
            enunciado.setInt(1, modeloCategoria.getIdCategoria());
            enunciado.setString(2, modeloCategoria.getNombre());
            enunciado.setString(3, modeloCategoria.getDescripcion());
            enunciado.executeUpdate();
            cargarTablaCategorias();
            cambiarEstado(EstadoFormulario.NINGUNA);
            JOptionPane.showMessageDialog(null, "Categoría editada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al editar categoría: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarCategoria() {
        Categoria categoriaSeleccionada = tableViewCategorias.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una categoría para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de eliminar la categoría: " + categoriaSeleccionada.getNombre() + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("{call sp_eliminarcategoria(?)}");
                enunciado.setInt(1, categoriaSeleccionada.getIdCategoria());
                enunciado.execute();
                cargarTablaCategorias();
                limpiarTextField();
                JOptionPane.showMessageDialog(null, "Categoría eliminada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar categoría: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void limpiarTextField() {
        txtIdCategoria.clear();
        txtNombre.clear();
        txtDescripcion.clear();
    }

    private void cambiarEstado(EstadoFormulario estado) {
        tipoDeAccion = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.ACTUALIZAR);

        txtNombre.setDisable(!activo);
        txtDescripcion.setDisable(!activo);
        tableViewCategorias.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnAgregar.setText(activo ? "💾 Guardar" : "➕ Agregar");
        btnEliminar.setText(activo ? "❌ Cancelar" : "🗑 Eliminar");

        boolean itemSelected = tableViewCategorias.getSelectionModel().getSelectedItem() != null;
        btnActualizar.setDisable(activo || !itemSelected);

        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            if (itemSelected) {
                cargarCategoriasEnComponentes();
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
                agregarCategoria();
                break;
            case ACTUALIZAR:
                actualizarCategoria();
                break;
        }
    }

    @FXML
    private void manejarBotonActualizar() {
        if (tableViewCategorias.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una categoría para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cambiarEstado(EstadoFormulario.ACTUALIZAR);
    }

    @FXML
    private void manejarBotonEliminarCancelar() {
        if (tipoDeAccion == EstadoFormulario.NINGUNA) {
            eliminarCategoria();
        } else {
            cambiarEstado(EstadoFormulario.NINGUNA);
        }
    }

    @FXML
    private void buscarCategoria() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        if (textoBusqueda.isEmpty()) {
            tableViewCategorias.setItems(listaCategorias);
        } else {
            ObservableList<Categoria> resultadoBusqueda = FXCollections.observableArrayList();
            for (Categoria c : this.listaCategorias) {
                if (c.getNombre().toLowerCase().contains(textoBusqueda)) {
                    resultadoBusqueda.add(c);
                }
            }
            tableViewCategorias.setItems(resultadoBusqueda);
        }
        if (!tableViewCategorias.getItems().isEmpty()) {
            tableViewCategorias.getSelectionModel().selectFirst();
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