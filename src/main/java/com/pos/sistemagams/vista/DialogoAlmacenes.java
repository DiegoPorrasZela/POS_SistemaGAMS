/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.modelo.Almacen;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author Diego
 */
public class DialogoAlmacenes extends JDialog {
    
    private ProductoDAO productoDAO;
    
    // Componentes de la interfaz
    private JList<Almacen> listaAlmacenes;
    private DefaultListModel<Almacen> modeloLista;
    private JTextField txtNombreAlmacen;
    private JTextField txtResponsable;
    private JTextArea txtDireccion;
    private JButton btnNuevo;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnGuardar;
    
    // Variable para controlar el modo (nuevo/editar)
    private boolean modoEdicion = false;
    private Almacen almacenSeleccionado = null;
    
    public DialogoAlmacenes(Component parent) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null, 
              "Almacén / Giro", ModalityType.APPLICATION_MODAL);
        
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarAlmacenes();
        configurarEventos();
    }
    
    // Constructor alternativo sin parámetros
    public DialogoAlmacenes() {
        super((Frame) null, "Almacén / Giro", true);
        
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarAlmacenes();
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
        
        JLabel lblTitulo = new JLabel("← Almacén / Giro");
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
        
        // Panel izquierdo - Lista de almacenes
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
        panel.setPreferredSize(new Dimension(280, 400));
        panel.setBorder(BorderFactory.createTitledBorder("ALMACÉN / GIRO"));
        
        // Lista de almacenes
        modeloLista = new DefaultListModel<>();
        listaAlmacenes = new JList<>(modeloLista);
        listaAlmacenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaAlmacenes.setBackground(new Color(0, 180, 216)); // Color azul claro
        listaAlmacenes.setForeground(Color.WHITE);
        listaAlmacenes.setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(listaAlmacenes);
        scrollPane.setPreferredSize(new Dimension(260, 300));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout());
        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(0, 25));
        JButton btnBuscar = new JButton("🔍");
        btnBuscar.setPreferredSize(new Dimension(30, 25));
        
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(btnBuscar, BorderLayout.EAST);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelBusqueda, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campo Nombre del Puesto/Almacén/Giro
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre del Puesto/Almacén/Giro:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombreAlmacen = new JTextField(25);
        txtNombreAlmacen.setPreferredSize(new Dimension(300, 30));
        txtNombreAlmacen.setEnabled(false);
        panel.add(txtNombreAlmacen, gbc);
        
        // Campo Responsable
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblResponsable = new JLabel("Responsable:");
        lblResponsable.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblResponsable, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtResponsable = new JTextField(25);
        txtResponsable.setPreferredSize(new Dimension(300, 30));
        txtResponsable.setEnabled(false);
        panel.add(txtResponsable, gbc);
        
        // Campo Dirección
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblDireccion = new JLabel("Dirección:");
        lblDireccion.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblDireccion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        txtDireccion = new JTextArea(4, 25);
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        txtDireccion.setEnabled(false);
        JScrollPane scrollDireccion = new JScrollPane(txtDireccion);
        scrollDireccion.setPreferredSize(new Dimension(300, 80));
        panel.add(scrollDireccion, gbc);
        
        // Panel de botones
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones, gbc);
        
        // Espacio en blanco
        gbc.gridx = 0; gbc.gridy = 7; gbc.weighty = 1.0;
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
        setSize(700, 550);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void configurarEventos() {
        // Evento de selección en la lista
        listaAlmacenes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Almacen seleccionado = listaAlmacenes.getSelectedValue();
                if (seleccionado != null) {
                    almacenSeleccionado = seleccionado;
                    txtNombreAlmacen.setText(seleccionado.getNombre());
                    txtResponsable.setText(seleccionado.getResponsable() != null ? seleccionado.getResponsable() : "");
                    txtDireccion.setText(seleccionado.getDireccion() != null ? seleccionado.getDireccion() : "");
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
            almacenSeleccionado = null;
            limpiarFormulario();
            habilitarCampos(true);
            btnGuardar.setEnabled(true);
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
            listaAlmacenes.clearSelection();
            txtNombreAlmacen.requestFocus();
        });
        
        // Evento botón Modificar
        btnModificar.addActionListener(e -> {
            if (almacenSeleccionado != null) {
                modoEdicion = true;
                habilitarCampos(true);
                btnGuardar.setEnabled(true);
                btnNuevo.setEnabled(false);
                btnEliminar.setEnabled(false);
                txtNombreAlmacen.requestFocus();
            }
        });
        
        // Evento botón Eliminar
        btnEliminar.addActionListener(this::eliminarAlmacen);
        
        // Evento botón Guardar
        btnGuardar.addActionListener(this::guardarAlmacen);
    }
    
    private void cargarAlmacenes() {
        try {
            List<Almacen> almacenes = productoDAO.obtenerAlmacenes();
            modeloLista.clear();
            
            for (Almacen almacen : almacenes) {
                modeloLista.addElement(almacen);
            }
            
            System.out.println("✅ Cargados " + almacenes.size() + " almacenes");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar almacenes: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void guardarAlmacen(ActionEvent e) {
        String nombre = txtNombreAlmacen.getText().trim();
        String responsable = txtResponsable.getText().trim();
        String direccion = txtDireccion.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre del almacén es obligatorio",
                "Campo requerido",
                JOptionPane.WARNING_MESSAGE);
            txtNombreAlmacen.requestFocus();
            return;
        }
        
        try {
            boolean exito = false;
            
            if (modoEdicion && almacenSeleccionado != null) {
                // Modificar almacén existente
                almacenSeleccionado.setNombre(nombre);
                almacenSeleccionado.setResponsable(responsable);
                almacenSeleccionado.setDireccion(direccion);
                exito = productoDAO.actualizarAlmacen(almacenSeleccionado);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Almacén modificado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Nuevo almacén
                Almacen nuevoAlmacen = new Almacen();
                nuevoAlmacen.setNombre(nombre);
                nuevoAlmacen.setDescripcion(nombre);
                nuevoAlmacen.setResponsable(responsable);
                nuevoAlmacen.setDireccion(direccion);
                nuevoAlmacen.setEstado(true);
                
                exito = productoDAO.guardarAlmacen(nuevoAlmacen);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Almacén creado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (exito) {
                cargarAlmacenes();
                limpiarFormulario();
                resetearBotones();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar almacén: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void eliminarAlmacen(ActionEvent e) {
        if (almacenSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un almacén para eliminar",
                "Ningún almacén seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el almacén '" + almacenSeleccionado.getNombre() + "'?\n\n" +
            "Esta acción no se puede deshacer y puede afectar a los productos que usan este almacén.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = productoDAO.eliminarAlmacen(almacenSeleccionado.getIdAlmacen());
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Almacén eliminado exitosamente",
                        "Eliminación exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarAlmacenes();
                    limpiarFormulario();
                    resetearBotones();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el almacén. Puede que esté siendo usado por productos.",
                        "Error al eliminar",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar almacén: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void habilitarCampos(boolean habilitar) {
        txtNombreAlmacen.setEnabled(habilitar);
        txtResponsable.setEnabled(habilitar);
        txtDireccion.setEnabled(habilitar);
    }
    
    private void limpiarFormulario() {
        txtNombreAlmacen.setText("");
        txtResponsable.setText("");
        txtDireccion.setText("");
        habilitarCampos(false);
        almacenSeleccionado = null;
    }
    
    private void resetearBotones() {
        modoEdicion = false;
        btnNuevo.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(false);
        listaAlmacenes.clearSelection();
    }
}