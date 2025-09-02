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
/**
 * Clase modelo para representar un Producto
 */
public class Producto {
    
    private int idProducto;
    private String codigo;
    private String nombre; // Campo unificado para nombre/descripción
    
    // Precios
    private BigDecimal precioCompra;
    private BigDecimal precioVenta1;
    private BigDecimal precioVenta2;
    private BigDecimal precioVenta3;
    private BigDecimal precioMayoreo;
    private int cantidadMayoreo;
    
    // Impuesto
    private boolean aplicaIgv;
    private BigDecimal porcentajeIgv;
    
    // Stock
    private int stockMinimo;
    private int stockMaximo;
    
    // Unidad
    private String unidadCompra;
    
    // Relaciones
    private int idCategoria;
    private int idProveedor;
    private int idDepartamento;
    private int idAlmacen;
    
    // Nombres para mostrar
    private String nombreCategoria;
    private String nombreProveedor;
    private String nombreDepartamento;
    private String nombreAlmacen;
    
    // Imagen
    private String imagenPath;
    
    // Control
    private boolean estado;
    private LocalDateTime fechaCreacion;
    
    // Constructor vacío
    public Producto() {
        this.aplicaIgv = true;
        this.porcentajeIgv = new BigDecimal("18.00");
        this.stockMinimo = 5;
        this.stockMaximo = 100;
        this.unidadCompra = "UND";
        this.estado = true;
        this.precioCompra = BigDecimal.ZERO;
        this.precioVenta1 = BigDecimal.ZERO;
        this.precioVenta2 = BigDecimal.ZERO;
        this.precioVenta3 = BigDecimal.ZERO;
        this.precioMayoreo = BigDecimal.ZERO;
        this.cantidadMayoreo = 0;
    }
    
    // Constructor con parámetros básicos
    public Producto(String codigo, String nombre, BigDecimal precioCompra, BigDecimal precioVenta1) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta1 = precioVenta1;
    }
    
    // Getters y Setters
    public int getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }
    
    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
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
    
    public BigDecimal getPrecioMayoreo() {
        return precioMayoreo;
    }
    
    public void setPrecioMayoreo(BigDecimal precioMayoreo) {
        this.precioMayoreo = precioMayoreo;
    }
    
    public int getCantidadMayoreo() {
        return cantidadMayoreo;
    }
    
    public void setCantidadMayoreo(int cantidadMayoreo) {
        this.cantidadMayoreo = cantidadMayoreo;
    }
    
    public boolean isAplicaIgv() {
        return aplicaIgv;
    }
    
    public void setAplicaIgv(boolean aplicaIgv) {
        this.aplicaIgv = aplicaIgv;
    }
    
    public BigDecimal getPorcentajeIgv() {
        return porcentajeIgv;
    }
    
    public void setPorcentajeIgv(BigDecimal porcentajeIgv) {
        this.porcentajeIgv = porcentajeIgv;
    }
    
    public int getStockMinimo() {
        return stockMinimo;
    }
    
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
    
    public int getStockMaximo() {
        return stockMaximo;
    }
    
    public void setStockMaximo(int stockMaximo) {
        this.stockMaximo = stockMaximo;
    }
    
    public String getUnidadCompra() {
        return unidadCompra;
    }
    
    public void setUnidadCompra(String unidadCompra) {
        this.unidadCompra = unidadCompra;
    }
    
    public int getIdCategoria() {
        return idCategoria;
    }
    
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
    
    public int getIdProveedor() {
        return idProveedor;
    }
    
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }
    
    public int getIdDepartamento() {
        return idDepartamento;
    }
    
    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }
    
    public int getIdAlmacen() {
        return idAlmacen;
    }
    
    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }
    
    public String getNombreCategoria() {
        return nombreCategoria;
    }
    
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
    
    public String getNombreProveedor() {
        return nombreProveedor;
    }
    
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }
    
    public String getNombreDepartamento() {
        return nombreDepartamento;
    }
    
    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }
    
    public String getNombreAlmacen() {
        return nombreAlmacen;
    }
    
    public void setNombreAlmacen(String nombreAlmacen) {
        this.nombreAlmacen = nombreAlmacen;
    }
    
    public String getImagenPath() {
        return imagenPath;
    }
    
    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }
    
    public boolean isEstado() {
        return estado;
    }
    
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    @Override
    public String toString() {
        return nombre + " (" + codigo + ")";
    }
}