/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Diálogo para cerrar caja con resumen completo
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.modelo.SesionCaja;
import com.pos.sistemagams.dao.CajaDAO;
import com.pos.sistemagams.dao.VentaDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
/**
 *
 * @author Diego
 */
/**
 * Diálogo modal para cerrar caja con cálculos corregidos
 */
public class DialogoCierreCaja extends JDialog {
    
    private SesionCaja sesionActiva;
    private CajaDAO cajaDAO;
    private VentaDAO ventaDAO;
    private boolean cajaCerrada = false;
    
    // Componentes
    private JLabel lblUsuario;
    private JLabel lblAperturaCaja;
    private JLabel lblVentasEfectivo;
    private JLabel lblEntradaEfectivo;
    private JLabel lblSalidaEfectivo;
    private JLabel lblDevolucionEfectivo;
    private JLabel lblDineroCaja;
    private JLabel lblVentaTotal;
    private JSpinner spnDineroReal;
    private JButton btnCerrarCaja;
    
    // Variables para cálculos corregidos
    private BigDecimal ventasEnEfectivo = BigDecimal.ZERO;
    private BigDecimal ventasConTarjeta = BigDecimal.ZERO;
    private BigDecimal ventasACredito = BigDecimal.ZERO;
    private BigDecimal ventasConPuntos = BigDecimal.ZERO;
    private BigDecimal totalDescuentos = BigDecimal.ZERO;
    
    public DialogoCierreCaja(Frame parent, SesionCaja sesion) {
        super(parent, "Cierre de Caja", true);
        this.sesionActiva = sesion;
        this.cajaDAO = new CajaDAO();
        this.ventaDAO = new VentaDAO();
        
        // Cargar datos ANTES de crear componentes
        cargarDatos();
        initComponents();
        configurarDialogo();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(new Color(70, 130, 180));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel lblTitulo = new JLabel("← Cierre de Caja", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));
        
        // Panel de contenido
        JPanel panelContenido = new JPanel();
        panelContenido.setBackground(Color.WHITE);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelContenido.setLayout(new BorderLayout());
        
        // Panel izquierdo - Información de usuario y resumen
        JPanel panelIzquierdo = crearPanelResumen();
        
        // Panel derecho - Totales y dinero real
        JPanel panelDerecho = crearPanelTotales();
        
        panelContenido.add(panelIzquierdo, BorderLayout.CENTER);
        panelContenido.add(panelDerecho, BorderLayout.EAST);
        
        panelPrincipal.add(panelContenido);
        panelPrincipal.add(Box.createVerticalStrut(30));
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        btnCerrarCaja = new JButton("Cerrar Caja");
        btnCerrarCaja.setFont(new Font("Arial", Font.BOLD, 12));
        btnCerrarCaja.setBackground(new Color(70, 130, 180));
        btnCerrarCaja.setForeground(Color.WHITE);
        btnCerrarCaja.setPreferredSize(new Dimension(140, 40));
        btnCerrarCaja.setFocusPainted(false);
        btnCerrarCaja.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        btnCerrarCaja.addActionListener(e -> cerrarCaja());
        
        // Agregar icono al botón
        btnCerrarCaja.setIcon(createPrintIcon());
        btnCerrarCaja.setHorizontalTextPosition(SwingConstants.LEFT);
        btnCerrarCaja.setIconTextGap(8);
        
        panelBotones.add(btnCerrarCaja);
        panelPrincipal.add(panelBotones);
        
        add(panelPrincipal);
    }
    
    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(380, 380));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        int fila = 0;
        
        // Usuario
        gbc.gridx = 0; gbc.gridy = fila++;
        gbc.gridwidth = 2;
        lblUsuario = new JLabel("Usuario : " + sesionActiva.getUsuarioNombre());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblUsuario, gbc);
        
        gbc.gridwidth = 1;
        
        // Apertura de Caja
        agregarCampoResumen(panel, gbc, fila++, "Apertura de Caja :", 
            "S/", sesionActiva.getMontoApertura());
        
        // Ventas en Efectivo (CORREGIDO: Solo ventas, no dinero total)
        agregarCampoResumen(panel, gbc, fila++, "Ventas en Efectivo :", 
            "S/", ventasEnEfectivo);
        
        // Entrada Efectivo - EN MANTENIMIENTO
        agregarCampoMantenimiento(panel, gbc, fila++, "Entrada Efectivo :");
        
        // Salida Efectivo - EN MANTENIMIENTO
        agregarCampoMantenimiento(panel, gbc, fila++, "Salida Efectivo :");
        
        // Devolución Efectivo - EN MANTENIMIENTO
        agregarCampoMantenimiento(panel, gbc, fila++, "Devolución Efectivo :");
        
        // Línea separadora
        gbc.gridx = 0; gbc.gridy = fila++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator separador = new JSeparator();
        separador.setPreferredSize(new Dimension(320, 2));
        panel.add(separador, gbc);
        
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // Dinero en Caja (CORRECTO: Apertura + Ventas en efectivo)
        BigDecimal dineroCalculado = calcularDineroEnCaja();
        agregarCampoResumen(panel, gbc, fila++, "Dinero en Caja :", 
            "S/", dineroCalculado);
        
        return panel;
    }
    
    private JPanel crearPanelTotales() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panel.setPreferredSize(new Dimension(350, 500));
        
        // PRIMERO: Métodos de pago
        JPanel panelMetodosPago = new JPanel();
        panelMetodosPago.setLayout(new BoxLayout(panelMetodosPago, BoxLayout.Y_AXIS));
        panelMetodosPago.setBackground(Color.WHITE);
        panelMetodosPago.setBorder(BorderFactory.createTitledBorder("Métodos de Pago"));
        
        // En Efectivo
        panelMetodosPago.add(crearPanelMetodoPago("En Efectivo :", ventasEnEfectivo));
        // Con Tarjeta
        panelMetodosPago.add(crearPanelMetodoPago("Con Tarjeta :", ventasConTarjeta));
        // A Crédito
        panelMetodosPago.add(crearPanelMetodoPago("A Crédito :", ventasACredito));
        // Con Puntos
        panelMetodosPago.add(crearPanelMetodoPago("Con Puntos :", ventasConPuntos));
        
        // SEGUNDO: Ventas Totales
        BigDecimal ventasTotalesReales = ventasEnEfectivo.add(ventasConTarjeta).add(ventasACredito).add(ventasConPuntos);
        JPanel panelVentaTotal = crearPanelTotal("Ventas Totales :", ventasTotalesReales, 16);
        
        // TERCERO: Descuentos
        JPanel panelDescuentos = crearPanelTotal("Descuentos :", totalDescuentos, 16);
        
        // LÍNEA SEPARADORA
        JSeparator separadorTotales = new JSeparator();
        separadorTotales.setPreferredSize(new Dimension(350, 2));
        separadorTotales.setBackground(Color.LIGHT_GRAY);
        JPanel panelSeparador = new JPanel(new BorderLayout());
        panelSeparador.setBackground(Color.WHITE);
        panelSeparador.add(separadorTotales, BorderLayout.CENTER);
        
        // CUARTO: Venta Bruta (CORREGIDO: Ventas Totales + Descuentos)
        BigDecimal ventaBrutaActual = ventasTotalesReales.add(totalDescuentos); // CORREGIDO: SUMA, no resta
        JPanel panelVentaBruta = crearPanelTotal("Venta Bruta :", ventaBrutaActual, 16);
        
        // QUINTO: Devolución Ventas
        JPanel panelDevolucionVentas = crearPanelSimple("Devolución Ventas :", BigDecimal.ZERO, Color.RED);
        
        // SEXTO: Venta Total Final (CORREGIDO: Ventas Totales - Devoluciones, NO usar Venta Bruta)
        BigDecimal devoluciones = BigDecimal.ZERO;
        BigDecimal ventaTotalFinal = ventasTotalesReales.subtract(devoluciones); // CORREGIDO: Usar ventasTotalesReales
        JPanel panelVentaTotalFinal = crearPanelTotal("Venta Total :", ventaTotalFinal, 18);
        
        // SÉPTIMO: Spinner para dinero real contado
        JPanel panelDineroReal = crearPanelDineroReal();
        
        // Ensamblar todo el panel en orden correcto
        panel.add(panelMetodosPago);
        panel.add(Box.createVerticalStrut(10));
        panel.add(panelVentaTotal);
        panel.add(Box.createVerticalStrut(5));
        panel.add(panelDescuentos);
        panel.add(Box.createVerticalStrut(5));
        panel.add(panelSeparador);
        panel.add(Box.createVerticalStrut(5));
        panel.add(panelVentaBruta);
        panel.add(Box.createVerticalStrut(10));
        panel.add(panelDevolucionVentas);
        panel.add(Box.createVerticalStrut(5));
        panel.add(panelVentaTotalFinal);
        panel.add(Box.createVerticalStrut(10));
        panel.add(panelDineroReal);
        
        return panel;
    }
    
    private JPanel crearPanelMetodoPago(String titulo, BigDecimal valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel lblValor = new JLabel("S/ " + String.format("%.2f", valor), JLabel.RIGHT);
        lblValor.setFont(new Font("Arial", Font.BOLD, 14));
        lblValor.setForeground(new Color(70, 130, 180));
        
        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(lblValor, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelTotal(String titulo, BigDecimal valor, int fontSize) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel lblValor = new JLabel("S/ " + String.format("%.2f", valor), JLabel.RIGHT);
        lblValor.setFont(new Font("Arial", Font.BOLD, fontSize));
        lblValor.setForeground(new Color(70, 130, 180));
        
        JLabel lblTexto = new JLabel(convertirNumeroATexto(valor).toUpperCase() + " SOLES", JLabel.CENTER);
        lblTexto.setFont(new Font("Arial", Font.PLAIN, 8));
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        panel.add(lblTexto, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelSimple(String titulo, BigDecimal valor, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel lblValor = new JLabel("S/ " + String.format("-%.2f", valor), JLabel.RIGHT);
        lblValor.setFont(new Font("Arial", Font.BOLD, 12));
        lblValor.setForeground(color);
        
        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(lblValor, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelDineroReal() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Dinero Real Contado"));
        
        JLabel lblTitulo = new JLabel("Efectivo contado físicamente:");
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Spinner para dinero real
        BigDecimal dineroEsperado = calcularDineroEnCaja();
        SpinnerNumberModel model = new SpinnerNumberModel(
            dineroEsperado.doubleValue(), 0.0, 999999.99, 0.01);
        spnDineroReal = new JSpinner(model);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnDineroReal, "#,##0.00");
        spnDineroReal.setEditor(editor);
        spnDineroReal.setFont(new Font("Arial", Font.BOLD, 14));
        spnDineroReal.setPreferredSize(new Dimension(120, 30));
        spnDineroReal.setMaximumSize(new Dimension(120, 30));
        
        // Configurar el campo de texto del spinner
        JFormattedTextField textField = ((JSpinner.DefaultEditor) spnDineroReal.getEditor()).getTextField();
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cerrarCaja();
                }
            }
        });
        
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(spnDineroReal);
        
        return panel;
    }
    
    private void agregarCampoResumen(JPanel panel, GridBagConstraints gbc, int fila, 
                                   String etiqueta, String moneda, BigDecimal valor) {
        gbc.gridx = 0; gbc.gridy = fila;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.PLAIN, 11));
        lblEtiqueta.setPreferredSize(new Dimension(150, 25));
        panel.add(lblEtiqueta, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        String valorTexto = moneda + " " + String.format("%.2f", valor);
        JLabel lblValor = new JLabel(valorTexto);
        lblValor.setFont(new Font("Arial", Font.BOLD, 11));
        lblValor.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblValor, gbc);
    }
    
    private void agregarCampoMantenimiento(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta) {
        gbc.gridx = 0; gbc.gridy = fila;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.PLAIN, 11));
        lblEtiqueta.setPreferredSize(new Dimension(150, 25));
        panel.add(lblEtiqueta, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblValor = new JLabel("En mantenimiento");
        lblValor.setFont(new Font("Arial", Font.ITALIC, 11));
        lblValor.setForeground(Color.GRAY);
        lblValor.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblValor, gbc);
    }
    
    private BigDecimal calcularDineroEnCaja() {
        BigDecimal apertura = sesionActiva.getMontoApertura() != null ? sesionActiva.getMontoApertura() : BigDecimal.ZERO;
        return apertura.add(ventasEnEfectivo);
    }
    
    private void obtenerDatosRealesVentas() {
        try {
            System.out.println("🔍 Obteniendo datos reales de ventas...");
            
            BigDecimal totalVentas = sesionActiva.getTotalVentas() != null ? sesionActiva.getTotalVentas() : BigDecimal.ZERO;
            System.out.println("📊 Total ventas de sesión: " + totalVentas);
            
            // Por ahora, todas las ventas son en efectivo
            this.ventasEnEfectivo = totalVentas;
            this.ventasConTarjeta = BigDecimal.ZERO;
            this.ventasACredito = BigDecimal.ZERO;
            this.ventasConPuntos = BigDecimal.ZERO;
            
            // Obtener descuentos reales de la base de datos
            this.totalDescuentos = obtenerDescuentosReales();
            
            System.out.println("💰 Datos de ventas calculados:");
            System.out.println("   - Efectivo: S/ " + ventasEnEfectivo);
            System.out.println("   - Descuentos: S/ " + totalDescuentos);
            
        } catch (Exception e) {
            System.err.println("❌ Error al obtener datos de ventas: " + e.getMessage());
            e.printStackTrace();
            
            BigDecimal totalVentas = sesionActiva.getTotalVentas() != null ? sesionActiva.getTotalVentas() : BigDecimal.ZERO;
            this.ventasEnEfectivo = totalVentas;
            this.ventasConTarjeta = BigDecimal.ZERO;
            this.ventasACredito = BigDecimal.ZERO;
            this.ventasConPuntos = BigDecimal.ZERO;
            this.totalDescuentos = BigDecimal.ZERO;
        }
    }
    
    private BigDecimal obtenerDescuentosReales() {
        try {
            java.util.Date hoy = new java.util.Date();
            java.util.List<com.pos.sistemagams.modelo.Venta> ventas = ventaDAO.obtenerVentasPorFechas(hoy, hoy);
            
            BigDecimal totalDescuentos = BigDecimal.ZERO;
            
            for (com.pos.sistemagams.modelo.Venta venta : ventas) {
                if (venta.getIdSesionCaja() == sesionActiva.getIdSesion()) {
                    BigDecimal porcentajeDescuento = venta.getDescuento() != null ? venta.getDescuento() : BigDecimal.ZERO;
                    
                    // CORREGIDO: Si el descuento es un porcentaje, calcular el monto real
                    if (porcentajeDescuento.compareTo(BigDecimal.ZERO) > 0) {
                        // Calcular el total original antes del descuento
                        BigDecimal totalConDescuento = venta.getTotal(); // Lo que se cobró
                        BigDecimal factorDescuento = BigDecimal.ONE.subtract(porcentajeDescuento.divide(new BigDecimal("100")));
                        BigDecimal totalOriginal = totalConDescuento.divide(factorDescuento, 2, java.math.RoundingMode.HALF_UP);
                        BigDecimal montoDescuento = totalOriginal.subtract(totalConDescuento);
                        
                        totalDescuentos = totalDescuentos.add(montoDescuento);
                        
                        System.out.println("🧮 Venta " + venta.getNumeroTicket() + ":");
                        System.out.println("   - Porcentaje descuento: " + porcentajeDescuento + "%");
                        System.out.println("   - Total cobrado: S/ " + totalConDescuento);
                        System.out.println("   - Total original: S/ " + totalOriginal);
                        System.out.println("   - Monto descontado: S/ " + montoDescuento);
                    }
                }
            }
            
            System.out.println("💰 Total descuentos calculados: S/ " + totalDescuentos);
            return totalDescuentos;
            
        } catch (Exception e) {
            System.err.println("❌ Error al obtener descuentos: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    private void cargarDatos() {
        try {
            System.out.println("🔄 Iniciando carga de datos...");
            
            SesionCaja sesionActualizada = cajaDAO.obtenerSesionPorId(sesionActiva.getIdSesion());
            if (sesionActualizada != null) {
                this.sesionActiva = sesionActualizada;
                System.out.println("✅ Sesión actualizada - ID: " + sesionActualizada.getIdSesion() + ", Total ventas: " + sesionActualizada.getTotalVentas());
            } else {
                System.out.println("⚠️ No se pudo actualizar la sesión, usando datos originales");
            }
            
            obtenerDatosRealesVentas();
            
            System.out.println("📊 Resumen final de datos:");
            System.out.println("   - Apertura: S/ " + sesionActiva.getMontoApertura());
            System.out.println("   - Ventas Efectivo: S/ " + ventasEnEfectivo);
            System.out.println("   - Total Descuentos: S/ " + totalDescuentos);
            System.out.println("   - Dinero en Caja: S/ " + calcularDineroEnCaja());
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
            
            BigDecimal totalVentas = sesionActiva.getTotalVentas() != null ? sesionActiva.getTotalVentas() : BigDecimal.ZERO;
            this.ventasEnEfectivo = totalVentas;
            this.ventasConTarjeta = BigDecimal.ZERO;
            this.ventasACredito = BigDecimal.ZERO;
            this.ventasConPuntos = BigDecimal.ZERO;
            this.totalDescuentos = BigDecimal.ZERO;
        }
    }
    
    private void configurarDialogo() {
        setSize(800, 750);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        
        SwingUtilities.invokeLater(() -> {
            spnDineroReal.requestFocus();
        });
    }
    
    private void cerrarCaja() {
        try {
            BigDecimal montoCierreReal = new BigDecimal(spnDineroReal.getValue().toString());
            
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Confirma el cierre de la caja?\n" +
                "Dinero esperado: S/ " + String.format("%.2f", calcularDineroEnCaja()) + "\n" +
                "Dinero real: S/ " + String.format("%.2f", montoCierreReal),
                "Confirmar cierre",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean cerrado = cajaDAO.cerrarCaja(sesionActiva.getIdSesion(), montoCierreReal, 
                    "Cierre normal del " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
                
                if (cerrado) {
                    cajaCerrada = true;
                    JOptionPane.showMessageDialog(this,
                        "¡Caja cerrada exitosamente!\n" +
                        "Sesión: " + sesionActiva.getNumeroSesion(),
                        "Cierre exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al cerrar la caja.\nIntente nuevamente.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al cerrar caja: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error inesperado: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Icon createPrintIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                g2d.fillRect(x + 2, y + 2, 12, 10);
                g2d.setColor(new Color(70, 130, 180));
                g2d.drawRect(x + 2, y + 2, 12, 10);
                g2d.fillRect(x + 4, y + 4, 8, 2);
                g2d.fillRect(x + 4, y + 7, 8, 2);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() { return 16; }
            
            @Override
            public int getIconHeight() { return 14; }
        };
    }
    
    private String convertirNumeroATexto(BigDecimal numero) {
        if (numero == null) return "cero";
        
        int parteEntera = numero.intValue();
        
        if (parteEntera == 0) return "cero";
        if (parteEntera == 1) return "uno";
        if (parteEntera == 2) return "dos";
        if (parteEntera == 3) return "tres";
        if (parteEntera == 4) return "cuatro";
        if (parteEntera == 5) return "cinco";
        if (parteEntera == 6) return "seis";
        if (parteEntera == 7) return "siete";
        if (parteEntera == 8) return "ocho";
        if (parteEntera == 9) return "nueve";
        if (parteEntera == 10) return "diez";
        if (parteEntera == 11) return "once";
        if (parteEntera == 12) return "doce";
        if (parteEntera == 13) return "trece";
        if (parteEntera == 14) return "catorce";
        if (parteEntera == 15) return "quince";
        if (parteEntera == 16) return "dieciséis";
        if (parteEntera == 17) return "diecisiete";
        if (parteEntera == 18) return "dieciocho";
        if (parteEntera == 19) return "diecinueve";
        if (parteEntera == 20) return "veinte";
        if (parteEntera == 24) return "veinticuatro";
        if (parteEntera == 30) return "treinta";
        if (parteEntera == 50) return "cincuenta";
        if (parteEntera == 54) return "cincuenta y cuatro";
        
        if (parteEntera < 30) {
            return "veinti" + convertirUnidad(parteEntera - 20);
        }
        if (parteEntera < 100) {
            String[] decenas = {"", "", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"};
            int d = parteEntera / 10;
            int u = parteEntera % 10;
            return decenas[d] + (u > 0 ? " y " + convertirUnidad(u) : "");
        }
        if (parteEntera == 100) return "cien";
        if (parteEntera < 1000) {
            String[] centenas = {"", "ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"};
            int c = parteEntera / 100;
            int resto = parteEntera % 100;
            return centenas[c] + (resto > 0 ? " " + convertirNumeroATexto(BigDecimal.valueOf(resto)) : "");
        }
        
        return "número grande";
    }
    
    private String convertirUnidad(int numero) {
        String[] unidades = {"", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"};
        return numero < unidades.length ? unidades[numero] : "";
    }
    
    public boolean isCajaCerrada() {
        return cajaCerrada;
    }
}