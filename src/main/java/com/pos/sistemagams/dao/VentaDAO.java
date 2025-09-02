/*
 * DAO para gestionar las ventas del sistema
 * VERSIÓN CORREGIDA: Ahora incluye actualización de stock
 */
package com.pos.sistemagams.dao;

import com.pos.sistemagams.modelo.*;
import com.pos.sistemagams.util.ConexionBD;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones de ventas
 */
public class VentaDAO {

    private Connection conexion;

    public VentaDAO() {
        try {
            this.conexion = ConexionBD.obtenerConexion();
            System.out.println("✅ VentaDAO inicializado correctamente");
        } catch (SQLException e) {
            System.err.println("❌ Error al inicializar VentaDAO: " + e.getMessage());
            this.conexion = null;
        }
    }

    /**
     * ✅ MÉTODO CORREGIDO: Guarda una venta completa con sus detalles Y
     * ACTUALIZA EL STOCK
     */
    public boolean guardarVenta(Venta venta, List<ItemVenta> items) throws SQLException {
        String sqlVenta = "INSERT INTO ventas (numero_venta, numero_ticket, id_usuario, id_sesion_caja, "
                + "subtotal, descuento, total, efectivo_recibido, cambio, metodo_pago, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_inventario, cantidad, precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";

        // ✅ NUEVO: Query para actualizar stock
        String sqlActualizarStock = """
            UPDATE inventario 
            SET stock_actual = stock_actual - ?, 
                fecha_actualizacion = CURRENT_TIMESTAMP 
            WHERE id_producto = ? AND id_almacen = ?
        """;

        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;
        PreparedStatement stmtStock = null;

        try {
            conn = ConexionBD.obtenerConexion();

            // Configurar timeout y transacción
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            System.out.println("🚀 Iniciando proceso de venta: " + venta.getNumeroTicket());

            // 1. Insertar venta principal
            stmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            stmtVenta.setQueryTimeout(30);
            stmtVenta.setString(1, venta.getNumeroVenta());
            stmtVenta.setString(2, venta.getNumeroTicket());
            stmtVenta.setInt(3, venta.getIdUsuario());
            stmtVenta.setInt(4, venta.getIdSesionCaja());
            stmtVenta.setBigDecimal(5, venta.getSubtotal());
            stmtVenta.setBigDecimal(6, venta.getDescuento());
            stmtVenta.setBigDecimal(7, venta.getTotal());
            stmtVenta.setBigDecimal(8, venta.getEfectivoRecibido());
            stmtVenta.setBigDecimal(9, venta.getCambio());
            stmtVenta.setString(10, venta.getMetodoPago());
            stmtVenta.setString(11, "COMPLETADA");

            int filasAfectadas = stmtVenta.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener ID de la venta insertada
                ResultSet rs = stmtVenta.getGeneratedKeys();
                if (rs.next()) {
                    int idVenta = rs.getInt(1);
                    System.out.println("✅ Venta principal guardada con ID: " + idVenta);

                    // 2. Preparar statements para detalles y stock
                    stmtDetalle = conn.prepareStatement(sqlDetalle);
                    stmtDetalle.setQueryTimeout(30);

                    stmtStock = conn.prepareStatement(sqlActualizarStock);
                    stmtStock.setQueryTimeout(30);

                    // 3. Procesar cada item de la venta
                    for (int i = 0; i < items.size(); i++) {
                        ItemVenta item = items.get(i);
                        System.out.println("📋 Procesando item " + (i + 1) + "/" + items.size()
                                + ": " + item.getProducto().getNombre()
                                + " (Cantidad: " + item.getCantidad() + ")");

                        // Obtener el ID del inventario para este producto
                        int idInventario = obtenerIdInventario(conn, item.getProducto().getIdProducto());

                        // ✅ VERIFICAR STOCK ANTES DE PROCESAR
                        BigDecimal stockActual = obtenerStockActual(conn, item.getProducto().getIdProducto());
                        if (stockActual.compareTo(item.getCantidad()) < 0) {
                            System.err.println("❌ Stock insuficiente para " + item.getProducto().getNombre()
                                    + ". Disponible: " + stockActual + ", Solicitado: " + item.getCantidad());
                            conn.rollback();
                            throw new SQLException("Stock insuficiente para el producto: " + item.getProducto().getNombre());
                        }

                        // Insertar detalle de venta
                        stmtDetalle.setInt(1, idVenta);
                        stmtDetalle.setInt(2, idInventario);
                        stmtDetalle.setBigDecimal(3, item.getCantidad());
                        stmtDetalle.setBigDecimal(4, item.getPrecioUnitario());
                        stmtDetalle.setBigDecimal(5, item.getTotal());
                        stmtDetalle.addBatch();

                        // ✅ NUEVO: Actualizar stock en inventario
                        stmtStock.setBigDecimal(1, item.getCantidad()); // Cantidad a descontar
                        stmtStock.setInt(2, item.getProducto().getIdProducto()); // ID del producto
                        stmtStock.setInt(3, item.getProducto().getIdAlmacen()); // ID del almacén
                        stmtStock.addBatch();

                        System.out.println("✅ Item preparado: " + item.getProducto().getNombre()
                                + " - Descontar " + item.getCantidad() + " unidades");
                    }

                    // Ejecutar batch de detalles
                    int[] resultadosDetalle = stmtDetalle.executeBatch();
                    System.out.println("✅ Detalles de venta guardados: " + resultadosDetalle.length + " registros");

                    // ✅ NUEVO: Ejecutar batch de actualización de stock
                    int[] resultadosStock = stmtStock.executeBatch();
                    System.out.println("✅ Stock actualizado: " + resultadosStock.length + " productos");

                    // ✅ VERIFICACIÓN: Comprobar que el stock se actualizó correctamente
                    for (ItemVenta item : items) {
                        BigDecimal stockFinal = obtenerStockActual(conn, item.getProducto().getIdProducto());
                        System.out.println("📦 Stock final " + item.getProducto().getNombre() + ": " + stockFinal);
                    }

                    // 4. Actualizar totales en sesión de caja
                    actualizarTotalesSesion(conn, venta.getIdSesionCaja(), venta.getTotal());

                    // ✅ CONFIRMAR TRANSACCIÓN
                    conn.commit();
                    System.out.println("🎉 Venta completada exitosamente: " + venta.getNumeroTicket());
                    System.out.println("💰 Total: $" + venta.getTotal());
                    return true;
                }
            }

            conn.rollback();
            System.err.println("❌ No se pudo insertar la venta principal");
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("🔄 Rollback ejecutado debido a error");
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
            System.err.println("❌ Error al guardar venta: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            // Cerrar recursos en orden inverso
            try {
                if (stmtStock != null) {
                    stmtStock.close();
                }
                if (stmtDetalle != null) {
                    stmtDetalle.close();
                }
                if (stmtVenta != null) {
                    stmtVenta.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    /**
     * ✅ NUEVO MÉTODO: Obtiene el stock actual de un producto
     */
    private BigDecimal obtenerStockActual(Connection conn, int idProducto) throws SQLException {
        String sql = "SELECT COALESCE(SUM(stock_actual), 0) as stock FROM inventario WHERE id_producto = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("stock");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * ✅ MÉTODO CORREGIDO: Genera número de venta único verificando duplicados
     */
    public String generarNumeroVenta() throws SQLException {
        if (conexion == null) {
            conexion = ConexionBD.obtenerConexion();
        }

        String numeroVenta = null;
        int intentos = 0;
        int maxIntentos = 10;

        while (numeroVenta == null && intentos < maxIntentos) {
            try {
                // Primero intentar con la función de BD si existe
                String sql = "SELECT GenerarNumeroTicket() as numero";

                try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        String numeroGenerado = "VTA-" + rs.getString("numero");

                        // Verificar que no exista
                        if (!existeNumeroVenta(numeroGenerado)) {
                            numeroVenta = numeroGenerado;
                            System.out.println("✅ Número de venta generado con función BD: " + numeroVenta);
                            break;
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("⚠️ Función BD no disponible, usando generador manual");
                }

                // Fallback: Generador manual mejorado
                if (numeroVenta == null) {
                    numeroVenta = generarNumeroManual();

                    // Verificar unicidad
                    if (existeNumeroVenta(numeroVenta)) {
                        numeroVenta = null; // Reintentar
                        intentos++;
                        System.out.println("⚠️ Número duplicado detectado, reintentando... (" + intentos + "/" + maxIntentos + ")");
                        Thread.sleep(10); // Pequeña pausa para evitar duplicados por tiempo
                    } else {
                        System.out.println("✅ Número de venta único generado: " + numeroVenta);
                    }
                }

            } catch (Exception e) {
                intentos++;
                System.err.println("❌ Error en intento " + intentos + ": " + e.getMessage());

                if (intentos >= maxIntentos) {
                    throw new SQLException("No se pudo generar un número de venta único después de " + maxIntentos + " intentos");
                }
            }
        }

        return numeroVenta;
    }

    /**
     * ✅ NUEVO MÉTODO: Verifica si ya existe un número de venta
     */
    private boolean existeNumeroVenta(String numeroVenta) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM ventas WHERE numero_venta = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, numeroVenta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }

        return false;
    }

    /**
     * ✅ NUEVO MÉTODO: Generador manual mejorado con múltiples estrategias
     */
    private String generarNumeroManual() {
        // Estrategia 1: Timestamp + Random
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 9999) + 1;

        // Formato: VTA-YYYYMMDD-HHMMSS-XXXX
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss");
        String fechaHora = sdf.format(new java.util.Date());

        return String.format("VTA-%s-%04d", fechaHora, random);
    }

    /**
     * ✅ MÉTODO CORREGIDO: Genera número de ticket único verificando duplicados
     */
    public String generarNumeroTicket() throws SQLException {
        if (conexion == null) {
            conexion = ConexionBD.obtenerConexion();
        }

        String numeroTicket = null;
        int intentos = 0;
        int maxIntentos = 10;

        while (numeroTicket == null && intentos < maxIntentos) {
            try {
                // Intentar con función de BD
                String sql = "SELECT GenerarNumeroTicket() as numero";

                try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        String numeroGenerado = rs.getString("numero");

                        // Verificar que no exista
                        if (!existeNumeroTicket(numeroGenerado)) {
                            numeroTicket = numeroGenerado;
                            System.out.println("✅ Número de ticket generado con función BD: " + numeroTicket);
                            break;
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("⚠️ Función BD no disponible para tickets, usando generador manual");
                }

                // Fallback manual
                if (numeroTicket == null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMM");
                    String mesAno = sdf.format(new java.util.Date());
                    int secuencial = obtenerSiguienteSecuencial(mesAno);

                    numeroTicket = mesAno + "-" + String.format("%06d", secuencial);

                    // Verificar unicidad
                    if (existeNumeroTicket(numeroTicket)) {
                        numeroTicket = null;
                        intentos++;
                        System.out.println("⚠️ Número de ticket duplicado, reintentando... (" + intentos + "/" + maxIntentos + ")");
                    } else {
                        System.out.println("✅ Número de ticket único generado: " + numeroTicket);
                    }
                }

            } catch (Exception e) {
                intentos++;
                System.err.println("❌ Error generando ticket en intento " + intentos + ": " + e.getMessage());

                if (intentos >= maxIntentos) {
                    // Última alternativa: timestamp
                    long timestamp = System.currentTimeMillis();
                    numeroTicket = "TKT-" + timestamp;
                    System.out.println("⚠️ Usando número de emergencia: " + numeroTicket);
                }
            }
        }

        return numeroTicket;
    }

    /**
     * ✅ NUEVO MÉTODO: Verifica si ya existe un número de ticket
     */
    private boolean existeNumeroTicket(String numeroTicket) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM ventas WHERE numero_ticket = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, numeroTicket);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }

        return false;
    }

    /**
     * ✅ NUEVO MÉTODO: Obtiene el siguiente secuencial para un mes/año
     */
    private int obtenerSiguienteSecuencial(String mesAno) throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(numero_ticket, 8) AS UNSIGNED)), 0) + 1 as siguiente "
                + "FROM ventas WHERE numero_ticket LIKE ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, mesAno + "-%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("siguiente");
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error obteniendo secuencial: " + e.getMessage());
            // Fallback: usar random
            return (int) (Math.random() * 999999) + 1;
        }

        return 1; // Primer ticket del mes
    }

    /**
     * Obtiene el ID del inventario para un producto
     */
    private int obtenerIdInventario(Connection conn, int idProducto) throws SQLException {
        String sql = "SELECT id_inventario FROM inventario WHERE id_producto = ? LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setQueryTimeout(10);
            stmt.setInt(1, idProducto);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_inventario");
                }
            }
        }

        throw new SQLException("No se encontró inventario para el producto ID: " + idProducto);
    }

    /**
     * Actualiza los totales de ventas en la sesión de caja
     */
    private void actualizarTotalesSesion(Connection conn, int idSesion, BigDecimal montoVenta) throws SQLException {
        String sql = "UPDATE sesiones_caja SET total_ventas = total_ventas + ? WHERE id_sesion = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setQueryTimeout(10);
            stmt.setBigDecimal(1, montoVenta);
            stmt.setInt(2, idSesion);
            stmt.executeUpdate();
            System.out.println("✅ Totales de sesión actualizados");
        }
    }

    /**
     * Obtiene todas las ventas de un rango de fechas
     */
    public List<Venta> obtenerVentasPorFechas(java.util.Date fechaInicio, java.util.Date fechaFin) throws SQLException {
        if (conexion == null) {
            conexion = ConexionBD.obtenerConexion();
        }

        String sql = "SELECT v.*, u.nombre as usuario_nombre, u.apellido as usuario_apellido "
                + "FROM ventas v "
                + "LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario "
                + "WHERE DATE(v.fecha_venta) BETWEEN ? AND ? "
                + "ORDER BY v.fecha_venta DESC";

        List<Venta> ventas = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Venta venta = new Venta();
                    venta.setIdVenta(rs.getInt("id_venta"));
                    venta.setNumeroVenta(rs.getString("numero_venta"));
                    venta.setNumeroTicket(rs.getString("numero_ticket"));
                    venta.setIdUsuario(rs.getInt("id_usuario"));
                    venta.setIdSesionCaja(rs.getInt("id_sesion_caja"));
                    venta.setSubtotal(rs.getBigDecimal("subtotal"));
                    venta.setDescuento(rs.getBigDecimal("descuento"));
                    venta.setTotal(rs.getBigDecimal("total"));
                    venta.setEfectivoRecibido(rs.getBigDecimal("efectivo_recibido"));
                    venta.setCambio(rs.getBigDecimal("cambio"));
                    venta.setMetodoPago(rs.getString("metodo_pago"));
                    venta.setEstado(rs.getString("estado"));
                    venta.setFechaVenta(rs.getTimestamp("fecha_venta"));

                    // Campos adicionales
                    String nombreCompleto = "";
                    if (rs.getString("usuario_nombre") != null) {
                        nombreCompleto = rs.getString("usuario_nombre");
                        if (rs.getString("usuario_apellido") != null) {
                            nombreCompleto += " " + rs.getString("usuario_apellido");
                        }
                    }
                    venta.setUsuarioNombre(nombreCompleto);

                    ventas.add(venta);
                }
            }
        }

        return ventas;
    }

    /**
     * Obtiene todas las ventas del día actual
     */
    public List<Venta> obtenerVentasHoy() throws SQLException {
        java.util.Date hoy = new java.util.Date();
        return obtenerVentasPorFechas(hoy, hoy);
    }

    /**
     * Calcula la utilidad de una venta
     */
    public BigDecimal calcularUtilidadVenta(int idVenta) throws SQLException {
        if (conexion == null) {
            conexion = ConexionBD.obtenerConexion();
        }

        String sql = "SELECT "
                + "SUM(dv.cantidad * dv.precio_unitario) as venta_total, "
                + "SUM(dv.cantidad * COALESCE(i.precio_costo, p.precio_compra)) as costo_total "
                + "FROM detalle_ventas dv "
                + "INNER JOIN inventario i ON dv.id_inventario = i.id_inventario "
                + "INNER JOIN productos p ON i.id_producto = p.id_producto "
                + "WHERE dv.id_venta = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVenta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal ventaTotal = rs.getBigDecimal("venta_total");
                    BigDecimal costoTotal = rs.getBigDecimal("costo_total");

                    if (ventaTotal != null && costoTotal != null) {
                        return ventaTotal.subtract(costoTotal);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al calcular utilidad para venta " + idVenta + ": " + e.getMessage());
            return calcularUtilidadConPrecioCompra(idVenta);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Método auxiliar para calcular utilidad usando precio_compra
     */
    private BigDecimal calcularUtilidadConPrecioCompra(int idVenta) throws SQLException {
        String sql = "SELECT "
                + "SUM(dv.cantidad * dv.precio_unitario) as venta_total, "
                + "SUM(dv.cantidad * p.precio_compra) as costo_total "
                + "FROM detalle_ventas dv "
                + "INNER JOIN inventario i ON dv.id_inventario = i.id_inventario "
                + "INNER JOIN productos p ON i.id_producto = p.id_producto "
                + "WHERE dv.id_venta = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idVenta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal ventaTotal = rs.getBigDecimal("venta_total");
                    BigDecimal costoTotal = rs.getBigDecimal("costo_total");

                    if (ventaTotal != null && costoTotal != null) {
                        return ventaTotal.subtract(costoTotal);
                    }
                }
            }
        }

        return BigDecimal.ZERO;
    }
}
