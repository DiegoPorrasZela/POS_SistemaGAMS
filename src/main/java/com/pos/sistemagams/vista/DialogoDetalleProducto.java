/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.pos.sistemagams.vista;

import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.dao.MovimientoInventarioDAO;
import com.pos.sistemagams.modelo.Producto;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Diálogo para mostrar el detalle completo de un producto
 */
public class DialogoDetalleProducto extends JDialog {
    
    private ProductoDAO productoDAO;
    private MovimientoInventarioDAO movimientoDAO;
    private Producto producto;
    
    // Componentes de la interfaz
    private JLabel lblImagenProducto;
    private JLabel lblCodigoBarras;
    
    // Campos de información
    private JLabel lblClave;
    private JLabel lblDescripcion;
    private JLabel lblCategoria;
    private JLabel lblDepartamento;
    private JLabel lblProveedor;
    private JLabel lblCosto;
    private JLabel lblUnidadVenta;
    private JLabel lblPrecios;
    private JLabel lblPrecioMayoreo;
    private JLabel lblCantMayoreo;
    private JLabel lblInventario;
    private JLabel lblExistencia;
    private JLabel lblExistenciaMinima;
    private JLabel lblExistenciaMaxima;
    private JLabel lblImpuesto;
    private JLabel lblFechaCreacion;
    private JLabel lblActivo;
    
    public DialogoDetalleProducto(Component parent, Producto producto) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null, 
              "Información Producto", ModalityType.APPLICATION_MODAL);
        
        this.productoDAO = new ProductoDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
        this.producto = producto;
        
        initComponents();
        configurarDialog();
        cargarDatosProducto();
        configurarEventos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(240, 248, 255));
        
        // Header
        JPanel headerPanel = crearHeader();
        
        // Panel de contenido
        JPanel contenidoPanel = crearPanelContenido();
        
        // Panel de botón
        JPanel botonPanel = crearPanelBoton();
        
        panelPrincipal.add(headerPanel, BorderLayout.NORTH);
        panelPrincipal.add(contenidoPanel, BorderLayout.CENTER);
        panelPrincipal.add(botonPanel, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel crearHeader() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(70, 130, 180));
        panel.setPreferredSize(new Dimension(0, 50));
        
        JLabel btnVolver = new JLabel("← Información Producto");
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Arial", Font.BOLD, 16));
        btnVolver.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        panel.add(btnVolver);
        
        return panel;
    }
    
    private JPanel crearPanelContenido() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel izquierdo - Imagen del producto
        JPanel panelIzquierdo = crearPanelImagen();
        
        // Panel derecho - Información del producto
        JPanel panelDerecho = crearPanelInformacion();
        
        panel.add(panelIzquierdo, BorderLayout.WEST);
        panel.add(panelDerecho, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelImagen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(300, 400));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        
        // Imagen del producto
        lblImagenProducto = new JLabel();
        lblImagenProducto.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagenProducto.setVerticalAlignment(SwingConstants.CENTER);
        lblImagenProducto.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        lblImagenProducto.setPreferredSize(new Dimension(280, 280));
        lblImagenProducto.setBackground(Color.WHITE);
        lblImagenProducto.setOpaque(true);
        
        // Código de barras
        lblCodigoBarras = new JLabel();
        lblCodigoBarras.setHorizontalAlignment(SwingConstants.CENTER);
        lblCodigoBarras.setVerticalAlignment(SwingConstants.CENTER);
        lblCodigoBarras.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        lblCodigoBarras.setPreferredSize(new Dimension(280, 80));
        lblCodigoBarras.setBackground(Color.WHITE);
        lblCodigoBarras.setOpaque(true);
        lblCodigoBarras.setFont(new Font("Courier New", Font.BOLD, 12));
        
        panel.add(lblImagenProducto, BorderLayout.CENTER);
        panel.add(lblCodigoBarras, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Crear campos de información
        crearCamposInformacion();
        
        // Organizar campos en un grid
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 30);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        int fila = 0;
        
        // Clave
        agregarCampo(gridPanel, gbc, fila++, "Clave:", lblClave, true);
        
        // Nombre del producto/Descripción
        agregarCampo(gridPanel, gbc, fila++, "Nombre del producto/Descripción:", lblDescripcion, true);
        
        // Categoría
        agregarCampo(gridPanel, gbc, fila++, "Categoría:", lblCategoria, false);
        
        // Departamento
        agregarCampo(gridPanel, gbc, fila++, "Departamento/Vitrina/Estante:", lblDepartamento, false);
        
        // Proveedor
        agregarCampo(gridPanel, gbc, fila++, "Proveedor:", lblProveedor, false);
        
        // Puesto/Almacén/Giro
        agregarCampo(gridPanel, gbc, fila++, "Puesto/Almacén/Giro:", lblInventario, true);
        
        // Costo y Unidad de medida (en la misma fila)
        agregarCampoDoble(gridPanel, gbc, fila++, "Costo:", lblCosto, "U.M.:", lblUnidadVenta);
        
        // Precios
        agregarCampo(gridPanel, gbc, fila++, "Precios (1/2/3):", lblPrecios, false);
        
        // Precio Mayoreo y Cantidad Mayoreo (en la misma fila)
        agregarCampoDoble(gridPanel, gbc, fila++, "P. Mayoreo:", lblPrecioMayoreo, "Cant Mayoreo:", lblCantMayoreo);
        
        // Existencia actual
        agregarCampo(gridPanel, gbc, fila++, "Existencia Actual:", lblExistencia, true);
        
        // Stock Mínimo y Stock Máximo (en la misma fila)
        agregarCampoDoble(gridPanel, gbc, fila++, "Stock Mínimo:", lblExistenciaMinima, "Stock Máximo:", lblExistenciaMaxima);
        
        // Impuesto
        agregarCampo(gridPanel, gbc, fila++, "Impuesto:", lblImpuesto, false);
        
        // Fecha de creación
        agregarCampo(gridPanel, gbc, fila++, "Fecha Creación:", lblFechaCreacion, false);
        
        // Estado activo
        agregarCampo(gridPanel, gbc, fila++, "Activo:", lblActivo, true);
        
        panel.add(gridPanel);
        
        return panel;
    }
    
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String titulo, JLabel campo, boolean destacado) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitulo.setPreferredSize(new Dimension(220, 25));
        panel.add(lblTitulo, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        if (destacado) {
            campo.setFont(new Font("Arial", Font.BOLD, 12));
        }
        panel.add(campo, gbc);
    }
    
    private void agregarCampoDoble(JPanel panel, GridBagConstraints gbc, int fila, String titulo1, JLabel campo1, String titulo2, JLabel campo2) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblTitulo1 = new JLabel(titulo1);
        lblTitulo1.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitulo1.setPreferredSize(new Dimension(220, 25));
        panel.add(lblTitulo1, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo1, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        JLabel lblTitulo2 = new JLabel(titulo2);
        lblTitulo2.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitulo2.setPreferredSize(new Dimension(120, 25));
        panel.add(lblTitulo2, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo2, gbc);
    }
    
    private void crearCamposInformacion() {
        lblClave = new JLabel();
        lblDescripcion = new JLabel();
        lblCategoria = new JLabel();
        lblDepartamento = new JLabel();
        lblProveedor = new JLabel();
        lblCosto = new JLabel();
        lblUnidadVenta = new JLabel();
        lblPrecios = new JLabel();
        lblPrecioMayoreo = new JLabel();
        lblCantMayoreo = new JLabel();
        lblInventario = new JLabel();
        lblExistencia = new JLabel();
        lblExistenciaMinima = new JLabel();
        lblExistenciaMaxima = new JLabel();
        lblImpuesto = new JLabel();
        lblFechaCreacion = new JLabel();
        lblActivo = new JLabel();
        
        // Configurar fuentes por defecto
        Font fuenteNormal = new Font("Arial", Font.PLAIN, 12);
        Font fuenteNegrita = new Font("Arial", Font.BOLD, 12);
        
        lblClave.setFont(fuenteNegrita);
        lblDescripcion.setFont(fuenteNegrita);
        lblCategoria.setFont(fuenteNormal);
        lblDepartamento.setFont(fuenteNormal);
        lblProveedor.setFont(fuenteNormal);
        lblCosto.setFont(fuenteNormal);
        lblUnidadVenta.setFont(fuenteNegrita);
        lblPrecios.setFont(fuenteNormal);
        lblPrecioMayoreo.setFont(fuenteNormal);
        lblCantMayoreo.setFont(fuenteNormal);
        lblInventario.setFont(fuenteNegrita);
        lblExistencia.setFont(fuenteNegrita);
        lblExistenciaMinima.setFont(fuenteNormal);
        lblExistenciaMaxima.setFont(fuenteNormal);
        lblImpuesto.setFont(fuenteNormal);
        lblFechaCreacion.setFont(fuenteNormal);
        lblActivo.setFont(fuenteNegrita);
    }
    
    private JPanel crearPanelBoton() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setPreferredSize(new Dimension(100, 35));
        btnCerrar.setBackground(new Color(70, 130, 180));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        
        panel.add(btnCerrar);
        
        return panel;
    }
    
    private void configurarDialog() {
        setSize(950, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void configurarEventos() {
        // Escape para cerrar
        Action cerrarAction = new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        };
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", cerrarAction);
        
        // Enter para cerrar también
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "cerrar");
    }
    
    private void cargarDatosProducto() {
        if (producto == null) return;
        
        // Obtener stock actual del almacén específico
        BigDecimal stockActual = BigDecimal.ZERO;
        if (producto.getIdAlmacen() > 0) {
            stockActual = movimientoDAO.obtenerStockActual(producto.getIdProducto(), producto.getIdAlmacen());
        }
        
        // Cargar datos básicos
        lblClave.setText(producto.getCodigo() != null ? producto.getCodigo() : "Sin código");
        
        // Descripción con HTML para manejo de texto largo
        String descripcion = producto.getNombre() != null ? producto.getNombre() : "Sin descripción";
        if (descripcion.length() > 50) {
            lblDescripcion.setText("<html><div style='width: 300px;'>" + descripcion + "</div></html>");
        } else {
            lblDescripcion.setText(descripcion);
        }
        
        lblCategoria.setText(producto.getNombreCategoria() != null ? producto.getNombreCategoria() : "Sin categoría");
        lblDepartamento.setText(producto.getNombreDepartamento() != null ? producto.getNombreDepartamento() : "Sin departamento");
        lblProveedor.setText(producto.getNombreProveedor() != null ? producto.getNombreProveedor() : "Sin proveedor");
        lblInventario.setText(producto.getNombreAlmacen() != null ? producto.getNombreAlmacen() : "Sin almacén");
        
        // Precios y costos
        lblCosto.setText(formatearPrecio(producto.getPrecioCompra()));
        lblUnidadVenta.setText(producto.getUnidadCompra() != null ? producto.getUnidadCompra() : "UND");
        
        lblPrecios.setText(String.format("%s / %s / %s", 
            formatearPrecio(producto.getPrecioVenta1()),
            formatearPrecio(producto.getPrecioVenta2()),
            formatearPrecio(producto.getPrecioVenta3())));
            
        lblPrecioMayoreo.setText(formatearPrecio(producto.getPrecioMayoreo()));
        lblCantMayoreo.setText(String.valueOf(producto.getCantidadMayoreo()));
        
        // Stock e inventario
        lblExistencia.setText(stockActual.toString());
        lblExistenciaMinima.setText(String.valueOf(producto.getStockMinimo()));
        lblExistenciaMaxima.setText(String.valueOf(producto.getStockMaximo()));
        
        // Impuesto
        if (producto.isAplicaIgv() && producto.getPorcentajeIgv() != null) {
            lblImpuesto.setText(String.format("IGV - %s%%", formatearPrecio(producto.getPorcentajeIgv())));
        } else {
            lblImpuesto.setText("Sin impuesto");
        }
        
        // Fecha de creación - CORREGIDA
        if (producto.getFechaCreacion() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(producto.getFechaCreacion());
                lblFechaCreacion.setText(sdf.format(timestamp));
            } catch (Exception e) {
                lblFechaCreacion.setText("Error en fecha");
                System.err.println("Error al formatear fecha: " + e.getMessage());
            }
        } else {
            lblFechaCreacion.setText("Sin fecha");
        }
        
        // Estado
        lblActivo.setText(producto.isEstado() ? "Sí" : "No");
        lblActivo.setForeground(producto.isEstado() ? new Color(0, 150, 0) : Color.RED);
        
        // Cargar imagen
        cargarImagenProducto();
        
        // Cargar código de barras
        cargarCodigoBarras();
    }
    
    private String formatearPrecio(java.math.BigDecimal precio) {
        if (precio == null) return "0.00";
        return String.format("%.2f", precio);
    }
    
    private void cargarImagenProducto() {
        if (producto.getImagenPath() != null && !producto.getImagenPath().isEmpty()) {
            File archivoImagen = new File(producto.getImagenPath());
            if (archivoImagen.exists()) {
                try {
                    BufferedImage imagen = ImageIO.read(archivoImagen);
                    
                    // Redimensionar manteniendo proporción
                    int anchoMax = 250;
                    int altoMax = 250;
                    
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
    
    private void cargarCodigoBarras() {
        if (producto.getCodigo() != null) {
            StringBuilder barras = new StringBuilder();
            barras.append("<html><center>");
            barras.append("<div style='font-family: monospace; font-size: 10px;'>");
            barras.append("||||  ||  |||||  ||  ||||  |  ||||||  |||<br>");
            barras.append("||  ||||  ||  ||  ||||  ||  ||  ||||  |||<br>");
            barras.append("</div>");
            barras.append("<div style='font-weight: bold; margin-top: 5px;'>");
            barras.append(producto.getCodigo());
            barras.append("</div>");
            barras.append("</center></html>");
            
            lblCodigoBarras.setText(barras.toString());
        } else {
            lblCodigoBarras.setText("<html><center>Sin código</center></html>");
        }
    }
    
    // Método estático para mostrar fácilmente el diálogo
    public static void mostrar(Component parent, Producto producto) {
        DialogoDetalleProducto dialogo = new DialogoDetalleProducto(parent, producto);
        dialogo.setVisible(true);
    }
}