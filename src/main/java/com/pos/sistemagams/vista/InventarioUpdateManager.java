/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.vista;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Manager singleton para coordinar actualizaciones de inventario entre paneles
 */
public class InventarioUpdateManager {
    
    private static InventarioUpdateManager instance;
    private List<InventarioUpdateListener> listeners;
    
    private InventarioUpdateManager() {
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Obtiene la instancia singleton
     */
    public static InventarioUpdateManager getInstance() {
        if (instance == null) {
            instance = new InventarioUpdateManager();
        }
        return instance;
    }
    
    /**
     * Registra un listener para recibir notificaciones
     */
    public void addListener(InventarioUpdateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            System.out.println("📢 Listener registrado: " + listener.getClass().getSimpleName());
        }
    }
    
    /**
     * Desregistra un listener
     */
    public void removeListener(InventarioUpdateListener listener) {
        listeners.remove(listener);
        System.out.println("📢 Listener desregistrado: " + listener.getClass().getSimpleName());
    }
    
    /**
     * Notifica a todos los listeners que el inventario se ha actualizado
     */
    public void notificarInventarioActualizado() {
        System.out.println("🔔 Notificando actualización de inventario a " + listeners.size() + " listeners");
        
        // Ejecutar en el hilo de la interfaz
        SwingUtilities.invokeLater(() -> {
            for (InventarioUpdateListener listener : listeners) {
                try {
                    listener.onInventarioActualizado();
                } catch (Exception e) {
                    System.err.println("❌ Error al notificar listener: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Fuerza el refresco de todos los paneles
     */
    public void forzarRefresco() {
        System.out.println("🔄 Forzando refresco de todos los paneles");
        
        SwingUtilities.invokeLater(() -> {
            for (InventarioUpdateListener listener : listeners) {
                try {
                    listener.refrescarDatos();
                } catch (Exception e) {
                    System.err.println("❌ Error al refrescar listener: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Obtiene el número de listeners registrados
     */
    public int getListenerCount() {
        return listeners.size();
    }
}