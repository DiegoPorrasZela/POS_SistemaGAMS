/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

 /*
 * PanelCaja completo con toda la funcionalidad del sistema de ventas
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.util.SessionManager;
import com.pos.sistemagams.modelo.*;
import com.pos.sistemagams.dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Diego
 */
/**
 * Panel principal de caja con funcionalidad completa
 */
public class PanelCaja extends javax.swing.JPanel {

    // DAOs
    private ProductoDAO productoDAO;
    private CajaDAO cajaDAO;

    // Modelos de tabla
    private DefaultTableModel modeloTablaVenta;

    // Variables de control
    private SesionCaja sesionActiva;
    private List<ItemVenta> itemsVenta;
    private boolean cajaHabilitada = false;

    // Variables para el total
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal igv = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;

    // Número de ticket actual
    private String numeroTicketActual = "000001";

    /**
     * Creates new form PanelCaja
     */
    public PanelCaja() {
        initComponents();
        inicializarComponentes();
        cargarInformacionUsuario();
    }

    /**
     * Inicializa los componentes y configuraciones
     */
    private void inicializarComponentes() {
        // Inicializar DAOs
        productoDAO = new ProductoDAO();
        cajaDAO = new CajaDAO();

        // Inicializar lista de items
        itemsVenta = new ArrayList<>();

        // Configurar tabla de venta
        configurarTablaVenta();

        // Deshabilitar controles inicialmente
        habilitarControles(false);

        // Inicializar displays
        actualizarDisplayTotales();

        System.out.println("✅ PanelCaja inicializado");
    }

    /**
     * Configura la tabla de venta
     */
    private void configurarTablaVenta() {
        String[] columnas = {
            "CLAVE", "NOMBRE DEL PRODUCTO/DESCRIPCION", "U.M.",
            "CANTIDAD", "PRECIO U.", "TOTAL"
        };

        modeloTablaVenta = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(modeloTablaVenta);

        // Configurar ancho de columnas
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);  // CLAVE
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);  // DESCRIPCION
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(50);   // U.M.
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(80);   // CANTIDAD
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);   // PRECIO U.
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);  // TOTAL
        }

        // Configurar renderizadores
        configurarRenderizadores();
    }

    /**
     * Configura los renderizadores de la tabla
     */
    private void configurarRenderizadores() {
        // Renderer para números alineados a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        jTable1.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // CANTIDAD
        jTable1.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // PRECIO U.
        jTable1.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // TOTAL
    }

    /**
     * Carga la información del usuario logueado
     */
    private void cargarInformacionUsuario() {
        try {
            if (SessionManager.getCurrentUser() != null) {
                String nombreCompleto = SessionManager.getCurrentUser().getNombreCompleto();
                txtVendedor.setText(nombreCompleto);
                System.out.println("✅ Usuario mostrado en caja: " + nombreCompleto);
            } else {
                txtVendedor.setText("Usuario no identificado");
                System.out.println("⚠️ No hay usuario en sesión");
            }
        } catch (Exception e) {
            txtVendedor.setText("Error al cargar usuario");
            System.err.println("❌ Error al cargar usuario en PanelCaja: " + e.getMessage());
        }
    }

    /**
     * Configura la sesión activa de caja
     */
    public void configurarSesionActiva(SesionCaja sesion) {
        this.sesionActiva = sesion;
        if (sesion != null) {
            System.out.println("✅ Sesión configurada: " + sesion.getNumeroSesion());
            habilitarControles(true);
            generarNumeroTicket();
        }
    }

    /**
     * Genera un número de ticket único
     */
    private void generarNumeroTicket() {
        // TODO: Implementar generación real basada en BD
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMM");
        String mesAno = sdf.format(new java.util.Date());

        // Por ahora, generar número secuencial simple
        int siguienteNumero = (int) (Math.random() * 9999) + 1;
        numeroTicketActual = mesAno + "-" + String.format("%06d", siguienteNumero);
        txtNumeroTicket.setText(numeroTicketActual);
    }

    /**
     * Habilita o deshabilita los controles de la caja
     */
    public void habilitarControles(boolean habilitar) {
        this.cajaHabilitada = habilitar;

        // Controles principales
        txtBuscarProducto.setEnabled(habilitar);
        btnBuscar.setEnabled(habilitar);

        // Botones de manipulación
        btnBorrarItem.setEnabled(habilitar);
        btnInformacionItem.setEnabled(habilitar);
        btnCantidad.setEnabled(habilitar);
        btnEscogerPrecio.setEnabled(habilitar);

        // Botones de movimientos
        btnEntradas.setEnabled(habilitar);
        btnSalidas.setEnabled(habilitar);
        btnDevolucion.setEnabled(habilitar);
        btnCobrar.setEnabled(habilitar);

        // Cambiar color según estado
        if (habilitar) {
            setBackground(Color.WHITE);
        } else {
            setBackground(Color.LIGHT_GRAY);
        }

        System.out.println(habilitar ? "✅ Controles habilitados" : "⚠️ Controles deshabilitados");
    }

    // ========================================
    // MÉTODOS DE BÚSQUEDA DE PRODUCTOS
    // ========================================
    /**
     * Muestra el diálogo para buscar productos
     */
    private void mostrarDialogoBuscarProductos() {
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }

        DialogoBuscarProductos dialogo = new DialogoBuscarProductos(
                (Frame) SwingUtilities.getWindowAncestor(this));
        dialogo.setModal(true);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);

        Producto productoSeleccionado = dialogo.getProductoSeleccionado();
        if (productoSeleccionado != null) {
            agregarProductoAVenta(productoSeleccionado, BigDecimal.ONE);
        }
    }

    /**
     * Busca un producto por código ingresado
     */
    private void buscarProductoPorCodigo() {
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }

        String codigo = txtBuscarProducto.getText().trim();
        if (codigo.isEmpty()) {
            mostrarDialogoBuscarProductos();
            return;
        }

        try {
            Producto producto = productoDAO.obtenerProductoPorCodigo(codigo);
            if (producto != null) {
                agregarProductoAVenta(producto, BigDecimal.ONE);
                txtBuscarProducto.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró un producto con el código: " + codigo,
                        "Producto no encontrado",
                        JOptionPane.WARNING_MESSAGE);
                txtBuscarProducto.selectAll();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Agrega un producto a la venta
     */
    private void agregarProductoAVenta(Producto producto, BigDecimal cantidad) {
        try {
            // Verificar stock disponible
            if (!verificarStockDisponible(producto, cantidad)) {
                return;
            }

            // Buscar si el producto ya está en la venta
            ItemVenta itemExistente = buscarItemExistente(producto);

            if (itemExistente != null) {
                // Actualizar cantidad del item existente
                BigDecimal nuevaCantidad = itemExistente.getCantidad().add(cantidad);
                if (!verificarStockDisponible(producto, nuevaCantidad)) {
                    return;
                }
                itemExistente.setCantidad(nuevaCantidad);
                itemExistente.calcularTotal();
            } else {
                // Crear nuevo item
                ItemVenta nuevoItem = new ItemVenta();
                nuevoItem.setProducto(producto);
                nuevoItem.setCantidad(cantidad);
                nuevoItem.setPrecioUnitario(producto.getPrecioVenta1());
                nuevoItem.calcularTotal();
                itemsVenta.add(nuevoItem);
            }

            actualizarTablaVenta();
            calcularTotales();

            System.out.println("✅ Producto agregado: " + producto.getNombre());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al agregar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Verifica si hay stock disponible
     */
    private boolean verificarStockDisponible(Producto producto, BigDecimal cantidadSolicitada) {
        try {
            // Obtener stock real del producto
            int stockActual = productoDAO.obtenerStockProducto(producto.getIdProducto());

            if (stockActual <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El producto '" + producto.getNombre() + "' no tiene stock disponible",
                        "Sin stock",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (cantidadSolicitada.intValue() > stockActual) {
                JOptionPane.showMessageDialog(this,
                        "Stock insuficiente para '" + producto.getNombre() + "'\n"
                        + "Stock disponible: " + stockActual + "\n"
                        + "Cantidad solicitada: " + cantidadSolicitada.intValue(),
                        "Stock insuficiente",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error al verificar stock: " + e.getMessage());
            // En caso de error, permitir la operación pero avisar
            JOptionPane.showMessageDialog(this,
                    "No se pudo verificar el stock. Procediendo con precaución.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return true;
        }
    }

    /**
     * Busca un item existente en la venta
     */
    private ItemVenta buscarItemExistente(Producto producto) {
        return itemsVenta.stream()
                .filter(item -> item.getProducto().getIdProducto() == producto.getIdProducto())
                .findFirst()
                .orElse(null);
    }

    // ========================================
    // MÉTODOS DE MANIPULACIÓN DE ITEMS
    // ========================================
    /**
     * Borra el item seleccionado de la venta
     */
    private void borrarItemSeleccionado() {
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }

        int filaSeleccionada = jTable1.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto para eliminar",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreProducto = (String) modeloTablaVenta.getValueAt(filaSeleccionada, 1);
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar el producto seleccionado?\n" + nombreProducto,
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            itemsVenta.remove(filaSeleccionada);
            actualizarTablaVenta();
            calcularTotales();
            System.out.println("✅ Item eliminado: " + nombreProducto);
        }
    }

    /**
     * Muestra información del item seleccionado
     */
    private void mostrarInformacionItem() {
        int filaSeleccionada = jTable1.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto para ver información",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ItemVenta item = itemsVenta.get(filaSeleccionada);
        DialogoInformacionProducto dialogo = new DialogoInformacionProducto(
                (Frame) SwingUtilities.getWindowAncestor(this),
                item.getProducto(),
                SessionManager.getCurrentUser().getRol());
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    /**
     * Modifica la cantidad del item seleccionado
     */
    private void modificarCantidad() {
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }

        int filaSeleccionada = jTable1.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto para cambiar cantidad",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ItemVenta item = itemsVenta.get(filaSeleccionada);
        DialogoCantidad dialogo = new DialogoCantidad(
                (Frame) SwingUtilities.getWindowAncestor(this),
                item.getProducto().getNombre(),
                item.getCantidad());
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);

        if (dialogo.isCantidadConfirmada()) {
            BigDecimal nuevaCantidad = dialogo.getCantidadIngresada();
            if (verificarStockDisponible(item.getProducto(), nuevaCantidad)) {
                item.setCantidad(nuevaCantidad);
                item.calcularTotal();
                actualizarTablaVenta();
                calcularTotales();
                System.out.println("✅ Cantidad actualizada: " + nuevaCantidad);
            }
        }
    }

    /**
     * Permite escoger el precio del item seleccionado
     */
    private void escogerPrecio() {
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }

        int filaSeleccionada = jTable1.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto para cambiar precio",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ItemVenta item = itemsVenta.get(filaSeleccionada);
        DialogoSeleccionarPrecio dialogo = new DialogoSeleccionarPrecio(
                (Frame) SwingUtilities.getWindowAncestor(this),
                item.getProducto());
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);

        if (dialogo.isPrecioSeleccionado()) {
            BigDecimal nuevoPrecio = dialogo.getPrecioSeleccionado();
            item.setPrecioUnitario(nuevoPrecio);
            item.calcularTotal();
            actualizarTablaVenta();
            calcularTotales();
            System.out.println("✅ Precio actualizado: $" + nuevoPrecio);
        }
    }

    // ========================================
    // MÉTODOS DE COBRO
    // ========================================
    /**
     * Procesa el cobro de la venta
     */
    private void procesarCobro() {
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }

        if (itemsVenta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay productos en la venta",
                    "Venta vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        DialogoCobrar dialogo = new DialogoCobrar(
                (Frame) SwingUtilities.getWindowAncestor(this),
                total); // Solo pasar el total por ahora
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);

        if (dialogo.isVentaCompletada()) {
            // Procesar la venta
            procesarVentaCompletada(dialogo);
        }
    }

    /**
     * Procesa una venta completada
     */
    private void procesarVentaCompletada(DialogoCobrar dialogo) {
        try {
            // Crear objeto Venta
            VentaDAO ventaDAO = new VentaDAO();

            Venta venta = new Venta();
            venta.setNumeroVenta(ventaDAO.generarNumeroVenta());
            venta.setNumeroTicket(numeroTicketActual);
            venta.setIdUsuario(SessionManager.getCurrentUser().getIdUsuario());
            venta.setIdSesionCaja(sesionActiva.getIdSesion());
            venta.setSubtotal(subtotal);
            venta.setDescuento(dialogo.getDescuentoPorcentaje());
            venta.setTotal(dialogo.getTotalNeto());
            venta.setEfectivoRecibido(dialogo.getMontoEfectivo());
            venta.setCambio(dialogo.getCambio());
            venta.setMetodoPago(dialogo.getMetodoPago());

            // Guardar venta en base de datos
            boolean guardado = ventaDAO.guardarVenta(venta, itemsVenta);

            if (guardado) {
                // Limpiar la venta actual
                limpiarVenta();

                JOptionPane.showMessageDialog(this,
                        "¡Venta procesada exitosamente!\n"
                        + "Ticket: " + numeroTicketActual + "\n"
                        + "Total: S/ " + String.format("%.2f", dialogo.getTotalNeto()) + "\n"
                        + "Método: " + dialogo.getMetodoPago(),
                        "Venta completada",
                        JOptionPane.INFORMATION_MESSAGE);

                System.out.println("✅ Venta guardada exitosamente: " + numeroTicketActual);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error: No se pudo guardar la venta.\nIntente nuevamente.",
                        "Error en venta",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al procesar venta: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al procesar la venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Limpia la venta actual
     */
    private void limpiarVenta() {
        itemsVenta.clear();
        actualizarTablaVenta();
        calcularTotales();
        txtBuscarProducto.setText("");
        txtBuscarProducto.requestFocus();
        generarNumeroTicket();

        // Refrescar panel de ventas
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof VistaPrincipal) {
            ((VistaPrincipal) parentWindow).refrescarPanelVentas();
        }

        System.out.println("✅ Venta limpiada");
    }

    // ========================================
    // MÉTODOS DE ACTUALIZACIÓN DE INTERFAZ
    // ========================================
    /**
     * Actualiza la tabla de venta
     */
    private void actualizarTablaVenta() {
        // Limpiar tabla
        modeloTablaVenta.setRowCount(0);

        // Agregar items
        for (ItemVenta item : itemsVenta) {
            Object[] fila = {
                item.getProducto().getCodigo(),
                item.getProducto().getNombre(),
                item.getProducto().getUnidadCompra(),
                item.getCantidad(),
                String.format("%.2f", item.getPrecioUnitario()),
                String.format("%.2f", item.getTotal())
            };
            modeloTablaVenta.addRow(fila);
        }
    }

    /**
     * Calcula los totales de la venta (IGV INCLUIDO)
     */
    private void calcularTotales() {
        total = BigDecimal.ZERO;
        subtotal = BigDecimal.ZERO;
        igv = BigDecimal.ZERO;

        for (ItemVenta item : itemsVenta) {
            BigDecimal totalItem = item.getTotal();
            total = total.add(totalItem);

            // Calcular subtotal e IGV según la fórmula correcta
            if (item.getProducto().isAplicaIgv()) {
                // Si aplica IGV: Subtotal = Total / 1.18, IGV = Total - Subtotal
                BigDecimal porcentajeTotal = BigDecimal.ONE.add(
                        item.getProducto().getPorcentajeIgv().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                );
                BigDecimal subtotalItem = totalItem.divide(porcentajeTotal, 2, RoundingMode.HALF_UP);
                BigDecimal igvItem = totalItem.subtract(subtotalItem);

                subtotal = subtotal.add(subtotalItem);
                igv = igv.add(igvItem);
            } else {
                // Si no aplica IGV: Subtotal = Total, IGV = 0
                subtotal = subtotal.add(totalItem);
            }
        }

        // Actualizar displays
        actualizarDisplayTotales();

        System.out.println("💰 Total: $" + total + " | Subtotal: $" + subtotal + " | IGV: $" + igv);
    }

    /**
     * Actualiza los displays de totales en la interfaz
     */
    private void actualizarDisplayTotales() {
        txtCantSubtotal.setText(String.format("%.2f", subtotal));
        txtCantImpuesto.setText(String.format("%.2f", igv));
        jLabel7.setText(String.format("%.2f", total));
        txtCantRegistro.setText(String.valueOf(itemsVenta.size()));
    }

    /**
     * Muestra mensaje cuando la caja no está abierta
     */
    private void mostrarMensajeCajaNoAbierta() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "⚠️ Debe abrir la caja primero para realizar operaciones\n\n"
                + "¿Desea abrir la caja ahora?",
                "Caja no abierta",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            abrirCajaManualmente();
        }
    }

    /**
     * Muestra diálogo para abrir caja manualmente
     */
    private void abrirCajaManualmente() {
        // Obtener referencia a VistaPrincipal
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof VistaPrincipal) {
            VistaPrincipal vistaPrincipal = (VistaPrincipal) parentWindow;

            // Verificar si ya hay una caja abierta
            if (cajaHabilitada) {
                int opcion = JOptionPane.showConfirmDialog(this,
                        "Ya hay una caja abierta.\n¿Desea cerrarla y abrir una nueva?",
                        "Caja ya abierta",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (opcion == JOptionPane.YES_OPTION) {
                    // Cerrar caja actual primero
                    vistaPrincipal.cerrarCaja();
                }
                return;
            }

            // Mostrar diálogo de apertura
            DialogoAperturaCaja dialogo = new DialogoAperturaCaja(
                    (Frame) parentWindow,
                    SessionManager.getCurrentUser());
            dialogo.setModal(true);
            dialogo.setLocationRelativeTo(this);
            dialogo.setVisible(true);

            if (dialogo.isCajaAbierta()) {
                SesionCaja nuevaSesion = dialogo.getSesionCreada();
                configurarSesionActiva(nuevaSesion);

                JOptionPane.showMessageDialog(this,
                        "✅ Caja abierta exitosamente\n"
                        + "Sesión: " + nuevaSesion.getNumeroSesion(),
                        "Caja abierta",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Método público para actualizar la información del usuario
     */
    public void actualizarUsuario() {
        cargarInformacionUsuario();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCierreCaja = new javax.swing.JButton();
        btnCobrar = new javax.swing.JButton();
        txtBuscarProducto = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtVendedor = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnBorrarItem = new javax.swing.JButton();
        btnInformacionItem = new javax.swing.JButton();
        btnCantidad = new javax.swing.JButton();
        btnEscogerPrecio = new javax.swing.JButton();
        btnEntradas = new javax.swing.JButton();
        btnSalidas = new javax.swing.JButton();
        btnDevolucion = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtNumeroTicket = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCantSubtotal = new javax.swing.JLabel();
        txtCantImpuesto = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCantRegistro = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        btnCierreCaja.setText("CIERRE DE CAJA");
        btnCierreCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCierreCajaActionPerformed(evt);
            }
        });

        btnCobrar.setText("COBRAR");
        btnCobrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCobrarActionPerformed(evt);
            }
        });

        txtBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarProductoActionPerformed(evt);
            }
        });

        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        jLabel1.setText("Producto:");

        jLabel2.setText("Vendedor:");

        txtVendedor.setText("@USUARIO_NOMBRE_APELLIDO");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "CLAVE", "NOMBRE DEL PRODUCTO/DESCRIPCION", "U.M.", "CANTIDAD", "PRECIO U.", "TOTAL"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(100);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setMinWidth(300);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(2).setMinWidth(20);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(20);
            jTable1.getColumnModel().getColumn(3).setMinWidth(50);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(4).setMinWidth(50);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(5).setMinWidth(50);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(50);
        }

        btnBorrarItem.setText("BORRAR ITEM");
        btnBorrarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarItemActionPerformed(evt);
            }
        });

        btnInformacionItem.setText("INFORMACION ITEM");
        btnInformacionItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInformacionItemActionPerformed(evt);
            }
        });

        btnCantidad.setText("CANTIDAD");
        btnCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCantidadActionPerformed(evt);
            }
        });

        btnEscogerPrecio.setText("ESCOGER PRECIO");
        btnEscogerPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEscogerPrecioActionPerformed(evt);
            }
        });

        btnEntradas.setText("ENTRADAS");
        btnEntradas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntradasActionPerformed(evt);
            }
        });

        btnSalidas.setText("SALIDAS");
        btnSalidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalidasActionPerformed(evt);
            }
        });

        btnDevolucion.setText("DEVOLUCION");
        btnDevolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDevolucionActionPerformed(evt);
            }
        });

        jLabel3.setText("TICKET N°:");

        txtNumeroTicket.setText("@N°deticket");

        jLabel4.setText("Sub Total:");

        txtCantSubtotal.setText("@cantSubtotal");

        txtCantImpuesto.setText("@cantImpuesto");

        jLabel7.setText("@cantTotal");

        txtCantRegistro.setText("@cantRegistros");

        jLabel9.setText("Impuesto");

        jLabel10.setText("Total");

        jLabel11.setText("N° Registros");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(119, 119, 119))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnDevolucion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnEntradas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(65, 65, 65)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel10)
                                        .addComponent(jLabel9)
                                        .addComponent(jLabel11)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCantSubtotal)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnCierreCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSalidas, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtCantImpuesto)
                            .addComponent(jLabel7)
                            .addComponent(txtCantRegistro)
                            .addComponent(txtNumeroTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))))
            .addGroup(layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(btnBorrarItem, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnInformacionItem)
                .addGap(18, 18, 18)
                .addComponent(btnCantidad)
                .addGap(18, 18, 18)
                .addComponent(btnEscogerPrecio)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtVendedor)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 499, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(242, 242, 242))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtVendedor))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscar))
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnBorrarItem)
                            .addComponent(btnInformacionItem)
                            .addComponent(btnCantidad)
                            .addComponent(btnEscogerPrecio)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtNumeroTicket))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtCantSubtotal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCantImpuesto)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCantRegistro)
                            .addComponent(jLabel11))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 3, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSalidas, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEntradas, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDevolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCierreCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(291, 291, 291))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCierreCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCierreCajaActionPerformed
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }

        // Obtener referencia a VistaPrincipal
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof VistaPrincipal) {
            ((VistaPrincipal) parentWindow).cerrarCaja();
        }
    }//GEN-LAST:event_btnCierreCajaActionPerformed

    private void btnCobrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCobrarActionPerformed
        procesarCobro();
    }//GEN-LAST:event_btnCobrarActionPerformed

    private void txtBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarProductoActionPerformed
        buscarProductoPorCodigo();
    }//GEN-LAST:event_txtBuscarProductoActionPerformed

    private void btnEntradasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntradasActionPerformed
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }
        // TODO: Implementar diálogo de entradas
        JOptionPane.showMessageDialog(this, "Función de entradas en desarrollo", "Próximamente", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnEntradasActionPerformed

    private void btnSalidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalidasActionPerformed
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }
        // TODO: Implementar diálogo de salidas
        JOptionPane.showMessageDialog(this, "Función de salidas en desarrollo", "Próximamente", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnSalidasActionPerformed

    private void btnDevolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDevolucionActionPerformed
        if (!cajaHabilitada) {
            mostrarMensajeCajaNoAbierta();
            return;
        }
        // TODO: Implementar diálogo de devoluciones
        JOptionPane.showMessageDialog(this, "Función de devoluciones en desarrollo", "Próximamente", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnDevolucionActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        mostrarDialogoBuscarProductos();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnBorrarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarItemActionPerformed
        borrarItemSeleccionado();
    }//GEN-LAST:event_btnBorrarItemActionPerformed

    private void btnInformacionItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInformacionItemActionPerformed
        mostrarInformacionItem();
    }//GEN-LAST:event_btnInformacionItemActionPerformed

    private void btnCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCantidadActionPerformed
        modificarCantidad();
    }//GEN-LAST:event_btnCantidadActionPerformed

    private void btnEscogerPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEscogerPrecioActionPerformed
        escogerPrecio();
    }//GEN-LAST:event_btnEscogerPrecioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBorrarItem;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCantidad;
    private javax.swing.JButton btnCierreCaja;
    private javax.swing.JButton btnCobrar;
    private javax.swing.JButton btnDevolucion;
    private javax.swing.JButton btnEntradas;
    private javax.swing.JButton btnEscogerPrecio;
    private javax.swing.JButton btnInformacionItem;
    private javax.swing.JButton btnSalidas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtBuscarProducto;
    private javax.swing.JLabel txtCantImpuesto;
    private javax.swing.JLabel txtCantRegistro;
    private javax.swing.JLabel txtCantSubtotal;
    private javax.swing.JLabel txtNumeroTicket;
    private javax.swing.JLabel txtVendedor;
    // End of variables declaration//GEN-END:variables
}
