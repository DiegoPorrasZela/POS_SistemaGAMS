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
public class Departamento {
    private int idDepartamento;
    private String nombre;
    private String descripcion;
    private int idAlmacen;           // NUEVO CAMPO - FK hacia almacenes
    private String nombreAlmacen;   // NUEVO CAMPO - Para mostrar el nombre del almacén
    private boolean estado;
    private LocalDateTime fechaCreacion;
    
    public Departamento() {}
    
    public Departamento(int idDepartamento, String nombre) {
        this.idDepartamento = idDepartamento;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public int getIdDepartamento() { return idDepartamento; }
    public void setIdDepartamento(int idDepartamento) { this.idDepartamento = idDepartamento; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public int getIdAlmacen() { return idAlmacen; }
    public void setIdAlmacen(int idAlmacen) { this.idAlmacen = idAlmacen; }
    
    public String getNombreAlmacen() { return nombreAlmacen; }
    public void setNombreAlmacen(String nombreAlmacen) { this.nombreAlmacen = nombreAlmacen; }
    
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    @Override
    public String toString() {
        return nombre;
    }
}
