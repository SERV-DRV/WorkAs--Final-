package org.workas.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Proyectos {

    private int idProyecto;
    private String titulo;
    private String descripcion;
    private int idCategoria;
    private int idCliente;
    private Integer idFreelancer;
    private BigDecimal presupuesto;
    private BigDecimal montoAcordado;
    private String estado;
    private Date fechaCreacion;
    private Date fechaEntrega;

    public Proyectos(int idProyecto, String titulo, String descripcion, int idCategoria, int idCliente, Integer idFreelancer, BigDecimal presupuesto, BigDecimal montoAcordado, String estado, Date fechaCreacion, Date fechaEntrega) {
        this.idProyecto = idProyecto;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.idCliente = idCliente;
        this.idFreelancer = idFreelancer;
        this.presupuesto = presupuesto;
        this.montoAcordado = montoAcordado;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
    }

    public Proyectos(int idCliente, int idCategoria, String titulo, String descripcion, BigDecimal presupuesto, Date fechaEntrega) {
        this.idCliente = idCliente;
        this.idCategoria = idCategoria;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.presupuesto = presupuesto;
        this.fechaEntrega = fechaEntrega;
        // Campos que se inician por la BD o son nulos al crear:
        this.idProyecto = 0;
        this.idFreelancer = null;
        this.montoAcordado = null;
        this.estado = "publicado";
        this.fechaCreacion = null;
    }

    public Proyectos(int idProyecto, String titulo, String descripcion, BigDecimal presupuesto, Date fechaEntrega, String estado) {
        this.idProyecto = idProyecto;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.presupuesto = presupuesto;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdFreelancer() {
        return idFreelancer;
    }

    public void setIdFreelancer(Integer idFreelancer) {
        this.idFreelancer = idFreelancer;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }

    public BigDecimal getMontoAcordado() {
        return montoAcordado;
    }

    public void setMontoAcordado(BigDecimal montoAcordado) {
        this.montoAcordado = montoAcordado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
}
