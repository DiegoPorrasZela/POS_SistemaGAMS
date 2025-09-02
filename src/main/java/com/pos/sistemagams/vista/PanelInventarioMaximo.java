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
public class PanelInventarioMaximo extends javax.swing.JPanel implements InventarioUpdateListener  {

    private ProductoDAO productoDAO;
    private MovimientoInventarioDAO movimientoDAO;
    private DefaultTableModel modeloTabla;
    
    /**
     * Creates new form PanelInventarioMaximo
     */
    public PanelInventarioMaximo() {
        initComponents();
        inicializarComponentes();
        
        // ✅ REGISTRAR ESTE PANEL PARA RECIBIR NOTIFICACIONES
        InventarioUpdateManager.getInstance().addListener(this);
        System.out.println("✅ PanelInventarioMaximo registrado para notificaciones");
    }

    private void inicializarComponentes() {
        productoDAO = new ProductoDAO();
        movimientoDAO = new MovimientoInventarioDAO();

        // Configurar modelo de tabla
        String[] columnas = {
            "CLAVE", "NOMBRE DEL PRODUCTO/DESCRIPCION", "CATEGORIA", "PROVEEDOR",
            "PUESTO/ALMACEN/GIRO", "COSTO", "PRECIO", "U.M", 
            "EXISTENCIA MAXIMA", "EXISTENCIA ACTUAL", "EXCESO", "ESTADO"
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
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(120);  // EXISTENCIA MAXIMA
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(120);  // EXISTENCIA ACTUAL
            jTable1.getColumnModel().getColumn(10).setPreferredWidth(100); // EXCESO
            jTable1.getColumnModel().getColumn(11).setPreferredWidth(100); // ESTADO
        }

        // Configurar renderizadores
        configurarRenderizadores();

        // Cargar datos iniciales
        cargarProductosInventarioMaximo();
    }

    private void configurarRenderizadores() {
        // Renderer para números alineados a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        jTable1.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // COSTO
        jTable1.getColumnModel().getColumn(6).setCellRenderer(rightRenderer); // PRECIO
        jTable1.getColumnModel().getColumn(8).setCellRenderer(rightRenderer); // EXISTENCIA MAXIMA
        jTable1.getColumnModel().getColumn(9).setCellRenderer(rightRenderer); // EXISTENCIA ACTUAL
        jTable1.getColumnModel().getColumn(10).setCellRenderer(rightRenderer); // EXCESO

        // Renderer especial para la columna ESTADO con colores
        DefaultTableCellRenderer estadoRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 11 && value != null) { // Columna ESTADO
                    String estado = value.toString();
                    
                    if (!isSelected) {
                        switch (estado) {
                            case "🔴 EXCESO CRÍTICO":
                                c.setBackground(new Color(255, 235, 235)); // Fondo rojo claro
                                c.setForeground(new Color(139, 0, 0)); // Texto rojo oscuro
                                break;
                            case "🟡 EXCESO MODERADO":
                                c.setBackground(new Color(255, 255, 235)); // Fondo amarillo claro
                                c.setForeground(new Color(184, 134, 11)); // Texto amarillo oscuro
                                break;
                            case "🟡 EN LÍMITE MÁXIMO":
                                c.setBackground(new Color(255, 245, 235)); // Fondo naranja muy claro
                                c.setForeground(new Color(255, 140, 0)); // Texto naranja
                                break;
                            case "🟢 NORMAL":
                                c.setBackground(new Color(235, 255, 235)); // Fondo verde claro
                                c.setForeground(new Color(34, 139, 34)); // Texto verde
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

        jTable1.getColumnModel().getColumn(11).setCellRenderer(estadoRenderer);

        // Renderer para colorear la columna EXCESO
        DefaultTableCellRenderer excesoRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 10 && value != null && !isSelected) { // Columna EXCESO
                    try {
                        BigDecimal exceso = new BigDecimal(value.toString());
                        if (exceso.compareTo(BigDecimal.ZERO) > 0) {
                            c.setForeground(new Color(139, 0, 0)); // Rojo para exceso positivo
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(Color.BLACK);
                        }
                    } catch (NumberFormatException e) {
                        c.setForeground(Color.BLACK);
                    }
                } else if (!isSelected) {
                    c.setForeground(Color.BLACK);
                }
                
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        };

        jTable1.getColumnModel().getColumn(10).setCellRenderer(excesoRenderer);

        // Renderer para colorear filas según el estado
        DefaultTableCellRenderer filaRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && column != 11 && column != 10) { // No aplicar a ESTADO ni EXCESO
                    String estado = (String) table.getValueAt(row, 11);
                    
                    switch (estado) {
                        case "🔴 EXCESO CRÍTICO":
                            c.setBackground(new Color(255, 245, 245)); // Fondo muy claro rojo
                            break;
                        case "🟡 EXCESO MODERADO":
                            c.setBackground(new Color(255, 255, 245)); // Fondo muy claro amarillo
                            break;
                        case "🟡 EN LÍMITE MÁXIMO":
                            c.setBackground(new Color(255, 250, 240)); // Fondo muy claro naranja
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

        // Aplicar renderer a todas las columnas excepto ESTADO y EXCESO
        for (int i = 0; i < 10; i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(filaRenderer);
        }
    }

    public void cargarProductosInventarioMaximo() {
        try {
            System.out.println("🔄 Cargando productos en límite máximo o con exceso...");

            // Limpiar tabla
            modeloTabla.setRowCount(0);

            // Obtener todos los productos
            List<Producto> productos = productoDAO.obtenerTodosLosProductos();
            System.out.println("📦 Productos obtenidos: " + productos.size());

            // ✅ ESTRUCTURA: Separar productos por nivel de stock máximo
            List<Object[]> productosEnLimite = new ArrayList<>();
            List<Object[]> productosCriticos = new ArrayList<>();
            List<Object[]> productosModerados = new ArrayList<>();

            // Procesar cada producto
            for (Producto producto : productos) {
                // Solo procesar productos que tienen stock máximo configurado (mayor a 0)
                if (producto.getStockMaximo() > 0) {
                    
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

                    // Determinar estado del stock y calcular exceso
                    ResultadoExceso resultado = determinarEstadoExceso(stockActual, producto.getStockMaximo());

                    // ✅ FILTRO CORREGIDO: Incluir productos EN LÍMITE, con exceso moderado y crítico
                    if (!resultado.estado.equals("🟢 NORMAL")) {
                        
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
                            producto.getStockMaximo(),
                            stockActual,
                            resultado.exceso,
                            resultado.estado
                        };

                        // ✅ CLASIFICAR POR PRIORIDAD (incluir EN LÍMITE)
                        switch (resultado.estado) {
                            case "🔴 EXCESO CRÍTICO":
                                productosCriticos.add(fila);
                                break;
                            case "🟡 EXCESO MODERADO":
                                productosModerados.add(fila);
                                break;
                            case "🟡 EN LÍMITE MÁXIMO":
                                productosEnLimite.add(fila);
                                break;
                        }
                    }
                }
            }

            // ✅ AGREGAR A LA TABLA EN ORDEN DE PRIORIDAD
            // 1. Primero productos con EXCESO CRÍTICO (máxima prioridad)
            for (Object[] fila : productosCriticos) {
                modeloTabla.addRow(fila);
            }
            
            // 2. Segundo productos con EXCESO MODERADO
            for (Object[] fila : productosModerados) {
                modeloTabla.addRow(fila);
            }
            
            // 3. Tercero productos EN LÍMITE MÁXIMO
            for (Object[] fila : productosEnLimite) {
                modeloTabla.addRow(fila);
            }

            int totalAlerta = productosCriticos.size() + productosModerados.size() + productosEnLimite.size();
            
            System.out.println("✅ Productos que requieren atención por stock máximo:");
            System.out.println("   🔴 Exceso crítico: " + productosCriticos.size());
            System.out.println("   🟡 Exceso moderado: " + productosModerados.size());
            System.out.println("   🟡 En límite máximo: " + productosEnLimite.size());
            System.out.println("   📊 Total: " + totalAlerta);

            // ✅ MOSTRAR MENSAJE SI NO HAY PRODUCTOS QUE REQUIERAN ATENCIÓN
            if (totalAlerta == 0) {
                // Agregar fila informativa
                Object[] filaVacia = {
                    "", "✅ ¡Perfecto! Ningún producto está en su límite máximo o lo excede", 
                    "", "", "", "", "", "", "", "", "", "🟢 NORMAL"
                };
                modeloTabla.addRow(filaVacia);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al cargar inventario máximo: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar inventario máximo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clase interna para manejar resultado de exceso
    private static class ResultadoExceso {
        public final String estado;
        public final BigDecimal exceso;
        
        public ResultadoExceso(String estado, BigDecimal exceso) {
            this.estado = estado;
            this.exceso = exceso;
        }
    }

    // Método que determina el estado de exceso
    private ResultadoExceso determinarEstadoExceso(BigDecimal stockActual, int stockMaximo) {
        BigDecimal stockMaximoBD = new BigDecimal(stockMaximo);
        
        // ✅ CAMBIO PRINCIPAL: Mostrar productos que están EN EL LÍMITE o lo exceden
        if (stockActual.compareTo(stockMaximoBD) < 0) {
            // Stock por debajo del límite máximo - NO mostrar
            return new ResultadoExceso("🟢 NORMAL", BigDecimal.ZERO);
        } else if (stockActual.compareTo(stockMaximoBD) == 0) {
            // ✅ Stock exactamente en el límite máximo - MOSTRAR como EN LÍMITE
            return new ResultadoExceso("🟡 EN LÍMITE MÁXIMO", BigDecimal.ZERO);
        } else {
            // Stock excede el límite máximo
            BigDecimal exceso = stockActual.subtract(stockMaximoBD);
            
            // Determinar nivel de exceso
            BigDecimal umbralCritico = stockMaximoBD.multiply(new BigDecimal("0.5")); // 50% del máximo
            
            if (exceso.compareTo(umbralCritico) > 0) {
                return new ResultadoExceso("🔴 EXCESO CRÍTICO", exceso);
            } else {
                return new ResultadoExceso("🟡 EXCESO MODERADO", exceso);
            }
        }
    }

    /**
     * ✅ NUEVO MÉTODO: Obtener resumen de excesos
     */
    public void mostrarResumenExcesos() {
        try {
            int totalFilas = modeloTabla.getRowCount();
            
            if (totalFilas == 0 || (totalFilas == 1 && modeloTabla.getValueAt(0, 1).toString().contains("Perfecto"))) {
                JOptionPane.showMessageDialog(this,
                    "🎉 ¡Excelente gestión de inventario!\n\n" +
                    "Ningún producto excede su stock máximo.\n" +
                    "El inventario está bien controlado y optimizado.",
                    "Estado del Inventario Máximo",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int excesosCriticos = 0;
            int excesosModerados = 0;
            BigDecimal valorTotalExceso = BigDecimal.ZERO;

            for (int i = 0; i < totalFilas; i++) {
                String estado = (String) modeloTabla.getValueAt(i, 11);
                Object costObj = modeloTabla.getValueAt(i, 5);
                Object excesObj = modeloTabla.getValueAt(i, 10);
                
                switch (estado) {
                    case "🔴 EXCESO CRÍTICO":
                        excesosCriticos++;
                        break;
                    case "🟡 EXCESO MODERADO":
                        excesosModerados++;
                        break;
                }
                
                // Calcular valor del exceso
                if (costObj != null && excesObj != null) {
                    try {
                        BigDecimal costo = new BigDecimal(costObj.toString());
                        BigDecimal exceso = new BigDecimal(excesObj.toString());
                        valorTotalExceso = valorTotalExceso.add(costo.multiply(exceso));
                    } catch (NumberFormatException e) {
                        // Ignorar errores de conversión
                    }
                }
            }

            StringBuilder resumen = new StringBuilder();
            resumen.append("📊 RESUMEN DE EXCESOS DE INVENTARIO\n\n");
            resumen.append("Total de productos con exceso: ").append(totalFilas).append("\n\n");
            
            if (excesosCriticos > 0) {
                resumen.append("🔴 EXCESO CRÍTICO: ").append(excesosCriticos).append(" productos\n");
            }
            if (excesosModerados > 0) {
                resumen.append("🟡 EXCESO MODERADO: ").append(excesosModerados).append(" productos\n");
            }
            
            resumen.append("\n💰 Valor estimado del exceso: $").append(valorTotalExceso.setScale(2)).append("\n");
            
            resumen.append("\n💡 Recomendaciones:\n");
            if (excesosCriticos > 0) {
                resumen.append("• Considerar promociones especiales para productos con exceso crítico\n");
                resumen.append("• Revisar políticas de reabastecimiento\n");
            }
            if (excesosModerados > 0) {
                resumen.append("• Monitorear productos con exceso moderado\n");
                resumen.append("• Ajustar cantidades de pedidos futuros\n");
            }
            resumen.append("• Evaluar demanda real vs. stock máximo configurado\n");

            JTextArea areaTexto = new JTextArea(resumen.toString());
            areaTexto.setEditable(false);
            areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scroll = new JScrollPane(areaTexto);
            scroll.setPreferredSize(new java.awt.Dimension(500, 400));

            JOptionPane.showMessageDialog(this, scroll, 
                "Resumen de Excesos de Inventario", JOptionPane.INFORMATION_MESSAGE);

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
            System.out.println("🔄 Refrescando tabla de inventario máximo...");
            cargarProductosInventarioMaximo();
        });
    }

    // ✅ IMPLEMENTACIÓN DE LA INTERFAZ InventarioUpdateListener
    @Override
    public void onInventarioActualizado() {
        System.out.println("🔔 PanelInventarioMaximo: Recibida notificación de actualización");
        refrescarTabla();
    }

    @Override
    public void refrescarDatos() {
        System.out.println("🔄 PanelInventarioMaximo: Refrescando datos forzadamente");
        refrescarTabla();
    }

    /**
     * Limpia los recursos cuando se cierra el panel
     */
    public void cleanup() {
        InventarioUpdateManager.getInstance().removeListener(this);
        System.out.println("🧹 PanelInventarioMaximo: Recursos limpiados");
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
