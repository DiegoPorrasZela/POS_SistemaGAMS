/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * Modelo para representar un movimiento de caja
 */
package com.pos.sistemagams.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;
/**
 *
 * @author Diego
 */
/**
 * Clase modelo para representar un Movimiento de Caja (entrada/salida)
 */
public class MovimientoCaja {
    
    private int idMovimientoCaja;
    private String numeroMovimiento;
    private int idSesion;
    private String tipoMovimiento; // ENTRADA, SALIDA
    private String concepto;
    private BigDecimal monto;
    private String observaciones;
    private int idUsuario;
    private Timestamp fechaMovimiento;
    
    // Campos adicionales para mostrar
    private String usuarioNombre;
    
    // Constructor vacío
    public MovimientoCaja() {
        this.monto = BigDecimal.ZERO;
    }
    
    // Constructor con parámetros básicos
    public MovimientoCaja(int idSesion, String tipoMovimiento, String concepto, BigDecimal monto, int idUsuario) {
        this();
        this.idSesion = idSesion;
        this.tipoMovimiento = tipoMovimiento;
        this.concepto = concepto;
        this.monto = monto;
        this.idUsuario = idUsuario;
    }
    
    // Getters y Setters
    public int getIdMovimientoCaja() {
        return idMovimientoCaja;
    }
    
    public void setIdMovimientoCaja(int idMovimientoCaja) {
        this.idMovimientoCaja = idMovimientoCaja;
    }
    
    public String getNumeroMovimiento() {
        return numeroMovimiento;
    }
    
    public void setNumeroMovimiento(String numeroMovimiento) {
        this.numeroMovimiento = numeroMovimiento;
    }
    
    public int getIdSesion() {
        return idSesion;
    }
    
    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }
    
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }
    
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
    
    public String getConcepto() {
        return concepto;
    }
    
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    
    public BigDecimal getMonto() {
        return monto;
    }
    
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public Timestamp getFechaMovimiento() {
        return fechaMovimiento;
    }
    
    public void setFechaMovimiento(Timestamp fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
    
    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
    
    // Métodos de utilidad
    public boolean esEntrada() {
        return "ENTRADA".equals(tipoMovimiento);
    }
    
    public boolean esSalida() {
        return "SALIDA".equals(tipoMovimiento);
    }
    
    @Override
    public String toString() {
        return tipoMovimiento + " - " + concepto + ": $" + monto;
    }
}