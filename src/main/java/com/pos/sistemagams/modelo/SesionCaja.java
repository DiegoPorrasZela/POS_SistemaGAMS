/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Modelo para representar una sesión de caja
 */
package com.pos.sistemagams.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;
/**
 *
 * @author Diego
 */
/**
 * Clase modelo para representar una Sesión de Caja
 */
public class SesionCaja {
    
    private int idSesion;
    private String numeroSesion;
    private int idCaja;
    private int idUsuario;
    private Timestamp fechaApertura;
    private Timestamp fechaCierre;
    private BigDecimal montoApertura;
    private BigDecimal montoCierreSistema;
    private BigDecimal montoCierreReal;
    private BigDecimal diferencia;
    private BigDecimal totalVentas;
    private BigDecimal totalEntradas;
    private BigDecimal totalSalidas;
    private String estado;
    private String observacionesApertura;
    private String observacionesCierre;
    
    // Campos adicionales para mostrar
    private String cajaNombre;
    private String usuarioNombre;
    
    // Constructor vacío
    public SesionCaja() {
        this.montoApertura = BigDecimal.ZERO;
        this.montoCierreSistema = BigDecimal.ZERO;
        this.montoCierreReal = BigDecimal.ZERO;
        this.diferencia = BigDecimal.ZERO;
        this.totalVentas = BigDecimal.ZERO;
        this.totalEntradas = BigDecimal.ZERO;
        this.totalSalidas = BigDecimal.ZERO;
        this.estado = "ABIERTA";
    }
    
    // Constructor con parámetros básicos
    public SesionCaja(int idCaja, int idUsuario, BigDecimal montoApertura) {
        this();
        this.idCaja = idCaja;
        this.idUsuario = idUsuario;
        this.montoApertura = montoApertura;
    }
    
    // Getters y Setters
    public int getIdSesion() {
        return idSesion;
    }
    
    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }
    
    public String getNumeroSesion() {
        return numeroSesion;
    }
    
    public void setNumeroSesion(String numeroSesion) {
        this.numeroSesion = numeroSesion;
    }
    
    public int getIdCaja() {
        return idCaja;
    }
    
    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public Timestamp getFechaApertura() {
        return fechaApertura;
    }
    
    public void setFechaApertura(Timestamp fechaApertura) {
        this.fechaApertura = fechaApertura;
    }
    
    public Timestamp getFechaCierre() {
        return fechaCierre;
    }
    
    public void setFechaCierre(Timestamp fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
    
    public BigDecimal getMontoApertura() {
        return montoApertura;
    }
    
    public void setMontoApertura(BigDecimal montoApertura) {
        this.montoApertura = montoApertura;
    }
    
    public BigDecimal getMontoCierreSistema() {
        return montoCierreSistema;
    }
    
    public void setMontoCierreSistema(BigDecimal montoCierreSistema) {
        this.montoCierreSistema = montoCierreSistema;
    }
    
    public BigDecimal getMontoCierreReal() {
        return montoCierreReal;
    }
    
    public void setMontoCierreReal(BigDecimal montoCierreReal) {
        this.montoCierreReal = montoCierreReal;
    }
    
    public BigDecimal getDiferencia() {
        return diferencia;
    }
    
    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }
    
    public BigDecimal getTotalVentas() {
        return totalVentas;
    }
    
    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }
    
    public BigDecimal getTotalEntradas() {
        return totalEntradas;
    }
    
    public void setTotalEntradas(BigDecimal totalEntradas) {
        this.totalEntradas = totalEntradas;
    }
    
    public BigDecimal getTotalSalidas() {
        return totalSalidas;
    }
    
    public void setTotalSalidas(BigDecimal totalSalidas) {
        this.totalSalidas = totalSalidas;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getObservacionesApertura() {
        return observacionesApertura;
    }
    
    public void setObservacionesApertura(String observacionesApertura) {
        this.observacionesApertura = observacionesApertura;
    }
    
    public String getObservacionesCierre() {
        return observacionesCierre;
    }
    
    public void setObservacionesCierre(String observacionesCierre) {
        this.observacionesCierre = observacionesCierre;
    }
    
    public String getCajaNombre() {
        return cajaNombre;
    }
    
    public void setCajaNombre(String cajaNombre) {
        this.cajaNombre = cajaNombre;
    }
    
    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
    
    // Métodos de utilidad
    public BigDecimal getDineroEsperado() {
        return montoApertura.add(totalVentas).add(totalEntradas).subtract(totalSalidas);
    }
    
    public boolean estaAbierta() {
        return "ABIERTA".equals(estado);
    }
    
    public boolean estaCerrada() {
        return "CERRADA".equals(estado);
    }
    
    @Override
    public String toString() {
        return numeroSesion + " - " + usuarioNombre + " (" + estado + ")";
    }
}