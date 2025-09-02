/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

 /*
 * VistaPrincipal con control de roles - VERSIÓN CORREGIDA
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.util.SessionManager;
import com.pos.sistemagams.modelo.Usuario;
import com.pos.sistemagams.modelo.SesionCaja;
import com.pos.sistemagams.dao.CajaDAO;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Diego
 */
/**
 * Vista principal con control de acceso por roles
 */
public class VistaPrincipal extends javax.swing.JFrame {

    private PanelCaja panelCaja;
    private Usuario usuarioActual;
    private CajaDAO cajaDAO;
    private SesionCaja sesionActiva;
    private PanelVentas panelVentas;

    /**
     * Creates new form VistaPrincipal
     */
    public VistaPrincipal() {
        initComponents();

        // Obtener usuario actual
        usuarioActual = SessionManager.getCurrentUser();

        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Error: No hay usuario logueado", "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }

        // Inicializar DAO
        cajaDAO = new CajaDAO();

        // Configurar acceso según rol
        configurarAccesoPorRol();

        // Inicializar paneles
        inicializarPaneles();

        // Configurar vista inicial y verificar apertura de caja
        configurarVistaInicial();
    }

    /**
     * Configura el acceso a las pestañas según el rol del usuario
     */
    private void configurarAccesoPorRol() {
        String rol = usuarioActual.getRol();

        if ("VENDEDOR".equalsIgnoreCase(rol)) {
            // VENDEDOR: Solo acceso a CAJA
            btnInventario.setEnabled(false);
            btnVentas.setEnabled(false);
            btnClientes.setEnabled(false);
            btnConfiguracion.setEnabled(false);

            // Cambiar color para indicar que están deshabilitadas
            btnInventario.setBackground(Color.LIGHT_GRAY);
            btnVentas.setBackground(Color.LIGHT_GRAY);
            btnClientes.setBackground(Color.LIGHT_GRAY);
            btnConfiguracion.setBackground(Color.LIGHT_GRAY);

            // Agregar tooltips explicativos
            btnInventario.setToolTipText("Solo disponible para ADMIN");
            btnVentas.setToolTipText("Solo disponible para ADMIN");
            btnClientes.setToolTipText("Solo disponible para ADMIN");
            btnConfiguracion.setToolTipText("Solo disponible para ADMIN");

            System.out.println("✅ Acceso configurado para VENDEDOR: Solo CAJA disponible");

        } else if ("ADMIN".equalsIgnoreCase(rol)) {
            // ADMIN: Acceso a todo
            System.out.println("✅ Acceso configurado para ADMIN: Todas las pestañas disponibles");

        } else {
            JOptionPane.showMessageDialog(this,
                    "Rol no reconocido: " + rol + "\nContacte al administrador",
                    "Error de permisos", JOptionPane.WARNING_MESSAGE);
        }

        // Actualizar título de la ventana con información del usuario
        this.setTitle("POS GAMS - " + usuarioActual.getNombreCompleto() + " (" + rol + ")");
    }

    /**
     * Inicializa los paneles según los permisos del usuario
     */
    private void inicializarPaneles() {
        // Siempre crear el panel de caja
        panelCaja = new PanelCaja();
        panelContenedor.add(panelCaja, "caja");

        // Solo crear otros paneles si es ADMIN
        if ("ADMIN".equalsIgnoreCase(usuarioActual.getRol())) {
            PanelInventario panelInventario = new PanelInventario();
            panelVentas = new PanelVentas(); // ← Asignar a la variable de clase
            PanelClientes panelClientes = new PanelClientes();
            PanelConfiguracion panelConfiguracion = new PanelConfiguracion();

            panelContenedor.add(panelInventario, "inventario");
            panelContenedor.add(panelVentas, "ventas");
            panelContenedor.add(panelClientes, "clientes");
            panelContenedor.add(panelConfiguracion, "configuracion");
        }
    }

    /**
     * Configura la vista inicial y verifica/solicita apertura de caja
     */
    private void configurarVistaInicial() {
        // Mostrar siempre la pestaña CAJA al inicio
        CardLayout layout = (CardLayout) panelContenedor.getLayout();
        layout.show(panelContenedor, "caja");

        // Verificar apertura de caja en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            verificarAperturaCaja();
        });
    }

    /**
     * Verifica si hay una caja abierta, si no, solicita apertura obligatoria
     */
    private void verificarAperturaCaja() {
        try {
            // Verificar si ya tiene una sesión activa
            sesionActiva = cajaDAO.obtenerSesionActiva(usuarioActual.getIdUsuario());

            if (sesionActiva != null) {
                // Ya tiene caja abierta
                System.out.println("✅ Caja ya abierta: " + sesionActiva.getNumeroSesion());
                habilitarFuncionalidadCaja(true);
                if (panelCaja != null) {
                    panelCaja.configurarSesionActiva(sesionActiva);
                }
            } else {
                // No tiene caja abierta - APERTURA OBLIGATORIA solo para primera vez
                System.out.println("⚠️ No hay caja abierta - Solicitando apertura");
                habilitarFuncionalidadCaja(false);
                mostrarDialogoAperturaCaja();
            }

        } catch (Exception e) {
            System.err.println("❌ Error al verificar apertura de caja: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al verificar el estado de la caja: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            // En caso de error, permitir trabajar sin caja (modo degradado)
            habilitarFuncionalidadCaja(false);
        }
    }

    /**
     * Muestra el diálogo de apertura de caja (OBLIGATORIO)
     */
    private void mostrarDialogoAperturaCaja() {
        DialogoAperturaCaja dialogo = new DialogoAperturaCaja(this, usuarioActual);
        dialogo.setModal(true);
        dialogo.setLocationRelativeTo(this);

        // CAMBIO: Permitir cerrar el diálogo
        dialogo.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);

        dialogo.setVisible(true);

        // Cuando el diálogo se cierre, verificar si se abrió la caja
        if (dialogo.isCajaAbierta()) {
            sesionActiva = dialogo.getSesionCreada();
            habilitarFuncionalidadCaja(true);
            if (panelCaja != null) {
                panelCaja.configurarSesionActiva(sesionActiva);
            }
            System.out.println("✅ Caja abierta exitosamente desde diálogo");

            JOptionPane.showMessageDialog(this,
                    "✅ Caja abierta exitosamente\n"
                    + "Sesión: " + sesionActiva.getNumeroSesion() + "\n"
                    + "Puede comenzar a trabajar",
                    "Caja Lista",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Si no abrió la caja, mantener controles deshabilitados
            habilitarFuncionalidadCaja(false);
            System.out.println("⚠️ No se abrió la caja - Controles deshabilitados");

            JOptionPane.showMessageDialog(this,
                    "⚠️ Sin caja abierta\n"
                    + "Los controles están deshabilitados.\n"
                    + "Use el botón 'Apertura de Caja' cuando esté listo.",
                    "Sin caja activa",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Habilita o deshabilita la funcionalidad de caja según el estado
     */
    private void habilitarFuncionalidadCaja(boolean habilitar) {
        if (panelCaja != null) {
            panelCaja.habilitarControles(habilitar);
        }
        System.out.println(habilitar ? "✅ Funcionalidad de caja habilitada" : "⚠️ Funcionalidad de caja deshabilitada");
    }

    /**
     * Método para cerrar caja (llamado desde PanelCaja)
     */
    public void cerrarCaja() {
        if (sesionActiva != null) {
            DialogoCierreCaja dialogo = new DialogoCierreCaja(this, sesionActiva);
            dialogo.setModal(true);
            dialogo.setLocationRelativeTo(this);
            dialogo.setVisible(true);

            if (dialogo.isCajaCerrada()) {
                sesionActiva = null;
                habilitarFuncionalidadCaja(false);

                // Preguntar si quiere abrir nueva caja o salir
                int opcion = JOptionPane.showConfirmDialog(this,
                        "Caja cerrada exitosamente.\n¿Desea abrir una nueva caja?",
                        "Caja cerrada",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (opcion == JOptionPane.YES_OPTION) {
                    mostrarDialogoAperturaCaja();
                } else {
                    System.exit(0);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "No hay una caja abierta para cerrar",
                    "Sin caja activa",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refrescarPanelVentas() {
        if (panelVentas != null) {
            panelVentas.refrescarDatos();
            System.out.println("✅ Panel de ventas refrescado");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu6 = new javax.swing.JMenu();
        panelContenedor = new javax.swing.JPanel();
        panelSuperior = new javax.swing.JPanel();
        btnCaja = new javax.swing.JButton();
        btnInventario = new javax.swing.JButton();
        btnVentas = new javax.swing.JButton();
        btnClientes = new javax.swing.JButton();
        btnConfiguracion = new javax.swing.JButton();

        jMenu6.setText("jMenu6");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelContenedor.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panelContenedor.setLayout(new java.awt.CardLayout());

        panelSuperior.setLayout(new java.awt.GridLayout(1, 0));

        btnCaja.setText("CAJA");
        btnCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCajaActionPerformed(evt);
            }
        });
        panelSuperior.add(btnCaja);

        btnInventario.setText("INVENTARIO");
        btnInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarioActionPerformed(evt);
            }
        });
        panelSuperior.add(btnInventario);

        btnVentas.setText("VENTAS");
        btnVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentasActionPerformed(evt);
            }
        });
        panelSuperior.add(btnVentas);

        btnClientes.setText("CLIENTES");
        btnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesActionPerformed(evt);
            }
        });
        panelSuperior.add(btnClientes);

        btnConfiguracion.setText("CONFIGURACION");
        btnConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfiguracionActionPerformed(evt);
            }
        });
        panelSuperior.add(btnConfiguracion);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, 912, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCajaActionPerformed
        CardLayout layout = (CardLayout) panelContenedor.getLayout();
        layout.show(panelContenedor, "caja");

    }//GEN-LAST:event_btnCajaActionPerformed

    private void btnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarioActionPerformed
        if (!btnInventario.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                    "No tiene permisos para acceder a Inventario",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CardLayout layout = (CardLayout) panelContenedor.getLayout();
        layout.show(panelContenedor, "inventario");

    }//GEN-LAST:event_btnInventarioActionPerformed

    private void btnVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentasActionPerformed
        if (!btnVentas.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                    "No tiene permisos para acceder a Ventas",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CardLayout layout = (CardLayout) panelContenedor.getLayout();
        layout.show(panelContenedor, "ventas");

    }//GEN-LAST:event_btnVentasActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        if (!btnClientes.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                    "No tiene permisos para acceder a Clientes",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CardLayout layout = (CardLayout) panelContenedor.getLayout();
        layout.show(panelContenedor, "clientes");


    }//GEN-LAST:event_btnClientesActionPerformed


    private void btnConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfiguracionActionPerformed
        if (!btnConfiguracion.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                    "No tiene permisos para acceder a Configuración",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CardLayout layout = (CardLayout) panelContenedor.getLayout();
        layout.show(panelContenedor, "configuracion");
    }//GEN-LAST:event_btnConfiguracionActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VistaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VistaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VistaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VistaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VistaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCaja;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnConfiguracion;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnVentas;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JPanel panelContenedor;
    private javax.swing.JPanel panelSuperior;
    // End of variables declaration//GEN-END:variables
}
