/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.modelo;

import java.math.BigDecimal;
/**
 *
 * @author Diego
 */
public class DetalleMovimientoInventario {
    
    private int idDetalleMovimiento;
    private int idMovimiento;
    private int idProducto;
    private BigDecimal cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal precioVenta1;
    private BigDecimal precioVenta2;
    private BigDecimal precioVenta3;
    private BigDecimal subtotal;
    private BigDecimal stockAnterior;
    private BigDecimal stockNuevo;
    
    // Campos adicionales para mostrar información del producto
    private String codigoProducto;
    private String nombreProducto;
    private String categoriaProducto;
    private String unidadMedida;
    
    // Constructores
    public DetalleMovimientoInventario() {
        this.cantidad = BigDecimal.ZERO;
        this.costoUnitario = BigDecimal.ZERO;
        this.precioVenta1 = BigDecimal.ZERO;
        this.precioVenta2 = BigDecimal.ZERO;
        this.precioVenta3 = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
        this.stockAnterior = BigDecimal.ZERO;
        this.stockNuevo = BigDecimal.ZERO;
    }
    
    public DetalleMovimientoInventario(int idMovimiento, int idProducto, BigDecimal cantidad, BigDecimal costoUnitario) {
        this();
        this.idMovimiento = idMovimiento;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        calcularSubtotal();
    }
    
    // Getters y Setters
    public int getIdDetalleMovimiento() {
        return idDetalleMovimiento;
    }
    
    public void setIdDetalleMovimiento(int idDetalleMovimiento) {
        this.idDetalleMovimiento = idDetalleMovimiento;
    }
    
    public int getIdMovimiento() {
        return idMovimiento;
    }
    
    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }
    
    public int getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    
    public BigDecimal getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }
    
    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
        calcularSubtotal();
    }
    
    public BigDecimal getPrecioVenta1() {
        return precioVenta1;
    }
    
    public void setPrecioVenta1(BigDecimal precioVenta1) {
        this.precioVenta1 = precioVenta1;
    }
    
    public BigDecimal getPrecioVenta2() {
        return precioVenta2;
    }
    
    public void setPrecioVenta2(BigDecimal precioVenta2) {
        this.precioVenta2 = precioVenta2;
    }
    
    public BigDecimal getPrecioVenta3() {
        return precioVenta3;
    }
    
    public void setPrecioVenta3(BigDecimal precioVenta3) {
        this.precioVenta3 = precioVenta3;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getStockAnterior() {
        return stockAnterior;
    }
    
    public void setStockAnterior(BigDecimal stockAnterior) {
        this.stockAnterior = stockAnterior;
    }
    
    public BigDecimal getStockNuevo() {
        return stockNuevo;
    }
    
    public void setStockNuevo(BigDecimal stockNuevo) {
        this.stockNuevo = stockNuevo;
    }
    
    public String getCodigoProducto() {
        return codigoProducto;
    }
    
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    public String getCategoriaProducto() {
        return categoriaProducto;
    }
    
    public void setCategoriaProducto(String categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }
    
    public String getUnidadMedida() {
        return unidadMedida;
    }
    
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
    
    // Métodos de utilidad
    public void calcularSubtotal() {
        if (cantidad != null && costoUnitario != null) {
            this.subtotal = cantidad.multiply(costoUnitario);
        }
    }
    
    public BigDecimal calcularVariacionStock() {
        if (stockNuevo != null && stockAnterior != null) {
            return stockNuevo.subtract(stockAnterior);
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (Cant: %s, Costo: %s)", 
            codigoProducto != null ? codigoProducto : "SIN CÓDIGO",
            nombreProducto != null ? nombreProducto : "SIN NOMBRE",
            cantidad != null ? cantidad.toString() : "0",
            costoUnitario != null ? costoUnitario.toString() : "0.00");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        DetalleMovimientoInventario that = (DetalleMovimientoInventario) obj;
        return idDetalleMovimiento == that.idDetalleMovimiento;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idDetalleMovimiento);
    }
}
