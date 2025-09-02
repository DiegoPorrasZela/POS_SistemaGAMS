/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pos.sistemagams.vista;

/**
 * Interfaz para notificar actualizaciones de inventario entre paneles
 */
public interface InventarioUpdateListener {
    
    /**
     * Método llamado cuando se actualiza el inventario
     */
    void onInventarioActualizado();
    
    /**
     * Método llamado cuando se necesita refrescar los datos
     */
    void refrescarDatos();
}