/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.modelo.Proveedor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author Diego
 */
public class DialogoProveedores extends JDialog {
    
    private ProductoDAO productoDAO;
    
    // Componentes de la interfaz
    private JList<Proveedor> listaProveedores;
    private DefaultListModel<Proveedor> modeloLista;
    private JTextField txtEmpresa;
    private JTextField txtContacto;
    private JTextField txtTelefono;
    private JTextField txtRuc;
    private JTextArea txtDireccion;
    private JButton btnNuevo;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnGuardar;
    
    // Variable para controlar el modo (nuevo/editar)
    private boolean modoEdicion = false;
    private Proveedor proveedorSeleccionado = null;
    
    public DialogoProveedores(Component parent) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null, 
              "Proveedores", ModalityType.APPLICATION_MODAL);
        
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarProveedores();
        configurarEventos();
    }
    
    // Constructor alternativo sin parámetros
    public DialogoProveedores() {
        super((Frame) null, "Proveedores", true);
        
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarProveedores();
        configurarEventos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = crearHeader();
        
        // Panel de contenido
        JPanel contenidoPanel = crearPanelContenido();
        
        panelPrincipal.add(headerPanel, BorderLayout.NORTH);
        panelPrincipal.add(contenidoPanel, BorderLayout.CENTER);
        
        add(panelPrincipal);
    }
    
    private JPanel crearHeader() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(70, 130, 180));
        panel.setPreferredSize(new Dimension(0, 50));
        
        JLabel lblTitulo = new JLabel("← Proveedores");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        panel.add(lblTitulo);
        
        return panel;
    }
    
    private JPanel crearPanelContenido() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel izquierdo - Lista de proveedores
        JPanel panelIzquierdo = crearPanelLista();
        
        // Panel derecho - Formulario
        JPanel panelDerecho = crearPanelFormulario();
        
        panel.add(panelIzquierdo, BorderLayout.WEST);
        panel.add(panelDerecho, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelLista() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(350, 500));
        panel.setBorder(BorderFactory.createTitledBorder("PROVEEDORES"));
        
        // Panel de cabecera de la lista
        JPanel headerLista = new JPanel(new GridLayout(1, 2));
        headerLista.setBackground(new Color(70, 130, 180));
        headerLista.setPreferredSize(new Dimension(0, 30));
        
        JLabel lblEmpresa = new JLabel("EMPRESA", SwingConstants.CENTER);
        lblEmpresa.setForeground(Color.WHITE);
        lblEmpresa.setFont(new Font("Arial", Font.BOLD, 11));
        
        JLabel lblContactoHeader = new JLabel("CONTACTO", SwingConstants.CENTER);
        lblContactoHeader.setForeground(Color.WHITE);
        lblContactoHeader.setFont(new Font("Arial", Font.BOLD, 11));
        
        headerLista.add(lblEmpresa);
        headerLista.add(lblContactoHeader);
        
        // Lista de proveedores con renderer personalizado
        modeloLista = new DefaultListModel<>();
        listaProveedores = new JList<>(modeloLista);
        listaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaProveedores.setCellRenderer(new ProveedorListRenderer());
        
        JScrollPane scrollPane = new JScrollPane(listaProveedores);
        scrollPane.setPreferredSize(new Dimension(330, 350));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout());
        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(0, 25));
        JButton btnBuscar = new JButton("🔍");
        btnBuscar.setPreferredSize(new Dimension(30, 25));
        
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(btnBuscar, BorderLayout.EAST);
        
        panel.add(headerLista, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelBusqueda, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Renderer personalizado para mostrar empresa y contacto
    private class ProveedorListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            if (value instanceof Proveedor) {
                Proveedor proveedor = (Proveedor) value;
                
                JPanel panel = new JPanel(new GridLayout(1, 2));
                panel.setOpaque(true);
                
                JLabel lblEmpresa = new JLabel(proveedor.getNombre());
                lblEmpresa.setFont(new Font("Arial", Font.PLAIN, 11));
                lblEmpresa.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                JLabel lblContacto = new JLabel(proveedor.getEmail() != null ? proveedor.getEmail() : "Sin contacto");
                lblContacto.setFont(new Font("Arial", Font.PLAIN, 11));
                lblContacto.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                if (isSelected) {
                    panel.setBackground(new Color(0, 120, 215));
                    lblEmpresa.setForeground(Color.WHITE);
                    lblContacto.setForeground(Color.WHITE);
                } else {
                    if (index % 2 == 0) {
                        panel.setBackground(new Color(0, 180, 216));
                    } else {
                        panel.setBackground(Color.WHITE);
                    }
                    lblEmpresa.setForeground(Color.BLACK);
                    lblContacto.setForeground(Color.BLACK);
                }
                
                panel.add(lblEmpresa);
                panel.add(lblContacto);
                
                return panel;
            }
            
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campo Empresa
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblEmpresa = new JLabel("Empresa:");
        lblEmpresa.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblEmpresa, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmpresa = new JTextField(25);
        txtEmpresa.setPreferredSize(new Dimension(300, 30));
        txtEmpresa.setEnabled(false);
        panel.add(txtEmpresa, gbc);
        
        // Campo Contacto
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblContacto = new JLabel("Contacto:");
        lblContacto.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblContacto, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtContacto = new JTextField(25);
        txtContacto.setPreferredSize(new Dimension(300, 30));
        txtContacto.setEnabled(false);
        panel.add(txtContacto, gbc);
        
        // Campo RUC
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblRuc = new JLabel("RUC:");
        lblRuc.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblRuc, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtRuc = new JTextField(25);
        txtRuc.setPreferredSize(new Dimension(300, 30));
        txtRuc.setEnabled(false);
        panel.add(txtRuc, gbc);
        
        // Campo Teléfono
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblTelefono, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtTelefono = new JTextField(25);
        txtTelefono.setPreferredSize(new Dimension(300, 30));
        txtTelefono.setEnabled(false);
        panel.add(txtTelefono, gbc);
        
        // Campo Dirección
        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblDireccion = new JLabel("Dirección:");
        lblDireccion.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblDireccion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 9; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        txtDireccion = new JTextArea(3, 25);
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        txtDireccion.setEnabled(false);
        JScrollPane scrollDireccion = new JScrollPane(txtDireccion);
        scrollDireccion.setPreferredSize(new Dimension(300, 70));
        panel.add(scrollDireccion, gbc);
        
        // Panel de botones
        gbc.gridx = 0; gbc.gridy = 10; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones, gbc);
        
        // Espacio en blanco
        gbc.gridx = 0; gbc.gridy = 11; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        
        // Botón Nuevo
        btnNuevo = new JButton("Nuevo ✓");
        btnNuevo.setBackground(new Color(70, 130, 180));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFont(new Font("Arial", Font.BOLD, 12));
        btnNuevo.setPreferredSize(new Dimension(120, 35));
        
        // Botón Modificar
        btnModificar = new JButton("Modificar 🔄");
        btnModificar.setBackground(new Color(70, 130, 180));
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setFont(new Font("Arial", Font.BOLD, 12));
        btnModificar.setPreferredSize(new Dimension(120, 35));
        btnModificar.setEnabled(false);
        
        // Botón Eliminar
        btnEliminar = new JButton("Eliminar ⊖");
        btnEliminar.setBackground(new Color(70, 130, 180));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEliminar.setPreferredSize(new Dimension(120, 35));
        btnEliminar.setEnabled(false);
        
        // Botón Guardar
        btnGuardar = new JButton("Guardar 💾");
        btnGuardar.setBackground(new Color(70, 130, 180));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.setEnabled(false);
        
        panel.add(btnNuevo);
        panel.add(btnModificar);
        panel.add(btnEliminar);
        panel.add(btnGuardar);
        
        return panel;
    }
    
    private void configurarDialog() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void configurarEventos() {
        // Evento de selección en la lista
        listaProveedores.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Proveedor seleccionado = listaProveedores.getSelectedValue();
                if (seleccionado != null) {
                    proveedorSeleccionado = seleccionado;
                    cargarDatosFormulario(seleccionado);
                    btnModificar.setEnabled(true);
                    btnEliminar.setEnabled(true);
                } else {
                    limpiarFormulario();
                }
            }
        });
        
        // Evento botón Nuevo
        btnNuevo.addActionListener(e -> {
            modoEdicion = false;
            proveedorSeleccionado = null;
            limpiarFormulario();
            habilitarCampos(true);
            btnGuardar.setEnabled(true);
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
            listaProveedores.clearSelection();
            txtEmpresa.requestFocus();
        });
        
        // Evento botón Modificar
        btnModificar.addActionListener(e -> {
            if (proveedorSeleccionado != null) {
                modoEdicion = true;
                habilitarCampos(true);
                btnGuardar.setEnabled(true);
                btnNuevo.setEnabled(false);
                btnEliminar.setEnabled(false);
                txtEmpresa.requestFocus();
            }
        });
        
        // Evento botón Eliminar
        btnEliminar.addActionListener(this::eliminarProveedor);
        
        // Evento botón Guardar
        btnGuardar.addActionListener(this::guardarProveedor);
    }
    
    private void cargarProveedores() {
        try {
            List<Proveedor> proveedores = productoDAO.obtenerProveedores();
            modeloLista.clear();
            
            for (Proveedor proveedor : proveedores) {
                modeloLista.addElement(proveedor);
            }
            
            System.out.println("✅ Cargados " + proveedores.size() + " proveedores");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar proveedores: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarDatosFormulario(Proveedor proveedor) {
        txtEmpresa.setText(proveedor.getNombre());
        txtContacto.setText(proveedor.getEmail() != null ? proveedor.getEmail() : "");
        txtRuc.setText(proveedor.getRuc() != null ? proveedor.getRuc() : "");
        txtTelefono.setText(proveedor.getTelefono() != null ? proveedor.getTelefono() : "");
        txtDireccion.setText(proveedor.getDireccion() != null ? proveedor.getDireccion() : "");
    }
    
    private void guardarProveedor(ActionEvent e) {
        String empresa = txtEmpresa.getText().trim();
        String contacto = txtContacto.getText().trim();
        String ruc = txtRuc.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();
        
        // Validación: Solo empresa es obligatoria
        if (empresa.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre de la empresa es obligatorio",
                "Campo requerido",
                JOptionPane.WARNING_MESSAGE);
            txtEmpresa.requestFocus();
            return;
        }
        
        try {
            boolean exito = false;
            
            if (modoEdicion && proveedorSeleccionado != null) {
                // Modificar proveedor existente
                proveedorSeleccionado.setNombre(empresa);
                proveedorSeleccionado.setEmail(contacto.isEmpty() ? null : contacto);
                proveedorSeleccionado.setRuc(ruc.isEmpty() ? null : ruc);
                proveedorSeleccionado.setTelefono(telefono.isEmpty() ? null : telefono);
                proveedorSeleccionado.setDireccion(direccion.isEmpty() ? null : direccion);
                
                exito = productoDAO.actualizarProveedor(proveedorSeleccionado);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Proveedor modificado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Nuevo proveedor
                Proveedor nuevoProveedor = new Proveedor();
                nuevoProveedor.setNombre(empresa);
                nuevoProveedor.setEmail(contacto.isEmpty() ? null : contacto);
                nuevoProveedor.setRuc(ruc.isEmpty() ? null : ruc);
                nuevoProveedor.setTelefono(telefono.isEmpty() ? null : telefono);
                nuevoProveedor.setDireccion(direccion.isEmpty() ? null : direccion);
                nuevoProveedor.setEstado(true);
                
                exito = productoDAO.guardarProveedor(nuevoProveedor);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Proveedor creado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (exito) {
                cargarProveedores();
                limpiarFormulario();
                resetearBotones();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar proveedor: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void eliminarProveedor(ActionEvent e) {
        if (proveedorSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un proveedor para eliminar",
                "Ningún proveedor seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el proveedor '" + proveedorSeleccionado.getNombre() + "'?\n\n" +
            "Esta acción no se puede deshacer y puede afectar a los productos que usan este proveedor.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = productoDAO.eliminarProveedor(proveedorSeleccionado.getIdProveedor());
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Proveedor eliminado exitosamente",
                        "Eliminación exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarProveedores();
                    limpiarFormulario();
                    resetearBotones();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el proveedor. Puede que esté siendo usado por productos.",
                        "Error al eliminar",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar proveedor: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void habilitarCampos(boolean habilitar) {
        txtEmpresa.setEnabled(habilitar);
        txtContacto.setEnabled(habilitar);
        txtRuc.setEnabled(habilitar);
        txtTelefono.setEnabled(habilitar);
        txtDireccion.setEnabled(habilitar);
    }
    
    private void limpiarFormulario() {
        txtEmpresa.setText("");
        txtContacto.setText("");
        txtRuc.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        habilitarCampos(false);
        proveedorSeleccionado = null;
    }
    
    private void resetearBotones() {
        modoEdicion = false;
        btnNuevo.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(false);
        listaProveedores.clearSelection();
    }
}