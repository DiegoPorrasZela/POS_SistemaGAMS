/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Diálogo para seleccionar precio de venta del producto
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.modelo.Producto;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
/**
 *
 * @author Diego
 */
/**
 * Diálogo modal para seleccionar precio
 */
public class DialogoSeleccionarPrecio extends JDialog {
    
    private Producto producto;
    private BigDecimal precioSeleccionado;
    private boolean precioConfirmado = false;
    
    // Componentes
    private JLabel lblTitulo;
    private JButton btnPrecio1;
    private JButton btnPrecio2;
    private JButton btnPrecio3;
    
    public DialogoSeleccionarPrecio(Frame parent, Producto producto) {
        super(parent, "Seleccione Precio", true);
        this.producto = producto;
        
        initComponents();
        configurarDialogo();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal con fondo azul
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(new Color(70, 130, 180));
        
        // Título
        lblTitulo = new JLabel("← Seleccione Precio", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));
        
        // Panel de contenido con fondo blanco
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Botones de precios
        btnPrecio1 = crearBotonPrecio("Precio 1", producto.getPrecioVenta1(), new Color(70, 130, 180));
        btnPrecio2 = crearBotonPrecio("Precio 2", producto.getPrecioVenta2(), new Color(255, 140, 0));
        btnPrecio3 = crearBotonPrecio("Precio 3", producto.getPrecioVenta3(), new Color(34, 139, 34));
        
        // Configurar acciones
        btnPrecio1.addActionListener(e -> seleccionarPrecio(producto.getPrecioVenta1()));
        btnPrecio2.addActionListener(e -> seleccionarPrecio(producto.getPrecioVenta2()));
        btnPrecio3.addActionListener(e -> seleccionarPrecio(producto.getPrecioVenta3()));
        
        // Agregar botones al panel
        panelContenido.add(btnPrecio1);
        panelContenido.add(Box.createVerticalStrut(10));
        panelContenido.add(btnPrecio2);
        panelContenido.add(Box.createVerticalStrut(10));
        panelContenido.add(btnPrecio3);
        
        panelPrincipal.add(panelContenido);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private JButton crearBotonPrecio(String texto, BigDecimal precio, Color color) {
        JButton boton = new JButton();
        boton.setLayout(new BorderLayout());
        boton.setPreferredSize(new Dimension(300, 50));
        boton.setMaximumSize(new Dimension(300, 50));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Texto del precio
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Arial", Font.BOLD, 14));
        lblTexto.setForeground(Color.WHITE);
        lblTexto.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Valor del precio
        JLabel lblPrecio = new JLabel(String.format("%.2f", precio));
        lblPrecio.setFont(new Font("Arial", Font.BOLD, 16));
        lblPrecio.setForeground(Color.WHITE);
        lblPrecio.setHorizontalAlignment(SwingConstants.RIGHT);
        
        boton.add(lblTexto, BorderLayout.WEST);
        boton.add(lblPrecio, BorderLayout.EAST);
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            Color colorOriginal = color;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorOriginal.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorOriginal);
            }
        });
        
        return boton;
    }
    
    private void configurarDialogo() {
        setSize(350, 280);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Teclas de acceso rápido
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_1:
                        seleccionarPrecio(producto.getPrecioVenta1());
                        break;
                    case java.awt.event.KeyEvent.VK_2:
                        seleccionarPrecio(producto.getPrecioVenta2());
                        break;
                    case java.awt.event.KeyEvent.VK_3:
                        seleccionarPrecio(producto.getPrecioVenta3());
                        break;
                    case java.awt.event.KeyEvent.VK_ESCAPE:
                        dispose();
                        break;
                }
            }
        });
        
        setFocusable(true);
    }
    
    private void seleccionarPrecio(BigDecimal precio) {
        if (precio != null && precio.compareTo(BigDecimal.ZERO) > 0) {
            precioSeleccionado = precio;
            precioConfirmado = true;
            
            System.out.println("✅ Precio seleccionado: $" + precio);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Este precio no está configurado para el producto",
                "Precio no disponible",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // Getters
    public boolean isPrecioSeleccionado() {
        return precioConfirmado;
    }
    
    public BigDecimal getPrecioSeleccionado() {
        return precioSeleccionado;
    }
}
