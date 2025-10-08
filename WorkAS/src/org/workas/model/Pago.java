package org.workas.model;

import java.time.LocalDateTime;

public class Pago {

    private int idPago;
    private int idProyecto; 
    private int idFreelancer; 
    private double monto;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String estado;

    public Pago(int idPago, int idProyecto, int idFreelancer, double monto, LocalDateTime fechaPago, String metodoPago, String estado) {
        this.idPago = idPago;
        this.idProyecto = idProyecto;
        this.idFreelancer = idFreelancer;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.estado = estado;
    }

    public Pago() {
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
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

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Pago{" + "idProyecto=" + idProyecto + ", idFreelancer=" + idFreelancer + ", estado=" + estado + '}';
    }
    
    
}
