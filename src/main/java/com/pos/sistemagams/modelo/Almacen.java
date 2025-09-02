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
public class Almacen {
    private int idAlmacen;
    private String nombre;
    private String descripcion;
    private String responsable;  // NUEVO CAMPO
    private String direccion;    // NUEVO CAMPO
    private boolean estado;
    private LocalDateTime fechaCreacion;
    
    public Almacen() {}
    
    public Almacen(int idAlmacen, String nombre) {
        this.idAlmacen = idAlmacen;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public int getIdAlmacen() { return idAlmacen; }
    public void setIdAlmacen(int idAlmacen) { this.idAlmacen = idAlmacen; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    @Override
    public String toString() {
        return nombre;
    }
}