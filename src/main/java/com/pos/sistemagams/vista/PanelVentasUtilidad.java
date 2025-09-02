/*
 * Panel para mostrar utilidades de ventas con filtros
 * VERSIÓN MEJORADA con controles de fecha
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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
/**
 *
 * @author Diego
 */
/**
 * Panel para mostrar utilidades de ventas con filtros mejorados
 */
public class PanelVentasUtilidad extends javax.swing.JPanel {
    
    private VentaDAO ventaDAO;
    private DefaultTableModel modeloTablaResumen;
    private DefaultTableModel modeloTablaDetalle;
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    // Componentes de filtro
    private JDateChooser dateChooserDesde;
    private JDateChooser dateChooserHasta;
    private JButton btnCargarHoy;
    private JButton btnCargarTodas;
    private JButton btnFiltrarFechas;
    private JButton btnRefrescar;
    private JLabel lblTotalRegistros;
    private JLabel lblTotalUtilidad;
    private JLabel lblTotalVentas;
    private JLabel lblTotalCostos;
    
    public PanelVentasUtilidad() {
        initComponents();
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        ventaDAO = new VentaDAO();
        
        // DESPUÉS de initComponents(), agregar filtros y configurar
        configurarLayoutYFiltros();
        configurarTablas();
        
        // Cargar datos iniciales
        cargarDatosUtilidad();
        
        System.out.println("✅ PanelVentasUtilidad inicializado con filtros");
    }
    
    /**
     * Modifica el layout después de initComponents() y agrega filtros
     */
    private void configurarLayoutYFiltros() {
        // Crear panel de filtros
        JPanel panelFiltros = crearPanelFiltros();
        
        // Modificar el layout principal
        this.removeAll();
        this.setLayout(new BorderLayout());
        
        // Agregar filtros en la parte superior
        this.add(panelFiltros, BorderLayout.NORTH);
        
        // Crear panel central para las dos tablas
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        // Panel izquierdo - Resumen por fechas
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Resumen por Fechas"));
        panelIzquierdo.add(jScrollPane1, BorderLayout.CENTER);
        
        // Panel derecho - Detalle por transacción
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Detalle por Transacción"));
        panelDerecho.add(jScrollPane2, BorderLayout.CENTER);
        
        // Usar JSplitPane para dividir las tablas
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(panelIzquierdo);
        splitPane.setRightComponent(panelDerecho);
        splitPane.setDividerLocation(650); // Posición inicial del divisor
        splitPane.setResizeWeight(0.5); // 50% para cada lado
        
        panelCentral.add(splitPane, BorderLayout.CENTER);
        this.add(panelCentral, BorderLayout.CENTER);
        
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
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Utilidad"));
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
        btnCargarHoy.addActionListener(e -> cargarDatosUtilidad());
        
        btnCargarTodas = new JButton("Todas las Utilidades");
        btnCargarTodas.setBackground(new Color(255, 140, 0));
        btnCargarTodas.setForeground(Color.WHITE);
        btnCargarTodas.addActionListener(e -> cargarTodasLasUtilidades());
        
        btnRefrescar = new JButton("🔄 Refrescar");
        btnRefrescar.addActionListener(e -> refrescarDatos());
        
        filaFechas.add(lblDesde);
        filaFechas.add(dateChooserDesde);
        filaFechas.add(lblHasta);
        filaFechas.add(dateChooserHasta);
        filaFechas.add(btnFiltrarFechas);
        filaFechas.add(Box.createHorizontalStrut(20));
        filaFechas.add(btnCargarHoy);
        filaFechas.add(btnCargarTodas);
        filaFechas.add(btnRefrescar);
        
        // Segunda fila: Estadísticas resumen
        JPanel filaEstadisticas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        lblTotalRegistros = new JLabel("Registros: 0");
        lblTotalRegistros.setFont(new Font("Arial", Font.BOLD, 12));
        
        lblTotalVentas = new JLabel("Ventas: S/ 0.00");
        lblTotalVentas.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalVentas.setForeground(new Color(34, 139, 34));
        
        lblTotalCostos = new JLabel("Costos: S/ 0.00");
        lblTotalCostos.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalCostos.setForeground(new Color(255, 140, 0));
        
        lblTotalUtilidad = new JLabel("Utilidad: S/ 0.00");
        lblTotalUtilidad.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalUtilidad.setForeground(new Color(70, 130, 180));
        
        filaEstadisticas.add(lblTotalRegistros);
        filaEstadisticas.add(Box.createHorizontalStrut(20));
        filaEstadisticas.add(lblTotalVentas);
        filaEstadisticas.add(Box.createHorizontalStrut(15));
        filaEstadisticas.add(lblTotalCostos);
        filaEstadisticas.add(Box.createHorizontalStrut(15));
        filaEstadisticas.add(lblTotalUtilidad);
        
        panelFiltros.add(filaFechas);
        panelFiltros.add(filaEstadisticas);
        
        return panelFiltros;
    }
    
    /**
     * Configura ambas tablas de utilidad
     */
    private void configurarTablas() {
        configurarTablaResumen();
        configurarTablaDetalle();
    }
    
    /**
     * Configura la tabla de resumen por fechas
     */
    private void configurarTablaResumen() {
        String[] columnasResumen = {
            "FECHA", "N° TICKETS", "N° DEVOLUCIONES", 
            "COSTOS", "VENTAS", "UTILIDAD"
        };
        
        modeloTablaResumen = new DefaultTableModel(columnasResumen, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jTable1.setModel(modeloTablaResumen);
        
        // Configurar ancho de columnas para tabla resumen
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
        }
        
        // Configurar renderizadores para tabla resumen
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
    }
    
    /**
     * Configura la tabla de detalle por transacción
     */
    private void configurarTablaDetalle() {
        String[] columnasDetalle = {
            "FECHA REGISTRO", "TIPO", "DOCUMENTO", 
            "COSTOS", "VENTAS", "UTILIDAD"
        };
        
        modeloTablaDetalle = new DefaultTableModel(columnasDetalle, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jTable2.setModel(modeloTablaDetalle);
        
        // Configurar ancho de columnas para tabla detalle
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(120);
            jTable2.getColumnModel().getColumn(1).setPreferredWidth(60);
            jTable2.getColumnModel().getColumn(2).setPreferredWidth(120);
            jTable2.getColumnModel().getColumn(3).setPreferredWidth(80);
            jTable2.getColumnModel().getColumn(4).setPreferredWidth(80);
            jTable2.getColumnModel().getColumn(5).setPreferredWidth(80);
        }
        
        // Configurar renderizadores para tabla detalle
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        jTable2.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable2.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jTable2.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        jTable2.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        jTable2.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
    }
    
    /**
     * Carga los datos de utilidad del día actual
     */
    public void cargarDatosUtilidad() {
        Date hoy = new Date();
        cargarUtilidadPorFecha(hoy, hoy);
        System.out.println("📅 Cargando utilidades de hoy");
    }
    
    /**
     * Carga TODAS las utilidades de la base de datos
     */
    public void cargarTodasLasUtilidades() {
        try {
            // Obtener fecha muy antigua (1 año atrás) hasta hoy
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);
            Date fechaInicio = cal.getTime();
            Date fechaFin = new Date();
            
            cargarUtilidadPorFecha(fechaInicio, fechaFin);
            System.out.println("📊 Cargando TODAS las utilidades");
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar todas las utilidades: " + e.getMessage());
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
        
        cargarUtilidadPorFecha(fechaDesde, fechaHasta);
        System.out.println("🔍 Filtrando utilidades desde " + formatoFecha.format(fechaDesde) + 
                          " hasta " + formatoFecha.format(fechaHasta));
    }
    
    /**
     * Carga utilidad por rango de fechas
     */
    public void cargarUtilidadPorFecha(Date fechaInicio, Date fechaFin) {
        try {
            List<Venta> ventas = ventaDAO.obtenerVentasPorFechas(fechaInicio, fechaFin);
            
            // Calcular utilidades para cada venta
            for (Venta venta : ventas) {
                BigDecimal utilidad = ventaDAO.calcularUtilidadVenta(venta.getIdVenta());
                venta.setUtilidad(utilidad);
            }
            
            actualizarTablaResumen(ventas);
            actualizarTablaDetalle(ventas);
            actualizarEstadisticas(ventas);
            
            System.out.println("✅ Datos de utilidad cargados para " + ventas.size() + " ventas");
            
        } catch (SQLException e) {
            System.err.println("❌ Error al cargar utilidades: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos de utilidad: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza la tabla de resumen por fechas
     */
    private void actualizarTablaResumen(List<Venta> ventas) {
        modeloTablaResumen.setRowCount(0);
        
        // Agrupar ventas por fecha
        Map<String, VentasDelDia> ventasPorFecha = new HashMap<>();
        
        for (Venta venta : ventas) {
            String fecha = formatoFecha.format(venta.getFechaVenta());
            
            VentasDelDia ventasDelDia = ventasPorFecha.getOrDefault(fecha, new VentasDelDia());
            ventasDelDia.numTickets++;
            ventasDelDia.totalVentas = ventasDelDia.totalVentas.add(venta.getTotal());
            ventasDelDia.totalUtilidad = ventasDelDia.totalUtilidad.add(venta.getUtilidad());
            // Los costos se calculan como ventas - utilidad
            ventasDelDia.totalCostos = ventasDelDia.totalVentas.subtract(ventasDelDia.totalUtilidad);
            
            ventasPorFecha.put(fecha, ventasDelDia);
        }
        
        // Agregar filas a la tabla (ordenadas por fecha)
        List<Map.Entry<String, VentasDelDia>> listaOrdenada = new ArrayList<>(ventasPorFecha.entrySet());
        listaOrdenada.sort(Map.Entry.comparingByKey());
        
        for (Map.Entry<String, VentasDelDia> entry : listaOrdenada) {
            String fecha = entry.getKey();
            VentasDelDia datos = entry.getValue();
            
            Object[] fila = {
                fecha,
                String.valueOf(datos.numTickets),
                "0", // N° DEVOLUCIONES - TODO: implementar cuando esté listo
                String.format("%.2f", datos.totalCostos),
                String.format("%.2f", datos.totalVentas),
                String.format("%.2f", datos.totalUtilidad)
            };
            
            modeloTablaResumen.addRow(fila);
        }
    }
    
    /**
     * Actualiza la tabla de detalle por transacción
     */
    private void actualizarTablaDetalle(List<Venta> ventas) {
        modeloTablaDetalle.setRowCount(0);
        
        for (Venta venta : ventas) {
            BigDecimal costos = venta.getTotal().subtract(venta.getUtilidad());
            
            Object[] fila = {
                formatoFechaHora.format(venta.getFechaVenta()),
                "Venta",
                venta.getNumeroTicket(),
                String.format("%.2f", costos),
                String.format("%.2f", venta.getTotal()),
                String.format("%.2f", venta.getUtilidad())
            };
            
            modeloTablaDetalle.addRow(fila);
        }
    }
    
    /**
     * Actualiza las estadísticas mostradas
     */
    private void actualizarEstadisticas(List<Venta> ventas) {
        int totalRegistros = ventas.size();
        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalUtilidad = BigDecimal.ZERO;
        BigDecimal totalCostos = BigDecimal.ZERO;
        
        for (Venta venta : ventas) {
            totalVentas = totalVentas.add(venta.getTotal());
            totalUtilidad = totalUtilidad.add(venta.getUtilidad());
        }
        
        totalCostos = totalVentas.subtract(totalUtilidad);
        
        if (lblTotalRegistros != null) {
            lblTotalRegistros.setText("Registros: " + totalRegistros);
        }
        if (lblTotalVentas != null) {
            lblTotalVentas.setText("Ventas: S/ " + String.format("%.2f", totalVentas));
        }
        if (lblTotalCostos != null) {
            lblTotalCostos.setText("Costos: S/ " + String.format("%.2f", totalCostos));
        }
        if (lblTotalUtilidad != null) {
            lblTotalUtilidad.setText("Utilidad: S/ " + String.format("%.2f", totalUtilidad));
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
     * Refresca los datos de utilidad
     */
    public void refrescarDatos() {
        if (dateChooserDesde != null && dateChooserHasta != null) {
            Date fechaDesde = dateChooserDesde.getDate();
            Date fechaHasta = dateChooserHasta.getDate();
            
            if (fechaDesde != null && fechaHasta != null) {
                cargarUtilidadPorFecha(fechaDesde, fechaHasta);
            } else {
                cargarDatosUtilidad();
            }
        } else {
            cargarDatosUtilidad();
        }
        
        System.out.println("🔄 Datos de utilidad refrescados");
    }
    
    /**
     * Clase auxiliar para agrupar ventas por día
     */
    private static class VentasDelDia {
        int numTickets = 0;
        int numDevoluciones = 0;
        BigDecimal totalCostos = BigDecimal.ZERO;
        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalUtilidad = BigDecimal.ZERO;
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "FECHA", "N° TICKETS", "N° DEVOLUCIONES", "COSTOS", "VENTAS", "UTILIDAD"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 644, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "FECHA REGISTRO", "TIPO", "DOCUMENTO", "COSTOS", "VENTAS", "UTILIDAD"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
