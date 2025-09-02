package com.pos.sistemagams.dao;

import com.pos.sistemagams.modelo.MovimientoInventario;
import com.pos.sistemagams.modelo.DetalleMovimientoInventario;
import com.pos.sistemagams.util.ConexionBD;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * DAO para manejo de movimientos de inventario
 */
public class MovimientoInventarioDAO {

    // ========================================
    // MÉTODOS PARA MOVIMIENTOS DE INVENTARIO
    // ========================================
    /**
     * Guarda un movimiento completo con sus detalles en una transacción
     */
    /**
     * Guarda un movimiento completo con sus detalles en una transacción ✅
     * VERSIÓN FINAL CORREGIDA
     */
    public boolean guardarMovimientoCompleto(MovimientoInventario movimiento, List<DetalleMovimientoInventario> detalles) {
        Connection conn = null;
        try {
            System.out.println("🚀 Iniciando guardado de movimiento completo...");
            System.out.println("   Tipo: " + movimiento.getTipoMovimiento());
            System.out.println("   Almacén ID: " + movimiento.getIdAlmacen());
            System.out.println("   Detalles: " + detalles.size());

            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false); // ✅ Iniciar transacción

            // 1. Generar número de movimiento
            String numeroMovimiento = generarNumeroMovimiento(movimiento.getTipoMovimiento());
            movimiento.setNumeroMovimiento(numeroMovimiento);
            System.out.println("📄 Número generado: " + numeroMovimiento);

            // 2. Guardar el movimiento principal
            int idMovimiento = guardarMovimiento(conn, movimiento);
            if (idMovimiento == 0) {
                System.out.println("❌ Error al guardar movimiento principal");
                conn.rollback();
                return false;
            }
            System.out.println("✅ Movimiento principal guardado con ID: " + idMovimiento);

            // 3. Guardar los detalles y actualizar inventario
            for (int i = 0; i < detalles.size(); i++) {
                DetalleMovimientoInventario detalle = detalles.get(i);
                detalle.setIdMovimiento(idMovimiento);

                System.out.println("📋 Procesando detalle " + (i + 1) + "/" + detalles.size());

                // Guardar detalle
                if (!guardarDetalleMovimiento(conn, detalle)) {
                    System.out.println("❌ Error al guardar detalle " + (i + 1));
                    conn.rollback();
                    return false;
                }
                System.out.println("✅ Detalle " + (i + 1) + " guardado");

                // ✅ ACTUALIZAR INVENTARIO CON LA MISMA CONEXIÓN
                if (!actualizarInventario(conn, detalle, movimiento.getTipoMovimiento(), movimiento.getIdAlmacen())) {
                    System.out.println("❌ Error al actualizar inventario para detalle " + (i + 1));
                    conn.rollback();
                    return false;
                }
                System.out.println("✅ Inventario actualizado para detalle " + (i + 1));
            }

            // ✅ CONFIRMAR TRANSACCIÓN
            conn.commit();
            System.out.println("🎉 Transacción confirmada exitosamente: " + numeroMovimiento);

            // ✅ VERIFICACIÓN FINAL: Comprobar que el stock se guardó correctamente
            System.out.println("🔍 Verificación final del inventario:");
            for (DetalleMovimientoInventario detalle : detalles) {
                BigDecimal stockVerificacion = obtenerStockActualConConexion(conn, detalle.getIdProducto(), movimiento.getIdAlmacen());
                System.out.println("   Producto ID " + detalle.getIdProducto() + ": Stock final = " + stockVerificacion);
            }

            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("🔄 Rollback ejecutado");
                }
            } catch (SQLException ex) {
                System.err.println("❌ Error en rollback: " + ex.getMessage());
            }
            System.err.println("❌ Error al guardar movimiento completo: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al guardar el movimiento: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // ✅ Restaurar autocommit
                    ConexionBD.cerrarConexion(conn);
                }
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR: Obtener stock con una conexión específica
     */
    private BigDecimal obtenerStockActualConConexion(Connection conn, int idProducto, int idAlmacen) {
        String sql = "SELECT COALESCE(stock_actual, 0) as stock FROM inventario WHERE id_producto = ? AND id_almacen = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idProducto);
            pst.setInt(2, idAlmacen);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("stock");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener stock con conexión: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    /**
     * Guarda el movimiento principal y retorna el ID generado
     */
    private int guardarMovimiento(Connection conn, MovimientoInventario movimiento) throws SQLException {
        String sql = """
            INSERT INTO movimientos_inventario (
                numero_movimiento, tipo_movimiento, id_almacen, fecha_movimiento, 
                motivo, total_productos, total_cantidad, total_costo, estado, id_usuario
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, movimiento.getNumeroMovimiento());
            pst.setString(2, movimiento.getTipoMovimiento());
            pst.setInt(3, movimiento.getIdAlmacen());
            pst.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(5, movimiento.getMotivo());
            pst.setInt(6, movimiento.getTotalProductos());
            pst.setBigDecimal(7, movimiento.getTotalCantidad());
            pst.setBigDecimal(8, movimiento.getTotalCosto());
            pst.setString(9, movimiento.getEstado());
            pst.setInt(10, movimiento.getIdUsuario());

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    movimiento.setIdMovimiento(idGenerado);
                    return idGenerado;
                }
            }
        }

        return 0;
    }

    /**
     * Guarda un detalle de movimiento
     */
    private boolean guardarDetalleMovimiento(Connection conn, DetalleMovimientoInventario detalle) throws SQLException {
        String sql = """
            INSERT INTO detalle_movimientos_inventario (
                id_movimiento, id_producto, cantidad, costo_unitario, 
                precio_venta_1, precio_venta_2, precio_venta_3, subtotal, 
                stock_anterior, stock_nuevo
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, detalle.getIdMovimiento());
            pst.setInt(2, detalle.getIdProducto());
            pst.setBigDecimal(3, detalle.getCantidad());
            pst.setBigDecimal(4, detalle.getCostoUnitario());
            pst.setBigDecimal(5, detalle.getPrecioVenta1());
            pst.setBigDecimal(6, detalle.getPrecioVenta2());
            pst.setBigDecimal(7, detalle.getPrecioVenta3());
            pst.setBigDecimal(8, detalle.getSubtotal());
            pst.setBigDecimal(9, detalle.getStockAnterior());
            pst.setBigDecimal(10, detalle.getStockNuevo());

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    detalle.setIdDetalleMovimiento(rs.getInt(1));
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Actualiza el inventario según el tipo de movimiento
     */
    private boolean actualizarInventario(Connection conn, DetalleMovimientoInventario detalle,
            String tipoMovimiento, int idAlmacen) throws SQLException {

        // Buscar si existe registro en inventario
        String sqlBuscar = """
            SELECT stock_actual, precio_costo, precio_venta_actual 
            FROM inventario 
            WHERE id_producto = ? AND id_almacen = ?
        """;

        BigDecimal stockActual = BigDecimal.ZERO;
        boolean existeRegistro = false;

        try (PreparedStatement pst = conn.prepareStatement(sqlBuscar)) {
            pst.setInt(1, detalle.getIdProducto());
            pst.setInt(2, idAlmacen);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                stockActual = rs.getBigDecimal("stock_actual");
                existeRegistro = true;
            }
        }

        // Calcular nuevo stock
        BigDecimal nuevoStock = stockActual;
        switch (tipoMovimiento) {
            case "ENTRADA":
                nuevoStock = stockActual.add(detalle.getCantidad());
                break;
            case "SALIDA":
                nuevoStock = stockActual.subtract(detalle.getCantidad());
                break;
            case "AJUSTE":
                nuevoStock = detalle.getCantidad();
                break;
        }

        // Actualizar detalle con stock real
        detalle.setStockAnterior(stockActual);
        detalle.setStockNuevo(nuevoStock);

        // Insertar o actualizar inventario
        if (existeRegistro) {
            String sqlUpdate = """
                UPDATE inventario 
                SET stock_actual = ?, precio_costo = ?, precio_venta_actual = ?, 
                    fecha_actualizacion = CURRENT_TIMESTAMP 
                WHERE id_producto = ? AND id_almacen = ?
            """;

            try (PreparedStatement pst = conn.prepareStatement(sqlUpdate)) {
                pst.setBigDecimal(1, nuevoStock);
                pst.setBigDecimal(2, detalle.getCostoUnitario());
                pst.setBigDecimal(3, detalle.getPrecioVenta1());
                pst.setInt(4, detalle.getIdProducto());
                pst.setInt(5, idAlmacen);

                return pst.executeUpdate() > 0;
            }
        } else {
            String sqlInsert = """
                INSERT INTO inventario (id_producto, id_almacen, stock_actual, precio_costo, precio_venta_actual) 
                VALUES (?, ?, ?, ?, ?)
            """;

            try (PreparedStatement pst = conn.prepareStatement(sqlInsert)) {
                pst.setInt(1, detalle.getIdProducto());
                pst.setInt(2, idAlmacen);
                pst.setBigDecimal(3, nuevoStock);
                pst.setBigDecimal(4, detalle.getCostoUnitario());
                pst.setBigDecimal(5, detalle.getPrecioVenta1());

                return pst.executeUpdate() > 0;
            }
        }
    }

    /**
     * Genera número de movimiento automático
     */
    private String generarNumeroMovimiento(String tipoMovimiento) {
        String prefijo;
        switch (tipoMovimiento) {
            case "ENTRADA":
                prefijo = "ENT";
                break;
            case "SALIDA":
                prefijo = "SAL";
                break;
            case "AJUSTE":
                prefijo = "AJU";
                break;
            default:
                prefijo = "MOV";
                break;
        }

        String sql = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(numero_movimiento, 4) AS UNSIGNED)), 0) + 1 as siguiente
            FROM movimientos_inventario 
            WHERE numero_movimiento LIKE ?
        """;

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, prefijo + "%");
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int siguiente = rs.getInt("siguiente");
                return String.format("%s%06d", prefijo, siguiente);
            }

        } catch (SQLException e) {
            System.err.println("Error al generar número de movimiento: " + e.getMessage());
        }

        return prefijo + "000001";
    }

    /**
     * Obtiene el siguiente número para mostrar en el diálogo
     */
    public int obtenerSiguienteNumero(String tipoMovimiento) {
        String numeroMovimiento = generarNumeroMovimiento(tipoMovimiento);
        String numeroStr = numeroMovimiento.substring(3); // Quitar prefijo
        return Integer.parseInt(numeroStr);
    }

    /**
     * Obtiene el stock actual de un producto en un almacén ✅ CORREGIDO: Mejor
     * manejo de conexiones y debugging
     */
    public BigDecimal obtenerStockActual(int idProducto, int idAlmacen) {
        String sql = """
        SELECT COALESCE(stock_actual, 0) as stock 
        FROM inventario 
        WHERE id_producto = ? AND id_almacen = ?
    """;

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, idProducto);
                pst.setInt(2, idAlmacen);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal stock = rs.getBigDecimal("stock");
                        System.out.println("📦 Stock obtenido - Producto ID: " + idProducto
                                + ", Almacén ID: " + idAlmacen + ", Stock: " + stock);
                        return stock;
                    } else {
                        System.out.println("📦 No se encontró registro - Producto ID: " + idProducto
                                + ", Almacén ID: " + idAlmacen + " - Retornando 0");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener stock actual: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionBD.cerrarConexion(conn);
        }

        return BigDecimal.ZERO;
    }

    /**
     * ✅ NUEVO MÉTODO: Forzar actualización inmediata del stock
     */
    public boolean actualizarStockDirecto(int idProducto, int idAlmacen, BigDecimal nuevoStock) {
        String sqlBuscar = "SELECT COUNT(*) FROM inventario WHERE id_producto = ? AND id_almacen = ?";
        String sqlUpdate = """
        UPDATE inventario 
        SET stock_actual = ?, fecha_actualizacion = CURRENT_TIMESTAMP 
        WHERE id_producto = ? AND id_almacen = ?
    """;
        String sqlInsert = """
        INSERT INTO inventario (id_producto, id_almacen, stock_actual, precio_costo, precio_venta_actual) 
        VALUES (?, ?, ?, 0, 0)
    """;

        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();

            // Verificar si existe el registro
            boolean existe = false;
            try (PreparedStatement pst = conn.prepareStatement(sqlBuscar)) {
                pst.setInt(1, idProducto);
                pst.setInt(2, idAlmacen);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        existe = rs.getInt(1) > 0;
                    }
                }
            }

            // Actualizar o insertar
            if (existe) {
                try (PreparedStatement pst = conn.prepareStatement(sqlUpdate)) {
                    pst.setBigDecimal(1, nuevoStock);
                    pst.setInt(2, idProducto);
                    pst.setInt(3, idAlmacen);

                    int filasAfectadas = pst.executeUpdate();
                    System.out.println("✅ Stock actualizado directamente. Filas afectadas: " + filasAfectadas);
                    return filasAfectadas > 0;
                }
            } else {
                try (PreparedStatement pst = conn.prepareStatement(sqlInsert)) {
                    pst.setInt(1, idProducto);
                    pst.setInt(2, idAlmacen);
                    pst.setBigDecimal(3, nuevoStock);

                    int filasAfectadas = pst.executeUpdate();
                    System.out.println("✅ Nuevo registro de stock insertado. Filas afectadas: " + filasAfectadas);
                    return filasAfectadas > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar stock directo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionBD.cerrarConexion(conn);
        }

        return false;
    }

    // ========================================
    // MÉTODOS DE CONSULTA
    // ========================================
    /**
     * Obtiene todos los movimientos de inventario
     */
    public List<MovimientoInventario> obtenerMovimientos() {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = """
            SELECT m.*, a.nombre as almacen_nombre, u.nombre as usuario_nombre
            FROM movimientos_inventario m
            LEFT JOIN almacenes a ON m.id_almacen = a.id_almacen
            LEFT JOIN usuarios u ON m.id_usuario = u.id_usuario
            ORDER BY m.fecha_movimiento DESC
        """;

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                MovimientoInventario movimiento = mapearMovimiento(rs);
                movimientos.add(movimiento);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Error al cargar movimientos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return movimientos;
    }

    /**
     * Obtiene los detalles de un movimiento
     */
    public List<DetalleMovimientoInventario> obtenerDetallesMovimiento(int idMovimiento) {
        List<DetalleMovimientoInventario> detalles = new ArrayList<>();
        String sql = """
            SELECT d.*, p.codigo, p.nombre as producto_nombre, 
                   c.nombre as categoria_nombre, p.unidad_compra
            FROM detalle_movimientos_inventario d
            INNER JOIN productos p ON d.id_producto = p.id_producto
            LEFT JOIN categorias c ON p.id_categoria = c.id_categoria
            WHERE d.id_movimiento = ?
            ORDER BY p.nombre
        """;

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idMovimiento);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                DetalleMovimientoInventario detalle = mapearDetalleMovimiento(rs);
                detalles.add(detalle);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener detalles del movimiento: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Error al cargar detalles: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return detalles;
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================
    /**
     * Mapea un ResultSet a MovimientoInventario
     */
    private MovimientoInventario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setIdMovimiento(rs.getInt("id_movimiento"));
        movimiento.setNumeroMovimiento(rs.getString("numero_movimiento"));
        movimiento.setTipoMovimiento(rs.getString("tipo_movimiento"));
        movimiento.setIdAlmacen(rs.getInt("id_almacen"));
        movimiento.setNombreAlmacen(rs.getString("almacen_nombre"));

        Timestamp fechaMovimiento = rs.getTimestamp("fecha_movimiento");
        if (fechaMovimiento != null) {
            movimiento.setFechaMovimiento(fechaMovimiento.toLocalDateTime());
        }

        movimiento.setMotivo(rs.getString("motivo"));
        movimiento.setTotalProductos(rs.getInt("total_productos"));
        movimiento.setTotalCantidad(rs.getBigDecimal("total_cantidad"));
        movimiento.setTotalCosto(rs.getBigDecimal("total_costo"));
        movimiento.setEstado(rs.getString("estado"));
        movimiento.setIdUsuario(rs.getInt("id_usuario"));
        movimiento.setNombreUsuario(rs.getString("usuario_nombre"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            movimiento.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        return movimiento;
    }

    /**
     * Mapea un ResultSet a DetalleMovimientoInventario
     */
    private DetalleMovimientoInventario mapearDetalleMovimiento(ResultSet rs) throws SQLException {
        DetalleMovimientoInventario detalle = new DetalleMovimientoInventario();

        detalle.setIdDetalleMovimiento(rs.getInt("id_detalle_movimiento"));
        detalle.setIdMovimiento(rs.getInt("id_movimiento"));
        detalle.setIdProducto(rs.getInt("id_producto"));
        detalle.setCantidad(rs.getBigDecimal("cantidad"));
        detalle.setCostoUnitario(rs.getBigDecimal("costo_unitario"));
        detalle.setPrecioVenta1(rs.getBigDecimal("precio_venta_1"));
        detalle.setPrecioVenta2(rs.getBigDecimal("precio_venta_2"));
        detalle.setPrecioVenta3(rs.getBigDecimal("precio_venta_3"));
        detalle.setSubtotal(rs.getBigDecimal("subtotal"));
        detalle.setStockAnterior(rs.getBigDecimal("stock_anterior"));
        detalle.setStockNuevo(rs.getBigDecimal("stock_nuevo"));

        // Información del producto
        detalle.setCodigoProducto(rs.getString("codigo"));
        detalle.setNombreProducto(rs.getString("producto_nombre"));
        detalle.setCategoriaProducto(rs.getString("categoria_nombre"));
        detalle.setUnidadMedida(rs.getString("unidad_compra"));

        return detalle;
    }

    // ========================================
    // MÉTODOS ADICIONALES ÚTILES
    // ========================================
    /**
     * Obtiene movimientos por almacén
     */
    public List<MovimientoInventario> obtenerMovimientosPorAlmacen(int idAlmacen) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = """
            SELECT m.*, a.nombre as almacen_nombre, u.nombre as usuario_nombre
            FROM movimientos_inventario m
            LEFT JOIN almacenes a ON m.id_almacen = a.id_almacen
            LEFT JOIN usuarios u ON m.id_usuario = u.id_usuario
            WHERE m.id_almacen = ?
            ORDER BY m.fecha_movimiento DESC
        """;

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idAlmacen);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                MovimientoInventario movimiento = mapearMovimiento(rs);
                movimientos.add(movimiento);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos por almacén: " + e.getMessage());
        }

        return movimientos;
    }

    /**
     * Obtiene movimientos por tipo
     */
    public List<MovimientoInventario> obtenerMovimientosPorTipo(String tipoMovimiento) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = """
            SELECT m.*, a.nombre as almacen_nombre, u.nombre as usuario_nombre
            FROM movimientos_inventario m
            LEFT JOIN almacenes a ON m.id_almacen = a.id_almacen
            LEFT JOIN usuarios u ON m.id_usuario = u.id_usuario
            WHERE m.tipo_movimiento = ?
            ORDER BY m.fecha_movimiento DESC
        """;

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, tipoMovimiento);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                MovimientoInventario movimiento = mapearMovimiento(rs);
                movimientos.add(movimiento);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimientos por tipo: " + e.getMessage());
        }

        return movimientos;
    }

    /**
     * Obtiene un movimiento por ID
     */
    public MovimientoInventario obtenerMovimientoPorId(int idMovimiento) {
        String sql = """
            SELECT m.*, a.nombre as almacen_nombre, u.nombre as usuario_nombre
            FROM movimientos_inventario m
            LEFT JOIN almacenes a ON m.id_almacen = a.id_almacen
            LEFT JOIN usuarios u ON m.id_usuario = u.id_usuario
            WHERE m.id_movimiento = ?
        """;

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idMovimiento);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return mapearMovimiento(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener movimiento por ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Cancela un movimiento (cambia estado a CANCELADO)
     */
    public boolean cancelarMovimiento(int idMovimiento, String motivo) {
        String sql = "UPDATE movimientos_inventario SET estado = 'CANCELADO', motivo = ? WHERE id_movimiento = ?";

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, motivo);
            pst.setInt(2, idMovimiento);

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Movimiento cancelado exitosamente");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al cancelar movimiento: " + e.getMessage());
        }

        return false;
    }

    /**
     * Obtiene resumen de inventario por almacén
     */
    public List<Object[]> obtenerResumenInventarioPorAlmacen(int idAlmacen) {
        List<Object[]> resumen = new ArrayList<>();
        String sql = """
            SELECT p.codigo, p.nombre, c.nombre as categoria, 
                   COALESCE(i.stock_actual, 0) as stock,
                   COALESCE(i.precio_costo, 0) as costo,
                   COALESCE(i.precio_venta_actual, 0) as precio_venta
            FROM productos p
            LEFT JOIN categorias c ON p.id_categoria = c.id_categoria
            LEFT JOIN inventario i ON p.id_producto = i.id_producto AND i.id_almacen = ?
            WHERE p.id_almacen = ? AND p.estado = true
            ORDER BY p.nombre
        """;

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idAlmacen);
            pst.setInt(2, idAlmacen);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getBigDecimal("stock"),
                    rs.getBigDecimal("costo"),
                    rs.getBigDecimal("precio_venta")
                };
                resumen.add(fila);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener resumen de inventario: " + e.getMessage());
        }

        return resumen;
    }
    
}
