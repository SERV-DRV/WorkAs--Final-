package org.workas.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Proyectos {

    private int idProyecto;
    private String titulo;
    private String descripcion;

    private Categoria categoria; 
    private Clientes cliente;   

    private Integer idFreelancer; 
    private BigDecimal presupuesto;
    private BigDecimal montoAcordado;
    private String estado;

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
    private Date fechaCreacion;
    private Date fechaEntrega;

    public Proyectos(int idProyecto, String titulo, String descripcion, Categoria categoria, Clientes cliente, Integer idFreelancer, BigDecimal presupuesto, BigDecimal montoAcordado, String estado, Date fechaCreacion, Date fechaEntrega) {
        this.idProyecto = idProyecto;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.cliente = cliente;     
        this.idFreelancer = idFreelancer;
        this.presupuesto = presupuesto;
        this.montoAcordado = montoAcordado;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
    }

    public Proyectos(Clientes cliente, Categoria categoria, String titulo, String descripcion, BigDecimal presupuesto, Date fechaEntrega) {
        this.cliente = cliente;
        this.categoria = categoria;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.presupuesto = presupuesto;
        this.fechaEntrega = fechaEntrega;

        this.idProyecto = 0;
        this.idFreelancer = null;
        this.montoAcordado = null;
        this.estado = "publicado";
        this.fechaCreacion = null;
    }

    public int getIdCategoria() {
        return (categoria != null) ? categoria.getIdCategoria() : 0;
    }

    public int getIdCliente() {
        return (cliente != null) ? cliente.getIdCliente() : 0;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Proyectos{" + "titulo=" + titulo + ", descripcion=" + descripcion + ", idFreelancer=" + idFreelancer + '}';
    }
    
    
}
