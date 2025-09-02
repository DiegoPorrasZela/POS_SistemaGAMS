/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pos.sistemagams.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.List;
import java.math.BigDecimal;
import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.dao.MovimientoInventarioDAO;
import com.pos.sistemagams.modelo.Producto;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
/**
 *
 * @author Diego
 */
public class PanelInventarioMinimo extends javax.swing.JPanel implements InventarioUpdateListener {
    
    private ProductoDAO productoDAO;
    private MovimientoInventarioDAO movimientoDAO;
    private DefaultTableModel modeloTabla;
    
    /**
     * Creates new form PanelInventarioMinimo
     */
    public PanelInventarioMinimo() {
        initComponents();
        inicializarComponentes();
        
        // ✅ REGISTRAR ESTE PANEL PARA RECIBIR NOTIFICACIONES
        InventarioUpdateManager.getInstance().addListener(this);
        System.out.println("✅ PanelInventarioMinimo registrado para notificaciones");
    }

    private void inicializarComponentes() {
        productoDAO = new ProductoDAO();
        movimientoDAO = new MovimientoInventarioDAO();

        // Configurar modelo de tabla
        String[] columnas = {
            "CLAVE", "NOMBRE DEL PRODUCTO/DESCRIPCION", "CATEGORIA", "PROVEEDOR",
            "PUESTO/ALMACEN/GIRO", "COSTO", "PRECIO", "U.M", 
            "EXISTENCIA MINIMA", "EXISTENCIA ACTUAL", "ESTADO"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };

        // Asignar modelo a la tabla
        jTable1.setModel(modeloTabla);

        // Configurar ancho de columnas
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);  // CLAVE
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);  // DESCRIPCION
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);  // CATEGORIA
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);  // PROVEEDOR
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(200);  // ALMACEN
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);   // COSTO
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(80);   // PRECIO
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(60);   // U.M
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(120);  // EXISTENCIA MINIMA
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(120);  // EXISTENCIA ACTUAL
            jTable1.getColumnModel().getColumn(10).setPreferredWidth(100); // ESTADO
        }

        // Configurar renderizadores
        configurarRenderizadores();

        // Cargar datos iniciales
        cargarProductosInventarioMinimo();
    }

    private void configurarRenderizadores() {
        // Renderer para números alineados a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        jTable1.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // COSTO
        jTable1.getColumnModel().getColumn(6).setCellRenderer(rightRenderer); // PRECIO
        jTable1.getColumnModel().getColumn(8).setCellRenderer(rightRenderer); // EXISTENCIA MINIMA
        jTable1.getColumnModel().getColumn(9).setCellRenderer(rightRenderer); // EXISTENCIA ACTUAL

        // Renderer especial para la columna ESTADO con colores
        DefaultTableCellRenderer estadoRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 10 && value != null) { // Columna ESTADO
                    String estado = value.toString();
                    
                    if (!isSelected) {
                        switch (estado) {
                            case "🔴 CRÍTICO":
                                c.setBackground(new Color(255, 235, 235)); // Fondo rojo claro
                                c.setForeground(new Color(139, 0, 0)); // Texto rojo oscuro
                                break;
                            case "🟡 BAJO":
                                c.setBackground(new Color(255, 255, 235)); // Fondo amarillo claro
                                c.setForeground(new Color(184, 134, 11)); // Texto amarillo oscuro
                                break;
                            case "⚫ SIN STOCK":
                                c.setBackground(new Color(240, 240, 240)); // Fondo gris claro
                                c.setForeground(Color.BLACK); // Texto negro
                                break;
                            default:
                                c.setBackground(Color.WHITE);
                                c.setForeground(Color.BLACK);
                        }
                    }
                    
                    setFont(getFont().deriveFont(Font.BOLD));
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    if (!isSelected) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return c;
            }
        };

        jTable1.getColumnModel().getColumn(10).setCellRenderer(estadoRenderer);

        // Renderer para colorear filas según el estado
        DefaultTableCellRenderer filaRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && column != 10) { // No aplicar a la columna ESTADO
                    String estado = (String) table.getValueAt(row, 10);
                    
                    switch (estado) {
                        case "🔴 CRÍTICO":
                            c.setBackground(new Color(255, 245, 245)); // Fondo muy claro rojo
                            break;
                        case "🟡 BAJO":
                            c.setBackground(new Color(255, 255, 245)); // Fondo muy claro amarillo
                            break;
                        case "⚫ SIN STOCK":
                            c.setBackground(new Color(248, 248, 248)); // Fondo muy claro gris
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                }
                
                // Alineación según la columna
                if (column == 5 || column == 6 || column == 8 || column == 9) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return c;
            }
        };

        // Aplicar renderer a todas las columnas excepto ESTADO
        for (int i = 0; i < 10; i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(filaRenderer);
        }
    }

    public void cargarProductosInventarioMinimo() {
        try {
            System.out.println("🔄 Cargando productos que necesitan reabastecimiento...");

            // Limpiar tabla
            modeloTabla.setRowCount(0);

            // Obtener todos los productos
            List<Producto> productos = productoDAO.obtenerTodosLosProductos();
            System.out.println("📦 Productos obtenidos: " + productos.size());

            // ✅ NUEVA ESTRUCTURA: Separar productos por prioridad
            List<Object[]> productosCriticos = new ArrayList<>();
            List<Object[]> productosSinStock = new ArrayList<>();
            List<Object[]> productosBajos = new ArrayList<>();

            // Procesar cada producto
            for (Producto producto : productos) {
                // Solo procesar productos que tienen stock mínimo configurado (mayor a 0)
                if (producto.getStockMinimo() > 0) {
                    
                    // Obtener stock actual
                    BigDecimal stockActual = BigDecimal.ZERO;
                    
                    if (producto.getIdAlmacen() > 0) {
                        // Si el producto tiene almacén asignado
                        stockActual = movimientoDAO.obtenerStockActual(
                            producto.getIdProducto(), producto.getIdAlmacen());
                    } else {
                        // Si no tiene almacén específico, sumar stock de todos los almacenes
                        List<com.pos.sistemagams.modelo.Almacen> almacenes = productoDAO.obtenerAlmacenes();
                        for (com.pos.sistemagams.modelo.Almacen almacen : almacenes) {
                            BigDecimal stockAlmacen = movimientoDAO.obtenerStockActual(
                                producto.getIdProducto(), almacen.getIdAlmacen());
                            stockActual = stockActual.add(stockAlmacen);
                        }
                    }

                    // Determinar estado del stock
                    String estado = determinarEstadoStock(stockActual, producto.getStockMinimo());

                    // ✅ FILTRO PRINCIPAL: Solo incluir productos que necesitan reabastecimiento
                    if (!estado.equals("🟢 NORMAL")) {
                        
                        // Crear fila
                        Object[] fila = {
                            producto.getCodigo(),
                            producto.getNombre(),
                            producto.getNombreCategoria() != null ? producto.getNombreCategoria() : "",
                            producto.getNombreProveedor() != null ? producto.getNombreProveedor() : "",
                            producto.getNombreAlmacen() != null ? producto.getNombreAlmacen() : "",
                            producto.getPrecioCompra(),
                            producto.getPrecioVenta1(),
                            producto.getUnidadCompra(),
                            producto.getStockMinimo(),
                            stockActual,
                            estado
                        };

                        // ✅ CLASIFICAR POR PRIORIDAD DE REABASTECIMIENTO
                        switch (estado) {
                            case "🔴 CRÍTICO":
                                productosCriticos.add(fila);
                                break;
                            case "⚫ SIN STOCK":
                                productosSinStock.add(fila);
                                break;
                            case "🟡 BAJO":
                                productosBajos.add(fila);
                                break;
                        }
                    }
                }
            }

            // ✅ AGREGAR A LA TABLA EN ORDEN DE PRIORIDAD
            // 1. Primero productos SIN STOCK (máxima prioridad)
            for (Object[] fila : productosSinStock) {
                modeloTabla.addRow(fila);
            }
            
            // 2. Segundo productos CRÍTICOS
            for (Object[] fila : productosCriticos) {
                modeloTabla.addRow(fila);
            }
            
            // 3. Tercero productos BAJOS
            for (Object[] fila : productosBajos) {
                modeloTabla.addRow(fila);
            }

            int totalReabastecimiento = productosCriticos.size() + productosSinStock.size() + productosBajos.size();
            
            System.out.println("✅ Productos que necesitan reabastecimiento:");
            System.out.println("   ⚫ Sin stock: " + productosSinStock.size());
            System.out.println("   🔴 Críticos: " + productosCriticos.size());
            System.out.println("   🟡 Bajos: " + productosBajos.size());
            System.out.println("   📊 Total: " + totalReabastecimiento);

            // ✅ MOSTRAR MENSAJE SI NO HAY PRODUCTOS PARA REABASTECER
            if (totalReabastecimiento == 0) {
                // Agregar fila informativa
                Object[] filaVacia = {
                    "", "✅ ¡Excelente! Todos los productos tienen stock adecuado", 
                    "", "", "", "", "", "", "", "", "🟢 NORMAL"
                };
                modeloTabla.addRow(filaVacia);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al cargar inventario mínimo: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar inventario mínimo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método que acepta BigDecimal y int
    private String determinarEstadoStock(BigDecimal stockActual, int stockMinimo) {
        BigDecimal stockMinimoBD = new BigDecimal(stockMinimo);
        
        if (stockActual.compareTo(BigDecimal.ZERO) == 0) {
            return "⚫ SIN STOCK";
        } else if (stockActual.compareTo(stockMinimoBD) < 0) {
            return "🔴 CRÍTICO";
        } else if (stockActual.compareTo(stockMinimoBD.multiply(new BigDecimal("1.5"))) <= 0) {
            return "🟡 BAJO";
        } else {
            return "🟢 NORMAL";
        }
    }

    /**
     * ✅ NUEVO MÉTODO: Obtener resumen de reabastecimiento
     */
    public void mostrarResumenReabastecimiento() {
        try {
            int totalFilas = modeloTabla.getRowCount();
            
            if (totalFilas == 0 || (totalFilas == 1 && modeloTabla.getValueAt(0, 1).toString().contains("Excelente"))) {
                JOptionPane.showMessageDialog(this,
                    "🎉 ¡Felicitaciones!\n\n" +
                    "Todos los productos tienen stock adecuado.\n" +
                    "No hay productos que requieran reabastecimiento en este momento.",
                    "Estado del Inventario",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int sinStock = 0;
            int criticos = 0;
            int bajos = 0;

            for (int i = 0; i < totalFilas; i++) {
                String estado = (String) modeloTabla.getValueAt(i, 10);
                switch (estado) {
                    case "⚫ SIN STOCK":
                        sinStock++;
                        break;
                    case "🔴 CRÍTICO":
                        criticos++;
                        break;
                    case "🟡 BAJO":
                        bajos++;
                        break;
                }
            }

            StringBuilder resumen = new StringBuilder();
            resumen.append("📋 RESUMEN DE REABASTECIMIENTO\n\n");
            resumen.append("Total de productos que necesitan atención: ").append(totalFilas).append("\n\n");
            
            if (sinStock > 0) {
                resumen.append("🚨 URGENTE - Sin stock: ").append(sinStock).append(" productos\n");
            }
            if (criticos > 0) {
                resumen.append("🔴 CRÍTICO - Stock bajo mínimo: ").append(criticos).append(" productos\n");
            }
            if (bajos > 0) {
                resumen.append("🟡 PRECAUCIÓN - Stock bajo: ").append(bajos).append(" productos\n");
            }
            
            resumen.append("\n💡 Recomendación:\n");
            if (sinStock > 0) {
                resumen.append("• Reabastecer INMEDIATAMENTE los productos sin stock\n");
            }
            if (criticos > 0) {
                resumen.append("• Priorizar la compra de productos críticos\n");
            }
            if (bajos > 0) {
                resumen.append("• Planificar reabastecimiento de productos con stock bajo\n");
            }

            JTextArea areaTexto = new JTextArea(resumen.toString());
            areaTexto.setEditable(false);
            areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scroll = new JScrollPane(areaTexto);
            scroll.setPreferredSize(new java.awt.Dimension(450, 350));

            JOptionPane.showMessageDialog(this, scroll, 
                "Resumen de Reabastecimiento", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar resumen: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método público para refrescar la tabla desde otros componentes
     */
    public void refrescarTabla() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔄 Refrescando tabla de inventario mínimo...");
            cargarProductosInventarioMinimo();
        });
    }

    // ✅ IMPLEMENTACIÓN DE LA INTERFAZ InventarioUpdateListener
    @Override
    public void onInventarioActualizado() {
        System.out.println("🔔 PanelInventarioMinimo: Recibida notificación de actualización");
        refrescarTabla();
    }

    @Override
    public void refrescarDatos() {
        System.out.println("🔄 PanelInventarioMinimo: Refrescando datos forzadamente");
        refrescarTabla();
    }

    /**
     * Limpia los recursos cuando se cierra el panel
     */
    public void cleanup() {
        InventarioUpdateManager.getInstance().removeListener(this);
        System.out.println("🧹 PanelInventarioMinimo: Recursos limpiados");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "CLAVE", "NOMBRE DEL PRODUCTO/DESCRIPCION", "CATEGORIA", "PROVEEDOR", "PUESTO/ALMACEN/GIRO", "COSTO", "PRECIO", "U.M", "EXISTENCIA MINIMA", "EXISTENCIA ACTUAL"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(100);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setMinWidth(300);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(2).setMinWidth(100);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(3).setMinWidth(100);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(4).setMinWidth(300);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(5).setMinWidth(50);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(6).setMinWidth(50);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(7).setMinWidth(20);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(20);
            jTable1.getColumnModel().getColumn(8).setMinWidth(50);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(9).setMinWidth(50);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(50);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1677, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 881, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    
}
