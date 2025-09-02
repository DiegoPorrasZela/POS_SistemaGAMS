/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.vista;

import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.modelo.Categoria;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Diego
 */
public class DialogoCategorias extends JDialog {
    
    private ProductoDAO productoDAO;
    
    // Componentes de la interfaz
    private JList<Categoria> listaCategorias;
    private DefaultListModel<Categoria> modeloLista;
    private JTextField txtNombreCategoria;
    private JButton btnNuevo;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnGuardar;
    
    // Variable para controlar el modo (nuevo/editar)
    private boolean modoEdicion = false;
    private Categoria categoriaSeleccionada = null;
    
    public DialogoCategorias(Component parent) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null, 
              "Categorías", ModalityType.APPLICATION_MODAL);
        
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarCategorias();
        configurarEventos();
    }
    
    // Constructor alternativo sin parámetros
    public DialogoCategorias() {
        super((Frame) null, "Categorías", true);
        
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarCategorias();
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
        
        JLabel lblTitulo = new JLabel("← Categorías");
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
        
        // Panel izquierdo - Lista de categorías
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
        panel.setPreferredSize(new Dimension(200, 400));
        panel.setBorder(BorderFactory.createTitledBorder("CATEGORÍA"));
        
        // Lista de categorías
        modeloLista = new DefaultListModel<>();
        listaCategorias = new JList<>(modeloLista);
        listaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCategorias.setBackground(new Color(0, 180, 216)); // Color azul claro
        listaCategorias.setForeground(Color.WHITE);
        listaCategorias.setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(listaCategorias);
        scrollPane.setPreferredSize(new Dimension(180, 300));
        
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
        
        // Campo Nombre de la Categoría
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre de la Categoría:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombreCategoria = new JTextField(25);
        txtNombreCategoria.setPreferredSize(new Dimension(300, 30));
        txtNombreCategoria.setEnabled(false); // Inicialmente deshabilitado
        panel.add(txtNombreCategoria, gbc);
        
        // Panel de botones
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones, gbc);
        
        // Espacio en blanco
        gbc.gridx = 0; gbc.gridy = 3; gbc.weighty = 1.0;
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
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void configurarEventos() {
        // Evento de selección en la lista
        listaCategorias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Categoria seleccionada = listaCategorias.getSelectedValue();
                if (seleccionada != null) {
                    categoriaSeleccionada = seleccionada;
                    txtNombreCategoria.setText(seleccionada.getNombre());
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
            categoriaSeleccionada = null;
            txtNombreCategoria.setText("");
            txtNombreCategoria.setEnabled(true);
            btnGuardar.setEnabled(true);
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
            listaCategorias.clearSelection();
            txtNombreCategoria.requestFocus();
        });
        
        // Evento botón Modificar
        btnModificar.addActionListener(e -> {
            if (categoriaSeleccionada != null) {
                modoEdicion = true;
                txtNombreCategoria.setEnabled(true);
                btnGuardar.setEnabled(true);
                btnNuevo.setEnabled(false);
                btnEliminar.setEnabled(false);
                txtNombreCategoria.requestFocus();
            }
        });
        
        // Evento botón Eliminar
        btnEliminar.addActionListener(this::eliminarCategoria);
        
        // Evento botón Guardar
        btnGuardar.addActionListener(this::guardarCategoria);
    }
    
    private void cargarCategorias() {
        try {
            List<Categoria> categorias = productoDAO.obtenerCategorias();
            modeloLista.clear();
            
            for (Categoria categoria : categorias) {
                modeloLista.addElement(categoria);
            }
            
            System.out.println("✅ Cargadas " + categorias.size() + " categorías");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar categorías: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void guardarCategoria(ActionEvent e) {
        String nombre = txtNombreCategoria.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre de la categoría es obligatorio",
                "Campo requerido",
                JOptionPane.WARNING_MESSAGE);
            txtNombreCategoria.requestFocus();
            return;
        }
        
        try {
            boolean exito = false;
            
            if (modoEdicion && categoriaSeleccionada != null) {
                // Modificar categoría existente
                categoriaSeleccionada.setNombre(nombre);
                exito = productoDAO.actualizarCategoria(categoriaSeleccionada);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Categoría modificada exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Nueva categoría
                Categoria nuevaCategoria = new Categoria();
                nuevaCategoria.setNombre(nombre);
                nuevaCategoria.setDescripcion(nombre); // Usar el mismo nombre como descripción
                nuevaCategoria.setEstado(true);
                
                exito = productoDAO.guardarCategoria(nuevaCategoria);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Categoría creada exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (exito) {
                cargarCategorias();
                limpiarFormulario();
                resetearBotones();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar categoría: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void eliminarCategoria(ActionEvent e) {
        if (categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar una categoría para eliminar",
                "Ninguna categoría seleccionada",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar la categoría '" + categoriaSeleccionada.getNombre() + "'?\n\n" +
            "Esta acción no se puede deshacer y puede afectar a los productos que usan esta categoría.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = productoDAO.eliminarCategoria(categoriaSeleccionada.getIdCategoria());
                
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Categoría eliminada exitosamente",
                        "Eliminación exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarCategorias();
                    limpiarFormulario();
                    resetearBotones();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar la categoría. Puede que esté siendo usada por productos.",
                        "Error al eliminar",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar categoría: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void limpiarFormulario() {
        txtNombreCategoria.setText("");
        txtNombreCategoria.setEnabled(false);
        categoriaSeleccionada = null;
    }
    
    private void resetearBotones() {
        modoEdicion = false;
        btnNuevo.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(false);
        listaCategorias.clearSelection();
    }
}