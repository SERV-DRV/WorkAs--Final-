package org.workas.model;

public class Freelancers {
    private int idFreelancer;
    private String nombre;
    private String apellido;
    private String email;
    private String contraseña;
    private String telefono;
    private String especialidad;
    private String portafolioUrl;
    private String fechaRegistro;

    public Freelancers() {
    }

    public Freelancers(int idFreelancer, String nombre, String apellido, String email, String contraseña, String telefono, String especialidad, String portafolioUrl, String fechaRegistro) {
        this.idFreelancer = idFreelancer;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contraseña = contraseña;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.portafolioUrl = portafolioUrl;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdFreelancer() {
        return idFreelancer;
    }

    public void setIdFreelancer(int idFreelancer) {
        this.idFreelancer = idFreelancer;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getPortafolioUrl() {
        return portafolioUrl;
    }

    public void setPortafolioUrl(String portafolioUrl) {
        this.portafolioUrl = portafolioUrl;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    @Override
    public String toString() {
        return nombre + " " + apellido + " (ID: " + idFreelancer + ")";
    }
}
