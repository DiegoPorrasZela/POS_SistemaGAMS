/*
 * Modelo para representar una venta
 */
package com.pos.sistemagams.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Clase modelo para representar una Venta
 */
public class Venta {
    
    private int idVenta;
    private String numeroVenta;
    private String numeroTicket;
    private int idUsuario;
    private int idSesionCaja;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private BigDecimal efectivoRecibido;
    private BigDecimal cambio;
    private String metodoPago;
    private String estado;
    private Timestamp fechaVenta;
    
    // Campos adicionales para mostrar
    private String usuarioNombre;
    private String clienteNombre;
    private BigDecimal utilidad;
    
    // Constructor vacío
    public Venta() {
        this.subtotal = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.efectivoRecibido = BigDecimal.ZERO;
        this.cambio = BigDecimal.ZERO;
        this.utilidad = BigDecimal.ZERO;
        this.metodoPago = "EFECTIVO";
        this.estado = "COMPLETADA";
        this.clienteNombre = "Público General";
    }
    
    // Constructor con parámetros básicos
    public Venta(String numeroTicket, int idUsuario, int idSesionCaja, BigDecimal total) {
        this();
        this.numeroTicket = numeroTicket;
        this.idUsuario = idUsuario;
        this.idSesionCaja = idSesionCaja;
        this.total = total;
        this.subtotal = total; // Por defecto, puede ajustarse
    }
    
    // Getters y Setters
    public int getIdVenta() {
        return idVenta;
    }
    
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }
    
    public String getNumeroVenta() {
        return numeroVenta;
    }
    
    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }
    
    public String getNumeroTicket() {
        return numeroTicket;
    }
    
    public void setNumeroTicket(String numeroTicket) {
        this.numeroTicket = numeroTicket;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public int getIdSesionCaja() {
        return idSesionCaja;
    }
    
    public void setIdSesionCaja(int idSesionCaja) {
        this.idSesionCaja = idSesionCaja;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public BigDecimal getEfectivoRecibido() {
        return efectivoRecibido;
    }
    
    public void setEfectivoRecibido(BigDecimal efectivoRecibido) {
        this.efectivoRecibido = efectivoRecibido;
    }
    
    public BigDecimal getCambio() {
        return cambio;
    }
    
    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Timestamp getFechaVenta() {
        return fechaVenta;
    }
    
    public void setFechaVenta(Timestamp fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
    
    public String getClienteNombre() {
        return clienteNombre;
    }
    
    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }
    
    public BigDecimal getUtilidad() {
        return utilidad;
    }
    
    public void setUtilidad(BigDecimal utilidad) {
        this.utilidad = utilidad;
    }
    
    // Métodos de utilidad
    public BigDecimal calcularImpuesto() {
        // Calcular IGV (18% incluido en el total)
        BigDecimal factor = new BigDecimal("1.18");
        BigDecimal subtotalSinIGV = total.divide(factor, 2, java.math.RoundingMode.HALF_UP);
        return total.subtract(subtotalSinIGV);
    }
    
    public BigDecimal calcularSubtotalSinIGV() {
        BigDecimal factor = new BigDecimal("1.18");
        return total.divide(factor, 2, java.math.RoundingMode.HALF_UP);
    }
    
    @Override
    public String toString() {
        return numeroTicket + " - " + usuarioNombre + " ($" + total + ")";
    }
}