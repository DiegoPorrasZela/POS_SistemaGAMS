/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * Diálogo modal OBLIGATORIO para apertura de caja
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.modelo.Usuario;
import com.pos.sistemagams.modelo.SesionCaja;
import com.pos.sistemagams.dao.CajaDAO;
import java.math.BigDecimal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Diálogo modal para apertura obligatoria de caja
 */
public class DialogoAperturaCaja extends JDialog {
    
    private Usuario usuario;
    private CajaDAO cajaDAO;
    private SesionCaja sesionCreada;
    private boolean cajaAbierta = false;
    
    // Componentes
    private JLabel lblTitulo;
    private JLabel lblUsuario;
    private JTextField txtUsuario;
    private JLabel lblMonto;
    private JSpinner spnMonto;
    private JButton btnAceptar;
    private JButton btnCancelar;
    
    public DialogoAperturaCaja(Frame parent, Usuario usuario) {
        super(parent, "Apertura de Caja - OBLIGATORIO", true);
        this.usuario = usuario;
        this.cajaDAO = new CajaDAO();
        
        initComponents();
        configurarDialogo();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(new Color(70, 130, 180)); // Azul
        
        // Título
        lblTitulo = new JLabel("⚠️ Apertura de Caja", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Espaciado
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));
        
        // Panel de contenido con fondo blanco
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new GridBagLayout());
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Usuario/Cajero
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        lblUsuario = new JLabel("Usuario / Cajero:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        panelContenido.add(lblUsuario, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUsuario = new JTextField(usuario.getNombreCompleto(), 20);
        txtUsuario.setEditable(false);
        txtUsuario.setBackground(Color.LIGHT_GRAY);
        txtUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        panelContenido.add(txtUsuario, gbc);
        
        // Monto
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        lblMonto = new JLabel("Monto:");
        lblMonto.setFont(new Font("Arial", Font.BOLD, 12));
        panelContenido.add(lblMonto, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        spnMonto = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 99999.99, 0.01));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnMonto, "#,##0.00");
        spnMonto.setEditor(editor);
        spnMonto.setFont(new Font("Arial", Font.BOLD, 14));
        ((JSpinner.DefaultEditor) spnMonto.getEditor()).getTextField().setHorizontalAlignment(JTextField.RIGHT);
        panelContenido.add(spnMonto, gbc);
        
        panelPrincipal.add(panelContenido);
        panelPrincipal.add(Box.createVerticalStrut(15));
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setOpaque(false);
        
        btnAceptar = new JButton("✓ Aceptar");
        btnAceptar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAceptar.setBackground(new Color(34, 139, 34)); // Verde
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setPreferredSize(new Dimension(120, 35));
        btnAceptar.setFocusPainted(false);
        btnAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirCaja();
            }
        });
        
        btnCancelar = new JButton("✗ Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(220, 20, 60)); // Rojo
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelar();
            }
        });
        
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        
        panelPrincipal.add(panelBotones);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private void configurarDialogo() {
        setSize(450, 320); // Tamaño aumentado para mejor proporción
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // CAMBIO: Permitir cerrar con X
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Ya no mostrar advertencia, simplemente cerrar
                cajaAbierta = false;
                dispose();
            }
        });
        
        // Focus en el spinner al abrir
        SwingUtilities.invokeLater(() -> {
            spnMonto.requestFocus();
            JComponent editor = spnMonto.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) editor).getTextField().selectAll();
            }
        });
    }
    
    private void abrirCaja() {
        try {
            BigDecimal montoApertura = new BigDecimal(spnMonto.getValue().toString());
            
            if (montoApertura.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this,
                    "El monto no puede ser negativo",
                    "Monto inválido",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Confirmar apertura
            int confirmacion = JOptionPane.showConfirmDialog(this,
                String.format("¿Confirma abrir la caja con S/.%.2f?", montoApertura),
                "Confirmar apertura",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Mostrar loading
            btnAceptar.setEnabled(false);
            btnAceptar.setText("Abriendo...");
            
            // Abrir caja
            sesionCreada = cajaDAO.abrirCaja(usuario.getIdUsuario(), montoApertura, 
                "Apertura normal del " + java.time.LocalDate.now());
            
            if (sesionCreada != null) {
                cajaAbierta = true;
                JOptionPane.showMessageDialog(this,
                    "¡Caja abierta exitosamente!\n" +
                    "Sesión: " + sesionCreada.getNumeroSesion() + "\n" +
                    "Monto inicial: S/." + String.format("%.2f", montoApertura),
                    "Apertura exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al abrir la caja.\nIntente nuevamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                btnAceptar.setEnabled(true);
                btnAceptar.setText("✓ Aceptar");
            }
            
        } catch (Exception e) {
            System.err.println("Error al abrir caja: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error inesperado: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            btnAceptar.setEnabled(true);
            btnAceptar.setText("✓ Aceptar");
        }
    }
    
    private void cancelar() {
        cajaAbierta = false;
        dispose();
    }
    
    // Getters
    public boolean isCajaAbierta() {
        return cajaAbierta;
    }
    
    public SesionCaja getSesionCreada() {
        return sesionCreada;
    }
}