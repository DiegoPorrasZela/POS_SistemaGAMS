package com.pos.sistemagams.vista;

import com.pos.sistemagams.dao.ProductoDAO;
import com.pos.sistemagams.modelo.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.List;

/**
 * Diálogo para modificar productos existentes
 */
public class DialogoModificarProducto extends JDialog {
    
    // DAO
    private ProductoDAO productoDAO;
    
    // Producto a modificar
    private Producto productoOriginal;
    
    // Componentes principales
    private JTabbedPane tabbedPane;
    private JTextField txtCodigo;
    private JTextArea txtNombre;
    
    // Pestaña General - ACTUALIZADA
    private JComboBox<Categoria> cbCategoria;
    private JComboBox<Almacen> cbAlmacenGeneral;      // NUEVO: Moved to General
    private JComboBox<Departamento> cbDepartamento;
    private JComboBox<Proveedor> cbProveedor;
    
    // Pestaña Precios
    private JCheckBox chkAplicaIGV;
    private JTextField txtPorcentajeIGV;
    private JTextField txtCostoCompra;
    private JTextField txtPrecio1;
    private JTextField txtPrecio2;
    private JTextField txtPrecio3;
    private JTextField txtPrecioMayoreo;
    private JTextField txtCantidadMayoreo;
    private JComboBox<String> cbUnidadCompra;
    
    // Pestaña Existencias - ACTUALIZADA (sin almacén)
    private JTextField txtStockMinimo;
    private JTextField txtStockMaximo;
    
    // Pestaña Imagen
    private JLabel lblImagenPreview;
    private JButton btnCargarImagen;
    private String rutaImagenSeleccionada;
    
    // Pestaña Código de Barras
    private JLabel lblCodigoBarras;
    
    // Botones
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    // Variable para indicar si se guardó
    private boolean guardado = false;
    
    public DialogoModificarProducto(Component parent, Producto producto) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null, 
              "Modificar Producto", ModalityType.APPLICATION_MODAL);
        this.productoDAO = new ProductoDAO();
        this.productoOriginal = producto;
        initComponents();
        configurarDialog();
        cargarDatos();
        cargarDatosProducto();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal con pestañas
        tabbedPane = new JTabbedPane();
        
        // Crear pestañas
        crearPanelDatosBasicos();
        crearPestanaGeneral();
        crearPestanaPrecios();
        crearPestanaExistencias();
        crearPestanaImagen();
        crearPestanaCodigoBarras();
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        
        add(tabbedPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void crearPanelDatosBasicos() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Código
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Código:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCodigo = new JTextField(15);
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(Color.LIGHT_GRAY);
        panel.add(txtCodigo, gbc);
        
        // Nombre del producto/Descripción
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Nombre del Producto/Descripción:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtNombre = new JTextArea(6, 30);
        txtNombre.setLineWrap(true);
        txtNombre.setWrapStyleWord(true);
        JScrollPane scrollNombre = new JScrollPane(txtNombre);
        panel.add(scrollNombre, gbc);
        
        tabbedPane.addTab("Datos Básicos", panel);
    }
    
    private void crearPestanaGeneral() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Categoría
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Categoría:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbCategoria = new JComboBox<>();
        panel.add(cbCategoria, gbc);
        
        // NUEVO: Puesto/Almacén/Giro (movido desde Existencias)
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Puesto/Almacén/Giro:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbAlmacenGeneral = new JComboBox<>();
        panel.add(cbAlmacenGeneral, gbc);
        
        // Departamento/Vitrina/Estante (MODIFICADO - ahora se filtra por almacén)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Departamento/Vitrina/Estante:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbDepartamento = new JComboBox<>();
        panel.add(cbDepartamento, gbc);
        
        // Proveedor
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Proveedor:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbProveedor = new JComboBox<>();
        panel.add(cbProveedor, gbc);
        
        // Espacio en blanco
        gbc.gridx = 0; gbc.gridy = 4; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        
        tabbedPane.addTab("General", panel);
    }
    
    private void crearPestanaPrecios() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Impuesto
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        chkAplicaIGV = new JCheckBox("Aplica IGV");
        panel.add(chkAplicaIGV, gbc);
        
        gbc.gridx = 1;
        panel.add(new JLabel("%:"), gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        txtPorcentajeIGV = new JTextField(8);
        panel.add(txtPorcentajeIGV, gbc);
        
        // Costo Compra
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Costo Compra:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCostoCompra = new JTextField();
        panel.add(txtCostoCompra, gbc);
        
        // Precio 1
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Precio 1:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrecio1 = new JTextField();
        panel.add(txtPrecio1, gbc);
        
        // Precio 2
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Precio 2:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrecio2 = new JTextField();
        panel.add(txtPrecio2, gbc);
        
        // Precio 3
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Precio 3:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrecio3 = new JTextField();
        panel.add(txtPrecio3, gbc);
        
        // Precio Mayoreo
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Precio Mayoreo:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrecioMayoreo = new JTextField();
        panel.add(txtPrecioMayoreo, gbc);
        
        // Cantidad Mayoreo
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Cant. Mayoreo:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCantidadMayoreo = new JTextField();
        panel.add(txtCantidadMayoreo, gbc);
        
        // Unidad de Compra
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Unidad Compra:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbUnidadCompra = new JComboBox<>(new String[]{"UND", "KG", "LT", "MT", "PZA"});
        panel.add(cbUnidadCompra, gbc);
        
        tabbedPane.addTab("Precios", panel);
    }
    
    private void crearPestanaExistencias() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Stock Mínimo
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Existencia Mínima:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtStockMinimo = new JTextField();
        panel.add(txtStockMinimo, gbc);
        
        // Stock Máximo
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Existencia Máxima:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtStockMaximo = new JTextField();
        panel.add(txtStockMaximo, gbc);
        
        // REMOVIDO: Puesto/Almacén/Giro (ahora está en General)
        
        // Espacio
        gbc.gridx = 0; gbc.gridy = 2; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        
        tabbedPane.addTab("Existencias", panel);
    }
    
    private void crearPestanaImagen() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior con botón
        JPanel panelSuperior = new JPanel(new FlowLayout());
        btnCargarImagen = new JButton("Cargar Imagen");
        btnCargarImagen.addActionListener(this::cargarImagen);
        panelSuperior.add(btnCargarImagen);
        
        // Panel central para preview de imagen
        lblImagenPreview = new JLabel();
        lblImagenPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagenPreview.setVerticalAlignment(SwingConstants.CENTER);
        lblImagenPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        lblImagenPreview.setPreferredSize(new Dimension(300, 300));
        lblImagenPreview.setText("Sin imagen seleccionada");
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(lblImagenPreview, BorderLayout.CENTER);
        
        tabbedPane.addTab("Imagen", panel);
    }
    
    private void crearPestanaCodigoBarras() {
        JPanel panel = new JPanel(new BorderLayout());
        
        lblCodigoBarras = new JLabel();
        lblCodigoBarras.setHorizontalAlignment(SwingConstants.CENTER);
        lblCodigoBarras.setVerticalAlignment(SwingConstants.CENTER);
        lblCodigoBarras.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        lblCodigoBarras.setPreferredSize(new Dimension(400, 150));
        lblCodigoBarras.setFont(new Font("Courier New", Font.BOLD, 16));
        
        panel.add(lblCodigoBarras, BorderLayout.CENTER);
        
        tabbedPane.addTab("Código de Barras", panel);
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.addActionListener(this::guardarCambios);
        
        panel.add(btnCancelar);
        panel.add(btnGuardar);
        
        return panel;
    }
    
    private void configurarDialog() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void cargarDatos() {
        // Cargar categorías
        List<Categoria> categorias = productoDAO.obtenerCategorias();
        cbCategoria.removeAllItems();
        cbCategoria.addItem(new Categoria(0, "-- Seleccionar --"));
        for (Categoria categoria : categorias) {
            cbCategoria.addItem(categoria);
        }
        
        // NUEVO: Cargar almacenes en General
        List<Almacen> almacenes = productoDAO.obtenerAlmacenes();
        cbAlmacenGeneral.removeAllItems();
        cbAlmacenGeneral.addItem(new Almacen(0, "-- Seleccionar Puesto/Almacén --"));
        for (Almacen almacen : almacenes) {
            cbAlmacenGeneral.addItem(almacen);
        }
        
        // Configurar evento para filtrar departamentos
        cbAlmacenGeneral.addActionListener(e -> filtrarDepartamentosPorAlmacen());
        
        // Cargar proveedores
        List<Proveedor> proveedores = productoDAO.obtenerProveedores();
        cbProveedor.removeAllItems();
        cbProveedor.addItem(new Proveedor(0, "-- Seleccionar --"));
        for (Proveedor proveedor : proveedores) {
            cbProveedor.addItem(proveedor);
        }
        
        // Departamentos inicialmente vacío (se llenan al seleccionar almacén)
        cbDepartamento.removeAllItems();
        cbDepartamento.addItem(new Departamento(0, "-- Seleccionar Departamento --"));
    }
    
    /**
     * NUEVO: Método para filtrar departamentos por almacén seleccionado
     */
    private void filtrarDepartamentosPorAlmacen() {
        try {
            Almacen almacenSeleccionado = (Almacen) cbAlmacenGeneral.getSelectedItem();
            
            cbDepartamento.removeAllItems();
            cbDepartamento.addItem(new Departamento(0, "-- Seleccionar Departamento --"));
            
            if (almacenSeleccionado != null && almacenSeleccionado.getIdAlmacen() > 0) {
                List<Departamento> departamentos = productoDAO.obtenerDepartamentosPorAlmacen(almacenSeleccionado.getIdAlmacen());
                
                for (Departamento departamento : departamentos) {
                    cbDepartamento.addItem(departamento);
                }
                
                cbDepartamento.setEnabled(true);
                System.out.println("✅ Cargados " + departamentos.size() + " departamentos para el almacén: " + almacenSeleccionado.getNombre());
            } else {
                cbDepartamento.setEnabled(false);
            }
            
        } catch (Exception e) {
            System.err.println("Error al filtrar departamentos: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al cargar departamentos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDatosProducto() {
        if (productoOriginal == null) return;
        
        // Datos básicos
        txtCodigo.setText(productoOriginal.getCodigo());
        txtNombre.setText(productoOriginal.getNombre());
        
        // Precios
        txtCostoCompra.setText(productoOriginal.getPrecioCompra().toString());
        txtPrecio1.setText(productoOriginal.getPrecioVenta1().toString());
        txtPrecio2.setText(productoOriginal.getPrecioVenta2().toString());
        txtPrecio3.setText(productoOriginal.getPrecioVenta3().toString());
        txtPrecioMayoreo.setText(productoOriginal.getPrecioMayoreo().toString());
        txtCantidadMayoreo.setText(String.valueOf(productoOriginal.getCantidadMayoreo()));
        
        // IGV
        chkAplicaIGV.setSelected(productoOriginal.isAplicaIgv());
        txtPorcentajeIGV.setText(productoOriginal.getPorcentajeIgv().toString());
        
        // Stock
        txtStockMinimo.setText(String.valueOf(productoOriginal.getStockMinimo()));
        txtStockMaximo.setText(String.valueOf(productoOriginal.getStockMaximo()));
        
        // Unidad
        cbUnidadCompra.setSelectedItem(productoOriginal.getUnidadCompra());
        
        // Seleccionar categoría
        for (int i = 0; i < cbCategoria.getItemCount(); i++) {
            Categoria cat = cbCategoria.getItemAt(i);
            if (cat.getIdCategoria() == productoOriginal.getIdCategoria()) {
                cbCategoria.setSelectedIndex(i);
                break;
            }
        }
        
        // NUEVO: Seleccionar almacén en General (PRIMERO para activar filtrado)
        for (int i = 0; i < cbAlmacenGeneral.getItemCount(); i++) {
            Almacen alm = cbAlmacenGeneral.getItemAt(i);
            if (alm.getIdAlmacen() == productoOriginal.getIdAlmacen()) {
                cbAlmacenGeneral.setSelectedIndex(i);
                // Esto activará el filtrado automáticamente
                break;
            }
        }
        
        // Esperar un momento para que se carguen los departamentos filtrados
        SwingUtilities.invokeLater(() -> {
            // Seleccionar departamento (DESPUÉS de que se filtren)
            for (int i = 0; i < cbDepartamento.getItemCount(); i++) {
                Departamento dept = cbDepartamento.getItemAt(i);
                if (dept.getIdDepartamento() == productoOriginal.getIdDepartamento()) {
                    cbDepartamento.setSelectedIndex(i);
                    break;
                }
            }
        });
        
        // Seleccionar proveedor
        for (int i = 0; i < cbProveedor.getItemCount(); i++) {
            Proveedor prov = cbProveedor.getItemAt(i);
            if (prov.getIdProveedor() == productoOriginal.getIdProveedor()) {
                cbProveedor.setSelectedIndex(i);
                break;
            }
        }
        
        // Imagen
        rutaImagenSeleccionada = productoOriginal.getImagenPath();
        if (rutaImagenSeleccionada != null && !rutaImagenSeleccionada.isEmpty()) {
            File archivoImagen = new File(rutaImagenSeleccionada);
            if (archivoImagen.exists()) {
                mostrarPreviewImagen(archivoImagen);
            }
        }
        
        // Código de barras
        actualizarCodigoBarras(productoOriginal.getCodigo());
    }
    
    private void actualizarCodigoBarras(String codigo) {
        // Simulación de código de barras con texto
        StringBuilder barras = new StringBuilder();
        barras.append("<html><center>");
        barras.append("||||  ||  |||||  ||  ||||  |  ||||||  |||<br>");
        barras.append("||  ||||  ||  ||  ||||  ||  ||  ||||  |||<br>");
        barras.append(codigo).append("<br>");
        barras.append("</center></html>");
        
        lblCodigoBarras.setText(barras.toString());
    }
    
    private void cargarImagen(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Imagen del Producto");
        
        // Filtros de archivo
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Imágenes (*.jpg, *.jpeg, *.png, *.gif)", 
            "jpg", "jpeg", "png", "gif"
        );
        fileChooser.setFileFilter(filter);
        
        int resultado = fileChooser.showOpenDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            
            try {
                // Crear directorio de imágenes si no existe
                Path directorioImagenes = Paths.get("imagenes/productos");
                if (!Files.exists(directorioImagenes)) {
                    Files.createDirectories(directorioImagenes);
                }
                
                // Generar nombre único para la imagen
                String extension = obtenerExtension(archivoSeleccionado.getName());
                String nombreArchivo = "producto_" + System.currentTimeMillis() + "." + extension;
                Path destino = directorioImagenes.resolve(nombreArchivo);
                
                // Copiar archivo
                Files.copy(archivoSeleccionado.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
                
                // Guardar ruta relativa
                rutaImagenSeleccionada = "imagenes/productos/" + nombreArchivo;
                
                // Mostrar preview
                mostrarPreviewImagen(destino.toFile());
                
                JOptionPane.showMessageDialog(this, 
                    "Imagen cargada correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar la imagen: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void mostrarPreviewImagen(File archivo) {
        try {
            BufferedImage imagen = ImageIO.read(archivo);
            
            // Redimensionar imagen para el preview
            int ancho = 250;
            int alto = 250;
            
            Image imagenRedimensionada = imagen.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            ImageIcon icono = new ImageIcon(imagenRedimensionada);
            
            lblImagenPreview.setIcon(icono);
            lblImagenPreview.setText("");
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error al mostrar la imagen: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String obtenerExtension(String nombreArchivo) {
        int indice = nombreArchivo.lastIndexOf('.');
        if (indice > 0) {
            return nombreArchivo.substring(indice + 1).toLowerCase();
        }
        return "";
    }
    
    private void guardarCambios(ActionEvent e) {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }
        
        try {
            // Actualizar el producto original con los nuevos datos
            productoOriginal.setNombre(txtNombre.getText().trim());
            
            // Precios
            productoOriginal.setPrecioCompra(new BigDecimal(txtCostoCompra.getText().replace(",", ".")));
            productoOriginal.setPrecioVenta1(new BigDecimal(txtPrecio1.getText().replace(",", ".")));
            productoOriginal.setPrecioVenta2(new BigDecimal(txtPrecio2.getText().replace(",", ".")));
            productoOriginal.setPrecioVenta3(new BigDecimal(txtPrecio3.getText().replace(",", ".")));
            productoOriginal.setPrecioMayoreo(new BigDecimal(txtPrecioMayoreo.getText().replace(",", ".")));
            productoOriginal.setCantidadMayoreo(Integer.parseInt(txtCantidadMayoreo.getText()));
            
            // IGV
            productoOriginal.setAplicaIgv(chkAplicaIGV.isSelected());
            productoOriginal.setPorcentajeIgv(new BigDecimal(txtPorcentajeIGV.getText().replace(",", ".")));
            
            // Stock
            productoOriginal.setStockMinimo(Integer.parseInt(txtStockMinimo.getText()));
            productoOriginal.setStockMaximo(Integer.parseInt(txtStockMaximo.getText()));
            
            // Unidad
            productoOriginal.setUnidadCompra(cbUnidadCompra.getSelectedItem().toString());
            
            // Relaciones
            Categoria categoriaSeleccionada = (Categoria) cbCategoria.getSelectedItem();
            if (categoriaSeleccionada != null && categoriaSeleccionada.getIdCategoria() > 0) {
                productoOriginal.setIdCategoria(categoriaSeleccionada.getIdCategoria());
            } else {
                productoOriginal.setIdCategoria(0);
            }
            
            // MODIFICADO: Usar cbAlmacenGeneral en lugar de cbAlmacen
            Almacen almacenSeleccionado = (Almacen) cbAlmacenGeneral.getSelectedItem();
            if (almacenSeleccionado != null && almacenSeleccionado.getIdAlmacen() > 0) {
                productoOriginal.setIdAlmacen(almacenSeleccionado.getIdAlmacen());
            } else {
                productoOriginal.setIdAlmacen(0);
            }
            
            Departamento departamentoSeleccionado = (Departamento) cbDepartamento.getSelectedItem();
            if (departamentoSeleccionado != null && departamentoSeleccionado.getIdDepartamento() > 0) {
                productoOriginal.setIdDepartamento(departamentoSeleccionado.getIdDepartamento());
            } else {
                productoOriginal.setIdDepartamento(0);
            }
            
            Proveedor proveedorSeleccionado = (Proveedor) cbProveedor.getSelectedItem();
            if (proveedorSeleccionado != null && proveedorSeleccionado.getIdProveedor() > 0) {
                productoOriginal.setIdProveedor(proveedorSeleccionado.getIdProveedor());
            } else {
                productoOriginal.setIdProveedor(0);
            }
            
            // Imagen
            productoOriginal.setImagenPath(rutaImagenSeleccionada);
            
            // Actualizar en base de datos
            if (productoDAO.actualizarProducto(productoOriginal)) {
                guardado = true;
                JOptionPane.showMessageDialog(this,
                    "Producto actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar el producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Error en los datos numéricos. Verifique los precios y cantidades.",
                "Error de Formato",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error inesperado: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private boolean validarCampos() {
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre del producto es obligatorio",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            tabbedPane.setSelectedIndex(0);
            txtNombre.requestFocus();
            return false;
        }
        
        // Validar costo de compra
        try {
            BigDecimal costo = new BigDecimal(txtCostoCompra.getText().replace(",", "."));
            if (costo.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException("Valor negativo");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El costo de compra debe ser un número válido mayor o igual a 0",
                "Error de Formato",
                JOptionPane.WARNING_MESSAGE);
            tabbedPane.setSelectedIndex(2);
            txtCostoCompra.requestFocus();
            return false;
        }
        
        // Validar precio de venta 1
        try {
            BigDecimal precio = new BigDecimal(txtPrecio1.getText().replace(",", "."));
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Valor menor o igual a 0");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El precio de venta 1 debe ser un número válido mayor a 0",
                "Error de Formato",
                JOptionPane.WARNING_MESSAGE);
            tabbedPane.setSelectedIndex(2);
            txtPrecio1.requestFocus();
            return false;
        }
        
        return true;
    }
    
    // Getter para saber si se guardó el producto
    public boolean isGuardado() {
        return guardado;
    }
}