/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Diálogo para ingresar cantidad de productos
 */
package com.pos.sistemagams.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

/**
 *
 * @author Diego
 */
/**
 * Diálogo modal para ingresar cantidad
 */
public class DialogoCantidad extends JDialog {
    
    private String nombreProducto;
    private BigDecimal cantidadActual;
    private BigDecimal cantidadIngresada;
    private boolean cantidadConfirmada = false;
    
    // Componentes
    private JLabel lblTitulo;
    private JLabel lblProducto;
    private JLabel lblCantidad;
    private JSpinner spnCantidad;
    private JButton btnAceptar;
    private JButton btnCancelar;
    
    public DialogoCantidad(Frame parent, String nombreProducto, BigDecimal cantidadActual) {
        super(parent, "Ingresar Cantidad", true);
        this.nombreProducto = nombreProducto;
        this.cantidadActual = cantidadActual;
        this.cantidadIngresada = cantidadActual;
        
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
        lblTitulo = new JLabel("← Ingresar Cantidad", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createVerticalStrut(15)); // Reducido de 20 a 15
        
        // Panel de contenido con fondo blanco
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Reducido padding vertical
        
        // Descripción del producto
        JLabel lblDescripcionTitulo = new JLabel("Descripción :");
        lblDescripcionTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDescripcionTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblProducto = new JLabel(nombreProducto);
        lblProducto.setFont(new Font("Arial", Font.BOLD, 14));
        lblProducto.setForeground(new Color(70, 130, 180));
        lblProducto.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelContenido.add(lblDescripcionTitulo);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(lblProducto);
        panelContenido.add(Box.createVerticalStrut(15)); // Reducido de 20 a 15
        
        // Cantidad
        JLabel lblCantidadTitulo = new JLabel("Cantidad :");
        lblCantidadTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCantidadTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Spinner para cantidad con botones + y -
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
            cantidadActual.doubleValue(), // valor inicial
            0.01, // mínimo
            9999.99, // máximo
            0.01 // paso
        );
        
        spnCantidad = new JSpinner(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnCantidad, "#,##0.##");
        spnCantidad.setEditor(editor);
        spnCantidad.setFont(new Font("Arial", Font.BOLD, 18));
        spnCantidad.setPreferredSize(new Dimension(150, 40));
        spnCantidad.setMaximumSize(new Dimension(150, 40));
        
        // Centrar el spinner
        JPanel panelSpinner = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSpinner.setBackground(Color.WHITE);
        panelSpinner.add(spnCantidad);
        
        // Configurar el campo de texto del spinner
        JFormattedTextField textField = ((JSpinner.DefaultEditor) spnCantidad.getEditor()).getTextField();
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Seleccionar todo el texto al hacer focus
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(() -> textField.selectAll());
            }
        });
        
        // Enter para aceptar
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    aceptar();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        
        panelContenido.add(lblCantidadTitulo);
        panelContenido.add(Box.createVerticalStrut(10));
        panelContenido.add(panelSpinner);
        
        panelPrincipal.add(panelContenido);
        panelPrincipal.add(Box.createVerticalStrut(15)); // Reducido de 20 a 15
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);
        
        btnAceptar = new JButton("✓");
        btnAceptar.setFont(new Font("Arial", Font.BOLD, 16));
        btnAceptar.setBackground(new Color(34, 139, 34));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setPreferredSize(new Dimension(60, 40));
        btnAceptar.setFocusPainted(false);
        btnAceptar.setBorder(BorderFactory.createEmptyBorder());
        btnAceptar.addActionListener(e -> aceptar());
        
        panelBotones.add(btnAceptar);
        
        panelPrincipal.add(panelBotones);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private void configurarDialogo() {
        setSize(400, 350); // Aumentado de 300 a 350 de altura para mostrar completamente el botón
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Focus en el spinner al abrir
        SwingUtilities.invokeLater(() -> {
            spnCantidad.requestFocus();
            JFormattedTextField textField = ((JSpinner.DefaultEditor) spnCantidad.getEditor()).getTextField();
            textField.selectAll();
        });
    }
    
    private void aceptar() {
        try {
            Object value = spnCantidad.getValue();
            double cantidad = ((Number) value).doubleValue();
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser mayor a cero",
                    "Cantidad inválida",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            cantidadIngresada = BigDecimal.valueOf(cantidad);
            cantidadConfirmada = true;
            
            System.out.println("✅ Cantidad confirmada: " + cantidadIngresada);
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Cantidad inválida. Ingrese un número válido.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Getters
    public boolean isCantidadConfirmada() {
        return cantidadConfirmada;
    }
    
    public BigDecimal getCantidadIngresada() {
        return cantidadIngresada;
    }
}