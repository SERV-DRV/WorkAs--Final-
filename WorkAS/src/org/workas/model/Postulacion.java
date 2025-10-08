package org.workas.model;

import java.time.LocalDateTime;

public class Postulacion {

    private int idPostulacion;
    private int idProyecto; 
    private int idFreelancer; 
    private String mensaje;
    private double montoOfrecido;
    private String estado;
    private LocalDateTime fechaPostulacion;


    public Postulacion(int idPostulacion, int idProyecto, int idFreelancer, String mensaje, double montoOfrecido, String estado, LocalDateTime fechaPostulacion) {
        this.idPostulacion = idPostulacion;
        this.idProyecto = idProyecto;
        this.idFreelancer = idFreelancer;
        this.mensaje = mensaje;
        this.montoOfrecido = montoOfrecido;
        this.estado = estado;
        this.fechaPostulacion = fechaPostulacion;
    }

    public Postulacion() {
    }

    public int getIdPostulacion() {
        return idPostulacion;
    }

    public void setIdPostulacion(int idPostulacion) {
        this.idPostulacion = idPostulacion;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public int getIdFreelancer() {
        return idFreelancer;
    }

    public void setIdFreelancer(int idFreelancer) {
        this.idFreelancer = idFreelancer;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public double getMontoOfrecido() {
        return montoOfrecido;
    }

    public void setMontoOfrecido(double montoOfrecido) {
        this.montoOfrecido = montoOfrecido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPostulacion() {
        return fechaPostulacion;
    }

    public void setFechaPostulacion(LocalDateTime fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    @Override
    public String toString() {
        return "Postulacion{" + "idProyecto=" + idProyecto + ", mensaje=" + mensaje + ", estado=" + estado + '}';
    }
    
    
}
