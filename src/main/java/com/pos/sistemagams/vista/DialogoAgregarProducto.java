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
 * Diálogo para agregar nuevos productos
 */
public class DialogoAgregarProducto extends JDialog {

    // DAO
    private ProductoDAO productoDAO;

    // Componentes principales
    private JTabbedPane tabbedPane;
    private JTextField txtCodigo;
    private JTextArea txtNombre; // Cambiado a TextArea para nombre/descripción

    // Pestaña General
    private JComboBox<Categoria> cbCategoria;
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

    // Pestaña Existencias
    private JTextField txtStockMinimo;
    private JTextField txtStockMaximo;
    private JComboBox<Almacen> cbAlmacen;

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

    private JComboBox<Almacen> cbAlmacenGeneral;  // Nueva variable para General

    public DialogoAgregarProducto(Component parent) {
        super(parent != null ? (Window) SwingUtilities.getWindowAncestor(parent) : null,
                "Agregar Producto", ModalityType.APPLICATION_MODAL);
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarDatos();
        generarCodigoAutomatico();
    }

    // Constructor alternativo para cuando no tengas referencia al parent
    public DialogoAgregarProducto() {
        super((Frame) null, "Agregar Producto", true);
        this.productoDAO = new ProductoDAO();
        initComponents();
        configurarDialog();
        cargarDatos();
        generarCodigoAutomatico();
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Código:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtCodigo = new JTextField(15);
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(Color.LIGHT_GRAY);
        panel.add(txtCodigo, gbc);

        // Nombre del producto/Descripción
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Nombre del Producto/Descripción:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Categoría:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cbCategoria = new JComboBox<>();
        panel.add(cbCategoria, gbc);

        // NUEVO: Puesto/Almacén/Giro
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Puesto/Almacén/Giro:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cbAlmacenGeneral = new JComboBox<>();  // Usamos nueva variable
        panel.add(cbAlmacenGeneral, gbc);

        // Departamento/Vitrina/Estante (MODIFICADO)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Departamento/Vitrina/Estante:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cbDepartamento = new JComboBox<>();
        cbDepartamento.setEnabled(false); // Deshabilitado hasta seleccionar almacén
        panel.add(cbDepartamento, gbc);

        // Proveedor
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Proveedor:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cbProveedor = new JComboBox<>();
        panel.add(cbProveedor, gbc);

        // Espacio en blanco
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);

        tabbedPane.addTab("General", panel);
    }

    private void crearPestanaPrecios() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);

        // Impuesto
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        chkAplicaIGV = new JCheckBox("Aplica IGV");
        chkAplicaIGV.setSelected(true);
        panel.add(chkAplicaIGV, gbc);

        gbc.gridx = 1;
        panel.add(new JLabel("%:"), gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        txtPorcentajeIGV = new JTextField("18.00", 8);
        panel.add(txtPorcentajeIGV, gbc);

        // Costo Compra
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Costo Compra:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtCostoCompra = new JTextField("0.00");
        panel.add(txtCostoCompra, gbc);

        // Precio 1
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Precio 1:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPrecio1 = new JTextField("0.00");
        panel.add(txtPrecio1, gbc);

        // Precio 2
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Precio 2:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPrecio2 = new JTextField("0.00");
        panel.add(txtPrecio2, gbc);

        // Precio 3
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Precio 3:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPrecio3 = new JTextField("0.00");
        panel.add(txtPrecio3, gbc);

        // Precio Mayoreo
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Precio Mayoreo:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPrecioMayoreo = new JTextField("0.00");
        panel.add(txtPrecioMayoreo, gbc);

        // Cantidad Mayoreo
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Cant. Mayoreo:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtCantidadMayoreo = new JTextField("0");
        panel.add(txtCantidadMayoreo, gbc);

        // Unidad de Compra
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Unidad Compra:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
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
    txtStockMinimo = new JTextField("5");
    panel.add(txtStockMinimo, gbc);
    
    // Stock Máximo
    gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
    panel.add(new JLabel("Existencia Máxima:"), gbc);
    
    gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
    txtStockMaximo = new JTextField("100");
    panel.add(txtStockMaximo, gbc);
    
    // REMOVIDO: cbAlmacen ya no va aquí
    
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
        btnCancelar.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro que desea cancelar?\nSe perderán todos los datos ingresados.",
                    "Confirmar Cancelación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(this::guardarProducto);

        panel.add(btnCancelar);
        panel.add(btnGuardar);

        return panel;
    }

    private void configurarDialog() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Confirmar antes de cerrar
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirmacion = JOptionPane.showConfirmDialog(
                        DialogoAgregarProducto.this,
                        "¿Está seguro que desea salir?\nSe perderán todos los datos ingresados.",
                        "Confirmar Salida",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmacion == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
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
    
    // REMOVIDO: No cargar cbAlmacen aquí ya que ya no existe
}
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

    private void generarCodigoAutomatico() {
        String codigo = productoDAO.generarCodigoUnico();
        txtCodigo.setText(codigo);
        actualizarCodigoBarras(codigo);
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

    private void guardarProducto(ActionEvent e) {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        try {
            // Crear objeto producto
            Producto producto = new Producto();

            // Datos básicos
            producto.setCodigo(txtCodigo.getText().trim());
            producto.setNombre(txtNombre.getText().trim());

            // Precios
            producto.setPrecioCompra(new BigDecimal(txtCostoCompra.getText().replace(",", ".")));
            producto.setPrecioVenta1(new BigDecimal(txtPrecio1.getText().replace(",", ".")));
            producto.setPrecioVenta2(new BigDecimal(txtPrecio2.getText().replace(",", ".")));
            producto.setPrecioVenta3(new BigDecimal(txtPrecio3.getText().replace(",", ".")));
            producto.setPrecioMayoreo(new BigDecimal(txtPrecioMayoreo.getText().replace(",", ".")));
            producto.setCantidadMayoreo(Integer.parseInt(txtCantidadMayoreo.getText()));

            // IGV
            producto.setAplicaIgv(chkAplicaIGV.isSelected());
            producto.setPorcentajeIgv(new BigDecimal(txtPorcentajeIGV.getText().replace(",", ".")));

            // Stock
            producto.setStockMinimo(Integer.parseInt(txtStockMinimo.getText()));
            producto.setStockMaximo(Integer.parseInt(txtStockMaximo.getText()));

            // Unidad
            producto.setUnidadCompra(cbUnidadCompra.getSelectedItem().toString());

            // Relaciones
            Categoria categoriaSeleccionada = (Categoria) cbCategoria.getSelectedItem();
            if (categoriaSeleccionada != null && categoriaSeleccionada.getIdCategoria() > 0) {
                producto.setIdCategoria(categoriaSeleccionada.getIdCategoria());
            }

            Proveedor proveedorSeleccionado = (Proveedor) cbProveedor.getSelectedItem();
            if (proveedorSeleccionado != null && proveedorSeleccionado.getIdProveedor() > 0) {
                producto.setIdProveedor(proveedorSeleccionado.getIdProveedor());
            }

            Departamento departamentoSeleccionado = (Departamento) cbDepartamento.getSelectedItem();
            if (departamentoSeleccionado != null && departamentoSeleccionado.getIdDepartamento() > 0) {
                producto.setIdDepartamento(departamentoSeleccionado.getIdDepartamento());
            }

            Almacen almacenSeleccionado = (Almacen) cbAlmacenGeneral.getSelectedItem();
            if (almacenSeleccionado != null && almacenSeleccionado.getIdAlmacen() > 0) {
                producto.setIdAlmacen(almacenSeleccionado.getIdAlmacen());
            }

            // Imagen
            producto.setImagenPath(rutaImagenSeleccionada);

            // Guardar en base de datos
            if (productoDAO.guardarProducto(producto)) {
                guardado = true;
                JOptionPane.showMessageDialog(this,
                        "Producto guardado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar el producto",
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
            tabbedPane.setSelectedIndex(0); // Ir a pestaña de datos básicos
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
            tabbedPane.setSelectedIndex(2); // Ir a pestaña de precios
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
            tabbedPane.setSelectedIndex(2); // Ir a pestaña de precios
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
