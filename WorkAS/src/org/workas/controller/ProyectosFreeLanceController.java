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

public class ProyectosFreeLanceController implements Initializable{
    
    private ObservableList<Proyectos> listaProyectos;
    private ObservableList<Categoria> listaCategorias;
    private ObservableList<Clientes> listaClientes;

    private Main principal;
    private Proyectos modeloProyecto;
    
    private final String[] estadosProyecto = {"publicado", "en curso", "finalizado", "cancelado"};

    private enum EstadoFormulario {
        AGREGAR, ELIMINAR, ACTUALIZAR, NINGUNA
    };
    EstadoFormulario tipoDeAccion = EstadoFormulario.NINGUNA;

    @FXML
    private TableView<Proyectos> tablaProyectos;
    @FXML
    private TableColumn colIDProyecto, colTitulo, colDescripcion, colCategoria, colPresupuesto, colFechaEntrega, colEstado;
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
    private Button btnAgregar, btnActualizar, btnEliminar, btnGuardar, btnCancelar; 

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public Main getPrincipal() {
        return principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cargarClientes();
        cargarCategorias();
        
        cargarTablaProyectos();
        
        cmbEstado.setItems(FXCollections.observableArrayList(estadosProyecto));
        
        configurarColumnas();
        
        tablaProyectos.setOnMouseClicked(eventHandler -> cargarProyectosEnComponentes());
        
        colIDProyecto.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.05));
        colTitulo.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.18));
        colDescripcion.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.25));
        colCategoria.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.12));
        colPresupuesto.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.10));
        colFechaEntrega.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.15));
        colEstado.prefWidthProperty().bind(tablaProyectos.widthProperty().multiply(0.10));
        
        cambiarEstado(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        colIDProyecto.setCellValueFactory(new PropertyValueFactory<Proyectos, Integer>("idProyecto"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<Proyectos, String>("titulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Proyectos, String>("descripcion"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<Proyectos, Categoria>("categoria")); 
        colPresupuesto.setCellValueFactory(new PropertyValueFactory<Proyectos, BigDecimal>("presupuesto"));
        colFechaEntrega.setCellValueFactory(new PropertyValueFactory<Proyectos, Date>("fechaEntrega"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Proyectos, String>("estado"));
    }
    
    
    private ArrayList<Clientes> cargarModeloClientes(){
        ArrayList<Clientes> clientes = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_listarclientes();");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                Clientes c = new Clientes(
                         resultado.getInt("id_cliente"),
                         resultado.getString("nombre"),
                         resultado.getString("apellido"),
                         resultado.getString("email"),
                         resultado.getString("contraseña"),
                         resultado.getString("telefono"),
                         resultado.getString("fecha_registro")
                );
                clientes.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar clientes");
            e.printStackTrace();
        }
        return clientes;
    }
    
    private void cargarClientes() {
        listaClientes = FXCollections.observableArrayList(cargarModeloClientes());
        cmbCliente.setItems(listaClientes);
    }
    
    private ArrayList<Categoria> cargarModeloCategorias(){
        ArrayList<Categoria> categorias = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_listarcategorias();"); 
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                Categoria cat = new Categoria(
                         resultado.getInt("id_categoria"),
                         resultado.getString("nombre"),
                         resultado.getString("descripcion")
                );
                categorias.add(cat);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar categorías");
            e.printStackTrace();
        }
        return categorias;
    }
    
    private void cargarCategorias() {
        listaCategorias = FXCollections.observableArrayList(cargarModeloCategorias());
        cmbCategoria.setItems(listaCategorias);
    }


    public void cargarTablaProyectos() {
        listaProyectos = FXCollections.observableArrayList(listarProyectos());
        tablaProyectos.setItems(listaProyectos);
        tablaProyectos.getSelectionModel().selectFirst();
        cargarProyectosEnComponentes();
    }

    public void cargarProyectosEnComponentes() {
        Proyectos proyectoSeleccionado = tablaProyectos.getSelectionModel().getSelectedItem();
        if (proyectoSeleccionado != null) {
            txtIDProyecto.setText(String.valueOf(proyectoSeleccionado.getIdProyecto()));
            txtTitulo.setText(proyectoSeleccionado.getTitulo());
            txtDescripcion.setText(proyectoSeleccionado.getDescripcion());
            txtPresupuesto.setText(String.valueOf(proyectoSeleccionado.getPresupuesto()));
            txtMontoAcordado.setText(proyectoSeleccionado.getMontoAcordado() != null ? String.valueOf(proyectoSeleccionado.getMontoAcordado()) : "");
            
            if (proyectoSeleccionado.getFechaEntrega() != null) {
                dpFechaEntrega.setValue(proyectoSeleccionado.getFechaEntrega().toLocalDate());
            } else {
                 dpFechaEntrega.setValue(null);
            }
            
            cmbEstado.setValue(proyectoSeleccionado.getEstado());

            for (Clientes c : cmbCliente.getItems()) {
                if (c.getIdCliente() == proyectoSeleccionado.getIdCliente()) {
                    cmbCliente.setValue(c);
                    break;
                }
            }
            
            for (Categoria cat : cmbCategoria.getItems()) {
                if (cat.getIdCategoria() == proyectoSeleccionado.getIdCategoria()) {
                    cmbCategoria.setValue(cat);
                    break;
                }
            }
        }
    }

    public ArrayList<Proyectos> listarProyectos() {
        ArrayList<Proyectos> proyectos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement enunciado = conexion.prepareCall("call sp_listarproyectos()");
            ResultSet resultado = enunciado.executeQuery();
            
            while (resultado.next()) {
                Clientes cli = new Clientes(resultado.getInt("id_cliente"), null, null, null, null, null, null); 
                Categoria cat = new Categoria(resultado.getInt("id_categoria"), "", ""); 
                
                proyectos.add(new Proyectos(
                         resultado.getInt("id_proyecto"),
                         resultado.getString("titulo"),
                         resultado.getString("descripcion"),
                         cat, 
                         cli, 
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
    
    private Proyectos cargarModeloProyecto() {
        int idProyecto = txtIDProyecto.getText().isEmpty() ? 0 : Integer.parseInt(txtIDProyecto.getText());
        
        Clientes clienteSeleccionado = cmbCliente.getSelectionModel().getSelectedItem();
        Categoria categoriaSeleccionada = cmbCategoria.getSelectionModel().getSelectedItem();

        if (clienteSeleccionado == null || categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un Cliente y una Categoría.");
            return null;
        }
        
        if (txtTitulo.getText().trim().isEmpty() || dpFechaEntrega.getValue() == null) {
             JOptionPane.showMessageDialog(null, "Título y Fecha de Entrega son obligatorios.");
            return null;
        }
        
        BigDecimal presupuesto;
        try {
            presupuesto = new BigDecimal(txtPresupuesto.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Presupuesto debe ser un valor numérico válido.");
            return null;
        }
        
        return new Proyectos(
            idProyecto, 
            txtTitulo.getText(), 
            txtDescripcion.getText(), 
            categoriaSeleccionada, 
            clienteSeleccionado,  
            null,
            presupuesto,
            null, 
            cmbEstado.getValue(), 
            null,
            Date.valueOf(dpFechaEntrega.getValue())
        );
    }
    

    public void agregarProyecto() {
        modeloProyecto = cargarModeloProyecto();
        if (modeloProyecto == null) return;
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_agregarproyecto(?,?,?,?,?,?,?);");
            
            enunciado.setInt(1, modeloProyecto.getIdCliente()); 
            enunciado.setInt(2, modeloProyecto.getIdCategoria()); 
            
            enunciado.setString(3, modeloProyecto.getTitulo());
            enunciado.setString(4, modeloProyecto.getDescripcion());
            enunciado.setBigDecimal(5, modeloProyecto.getPresupuesto());
            enunciado.setDate(6, modeloProyecto.getFechaEntrega());
            enunciado.setString(7, "publicado"); 
            
            int registrosAgregados = enunciado.executeUpdate();
            if (registrosAgregados > 0) {
                System.out.println("Proyecto agregado correctamente");
                cargarTablaProyectos();
                cambiarEstado(EstadoFormulario.NINGUNA);
                JOptionPane.showMessageDialog(null, "Proyecto agregado con éxito.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar proyecto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void actualizarProyecto() {
        modeloProyecto = cargarModeloProyecto();
        if (modeloProyecto == null) return;
        
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_actualizarproyecto(?,?,?,?,?,?);"); 
            enunciado.setInt(1, modeloProyecto.getIdProyecto());
            enunciado.setString(2, modeloProyecto.getTitulo());
            enunciado.setString(3, modeloProyecto.getDescripcion());
            enunciado.setBigDecimal(4, modeloProyecto.getPresupuesto());
            enunciado.setDate(5, Date.valueOf(modeloProyecto.getFechaEntrega().toLocalDate()));
            enunciado.setString(6, modeloProyecto.getEstado());
            
            enunciado.executeUpdate();
            cargarTablaProyectos();
            cambiarEstado(EstadoFormulario.NINGUNA);
            JOptionPane.showMessageDialog(null, "Proyecto actualizado con éxito.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar proyecto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarProyecto() {
        Proyectos proyectoSeleccionado = tablaProyectos.getSelectionModel().getSelectedItem();
        if (proyectoSeleccionado == null) return;

        int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el proyecto: " + proyectoSeleccionado.getTitulo() + "?", "Eliminar Proyecto", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().
                        prepareCall("{call sp_eliminarproyecto(?)}");
                enunciado.setInt(1, proyectoSeleccionado.getIdProyecto());
                enunciado.execute();
                cargarTablaProyectos();
                JOptionPane.showMessageDialog(null, "Proyecto eliminado con éxito.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar proyecto: " + e.getMessage());
                e.printStackTrace();
            }
        }
        cambiarEstado(EstadoFormulario.NINGUNA);
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
                System.out.println("Voy a crear un registro para Proyecto");
                limpiarTextField();
                cambiarEstado(EstadoFormulario.AGREGAR);
                break;
            case AGREGAR:
                System.out.println("Voy a guardar los datos ingresados");
                agregarProyecto();
                break;
            case ACTUALIZAR:
                System.out.println("Voy a guardar la edicion indicada");
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
            System.out.println("Voy a eliminar el registro");
            eliminarProyecto();
        } else {
            cambiarEstado(EstadoFormulario.NINGUNA);
        }
    }

    @FXML
    private void buscarPorTitulo() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        ArrayList<Proyectos> resultadoBusqueda = new ArrayList<>();

        if (textoBusqueda.isEmpty()) {
            cargarTablaProyectos();
            return;
        }
        
        for (Proyectos p : listaProyectos) {
            if (p.getTitulo().toLowerCase().contains(textoBusqueda)) {
                resultadoBusqueda.add(p);
            }
        }

        tablaProyectos.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (!resultadoBusqueda.isEmpty()) {
            tablaProyectos.getSelectionModel().selectFirst();
            cargarProyectosEnComponentes();
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
