/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Diálogo para buscar y seleccionar productos
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.modelo.Producto;
import com.pos.sistemagams.dao.ProductoDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
/**
 *
 * @author Diego
 */
/**
 * Diálogo modal para buscar productos
 */
public class DialogoBuscarProductos extends JDialog {
    
    private ProductoDAO productoDAO;
    private DefaultTableModel modeloTabla;
    private JTable tablaProductos;
    private JTextField txtBuscar;
    private JButton btnSeleccionar;
    private JButton btnCancelar;
    
    private Producto productoSeleccionado;
    private List<Producto> productos;
    
    public DialogoBuscarProductos(Frame parent) {
        super(parent, "Buscar Productos", true);
        this.productoDAO = new ProductoDAO();
        
        initComponents();
        configurarDialogo();
        cargarProductos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel superior con búsqueda
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBackground(new Color(70, 130, 180));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setForeground(Color.WHITE);
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        
        txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 12));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarProductos();
            }
        });
        
        JButton btnBuscar = new JButton("🔍");
        btnBuscar.setPreferredSize(new Dimension(40, 25));
        btnBuscar.addActionListener(e -> filtrarProductos());
        
        panelSuperior.add(lblBuscar);
        panelSuperior.add(txtBuscar);
        panelSuperior.add(btnBuscar);
        
        // Tabla de productos
        String[] columnas = {"CLAVE", "DESCRIPCION", "U. M.", "PRECIO", "EXISTEN"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setRowHeight(25);
        
        // Configurar ancho de columnas
        if (tablaProductos.getColumnModel().getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(100);  // CLAVE
            tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(300);  // DESCRIPCION
            tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(60);   // U.M.
            tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(80);   // PRECIO
            tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(80);   // EXISTEN
        }
        
        // Configurar renderizadores
        configurarRenderizadores();
        
        // Doble clic para seleccionar
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarProducto();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setPreferredSize(new Dimension(650, 400));
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(Color.WHITE);
        
        btnSeleccionar = new JButton("✓ Seleccionar");
        btnSeleccionar.setFont(new Font("Arial", Font.BOLD, 12));
        btnSeleccionar.setBackground(new Color(34, 139, 34));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setPreferredSize(new Dimension(120, 35));
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.addActionListener(e -> seleccionarProducto());
        
        btnCancelar = new JButton("✗ Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(220, 20, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnCancelar);
        
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void configurarRenderizadores() {
        // Renderer para números alineados a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        tablaProductos.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // PRECIO
        tablaProductos.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // EXISTEN
        
        // Renderer para filas alternadas
        DefaultTableCellRenderer filaRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(245, 245, 245));
                    }
                }
                
                // Alineación según la columna
                if (column == 3 || column == 4) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return c;
            }
        };
        
        // Aplicar renderer a todas las columnas
        for (int i = 0; i < tablaProductos.getColumnCount(); i++) {
            if (i != 3 && i != 4) { // Excepto PRECIO y EXISTEN que ya tienen su renderer
                tablaProductos.getColumnModel().getColumn(i).setCellRenderer(filaRenderer);
            }
        }
    }
    
    private void configurarDialogo() {
        setSize(700, 500);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Focus en el campo de búsqueda al abrir
        SwingUtilities.invokeLater(() -> {
            txtBuscar.requestFocus();
        });
    }
    
    private void cargarProductos() {
        try {
            productos = productoDAO.obtenerTodosLosProductos();
            actualizarTabla(productos);
            
            System.out.println("✅ Productos cargados en diálogo: " + productos.size());
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar productos: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al cargar productos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filtrarProductos() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        
        if (filtro.isEmpty()) {
            actualizarTabla(productos);
            return;
        }
        
        List<Producto> productosFiltrados = productos.stream()
            .filter(p -> 
                p.getCodigo().toLowerCase().contains(filtro) ||
                p.getNombre().toLowerCase().contains(filtro) ||
                (p.getNombreCategoria() != null && p.getNombreCategoria().toLowerCase().contains(filtro))
            )
            .toList();
        
        actualizarTabla(productosFiltrados);
    }
    
    private void actualizarTabla(List<Producto> productosAMostrar) {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Agregar productos
        for (Producto producto : productosAMostrar) {
            // Obtener stock real del producto
            int stockReal = 0;
            try {
                stockReal = productoDAO.obtenerStockProducto(producto.getIdProducto());
            } catch (Exception e) {
                System.err.println("Error al obtener stock para producto " + producto.getCodigo() + ": " + e.getMessage());
            }
            
            Object[] fila = {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getUnidadCompra(),
                String.format("%.2f", producto.getPrecioVenta1()),
                String.valueOf(stockReal)  // Stock real en lugar de "0"
            };
            modeloTabla.addRow(fila);
        }
        
        // Actualizar contador en el título
        setTitle("Buscar Productos (" + productosAMostrar.size() + " encontrados)");
    }
    
    private void seleccionarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto de la lista",
                "Sin selección",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Obtener el código del producto seleccionado
            String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
            String stockTexto = (String) modeloTabla.getValueAt(filaSeleccionada, 4);
            
            // Verificar stock antes de seleccionar
            int stock = Integer.parseInt(stockTexto);
            if (stock <= 0) {
                JOptionPane.showMessageDialog(this,
                    "No se puede seleccionar este producto.\n" +
                    "Stock disponible: " + stock,
                    "Sin stock",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Buscar el producto completo
            productoSeleccionado = productos.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
            
            if (productoSeleccionado != null) {
                System.out.println("✅ Producto seleccionado: " + productoSeleccionado.getNombre() + " (Stock: " + stock + ")");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al obtener el producto seleccionado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al seleccionar producto: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al seleccionar producto: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Getter
    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }
}