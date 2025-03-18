package com.mycompany;

import com.mycompany.ConexionBD;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;

public class VerAsistencias extends JDialog {

    private JTable tablaAsistencias;
    private JScrollPane scrollPane;
    private JButton btnCerrar;
    private JButton btnGuardar;
    private JButton btnCargar;
    private JComboBox<String> comboEmpleados; 
    private JTextField txtFechaInicio; 
    private JTextField txtFechaFin;   
    private JLabel lblEmpleado; 
    private JLabel lblFechaInicio;
    private JLabel lblFechaFin;

    public VerAsistencias(Frame parent) {
        super(parent, "Ver Asistencias", true); 
        setLayout(new BorderLayout());
        setSize(1000, 500);
        setLocationRelativeTo(parent); 

        comboEmpleados = new JComboBox<>();
        cargarEmpleados();  

        lblEmpleado = new JLabel("Seleccione un empleado:");

        lblFechaInicio = new JLabel("Fecha Inicio (yyyy-MM-dd):");
        txtFechaInicio = new JTextField(10);

        lblFechaFin = new JLabel("Fecha Fin (yyyy-MM-dd):");
        txtFechaFin = new JTextField(10);

        btnCargar = new JButton("Cargar Asistencias");
        btnCargar.addActionListener(e -> cargarAsistencias());

        JPanel panelSuperior = new JPanel();
        panelSuperior.add(lblEmpleado);
        panelSuperior.add(comboEmpleados);
        panelSuperior.add(lblFechaInicio);
        panelSuperior.add(txtFechaInicio);
        panelSuperior.add(lblFechaFin);
        panelSuperior.add(txtFechaFin);
        panelSuperior.add(btnCargar);

        tablaAsistencias = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        scrollPane = new JScrollPane(tablaAsistencias);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.RIGHT));

        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose()); 
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> dispose()); 

        panelBotones.add(btnCerrar);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarEmpleados() {
        String sql = "SELECT NOMBRE_COMPLETO FROM empleados";
        
        try (Connection con = ConexionBD.obtenerConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            comboEmpleados.removeAllItems();

            comboEmpleados.addItem("Todos los empleados");

            while (rs.next()) {
                String nombreCompleto = rs.getString("NOMBRE_COMPLETO");
                comboEmpleados.addItem(nombreCompleto);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los empleados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void cargarAsistencias() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{
            "ID Empleado", "Nombre Completo", "Fecha", "Hora Entrada", "Hora Salida", "Estado", "Observaciones"
        });

        String empleadoSeleccionado = (String) comboEmpleados.getSelectedItem();
        String fechaInicio = txtFechaInicio.getText();
        String fechaFin = txtFechaFin.getText();

        if (!isValidDate(fechaInicio) || !isValidDate(fechaFin)) {
            JOptionPane.showMessageDialog(this, "Las fechas no son válidas. Por favor, use el formato yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT a.ID_EMPLEADO, e.NOMBRE_COMPLETO, a.FECHA, a.HORA_ENTRADA, a.HORA_SALIDA, a.ESTADO, a.OBSERVACIONES "
                + "FROM asistencias a "
                + "JOIN empleados e ON a.ID_EMPLEADO = e.ID "
                + "WHERE a.FECHA BETWEEN ? AND ?";

        if (!"Todos los empleados".equals(empleadoSeleccionado)) {
            sql += " AND e.NOMBRE_COMPLETO = ?";
        }

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, fechaInicio);
            stmt.setString(2, fechaFin);

            if (!"Todos los empleados".equals(empleadoSeleccionado)) {
                stmt.setString(3, empleadoSeleccionado);
            }

            try (ResultSet rs = stmt.executeQuery()) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                while (rs.next()) {
                    int idEmpleado = rs.getInt("ID_EMPLEADO");
                    String nombreCompleto = rs.getString("NOMBRE_COMPLETO");

                    String fechaStr = rs.getString("FECHA");
                    LocalDate fecha = LocalDate.parse(fechaStr, formatter);

                    String horaEntrada = rs.getString("HORA_ENTRADA");
                    String horaSalida = rs.getString("HORA_SALIDA");
                    String estado = rs.getString("ESTADO");
                    String observaciones = rs.getString("OBSERVACIONES");

                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String fechaFormateada = fecha.format(outputFormatter);

                    modelo.addRow(new Object[]{
                            idEmpleado, nombreCompleto, fechaFormateada, horaEntrada, horaSalida, estado, observaciones
                    });
                }

                tablaAsistencias.setModel(modelo);

                if (modelo.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No se encontraron asistencias.", "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las asistencias: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean isValidDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

