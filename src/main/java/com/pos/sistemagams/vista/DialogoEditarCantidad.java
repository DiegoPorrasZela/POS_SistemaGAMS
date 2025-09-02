package com.pos.sistemagams.vista;

import com.pos.sistemagams.vista.DialogoEntradaInventario.ItemEntradaInventario;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import javax.swing.*;

/**
 * Diálogo para editar cantidad y precios en entrada de inventario
 */
public class DialogoEditarCantidad extends JDialog {
    
    private ItemEntradaInventario item;
    private JTextField txtDescripcion;
    private JTextField txtCosto;
    private JTextField txtPrecio1;
    private JTextField txtPrecio2;
    private JTextField txtPrecio3;
    private JTextField txtCantidad;
    private JButton btnConfirmar;
    private boolean confirmado = false;
    
    public DialogoEditarCantidad(Component parent, ItemEntradaInventario item) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null, 
              "Ingresar Cantidad", ModalityType.APPLICATION_MODAL);
        
        this.item = item;
        initComponents();
        configurarDialog();
        cargarDatos();
        configurarEventos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel lblTitulo = new JLabel("← Ingresar Cantidad");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.add(lblTitulo);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Descripción del producto
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Arial", Font.BOLD, 12));
        panelPrincipal.add(lblDescripcion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtDescripcion = new JTextField(25);
        txtDescripcion.setPreferredSize(new Dimension(350, 40));
        txtDescripcion.setEditable(false);
        txtDescripcion.setBackground(Color.LIGHT_GRAY);
        txtDescripcion.setHorizontalAlignment(SwingConstants.CENTER);
        txtDescripcion.setFont(new Font("Arial", Font.BOLD, 12));
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        panelPrincipal.add(txtDescripcion, gbc);
        
        // Costo
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblCosto = new JLabel("Costo:");
        lblCosto.setFont(new Font("Arial", Font.BOLD, 12));
        lblCosto.setForeground(Color.BLUE);
        panelPrincipal.add(lblCosto, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCosto = new JTextField();
        txtCosto.setPreferredSize(new Dimension(350, 30));
        txtCosto.setHorizontalAlignment(SwingConstants.RIGHT);
        txtCosto.setFont(new Font("Arial", Font.PLAIN, 12));
        panelPrincipal.add(txtCosto, gbc);
        
        // Precio 1
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblPrecio1 = new JLabel("Precio 1:");
        lblPrecio1.setFont(new Font("Arial", Font.BOLD, 12));
        lblPrecio1.setForeground(new Color(255, 140, 0)); // Orange
        panelPrincipal.add(lblPrecio1, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrecio1 = new JTextField();
        txtPrecio1.setPreferredSize(new Dimension(350, 30));
        txtPrecio1.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPrecio1.setFont(new Font("Arial", Font.PLAIN, 12));
        panelPrincipal.add(txtPrecio1, gbc);
        
        // Precio 2
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblPrecio2 = new JLabel("Precio 2:");
        lblPrecio2.setFont(new Font("Arial", Font.BOLD, 12));
        lblPrecio2.setForeground(new Color(255, 140, 0)); // Orange
        panelPrincipal.add(lblPrecio2, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrecio2 = new JTextField();
        txtPrecio2.setPreferredSize(new Dimension(350, 30));
        txtPrecio2.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPrecio2.setFont(new Font("Arial", Font.PLAIN, 12));
        panelPrincipal.add(txtPrecio2, gbc);
        
        // Precio 3
        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblPrecio3 = new JLabel("Precio 3:");
        lblPrecio3.setFont(new Font("Arial", Font.BOLD, 12));
        lblPrecio3.setForeground(new Color(255, 140, 0)); // Orange
        panelPrincipal.add(lblPrecio3, gbc);
        
        gbc.gridx = 0; gbc.gridy = 9; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrecio3 = new JTextField();
        txtPrecio3.setPreferredSize(new Dimension(350, 30));
        txtPrecio3.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPrecio3.setFont(new Font("Arial", Font.PLAIN, 12));
        panelPrincipal.add(txtPrecio3, gbc);
        
        // Cantidad
        gbc.gridx = 0; gbc.gridy = 10; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 12));
        panelPrincipal.add(lblCantidad, gbc);
        
        gbc.gridx = 0; gbc.gridy = 11; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel panelCantidad = new JPanel(new BorderLayout());
        
        txtCantidad = new JTextField();
        txtCantidad.setPreferredSize(new Dimension(300, 40));
        txtCantidad.setHorizontalAlignment(SwingConstants.CENTER);
        txtCantidad.setFont(new Font("Arial", Font.BOLD, 16));
        txtCantidad.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
        btnConfirmar = new JButton("✓");
        btnConfirmar.setPreferredSize(new Dimension(50, 40));
        btnConfirmar.setBackground(new Color(70, 130, 180));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBorderPainted(false);
        
        panelCantidad.add(txtCantidad, BorderLayout.CENTER);
        panelCantidad.add(btnConfirmar, BorderLayout.EAST);
        
        panelPrincipal.add(panelCantidad, gbc);
        
        // Espacio adicional
        gbc.gridx = 0; gbc.gridy = 12; gbc.weighty = 1.0;
        panelPrincipal.add(new JLabel(), gbc);
        
        add(headerPanel, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private void configurarDialog() {
        setSize(420, 580);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void cargarDatos() {
        txtDescripcion.setText(item.getProducto().getNombre().toUpperCase());
        txtCosto.setText(item.getCosto().toString());
        txtPrecio1.setText(item.getPrecio1().toString());
        txtPrecio2.setText(item.getPrecio2().toString());
        txtPrecio3.setText(item.getPrecio3().toString());
        txtCantidad.setText(item.getCantidad().toString());
        
        // Seleccionar el texto de cantidad para facilitar edición
        txtCantidad.selectAll();
        txtCantidad.requestFocus();
    }
    
    private void configurarEventos() {
        // Botón confirmar
        btnConfirmar.addActionListener(this::confirmar);
        
        // Enter en cantidad confirma
        txtCantidad.addActionListener(this::confirmar);
        
        // Enter en otros campos navega al siguiente
        txtCosto.addActionListener(e -> txtPrecio1.requestFocus());
        txtPrecio1.addActionListener(e -> txtPrecio2.requestFocus());
        txtPrecio2.addActionListener(e -> txtPrecio3.requestFocus());
        txtPrecio3.addActionListener(e -> txtCantidad.requestFocus());
        
        // Escape cancela
        Action cancelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        getRootPane().getActionMap().put("cancel", cancelAction);
        
        // Enter global confirma
        Action confirmAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtCantidad.hasFocus()) {
                    confirmar(e);
                }
            }
        };
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirm");
        getRootPane().getActionMap().put("confirm", confirmAction);
    }
    
    private void confirmar(ActionEvent e) {
        try {
            // Validar y obtener valores
            String costoStr = txtCosto.getText().trim().replace(",", ".");
            String precio1Str = txtPrecio1.getText().trim().replace(",", ".");
            String precio2Str = txtPrecio2.getText().trim().replace(",", ".");
            String precio3Str = txtPrecio3.getText().trim().replace(",", ".");
            String cantidadStr = txtCantidad.getText().trim().replace(",", ".");
            
            // Validar que no estén vacíos
            if (costoStr.isEmpty() || precio1Str.isEmpty() || cantidadStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Costo, Precio 1 y Cantidad son obligatorios",
                    "Campos requeridos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Convertir a BigDecimal
            BigDecimal nuevoCosto = new BigDecimal(costoStr);
            BigDecimal nuevoPrecio1 = new BigDecimal(precio1Str);
            BigDecimal nuevoPrecio2 = precio2Str.isEmpty() ? BigDecimal.ZERO : new BigDecimal(precio2Str);
            BigDecimal nuevoPrecio3 = precio3Str.isEmpty() ? BigDecimal.ZERO : new BigDecimal(precio3Str);
            BigDecimal nuevaCantidad = new BigDecimal(cantidadStr);
            
            // Validar valores positivos
            if (nuevoCosto.compareTo(BigDecimal.ZERO) < 0) {
                mostrarError("El costo no puede ser negativo", txtCosto);
                return;
            }
            
            if (nuevoPrecio1.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarError("El precio 1 debe ser mayor a 0", txtPrecio1);
                return;
            }
            
            if (nuevaCantidad.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarError("La cantidad debe ser mayor a 0", txtCantidad);
                return;
            }
            
            // Actualizar item
            item.setCosto(nuevoCosto);
            item.setPrecio1(nuevoPrecio1);
            item.setPrecio2(nuevoPrecio2);
            item.setPrecio3(nuevoPrecio3);
            item.setCantidad(nuevaCantidad);
            
            confirmado = true;
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Error en el formato de los números.\n" +
                "Use punto (.) como separador decimal.",
                "Error de formato",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarError(String mensaje, JTextField campo) {
        JOptionPane.showMessageDialog(this, mensaje, "Valor inválido", JOptionPane.WARNING_MESSAGE);
        campo.selectAll();
        campo.requestFocus();
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
}