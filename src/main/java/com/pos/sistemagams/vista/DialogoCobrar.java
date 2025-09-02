/*
 * Diálogo completo para procesar el cobro con múltiples métodos de pago
 */
package com.pos.sistemagams.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Diálogo modal para procesar cobro
 */
public class DialogoCobrar extends JDialog {
    
    private BigDecimal totalAPagar;
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;
    private BigDecimal totalNeto;
    private BigDecimal creditoDisponible = BigDecimal.ZERO;
    private BigDecimal montoEfectivo = BigDecimal.ZERO;
    private BigDecimal cambio = BigDecimal.ZERO;
    
    private boolean ventaCompletada = false;
    private String metodoPago = "EFECTIVO";
    
    // Componentes
    private JLabel lblTotalOperacion;
    private JTextField txtDescuento;
    private JLabel lblTotalNeto;
    private JTextField txtEfectivo;
    private JLabel lblTotalAPagar;
    private JLabel lblCambio;
    private JLabel lblSubtitulo;
    private JLabel lblCambioSubtitulo;
    private JButton btnCobrarTicket;
    private JButton btnCobrarA4;
    private JButton btnCobrarSinImprimir;
    
    public DialogoCobrar(Frame parent, BigDecimal total) {
        super(parent, "Cobrar", true);
        this.totalAPagar = total;
        this.totalNeto = total;
        
        initComponents();
        configurarDialogo();
        actualizarCalculos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(70, 130, 180));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel lblTitulo = new JLabel("← Cobrar", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        
        // Panel de contenido
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new GridBagLayout());
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Crear campos de la interfaz
        crearCamposInterfaz(panelContenido, gbc);
        
        // Panel de totales (lado derecho)
        JPanel panelTotales = crearPanelTotales();
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        
        // Ensamblar
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelContenido, BorderLayout.CENTER);
        panelCentral.add(panelTotales, BorderLayout.EAST);
        
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void crearCamposInterfaz(JPanel panel, GridBagConstraints gbc) {
        // Total Operación
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblTotalOp = new JLabel("Total Operación :");
        lblTotalOp.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTotalOp, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        lblTotalOperacion = new JLabel(String.format("%.2f", totalAPagar));
        lblTotalOperacion.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalOperacion.setForeground(Color.BLUE);
        lblTotalOperacion.setOpaque(true);
        lblTotalOperacion.setBackground(new Color(240, 240, 255));
        lblTotalOperacion.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(lblTotalOperacion, gbc);
        
        // Descuento %
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblDesc = new JLabel("Descuento % :");
        lblDesc.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblDesc, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDescuento = new JTextField("0.00");
        txtDescuento.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDescuento.setHorizontalAlignment(JTextField.RIGHT);
        txtDescuento.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularDescuento();
            }
        });
        panel.add(txtDescuento, gbc);
        
        // Total Neto
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblNeto = new JLabel("Total Neto :");
        lblNeto.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblNeto, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        lblTotalNeto = new JLabel(String.format("%.2f", totalNeto));
        lblTotalNeto.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalNeto.setForeground(Color.BLUE);
        lblTotalNeto.setOpaque(true);
        lblTotalNeto.setBackground(new Color(240, 240, 255));
        lblTotalNeto.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(lblTotalNeto, gbc);
        
        // Efectivo
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblEfectivo = new JLabel("Efectivo :");
        lblEfectivo.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblEfectivo, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtEfectivo = new JTextField(String.format("%.2f", totalNeto.add(new BigDecimal("50"))));
        txtEfectivo.setFont(new Font("Arial", Font.BOLD, 14));
        txtEfectivo.setHorizontalAlignment(JTextField.RIGHT);
        txtEfectivo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularCambio();
            }
        });
        panel.add(txtEfectivo, gbc);
    }
    
    private JPanel crearPanelTotales() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 0));
        panel.setPreferredSize(new Dimension(280, 300)); // Aumentado de 200 a 280 para más espacio
        
        // Total a Pagar
        JPanel panelTotalPagar = new JPanel(new BorderLayout());
        panelTotalPagar.setBackground(Color.WHITE);
        panelTotalPagar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTotalPagarTitulo = new JLabel("Total a Pagar", JLabel.CENTER);
        lblTotalPagarTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        
        lblTotalAPagar = new JLabel("S/ 135.00", JLabel.CENTER);
        lblTotalAPagar.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalAPagar.setForeground(Color.BLUE);
        
        lblSubtitulo = new JLabel("CIENTO TREINTA Y CINCO SOLES", JLabel.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 9)); // Reducido de 10 a 9
        
        panelTotalPagar.add(lblTotalPagarTitulo, BorderLayout.NORTH);
        panelTotalPagar.add(lblTotalAPagar, BorderLayout.CENTER);
        panelTotalPagar.add(lblSubtitulo, BorderLayout.SOUTH);
        
        // Cambio
        JPanel panelCambio = new JPanel(new BorderLayout());
        panelCambio.setBackground(Color.WHITE);
        panelCambio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblCambioTitulo = new JLabel("Cambio", JLabel.CENTER);
        lblCambioTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        
        lblCambio = new JLabel("S/ 65.00", JLabel.CENTER);
        lblCambio.setFont(new Font("Arial", Font.BOLD, 20));
        lblCambio.setForeground(Color.BLUE);
        
        lblCambioSubtitulo = new JLabel("SESENTA Y CINCO SOLES", JLabel.CENTER);
        lblCambioSubtitulo.setFont(new Font("Arial", Font.PLAIN, 9)); // Reducido de 10 a 9
        
        panelCambio.add(lblCambioTitulo, BorderLayout.NORTH);
        panelCambio.add(lblCambio, BorderLayout.CENTER);
        panelCambio.add(lblCambioSubtitulo, BorderLayout.SOUTH);
        
        panel.add(panelTotalPagar);
        panel.add(Box.createVerticalStrut(20));
        panel.add(panelCambio);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Primer fila de botones
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        fila1.setOpaque(false);
        
        btnCobrarTicket = new JButton("Cobrar con Ticket");
        btnCobrarTicket.setFont(new Font("Arial", Font.BOLD, 12));
        btnCobrarTicket.setBackground(new Color(70, 130, 180));
        btnCobrarTicket.setForeground(Color.WHITE);
        btnCobrarTicket.setPreferredSize(new Dimension(150, 35));
        btnCobrarTicket.setFocusPainted(false);
        btnCobrarTicket.addActionListener(e -> procesarCobro("TICKET"));
        
        btnCobrarA4 = new JButton("Cobrar con A4");
        btnCobrarA4.setFont(new Font("Arial", Font.BOLD, 12));
        btnCobrarA4.setBackground(new Color(34, 139, 34));
        btnCobrarA4.setForeground(Color.WHITE);
        btnCobrarA4.setPreferredSize(new Dimension(150, 35));
        btnCobrarA4.setFocusPainted(false);
        btnCobrarA4.addActionListener(e -> procesarCobro("A4"));
        
        fila1.add(btnCobrarTicket);
        fila1.add(btnCobrarA4);
        
        // Segunda fila
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fila2.setOpaque(false);
        
        btnCobrarSinImprimir = new JButton("Cobrar Sin Imprimir");
        btnCobrarSinImprimir.setFont(new Font("Arial", Font.BOLD, 12));
        btnCobrarSinImprimir.setBackground(new Color(255, 140, 0));
        btnCobrarSinImprimir.setForeground(Color.WHITE);
        btnCobrarSinImprimir.setPreferredSize(new Dimension(200, 35));
        btnCobrarSinImprimir.setFocusPainted(false);
        btnCobrarSinImprimir.addActionListener(e -> procesarCobro("SIN_IMPRIMIR"));
        
        fila2.add(btnCobrarSinImprimir);
        
        panel.add(fila1);
        panel.add(Box.createVerticalStrut(10));
        panel.add(fila2);
        
        return panel;
    }
    
    private void configurarDialogo() {
        setSize(700, 400); // Aumentado el ancho de 600 a 700 para dar más espacio al panel derecho
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        
        // Focus en efectivo al abrir
        SwingUtilities.invokeLater(() -> {
            txtEfectivo.requestFocus();
            txtEfectivo.selectAll();
        });
        
        // Calcular valores iniciales
        calcularCambio();
    }
    
    private void calcularDescuento() {
        try {
            String textoDescuento = txtDescuento.getText().trim();
            if (textoDescuento.isEmpty()) {
                textoDescuento = "0";
            }
            
            descuentoPorcentaje = new BigDecimal(textoDescuento);
            
            if (descuentoPorcentaje.compareTo(BigDecimal.ZERO) < 0 || 
                descuentoPorcentaje.compareTo(new BigDecimal("100")) > 0) {
                descuentoPorcentaje = BigDecimal.ZERO;
                txtDescuento.setText("0.00");
            }
            
            // Calcular total neto
            BigDecimal descuentoMonto = totalAPagar.multiply(descuentoPorcentaje)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            totalNeto = totalAPagar.subtract(descuentoMonto);
            
            actualizarCalculos();
            
        } catch (NumberFormatException e) {
            txtDescuento.setText("0.00");
            descuentoPorcentaje = BigDecimal.ZERO;
            totalNeto = totalAPagar;
            actualizarCalculos();
        }
    }
    
    private void calcularCambio() {
        try {
            String textoEfectivo = txtEfectivo.getText().trim();
            if (textoEfectivo.isEmpty()) {
                textoEfectivo = "0";
            }
            
            montoEfectivo = new BigDecimal(textoEfectivo);
            cambio = montoEfectivo.subtract(totalNeto);
            
            if (cambio.compareTo(BigDecimal.ZERO) < 0) {
                cambio = BigDecimal.ZERO;
            }
            
            actualizarCalculos();
            
        } catch (NumberFormatException e) {
            montoEfectivo = BigDecimal.ZERO;
            cambio = BigDecimal.ZERO;
            actualizarCalculos();
        }
    }
    
    private void actualizarCalculos() {
        lblTotalOperacion.setText(String.format("%.2f", totalAPagar));
        lblTotalNeto.setText(String.format("%.2f", totalNeto));
        lblTotalAPagar.setText(String.format("S/ %.2f", totalNeto));
        lblCambio.setText(String.format("S/ %.2f", cambio));
        
        // Actualizar subtítulos con números convertidos a texto
        lblSubtitulo.setText(convertirNumeroATexto(totalNeto).toUpperCase() + " SOLES");
        lblCambioSubtitulo.setText(convertirNumeroATexto(cambio).toUpperCase() + " SOLES");
        
        // Habilitar/deshabilitar botones según si hay suficiente dinero
        boolean suficienteDinero = montoEfectivo.compareTo(totalNeto) >= 0;
        btnCobrarTicket.setEnabled(suficienteDinero);
        btnCobrarA4.setEnabled(suficienteDinero);
        btnCobrarSinImprimir.setEnabled(suficienteDinero);
    }
    
    private String convertirNumeroATexto(BigDecimal numero) {
        // Método simple para convertir números a texto
        // En una implementación real, usarías una librería más completa
        int parteEntera = numero.intValue();
        
        if (parteEntera == 0) return "cero";
        if (parteEntera <= 20) {
            String[] unidades = {"", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez",
                                "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete", "dieciocho", "diecinueve", "veinte"};
            return unidades[parteEntera];
        }
        if (parteEntera < 100) {
            String[] decenas = {"", "", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"};
            int d = parteEntera / 10;
            int u = parteEntera % 10;
            return decenas[d] + (u > 0 ? " y " + convertirNumeroATexto(BigDecimal.valueOf(u)) : "");
        }
        if (parteEntera < 1000) {
            String[] centenas = {"", "ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"};
            int c = parteEntera / 100;
            int resto = parteEntera % 100;
            if (parteEntera == 100) return "cien";
            return centenas[c] + (resto > 0 ? " " + convertirNumeroATexto(BigDecimal.valueOf(resto)) : "");
        }
        
        return "número grande"; // Para números más grandes
    }
    
    private void procesarCobro(String tipoImpresion) {
        if (montoEfectivo.compareTo(totalNeto) < 0) {
            JOptionPane.showMessageDialog(this,
                "El monto en efectivo es insuficiente",
                "Monto insuficiente",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        metodoPago = "EFECTIVO";
        ventaCompletada = true;
        
        System.out.println("✅ Cobro procesado - Tipo: " + tipoImpresion + 
                          " | Total: S/." + totalNeto + 
                          " | Efectivo: S/." + montoEfectivo + 
                          " | Cambio: S/." + cambio);
        
        dispose();
    }
    
    // Getters
    public boolean isVentaCompletada() {
        return ventaCompletada;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public BigDecimal getTotalNeto() {
        return totalNeto;
    }
    
    public BigDecimal getMontoEfectivo() {
        return montoEfectivo;
    }
    
    public BigDecimal getCambio() {
        return cambio;
    }
    
    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }
}