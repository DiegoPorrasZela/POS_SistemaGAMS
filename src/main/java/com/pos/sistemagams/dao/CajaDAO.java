/*
 * DAO para operaciones de caja
 */
package com.pos.sistemagams.dao;

import com.pos.sistemagams.modelo.SesionCaja;
import com.pos.sistemagams.modelo.MovimientoCaja;
import com.pos.sistemagams.util.ConexionBD;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * DAO para gestión de operaciones de caja
 */
public class CajaDAO {

    // ========================================
    // MÉTODOS PARA SESIONES DE CAJA
    // ========================================

    /**
     * Abre una nueva sesión de caja
     */
    public SesionCaja abrirCaja(int idUsuario, BigDecimal montoApertura, String observaciones) {
        String sqlVerificar = "SELECT COUNT(*) FROM sesiones_caja WHERE id_usuario = ? AND estado = 'ABIERTA'";
        String sqlInsertar = """
            INSERT INTO sesiones_caja (
                numero_sesion, id_caja, id_usuario, monto_apertura, 
                observaciones_apertura, estado
            ) VALUES (
                GenerarNumeroSesion(), 1, ?, ?, ?, 'ABIERTA'
            )
        """;

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            
            // Verificar si el usuario ya tiene una caja abierta
            try (PreparedStatement pstVerificar = conn.prepareStatement(sqlVerificar)) {
                pstVerificar.setInt(1, idUsuario);
                ResultSet rs = pstVerificar.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("❌ El usuario ya tiene una caja abierta");
                    return null;
                }
            }

            // Insertar nueva sesión
            try (PreparedStatement pst = conn.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)) {
                pst.setInt(1, idUsuario);
                pst.setBigDecimal(2, montoApertura);
                pst.setString(3, observaciones);

                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    ResultSet keys = pst.getGeneratedKeys();
                    if (keys.next()) {
                        int idSesion = keys.getInt(1);
                        
                        // Obtener la sesión completa
                        SesionCaja sesion = obtenerSesionPorId(idSesion);
                        System.out.println("✅ Caja abierta exitosamente: " + sesion.getNumeroSesion());
                        return sesion;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al abrir caja: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al abrir caja: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConexionBD.cerrarConexion(conn);
        }

        return null;
    }

    /**
     * Obtiene la sesión activa de un usuario
     */
    public SesionCaja obtenerSesionActiva(int idUsuario) {
        String sql = """
            SELECT sc.*, c.nombre as caja_nombre, u.nombre as usuario_nombre, u.apellido as usuario_apellido
            FROM sesiones_caja sc
            INNER JOIN cajas c ON sc.id_caja = c.id_caja
            INNER JOIN usuarios u ON sc.id_usuario = u.id_usuario
            WHERE sc.id_usuario = ? AND sc.estado = 'ABIERTA'
            ORDER BY sc.fecha_apertura DESC
            LIMIT 1
        """;

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, idUsuario);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    return mapearSesionCaja(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener sesión activa: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conn);
        }

        return null;
    }

    /**
     * Obtiene una sesión por ID
     */
    public SesionCaja obtenerSesionPorId(int idSesion) {
        String sql = """
            SELECT sc.*, c.nombre as caja_nombre, u.nombre as usuario_nombre, u.apellido as usuario_apellido
            FROM sesiones_caja sc
            INNER JOIN cajas c ON sc.id_caja = c.id_caja
            INNER JOIN usuarios u ON sc.id_usuario = u.id_usuario
            WHERE sc.id_sesion = ?
        """;

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, idSesion);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    return mapearSesionCaja(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener sesión por ID: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conn);
        }

        return null;
    }

    /**
     * Cierra una sesión de caja
     */
    public boolean cerrarCaja(int idSesion, BigDecimal montoCierreReal, String observaciones) {
        String sqlActualizar = """
            UPDATE sesiones_caja 
            SET fecha_cierre = NOW(),
                monto_cierre_real = ?,
                monto_cierre_sistema = (
                    monto_apertura + 
                    COALESCE(total_ventas, 0) + 
                    COALESCE(total_entradas, 0) - 
                    COALESCE(total_salidas, 0)
                ),
                diferencia = ? - (
                    monto_apertura + 
                    COALESCE(total_ventas, 0) + 
                    COALESCE(total_entradas, 0) - 
                    COALESCE(total_salidas, 0)
                ),
                observaciones_cierre = ?,
                estado = 'CERRADA'
            WHERE id_sesion = ? AND estado = 'ABIERTA'
        """;

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pst = conn.prepareStatement(sqlActualizar)) {
                pst.setBigDecimal(1, montoCierreReal);
                pst.setBigDecimal(2, montoCierreReal);
                pst.setString(3, observaciones);
                pst.setInt(4, idSesion);

                int filasAfectadas = pst.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("✅ Caja cerrada exitosamente");
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar caja: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al cerrar caja: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConexionBD.cerrarConexion(conn);
        }

        return false;
    }

    // ========================================
    // MÉTODOS PARA MOVIMIENTOS DE CAJA
    // ========================================

    /**
     * Registra una entrada de dinero a la caja
     */
    public boolean registrarEntrada(int idSesion, String concepto, BigDecimal monto, String observaciones, int idUsuario) {
        return registrarMovimientoCaja(idSesion, "ENTRADA", concepto, monto, observaciones, idUsuario);
    }

    /**
     * Registra una salida de dinero de la caja
     */
    public boolean registrarSalida(int idSesion, String concepto, BigDecimal monto, String observaciones, int idUsuario) {
        return registrarMovimientoCaja(idSesion, "SALIDA", concepto, monto, observaciones, idUsuario);
    }

    /**
     * Registra un movimiento de caja (entrada o salida)
     */
    private boolean registrarMovimientoCaja(int idSesion, String tipoMovimiento, String concepto, 
                                          BigDecimal monto, String observaciones, int idUsuario) {
        String sqlMovimiento = """
            INSERT INTO movimientos_caja (
                numero_movimiento, id_sesion, tipo_movimiento, concepto, 
                monto, observaciones, id_usuario
            ) VALUES (
                GenerarNumeroMovimientoCaja(?), ?, ?, ?, ?, ?, ?
            )
        """;

        String sqlActualizarSesion = """
            UPDATE sesiones_caja 
            SET total_entradas = (
                    SELECT COALESCE(SUM(monto), 0) 
                    FROM movimientos_caja 
                    WHERE id_sesion = ? AND tipo_movimiento = 'ENTRADA'
                ),
                total_salidas = (
                    SELECT COALESCE(SUM(monto), 0) 
                    FROM movimientos_caja 
                    WHERE id_sesion = ? AND tipo_movimiento = 'SALIDA'
                )
            WHERE id_sesion = ?
        """;

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // Insertar movimiento
            try (PreparedStatement pstMovimiento = conn.prepareStatement(sqlMovimiento)) {
                pstMovimiento.setString(1, tipoMovimiento);
                pstMovimiento.setInt(2, idSesion);
                pstMovimiento.setString(3, tipoMovimiento);
                pstMovimiento.setString(4, concepto);
                pstMovimiento.setBigDecimal(5, monto);
                pstMovimiento.setString(6, observaciones);
                pstMovimiento.setInt(7, idUsuario);

                int filasMovimiento = pstMovimiento.executeUpdate();

                if (filasMovimiento > 0) {
                    // Actualizar totales en la sesión
                    try (PreparedStatement pstSesion = conn.prepareStatement(sqlActualizarSesion)) {
                        pstSesion.setInt(1, idSesion);
                        pstSesion.setInt(2, idSesion);
                        pstSesion.setInt(3, idSesion);
                        pstSesion.executeUpdate();
                    }

                    conn.commit();
                    System.out.println("✅ " + tipoMovimiento + " registrada exitosamente: $" + monto);
                    return true;
                }
            }

            conn.rollback();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error al registrar " + tipoMovimiento.toLowerCase() + ": " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al registrar " + tipoMovimiento.toLowerCase() + ": " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    ConexionBD.cerrarConexion(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Obtiene todos los movimientos de una sesión
     */
    public List<MovimientoCaja> obtenerMovimientosPorSesion(int idSesion) {
        List<MovimientoCaja> movimientos = new ArrayList<>();
        String sql = """
            SELECT mc.*, u.nombre as usuario_nombre, u.apellido as usuario_apellido
            FROM movimientos_caja mc
            INNER JOIN usuarios u ON mc.id_usuario = u.id_usuario
            WHERE mc.id_sesion = ?
            ORDER BY mc.fecha_movimiento DESC
        """;

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, idSesion);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    MovimientoCaja movimiento = mapearMovimientoCaja(rs);
                    movimientos.add(movimiento);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener movimientos: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conn);
        }

        return movimientos;
    }

    // ========================================
    // MÉTODOS PARA VENTAS
    // ========================================

    /**
     * Actualiza los totales de ventas en la sesión activa
     */
    public void actualizarTotalVentas(int idSesion) {
        String sql = """
            UPDATE sesiones_caja 
            SET total_ventas = (
                SELECT COALESCE(SUM(total), 0) 
                FROM ventas 
                WHERE id_sesion_caja = ? AND estado = 'COMPLETADA'
            )
            WHERE id_sesion = ?
        """;

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, idSesion);
                pst.setInt(2, idSesion);
                pst.executeUpdate();
                System.out.println("✅ Total de ventas actualizado");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar total de ventas: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conn);
        }
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    /**
     * Mapea un ResultSet a un objeto SesionCaja
     */
    private SesionCaja mapearSesionCaja(ResultSet rs) throws SQLException {
        SesionCaja sesion = new SesionCaja();
        
        sesion.setIdSesion(rs.getInt("id_sesion"));
        sesion.setNumeroSesion(rs.getString("numero_sesion"));
        sesion.setIdCaja(rs.getInt("id_caja"));
        sesion.setIdUsuario(rs.getInt("id_usuario"));
        sesion.setFechaApertura(rs.getTimestamp("fecha_apertura"));
        sesion.setFechaCierre(rs.getTimestamp("fecha_cierre"));
        sesion.setMontoApertura(rs.getBigDecimal("monto_apertura"));
        sesion.setMontoCierreSistema(rs.getBigDecimal("monto_cierre_sistema"));
        sesion.setMontoCierreReal(rs.getBigDecimal("monto_cierre_real"));
        sesion.setDiferencia(rs.getBigDecimal("diferencia"));
        sesion.setTotalVentas(rs.getBigDecimal("total_ventas"));
        sesion.setTotalEntradas(rs.getBigDecimal("total_entradas"));
        sesion.setTotalSalidas(rs.getBigDecimal("total_salidas"));
        sesion.setEstado(rs.getString("estado"));
        sesion.setObservacionesApertura(rs.getString("observaciones_apertura"));
        sesion.setObservacionesCierre(rs.getString("observaciones_cierre"));
        
        // Nombres para mostrar
        sesion.setCajaNombre(rs.getString("caja_nombre"));
        sesion.setUsuarioNombre(rs.getString("usuario_nombre") + " " + rs.getString("usuario_apellido"));
        
        return sesion;
    }

    /**
     * Mapea un ResultSet a un objeto MovimientoCaja
     */
    private MovimientoCaja mapearMovimientoCaja(ResultSet rs) throws SQLException {
        MovimientoCaja movimiento = new MovimientoCaja();
        
        movimiento.setIdMovimientoCaja(rs.getInt("id_movimiento_caja"));
        movimiento.setNumeroMovimiento(rs.getString("numero_movimiento"));
        movimiento.setIdSesion(rs.getInt("id_sesion"));
        movimiento.setTipoMovimiento(rs.getString("tipo_movimiento"));
        movimiento.setConcepto(rs.getString("concepto"));
        movimiento.setMonto(rs.getBigDecimal("monto"));
        movimiento.setObservaciones(rs.getString("observaciones"));
        movimiento.setIdUsuario(rs.getInt("id_usuario"));
        movimiento.setFechaMovimiento(rs.getTimestamp("fecha_movimiento"));
        
        // Nombres para mostrar
        movimiento.setUsuarioNombre(rs.getString("usuario_nombre") + " " + rs.getString("usuario_apellido"));
        
        return movimiento;
    }
}