/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 *
 * @author Diego
 */
public class MovimientoInventario {
    
    private int idMovimiento;
    private String numeroMovimiento;
    private String tipoMovimiento; // ENTRADA, SALIDA, AJUSTE
    private int idAlmacen;
    private String nombreAlmacen; // Para mostrar en vistas
    private LocalDateTime fechaMovimiento;
    private String motivo;
    private int totalProductos;
    private BigDecimal totalCantidad;
    private BigDecimal totalCosto;
    private String estado; // PENDIENTE, COMPLETADO, CANCELADO
    private int idUsuario;
    private String nombreUsuario; // Para mostrar en vistas
    private LocalDateTime fechaCreacion;
    
    // Constructores
    public MovimientoInventario() {
        this.totalCantidad = BigDecimal.ZERO;
        this.totalCosto = BigDecimal.ZERO;
        this.totalProductos = 0;
        this.estado = "PENDIENTE";
    }
    
    public MovimientoInventario(String tipoMovimiento, int idAlmacen) {
        this();
        this.tipoMovimiento = tipoMovimiento;
        this.idAlmacen = idAlmacen;
    }
    
    // Getters y Setters
    public int getIdMovimiento() {
        return idMovimiento;
    }
    
    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }
    
    public String getNumeroMovimiento() {
        return numeroMovimiento;
    }
    
    public void setNumeroMovimiento(String numeroMovimiento) {
        this.numeroMovimiento = numeroMovimiento;
    }
    
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }
    
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
    
    public int getIdAlmacen() {
        return idAlmacen;
    }
    
    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }
    
    public String getNombreAlmacen() {
        return nombreAlmacen;
    }
    
    public void setNombreAlmacen(String nombreAlmacen) {
        this.nombreAlmacen = nombreAlmacen;
    }
    
    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }
    
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
    
    public String getMotivo() {
        return motivo;
    }
    
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    public int getTotalProductos() {
        return totalProductos;
    }
    
    public void setTotalProductos(int totalProductos) {
        this.totalProductos = totalProductos;
    }
    
    public BigDecimal getTotalCantidad() {
        return totalCantidad;
    }
    
    public void setTotalCantidad(BigDecimal totalCantidad) {
        this.totalCantidad = totalCantidad;
    }
    
    public BigDecimal getTotalCosto() {
        return totalCosto;
    }
    
    public void setTotalCosto(BigDecimal totalCosto) {
        this.totalCosto = totalCosto;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    // Métodos de utilidad
    public String getTipoMovimientoDescripcion() {
        switch (tipoMovimiento) {
            case "ENTRADA": return "Entrada de Inventario";
            case "SALIDA": return "Salida de Inventario";
            case "AJUSTE": return "Ajuste de Inventario";
            default: return "Movimiento";
        }
    }
    
    public String getEstadoDescripcion() {
        switch (estado) {
            case "PENDIENTE": return "Pendiente";
            case "COMPLETADO": return "Completado";
            case "CANCELADO": return "Cancelado";
            default: return "Desconocido";
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", 
            numeroMovimiento != null ? numeroMovimiento : "SIN NÚMERO", 
            getTipoMovimientoDescripcion(), 
            getEstadoDescripcion());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MovimientoInventario that = (MovimientoInventario) obj;
        return idMovimiento == that.idMovimiento;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idMovimiento);
    }
}
