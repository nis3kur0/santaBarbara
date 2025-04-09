package com.mycompany;


import com.mycompany.ConexionBD;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BonificacionesDialog extends JDialog {
    private JTabbedPane tabbedPane;
    private JPanel panelIndividual;
    private JPanel panelGeneral;
    private JPanel panelConsulta;
    
    // Componentes para pestaña individual
    private JComboBox<String> comboEmpleados;
    private JComboBox<String> comboTipoBonificacion;
    private JTextField txtMonto;
    private JTextArea txtDescripcion;
    private JDateChooser dateChooserMes;
    
    // Componentes para pestaña general
    private JComboBox<String> comboTipoBonificacionGeneral;
    private JTextField txtMontoGeneral;
    private JTextArea txtDescripcionGeneral;
    private JDateChooser dateChooserMesGeneral;
    
    // Componentes para pestaña consulta
    private JTable tablaBonificaciones;
    private JButton btnEliminar;
    private JButton btnActualizar;
    
    // Botones generales
    private JButton btnAplicar;
    private JButton btnCancelar;

    public BonificacionesDialog(Frame parent) {
        super(parent, "Gestión de Bonificaciones", true);
        initComponents();
        setupLayout();
        configurarBotonAplicar();
        cargarBonificacionesActivas();
        pack();
        setLocationRelativeTo(parent);
        setSize(700, 500);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Pestaña individual
        panelIndividual = new JPanel();
        panelIndividual.setBorder(new TitledBorder("Bonificación Individual"));
        
        comboEmpleados = new JComboBox<>();
        cargarEmpleadosEnComboBox();
        
        comboTipoBonificacion = new JComboBox<>(new String[]{"Productividad", "Asistencia", "Puntualidad", "Otros"});
        txtMonto = new JTextField(10);
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        dateChooserMes = new JDateChooser(new Date());
        dateChooserMes.setDateFormatString("MM/yyyy");
        dateChooserMes.getDateEditor().setEnabled(false);
        
        // Pestaña general
        panelGeneral = new JPanel();
        panelGeneral.setBorder(new TitledBorder("Bonificación General"));
        
        comboTipoBonificacionGeneral = new JComboBox<>(new String[]{"Productividad", "Asistencia", "Puntualidad", "Otros"});
        txtMontoGeneral = new JTextField(10);
        txtDescripcionGeneral = new JTextArea(3, 20);
        txtDescripcionGeneral.setLineWrap(true);
        dateChooserMesGeneral = new JDateChooser(new Date());
        dateChooserMesGeneral.setDateFormatString("MM/yyyy");
        dateChooserMesGeneral.getDateEditor().setEnabled(false);
        
        // Pestaña consulta
        panelConsulta = new JPanel(new BorderLayout());
        tablaBonificaciones = new JTable();
        JScrollPane scrollPane = new JScrollPane(tablaBonificaciones);
        
        btnEliminar = new JButton("Eliminar Seleccionada");
        btnActualizar = new JButton("Actualizar Lista");
        
        btnActualizar.addActionListener(e -> cargarBonificacionesActivas());
        btnEliminar.addActionListener(e -> eliminarBonificacionSeleccionada());
        
        JPanel panelBotonesConsulta = new JPanel();
        panelBotonesConsulta.add(btnActualizar);
        panelBotonesConsulta.add(btnEliminar);
        
        panelConsulta.add(scrollPane, BorderLayout.CENTER);
        panelConsulta.add(panelBotonesConsulta, BorderLayout.SOUTH);
        
        // Botones generales
        btnAplicar = new JButton("Aplicar");
        btnCancelar = new JButton("Cancelar");
        
        btnCancelar.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        // Configuración pestaña individual
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
        
        // Configuración pestaña general
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
        
        // Añadir las tres pestañas
        tabbedPane.addTab("Individual", panelIndividual);
        tabbedPane.addTab("General", panelGeneral);
        tabbedPane.addTab("Consultar/Eliminar", panelConsulta);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnAplicar);
        
        // Diseño principal
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
    
    private void cargarBonificacionesActivas() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Integer.class;
                    case 3 -> Double.class;
                    default -> String.class;
                };
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        model.addColumn("ID");
        model.addColumn("Empleado");
        model.addColumn("Tipo");
        model.addColumn("Monto");
        model.addColumn("Fecha Inicio");
        model.addColumn("Fecha Fin");
        model.addColumn("Descripción");
        
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        String query = "SELECT b.id_bonificacion, e.NOMBRE_COMPLETO, b.tipo, b.monto, " +
                      "b.inicio_bon, b.fin_bon, b.descripcion " +
                      "FROM bonificaciones b LEFT JOIN empleados e ON b.id_empleado = e.ID " +
                      "WHERE b.fin_bon >= date('now') ORDER BY b.fecha_aplicacion DESC";
        
        try (Connection con = ConexionBD.obtenerConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                try {
                    String inicioStr = rs.getString("inicio_bon");
                    String finStr = rs.getString("fin_bon");
                    
                    String inicioDisplay = "";
                    String finDisplay = "";
                    
                    if (inicioStr != null && !inicioStr.isEmpty()) {
                        Date fechaInicio = dbFormat.parse(inicioStr);
                        inicioDisplay = displayFormat.format(fechaInicio);
                    }
                    
                    if (finStr != null && !finStr.isEmpty()) {
                        Date fechaFin = dbFormat.parse(finStr);
                        finDisplay = displayFormat.format(fechaFin);
                    }
                    
                    model.addRow(new Object[]{
                        rs.getInt("id_bonificacion"),
                        rs.getString("NOMBRE_COMPLETO"),
                        rs.getString("tipo"),
                        rs.getDouble("monto"),
                        inicioDisplay,
                        finDisplay,
                        rs.getString("descripcion")
                    });
                    
                } catch (ParseException pe) {
                    System.err.println("Error parseando fecha: " + pe.getMessage());
                    model.addRow(new Object[]{
                        rs.getInt("id_bonificacion"),
                        rs.getString("NOMBRE_COMPLETO"),
                        rs.getString("tipo"),
                        rs.getDouble("monto"),
                        rs.getString("inicio_bon"),
                        rs.getString("fin_bon"),
                        rs.getString("descripcion")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar bonificaciones: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error al cargar bonificaciones: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        tablaBonificaciones.setModel(model);
        
        // Ajustar tamaño de columnas
        tablaBonificaciones.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaBonificaciones.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaBonificaciones.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaBonificaciones.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaBonificaciones.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaBonificaciones.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaBonificaciones.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaBonificaciones.getColumnModel().getColumn(6).setPreferredWidth(200);
    }
    
    private void eliminarBonificacionSeleccionada() {
        int filaSeleccionada = tablaBonificaciones.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una bonificación para eliminar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idBonificacion = (int) tablaBonificaciones.getValueAt(filaSeleccionada, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar esta bonificación?", "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM bonificaciones WHERE id_bonificacion = ?";
            
            try (Connection con = ConexionBD.obtenerConexion();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                
                pstmt.setInt(1, idBonificacion);
                pstmt.executeUpdate();
                cargarBonificacionesActivas();
                
                JOptionPane.showMessageDialog(this, "Bonificación eliminada correctamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar bonificación: " + e.getMessage(), 
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
                        cargarBonificacionesActivas();
                        tabbedPane.setSelectedIndex(2);
                    });
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al insertar bonificación:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al guardar en base de datos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error inesperado:");
            e.printStackTrace();
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
                insertarBonificacion(null, getTipoBonificacion(), getMonto(), 
                    getDescripcion(), getFechaInicio(), getFechaFin());
            } else {
                int idEmpleado = obtenerIdEmpleadoPorNombre(getNombreEmpleado());
                if (idEmpleado != -1) {
                    insertarBonificacion(idEmpleado, getTipoBonificacion(), getMonto(), 
                        getDescripcion(), getFechaInicio(), getFechaFin());
                }
            }
            
            cargarBonificacionesActivas();
            
            if (esBonificacionGeneral()) {
                txtMontoGeneral.setText("");
                txtDescripcionGeneral.setText("");
            } else {
                txtMonto.setText("");
                txtDescripcion.setText("");
            }
        });
    }
}
