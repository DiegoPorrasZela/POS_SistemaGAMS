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
 * Diálogo para ajuste de inventario
 */
public class DialogoAjusteInventario extends JDialog {
    
    // DAOs
    private ProductoDAO productoDAO;
    private MovimientoInventarioDAO movimientoDAO;
    
    // Componentes principales
    private JTextField txtAlmacen;
    private JButton btnBuscarAlmacen;
    private JTextField txtProducto;
    private JButton btnBuscarProducto;
    private JLabel lblNumeroAjuste;
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
    private List<ItemAjusteInventario> itemsAjuste;
    private int numeroAjuste;
    
    // Variable para indicar si se guardó exitosamente
    private boolean guardadoExitosamente = false;
    
    // Componente padre (opcional para callback)
    private PanelInventarioMovimientos panelPadre;
    
    // Clase interna para items de ajuste
    public static class ItemAjusteInventario {
        private Producto producto;
        private BigDecimal cantidadAnterior;
        private BigDecimal cantidadNueva;
        private BigDecimal diferencia;
        private BigDecimal costo;
        private BigDecimal precio1;
        private BigDecimal precio2;
        private BigDecimal precio3;
        private BigDecimal total;
        
        public ItemAjusteInventario(Producto producto) {
            this.producto = producto;
            this.cantidadAnterior = BigDecimal.ZERO;
            this.cantidadNueva = BigDecimal.ZERO;
            this.diferencia = BigDecimal.ZERO;
            this.costo = producto.getPrecioCompra();
            this.precio1 = producto.getPrecioVenta1();
            this.precio2 = producto.getPrecioVenta2();
            this.precio3 = producto.getPrecioVenta3();
            calcularTotal();
        }
        
        public void calcularTotal() {
            this.total = this.diferencia.abs().multiply(this.costo);
        }
        
        public void calcularDiferencia() {
            this.diferencia = this.cantidadNueva.subtract(this.cantidadAnterior);
            calcularTotal();
        }
        
        // Getters y setters
        public Producto getProducto() { return producto; }
        public BigDecimal getCantidadAnterior() { return cantidadAnterior; }
        public void setCantidadAnterior(BigDecimal cantidadAnterior) { 
            this.cantidadAnterior = cantidadAnterior; 
            calcularDiferencia();
        }
        public BigDecimal getCantidadNueva() { return cantidadNueva; }
        public void setCantidadNueva(BigDecimal cantidadNueva) { 
            this.cantidadNueva = cantidadNueva; 
            calcularDiferencia();
        }
        public BigDecimal getDiferencia() { return diferencia; }
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
        public BigDecimal getTotal() { return total; }
        
        public String getTipoAjuste() {
            if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
                return "SUMA";
            } else if (diferencia.compareTo(BigDecimal.ZERO) < 0) {
                return "RESTA";
            } else {
                return "SIN CAMBIO";
            }
        }
    }
    
    // Constructores
    public DialogoAjusteInventario(Component parent) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null, 
              "Ajuste de Inventario", ModalityType.APPLICATION_MODAL);
        
        // Si el parent es PanelInventarioMovimientos, guardarlo para callback
        if (parent instanceof PanelInventarioMovimientos) {
            this.panelPadre = (PanelInventarioMovimientos) parent;
        }
        
        this.productoDAO = new ProductoDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
        this.itemsAjuste = new ArrayList<>();
        
        initComponents();
        configurarDialog();
        configurarTabla();
        configurarEventos();
        generarNumeroAjuste();
        actualizarTotales();
    }
    
    public DialogoAjusteInventario() {
        super((Frame) null, "Ajuste de Inventario", true);
        
        this.productoDAO = new ProductoDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
        this.itemsAjuste = new ArrayList<>();
        
        initComponents();
        configurarDialog();
        configurarTabla();
        configurarEventos();
        generarNumeroAjuste();
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
        // Color verde para ajuste
        panel.setBackground(new Color(34, 139, 34)); // Forest Green
        panel.setPreferredSize(new Dimension(0, 50));
        
        // Título izquierdo
        JLabel lblTitulo = new JLabel("← Ajuste de Inventario");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Panel derecho con número de ajuste y totales
        JPanel panelDerecho = new JPanel(new GridLayout(3, 2, 5, 2));
        panelDerecho.setBackground(new Color(34, 139, 34));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 15));
        
        Font fontSmall = new Font("Arial", Font.PLAIN, 11);
        Font fontBold = new Font("Arial", Font.BOLD, 12);
        
        JLabel lblNumAjuste = new JLabel("Ajuste N° :");
        lblNumAjuste.setForeground(Color.WHITE);
        lblNumAjuste.setFont(fontSmall);
        lblNumAjuste.setHorizontalAlignment(SwingConstants.RIGHT);
        
        lblNumeroAjuste = new JLabel("0000001");
        lblNumeroAjuste.setForeground(Color.WHITE);
        lblNumeroAjuste.setFont(fontBold);
        lblNumeroAjuste.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel lblCostos = new JLabel("Total Costos :");
        lblCostos.setForeground(Color.WHITE);
        lblCostos.setFont(fontSmall);
        lblCostos.setHorizontalAlignment(SwingConstants.RIGHT);
        
        lblTotalCostos = new JLabel("0");
        lblTotalCostos.setForeground(Color.WHITE);
        lblTotalCostos.setFont(fontBold);
        lblTotalCostos.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel lblCantidad = new JLabel("Total Ajustes :");
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
        
        panelDerecho.add(lblNumAjuste);
        panelDerecho.add(lblNumeroAjuste);
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
        btnBuscarAlmacen.setBackground(new Color(34, 139, 34));
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
        btnBuscarProducto.setBackground(new Color(34, 139, 34));
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
                            "STOCK ANTERIOR", "STOCK NUEVO", "DIFERENCIA", "TIPO", "COSTO", "TOTAL"};
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo permitir editar stock nuevo
                return column == 5; // STOCK NUEVO
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
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(100); // STOCK ANTERIOR
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(100); // STOCK NUEVO
        tablaProductos.getColumnModel().getColumn(6).setPreferredWidth(100); // DIFERENCIA
        tablaProductos.getColumnModel().getColumn(7).setPreferredWidth(80);  // TIPO
        tablaProductos.getColumnModel().getColumn(8).setPreferredWidth(80);  // COSTO
        tablaProductos.getColumnModel().getColumn(9).setPreferredWidth(80);  // TOTAL
        
        // Header con color
        tablaProductos.getTableHeader().setBackground(new Color(180, 180, 180));
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 139, 34));
        panel.setPreferredSize(new Dimension(0, 80));
        
        // Panel de botones izquierdo
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        panelBotones.setBackground(new Color(34, 139, 34));
        
        btnBorrar = crearBoton("Borrar ⊗", new Color(34, 139, 34));
        btnCantidad = crearBoton("Cantidad 📝", new Color(34, 139, 34));
        btnDetalle = crearBoton("Detalle 📋", new Color(34, 139, 34));
        
        panelBotones.add(btnBorrar);
        panelBotones.add(btnCantidad);
        panelBotones.add(btnDetalle);
        
        // Panel central con motivo
        JPanel panelMotivo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelMotivo.setBackground(new Color(34, 139, 34));
        
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
        panelGuardar.setBackground(new Color(34, 139, 34));
        
        btnGuardar = crearBoton("Guardar 💾", new Color(34, 139, 34));
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
        setSize(1200, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
    }
    
    private void configurarTabla() {
        // Renderer para números alineados a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        tablaProductos.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // STOCK ANTERIOR
        tablaProductos.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // STOCK NUEVO
        tablaProductos.getColumnModel().getColumn(6).setCellRenderer(rightRenderer); // DIFERENCIA
        tablaProductos.getColumnModel().getColumn(8).setCellRenderer(rightRenderer); // COSTO
        tablaProductos.getColumnModel().getColumn(9).setCellRenderer(rightRenderer); // TOTAL
        
        // Renderer especial para la columna DIFERENCIA con colores
        DefaultTableCellRenderer diferenciaCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 6 && value != null) { // Columna DIFERENCIA
                    String diferencia = value.toString();
                    try {
                        BigDecimal val = new BigDecimal(diferencia);
                        if (val.compareTo(BigDecimal.ZERO) > 0) {
                            c.setForeground(new Color(34, 139, 34)); // Verde para positivo
                        } else if (val.compareTo(BigDecimal.ZERO) < 0) {
                            c.setForeground(Color.RED); // Rojo para negativo
                        } else {
                            c.setForeground(Color.BLACK); // Negro para cero
                        }
                    } catch (NumberFormatException e) {
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        };
        
        tablaProductos.getColumnModel().getColumn(6).setCellRenderer(diferenciaCellRenderer);
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
        btnGuardar.addActionListener(this::guardarAjuste);
        
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
            itemsAjuste.clear();
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
        
        // Obtener productos del almacén seleccionado
        List<Producto> productos = productoDAO.obtenerProductosPorAlmacen(almacenSeleccionado.getIdAlmacen());
        
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay productos disponibles para este almacén",
                "Sin productos",
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
    
    private void agregarProducto(Producto producto) {
        // Verificar si el producto ya está en la lista
        for (ItemAjusteInventario item : itemsAjuste) {
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
        
        // Crear nuevo item
        ItemAjusteInventario item = new ItemAjusteInventario(producto);
        item.setCantidadAnterior(stockActual);
        item.setCantidadNueva(stockActual); // Inicialmente igual
        
        itemsAjuste.add(item);
        actualizarTabla();
        actualizarTotales();
        
        // Limpiar campo de búsqueda
        txtProducto.setText("");
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        
        for (ItemAjusteInventario item : itemsAjuste) {
            Object[] fila = {
                item.getProducto().getCodigo(),
                item.getProducto().getNombre(),
                item.getProducto().getNombreCategoria() != null ? 
                    item.getProducto().getNombreCategoria() : "Sin categoría",
                item.getProducto().getUnidadCompra(),
                item.getCantidadAnterior(),
                item.getCantidadNueva(),
                item.getDiferencia(),
                item.getTipoAjuste(),
                item.getCosto(),
                item.getTotal()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void actualizarItemDesdeFila(int fila) {
        if (fila >= 0 && fila < itemsAjuste.size()) {
            ItemAjusteInventario item = itemsAjuste.get(fila);
            
            try {
                // Actualizar stock nuevo
                Object valorStockNuevo = modeloTabla.getValueAt(fila, 5);
                if (valorStockNuevo != null) {
                    BigDecimal stockNuevo = new BigDecimal(valorStockNuevo.toString());
                    
                    // Validar que no sea negativo
                    if (stockNuevo.compareTo(BigDecimal.ZERO) < 0) {
                        JOptionPane.showMessageDialog(this,
                            "El stock no puede ser negativo",
                            "Valor inválido",
                            JOptionPane.WARNING_MESSAGE);
                        stockNuevo = BigDecimal.ZERO;
                        modeloTabla.setValueAt(stockNuevo, fila, 5);
                    }
                    
                    item.setCantidadNueva(stockNuevo);
                }
                
                // Actualizar diferencia y tipo en la tabla
                modeloTabla.setValueAt(item.getDiferencia(), fila, 6);
                modeloTabla.setValueAt(item.getTipoAjuste(), fila, 7);
                modeloTabla.setValueAt(item.getTotal(), fila, 9);
                
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
        int totalAjustes = 0;
        int totalProductos = itemsAjuste.size();
        
        for (ItemAjusteInventario item : itemsAjuste) {
            totalCostos = totalCostos.add(item.getTotal());
            if (item.getDiferencia().compareTo(BigDecimal.ZERO) != 0) {
                totalAjustes++;
            }
        }
        
        lblTotalCostos.setText(totalCostos.setScale(2, RoundingMode.HALF_UP).toString());
        lblTotalCantidad.setText(String.valueOf(totalAjustes));
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
            "¿Está seguro que desea eliminar este producto del ajuste?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            itemsAjuste.remove(filaSeleccionada);
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
        
        ItemAjusteInventario item = itemsAjuste.get(filaSeleccionada);
        
        // Solicitar nueva cantidad
        String input = JOptionPane.showInputDialog(this,
            "Ingrese la nueva cantidad para: " + item.getProducto().getNombre() + 
            "\nCantidad actual: " + item.getCantidadAnterior(),
            "Editar Cantidad",
            JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                BigDecimal nuevaCantidad = new BigDecimal(input.trim().replace(",", "."));
                
                if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this,
                        "La cantidad no puede ser negativa",
                        "Valor inválido",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                item.setCantidadNueva(nuevaCantidad);
                actualizarTabla();
                actualizarTotales();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Formato de número inválido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
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
        
        ItemAjusteInventario item = itemsAjuste.get(filaSeleccionada);
        Producto producto = item.getProducto();
        
        // Crear diálogo de detalle
        StringBuilder detalle = new StringBuilder();
        detalle.append("INFORMACIÓN DEL AJUSTE\n\n");
        detalle.append("Código: ").append(producto.getCodigo()).append("\n");
        detalle.append("Nombre: ").append(producto.getNombre()).append("\n");
        detalle.append("Categoría: ").append(producto.getNombreCategoria() != null ? 
            producto.getNombreCategoria() : "Sin categoría").append("\n");
        detalle.append("Unidad: ").append(producto.getUnidadCompra()).append("\n\n");
        
        detalle.append("AJUSTE DE INVENTARIO\n");
        detalle.append("Stock anterior: ").append(item.getCantidadAnterior()).append("\n");
        detalle.append("Stock nuevo: ").append(item.getCantidadNueva()).append("\n");
        detalle.append("Diferencia: ").append(item.getDiferencia()).append("\n");
        detalle.append("Tipo de ajuste: ").append(item.getTipoAjuste()).append("\n\n");
        
        detalle.append("PRECIOS\n");
        detalle.append("Costo unitario: ").append(item.getCosto()).append("\n");
        detalle.append("Precio venta 1: ").append(item.getPrecio1()).append("\n");
        detalle.append("Precio venta 2: ").append(item.getPrecio2()).append("\n");
        detalle.append("Precio venta 3: ").append(item.getPrecio3()).append("\n\n");
        detalle.append("TOTAL COSTO AJUSTE: ").append(item.getTotal());
        
        JTextArea areaTexto = new JTextArea(detalle.toString());
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setPreferredSize(new Dimension(400, 350));
        
        JOptionPane.showMessageDialog(this, scroll, "Detalle del Ajuste", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void guardarAjuste(ActionEvent e) {
        try {
            // Validaciones
            if (almacenSeleccionado == null) {
                JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un almacén",
                    "Almacén requerido",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (itemsAjuste.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Debe agregar al menos un producto",
                    "Sin productos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Verificar que haya al menos un ajuste
            boolean hayAjustes = false;
            for (ItemAjusteInventario item : itemsAjuste) {
                if (item.getDiferencia().compareTo(BigDecimal.ZERO) != 0) {
                    hayAjustes = true;
                    break;
                }
            }
            
            if (!hayAjustes) {
                JOptionPane.showMessageDialog(this,
                    "No hay ajustes para procesar.\nTodos los productos mantienen su stock actual.",
                    "Sin cambios",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String motivo = txtMotivo.getText().trim();
            if (motivo.isEmpty()) {
                int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Desea guardar el ajuste sin especificar un motivo?",
                    "Sin motivo",
                    JOptionPane.YES_NO_OPTION);
                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
                motivo = "Ajuste de inventario sin motivo especificado";
            }
            
            // Mostrar resumen del ajuste
            StringBuilder resumen = new StringBuilder();
            resumen.append("RESUMEN DEL AJUSTE:\n\n");
            for (ItemAjusteInventario item : itemsAjuste) {
                if (item.getDiferencia().compareTo(BigDecimal.ZERO) != 0) {
                    resumen.append("• ").append(item.getProducto().getNombre())
                           .append(": ").append(item.getCantidadAnterior())
                           .append(" → ").append(item.getCantidadNueva())
                           .append(" (").append(item.getTipoAjuste()).append(")\n");
                }
            }
            resumen.append("\n¿Confirma el ajuste de inventario?");
            
            int confirmacion = JOptionPane.showConfirmDialog(this,
                resumen.toString(),
                "Confirmar Ajuste de Inventario",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Crear movimiento
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setTipoMovimiento("AJUSTE");
            movimiento.setIdAlmacen(almacenSeleccionado.getIdAlmacen());
            movimiento.setMotivo(motivo);
            movimiento.setEstado("COMPLETADO");
            movimiento.setIdUsuario(1); // TODO: Obtener ID del usuario actual
            
            // Calcular totales (solo productos con ajustes)
            BigDecimal totalCantidad = BigDecimal.ZERO;
            BigDecimal totalCosto = BigDecimal.ZERO;
            int productosAjustados = 0;
            
            for (ItemAjusteInventario item : itemsAjuste) {
                if (item.getDiferencia().compareTo(BigDecimal.ZERO) != 0) {
                    totalCantidad = totalCantidad.add(item.getDiferencia().abs());
                    totalCosto = totalCosto.add(item.getTotal());
                    productosAjustados++;
                }
            }
            
            movimiento.setTotalCantidad(totalCantidad);
            movimiento.setTotalCosto(totalCosto);
            movimiento.setTotalProductos(productosAjustados);
            
            // Crear detalles (solo para productos con cambios)
            List<DetalleMovimientoInventario> detalles = new ArrayList<>();
            
            for (ItemAjusteInventario item : itemsAjuste) {
                if (item.getDiferencia().compareTo(BigDecimal.ZERO) != 0) {
                    DetalleMovimientoInventario detalle = new DetalleMovimientoInventario();
                    detalle.setIdProducto(item.getProducto().getIdProducto());
                    detalle.setCantidad(item.getCantidadNueva()); // Para ajuste, guardamos la cantidad final
                    detalle.setCostoUnitario(item.getCosto());
                    detalle.setPrecioVenta1(item.getPrecio1());
                    detalle.setPrecioVenta2(item.getPrecio2());
                    detalle.setPrecioVenta3(item.getPrecio3());
                    detalle.setSubtotal(item.getTotal());
                    detalle.setStockAnterior(item.getCantidadAnterior());
                    detalle.setStockNuevo(item.getCantidadNueva());
                    
                    detalles.add(detalle);
                }
            }
            
            // Guardar en base de datos
            if (movimientoDAO.guardarMovimientoCompleto(movimiento, detalles)) {
                JOptionPane.showMessageDialog(this,
                    "Ajuste de inventario guardado exitosamente\n" +
                    "Número: " + movimiento.getNumeroMovimiento() + "\n" +
                    "Productos ajustados: " + productosAjustados,
                    "Ajuste guardado",
                    JOptionPane.INFORMATION_MESSAGE);
                
                guardadoExitosamente = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar el ajuste de inventario",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            System.err.println("Error al guardar ajuste: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error inesperado al guardar: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generarNumeroAjuste() {
        try {
            numeroAjuste = movimientoDAO.obtenerSiguienteNumero("AJUSTE");
            lblNumeroAjuste.setText(String.format("%07d", numeroAjuste));
        } catch (Exception e) {
            System.err.println("Error al generar número de ajuste: " + e.getMessage());
            lblNumeroAjuste.setText("0000001");
        }
    }
    
    // Métodos getter
    public boolean isGuardadoExitosamente() {
        return guardadoExitosamente;
    }
}