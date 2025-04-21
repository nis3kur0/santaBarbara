package com.mycompany;

import com.mycompany.ConexionBD;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BonificacionesDialog extends JDialog {
    private JTabbedPane tabbedPane;
    private JPanel panelIndividual;
    private JPanel panelGeneral;
    private JPanel panelBonificacionesActivas;
    private JPanel panelHistorial;
    
    private JComboBox<String> comboEmpleados;
    private JComboBox<String> comboTipoBonificacion;
    private JTextField txtMonto;
    private JTextArea txtDescripcion;
    private JDateChooser dateChooserMes;
    
    private JComboBox<String> comboTipoBonificacionGeneral;
    private JTextField txtMontoGeneral;
    private JTextArea txtDescripcionGeneral;
    private JDateChooser dateChooserMesGeneral;
    
    private JTable tablaBonificacionesActivas;
    private JTable tablaHistorial;
    private JButton btnEliminar;
    private JButton btnActualizar;
    
    private JButton btnAplicar;
    private JButton btnCancelar;
    
    public static String[] opcionesBonificacion = {"Bono por productividad", "Bono por fiestas", "Bono por maternidad", "Otros"};

    public BonificacionesDialog(Frame parent) {
        super(parent, "Gestión de Bonificaciones", true);
        initComponents();
        setupLayout();
        configurarBotonAplicar();
        cargarBonificacionesDelMes();
        cargarHistorialBonificaciones();
        pack();
        setLocationRelativeTo(parent);
        setSize(900, 600);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        panelIndividual = new JPanel();
        panelIndividual.setBorder(new TitledBorder("Bonificación Individual"));
        
        comboEmpleados = new JComboBox<>();
        cargarEmpleadosEnComboBox();
        
        comboTipoBonificacion = new JComboBox<>(opcionesBonificacion);
        txtMonto = new JTextField(10);
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        dateChooserMes = new JDateChooser(new Date());
        dateChooserMes.setDateFormatString("MM/yyyy");
        dateChooserMes.getDateEditor().setEnabled(false);
        
        panelGeneral = new JPanel();
        panelGeneral.setBorder(new TitledBorder("Bonificación General"));
        
        comboTipoBonificacionGeneral = new JComboBox<>(new String[]{"Bono por productividad", "Bono por fiestas", "Otros"});
        txtMontoGeneral = new JTextField(10);
        txtDescripcionGeneral = new JTextArea(3, 20);
        txtDescripcionGeneral.setLineWrap(true);
        dateChooserMesGeneral = new JDateChooser(new Date());
        dateChooserMesGeneral.setDateFormatString("MM/yyyy");
        dateChooserMesGeneral.getDateEditor().setEnabled(false);
        
        panelBonificacionesActivas = new JPanel(new BorderLayout());
        tablaBonificacionesActivas = new JTable();
        JScrollPane scrollPaneActivas = new JScrollPane(tablaBonificacionesActivas);
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarBonificacionesDelMes());
        
        btnEliminar = new JButton("Eliminar Seleccionada");
        btnEliminar.addActionListener(e -> eliminarBonificacionSeleccionada());
        
        JPanel panelBotonesActivas = new JPanel();
        panelBotonesActivas.add(btnActualizar);
        panelBotonesActivas.add(btnEliminar);
        
        panelBonificacionesActivas.add(scrollPaneActivas, BorderLayout.CENTER);
        panelBonificacionesActivas.add(panelBotonesActivas, BorderLayout.SOUTH);
        
        panelHistorial = new JPanel(new BorderLayout());
        tablaHistorial = new JTable();
        JScrollPane scrollPaneHistorial = new JScrollPane(tablaHistorial);
        
        JButton btnActualizarHistorial = new JButton("Actualizar");
        btnActualizarHistorial.addActionListener(e -> cargarHistorialBonificaciones());
        
        JPanel panelBotonesHistorial = new JPanel();
        panelBotonesHistorial.add(btnActualizarHistorial);
        
        panelHistorial.add(scrollPaneHistorial, BorderLayout.CENTER);
        panelHistorial.add(panelBotonesHistorial, BorderLayout.SOUTH);
        
        btnAplicar = new JButton("Aplicar");
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        // Configurar doble clic para ver detalles
        tablaBonificacionesActivas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaBonificacionesActivas.rowAtPoint(e.getPoint());
                    if (fila >= 0) {
                        mostrarDetalleBonificaciones(fila);
                    }
                }
            }
        });
    }

    private void setupLayout() {
        JPanel panelIndividualForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelIndividualForm.add(new JLabel("Empleado:"), gbc);
        gbc.gridx = 1;
        panelIndividualForm.add(comboEmpleados, gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        panelIndividualForm.add(new JLabel("Tipo de bonificación:"), gbc);
        gbc.gridx = 1;
        panelIndividualForm.add(comboTipoBonificacion, gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        panelIndividualForm.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 1;
        panelIndividualForm.add(txtMonto, gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        panelIndividualForm.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panelIndividualForm.add(new JScrollPane(txtDescripcion), gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        panelIndividualForm.add(new JLabel("Mes de aplicación:"), gbc);
        gbc.gridx = 1;
        panelIndividualForm.add(dateChooserMes, gbc);
        
        panelIndividual.add(panelIndividualForm);
        
        JPanel panelGeneralForm = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelGeneralForm.add(new JLabel("Tipo de bonificación:"), gbc);
        gbc.gridx = 1;
        panelGeneralForm.add(comboTipoBonificacionGeneral, gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        panelGeneralForm.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 1;
        panelGeneralForm.add(txtMontoGeneral, gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        panelGeneralForm.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panelGeneralForm.add(new JScrollPane(txtDescripcionGeneral), gbc);
        
        gbc.gridx = 0; gbc.gridy++;
        panelGeneralForm.add(new JLabel("Mes de aplicación:"), gbc);
        gbc.gridx = 1;
        panelGeneralForm.add(dateChooserMesGeneral, gbc);
        
        panelGeneral.add(panelGeneralForm);
        
        tabbedPane.addTab("Individual", panelIndividual);
        tabbedPane.addTab("General", panelGeneral);
        tabbedPane.addTab("Bonificaciones del Mes", panelBonificacionesActivas);
        tabbedPane.addTab("Historial Completo", panelHistorial);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnAplicar);
        
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarEmpleadosEnComboBox() {
        comboEmpleados.removeAllItems();
        comboEmpleados.addItem("Selecciona un empleado");

        String query = "SELECT NOMBRE_COMPLETO FROM empleados";
        try (Connection con = ConexionBD.obtenerConexion(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String nombreEmpleado = rs.getString("NOMBRE_COMPLETO");
                comboEmpleados.addItem(nombreEmpleado);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarBonificacionesDelMes() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        model.addColumn("Empleado");
        model.addColumn("Tipos de Bonificación");
        model.addColumn("Total");
        model.addColumn("Periodo");
        
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM yyyy");
        
        String query = "SELECT e.NOMBRE_COMPLETO, " +
                      "GROUP_CONCAT(b.tipo, ', ') AS tipos, " +
                      "SUM(b.monto) AS total, " +
                      "b.inicio_bon " +
                      "FROM bonificaciones b " +
                      "LEFT JOIN empleados e ON b.id_empleado = e.ID " +
                      "WHERE strftime('%Y-%m', b.inicio_bon) = strftime('%Y-%m', 'now') " +
                      "GROUP BY e.NOMBRE_COMPLETO, b.inicio_bon " +
                      "ORDER BY e.NOMBRE_COMPLETO";
        
        try (Connection con = ConexionBD.obtenerConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String periodo = "";
                try {
                    Date inicio = dbFormat.parse(rs.getString("inicio_bon"));
                    periodo = displayFormat.format(inicio);
                } catch (ParseException e) {
                    periodo = rs.getString("inicio_bon");
                }
                
               model.addRow(new Object[]{
    rs.getString("NOMBRE_COMPLETO"),
    rs.getString("tipos"),
    String.format("Bs. %,.2f", rs.getDouble("total")),  // Cambiado de $ a Bs.
    periodo
});

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar bonificaciones del mes: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        tablaBonificacionesActivas.setModel(model);
        ajustarAnchoColumnas(tablaBonificacionesActivas);
    }
    
    private void cargarHistorialBonificaciones() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        model.addColumn("Empleado");
        model.addColumn("Tipos de Bonificación");
        model.addColumn("Total");
        model.addColumn("Periodo");
        
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM yyyy");
        
        String query = "SELECT e.NOMBRE_COMPLETO, " +
                      "GROUP_CONCAT(b.tipo, ', ') AS tipos, " +
                      "SUM(b.monto) AS total, " +
                      "b.inicio_bon " +
                      "FROM bonificaciones b " +
                      "LEFT JOIN empleados e ON b.id_empleado = e.ID " +
                      "GROUP BY e.NOMBRE_COMPLETO, b.inicio_bon " +
                      "ORDER BY b.inicio_bon DESC";
        
        try (Connection con = ConexionBD.obtenerConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String periodo = "";
                try {
                    Date inicio = dbFormat.parse(rs.getString("inicio_bon"));
                    periodo = displayFormat.format(inicio);
                } catch (ParseException e) {
                    periodo = rs.getString("inicio_bon");
                }
                
               model.addRow(new Object[]{
    rs.getString("NOMBRE_COMPLETO"),
    rs.getString("tipos"),
    String.format("Bs. %,.2f", rs.getDouble("total")),  // Cambiado de $ a Bs.
    periodo
});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar historial: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        tablaHistorial.setModel(model);
        ajustarAnchoColumnas(tablaHistorial);
    }
    
    private void ajustarAnchoColumnas(JTable tabla) {
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel columnModel = tabla.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150); // Empleado
        columnModel.getColumn(1).setPreferredWidth(250); // Tipos
        columnModel.getColumn(2).setPreferredWidth(100); // Total
        columnModel.getColumn(3).setPreferredWidth(100); // Periodo
    }
    
    private void mostrarDetalleBonificaciones(int filaSeleccionada) {
        String nombreEmpleado = (String) tablaBonificacionesActivas.getValueAt(filaSeleccionada, 0);
        String periodo = (String) tablaBonificacionesActivas.getValueAt(filaSeleccionada, 3);
        
        String sql = "SELECT b.tipo, b.monto, b.descripcion " +
                    "FROM bonificaciones b " +
                    "JOIN empleados e ON b.id_empleado = e.ID " +
                    "WHERE e.NOMBRE_COMPLETO = ? AND strftime('%Y-%m', b.inicio_bon) = ?";
        
        StringBuilder detalles = new StringBuilder();
        detalles.append("<html><b>").append(nombreEmpleado).append("</b><br>");
        detalles.append("Periodo: ").append(periodo).append("<br><br>");
        
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreEmpleado);
            pstmt.setString(2, new SimpleDateFormat("yyyy-MM").format(
                new SimpleDateFormat("MMM yyyy").parse(periodo)));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    detalles.append("<b>Tipo:</b> ").append(rs.getString("tipo")).append("<br>");
detalles.append("<b>Monto:</b> Bs.").append(String.format("%,.2f", rs.getDouble("monto"))).append("<br>"); 
detalles.append("<b>Descripción:</b> ").append(rs.getString("descripcion")).append("<br><br>");
                }
            }
        } catch (SQLException | ParseException e) {
            detalles.append("Error al cargar detalles: ").append(e.getMessage());
        }
        
        JOptionPane.showMessageDialog(this, detalles.toString(), 
            "Detalle de Bonificaciones", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean existeBonificacionDuplicada(int idEmpleado, String tipo, Date inicio) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String inicioStr = sdf.format(inicio);
        
        String sql = "SELECT COUNT(*) FROM bonificaciones WHERE id_empleado = ? " +
                    "AND tipo = ? AND inicio_bon = ?";
        
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEmpleado);
            pstmt.setString(2, tipo);
            pstmt.setString(3, inicioStr);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar duplicados: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private void eliminarBonificacionSeleccionada() {
        int filaSeleccionada = tablaBonificacionesActivas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una bonificación para eliminar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombreEmpleado = (String) tablaBonificacionesActivas.getValueAt(filaSeleccionada, 0);
        String periodo = (String) tablaBonificacionesActivas.getValueAt(filaSeleccionada, 3);
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar TODAS las bonificaciones de " + nombreEmpleado + " para " + periodo + "?", 
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM bonificaciones WHERE id_empleado = " +
                        "(SELECT ID FROM empleados WHERE NOMBRE_COMPLETO = ?) " +
                        "AND strftime('%Y-%m', inicio_bon) = ?";
            
            try (Connection con = ConexionBD.obtenerConexion();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                
                pstmt.setString(1, nombreEmpleado);
                
                // Convertir periodo a formato yyyy-MM
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM yyyy");
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM");
                String periodoFormatoDB = dbFormat.format(displayFormat.parse(periodo));
                
                pstmt.setString(2, periodoFormatoDB);
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Se eliminaron " + affectedRows + " bonificaciones", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarBonificacionesDelMes();
                    cargarHistorialBonificaciones();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontraron bonificaciones a eliminar", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException | ParseException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar bonificaciones: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private Date getPrimerDiaDelMes(Date fecha) {
        if (fecha == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private Date getUltimoDiaDelMes(Date fecha) {
        if (fecha == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public boolean esBonificacionGeneral() {
        return tabbedPane.getSelectedIndex() == 1;
    }

    public String getNombreEmpleado() {
        return (String) comboEmpleados.getSelectedItem();
    }

    public String getTipoBonificacion() {
        return esBonificacionGeneral() ? 
            (String) comboTipoBonificacionGeneral.getSelectedItem() : 
            (String) comboTipoBonificacion.getSelectedItem();
    }

    public double getMonto() {
        try {
            return Double.parseDouble(esBonificacionGeneral() ? 
                txtMontoGeneral.getText() : txtMonto.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Date getFechaInicio() {
        Date fechaSeleccionada = esBonificacionGeneral() ? 
            dateChooserMesGeneral.getDate() : dateChooserMes.getDate();
        return getPrimerDiaDelMes(fechaSeleccionada);
    }

    public Date getFechaFin() {
        Date fechaSeleccionada = esBonificacionGeneral() ? 
            dateChooserMesGeneral.getDate() : dateChooserMes.getDate();
        return getUltimoDiaDelMes(fechaSeleccionada);
    }

    public String getDescripcion() {
        return esBonificacionGeneral() ? 
            txtDescripcionGeneral.getText() : txtDescripcion.getText();
    }

    private int obtenerIdEmpleadoPorNombre(String nombre) {
        if (nombre.equals("Selecciona un empleado")) {
            return -1;
        }
        
        String sql = "SELECT ID FROM empleados WHERE NOMBRE_COMPLETO = ?";
        try (Connection conn = ConexionBD.obtenerConexion(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener ID del empleado: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
    
   private boolean insertarBonificacion(Integer idEmpleado, String tipo, double monto, 
                                   String descripcion, Date inicio, Date fin) {
    // Primero verificamos la contraseña antes de cualquier operación
    if (!confirmarAccionConPassword.confirmarAccion(this)) {
        return false;
    }

    if (idEmpleado != null && idEmpleado != -1) {
        if (existeBonificacionDuplicada(idEmpleado, tipo, inicio)) {
            JOptionPane.showMessageDialog(this, 
                "Este empleado ya tiene una bonificación de este tipo para el período seleccionado",
                "Bonificación duplicada", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    try {
        if (inicio == null || fin == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un mes válido", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String inicioStr = sdf.format(inicio);
        String finStr = sdf.format(fin);
        
        String sql = "INSERT INTO bonificaciones (id_empleado, tipo, monto, descripcion, " +
                    "fecha_aplicacion, inicio_bon, fin_bon) VALUES (?, ?, ?, ?, date('now'), ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            if (idEmpleado == null || idEmpleado == -1) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, idEmpleado);
            }

            pstmt.setString(2, tipo);
            pstmt.setDouble(3, monto);
            pstmt.setString(4, descripcion);
            pstmt.setString(5, inicioStr);
            pstmt.setString(6, finStr);

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                SwingUtilities.invokeLater(() -> {
                    cargarBonificacionesDelMes();
                    cargarHistorialBonificaciones();
                    tabbedPane.setSelectedIndex(2);
                });
                return true;
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al guardar en base de datos: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    return false;
}
    
    private void configurarBotonAplicar() {
        btnAplicar.addActionListener(e -> {
            if (!esBonificacionGeneral() && getNombreEmpleado().equals("Selecciona un empleado")) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un empleado", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (getMonto() <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Date mesSeleccionado = esBonificacionGeneral() ? 
                dateChooserMesGeneral.getDate() : dateChooserMes.getDate();
            
            if (mesSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un mes", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (esBonificacionGeneral()) {
                if (aplicarBonificacionGeneral()) {
                    txtMontoGeneral.setText("");
                    txtDescripcionGeneral.setText("");
                    dateChooserMesGeneral.setDate(new Date());
                }
            } else {
                int idEmpleado = obtenerIdEmpleadoPorNombre(getNombreEmpleado());
                if (idEmpleado != -1) {
                    int confirmacion = JOptionPane.showConfirmDialog(this,
                        "¿Desea agregar esta bonificación además de las existentes?",
                        "Confirmar bonificación adicional",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        insertarBonificacion(idEmpleado, getTipoBonificacion(), getMonto(), 
                            getDescripcion(), getFechaInicio(), getFechaFin());
                    }
                }
            }
        });
    }

    private boolean aplicarBonificacionGeneral() {
    // Verificación de contraseña antes de continuar
    if (!confirmarAccionConPassword.confirmarAccion(this)) {
        return false;
    }

    Connection con = null;
    try {
        con = ConexionBD.obtenerConexion();
        con.setAutoCommit(false);
        
        List<Integer> idsEmpleados = new ArrayList<>();
        String sqlEmpleados = "SELECT ID FROM empleados";
        try (PreparedStatement pstmt = con.prepareStatement(sqlEmpleados);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                idsEmpleados.add(rs.getInt("ID"));
            }
        }

        if (idsEmpleados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay empleados registrados", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sqlInsert = "INSERT INTO bonificaciones (id_empleado, tipo, monto, descripcion, " +
                         "fecha_aplicacion, inicio_bon, fin_bon) VALUES (?, ?, ?, ?, date('now'), ?, ?)";
        
        try (PreparedStatement pstmt = con.prepareStatement(sqlInsert)) {
            String tipo = getTipoBonificacion();
            double monto = getMonto();
            String descripcion = getDescripcion();
            String inicioStr = new SimpleDateFormat("yyyy-MM-dd").format(getFechaInicio());
            String finStr = new SimpleDateFormat("yyyy-MM-dd").format(getFechaFin());
            
            for (Integer idEmpleado : idsEmpleados) {
                pstmt.setInt(1, idEmpleado);
                pstmt.setString(2, tipo);
                pstmt.setDouble(3, monto);
                pstmt.setString(4, descripcion);
                pstmt.setString(5, inicioStr);
                pstmt.setString(6, finStr);
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            con.commit();
            
            JOptionPane.showMessageDialog(this, 
    "Bonificación de Bs. " + monto + " aplicada a " + idsEmpleados.size() + " empleados",  // Cambiado de $ a Bs.
    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
    } catch (SQLException ex) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(this, 
            "Error al aplicar bonificación general: " + ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    return false;
}
}
