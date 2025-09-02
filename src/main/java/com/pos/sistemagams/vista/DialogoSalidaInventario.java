/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.dao.MovimientoInventarioDAO;
import com.pos.sistemagams.modelo.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Diálogo para salida de inventario
 */
public class DialogoSalidaInventario extends JDialog {
    
    // DAOs
    private ProductoDAO productoDAO;
    private MovimientoInventarioDAO movimientoDAO;
    
    // Componentes principales
    private JTextField txtAlmacen;
    private JButton btnBuscarAlmacen;
    private JTextField txtProducto;
    private JButton btnBuscarProducto;
    private JLabel lblNumeroSalida;
    private JLabel lblTotalCostos;
    private JLabel lblTotalCantidad;
    private JLabel lblTotalProductos;
    
    // Tabla de productos
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    
    // Botones de acción
    private JButton btnBorrar;
    private JButton btnCantidad;
    private JButton btnDetalle;
    private JButton btnGuardar;
    private JTextArea txtMotivo;
    
    // Variables de control
    private Almacen almacenSeleccionado;
    private List<ItemSalidaInventario> itemsSalida;
    private int numeroSalida;
    
    // Variable para indicar si se guardó exitosamente
    private boolean guardadoExitosamente = false;
    
    // Componente padre (opcional para callback)
    private PanelInventarioMovimientos panelPadre;
    
    // Clase interna para items de salida
    public static class ItemSalidaInventario {
        private Producto producto;
        private BigDecimal cantidad;
        private BigDecimal costo;
        private BigDecimal precio1;
        private BigDecimal precio2;
        private BigDecimal precio3;
        private BigDecimal stockAnterior;
        private BigDecimal total;
        
        public ItemSalidaInventario(Producto producto) {
            this.producto = producto;
            this.cantidad = BigDecimal.ONE;
            this.costo = producto.getPrecioCompra();
            this.precio1 = producto.getPrecioVenta1();
            this.precio2 = producto.getPrecioVenta2();
            this.precio3 = producto.getPrecioVenta3();
            this.stockAnterior = BigDecimal.ZERO;
            calcularTotal();
        }
        
        public void calcularTotal() {
            this.total = this.cantidad.multiply(this.costo);
        }
        
        // Getters y setters
        public Producto getProducto() { return producto; }
        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) { 
            this.cantidad = cantidad; 
            calcularTotal();
        }
        public BigDecimal getCosto() { return costo; }
        public void setCosto(BigDecimal costo) { 
            this.costo = costo; 
            calcularTotal();
        }
        public BigDecimal getPrecio1() { return precio1; }
        public void setPrecio1(BigDecimal precio1) { this.precio1 = precio1; }
        public BigDecimal getPrecio2() { return precio2; }
        public void setPrecio2(BigDecimal precio2) { this.precio2 = precio2; }
        public BigDecimal getPrecio3() { return precio3; }
        public void setPrecio3(BigDecimal precio3) { this.precio3 = precio3; }
        public BigDecimal getStockAnterior() { return stockAnterior; }
        public void setStockAnterior(BigDecimal stockAnterior) { this.stockAnterior = stockAnterior; }
        public BigDecimal getTotal() { return total; }
    }
    
    // Constructores
    public DialogoSalidaInventario(Component parent) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null, 
              "Salida de Inventario", ModalityType.APPLICATION_MODAL);
        
        // Si el parent es PanelInventarioMovimientos, guardarlo para callback
        if (parent instanceof PanelInventarioMovimientos) {
            this.panelPadre = (PanelInventarioMovimientos) parent;
        }
        
        this.productoDAO = new ProductoDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
        this.itemsSalida = new ArrayList<>();
        
        initComponents();
        configurarDialog();
        configurarTabla();
        configurarEventos();
        generarNumeroSalida();
        actualizarTotales();
    }
    
    public DialogoSalidaInventario() {
        super((Frame) null, "Salida de Inventario", true);
        
        this.productoDAO = new ProductoDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
        this.itemsSalida = new ArrayList<>();
        
        initComponents();
        configurarDialog();
        configurarTabla();
        configurarEventos();
        generarNumeroSalida();
        actualizarTotales();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = crearHeader();
        
        // Panel superior con búsquedas
        JPanel panelBusqueda = crearPanelBusqueda();
        
        // Panel central con tabla
        JPanel panelTabla = crearPanelTabla();
        
        // Panel inferior con botones y motivo
        JPanel panelInferior = crearPanelInferior();
        
        panelPrincipal.add(headerPanel, BorderLayout.NORTH);
        panelPrincipal.add(panelBusqueda, BorderLayout.PAGE_START);
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel crearHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        // Color rojo/naranja para diferenciarlo de entrada
        panel.setBackground(new Color(220, 53, 69)); // Bootstrap danger color
        panel.setPreferredSize(new Dimension(0, 50));
        
        // Título izquierdo
        JLabel lblTitulo = new JLabel("← Salida de Inventario");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Panel derecho con número de salida y totales
        JPanel panelDerecho = new JPanel(new GridLayout(3, 2, 5, 2));
        panelDerecho.setBackground(new Color(220, 53, 69));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 15));
        
        Font fontSmall = new Font("Arial", Font.PLAIN, 11);
        Font fontBold = new Font("Arial", Font.BOLD, 12);
        
        JLabel lblNumSalida = new JLabel("Salida N° :");
        lblNumSalida.setForeground(Color.WHITE);
        lblNumSalida.setFont(fontSmall);
        lblNumSalida.setHorizontalAlignment(SwingConstants.RIGHT);
        
        lblNumeroSalida = new JLabel("0000001");
        lblNumeroSalida.setForeground(Color.WHITE);
        lblNumeroSalida.setFont(fontBold);
        lblNumeroSalida.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel lblCostos = new JLabel("Total Costos :");
        lblCostos.setForeground(Color.WHITE);
        lblCostos.setFont(fontSmall);
        lblCostos.setHorizontalAlignment(SwingConstants.RIGHT);
        
        lblTotalCostos = new JLabel("0");
        lblTotalCostos.setForeground(Color.WHITE);
        lblTotalCostos.setFont(fontBold);
        lblTotalCostos.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel lblCantidad = new JLabel("Total Cantidad :");
        lblCantidad.setForeground(Color.WHITE);
        lblCantidad.setFont(fontSmall);
        lblCantidad.setHorizontalAlignment(SwingConstants.RIGHT);
        
        lblTotalCantidad = new JLabel("0");
        lblTotalCantidad.setForeground(Color.WHITE);
        lblTotalCantidad.setFont(fontBold);
        lblTotalCantidad.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel lblProductos = new JLabel("Total Productos :");
        lblProductos.setForeground(Color.WHITE);
        lblProductos.setFont(fontSmall);
        lblProductos.setHorizontalAlignment(SwingConstants.RIGHT);
        
        lblTotalProductos = new JLabel("0");
        lblTotalProductos.setForeground(Color.WHITE);
        lblTotalProductos.setFont(fontBold);
        lblTotalProductos.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panelDerecho.add(lblNumSalida);
        panelDerecho.add(lblNumeroSalida);
        panelDerecho.add(lblCostos);
        panelDerecho.add(lblTotalCostos);
        panelDerecho.add(lblCantidad);
        panelDerecho.add(lblTotalCantidad);
        
        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(panelDerecho, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Puesto/Almacén/Giro
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblAlmacen = new JLabel("Puesto/Almacén/Giro:");
        lblAlmacen.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblAlmacen, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtAlmacen = new JTextField(25);
        txtAlmacen.setPreferredSize(new Dimension(300, 30));
        txtAlmacen.setEditable(false);
        txtAlmacen.setBackground(Color.WHITE);
        panel.add(txtAlmacen, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        btnBuscarAlmacen = new JButton("🔍");
        btnBuscarAlmacen.setPreferredSize(new Dimension(35, 30));
        btnBuscarAlmacen.setBackground(new Color(220, 53, 69));
        btnBuscarAlmacen.setForeground(Color.WHITE);
        panel.add(btnBuscarAlmacen, gbc);
        
        // Producto
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblProducto, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtProducto = new JTextField(25);
        txtProducto.setPreferredSize(new Dimension(300, 30));
        txtProducto.setEnabled(false);
        panel.add(txtProducto, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        btnBuscarProducto = new JButton("🔍");
        btnBuscarProducto.setPreferredSize(new Dimension(35, 30));
        btnBuscarProducto.setBackground(new Color(220, 53, 69));
        btnBuscarProducto.setForeground(Color.WHITE);
        btnBuscarProducto.setEnabled(false);
        panel.add(btnBuscarProducto, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        // Crear tabla
        String[] columnas = {"CLAVE", "NOMBRE DEL PRODUCTO/DESCRIPCIÓN", "CATEGORIA", "UNIDAD", 
                            "EXISTENCIA", "CANTIDAD", "COSTO", "TOTAL"};
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo permitir editar cantidad, costo y precios
                return column == 5 || column == 6; // CANTIDAD y COSTO
            }
        };
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(25);
        tablaProductos.setGridColor(Color.LIGHT_GRAY);
        tablaProductos.setShowGrid(true);
        
        // Configurar ancho de columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(80);  // CLAVE
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(300); // DESCRIPCIÓN
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(100); // CATEGORIA
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(80);  // UNIDAD
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(80);  // EXISTENCIA
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(80);  // CANTIDAD
        tablaProductos.getColumnModel().getColumn(6).setPreferredWidth(80);  // COSTO
        tablaProductos.getColumnModel().getColumn(7).setPreferredWidth(80);  // TOTAL
        
        // Header con color
        tablaProductos.getTableHeader().setBackground(new Color(180, 180, 180));
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(220, 53, 69));
        panel.setPreferredSize(new Dimension(0, 80));
        
        // Panel de botones izquierdo
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        panelBotones.setBackground(new Color(220, 53, 69));
        
        btnBorrar = crearBoton("Borrar ⊗", new Color(220, 53, 69));
        btnCantidad = crearBoton("Cantidad 📝", new Color(220, 53, 69));
        btnDetalle = crearBoton("Detalle 📋", new Color(220, 53, 69));
        
        panelBotones.add(btnBorrar);
        panelBotones.add(btnCantidad);
        panelBotones.add(btnDetalle);
        
        // Panel central con motivo
        JPanel panelMotivo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelMotivo.setBackground(new Color(220, 53, 69));
        
        JLabel lblMotivo = new JLabel("Motivo:");
        lblMotivo.setForeground(Color.WHITE);
        lblMotivo.setFont(new Font("Arial", Font.BOLD, 12));
        
        txtMotivo = new JTextArea(2, 30);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        scrollMotivo.setPreferredSize(new Dimension(300, 50));
        
        panelMotivo.add(lblMotivo);
        panelMotivo.add(scrollMotivo);
        
        // Panel derecho con botón guardar
        JPanel panelGuardar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        panelGuardar.setBackground(new Color(220, 53, 69));
        
        btnGuardar = crearBoton("Guardar 💾", new Color(220, 53, 69));
        btnGuardar.setPreferredSize(new Dimension(120, 50));
        
        panelGuardar.add(btnGuardar);
        
        panel.add(panelBotones, BorderLayout.WEST);
        panel.add(panelMotivo, BorderLayout.CENTER);
        panel.add(panelGuardar, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 11));
        boton.setPreferredSize(new Dimension(100, 35));
        boton.setFocusPainted(false);
        boton.setBorderPainted(true);
        return boton;
    }
    
    private void configurarDialog() {
        setSize(1000, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
    }
    
    private void configurarTabla() {
        // Renderer para números alineados a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        tablaProductos.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // EXISTENCIA
        tablaProductos.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // CANTIDAD
        tablaProductos.getColumnModel().getColumn(6).setCellRenderer(rightRenderer); // COSTO
        tablaProductos.getColumnModel().getColumn(7).setCellRenderer(rightRenderer); // TOTAL
    }
    
    private void configurarEventos() {
        // Buscar almacén
        btnBuscarAlmacen.addActionListener(this::buscarAlmacen);
        
        // Buscar producto
        btnBuscarProducto.addActionListener(this::buscarProducto);
        
        // Botones de acción
        btnBorrar.addActionListener(this::borrarItem);
        btnCantidad.addActionListener(this::editarCantidad);
        btnDetalle.addActionListener(this::mostrarDetalle);
        btnGuardar.addActionListener(this::guardarSalida);
        
        // Eventos de tabla
        tablaProductos.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                actualizarItemDesdeFila(e.getFirstRow());
                actualizarTotales();
            }
        });
    }
    
    private void buscarAlmacen(ActionEvent e) {
        // Crear diálogo de selección de almacén
        List<Almacen> almacenes = productoDAO.obtenerAlmacenes();
        
        if (almacenes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay almacenes disponibles",
                "Sin almacenes",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Object[] opciones = almacenes.toArray();
        Object seleccion = JOptionPane.showInputDialog(this,
            "Seleccione el Puesto/Almacén/Giro:",
            "Seleccionar Almacén",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);
        
        if (seleccion != null) {
            almacenSeleccionado = (Almacen) seleccion;
            txtAlmacen.setText(almacenSeleccionado.getNombre());
            txtProducto.setEnabled(true);
            btnBuscarProducto.setEnabled(true);
            
            // Limpiar productos anteriores si cambia de almacén
            itemsSalida.clear();
            actualizarTabla();
            actualizarTotales();
        }
    }
    
    private void buscarProducto(ActionEvent e) {
        if (almacenSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                "Primero debe seleccionar un almacén",
                "Almacén requerido",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener productos del almacén seleccionado que tengan stock
        List<Producto> productos = obtenerProductosConStock(almacenSeleccionado.getIdAlmacen());
        
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay productos con stock disponible en este almacén",
                "Sin productos con stock",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Object[] opciones = productos.toArray();
        Object seleccion = JOptionPane.showInputDialog(this,
            "Seleccione el producto:",
            "Seleccionar Producto",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);
        
        if (seleccion != null) {
            Producto productoSeleccionado = (Producto) seleccion;
            agregarProducto(productoSeleccionado);
        }
    }
    
    /**
     * Obtiene productos que tienen stock en el almacén seleccionado
     */
    private List<Producto> obtenerProductosConStock(int idAlmacen) {
        List<Producto> productos = productoDAO.obtenerProductosPorAlmacen(idAlmacen);
        List<Producto> productosConStock = new ArrayList<>();
        
        for (Producto producto : productos) {
            BigDecimal stock = movimientoDAO.obtenerStockActual(producto.getIdProducto(), idAlmacen);
            if (stock.compareTo(BigDecimal.ZERO) > 0) {
                productosConStock.add(producto);
            }
        }
        
        return productosConStock;
    }
    
    private void agregarProducto(Producto producto) {
        // Verificar si el producto ya está en la lista
        for (ItemSalidaInventario item : itemsSalida) {
            if (item.getProducto().getIdProducto() == producto.getIdProducto()) {
                JOptionPane.showMessageDialog(this,
                    "El producto ya está en la lista",
                    "Producto duplicado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Obtener stock actual
        BigDecimal stockActual = movimientoDAO.obtenerStockActual(
            producto.getIdProducto(), almacenSeleccionado.getIdAlmacen());
        
        // Verificar que hay stock disponible
        if (stockActual.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this,
                "El producto no tiene stock disponible",
                "Sin stock",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear nuevo item
        ItemSalidaInventario item = new ItemSalidaInventario(producto);
        item.setStockAnterior(stockActual);
        
        // Validar que la cantidad no exceda el stock
        if (item.getCantidad().compareTo(stockActual) > 0) {
            item.setCantidad(stockActual);
        }
        
        itemsSalida.add(item);
        actualizarTabla();
        actualizarTotales();
        
        // Limpiar campo de búsqueda
        txtProducto.setText("");
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        
        for (ItemSalidaInventario item : itemsSalida) {
            Object[] fila = {
                item.getProducto().getCodigo(),
                item.getProducto().getNombre(),
                item.getProducto().getNombreCategoria() != null ? 
                    item.getProducto().getNombreCategoria() : "Sin categoría",
                item.getProducto().getUnidadCompra(),
                item.getStockAnterior(),
                item.getCantidad(),
                item.getCosto(),
                item.getTotal()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void actualizarItemDesdeFila(int fila) {
        if (fila >= 0 && fila < itemsSalida.size()) {
            ItemSalidaInventario item = itemsSalida.get(fila);
            
            try {
                // Actualizar cantidad
                Object valorCantidad = modeloTabla.getValueAt(fila, 5);
                if (valorCantidad != null) {
                    BigDecimal cantidad = new BigDecimal(valorCantidad.toString());
                    
                    // Validar que no exceda el stock
                    if (cantidad.compareTo(item.getStockAnterior()) > 0) {
                        JOptionPane.showMessageDialog(this,
                            "La cantidad no puede ser mayor al stock disponible (" + 
                            item.getStockAnterior() + ")",
                            "Cantidad excedida",
                            JOptionPane.WARNING_MESSAGE);
                        cantidad = item.getStockAnterior();
                        modeloTabla.setValueAt(cantidad, fila, 5);
                    }
                    
                    item.setCantidad(cantidad);
                }
                
                // Actualizar costo
                Object valorCosto = modeloTabla.getValueAt(fila, 6);
                if (valorCosto != null) {
                    BigDecimal costo = new BigDecimal(valorCosto.toString());
                    item.setCosto(costo);
                }
                
                // Actualizar total en la tabla
                modeloTabla.setValueAt(item.getTotal(), fila, 7);
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Valor numérico inválido",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
                actualizarTabla(); // Restaurar valores originales
            }
        }
    }
    
    private void actualizarTotales() {
        BigDecimal totalCostos = BigDecimal.ZERO;
        BigDecimal totalCantidad = BigDecimal.ZERO;
        int totalProductos = itemsSalida.size();
        
        for (ItemSalidaInventario item : itemsSalida) {
            totalCostos = totalCostos.add(item.getTotal());
            totalCantidad = totalCantidad.add(item.getCantidad());
        }
        
        lblTotalCostos.setText(totalCostos.setScale(2, RoundingMode.HALF_UP).toString());
        lblTotalCantidad.setText(totalCantidad.setScale(2, RoundingMode.HALF_UP).toString());
        lblTotalProductos.setText(String.valueOf(totalProductos));
    }
    
    private void borrarItem(ActionEvent e) {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto para eliminar",
                "Ningún producto seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar este producto de la salida?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            itemsSalida.remove(filaSeleccionada);
            actualizarTabla();
            actualizarTotales();
        }
    }
    
    private void editarCantidad(ActionEvent e) {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto para editar la cantidad",
                "Ningún producto seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ItemSalidaInventario item = itemsSalida.get(filaSeleccionada);
        
        // Crear un ItemEntradaInventario temporal para usar con DialogoEditarCantidad
        // (adaptar el item de salida al formato que espera el diálogo)
        DialogoEntradaInventario.ItemEntradaInventario itemTemporal = 
            new DialogoEntradaInventario.ItemEntradaInventario(item.getProducto());
        itemTemporal.setCantidad(item.getCantidad());
        itemTemporal.setCosto(item.getCosto());
        itemTemporal.setPrecio1(item.getPrecio1());
        itemTemporal.setPrecio2(item.getPrecio2());
        itemTemporal.setPrecio3(item.getPrecio3());
        
        // Abrir diálogo de edición
        DialogoEditarCantidad dialogo = new DialogoEditarCantidad(this, itemTemporal);
        dialogo.setVisible(true);
        
        if (dialogo.isConfirmado()) {
            // Validar que la cantidad no exceda el stock
            if (itemTemporal.getCantidad().compareTo(item.getStockAnterior()) > 0) {
                JOptionPane.showMessageDialog(this,
                    "La cantidad no puede ser mayor al stock disponible (" + 
                    item.getStockAnterior() + ")",
                    "Cantidad excedida",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Actualizar item con los nuevos valores
            item.setCantidad(itemTemporal.getCantidad());
            item.setCosto(itemTemporal.getCosto());
            item.setPrecio1(itemTemporal.getPrecio1());
            item.setPrecio2(itemTemporal.getPrecio2());
            item.setPrecio3(itemTemporal.getPrecio3());
            
            actualizarTabla();
            actualizarTotales();
        }
    }
    
    private void mostrarDetalle(ActionEvent e) {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto para ver el detalle",
                "Ningún producto seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ItemSalidaInventario item = itemsSalida.get(filaSeleccionada);
        Producto producto = item.getProducto();
        
        // Crear diálogo de detalle (similar al de productos)
        StringBuilder detalle = new StringBuilder();
        detalle.append("INFORMACIÓN DEL PRODUCTO\n\n");
        detalle.append("Código: ").append(producto.getCodigo()).append("\n");
        detalle.append("Nombre: ").append(producto.getNombre()).append("\n");
        detalle.append("Categoría: ").append(producto.getNombreCategoria() != null ? 
            producto.getNombreCategoria() : "Sin categoría").append("\n");
        detalle.append("Unidad: ").append(producto.getUnidadCompra()).append("\n");
        detalle.append("Stock anterior: ").append(item.getStockAnterior()).append("\n");
        detalle.append("Cantidad a sacar: ").append(item.getCantidad()).append("\n");
        detalle.append("Stock después: ").append(item.getStockAnterior().subtract(item.getCantidad())).append("\n\n");
        detalle.append("PRECIOS\n");
        detalle.append("Costo unitario: ").append(item.getCosto()).append("\n");
        detalle.append("Precio venta 1: ").append(item.getPrecio1()).append("\n");
        detalle.append("Precio venta 2: ").append(item.getPrecio2()).append("\n");
        detalle.append("Precio venta 3: ").append(item.getPrecio3()).append("\n\n");
        detalle.append("TOTAL COSTO: ").append(item.getTotal());
        
        JTextArea areaTexto = new JTextArea(detalle.toString());
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scroll, "Detalle del Producto", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void guardarSalida(ActionEvent e) {
        try {
            // Validaciones
            if (almacenSeleccionado == null) {
                JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un almacén",
                    "Almacén requerido",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (itemsSalida.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Debe agregar al menos un producto",
                    "Sin productos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String motivo = txtMotivo.getText().trim();
            if (motivo.isEmpty()) {
                int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Desea guardar la salida sin especificar un motivo?",
                    "Sin motivo",
                    JOptionPane.YES_NO_OPTION);
                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
                motivo = "Salida sin motivo especificado";
            }
            
            // Validar stock para todos los productos
            for (ItemSalidaInventario item : itemsSalida) {
                BigDecimal stockActual = movimientoDAO.obtenerStockActual(
                    item.getProducto().getIdProducto(), almacenSeleccionado.getIdAlmacen());
                
                if (item.getCantidad().compareTo(stockActual) > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Stock insuficiente para el producto: " + item.getProducto().getNombre() + 
                        "\nStock disponible: " + stockActual + 
                        "\nCantidad solicitada: " + item.getCantidad(),
                        "Stock insuficiente",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Crear movimiento
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setTipoMovimiento("SALIDA");
            movimiento.setIdAlmacen(almacenSeleccionado.getIdAlmacen());
            movimiento.setMotivo(motivo);
            movimiento.setTotalProductos(itemsSalida.size());
            movimiento.setEstado("COMPLETADO");
            movimiento.setIdUsuario(1); // TODO: Obtener ID del usuario actual
            
            // Calcular totales
            BigDecimal totalCantidad = BigDecimal.ZERO;
            BigDecimal totalCosto = BigDecimal.ZERO;
            
            for (ItemSalidaInventario item : itemsSalida) {
                totalCantidad = totalCantidad.add(item.getCantidad());
                totalCosto = totalCosto.add(item.getTotal());
            }
            
            movimiento.setTotalCantidad(totalCantidad);
            movimiento.setTotalCosto(totalCosto);
            
            // Crear detalles
            List<DetalleMovimientoInventario> detalles = new ArrayList<>();
            
            for (ItemSalidaInventario item : itemsSalida) {
                DetalleMovimientoInventario detalle = new DetalleMovimientoInventario();
                detalle.setIdProducto(item.getProducto().getIdProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setCostoUnitario(item.getCosto());
                detalle.setPrecioVenta1(item.getPrecio1());
                detalle.setPrecioVenta2(item.getPrecio2());
                detalle.setPrecioVenta3(item.getPrecio3());
                detalle.setSubtotal(item.getTotal());
                
                detalles.add(detalle);
            }
            
            // Guardar en base de datos
            if (movimientoDAO.guardarMovimientoCompleto(movimiento, detalles)) {
                JOptionPane.showMessageDialog(this,
                    "Salida de inventario guardada exitosamente\n" +
                    "Número: " + movimiento.getNumeroMovimiento(),
                    "Salida guardada",
                    JOptionPane.INFORMATION_MESSAGE);
                
                guardadoExitosamente = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar la salida de inventario",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            System.err.println("Error al guardar salida: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error inesperado al guardar: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generarNumeroSalida() {
        try {
            numeroSalida = movimientoDAO.obtenerSiguienteNumero("SALIDA");
            lblNumeroSalida.setText(String.format("%07d", numeroSalida));
        } catch (Exception e) {
            System.err.println("Error al generar número de salida: " + e.getMessage());
            lblNumeroSalida.setText("0000001");
        }
    }
    
    // Métodos getter
    public boolean isGuardadoExitosamente() {
        return guardadoExitosamente;
    }
    
}