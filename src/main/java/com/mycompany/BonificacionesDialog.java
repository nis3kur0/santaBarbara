/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BonificacionesDialog extends JDialog {

    private JTable tablaBonificaciones;
    private JScrollPane scrollPane;
    private JComboBox<String> cbEmpleados;
    private JComboBox<String> cbTipoBonificacion;
    private JTextField txtMonto;
    private JTextField txtDescripcion;
    private JDateChooser dcFechaAplicacion;
    private JCheckBox chkRecurrente;
    private JButton btnGuardar;

    public BonificacionesDialog(Frame parent) {
        super(parent, "Gestión de Bonificaciones", true);
        setLayout(new BorderLayout());
        setSize(900, 600);
        setLocationRelativeTo(parent);

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbEmpleados = new JComboBox<>();
        cargarEmpleadosEnComboBox();

        cbTipoBonificacion = new JComboBox<>(new String[]{
            "Seleccione tipo",
            "Antigüedad",
            "Productividad",
            "Especial",
            "Vacaciones",
            "Utilidades"
        });

        txtMonto = new JTextField(10);
        txtDescripcion = new JTextField(20);
        dcFechaAplicacion = new JDateChooser();
        dcFechaAplicacion.setDateFormatString("dd/MM/yyyy");
        chkRecurrente = new JCheckBox("Recurrente");
        btnGuardar = new JButton("Guardar Bonificación");

        // Configuración del diseño
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelSuperior.add(new JLabel("Empleado:"), gbc);

        gbc.gridx = 1;
        panelSuperior.add(cbEmpleados, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelSuperior.add(new JLabel("Tipo de Bonificación:"), gbc);

        gbc.gridx = 1;
        panelSuperior.add(cbTipoBonificacion, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelSuperior.add(new JLabel("Monto:"), gbc);

        gbc.gridx = 1;
        panelSuperior.add(txtMonto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelSuperior.add(new JLabel("Fecha de Aplicación:"), gbc);

        gbc.gridx = 1;
        panelSuperior.add(dcFechaAplicacion, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelSuperior.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1;
        panelSuperior.add(txtDescripcion, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        panelSuperior.add(chkRecurrente, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        panelSuperior.add(btnGuardar, gbc);

        // Tabla para mostrar el historial
        tablaBonificaciones = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scrollPane = new JScrollPane(tablaBonificaciones);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCargar = new JButton("Cargar Historial");
        JButton btnImprimir = new JButton("Imprimir");
        JButton btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnCargar);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnCerrar);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> cargarHistorialBonificaciones());
        btnGuardar.addActionListener(e -> guardarBonificacion());
        btnImprimir.addActionListener(e -> imprimirHistorial());
        btnCerrar.addActionListener(e -> dispose());

        cbEmpleados.addActionListener(e -> {
            if (cbEmpleados.getSelectedIndex() > 0) {
                cargarHistorialBonificaciones();
            }
        });
    }

    private void cargarEmpleadosEnComboBox() {
        cbEmpleados.removeAllItems();
        cbEmpleados.addItem("Seleccione un empleado");
        String query = "SELECT ID, NOMBRE_COMPLETO FROM empleados ORDER BY NOMBRE_COMPLETO";

        try (Connection con = ConexionBD.obtenerConexion(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                cbEmpleados.addItem(rs.getString("NOMBRE_COMPLETO"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar empleados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int obtenerIdEmpleadoSeleccionado() {
        String nombre = (String) cbEmpleados.getSelectedItem();
        if (nombre == null || nombre.equals("Seleccione un empleado")) {
            return -1;
        }

        String sql = "SELECT ID FROM empleados WHERE NOMBRE_COMPLETO = ?";

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener ID del empleado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

   private void cargarHistorialBonificaciones() {
    int idEmpleado = obtenerIdEmpleadoSeleccionado();
    if (idEmpleado == -1) return;

    DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    modelo.setColumnIdentifiers(new Object[]{
        "ID", "Tipo", "Monto", "Fecha Aplicación", "Inicio Mes", "Fin Mes", "Recurrente", "Descripción"
    });

    String sql = "SELECT * FROM bonificaciones WHERE id_empleado = ? ORDER BY fecha_aplicacion DESC";

    try (Connection con = ConexionBD.obtenerConexion();
         PreparedStatement pst = con.prepareStatement(sql)) {
        
        pst.setInt(1, idEmpleado);
        
        try (ResultSet rs = pst.executeQuery()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_bonificacion"),
                    rs.getString("tipo"),
                    String.format("%,.2f", rs.getDouble("monto")),
                    sdf.format(rs.getDate("fecha_aplicacion")),
                    sdf.format(rs.getDate("inicio_bon")),
                    sdf.format(rs.getDate("fin_bon")),
                    rs.getBoolean("es_recurrente") ? "Sí" : "No",
                    rs.getString("descripcion")
                });
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar bonificaciones: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    tablaBonificaciones.setModel(modelo);
}

   private void guardarBonificacion() {
    int idEmpleado = obtenerIdEmpleadoSeleccionado();
    if (idEmpleado == -1) {
        JOptionPane.showMessageDialog(this, 
            "Seleccione un empleado válido", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String tipo = (String) cbTipoBonificacion.getSelectedItem();
    if (cbTipoBonificacion.getSelectedIndex() == 0) {
        JOptionPane.showMessageDialog(this, 
            "Seleccione un tipo de bonificación", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        double monto = Double.parseDouble(txtMonto.getText());
        if (monto <= 0) {
            JOptionPane.showMessageDialog(this, 
                "El monto debe ser mayor que cero", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String descripcion = txtDescripcion.getText();
        if (descripcion.isEmpty()) {
            descripcion = "Bonificación de " + tipo.toLowerCase();
        }

        java.util.Date fechaUtil = dcFechaAplicacion.getDate();
        if (fechaUtil == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione una fecha válida", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        java.sql.Date fecha = new java.sql.Date(fechaUtil.getTime());
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaUtil);
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        java.sql.Date inicioMes = new java.sql.Date(cal.getTimeInMillis());
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        java.sql.Date finMes = new java.sql.Date(cal.getTimeInMillis());

        boolean recurrente = chkRecurrente.isSelected();

        String sql = "INSERT INTO bonificaciones (id_empleado, tipo, monto, fecha_aplicacion, inicio_bon, fin_bon, descripcion, es_recurrente) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEmpleado);
            pstmt.setString(2, tipo);
            pstmt.setDouble(3, monto);
            pstmt.setDate(4, fecha);
            pstmt.setDate(5, inicioMes);
            pstmt.setDate(6, finMes);
            pstmt.setString(7, descripcion);
            pstmt.setBoolean(8, recurrente);
            
            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Bonificación registrada con éxito", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarHistorialBonificaciones();
            }
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, 
            "Ingrese un monto válido", 
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al guardar bonificación: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void limpiarCampos() {
        txtMonto.setText("");
        txtDescripcion.setText("");
        dcFechaAplicacion.setDate(null);
        chkRecurrente.setSelected(false);
        cbTipoBonificacion.setSelectedIndex(0);
    }

    private void imprimirHistorial() {
        try {
            tablaBonificaciones.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al imprimir: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
