package com.mycompany;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.util.Date;

public class LiquidacionesDialog extends JDialog {

    private JTable tablaliquidaciones;
    private JScrollPane scrollPane;
    private JComboBox<String> cbEmpleados;
    private JComboBox<String> cbTipoLiquidacion;
    private JTextField txtMontoTotal;
    private JTextArea txaMotivo;
    private JDateChooser dcFechaLiquidacion;
    private JCheckBox chkDespido;
    private JButton btnGuardar;
    private JButton btnCalcular;

    public LiquidacionesDialog(Frame parent) {
        super(parent, "Gestión de Liquidaciones y Despidos", true);
        setLayout(new BorderLayout());
        setSize(1000, 700);
        setLocationRelativeTo(parent);

        // Panel superior con controles
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Componentes
        cbEmpleados = new JComboBox<>();
        cargarEmpleadosEnComboBox();

        cbTipoLiquidacion = new JComboBox<>(new String[]{
            "Seleccione tipo",
            "Liquidación voluntaria",
            "Liquidación por despido",
            "Liquidación por renuncia",
            "Fin de contrato"
        });

        txtMontoTotal = new JTextField(15);
        txtMontoTotal.setEditable(false);
        txaMotivo = new JTextArea(3, 30);
        txaMotivo.setLineWrap(true);
        JScrollPane scrollMotivo = new JScrollPane(txaMotivo);
        dcFechaLiquidacion = new JDateChooser();
        dcFechaLiquidacion.setDateFormatString("dd/MM/yyyy");
        chkDespido = new JCheckBox("Incluir preaviso y auxilio de cesantías");
        btnCalcular = new JButton("Calcular Liquidación");
        btnGuardar = new JButton("Guardar Liquidación");

        // Configuración del diseño
        gbc.gridx = 0; gbc.gridy = 0;
        panelSuperior.add(new JLabel("Empleado:"), gbc);
        gbc.gridx = 1;
        panelSuperior.add(cbEmpleados, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelSuperior.add(new JLabel("Tipo de Liquidación:"), gbc);
        gbc.gridx = 1;
        panelSuperior.add(cbTipoLiquidacion, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelSuperior.add(new JLabel("Fecha Liquidación:"), gbc);
        gbc.gridx = 1;
        panelSuperior.add(dcFechaLiquidacion, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelSuperior.add(new JLabel("Monto Total:"), gbc);
        gbc.gridx = 1;
        panelSuperior.add(txtMontoTotal, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelSuperior.add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1;
        panelSuperior.add(scrollMotivo, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        panelSuperior.add(chkDespido, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        panelSuperior.add(btnCalcular, gbc);

        gbc.gridx = 1; gbc.gridy = 7;
        panelSuperior.add(btnGuardar, gbc);

        // Tabla para mostrar el historial
        tablaliquidaciones = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scrollPane = new JScrollPane(tablaliquidaciones);

        // Panel de botones inferiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCargar = new JButton("Cargar Historial");
        JButton btnImprimir = new JButton("Imprimir Liquidación");
        JButton btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnCargar);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnCerrar);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // Listeners
        btnCargar.addActionListener(e -> cargarHistorialLiquidaciones());
        btnCalcular.addActionListener(e -> calcularLiquidacion());
        btnGuardar.addActionListener(e -> guardarLiquidacion());
        btnImprimir.addActionListener(e -> imprimirLiquidacion());
        btnCerrar.addActionListener(e -> dispose());

        cbEmpleados.addActionListener(e -> {
            if (cbEmpleados.getSelectedIndex() > 0) {
                cargarHistorialLiquidaciones();
            }
        });
    }

    private void cargarEmpleadosEnComboBox() {
        cbEmpleados.removeAllItems();
        cbEmpleados.addItem("Seleccione un empleado");
        String query = "SELECT ID, NOMBRE_COMPLETO FROM empleados WHERE activo = 1 ORDER BY NOMBRE_COMPLETO";

        try (Connection con = ConexionBD.obtenerConexion(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {

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

        try (Connection conn = ConexionBD.obtenerConexion(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

    private void cargarHistorialLiquidaciones() {
        int idEmpleado = obtenerIdEmpleadoSeleccionado();
        if (idEmpleado == -1) return;

        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.setColumnIdentifiers(new Object[]{
            "ID", "Tipo", "Monto Total", "Fecha Liquidación", "Motivo", "Incluyó preaviso", "Fecha Registro"
        });

        String sql = "SELECT * FROM liquidaciones WHERE id_empleado = ? ORDER BY fecha_liquidacion DESC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, idEmpleado);
            
            try (ResultSet rs = pst.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("id_liquidacion"),
                        rs.getString("tipo"),
                        String.format("%,.2f", rs.getDouble("monto_total")),
                        sdf.format(rs.getDate("fecha_liquidacion")),
                        rs.getString("motivo"),
                        rs.getBoolean("incluye_preaviso") ? "Sí" : "No",
                        sdf.format(rs.getDate("fecha_registro"))
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar liquidaciones: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        tablaliquidaciones.setModel(modelo);
    }

    private void calcularLiquidacion() {
        int idEmpleado = obtenerIdEmpleadoSeleccionado();
        if (idEmpleado == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un empleado válido", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dcFechaLiquidacion.getDate() == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione una fecha de liquidación", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Aquí iría la lógica compleja de cálculo de liquidación
            // Esto es un ejemplo simplificado
            
            // 1. Obtener datos básicos del empleado
            String sqlEmpleado = "SELECT fecha_ingreso, salario FROM empleados WHERE id = ?";
            Date fechaIngreso = null;
            double salario = 0;
            
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sqlEmpleado)) {
                
                pstmt.setInt(1, idEmpleado);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    fechaIngreso = rs.getDate("fecha_ingreso");
                    salario = rs.getDouble("salario");
                }
            }
            
            // 2. Calcular tiempo de servicio
            long diff = dcFechaLiquidacion.getDate().getTime() - fechaIngreso.getTime();
            int añosServicio = (int) (diff / (1000L * 60 * 60 * 24 * 365));
            
            // 3. Calcular componentes de liquidación
            double cesantias = salario * añosServicio;
            double interesesCesantias = cesantias * 0.12;
            double primaServicios = salario * añosServicio;
            double vacaciones = salario * (añosServicio / 2);
            
            // 4. Calcular total
            double total = cesantias + interesesCesantias + primaServicios + vacaciones;
            
            // Si es despido, agregar preaviso y auxilio
            if (chkDespido.isSelected()) {
                double preaviso = salario;
                double auxilioCesantias = salario * 0.5;
                total += preaviso + auxilioCesantias;
            }
            
            txtMontoTotal.setText(String.format("%,.2f", total));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al calcular liquidación: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarLiquidacion() {
        int idEmpleado = obtenerIdEmpleadoSeleccionado();
        if (idEmpleado == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un empleado válido", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipo = (String) cbTipoLiquidacion.getSelectedItem();
        if (cbTipoLiquidacion.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un tipo de liquidación", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtMontoTotal.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Calcule primero el monto de la liquidación", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double montoTotal = Double.parseDouble(txtMontoTotal.getText().replace(",", ""));
            String motivo = txaMotivo.getText();
            
            if (motivo.isEmpty()) {
                motivo = "Liquidación por " + tipo.toLowerCase();
            }

            java.sql.Date fechaLiquidacion = new java.sql.Date(dcFechaLiquidacion.getDate().getTime());
            boolean incluyePreaviso = chkDespido.isSelected();

            String sql = "INSERT INTO liquidaciones (id_empleado, tipo, monto_total, fecha_liquidacion, "
                       + "motivo, incluye_preaviso, fecha_registro) "
                       + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)";

            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, idEmpleado);
                pstmt.setString(2, tipo);
                pstmt.setDouble(3, montoTotal);
                pstmt.setDate(4, fechaLiquidacion);
                pstmt.setString(5, motivo);
                pstmt.setBoolean(6, incluyePreaviso);
                
                int filas = pstmt.executeUpdate();
                if (filas > 0) {
                    // Marcar empleado como inactivo
                    String sqlUpdate = "UPDATE empleados SET activo = 0 WHERE id = ?";
                    try (PreparedStatement pst = conn.prepareStatement(sqlUpdate)) {
                        pst.setInt(1, idEmpleado);
                        pst.executeUpdate();
                    }
                    
                    JOptionPane.showMessageDialog(this, 
                        "Liquidación registrada con éxito", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                    cargarEmpleadosEnComboBox(); // Actualizar lista de empleados activos
                    cargarHistorialLiquidaciones();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Monto total inválido", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar liquidación: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirLiquidacion() {
        // Implementar lógica de impresión específica para liquidaciones
        try {
            tablaliquidaciones.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al imprimir: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtMontoTotal.setText("");
        txaMotivo.setText("");
        dcFechaLiquidacion.setDate(null);
        chkDespido.setSelected(false);
        cbTipoLiquidacion.setSelectedIndex(0);
    }
}
