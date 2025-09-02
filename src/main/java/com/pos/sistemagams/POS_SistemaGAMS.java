package com.pos.sistemagams;

import com.formdev.flatlaf.FlatDarkLaf;
import com.pos.sistemagams.util.ConexionBD;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Diego
 */

/**
 * Clase principal del Sistema POS para Tienda de Ropa
 */
public class POS_SistemaGAMS {

    public static void main(String[] args) {
        // Configurar el Look and Feel moderno
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            System.out.println("✅ Look and Feel FlatLaf aplicado correctamente");
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("❌ Error al aplicar FlatLaf: " + e.getMessage());
            System.out.println("⚠️  Usando Look and Feel por defecto");
        }

        // Mostrar información del sistema
        System.out.println("🏪 =================================");
        System.out.println("🏪 SISTEMA POS - TIENDA DE ROPA");
        System.out.println("🏪 Version: 1.0");
        System.out.println("🏪 =================================");

        // Probar la conexión a la base de datos
        System.out.println("\n🔄 Probando conexión a la base de datos...");
        ConexionBD.probarConexion();

        // Aquí más adelante iniciaremos la ventana de login
        System.out.println("\n🚀 Sistema iniciado correctamente");
        System.out.println("📝 Próximo paso: Crear ventana de login");
        // Mostrar login
        javax.swing.SwingUtilities.invokeLater(() -> {
            new com.pos.sistemagams.vista.VistaLogin().setVisible(true);
        });

    }

}
