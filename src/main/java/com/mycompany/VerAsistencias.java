package com.mycompany;

import com.mycompany.ConexionBD;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;

public class VerAsistencias extends JDialog {

    private JTable tablaAsistencias;
    private JScrollPane scrollPane;
    private JComboBox<String> jComboBox1;
    private JDateChooser dateChooserInicio;
    private JDateChooser dateChooserFin;
    private JButton btnCerrar;

    public VerAsistencias(Frame parent) {
        super(parent, "Reporte de Asistencias", true);
        setLayout(new BorderLayout());
        setSize(800, 500);
        setLocationRelativeTo(parent);

        // ComboBox para seleccionar empleados
        jComboBox1 = new JComboBox<>();
        cargarEmpleadosEnComboBox();

        // DateChooser para seleccionar fecha de inicio y fecha final
        dateChooserInicio = new JDateChooser();
        dateChooserInicio.setDateFormatString("dd/MM/yyyy");
        dateChooserFin = new JDateChooser();
        dateChooserFin.setDateFormatString("dd/MM/yyyy");

        // Botón para cargar asistencias
        JButton btnCargarAsistencias = new JButton("Cargar Asistencias");
        btnCargarAsistencias.addActionListener(e -> {
            String nombreEmpleado = (String) jComboBox1.getSelectedItem();
            if (!nombreEmpleado.equals("Selecciona un empleado")) {
                int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);
                if (idEmpleado != -1) {
                    java.util.Date fechaInicio = dateChooserInicio.getDate();
                    java.util.Date fechaFin = dateChooserFin.getDate();
                    if (fechaInicio != null && fechaFin != null) {
                        cargarAsistenciasEmpleadoEnTabla(idEmpleado, fechaInicio, fechaFin);
                    } else {
                        JOptionPane.showMessageDialog(this, "Selecciona ambas fechas.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Empleado no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Panel superior con ComboBox, DateChooser y botón de cargar
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Selecciona un empleado:"));
        panelSuperior.add(jComboBox1);
        panelSuperior.add(new JLabel("Fecha Inicio:"));
        panelSuperior.add(dateChooserInicio);
        panelSuperior.add(new JLabel("Fecha Fin:"));
        panelSuperior.add(dateChooserFin);
        panelSuperior.add(btnCargarAsistencias);

        // Tabla para mostrar las asistencias
        tablaAsistencias = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scrollPane = new JScrollPane(tablaAsistencias);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // Botón para cerrar el diálogo
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        // Agregar botones al panel
        panelBotones.add(btnCerrar);

        // Agregar componentes al diálogo
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

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

    public void cargarAsistenciasEmpleadoEnTabla(int idEmpleado, java.util.Date fechaInicio, java.util.Date fechaFin) {
    DefaultTableModel modelo = new DefaultTableModel();
    modelo.setColumnIdentifiers(new Object[]{
        "ID Asistencia", "Fecha", "Hora Entrada", "Hora Salida", "Estado", "Observaciones"
    });

    // Consulta SQL para obtener las asistencias en el rango de fechas
    String sql = "SELECT * FROM asistencias WHERE ID_EMPLEADO = ? AND FECHA BETWEEN ? AND ?";

    try (Connection con = ConexionBD.obtenerConexion();
         PreparedStatement pst = con.prepareStatement(sql)) {

        // Convertir java.util.Date a java.sql.Date
        java.sql.Date fechaInicioSQL = new java.sql.Date(fechaInicio.getTime());
        java.sql.Date fechaFinSQL = new java.sql.Date(fechaFin.getTime());

        // Establecer parámetros en la consulta
        pst.setInt(1, idEmpleado);
        pst.setDate(2, fechaInicioSQL);
        pst.setDate(3, fechaFinSQL);

        // Ejecutar la consulta
        try (ResultSet rs = pst.executeQuery()) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

            // Recorrer el ResultSet y agregar filas al modelo de la tabla
            while (rs.next()) {
                String fecha = rs.getString("FECHA");
                String horaEntrada = rs.getString("HORA_ENTRADA");
                String horaSalida = rs.getString("HORA_SALIDA");
                String estado = rs.getString("ESTADO");
                String observaciones = rs.getString("OBSERVACIONES");

                // Formatear la fecha
                String fechaFormateada = formatoFecha.format(rs.getDate("FECHA"));

                // Agregar fila al modelo
                modelo.addRow(new Object[]{
                    rs.getInt("ID_ASISTENCIA"),
                    fechaFormateada,
                    horaEntrada,
                    horaSalida,
                    estado,
                    observaciones
                });
            }
        }
    } catch (SQLException e) {
        // Mostrar mensaje de error si ocurre una excepción
        JOptionPane.showMessageDialog(this, "Error al cargar las asistencias: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace(); // Imprimir la traza de la excepción para depuración
    }

    // Establecer el modelo en la tabla
    tablaAsistencias.setModel(modelo);

    // Verificar si la tabla está vacía
    if (modelo.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No se encontraron asistencias en el rango de fechas seleccionado.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
}
