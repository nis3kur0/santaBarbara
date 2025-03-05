package com.mycompany.views;

import com.mycompany.ConexionBD;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ReportePagosDialog extends JDialog {

    private JTable tablaPagos;
    private JScrollPane scrollPane;
    private JComboBox<String> jComboBox1;  // ComboBox para seleccionar el empleado
    private JButton btnCerrar;

    public ReportePagosDialog(Frame parent) {
        super(parent, "Reporte de Pagos", true); // El "true" hace que el JDialog sea modal
        setLayout(new BorderLayout());
        setSize(800, 500);
        setLocationRelativeTo(parent); // Centra el JDialog respecto a la ventana principal

        // Crear ComboBox para seleccionar empleado
        jComboBox1 = new JComboBox<>();
        cargarEmpleadosEnComboBox();  // Cargar empleados en el JComboBox

        // Botón para cargar los pagos
        JButton btnCargarPagos = new JButton("Cargar Pagos");
        btnCargarPagos.addActionListener(e -> {
            String nombreEmpleado = (String) jComboBox1.getSelectedItem();
            if (!nombreEmpleado.equals("Selecciona un empleado")) {
                int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);
                if (idEmpleado != -1) {
                    cargarPagosEmpleadoEnTabla(idEmpleado);  // Cargar pagos en la tabla
                } else {
                    JOptionPane.showMessageDialog(this, "Empleado no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Panel para el ComboBox y el botón
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Selecciona un empleado:"));
        panelSuperior.add(jComboBox1);
        panelSuperior.add(btnCargarPagos);

        // Crear la tabla
        tablaPagos = new JTable();
        scrollPane = new JScrollPane(tablaPagos);

        // Crear panel de botones (guardar, imprimir, cerrar)
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.RIGHT));

       // Botón de Guardar
JButton btnGuardar = new JButton("Guardar");
btnGuardar.addActionListener(e -> {
    // Obtener el nombre del empleado seleccionado
    String nombreEmpleado = (String) jComboBox1.getSelectedItem();
    
    if (!nombreEmpleado.equals("Selecciona un empleado")) {
        // Obtener el ID del empleado por su nombre
        int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);
        
        if (idEmpleado != -1) {
            // Aquí definimos un rango de fechas predeterminado para el recibo
            String fechaInicio = "2025-01-01";  // Fecha de inicio predeterminada
            String fechaFin = "2025-01-31";     // Fecha de fin predeterminada

            // Llamamos a generarRecibo con los parámetros obtenidos
            reportePagos.generarRecibo(idEmpleado, fechaInicio, fechaFin);
            
            JOptionPane.showMessageDialog(this, "Recibo generado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Empleado no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Por favor, selecciona un empleado.", "Error", JOptionPane.ERROR_MESSAGE);
    }
});

        // Botón de Imprimir (sin funcionalidad por ahora)
        JButton btnImprimir = new JButton("Imprimir");
        btnImprimir.addActionListener(e -> {
            // Aquí iría el código para imprimir los datos
            System.out.println("Imprimir no implementado aún.");
        });

        // Botón de Cerrar
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose()); // Al hacer click, cierra el JDialog

        // Añadir los botones al panel de botones
        panelBotones.add(btnGuardar);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnCerrar);

        // Añadir todo al JDialog
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Método para cargar empleados en el ComboBox
    public void cargarEmpleadosEnComboBox() {
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Selecciona un empleado");
        String query = "SELECT NOMBRE_COMPLETO FROM empleados";

        try (Connection con = ConexionBD.obtenerConexion(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String nombreEmpleado = rs.getString("NOMBRE_COMPLETO");
                jComboBox1.addItem(nombreEmpleado);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para obtener el ID del empleado por su nombre
    private int obtenerIdEmpleadoPorNombre(String nombre) {
        String sql = "SELECT ID FROM empleados WHERE NOMBRE_COMPLETO = ?";

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener el ID del empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    // Método para cargar los pagos del empleado en la tabla
    public void cargarPagosEmpleadoEnTabla(int idEmpleado) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{
            "ID Pago", "Fecha Inicio", "Fecha Fin", "Fecha de Pago", 
            "Salario Base", "Días Trabajados", "Ausencias", 
            "Horas Extras", "IVSS", "FAOV", "INCES", "Sueldo Final"
        });

        String sql = "SELECT * FROM pagos_nomina WHERE ID_EMPLEADO = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idEmpleado);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getInt("ID_PAGO"),
                        rs.getDate("FECHA_INICIO_NOMINA"),
                        rs.getDate("FECHA_FIN_NOMINA"),
                        rs.getDate("FECHA_DE_PAGO"),
                        rs.getDouble("SALARIO_BASE"),
                        rs.getInt("DIAS_TRABAJADOS"),
                        rs.getInt("AUSENCIAS"),
                        rs.getDouble("HORAS_EXTRAS"),
                        rs.getDouble("IVSS"),
                        rs.getDouble("FAOV"),
                        rs.getDouble("INCES"),
                        rs.getDouble("SUELDO_FINAL")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los pagos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        tablaPagos.setModel(modelo);
    }
}
