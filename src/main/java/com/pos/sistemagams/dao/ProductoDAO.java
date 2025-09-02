package com.pos.sistemagams.dao;

import com.pos.sistemagams.modelo.*;
import com.pos.sistemagams.util.ConexionBD;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * DAO para manejo de productos y datos relacionados
 * @author Diego
 */
public class ProductoDAO {
    
    // ========================================
    // MÉTODOS PARA PRODUCTOS
    // ========================================
    
    /**
     * Genera un código único para el producto
     */
    public String generarCodigoUnico() {
        String codigo;
        boolean existe;
        Random random = new Random();
        
        do {
            // Generar código de 8 dígitos
            codigo = String.format("%08d", random.nextInt(100000000));
            existe = verificarCodigoExiste(codigo);
        } while (existe);
        
        return codigo;
    }
    
    /**
     * Verifica si un código ya existe en la base de datos
     */
    private boolean verificarCodigoExiste(String codigo) {
        String sql = "SELECT COUNT(*) FROM productos WHERE codigo = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, codigo);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar código: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Guarda un nuevo producto en la base de datos
     */
    public boolean guardarProducto(Producto producto) {
        String sql = """
            INSERT INTO productos (
                codigo, nombre, precio_compra, precio_venta_1, 
                precio_venta_2, precio_venta_3, precio_mayoreo, cantidad_mayoreo,
                aplica_igv, porcentaje_igv, stock_minimo, stock_maximo, 
                unidad_compra, id_categoria, id_proveedor, id_departamento, 
                id_almacen, imagen_path, estado
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, producto.getCodigo());
            pst.setString(2, producto.getNombre());
            pst.setBigDecimal(3, producto.getPrecioCompra());
            pst.setBigDecimal(4, producto.getPrecioVenta1());
            pst.setBigDecimal(5, producto.getPrecioVenta2());
            pst.setBigDecimal(6, producto.getPrecioVenta3());
            pst.setBigDecimal(7, producto.getPrecioMayoreo());
            pst.setInt(8, producto.getCantidadMayoreo());
            pst.setBoolean(9, producto.isAplicaIgv());
            pst.setBigDecimal(10, producto.getPorcentajeIgv());
            pst.setInt(11, producto.getStockMinimo());
            pst.setInt(12, producto.getStockMaximo());
            pst.setString(13, producto.getUnidadCompra());
            
            // Manejar valores nulos para las FK
            if (producto.getIdCategoria() > 0) {
                pst.setInt(14, producto.getIdCategoria());
            } else {
                pst.setNull(14, Types.INTEGER);
            }
            
            if (producto.getIdProveedor() > 0) {
                pst.setInt(15, producto.getIdProveedor());
            } else {
                pst.setNull(15, Types.INTEGER);
            }
            
            if (producto.getIdDepartamento() > 0) {
                pst.setInt(16, producto.getIdDepartamento());
            } else {
                pst.setNull(16, Types.INTEGER);
            }
            
            if (producto.getIdAlmacen() > 0) {
                pst.setInt(17, producto.getIdAlmacen());
            } else {
                pst.setNull(17, Types.INTEGER);
            }
            
            pst.setString(18, producto.getImagenPath());
            pst.setBoolean(19, producto.isEstado());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    producto.setIdProducto(rs.getInt(1));
                }
                
                // Recargar el producto completo desde la BD para obtener la fecha_creacion
                Producto productoCompleto = obtenerProductoPorId(producto.getIdProducto());
                if (productoCompleto != null && productoCompleto.getFechaCreacion() != null) {
                    producto.setFechaCreacion(productoCompleto.getFechaCreacion());
                }
                
                System.out.println("✅ Producto guardado exitosamente con ID: " + producto.getIdProducto());
                System.out.println("✅ Fecha de creación: " + producto.getFechaCreacion());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al guardar el producto: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Obtiene todos los productos ACTIVOS (estado = true)
     */
    public List<Producto> obtenerTodosLosProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, c.nombre as categoria_nombre, pr.nombre as proveedor_nombre,
                   d.nombre as departamento_nombre, a.nombre as almacen_nombre
            FROM productos p
            LEFT JOIN categorias c ON p.id_categoria = c.id_categoria
            LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor
            LEFT JOIN departamentos d ON p.id_departamento = d.id_departamento
            LEFT JOIN almacenes a ON p.id_almacen = a.id_almacen
            WHERE p.estado = true
            ORDER BY p.nombre
        """;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Producto producto = mapearProducto(rs);
                productos.add(producto);
            }
            
            System.out.println("✅ Cargados " + productos.size() + " productos activos");
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al cargar productos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return productos;
    }
    
    /**
     * Obtiene un producto por su ID
     */
    public Producto obtenerProductoPorId(int idProducto) {
        String sql = """
            SELECT p.*, c.nombre as categoria_nombre, pr.nombre as proveedor_nombre,
                   d.nombre as departamento_nombre, a.nombre as almacen_nombre
            FROM productos p
            LEFT JOIN categorias c ON p.id_categoria = c.id_categoria
            LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor
            LEFT JOIN departamentos d ON p.id_departamento = d.id_departamento
            LEFT JOIN almacenes a ON p.id_almacen = a.id_almacen
            WHERE p.id_producto = ? AND p.estado = true
        """;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idProducto);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearProducto(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al obtener producto: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return null;
    }
    
    /**
     * Obtiene un producto por su código
     */
    public Producto obtenerProductoPorCodigo(String codigo) {
        String sql = """
            SELECT p.*, c.nombre as categoria_nombre, pr.nombre as proveedor_nombre,
                   d.nombre as departamento_nombre, a.nombre as almacen_nombre
            FROM productos p
            LEFT JOIN categorias c ON p.id_categoria = c.id_categoria
            LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor
            LEFT JOIN departamentos d ON p.id_departamento = d.id_departamento
            LEFT JOIN almacenes a ON p.id_almacen = a.id_almacen
            WHERE p.codigo = ? AND p.estado = true
        """;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, codigo);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearProducto(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por código: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al obtener producto: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return null;
    }
    
    /**
     * Actualiza un producto existente
     */
    public boolean actualizarProducto(Producto producto) {
        String sql = """
            UPDATE productos SET 
                nombre = ?, precio_compra = ?, precio_venta_1 = ?, 
                precio_venta_2 = ?, precio_venta_3 = ?, precio_mayoreo = ?, 
                cantidad_mayoreo = ?, aplica_igv = ?, porcentaje_igv = ?, 
                stock_minimo = ?, stock_maximo = ?, unidad_compra = ?, 
                id_categoria = ?, id_proveedor = ?, id_departamento = ?, 
                id_almacen = ?, imagen_path = ?
            WHERE id_producto = ?
        """;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, producto.getNombre());
            pst.setBigDecimal(2, producto.getPrecioCompra());
            pst.setBigDecimal(3, producto.getPrecioVenta1());
            pst.setBigDecimal(4, producto.getPrecioVenta2());
            pst.setBigDecimal(5, producto.getPrecioVenta3());
            pst.setBigDecimal(6, producto.getPrecioMayoreo());
            pst.setInt(7, producto.getCantidadMayoreo());
            pst.setBoolean(8, producto.isAplicaIgv());
            pst.setBigDecimal(9, producto.getPorcentajeIgv());
            pst.setInt(10, producto.getStockMinimo());
            pst.setInt(11, producto.getStockMaximo());
            pst.setString(12, producto.getUnidadCompra());
            
            // Manejar valores nulos para las FK
            if (producto.getIdCategoria() > 0) {
                pst.setInt(13, producto.getIdCategoria());
            } else {
                pst.setNull(13, Types.INTEGER);
            }
            
            if (producto.getIdProveedor() > 0) {
                pst.setInt(14, producto.getIdProveedor());
            } else {
                pst.setNull(14, Types.INTEGER);
            }
            
            if (producto.getIdDepartamento() > 0) {
                pst.setInt(15, producto.getIdDepartamento());
            } else {
                pst.setNull(15, Types.INTEGER);
            }
            
            if (producto.getIdAlmacen() > 0) {
                pst.setInt(16, producto.getIdAlmacen());
            } else {
                pst.setNull(16, Types.INTEGER);
            }
            
            pst.setString(17, producto.getImagenPath());
            pst.setInt(18, producto.getIdProducto());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Producto actualizado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al actualizar el producto: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Elimina un producto (cambiar estado a false) - SOLO si no tiene stock
     */
    public boolean eliminarProducto(int idProducto) {
        // Primero verificar el stock
        int stockActual = obtenerStockProducto(idProducto);
        if (stockActual > 0) {
            System.out.println("❌ No se puede eliminar producto con stock: " + stockActual);
            return false; // No se puede eliminar si tiene stock
        }
        
        String sql = "UPDATE productos SET estado = false WHERE id_producto = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idProducto);
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Producto eliminado exitosamente (estado = false)");
                return true;
            } else {
                System.out.println("❌ No se encontró el producto para eliminar");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al eliminar el producto: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Obtiene el stock actual de un producto
     */
    public int obtenerStockProducto(int idProducto) {
        String sql = """
            SELECT COALESCE(SUM(stock_actual), 0) as stock_total 
            FROM inventario 
            WHERE id_producto = ?
        """;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idProducto);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("stock_total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener stock del producto: " + e.getMessage());
        }
        
        return 0;
    }
    
    // ========================================
    // MÉTODOS PARA CATEGORÍAS
    // ========================================
    
    /**
     * Obtiene todas las categorías activas
     */
    public List<Categoria> obtenerCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre, descripcion FROM categorias WHERE estado = true ORDER BY nombre";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("id_categoria"));
                categoria.setNombre(rs.getString("nombre"));
                categoria.setDescripcion(rs.getString("descripcion"));
                categorias.add(categoria);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al cargar categorías: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return categorias;
    }
    
    /**
     * Guarda una nueva categoría en la base de datos
     */
    public boolean guardarCategoria(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre, descripcion, estado) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, categoria.getNombre());
            pst.setString(2, categoria.getDescripcion());
            pst.setBoolean(3, categoria.isEstado());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
                System.out.println("✅ Categoría guardada exitosamente con ID: " + categoria.getIdCategoria());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar categoría: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al guardar la categoría: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Actualiza una categoría existente
     */
    public boolean actualizarCategoria(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ? WHERE id_categoria = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, categoria.getNombre());
            pst.setString(2, categoria.getNombre()); // Usar el mismo nombre como descripción
            pst.setInt(3, categoria.getIdCategoria());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Categoría actualizada exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar categoría: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al actualizar la categoría: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Elimina una categoría (cambiar estado a false)
     */
    public boolean eliminarCategoria(int idCategoria) {
        String sql = "UPDATE categorias SET estado = false WHERE id_categoria = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idCategoria);
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Categoría eliminada exitosamente (estado = false)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar categoría: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al eliminar la categoría: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    // ========================================
    // MÉTODOS PARA ALMACENES
    // ========================================
    
    /**
     * Obtiene todos los almacenes activos
     */
    public List<Almacen> obtenerAlmacenes() {
        List<Almacen> almacenes = new ArrayList<>();
        String sql = "SELECT id_almacen, nombre, descripcion, responsable, direccion FROM almacenes WHERE estado = true ORDER BY nombre";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Almacen almacen = new Almacen();
                almacen.setIdAlmacen(rs.getInt("id_almacen"));
                almacen.setNombre(rs.getString("nombre"));
                almacen.setDescripcion(rs.getString("descripcion"));
                almacen.setResponsable(rs.getString("responsable"));
                almacen.setDireccion(rs.getString("direccion"));
                almacenes.add(almacen);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener almacenes: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al cargar almacenes: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return almacenes;
    }
    
    /**
     * Guarda un nuevo almacén en la base de datos
     */
    public boolean guardarAlmacen(Almacen almacen) {
        String sql = "INSERT INTO almacenes (nombre, descripcion, responsable, direccion, estado) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, almacen.getNombre());
            pst.setString(2, almacen.getDescripcion());
            pst.setString(3, almacen.getResponsable());
            pst.setString(4, almacen.getDireccion());
            pst.setBoolean(5, almacen.isEstado());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    almacen.setIdAlmacen(rs.getInt(1));
                }
                System.out.println("✅ Almacén guardado exitosamente con ID: " + almacen.getIdAlmacen());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar almacén: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al guardar el almacén: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    
    
    /**
     * Actualiza un almacén existente
     */
    public boolean actualizarAlmacen(Almacen almacen) {
        String sql = "UPDATE almacenes SET nombre = ?, descripcion = ?, responsable = ?, direccion = ? WHERE id_almacen = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, almacen.getNombre());
            pst.setString(2, almacen.getNombre()); // Usar el mismo nombre como descripción
            pst.setString(3, almacen.getResponsable());
            pst.setString(4, almacen.getDireccion());
            pst.setInt(5, almacen.getIdAlmacen());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Almacén actualizado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar almacén: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al actualizar el almacén: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Elimina un almacén (cambiar estado a false)
     */
    public boolean eliminarAlmacen(int idAlmacen) {
        String sql = "UPDATE almacenes SET estado = false WHERE id_almacen = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idAlmacen);
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Almacén eliminado exitosamente (estado = false)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar almacén: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al eliminar el almacén: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    // ========================================
    // MÉTODOS PARA DEPARTAMENTOS
    // ========================================
    
    /**
     * Obtiene todos los departamentos activos
     */
    public List<Departamento> obtenerDepartamentos() {
        List<Departamento> departamentos = new ArrayList<>();
        String sql = """
            SELECT d.id_departamento, d.nombre, d.descripcion, d.id_almacen, 
                   a.nombre as almacen_nombre
            FROM departamentos d
            LEFT JOIN almacenes a ON d.id_almacen = a.id_almacen
            WHERE d.estado = true 
            ORDER BY a.nombre, d.nombre
        """;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Departamento departamento = new Departamento();
                departamento.setIdDepartamento(rs.getInt("id_departamento"));
                departamento.setNombre(rs.getString("nombre"));
                departamento.setDescripcion(rs.getString("descripcion"));
                departamento.setIdAlmacen(rs.getInt("id_almacen"));
                departamento.setNombreAlmacen(rs.getString("almacen_nombre"));
                departamentos.add(departamento);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener departamentos: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al cargar departamentos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return departamentos;
    }
    
    /**
     * Guarda un nuevo departamento en la base de datos
     */
    public boolean guardarDepartamento(Departamento departamento) {
        String sql = "INSERT INTO departamentos (nombre, descripcion, id_almacen, estado) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, departamento.getNombre());
            pst.setString(2, departamento.getDescripcion());
            
            if (departamento.getIdAlmacen() > 0) {
                pst.setInt(3, departamento.getIdAlmacen());
            } else {
                pst.setNull(3, Types.INTEGER);
            }
            
            pst.setBoolean(4, departamento.isEstado());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    departamento.setIdDepartamento(rs.getInt(1));
                }
                System.out.println("✅ Departamento guardado exitosamente con ID: " + departamento.getIdDepartamento());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar departamento: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al guardar el departamento: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Actualiza un departamento existente
     */
    public boolean actualizarDepartamento(Departamento departamento) {
        String sql = "UPDATE departamentos SET nombre = ?, descripcion = ?, id_almacen = ? WHERE id_departamento = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, departamento.getNombre());
            pst.setString(2, departamento.getNombre()); // Usar el mismo nombre como descripción
            
            if (departamento.getIdAlmacen() > 0) {
                pst.setInt(3, departamento.getIdAlmacen());
            } else {
                pst.setNull(3, Types.INTEGER);
            }
            
            pst.setInt(4, departamento.getIdDepartamento());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Departamento actualizado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar departamento: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al actualizar el departamento: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Obtiene departamentos por almacén específico
     */
    public List<Departamento> obtenerDepartamentosPorAlmacen(int idAlmacen) {
        List<Departamento> departamentos = new ArrayList<>();
        String sql = """
            SELECT d.id_departamento, d.nombre, d.descripcion, d.id_almacen, 
                   a.nombre as almacen_nombre
            FROM departamentos d
            LEFT JOIN almacenes a ON d.id_almacen = a.id_almacen
            WHERE d.estado = true AND d.id_almacen = ?
            ORDER BY d.nombre
        """;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idAlmacen);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Departamento departamento = new Departamento();
                departamento.setIdDepartamento(rs.getInt("id_departamento"));
                departamento.setNombre(rs.getString("nombre"));
                departamento.setDescripcion(rs.getString("descripcion"));
                departamento.setIdAlmacen(rs.getInt("id_almacen"));
                departamento.setNombreAlmacen(rs.getString("almacen_nombre"));
                departamentos.add(departamento);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener departamentos por almacén: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al cargar departamentos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return departamentos;
    }
    
    /**
     * Elimina un departamento (cambiar estado a false)
     */
    public boolean eliminarDepartamento(int idDepartamento) {
        String sql = "UPDATE departamentos SET estado = false WHERE id_departamento = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idDepartamento);
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Departamento eliminado exitosamente (estado = false)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar departamento: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al eliminar el departamento: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    // ========================================
    // MÉTODOS PARA PROVEEDORES
    // ========================================
    
    /**
     * Obtiene todos los proveedores activos
     */
    public List<Proveedor> obtenerProveedores() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id_proveedor, nombre, ruc, direccion, telefono, email FROM proveedores WHERE estado = true ORDER BY nombre";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Proveedor proveedor = new Proveedor();
                proveedor.setIdProveedor(rs.getInt("id_proveedor"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setRuc(rs.getString("ruc"));
                proveedor.setDireccion(rs.getString("direccion"));
                proveedor.setTelefono(rs.getString("telefono"));
                proveedor.setEmail(rs.getString("email"));
                proveedores.add(proveedor);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al cargar proveedores: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return proveedores;
    }
    
    /**
     * Guarda un nuevo proveedor en la base de datos
     */
    public boolean guardarProveedor(Proveedor proveedor) {
        String sql = "INSERT INTO proveedores (nombre, ruc, direccion, telefono, email, estado) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, proveedor.getNombre());
            pst.setString(2, proveedor.getRuc());
            pst.setString(3, proveedor.getDireccion());
            pst.setString(4, proveedor.getTelefono());
            pst.setString(5, proveedor.getEmail());
            pst.setBoolean(6, proveedor.isEstado());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    proveedor.setIdProveedor(rs.getInt(1));
                }
                System.out.println("✅ Proveedor guardado exitosamente con ID: " + proveedor.getIdProveedor());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar proveedor: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al guardar el proveedor: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Actualiza un proveedor existente
     */
    public boolean actualizarProveedor(Proveedor proveedor) {
        String sql = "UPDATE proveedores SET nombre = ?, ruc = ?, direccion = ?, telefono = ?, email = ? WHERE id_proveedor = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, proveedor.getNombre());
            pst.setString(2, proveedor.getRuc());
            pst.setString(3, proveedor.getDireccion());
            pst.setString(4, proveedor.getTelefono());
            pst.setString(5, proveedor.getEmail());
            pst.setInt(6, proveedor.getIdProveedor());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Proveedor actualizado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar proveedor: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al actualizar el proveedor: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Elimina un proveedor (cambiar estado a false)
     */
    public boolean eliminarProveedor(int idProveedor) {
        String sql = "UPDATE proveedores SET estado = false WHERE id_proveedor = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idProveedor);
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Proveedor eliminado exitosamente (estado = false)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar proveedor: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error al eliminar el proveedor: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Obtiene productos por almacén específico
     */
    public List<Producto> obtenerProductosPorAlmacen(int idAlmacen) {
        List<Producto> productos = new ArrayList<>();
        String sql = """
            SELECT p.*, c.nombre as categoria_nombre, pr.nombre as proveedor_nombre,
                   d.nombre as departamento_nombre, a.nombre as almacen_nombre
            FROM productos p
            LEFT JOIN categorias c ON p.id_categoria = c.id_categoria
            LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor
            LEFT JOIN departamentos d ON p.id_departamento = d.id_departamento
            LEFT JOIN almacenes a ON p.id_almacen = a.id_almacen
            WHERE p.id_almacen = ? AND p.estado = true
            ORDER BY p.nombre
        """;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idAlmacen);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Producto producto = mapearProducto(rs);
                productos.add(producto);
            }
            
            System.out.println("✅ Cargados " + productos.size() + " productos para el almacén ID: " + idAlmacen);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos por almacén: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error al cargar productos del almacén: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return productos;
    }
    
    /**
 * Obtiene los almacenes donde un producto tiene movimientos de inventario
 */
public List<Integer> obtenerAlmacenesDelProducto(int idProducto) {
    List<Integer> almacenes = new ArrayList<>();
    String sql = """
        SELECT DISTINCT dmi.id_almacen 
        FROM detalle_movimientos_inventario dmi
        INNER JOIN movimientos_inventario mi ON dmi.id_movimiento = mi.id_movimiento
        WHERE dmi.id_producto = ? AND mi.estado = 'COMPLETADO'
        ORDER BY dmi.id_almacen
    """;
    
    try (Connection conn = ConexionBD.obtenerConexion();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setInt(1, idProducto);
        
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                almacenes.add(rs.getInt("id_almacen"));
            }
        }
        
        System.out.println("✅ Almacenes encontrados para producto " + idProducto + ": " + almacenes.size());
        
    } catch (SQLException e) {
        System.err.println("Error al obtener almacenes del producto: " + e.getMessage());
        e.printStackTrace();
    }
    
    return almacenes;
}
    
    
    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================
    
    /**
     * Mapea un ResultSet a un objeto Producto
     */
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        
        producto.setIdProducto(rs.getInt("id_producto"));
        producto.setCodigo(rs.getString("codigo"));
        producto.setNombre(rs.getString("nombre"));
        producto.setPrecioCompra(rs.getBigDecimal("precio_compra"));
        producto.setPrecioVenta1(rs.getBigDecimal("precio_venta_1"));
        producto.setPrecioVenta2(rs.getBigDecimal("precio_venta_2"));
        producto.setPrecioVenta3(rs.getBigDecimal("precio_venta_3"));
        producto.setPrecioMayoreo(rs.getBigDecimal("precio_mayoreo"));
        producto.setCantidadMayoreo(rs.getInt("cantidad_mayoreo"));
        producto.setAplicaIgv(rs.getBoolean("aplica_igv"));
        producto.setPorcentajeIgv(rs.getBigDecimal("porcentaje_igv"));
        producto.setStockMinimo(rs.getInt("stock_minimo"));
        producto.setStockMaximo(rs.getInt("stock_maximo"));
        producto.setUnidadCompra(rs.getString("unidad_compra"));
        producto.setIdCategoria(rs.getInt("id_categoria"));
        producto.setIdProveedor(rs.getInt("id_proveedor"));
        producto.setIdDepartamento(rs.getInt("id_departamento"));
        producto.setIdAlmacen(rs.getInt("id_almacen"));
        producto.setImagenPath(rs.getString("imagen_path"));
        producto.setEstado(rs.getBoolean("estado"));
        
        // MAPEAR LA FECHA DE CREACIÓN
        Timestamp fechaCreacionBD = rs.getTimestamp("fecha_creacion");
        if (fechaCreacionBD != null) {
            producto.setFechaCreacion(fechaCreacionBD.toLocalDateTime());
            System.out.println("✅ Fecha mapeada: " + fechaCreacionBD.toLocalDateTime());
        } else {
            System.out.println("⚠️ No hay fecha de creación en la BD");
        }
        
        // Nombres para mostrar
        producto.setNombreCategoria(rs.getString("categoria_nombre"));
        producto.setNombreProveedor(rs.getString("proveedor_nombre"));
        producto.setNombreDepartamento(rs.getString("departamento_nombre"));
        producto.setNombreAlmacen(rs.getString("almacen_nombre"));
        
        return producto;
    }
}