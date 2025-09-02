/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Diálogo para mostrar información detallada del producto
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.dao.MovimientoInventarioDAO;
import com.pos.sistemagams.modelo.Producto;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;

/**
 *
 * @author Diego
 */
/**
 * Diálogo modal para mostrar información del producto
 */
public class DialogoInformacionProducto extends JDialog {
    
    private Producto producto;
    private String rolUsuario;
    private ProductoDAO productoDAO;
    private MovimientoInventarioDAO movimientoDAO;
    
    // Componentes
    private JLabel lblTitulo;
    private JPanel panelImagen;
    private JPanel panelInformacion;
    private JButton btnCerrar;
    private JLabel lblImagenProducto;
    private JLabel lblCodigoBarras;
    
    public DialogoInformacionProducto(Frame parent, Producto producto, String rolUsuario) {
        super(parent, "Información Producto", true);
        this.producto = producto;
        this.rolUsuario = rolUsuario;
        this.productoDAO = new ProductoDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
        
        initComponents();
        configurarDialogo();
        cargarDatosProducto();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        
        // Header con título
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(70, 130, 180));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        lblTitulo = new JLabel("← Información Producto");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        
        // Panel central con imagen y datos
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelCentral.setBackground(Color.WHITE);
        
        // Panel de imagen (lado izquierdo)
        crearPanelImagen();
        
        // Panel de información (lado derecho)
        crearPanelInformacion();
        
        panelCentral.add(panelImagen, BorderLayout.WEST);
        panelCentral.add(panelInformacion, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCerrar.setBackground(new Color(70, 130, 180));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setPreferredSize(new Dimension(100, 35));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnCerrar);
        
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void crearPanelImagen() {
        panelImagen = new JPanel();
        panelImagen.setLayout(new BoxLayout(panelImagen, BoxLayout.Y_AXIS));
        panelImagen.setBackground(Color.WHITE);
        panelImagen.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        panelImagen.setPreferredSize(new Dimension(280, 450)); // Aumentado de 400 a 450 para mejor proporción
        
        // Imagen del producto
        lblImagenProducto = new JLabel();
        lblImagenProducto.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagenProducto.setVerticalAlignment(SwingConstants.CENTER);
        lblImagenProducto.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        lblImagenProducto.setPreferredSize(new Dimension(260, 280));
        lblImagenProducto.setMaximumSize(new Dimension(260, 280));
        lblImagenProducto.setBackground(new Color(245, 245, 245));
        lblImagenProducto.setOpaque(true);
        
        // Código de barras
        JPanel panelCodigoBarras = new JPanel();
        panelCodigoBarras.setLayout(new BoxLayout(panelCodigoBarras, BoxLayout.Y_AXIS));
        panelCodigoBarras.setBackground(Color.WHITE);
        panelCodigoBarras.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelCodigoBarras.setPreferredSize(new Dimension(260, 80));
        panelCodigoBarras.setMaximumSize(new Dimension(260, 80));
        
        // Simulación de código de barras (manteniendo el original)
        JPanel barrasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                // Dibujar barras simuladas
                for (int i = 0; i < 50; i++) {
                    if (i % 3 == 0 || i % 7 == 0) {
                        g.fillRect(i * 3, 0, 2, 20);
                    } else {
                        g.fillRect(i * 3, 0, 1, 20);
                    }
                }
            }
        };
        barrasPanel.setPreferredSize(new Dimension(150, 20));
        barrasPanel.setMaximumSize(new Dimension(150, 20));
        barrasPanel.setBackground(Color.WHITE);
        barrasPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblCodigoBarras = new JLabel();
        lblCodigoBarras.setFont(new Font("Monospaced", Font.BOLD, 10));
        lblCodigoBarras.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCodigoBarras.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelCodigoBarras.add(barrasPanel);
        panelCodigoBarras.add(Box.createVerticalStrut(5));
        panelCodigoBarras.add(lblCodigoBarras);
        
        panelImagen.add(lblImagenProducto);
        panelImagen.add(Box.createVerticalStrut(10));
        panelImagen.add(panelCodigoBarras);
    }
    
    private void crearPanelInformacion() {
        panelInformacion = new JPanel();
        panelInformacion.setLayout(new GridBagLayout());
        panelInformacion.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 10, 6, 20); // Reducido el espaciado vertical de 8 a 6 para que quepa más información
        
        int fila = 0;
        
        // Información básica
        agregarCampo("Clave:", producto.getCodigo(), gbc, fila++);
        agregarCampo("Nombre del producto/Descripción:", producto.getNombre(), gbc, fila++);
        agregarCampo("Categoría:", producto.getNombreCategoria() != null ? producto.getNombreCategoria() : "Sin categoría", gbc, fila++);
        agregarCampo("Departamento/Vitrina/Estante:", producto.getNombreDepartamento() != null ? producto.getNombreDepartamento() : "Sin departamento", gbc, fila++);
        agregarCampo("Proveedor:", producto.getNombreProveedor() != null ? producto.getNombreProveedor() : "Sin proveedor", gbc, fila++);
        agregarCampo("Puesto/Almacén/Giro:", producto.getNombreAlmacen() != null ? producto.getNombreAlmacen() : "Sin almacén", gbc, fila++);
        
        // Costo (solo para ADMIN)
        if ("ADMIN".equalsIgnoreCase(rolUsuario)) {
            agregarCampo("Costo:", String.format("%.2f", producto.getPrecioCompra()), gbc, fila++);
        }
        
        // U.M.
        agregarCampo("U.M.:", producto.getUnidadCompra() != null ? producto.getUnidadCompra() : "UND", gbc, fila++);
        
        // Precios
        agregarCampo("Precios (1/2/3):", 
            String.format("%.2f / %.2f / %.2f", 
                producto.getPrecioVenta1(),
                producto.getPrecioVenta2(),
                producto.getPrecioVenta3()), gbc, fila++);
        
        // Mayoreo
        if (producto.getPrecioMayoreo() != null && producto.getPrecioMayoreo().compareTo(java.math.BigDecimal.ZERO) > 0) {
            agregarCampo("P. Mayoreo:", String.format("%.2f", producto.getPrecioMayoreo()), gbc, fila++);
            agregarCampo("Cant Mayoreo:", String.valueOf(producto.getCantidadMayoreo()), gbc, fila++);
        } else {
            agregarCampo("P. Mayoreo:", "0.00", gbc, fila++);
            agregarCampo("Cant Mayoreo:", "0", gbc, fila++);
        }
        
        // Stock real usando el método mejorado de DialogoDetalleProducto
        BigDecimal stockActual = BigDecimal.ZERO;
        if (producto.getIdAlmacen() > 0 && movimientoDAO != null) {
            stockActual = movimientoDAO.obtenerStockActual(producto.getIdProducto(), producto.getIdAlmacen());
        } else {
            try {
                int stockReal = productoDAO.obtenerStockProducto(producto.getIdProducto());
                stockActual = BigDecimal.valueOf(stockReal);
            } catch (Exception e) {
                System.err.println("Error al obtener stock: " + e.getMessage());
            }
        }
        
        agregarCampo("Existencia Actual:", stockActual.toString(), gbc, fila++);
        
        agregarCampo("Stock Mínimo:", String.valueOf(producto.getStockMinimo()), gbc, fila++);
        agregarCampo("Stock Máximo:", String.valueOf(producto.getStockMaximo()), gbc, fila++);
        
        // IGV
        String igvTexto = producto.isAplicaIgv() ? 
            "IGV - " + String.format("%.2f", producto.getPorcentajeIgv()) + "%" : 
            "Sin IGV";
        agregarCampo("Impuesto:", igvTexto, gbc, fila++);
        
        // Fecha creación - usando el método corregido de DialogoDetalleProducto
        if (producto.getFechaCreacion() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(producto.getFechaCreacion());
                agregarCampo("Fecha Creación:", sdf.format(timestamp), gbc, fila++);
            } catch (Exception e) {
                agregarCampo("Fecha Creación:", "Error en fecha", gbc, fila++);
                System.err.println("Error al formatear fecha: " + e.getMessage());
            }
        } else {
            agregarCampo("Fecha Creación:", "Sin fecha", gbc, fila++);
        }
        
        // Estado
        agregarCampo("Activo:", producto.isEstado() ? "Sí" : "No", gbc, fila++);
    }
    
    private void agregarCampo(String etiqueta, String valor, GridBagConstraints gbc, int fila) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 11));
        lblEtiqueta.setForeground(Color.BLACK);
        lblEtiqueta.setPreferredSize(new Dimension(200, 25)); // Ancho fijo para las etiquetas
        panelInformacion.add(lblEtiqueta, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblValor = new JLabel(valor != null ? valor : "");
        lblValor.setFont(new Font("Arial", Font.PLAIN, 11));
        lblValor.setForeground(new Color(70, 130, 180));
        panelInformacion.add(lblValor, gbc);
    }
    
    private void cargarDatosProducto() {
        if (producto == null) return;
        
        // Cargar imagen usando el método mejorado de DialogoDetalleProducto
        cargarImagenProducto();
        
        // Cargar código de barras
        if (producto.getCodigo() != null) {
            lblCodigoBarras.setText(producto.getCodigo());
        } else {
            lblCodigoBarras.setText("Sin código");
        }
    }
    
    private void cargarImagenProducto() {
        if (producto.getImagenPath() != null && !producto.getImagenPath().isEmpty()) {
            File archivoImagen = new File(producto.getImagenPath());
            if (archivoImagen.exists()) {
                try {
                    BufferedImage imagen = ImageIO.read(archivoImagen);
                    
                    // Redimensionar manteniendo proporción
                    int anchoMax = 240;
                    int altoMax = 260;
                    
                    int anchoOriginal = imagen.getWidth();
                    int altoOriginal = imagen.getHeight();
                    
                    double escalaAncho = (double) anchoMax / anchoOriginal;
                    double escalaAlto = (double) altoMax / altoOriginal;
                    double escala = Math.min(escalaAncho, escalaAlto);
                    
                    int nuevoAncho = (int) (anchoOriginal * escala);
                    int nuevoAlto = (int) (altoOriginal * escala);
                    
                    Image imagenRedimensionada = imagen.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
                    ImageIcon icono = new ImageIcon(imagenRedimensionada);
                    
                    lblImagenProducto.setIcon(icono);
                    lblImagenProducto.setText("");
                    
                } catch (IOException e) {
                    mostrarImagenPorDefecto();
                    System.err.println("Error al cargar imagen: " + e.getMessage());
                }
            } else {
                mostrarImagenPorDefecto();
            }
        } else {
            mostrarImagenPorDefecto();
        }
    }
    
    private void mostrarImagenPorDefecto() {
        lblImagenProducto.setIcon(null);
        lblImagenProducto.setText("<html><center>" +
                                 "<div style='font-size: 48px; margin-bottom: 10px;'>📦</div>" +
                                 "<div style='color: #888888;'>Sin imagen disponible</div>" +
                                 "</center></html>");
        lblImagenProducto.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagenProducto.setVerticalAlignment(SwingConstants.CENTER);
    }
    
    private void configurarDialogo() {
        setSize(800, 650); // Aumentado la altura de 550 a 650 para mostrar toda la información
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        
        // Tecla ESC para cerrar
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        
        setFocusable(true);
    }
}