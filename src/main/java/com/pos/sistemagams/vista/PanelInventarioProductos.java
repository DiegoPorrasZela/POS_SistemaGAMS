/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pos.sistemagams.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.math.BigDecimal;
import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.dao.MovimientoInventarioDAO;
import com.pos.sistemagams.modelo.Producto;
import com.pos.sistemagams.vista.DialogoCategorias;
import com.pos.sistemagams.vista.DialogoProveedores;

/**
 *
 * @author Diego
 */
public class PanelInventarioProductos extends javax.swing.JPanel implements InventarioUpdateListener {

    private ProductoDAO productoDAO;
    private MovimientoInventarioDAO movimientoDAO;
    private DefaultTableModel modeloTabla;


    /**
     * Creates new form PanelInventarioProductos
     */
    public PanelInventarioProductos() {
        initComponents();
        inicializarComponentesPersonalizados();
        
        // ✅ REGISTRAR ESTE PANEL PARA RECIBIR NOTIFICACIONES
        InventarioUpdateManager.getInstance().addListener(this);
        System.out.println("✅ PanelInventarioProductos registrado para notificaciones");
    }

    private void inicializarComponentesPersonalizados() {
        productoDAO = new ProductoDAO();
        movimientoDAO = new MovimientoInventarioDAO();

        // Configurar modelo de tabla
        String[] columnas = {
            "CLAVE", "NOMBRE DEL PRODUCTO/DESCRIPCION", "CATEGORIA",
            "DEPARTAMENTO/VITRINA/ESTANTE", "PUESTO/ALMACEN/GIRO",
            "U.M", "EXISTEN", "COSTO", "PRECIO 1", "PRECIO 2"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };

        // Asignar modelo a la tabla
        jTable1.setModel(modeloTabla);

        // Mantener el formato de columnas que ya tienes
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setMinWidth(300);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(2).setMinWidth(100);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(3).setMinWidth(300);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(4).setMinWidth(300);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(5).setMinWidth(20);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(20);
            jTable1.getColumnModel().getColumn(6).setMinWidth(50);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(7).setMinWidth(50);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(8).setMinWidth(50);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(9).setMinWidth(50);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(50);
        }

        // Cargar productos iniciales
        cargarProductosEnTabla();
    }

    public void cargarProductosEnTabla() {
        try {
            System.out.println("🔄 Cargando productos en tabla...");

            // Limpiar tabla
            modeloTabla.setRowCount(0);

            // Obtener productos
            List<Producto> productos = productoDAO.obtenerTodosLosProductos();
            System.out.println("📦 Productos obtenidos: " + productos.size());

            // Agregar cada producto a la tabla
            for (Producto producto : productos) {
                // ✅ OBTENER STOCK REAL desde MovimientoInventarioDAO
                BigDecimal stockReal = BigDecimal.ZERO;

                if (producto.getIdAlmacen() > 0) {
                    // Si el producto tiene almacén asignado, obtener stock de ese almacén
                    stockReal = movimientoDAO.obtenerStockActual(producto.getIdProducto(), producto.getIdAlmacen());
                    System.out.println("📦 Stock producto " + producto.getCodigo() + " en almacén " + producto.getIdAlmacen() + ": " + stockReal);
                } else {
                    // Si no tiene almacén específico, buscar en TODOS los almacenes disponibles
                    List<com.pos.sistemagams.modelo.Almacen> todosLosAlmacenes = productoDAO.obtenerAlmacenes();
                    System.out.println("🔍 Producto sin almacén específico, buscando en " + todosLosAlmacenes.size() + " almacenes");

                    for (com.pos.sistemagams.modelo.Almacen almacen : todosLosAlmacenes) {
                        BigDecimal stockAlmacen = movimientoDAO.obtenerStockActual(producto.getIdProducto(), almacen.getIdAlmacen());
                        if (stockAlmacen.compareTo(BigDecimal.ZERO) > 0) {
                            stockReal = stockReal.add(stockAlmacen);
                            System.out.println("📦 Stock encontrado en almacén " + almacen.getNombre() + ": " + stockAlmacen);
                        }
                    }
                    System.out.println("🧮 Stock total producto " + producto.getCodigo() + ": " + stockReal);
                }

                // ✅ VERIFICAR QUE EL STOCK NO SEA NULL
                if (stockReal == null) {
                    stockReal = BigDecimal.ZERO;
                }

                Object[] fila = {
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getNombreCategoria() != null ? producto.getNombreCategoria() : "",
                    producto.getNombreDepartamento() != null ? producto.getNombreDepartamento() : "",
                    producto.getNombreAlmacen() != null ? producto.getNombreAlmacen() : "",
                    producto.getUnidadCompra(),
                    stockReal.toString(), // ✅ STOCK REAL, NO FIJO
                    String.format("%.2f", producto.getPrecioCompra()),
                    String.format("%.2f", producto.getPrecioVenta1()),
                    String.format("%.2f", producto.getPrecioVenta2())
                };

                modeloTabla.addRow(fila);
            }

            System.out.println("✅ Tabla actualizada con " + productos.size() + " productos con stock real");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar productos en tabla: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método público para refrescar la tabla desde otros componentes
     */
    public void refrescarTabla() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔄 Refrescando tabla de productos...");
            cargarProductosEnTabla();
        });
    }

    // ✅ IMPLEMENTACIÓN DE LA INTERFAZ InventarioUpdateListener
    @Override
    public void onInventarioActualizado() {
        System.out.println("🔔 PanelInventarioProductos: Recibida notificación de actualización");
        refrescarTabla();
    }

    @Override
    public void refrescarDatos() {
        System.out.println("🔄 PanelInventarioProductos: Refrescando datos forzadamente");
        refrescarTabla();
    }

    /**
     * Limpia los recursos cuando se cierra el panel
     */
    public void cleanup() {
        InventarioUpdateManager.getInstance().removeListener(this);
        System.out.println("🧹 PanelInventarioProductos: Recursos limpiados");
    }

    /**
     * Obtiene el producto seleccionado en la tabla
     */
    private Producto obtenerProductoSeleccionado() {
        int filaSeleccionada = jTable1.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un producto de la tabla",
                    "Ningún producto seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // Obtener el código del producto de la tabla
        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        // Buscar el producto completo en la base de datos
        return productoDAO.obtenerProductoPorCodigo(codigo);
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
        btnAgregar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnDetalle = new javax.swing.JButton();
        btnCategorias = new javax.swing.JButton();
        btnAlmacen = new javax.swing.JButton();
        btnDepartamento = new javax.swing.JButton();
        btnProveedores = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "CLAVE", "NOMBRE DEL PRODUCTO/DESCRIPCION", "CATEGORIA", "DEPARTAMENTO/VITRINA/ESTANTE", "PUESTO/ALMACEN/GIRO", "U.M", "EXISTEN", "COSTO", "PRECIO 1", "PRECIO 2"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setMinWidth(300);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(2).setMinWidth(100);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(3).setMinWidth(300);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(4).setMinWidth(300);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(5).setMinWidth(20);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(20);
            jTable1.getColumnModel().getColumn(6).setMinWidth(50);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(7).setMinWidth(50);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(8).setMinWidth(50);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(9).setMinWidth(50);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(50);
        }

        btnAgregar.setText("AGREGAR");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnModificar.setText("MODIFICAR");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setText("ELIMINAR");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnDetalle.setText("DETALLE");
        btnDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetalleActionPerformed(evt);
            }
        });

        btnCategorias.setText("CATEGORIAS");
        btnCategorias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoriasActionPerformed(evt);
            }
        });

        btnAlmacen.setText("PSTO/ALMCN");
        btnAlmacen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlmacenActionPerformed(evt);
            }
        });

        btnDepartamento.setText("DPT/VITR/EST");
        btnDepartamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepartamentoActionPerformed(evt);
            }
        });

        btnProveedores.setText("PROVEEDORES");
        btnProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProveedoresActionPerformed(evt);
            }
        });

        jLabel1.setText("Producto:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1529, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCategorias, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDetalle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAlmacen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(71, 71, 71)
                        .addComponent(btnCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(132, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
         try {
            // Crear y mostrar el diálogo
            DialogoAgregarProducto dialogo = new DialogoAgregarProducto();
            dialogo.setVisible(true);

            // Si se guardó el producto, actualizar la tabla
            if (dialogo.isGuardado()) {
                // ✅ AQUÍ ES LA CLAVE: Recargar la tabla después de agregar
                cargarProductosEnTabla();

                JOptionPane.showMessageDialog(this,
                        "Producto agregado exitosamente",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir el diálogo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        try {
            // Obtener producto seleccionado
            Producto productoSeleccionado = obtenerProductoSeleccionado();

            if (productoSeleccionado == null) {
                return; // Ya se mostró mensaje de error
            }

            // Crear y mostrar el diálogo de modificación
            DialogoModificarProducto dialogo = new DialogoModificarProducto(this, productoSeleccionado);
            dialogo.setVisible(true);

            // Si se guardaron los cambios, actualizar la tabla
            if (dialogo.isGuardado()) {
                cargarProductosEnTabla();

                JOptionPane.showMessageDialog(this,
                        "Producto modificado exitosamente",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir el diálogo de modificación: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        try {
            // Obtener producto seleccionado
            Producto productoSeleccionado = obtenerProductoSeleccionado();

            if (productoSeleccionado == null) {
                return; // Ya se mostró mensaje de error
            }

            // Verificar stock actual usando el MovimientoInventarioDAO
            BigDecimal stockActual = BigDecimal.ZERO;
            if (productoSeleccionado.getIdAlmacen() > 0) {
                stockActual = movimientoDAO.obtenerStockActual(
                        productoSeleccionado.getIdProducto(),
                        productoSeleccionado.getIdAlmacen());
            }

            if (stockActual.compareTo(BigDecimal.ZERO) > 0) {
                // No se puede eliminar si tiene stock
                JOptionPane.showMessageDialog(this,
                        "No se puede eliminar el producto: " + productoSeleccionado.getNombre() + "\n"
                        + "Existencias: " + stockActual,
                        "No se puede eliminar",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Confirmación de eliminación
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea eliminar el producto?\n\n"
                    + "Producto: " + productoSeleccionado.getNombre() + "\n"
                    + "Código: " + productoSeleccionado.getCodigo() + "\n\n"
                    + "Esta acción no se puede deshacer.",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                // Eliminar producto
                if (productoDAO.eliminarProducto(productoSeleccionado.getIdProducto())) {
                    // Actualizar tabla
                    cargarProductosEnTabla();

                    JOptionPane.showMessageDialog(this,
                            "Producto eliminado exitosamente",
                            "Eliminación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al eliminar el producto",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar el producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetalleActionPerformed
        try {
            // Obtener producto seleccionado
            Producto productoSeleccionado = obtenerProductoSeleccionado();

            if (productoSeleccionado == null) {
                return; // Ya se mostró mensaje de error
            }

            // Crear y mostrar el diálogo de detalle
            DialogoDetalleProducto dialogo = new DialogoDetalleProducto(this, productoSeleccionado);
            dialogo.setVisible(true);

            // ✅ REFRESCAR TABLA después de ver detalles (por si el stock cambió)
            cargarProductosEnTabla();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al mostrar los detalles del producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }//GEN-LAST:event_btnDetalleActionPerformed

    private void btnCategoriasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriasActionPerformed
        try {
            // Usar el constructor sin parámetros
            DialogoCategorias dialogo = new DialogoCategorias();
            dialogo.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir la gestión de categorías: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnCategoriasActionPerformed

    private void btnAlmacenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlmacenActionPerformed
        try {
            // Crear y mostrar el diálogo de almacenes
            DialogoAlmacenes dialogo = new DialogoAlmacenes();
            dialogo.setVisible(true);

            // Opcional: Recargar datos si se hicieron cambios
            // (Para actualizar los ComboBox de almacenes en otros formularios)
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir la gestión de almacenes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnAlmacenActionPerformed

    private void btnDepartamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepartamentoActionPerformed
        try {
            // Crear y mostrar el diálogo de departamentos
            DialogoDepartamentos dialogo = new DialogoDepartamentos();
            dialogo.setVisible(true);

            // Opcional: Recargar datos si se hicieron cambios
            // (Para actualizar los ComboBox de departamentos en otros formularios)
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir la gestión de departamentos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnDepartamentoActionPerformed

    private void btnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedoresActionPerformed
        try {
            // Crear y mostrar el diálogo de proveedores
            DialogoProveedores dialogo = new DialogoProveedores();
            dialogo.setVisible(true);

            // Opcional: Recargar datos si se hicieron cambios
            // (Para actualizar los ComboBox de proveedores en otros formularios)
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir la gestión de proveedores: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnProveedoresActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnAlmacen;
    private javax.swing.JButton btnCategorias;
    private javax.swing.JButton btnDepartamento;
    private javax.swing.JButton btnDetalle;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnProveedores;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    
}
