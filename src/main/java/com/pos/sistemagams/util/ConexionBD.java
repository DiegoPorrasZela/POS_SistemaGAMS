package com.pos.sistemagams.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Clase para manejar la conexión a la base de datos MySQL
 */
public class ConexionBD {
    
    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/pos_db";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Static block para cargar el driver una sola vez
    static {
        try {
            Class.forName(DRIVER);
            System.out.println("✅ Driver MySQL cargado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: No se encontró el driver de MySQL");
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene una NUEVA conexión a la base de datos cada vez que se llama
     * ✅ CAMBIO PRINCIPAL: Siempre crear nueva conexión
     * @return Connection objeto de conexión
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            // Crear una NUEVA conexión cada vez
            Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            
            if (conexion != null) {
                // ✅ Configurar parámetros importantes de la conexión
                conexion.setAutoCommit(true); // Por defecto autocommit activo
                System.out.println("✅ Nueva conexión creada exitosamente");
                return conexion;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión a la base de datos");
            System.err.println("URL: " + URL);
            System.err.println("Usuario: " + USUARIO);
            System.err.println("Error: " + e.getMessage());
            
            // Re-lanzar la excepción para que sea manejada por el código que llama
            throw e;
        }
        
        throw new SQLException("No se pudo establecer conexión a la base de datos");
    }
    
    /**
     * Cierra una conexión específica
     * @param conexion La conexión a cerrar
     */
    public static void cerrarConexion(Connection conexion) {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("🔒 Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si una conexión específica está activa
     * @param conexion La conexión a verificar
     * @return true si la conexión está activa, false en caso contrario
     */
    public static boolean conexionActiva(Connection conexion) {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Método para probar la conexión
     */
    public static void probarConexion() {
        Connection conn = null;
        try {
            conn = obtenerConexion();
            System.out.println("🎉 ¡Conexión a la base de datos establecida correctamente!");
            
            // Probar una consulta simple
            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery("SELECT 1 as test")) {
                
                if (rs.next()) {
                    System.out.println("✅ Consulta de prueba exitosa: " + rs.getInt("test"));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("💥 Error: No se pudo establecer conexión a la base de datos");
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null, 
                "Error de conexión a la base de datos:\n" + e.getMessage(), 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            cerrarConexion(conn);
        }
    }
    
    /**
     * Obtiene información sobre la base de datos
     */
    public static void informacionBD() {
        Connection conn = null;
        try {
            conn = obtenerConexion();
            var metaData = conn.getMetaData();
            
            System.out.println("=== INFORMACIÓN DE LA BASE DE DATOS ===");
            System.out.println("Producto: " + metaData.getDatabaseProductName());
            System.out.println("Versión: " + metaData.getDatabaseProductVersion());
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Usuario: " + metaData.getUserName());
            System.out.println("AutoCommit por defecto: " + conn.getAutoCommit());
            System.out.println("======================================");
            
        } catch (SQLException e) {
            System.err.println("Error al obtener información de la BD: " + e.getMessage());
        } finally {
            cerrarConexion(conn);
        }
    }
}