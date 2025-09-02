/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Modelo para representar un item en una venta
 */
package com.pos.sistemagams.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 *
 * @author Diego
 */
/**
 * Clase modelo para representar un Item de Venta
 */
public class ItemVenta {
    
    private Producto producto;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal total;
    
    // Constructor vacío
    public ItemVenta() {
        this.cantidad = BigDecimal.ZERO;
        this.precioUnitario = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }
    
    // Constructor con parámetros
    public ItemVenta(Producto producto, BigDecimal cantidad, BigDecimal precioUnitario) {
        this();
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularTotal();
    }
    
    // Getters y Setters
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public BigDecimal getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
        calcularTotal();
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularTotal();
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
        calcularTotal();
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    /**
     * Calcula el total del item
     */
    public void calcularTotal() {
        if (cantidad != null && precioUnitario != null) {
            BigDecimal subtotal = cantidad.multiply(precioUnitario);
            
            if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
                this.total = subtotal.subtract(descuento);
            } else {
                this.total = subtotal;
            }
            
            // Redondear a 2 decimales
            this.total = this.total.setScale(2, RoundingMode.HALF_UP);
        }
    }
    
    /**
     * Calcula el IGV del item si aplica
     */
    public BigDecimal calcularIGV() {
        if (producto != null && producto.isAplicaIgv() && total != null) {
            return total.multiply(producto.getPorcentajeIgv())
                       .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Obtiene el total con IGV incluido
     */
    public BigDecimal getTotalConIGV() {
        return total.add(calcularIGV());
    }
    
    @Override
    public String toString() {
        return String.format("%s x%s = $%.2f", 
            producto != null ? producto.getNombre() : "Sin producto",
            cantidad,
            total);
    }
}
