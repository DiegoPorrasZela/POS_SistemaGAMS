package com.pos.sistemagams.modelo;

import java.sql.Timestamp;

/**
 * Clase modelo para representar un Usuario del sistema
 */
public class Usuario {
    
    // Atributos
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rol; // ADMIN o VENDEDOR
    private boolean estado;
    private Timestamp fechaCreacion;
    
    // Constructores
    public Usuario() {
        this.estado = true;
    }
    
    public Usuario(String nombre, String apellido, String email, String password, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.estado = true;
    }
    
    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public boolean isEstado() {
        return estado;
    }
    
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    // Métodos adicionales
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    public boolean esAdmin() {
        return "ADMIN".equalsIgnoreCase(rol);
    }
    
    public boolean esVendedor() {
        return "VENDEDOR".equalsIgnoreCase(rol);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", estado=" + estado +
                '}';
    }
}