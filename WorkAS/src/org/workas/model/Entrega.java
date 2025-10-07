/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.workas.model;

import java.time.LocalDateTime;

/**
 *
 * @author PC
 */
public class Entrega {

    private int idEntrega;
    private int idProyecto;
    private int idFreelancer; 
    private String archivoUrl;
    private String descripcion;
    private LocalDateTime fechaEntrega;

    public Entrega(int idEntrega, int idProyecto, int idFreelancer, String archivoUrl, String descripcion, LocalDateTime fechaEntrega) {
        this.idEntrega = idEntrega;
        this.idProyecto = idProyecto;
        this.idFreelancer = idFreelancer;
        this.archivoUrl = archivoUrl;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
    }

    public Entrega() {
    }
    
    public int getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(int idEntrega) {
        this.idEntrega = idEntrega;
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

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
}
