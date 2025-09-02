/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.modelo.Departamento;
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
public class DialogoDepartamentos extends JDialog {
    
    private ProductoDAO productoDAO;
    
    // Componentes de la interfaz
    private JList<Departamento> listaDepartamentos;
    private DefaultListModel<Departamento> modeloLista;
    private JTextField txtNombreDepartamento;
    private JComboBox<Almacen> cbAlmacen;
    private JButton btnNuevo;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnGuardar;
    
    // Variable para controlar el modo (nuevo/editar)
    private boolean modoEdicion = false;
    private Departamento departamentoSeleccionado = null;
    
    public DialogoDepartamentos(Component parent) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null, 
              "Departamentos", ModalityType.APPLICATION_MODAL);
        
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarAlmacenes();
        cargarDepartamentos();
        configurarEventos();
    }
    
    // Constructor alternativo sin parámetros
    public DialogoDepartamentos() {
        super((Frame) null, "Departamentos", true);
        
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarAlmacenes();
        cargarDepartamentos();
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
        
        JLabel lblTitulo = new JLabel("← Departamentos");
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
        
        // Panel izquierdo - Lista de departamentos
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
        panel.setBorder(BorderFactory.createTitledBorder("DEPARTAMENTO"));
        
        // Lista de departamentos
        modeloLista = new DefaultListModel<>();
        listaDepartamentos = new JList<>(modeloLista);
        listaDepartamentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaDepartamentos.setBackground(new Color(0, 180, 216)); // Color azul claro
        listaDepartamentos.setForeground(Color.WHITE);
        listaDepartamentos.setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(listaDepartamentos);
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
        
        // Campo Puesto/Almacén/Giro
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblAlmacen = new JLabel("Puesto/Almacén/Giro:");
        lblAlmacen.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblAlmacen, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbAlmacen = new JComboBox<>();
        cbAlmacen.setPreferredSize(new Dimension(300, 30));
        cbAlmacen.setEnabled(false);
        panel.add(cbAlmacen, gbc);
        
        // Campo Nombre del Departamento/Vitrina/Estante
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel lblNombre = new JLabel("Nombre del Departamento/Vitrina/Estante:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombreDepartamento = new JTextField(25);
        txtNombreDepartamento.setPreferredSize(new Dimension(300, 30));
        txtNombreDepartamento.setEnabled(false);
        panel.add(txtNombreDepartamento, gbc);
        
        // Panel de botones
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones, gbc);
        
        // Espacio en blanco
        gbc.gridx = 0; gbc.gridy = 5; gbc.weighty = 1.0;
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
        setSize(650, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void configurarEventos() {
        // Evento de selección en la lista
        listaDepartamentos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Departamento seleccionado = listaDepartamentos.getSelectedValue();
                if (seleccionado != null) {
                    departamentoSeleccionado = seleccionado;
                    txtNombreDepartamento.setText(seleccionado.getNombre());
                    
                    // Seleccionar el almacén correspondiente
                    for (int i = 0; i < cbAlmacen.getItemCount(); i++) {
                        Almacen almacen = cbAlmacen.getItemAt(i);
                        if (almacen.getIdAlmacen() == seleccionado.getIdAlmacen()) {
                            cbAlmacen.setSelectedIndex(i);
                            break;
                        }
                    }
                    
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
            departamentoSeleccionado = null;
            limpiarFormulario();
            habilitarCampos(true);
            btnGuardar.setEnabled(true);
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
            listaDepartamentos.clearSelection();
            cbAlmacen.requestFocus();
        });
        
        // Evento botón Modificar
        btnModificar.addActionListener(e -> {
            if (departamentoSeleccionado != null) {
                modoEdicion = true;
                habilitarCampos(true);
                btnGuardar.setEnabled(true);
                btnNuevo.setEnabled(false);
                btnEliminar.setEnabled(false);
                cbAlmacen.requestFocus();
            }
        });
        
        // Evento botón Eliminar
        btnEliminar.addActionListener(this::eliminarDepartamento);
        
        // Evento botón Guardar
        btnGuardar.addActionListener(this::guardarDepartamento);
    }
    
    private void cargarAlmacenes() {
        try {
            List<Almacen> almacenes = productoDAO.obtenerAlmacenes();
            cbAlmacen.removeAllItems();
            cbAlmacen.addItem(new Almacen(0, "-- Seleccionar Puesto/Almacén --"));
            
            for (Almacen almacen : almacenes) {
                cbAlmacen.addItem(almacen);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar almacenes: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarDepartamentos() {
        try {
            List<Departamento> departamentos = productoDAO.obtenerDepartamentos();
            modeloLista.clear();
            
            for (Departamento departamento : departamentos) {
                modeloLista.addElement(departamento);
            }
            
            System.out.println("✅ Cargados " + departamentos.size() + " departamentos");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar departamentos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void guardarDepartamento(ActionEvent e) {
        String nombre = txtNombreDepartamento.getText().trim();
        Almacen almacenSeleccionado = (Almacen) cbAlmacen.getSelectedItem();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre del departamento es obligatorio",
                "Campo requerido",
                JOptionPane.WARNING_MESSAGE);
            txtNombreDepartamento.requestFocus();
            return;
        }
        
        if (almacenSeleccionado == null || almacenSeleccionado.getIdAlmacen() == 0) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un Puesto/Almacén/Giro",
                "Campo requerido",
                JOptionPane.WARNING_MESSAGE);
            cbAlmacen.requestFocus();
            return;
        }
        
        try {
            boolean exito = false;
            
            if (modoEdicion && departamentoSeleccionado != null) {
                // Modificar departamento existente
                departamentoSeleccionado.setNombre(nombre);
                departamentoSeleccionado.setIdAlmacen(almacenSeleccionado.getIdAlmacen());
                exito = productoDAO.actualizarDepartamento(departamentoSeleccionado);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Departamento modificado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Nuevo departamento
                Departamento nuevoDepartamento = new Departamento();
                nuevoDepartamento.setNombre(nombre);
                nuevoDepartamento.setDescripcion(nombre);
                nuevoDepartamento.setIdAlmacen(almacenSeleccionado.getIdAlmacen());
                nuevoDepartamento.setEstado(true);
                
                exito = productoDAO.guardarDepartamento(nuevoDepartamento);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Departamento creado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (exito) {
                cargarDepartamentos();
                limpiarFormulario();
                resetearBotones();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar departamento: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void eliminarDepartamento(ActionEvent e) {
        if (departamentoSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un departamento para eliminar",
                "Ningún departamento seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el departamento '" + departamentoSeleccionado.getNombre() + "'?\n\n" +
            "Esta acción no se puede deshacer y puede afectar a los productos que usan este departamento.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = productoDAO.eliminarDepartamento(departamentoSeleccionado.getIdDepartamento());
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Departamento eliminado exitosamente",
                        "Eliminación exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarDepartamentos();
                    limpiarFormulario();
                    resetearBotones();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el departamento. Puede que esté siendo usado por productos.",
                        "Error al eliminar",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar departamento: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void habilitarCampos(boolean habilitar) {
        cbAlmacen.setEnabled(habilitar);
        txtNombreDepartamento.setEnabled(habilitar);
    }
    
    private void limpiarFormulario() {
        cbAlmacen.setSelectedIndex(0);
        txtNombreDepartamento.setText("");
        habilitarCampos(false);
        departamentoSeleccionado = null;
    }
    
    private void resetearBotones() {
        modoEdicion = false;
        btnNuevo.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(false);
        listaDepartamentos.clearSelection();
    }
}