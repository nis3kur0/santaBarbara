/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.views;

import com.mycompany.BonificacionesDialog;
import com.mycompany.ConexionBD;
import com.mycompany.LiquidacionesDialog;
import com.mycompany.recibodePago;
import com.mycompany.detalleNomina;
import com.mycompany.historialNomina;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.mycompany.confirmarAccionConPassword;

/**
 * PENDIENTE A REFACTORIZACION
 *
 *
 */
public class Nomina extends javax.swing.JPanel {

    /**
     * Creates new form Nomina
     */
    public Nomina() {
        initComponents();
        styles();
    }

    //ESTILOS
    private void styles() {

        tableTitle.setFont(UIManager.getFont("h1.font"));

    }

    //VARIABLES
    private double totalSueldoNeto = 0;

    //VALIDACIONES
    public boolean validarFechasSolapadas(LocalDate fechaInicio, LocalDate fechaFin) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConexionBD.obtenerConexion();

            String fechaInicioStr = fechaInicio.toString();
            String fechaFinStr = fechaFin.toString();

            String sql = "SELECT COUNT(*) FROM nomina WHERE ("
                    + "(? BETWEEN FECHA_INICIO_NOMINA AND FECHA_FIN_NOMINA) OR "
                    + "(FECHA_INICIO_NOMINA BETWEEN ? AND ?) OR "
                    + "(FECHA_FIN_NOMINA BETWEEN ? AND ?))";

            pst = con.prepareStatement(sql);
            pst.setString(1, fechaInicioStr);
            pst.setString(2, fechaFinStr);
            pst.setString(3, fechaInicioStr);
            pst.setString(4, fechaFinStr);
            pst.setString(5, fechaInicioStr);

            rs = pst.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Las fechas seleccionadas se solapan con una nómina existente.");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al verificar solapamiento de fechas: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    //FIN//
//CALCULOS Y ACCIONES
public void calcularNomina() {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicioStr = sdf.format(fechaInicioNom.getDate());
        String fechaFinStr = sdf.format(fechaFinNom.getDate());

        LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
        LocalDate fechaFin = LocalDate.parse(fechaFinStr);

        long diasDeDiferencia = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (diasDeDiferencia < 14) {
            JOptionPane.showMessageDialog(null, "El período de la nómina debe ser de al menos 15 días.");
            return;
        }
        if (diasDeDiferencia > 30) {
            JOptionPane.showMessageDialog(null, "El período de la nómina no puede exceder los 31 días.");
            return;
        }

        if (validarFechasSolapadas(fechaInicio, fechaFin)) {
            return;
        }

        con = ConexionBD.obtenerConexion();

        String sql = "SELECT e.NOMBRE_COMPLETO, e.SALARIO AS SALARIO_BASE, "
                + "COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END) AS DIAS_TRABAJADOS, "
                + "COUNT(CASE WHEN a.ESTADO = 'Ausente' THEN 1 END) AS AUSENCIAS, "
                + "ROUND(SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 "
                + "THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END), 2) AS HORAS_EXTRAS, "
                + "COALESCE(SUM(b.monto), 0) AS BONIFICACIONES, "
                + "ROUND((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END), 2) AS SUELDO_NETO, "
                + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.04, 2) AS IVSS, "
                + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS FAOV, "
                + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS INCES, "
                + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) - "
                + "(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.05) + "
                + "SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 "
                + "THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END) + "
                + "COALESCE(SUM(b.monto), 0), 2) AS SUELDO_FINAL "
                + "FROM empleados e "
                + "LEFT JOIN asistencias a ON e.ID = a.ID_EMPLEADO AND a.FECHA BETWEEN ? AND ? "
                + "LEFT JOIN bonificaciones b ON (e.ID = b.id_empleado OR b.id_empleado IS NULL) "
                + "AND (b.inicio_bon <= ? AND b.fin_bon >= ?) "
                + "GROUP BY e.NOMBRE_COMPLETO, e.SALARIO";

        pst = con.prepareStatement(sql);
        pst.setString(1, fechaInicioStr);
        pst.setString(2, fechaFinStr);
        pst.setString(3, fechaFinStr);
        pst.setString(4, fechaInicioStr);
        rs = pst.executeQuery();

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.setColumnIdentifiers(new Object[]{
            "Nombre", 
            "Salario Base", 
            "Días Trabajados", 
            "Ausencias",
            "Horas Extras", 
            "Bonificaciones",  
            "IVSS", 
            "FAOV", 
            "INCES", 
            "Salario Neto"
        });

        tablaNomina.setModel(model);

        double totalSalarioBase = 0;
        totalSueldoNeto = 0;
        double totalDeducciones = 0;
        double totalFAOV = 0;
        double totalIVSS = 0;
        double totalInces = 0;
        double totalBonificaciones = 0;

        while (rs.next()) {
            int diasTrabajados = rs.getInt("DIAS_TRABAJADOS");
            int ausencias = rs.getInt("AUSENCIAS");
            double horasExtras = rs.getDouble("HORAS_EXTRAS");
            double bonificaciones = rs.getDouble("BONIFICACIONES");
            double sueldoNeto = rs.getDouble("SUELDO_FINAL");
            double ivss = rs.getDouble("IVSS");
            double faov = rs.getDouble("FAOV");
            double inces = rs.getDouble("INCES");
            double salarioBase = rs.getDouble("SALARIO_BASE");

            if (rs.wasNull()) {
                diasTrabajados = 0;
                ausencias = 0;
                horasExtras = 0.0;
                bonificaciones = 0.0;
                sueldoNeto = 0.0;
                ivss = 0.0;
                faov = 0.0;
                inces = 0.0;
                salarioBase = 0.0;
            }

            model.addRow(new Object[]{
                rs.getString("NOMBRE_COMPLETO"),  // Quitamos el ID
                String.format(Locale.US, "%.2f BS", salarioBase),
                diasTrabajados,
                ausencias,
                String.format(Locale.US, "%.2f", horasExtras),
                String.format(Locale.US, "%.2f BS", bonificaciones),  // Mostramos bonificaciones
                String.format(Locale.US, "%.2f BS", ivss),
                String.format(Locale.US, "%.2f BS", faov),
                String.format(Locale.US, "%.2f BS", inces),
                String.format(Locale.US, "%.2f BS", sueldoNeto),
            });

            totalSalarioBase += salarioBase;
            totalSueldoNeto += sueldoNeto;
            totalDeducciones += (ivss + faov + inces);
            totalFAOV += faov;
            totalIVSS += ivss;
            totalInces += inces;
            totalBonificaciones += bonificaciones;
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No se encontraron datos para el período seleccionado.");
        }

        montoBaseL.setText(String.format("%.2f BS", totalSalarioBase));
        montoNetoL.setText(String.format("%.2f BS", totalSueldoNeto));
        deduccionesTotalesL.setText(String.format("%.2f BS", totalDeducciones));
        faovLabel.setText(String.format("%.2f BS", totalFAOV));
        ivssLabel.setText(String.format("%.2f BS", totalIVSS));
        incesLabel.setText(String.format("%.2f BS", totalInces));
        
        

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al calcular la nómina: " + ex.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    public void registrarPagoNomina() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaInicioStr = sdf.format(fechaInicioNom.getDate());
            String fechaFinStr = sdf.format(fechaFinNom.getDate());

            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);
            LocalDate fechaPago = LocalDate.now();

            con = ConexionBD.obtenerConexion();

            String sql = "SELECT e.ID, e.NOMBRE_COMPLETO, e.SALARIO AS SALARIO_BASE, "
                    + "COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END) AS DIAS_TRABAJADOS, "
                    + "COUNT(CASE WHEN a.ESTADO = 'Ausente' THEN 1 END) AS AUSENCIAS, "
                    + "ROUND(SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 "
                    + "THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END), 2) AS HORAS_EXTRAS, "
                    + "ROUND((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END), 2) AS SUELDO_NETO, "
                    + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.04, 2) AS IVSS, "
                    + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS FAOV, "
                    + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS INCES, "
                    + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) - "
                    + "(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.05) + "
                    + "SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 "
                    + "THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END), 2) AS SUELDO_FINAL "
                    + "FROM empleados e "
                    + "LEFT JOIN asistencias a ON e.ID = a.ID_EMPLEADO AND a.FECHA BETWEEN ? AND ? "
                    + "GROUP BY e.ID, e.NOMBRE_COMPLETO, e.SALARIO";

            pst = con.prepareStatement(sql);
            pst.setString(1, fechaInicioStr);
            pst.setString(2, fechaFinStr);
            rs = pst.executeQuery();

            String insertSql = "INSERT INTO pagos_nomina (ID_EMPLEADO, NOMBRE_COMPLETO, SALARIO_BASE, DIAS_TRABAJADOS, "
                    + "AUSENCIAS, HORAS_EXTRAS, IVSS, FAOV, INCES, SUELDO_FINAL, FECHA_INICIO_NOMINA, FECHA_FIN_NOMINA, FECHA_DE_PAGO) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement insertPst = con.prepareStatement(insertSql);

            while (rs.next()) {
                insertPst.setInt(1, rs.getInt("ID"));
                insertPst.setString(2, rs.getString("NOMBRE_COMPLETO"));
                insertPst.setDouble(3, rs.getDouble("SALARIO_BASE"));
                insertPst.setInt(4, rs.getInt("DIAS_TRABAJADOS"));
                insertPst.setInt(5, rs.getInt("AUSENCIAS"));
                insertPst.setDouble(6, rs.getDouble("HORAS_EXTRAS"));
                insertPst.setDouble(7, rs.getDouble("IVSS"));
                insertPst.setDouble(8, rs.getDouble("FAOV"));
                insertPst.setDouble(9, rs.getDouble("INCES"));
                insertPst.setDouble(10, rs.getDouble("SUELDO_FINAL"));
                insertPst.setDate(11, java.sql.Date.valueOf(fechaInicio));
                insertPst.setDate(12, java.sql.Date.valueOf(fechaFin));
                insertPst.setDate(13, java.sql.Date.valueOf(fechaPago));

                insertPst.addBatch();
            }

            insertPst.executeBatch();
            JOptionPane.showMessageDialog(null, "Registro de pagos guardado exitosamente.");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar los pagos: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void abrirVentanaPagos() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        ReportePagosDialog ventanaPagos = new ReportePagosDialog(frame);
        ventanaPagos.setVisible(true);
    }
    
    private void limpiar() {
    montoBaseL.setText("");
    montoNetoL.setText("");
    deduccionesTotalesL.setText("");
    faovLabel.setText("");
    ivssLabel.setText("");
    incesLabel.setText("");

    fechaInicioNom.setDate(null);
    fechaFinNom.setDate(null);

    DefaultTableModel modelo = (DefaultTableModel) tablaNomina.getModel();
    modelo.setRowCount(0);
}

//VENTANA DE HISTORIAL DE NOMINAS DETALLE
    
    private void detalleNomina(int idNomina) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        JDialog dialog = new JDialog();
        dialog.setTitle("Detalle de Nómina");  
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null); 
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        model.setColumnIdentifiers(new Object[] {
            "Empleado", "Días Trabajados", "Ausencias", "Horas Extras",
            "Sueldo Neto", "IVSS", "FAOV", "INCES", "Sueldo Final"
        });

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnImprimir = new JButton("Imprimir");

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idNominaSeleccionada = idNomina;  
                detalleNomina.generarDetalleNomina(idNominaSeleccionada);
            }
        });

        btnImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnImprimir);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);

        con = ConexionBD.obtenerConexion();
        String sqlFecha = "SELECT FECHA_INICIO_NOMINA, FECHA_FIN_NOMINA FROM nomina WHERE ID_NOMINA = ?";
        pst = con.prepareStatement(sqlFecha);
        pst.setInt(1, idNomina);
        rs = pst.executeQuery();

        if (rs.next()) {
            String fechaInicio = rs.getString("FECHA_INICIO_NOMINA");
            String fechaFin = rs.getString("FECHA_FIN_NOMINA");

            calcularNominaDetalles(fechaInicio, fechaFin, model);
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró la nómina seleccionada.");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al obtener los datos de la nómina: " + ex.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    
    public void calcularNominaDetalles(String fechaInicioStr, String fechaFinStr, DefaultTableModel model) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        con = ConexionBD.obtenerConexion();

        String sql = "SELECT e.NOMBRE_COMPLETO, "
                + "COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END) AS DIAS_TRABAJADOS, "
                + "COUNT(CASE WHEN a.ESTADO = 'Ausente' THEN 1 END) AS AUSENCIAS, "
                + "ROUND(SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 "
                + "THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END), 2) AS HORAS_EXTRAS, "
                + "ROUND((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END), 2) AS SUELDO_NETO, "
                + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.04, 2) AS IVSS, "
                + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS FAOV, "
                + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS INCES, "
                + "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) - "
                + "(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.05) + "
                + "SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 "
                + "THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END), 2) AS SUELDO_FINAL "
                + "FROM empleados e "
                + "LEFT JOIN asistencias a ON e.ID = a.ID_EMPLEADO AND a.FECHA BETWEEN ? AND ? "
                + "GROUP BY e.NOMBRE_COMPLETO, e.SALARIO";

        pst = con.prepareStatement(sql);
        pst.setString(1, fechaInicioStr);
        pst.setString(2, fechaFinStr);
        rs = pst.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[] {
                rs.getString("NOMBRE_COMPLETO"),
                rs.getInt("DIAS_TRABAJADOS"),
                rs.getInt("AUSENCIAS"),
                rs.getDouble("HORAS_EXTRAS"),
                rs.getDouble("SUELDO_NETO"),
                rs.getDouble("IVSS"),
                rs.getDouble("FAOV"),
                rs.getDouble("INCES"),
                rs.getDouble("SUELDO_FINAL")
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No se encontraron registros para la nómina.");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al calcular la nómina: " + ex.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    //VENTANA DE HISTORIAL DE NOMINAS DETALLE

    private void guardarNomina(double totalSueldoNeto) {
        int respuesta = JOptionPane.showConfirmDialog(
                null,
                "Una vez guardada, la nómina no podrá cambiarse. ¿Estás seguro de que deseas continuar?",
                "Advertencia",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            if (confirmarAccionConPassword.confirmarAccion(jPanel1)) {
                Connection con = null;
                PreparedStatement pst = null;
                ResultSet rs = null;

                try {
                    con = ConexionBD.obtenerConexion();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaInicio = sdf.format(fechaInicioNom.getDate());
                    String fechaFin = sdf.format(fechaFinNom.getDate());
                    String fechaPago = sdf.format(new Date());

                    String sqlEmpleados = "SELECT COUNT(*) AS total FROM empleados";
                    pst = con.prepareStatement(sqlEmpleados);
                    rs = pst.executeQuery();

                    int totalEmpleados = 0;
                    if (rs.next()) {
                        totalEmpleados = rs.getInt("total");
                    }

                    rs.close();
                    pst.close();

                    String sql = "INSERT INTO nomina (FECHA_INICIO_NOMINA, FECHA_FIN_NOMINA, FECHA_PAGO_NOMINA, TOTAL_EMPLEADOS, MONTO_TOTAL) "
                            + "VALUES (?, ?, ?, ?, ?)";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, fechaInicio);
                    pst.setString(2, fechaFin);
                    pst.setString(3, fechaPago);
                    pst.setInt(4, totalEmpleados);
                    pst.setDouble(5, totalSueldoNeto);

                    int filasAfectadas = pst.executeUpdate();

                    if (filasAfectadas > 0) {
                        JOptionPane.showMessageDialog(null, "Nómina guardada correctamente.");
                        registrarPagoNomina();
                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo guardar la nómina.");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al guardar la nómina: " + ex.getMessage());
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (pst != null) {
                            pst.close();
                        }
                        if (con != null) {
                            con.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void mostrarPagosNomina() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Registro de Pagos de Nómina");
        dialog.setSize(1200, 600);
        dialog.setLocationRelativeTo(null);

        String[] columnas = {
            "ID Pago", "Fecha Inicio", "Fecha Fin", "Fecha de Pago",
            "ID Empleado", "Nombre Completo", "Salario Base",
            "Días Trabajados", "Ausencias", "Horas Extras",
            "IVSS", "FAOV", "INCES", "Sueldo Final"
        };

        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(columnas);

        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        dialog.add(scrollPane, BorderLayout.CENTER);

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement pst = con.prepareStatement("SELECT * FROM pagos_nomina"); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("ID_PAGO"),
                    rs.getDate("FECHA_INICIO_NOMINA"),
                    rs.getDate("FECHA_FIN_NOMINA"),
                    rs.getDate("FECHA_DE_PAGO"),
                    rs.getInt("ID_EMPLEADO"),
                    rs.getString("NOMBRE_COMPLETO"),
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
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener los registros de pagos de nómina: " + e.getMessage());
        }

        dialog.setVisible(true);
    }

    public void historialNomina() {
        JFrame historialFrame = new JFrame("Historial de Nómina");
        historialFrame.setSize(800, 400);
        historialFrame.setLocationRelativeTo(null);
        historialFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTable tablaHistorialNomina = new JTable();
        JScrollPane scrollPane = new JScrollPane(tablaHistorialNomina);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btnGuardar = new JButton("Guardar");

        btnGuardar.addActionListener(e -> {
            historialNomina.generarReporteNomina();
        });

        panelBotones.add(btnGuardar);

        JButton btnImprimir = new JButton("Imprimir");
        panelBotones.add(btnImprimir);
        
JButton btnVerDetalle = new JButton("Ver detalle");
btnVerDetalle.addActionListener(e -> {
    int selectedRow = tablaHistorialNomina.getSelectedRow();

    if (selectedRow != -1) {
        int idNomina = (int) tablaHistorialNomina.getValueAt(selectedRow, 0); 

       detalleNomina ( idNomina);
    } else {
        JOptionPane.showMessageDialog(null, "Por favor, seleccione una nómina para ver los detalles.");
    }
});
    panelBotones.add(btnVerDetalle);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> historialFrame.dispose());
        panelBotones.add(btnCerrar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        historialFrame.add(panel);
        historialFrame.setVisible(true);

        cargarDatosEnTabla(tablaHistorialNomina);
    }

    private void cargarDatosEnTabla(JTable tablaHistorialNomina) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConexionBD.obtenerConexion();

            String sql = "SELECT ID_NOMINA, FECHA_INICIO_NOMINA, FECHA_FIN_NOMINA, FECHA_PAGO_NOMINA, TOTAL_EMPLEADOS, MONTO_TOTAL FROM nomina";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            model.setColumnIdentifiers(new Object[]{
                "ID Nómina", "Fecha Inicio", "Fecha Fin", "Fecha Pago",
                "Total Empleados", "Monto Total"
            });

            tablaHistorialNomina.setModel(model);

            while (rs.next()) {
                String fechaInicio = rs.getString("FECHA_INICIO_NOMINA");
                String fechaFin = rs.getString("FECHA_FIN_NOMINA");
                String fechaPago = rs.getString("FECHA_PAGO_NOMINA");

                fechaInicio = (fechaInicio != null) ? fechaInicio : "N/A";
                fechaFin = (fechaFin != null) ? fechaFin : "N/A";
                fechaPago = (fechaPago != null) ? fechaPago : "N/A";

                model.addRow(new Object[]{
                    rs.getInt("ID_NOMINA"),
                    fechaInicio,
                    fechaFin,
                    fechaPago,
                    rs.getInt("TOTAL_EMPLEADOS"),
                    String.format("%.2f BS", rs.getDouble("MONTO_TOTAL"))
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No se encontraron registros en el historial de nómina.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al mostrar el historial de nómina: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //FIN//
//OTROS
    public void calcularAportesEmpleador() {
        DefaultTableModel model = (DefaultTableModel) tablaNomina.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Primero debe calcular la nómina antes de calcular los aportes del empleador.");
            return;
        }

        double totalIVSS = 0;
        double totalFAOV = 0;
        double totalINCES = 0;

        try {
            for (int i = 0; i < model.getRowCount(); i++) {
                String salarioBaseStr = model.getValueAt(i, 2).toString()
                        .replace(" BS", "")
                        .replace(",", ".") 
                        .trim();

                double salarioBase = Double.parseDouble(salarioBaseStr);

                double ivssEmpleado = salarioBase * 0.09; 
                double faovEmpleado = salarioBase * 0.02;
                double incesEmpleado = salarioBase * 0.02; 

                totalIVSS += ivssEmpleado;
                totalFAOV += faovEmpleado;
                totalINCES += incesEmpleado;
            }

            double totalAportes = totalIVSS + totalFAOV + totalINCES;
            mostrarAportesDialog(totalIVSS, totalFAOV, totalINCES, totalAportes);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error al convertir el salario base: formato incorrecto.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al calcular los aportes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
//INSERTAR LOS APORTES EN GENERAL DEL EMPLEADOR
    private void insertarAportesEmpleador () {
    
    
    
    }
    
    //INSERTAR LOS APORTES EN GENERAL DEL EMPLEADOR
    private void mostrarAportesDialog(double ivss, double faov, double inces, double total) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Aportes del Empleador");
        dialog.setSize(350, 250);
        dialog.setLayout(new BorderLayout());

        JPanel panelDatos = new JPanel(new GridLayout(4, 1, 10, 10));
        panelDatos.add(new JLabel(String.format("IVSS (9%%): %.2f BS", ivss)));
        panelDatos.add(new JLabel(String.format("FAOV (2%%): %.2f BS", faov)));
        panelDatos.add(new JLabel(String.format("INCES (2%%): %.2f BS", inces)));
        panelDatos.add(new JLabel(String.format("Total Aportes: %.2f BS", total)));

        JButton cerrarButton = new JButton("Cerrar");
        cerrarButton.addActionListener(e -> dialog.dispose());

        dialog.add(panelDatos, BorderLayout.CENTER);
        dialog.add(cerrarButton, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    //FIN//
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        fechaInicioNom = new com.toedter.calendar.JDateChooser();
        fechaFinNom = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        tableTitle = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        montoBaseL = new javax.swing.JLabel();
        montoNetoL = new javax.swing.JLabel();
        deduccionesTotalesL = new javax.swing.JLabel();
        faovLabel = new javax.swing.JLabel();
        ivssLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        incesLabel = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaNomina = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        Bonificaciones = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(1280, 720));
        setPreferredSize(new java.awt.Dimension(1010, 400));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(1010, 864));
        jPanel1.setPreferredSize(new java.awt.Dimension(1010, 720));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Periodo de nomina"));

        jLabel6.setText("Fecha de final");

        jLabel7.setText("Fecha de inicio");

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/calculator.png"))); // NOI18N
        jButton4.setText("Calcular");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asfasfasfas.png"))); // NOI18N
        jButton1.setText("Guardar nomina");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clean.png"))); // NOI18N
        jButton3.setText("Limpiar Campos");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fechaInicioNom, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(fechaFinNom, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(46, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(54, 54, 54))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fechaInicioNom, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fechaFinNom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 610, 160));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/receipt.png"))); // NOI18N
        jButton2.setText("Generar Recibo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 610, 170, 40));

        tableTitle.setText("Tabla de nómina");
        jPanel1.add(tableTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, -1, -1));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Totales"));

        jLabel2.setText("Monto neto total:");

        jLabel3.setText("Deducciones totales:");

        jLabel4.setText("FAOV (1%): ");

        jLabel5.setText("IVSS (4%):");

        jLabel9.setText("Monto base total:");

        montoBaseL.setText("Total");

        montoNetoL.setText("Total");

        deduccionesTotalesL.setText("Total");

        faovLabel.setText("Total");

        ivssLabel.setText("Total");

        jLabel8.setText("INCES");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(incesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(24, 24, 24))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(montoBaseL, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addGap(37, 37, 37)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ivssLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                                    .addComponent(faovLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(montoNetoL, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                                    .addComponent(deduccionesTotalesL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 15, Short.MAX_VALUE)))
                        .addGap(27, 27, 27))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(montoBaseL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(montoNetoL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(deduccionesTotalesL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(faovLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(ivssLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(incesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(6, 6, 6))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 50, 270, 170));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clock.png"))); // NOI18N
        jButton5.setText("Historial de nomina");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 610, -1, 40));
        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 360, -1, -1));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        tablaNomina.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Salario Base", "Días Trabajados", "Ausencias", "Horas Extras", "IVSS", "FAOV", "INCES", "Salario Neto"
            }
        ));
        jScrollPane1.setViewportView(tablaNomina);

        jScrollPane2.setViewportView(jScrollPane1);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 950, 280));

        jButton8.setText("Ver aportes de empleador");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 230, 270, 30));

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/historial-de-transacciones.png"))); // NOI18N
        jButton11.setText("Historial pagos");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 610, 170, 40));

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/despido.png"))); // NOI18N
        jButton9.setText("Liquidación");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 610, 170, 40));

        Bonificaciones.setText("Bonificaciones");
        Bonificaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BonificacionesActionPerformed(evt);
            }
        });
        jPanel1.add(Bonificaciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 610, 160, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1088, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(192, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        int filaSeleccionada = tablaNomina.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado de la tabla.");
            return;
        }

        int idEmpleado = (int) tablaNomina.getValueAt(filaSeleccionada, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicio = sdf.format(fechaInicioNom.getDate());
        String fechaFin = sdf.format(fechaFinNom.getDate());

        if (fechaInicio == null || fechaFin == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un período válido.");
            return;
        }

        recibodePago.generarRecibo(idEmpleado, fechaInicio, fechaFin);
// TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        guardarNomina(totalSueldoNeto);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:

        historialNomina();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        calcularNomina();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:

        calcularAportesEmpleador();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        abrirVentanaPagos();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
limpiar();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
LiquidacionesDialog liquidacionesDialog = 
        new LiquidacionesDialog ((JFrame) SwingUtilities.getWindowAncestor(this));
    liquidacionesDialog.setVisible(true);          
    }//GEN-LAST:event_jButton9ActionPerformed

    private void BonificacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BonificacionesActionPerformed
     BonificacionesDialog dialog = new BonificacionesDialog((JFrame) SwingUtilities.getWindowAncestor(this));
    dialog.setVisible(true);
    }//GEN-LAST:event_BonificacionesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Bonificaciones;
    private javax.swing.JLabel deduccionesTotalesL;
    private javax.swing.JLabel faovLabel;
    private com.toedter.calendar.JDateChooser fechaFinNom;
    private com.toedter.calendar.JDateChooser fechaInicioNom;
    private javax.swing.JLabel incesLabel;
    private javax.swing.JLabel ivssLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel montoBaseL;
    private javax.swing.JLabel montoNetoL;
    private javax.swing.JTable tablaNomina;
    private javax.swing.JLabel tableTitle;
    // End of variables declaration//GEN-END:variables
}
