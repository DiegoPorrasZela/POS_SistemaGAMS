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
public class Categoria {
    private int idCategoria;
    private String nombre;
    private String descripcion;
    private boolean estado;
    private LocalDateTime fechaCreacion;
    
    public Categoria() {}
    
    public Categoria(int idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    @Override
    public String toString() {
        return nombre;
    }
}
