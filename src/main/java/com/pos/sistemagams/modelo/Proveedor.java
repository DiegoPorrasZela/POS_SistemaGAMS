/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.modelo;

import java.time.LocalDateTime;
/**
 *
 * @author Diego
 */
public class Proveedor {
    private int idProveedor;
    private String nombre;
    private String ruc;
    private String direccion;
    private String telefono;
    private String email;
    private boolean estado;
    private LocalDateTime fechaCreacion;
    
    public Proveedor() {}
    
    public Proveedor(int idProveedor, String nombre) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    @Override
    public String toString() {
        return nombre;
    }
}