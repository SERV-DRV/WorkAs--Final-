package org.workas.model;

import java.time.LocalDateTime;

public class Factura {

    private int idFactura;
    private int idCliente;
    private LocalDateTime fecha;
    private double total;
    private String estado; 

    public Factura(int idFactura, int idCliente, LocalDateTime fecha, double total, String estado) {
        this.idFactura = idFactura;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public Factura() {
    }
    
    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Factura{" + "idCliente=" + idCliente + ", estado=" + estado + '}';
    }
    
}
