package com.mycompany.views;

import com.mycompany.ConexionBD;
import com.mycompany.views.reportePagos;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;

public class ReportePagosDialog extends JDialog {

    private JTable tablaPagos;
    private JScrollPane scrollPane;
    private JComboBox<String> jComboBox1;  
    private JButton btnCerrar;

    public ReportePagosDialog(Frame parent) {
        super(parent, "Reporte de Pagos", true); 
        setLayout(new BorderLayout());
        setSize(800, 500);
        setLocationRelativeTo(parent); 

        
        jComboBox1 = new JComboBox<>();
        cargarEmpleadosEnComboBox();  

    
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

        
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Selecciona un empleado:"));
        panelSuperior.add(jComboBox1);
        panelSuperior.add(btnCargarPagos);

       
       tablaPagos = new JTable() {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};

        scrollPane = new JScrollPane(tablaPagos);

       
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.RIGHT));

JButton btnGuardar = new JButton("Guardar");
btnGuardar.addActionListener(e -> {
   
    String nombreEmpleado = (String) jComboBox1.getSelectedItem();
    
    if (!nombreEmpleado.equals("Selecciona un empleado")) {
   
        int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);
        
        if (idEmpleado != -1) {
            
            String fechaInicio = "2025-01-01"; 
            String fechaFin = "2025-01-31";     

            
            reportePagos.generarRecibo(idEmpleado, fechaInicio, fechaFin);
            
            JOptionPane.showMessageDialog(this, "Historial generado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Empleado no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Por favor, selecciona un empleado.", "Error", JOptionPane.ERROR_MESSAGE);
    }
});

        
        JButton btnImprimir = new JButton("Imprimir");
        btnImprimir.addActionListener(e -> {
            
            System.out.println("No se detecta ninguna impresora");
        });

        
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose()); 

       
        panelBotones.add(btnGuardar);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnCerrar);

      
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
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                String fechaInicio = rs.getString("FECHA_INICIO_NOMINA");
                String fechaFin = rs.getString("FECHA_FIN_NOMINA");
                String fechaPago = rs.getString("FECHA_DE_PAGO");

                String fechaInicioFormateada = formatoFecha.format(Date.valueOf(fechaInicio));
                String fechaFinFormateada = formatoFecha.format(Date.valueOf(fechaFin));
                String fechaPagoFormateada = formatoFecha.format(Date.valueOf(fechaPago));

                modelo.addRow(new Object[]{
                    rs.getInt("ID_PAGO"),
                    fechaInicioFormateada,
                    fechaFinFormateada,
                    fechaPagoFormateada,
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
