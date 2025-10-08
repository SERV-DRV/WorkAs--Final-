package org.workas.model;

public class DetalleFactura {

    private int idDetalle;
    private int idFactura;
    private String descripcion;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleFactura(int idDetalle, int idFactura, String descripcion, int cantidad, double precioUnitario, double subtotal) {
        this.idDetalle = idDetalle;
        this.idFactura = idFactura;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public DetalleFactura() {
    }
    
    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "DetalleFactura{" + "idFactura=" + idFactura + ", descripcion=" + descripcion + '}';
    }
    
}
