/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/*
 * Panel para mostrar las transacciones de ventas con filtros
 * VERSIÓN CORREGIDA para NetBeans
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.dao.VentaDAO;
import com.pos.sistemagams.modelo.Venta;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
/**
 *
 * @author Diego
 */
/**
 * Panel para mostrar transacciones de ventas con filtros mejorados
 */
public class PanelVentasTransacciones extends javax.swing.JPanel {
    
    private VentaDAO ventaDAO;
    private DefaultTableModel modeloTabla;
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat formatoFechaSolo = new SimpleDateFormat("dd/MM/yyyy");
    
    // Componentes de filtro
    private JDateChooser dateChooserDesde;
    private JDateChooser dateChooserHasta;
    private JTextField txtBuscarTicket;
    private JButton btnCargarHoy;
    private JButton btnCargarTodas;
    private JButton btnFiltrarFechas;
    private JButton btnBuscarTicket;
    private JButton btnRefrescar;
    private JLabel lblTotalRegistros;
    private JLabel lblTotalVentas;
    
    public PanelVentasTransacciones() {
        initComponents();
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        ventaDAO = new VentaDAO();
        
        // DESPUÉS de initComponents(), modificar el layout y agregar filtros
        configurarLayoutYFiltros();
        configurarTabla();
        
        // Cargar datos iniciales
        cargarVentasHoy();
        
        System.out.println("✅ PanelVentasTransacciones inicializado con filtros");
    }
    
    /**
     * Modifica el layout después de initComponents() y agrega filtros
     */
    private void configurarLayoutYFiltros() {
        // Cambiar el layout del panel principal
        this.removeAll();
        this.setLayout(new BorderLayout());
        
        // Crear y agregar panel de filtros
        JPanel panelFiltros = crearPanelFiltros();
        this.add(panelFiltros, BorderLayout.NORTH);
        
        // Agregar la tabla que ya existe
        this.add(jScrollPane1, BorderLayout.CENTER);
        
        // Revalidar para aplicar los cambios
        this.revalidate();
        this.repaint();
    }
    
    /**
     * Crea el panel de filtros superior
     */
    private JPanel crearPanelFiltros() {
        JPanel panelFiltros = new JPanel();
        panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));
        panelFiltros.setPreferredSize(new Dimension(0, 120));
        
        // Primera fila: Filtros de fecha
        JPanel filaFechas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel lblDesde = new JLabel("Desde:");
        dateChooserDesde = new JDateChooser();
        dateChooserDesde.setPreferredSize(new Dimension(120, 25));
        dateChooserDesde.setDate(obtenerInicioMes());
        
        JLabel lblHasta = new JLabel("Hasta:");
        dateChooserHasta = new JDateChooser();
        dateChooserHasta.setPreferredSize(new Dimension(120, 25));
        dateChooserHasta.setDate(new Date());
        
        btnFiltrarFechas = new JButton("Filtrar por Fechas");
        btnFiltrarFechas.setBackground(new Color(70, 130, 180));
        btnFiltrarFechas.setForeground(Color.WHITE);
        btnFiltrarFechas.addActionListener(e -> filtrarPorFechas());
        
        btnCargarHoy = new JButton("Solo Hoy");
        btnCargarHoy.setBackground(new Color(34, 139, 34));
        btnCargarHoy.setForeground(Color.WHITE);
        btnCargarHoy.addActionListener(e -> cargarVentasHoy());
        
        btnCargarTodas = new JButton("Todas las Ventas");
        btnCargarTodas.setBackground(new Color(255, 140, 0));
        btnCargarTodas.setForeground(Color.WHITE);
        btnCargarTodas.addActionListener(e -> cargarTodasLasVentas());
        
        filaFechas.add(lblDesde);
        filaFechas.add(dateChooserDesde);
        filaFechas.add(lblHasta);
        filaFechas.add(dateChooserHasta);
        filaFechas.add(btnFiltrarFechas);
        filaFechas.add(Box.createHorizontalStrut(20));
        filaFechas.add(btnCargarHoy);
        filaFechas.add(btnCargarTodas);
        
        // Segunda fila: Búsqueda por ticket y estadísticas
        JPanel filaBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel lblTicket = new JLabel("N° Ticket:");
        txtBuscarTicket = new JTextField(15);
        txtBuscarTicket.addActionListener(e -> buscarPorTicket());
        
        btnBuscarTicket = new JButton("Buscar");
        btnBuscarTicket.setBackground(new Color(70, 130, 180));
        btnBuscarTicket.setForeground(Color.WHITE);
        btnBuscarTicket.addActionListener(e -> buscarPorTicket());
        
        btnRefrescar = new JButton("🔄 Refrescar");
        btnRefrescar.addActionListener(e -> refrescarDatos());
        
        lblTotalRegistros = new JLabel("Registros: 0");
        lblTotalRegistros.setFont(new Font("Arial", Font.BOLD, 12));
        
        lblTotalVentas = new JLabel("Total: S/ 0.00");
        lblTotalVentas.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalVentas.setForeground(new Color(34, 139, 34));
        
        filaBusqueda.add(lblTicket);
        filaBusqueda.add(txtBuscarTicket);
        filaBusqueda.add(btnBuscarTicket);
        filaBusqueda.add(Box.createHorizontalStrut(20));
        filaBusqueda.add(btnRefrescar);
        filaBusqueda.add(Box.createHorizontalStrut(20));
        filaBusqueda.add(lblTotalRegistros);
        filaBusqueda.add(Box.createHorizontalStrut(10));
        filaBusqueda.add(lblTotalVentas);
        
        panelFiltros.add(filaFechas);
        panelFiltros.add(filaBusqueda);
        
        return panelFiltros;
    }
    
    /**
     * Configura la tabla de transacciones
     */
    private void configurarTabla() {
        String[] columnas = {
            "FECHA REGISTRO", "TIPO", "N° TICKET", "NOMBRE Y APELLIDO", 
            "CLIENTE", "MONTO TOTAL", "IMPUESTO", "SUB TOTAL", 
            "DESCUENTO", "EFECTIVO", "TARJETA"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jTable1.setModel(modeloTabla);
        
        // Configurar ancho de columnas
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(10).setPreferredWidth(80);
        }
        
        configurarRenderizadores();
    }
    
    private void configurarRenderizadores() {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Aplicar renderizadores
        jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        for (int i = 5; i <= 10; i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }
    }
    
    /**
     * Carga las ventas del día actual
     */
    public void cargarVentasHoy() {
        Date hoy = new Date();
        cargarVentasPorFecha(hoy, hoy);
        System.out.println("📅 Cargando ventas de hoy");
    }
    
    /**
     * Carga TODAS las ventas de la base de datos
     */
    public void cargarTodasLasVentas() {
        try {
            // Obtener fecha muy antigua (1 año atrás) hasta hoy
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);
            Date fechaInicio = cal.getTime();
            Date fechaFin = new Date();
            
            cargarVentasPorFecha(fechaInicio, fechaFin);
            System.out.println("📊 Cargando TODAS las ventas");
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar todas las ventas: " + e.getMessage());
        }
    }
    
    /**
     * Filtra por las fechas seleccionadas
     */
    private void filtrarPorFechas() {
        Date fechaDesde = dateChooserDesde.getDate();
        Date fechaHasta = dateChooserHasta.getDate();
        
        if (fechaDesde == null || fechaHasta == null) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione ambas fechas",
                "Fechas requeridas",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (fechaDesde.after(fechaHasta)) {
            JOptionPane.showMessageDialog(this,
                "La fecha 'Desde' no puede ser mayor que 'Hasta'",
                "Fechas inválidas",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        cargarVentasPorFecha(fechaDesde, fechaHasta);
        System.out.println("🔍 Filtrando desde " + formatoFechaSolo.format(fechaDesde) + 
                          " hasta " + formatoFechaSolo.format(fechaHasta));
    }
    
    /**
     * Busca por número de ticket
     */
    private void buscarPorTicket() {
        String numeroTicket = txtBuscarTicket.getText().trim();
        if (numeroTicket.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingrese un número de ticket para buscar",
                "Ticket requerido",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Buscar en todas las ventas
            cargarTodasLasVentas();
            
            // Filtrar en la tabla
            filtrarTablaByTicket(numeroTicket);
            System.out.println("🔍 Buscando ticket: " + numeroTicket);
            
        } catch (Exception e) {
            System.err.println("❌ Error en búsqueda: " + e.getMessage());
        }
    }
    
    /**
     * Filtra la tabla actual por número de ticket
     */
    private void filtrarTablaByTicket(String numeroTicket) {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        
        // Buscar filas que contengan el número de ticket
        for (int i = modelo.getRowCount() - 1; i >= 0; i--) {
            String ticketEnFila = (String) modelo.getValueAt(i, 2);
            if (!ticketEnFila.toLowerCase().contains(numeroTicket.toLowerCase())) {
                modelo.removeRow(i);
            }
        }
        
        actualizarEstadisticas();
    }
    
    /**
     * Carga ventas por rango de fechas
     */
    public void cargarVentasPorFecha(Date fechaInicio, Date fechaFin) {
        try {
            List<Venta> ventas = ventaDAO.obtenerVentasPorFechas(fechaInicio, fechaFin);
            actualizarTabla(ventas);
            actualizarEstadisticas();
            System.out.println("✅ Cargadas " + ventas.size() + " ventas");
            
        } catch (SQLException e) {
            System.err.println("❌ Error al cargar ventas: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al cargar las ventas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza la tabla con las ventas
     */
    private void actualizarTabla(List<Venta> ventas) {
        modeloTabla.setRowCount(0);
        
        for (Venta venta : ventas) {
            Object[] fila = {
                formatoFecha.format(venta.getFechaVenta()),
                "Venta",
                venta.getNumeroTicket(),
                venta.getUsuarioNombre() != null ? venta.getUsuarioNombre() : "N/A",
                venta.getClienteNombre() != null ? venta.getClienteNombre() : "Público General",
                String.format("%.2f", venta.getTotal()),
                String.format("%.2f", venta.calcularImpuesto()),
                String.format("%.2f", venta.calcularSubtotalSinIGV()),
                String.format("%.2f", venta.getDescuento()),
                "EFECTIVO".equals(venta.getMetodoPago()) ? String.format("%.2f", venta.getTotal()) : "0.00",
                !"EFECTIVO".equals(venta.getMetodoPago()) ? String.format("%.2f", venta.getTotal()) : "0.00"
            };
            modeloTabla.addRow(fila);
        }
        
        repaint();
    }
    
    /**
     * Actualiza las estadísticas mostradas
     */
    private void actualizarEstadisticas() {
        int totalRegistros = modeloTabla.getRowCount();
        double totalVentas = 0.0;
        
        for (int i = 0; i < totalRegistros; i++) {
            String montoStr = (String) modeloTabla.getValueAt(i, 5);
            try {
                totalVentas += Double.parseDouble(montoStr);
            } catch (NumberFormatException e) {
                // Ignorar errores de formato
            }
        }
        
        if (lblTotalRegistros != null) {
            lblTotalRegistros.setText("Registros: " + totalRegistros);
        }
        if (lblTotalVentas != null) {
            lblTotalVentas.setText("Total: S/ " + String.format("%.2f", totalVentas));
        }
    }
    
    /**
     * Obtiene el primer día del mes actual
     */
    private Date obtenerInicioMes() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Refresca los datos de la tabla
     */
    public void refrescarDatos() {
        // Refrescar con los últimos criterios utilizados
        if (dateChooserDesde != null && dateChooserHasta != null) {
            Date fechaDesde = dateChooserDesde.getDate();
            Date fechaHasta = dateChooserHasta.getDate();
            
            if (fechaDesde != null && fechaHasta != null) {
                cargarVentasPorFecha(fechaDesde, fechaHasta);
            } else {
                cargarVentasHoy();
            }
        } else {
            cargarVentasHoy();
        }
        
        System.out.println("🔄 Datos refrescados");
    }
    
    /**
     * Obtiene la venta seleccionada
     */
    public Venta getVentaSeleccionada() {
        int filaSeleccionada = jTable1.getSelectedRow();
        if (filaSeleccionada >= 0) {
            String numeroTicket = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
            System.out.println("📋 Venta seleccionada: " + numeroTicket);
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "FECHA REGISTRO", "TIPO", "N° TICKET", "NOMBRE Y APELLIDO", "CLIENTE", "MONTO TOTAL", "IMPUESTO", "SUB TOTAL", "DESCUENTO", "EFECTIVO", "TARJETA"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1062, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
