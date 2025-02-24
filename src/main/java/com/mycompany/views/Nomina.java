/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.views;

import com.mycompany.ConexionBD;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;






/**
 *
 * 
 */
public class Nomina extends javax.swing.JPanel {

    /**
     * Creates new form Nomina
     */
    public Nomina() {
        initComponents();
        styles ();
    }
    
    //ESTILOS
    
     private void  styles () {
    
    tableTitle.setFont( UIManager.getFont( "h1.font" ) );
    
    }
    
 //OBTENER DATOS NOMINA, METODO EN PROCESO
    



    private double totalSueldoNeto = 0;




public boolean validarFechasSolapadas(LocalDate fechaInicio, LocalDate fechaFin) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        con = ConexionBD.obtenerConexion();

        // Convertimos las fechas a String para la consulta
        String fechaInicioStr = fechaInicio.toString();  // "yyyy-MM-dd"
        String fechaFinStr = fechaFin.toString();        // "yyyy-MM-dd"

        // Consulta para verificar solapamientos de fechas de nómina
        String sql = "SELECT COUNT(*) FROM nomina WHERE (" +
                     "(? BETWEEN FECHA_INICIO_NOMINA AND FECHA_FIN_NOMINA) OR " +    // Fecha inicio dentro del rango
                     "(? BETWEEN FECHA_INICIO_NOMINA AND FECHA_FIN_NOMINA) OR " +    // Fecha fin dentro del rango
                     "(FECHA_INICIO_NOMINA BETWEEN ? AND ?) OR " +            // Rango de la nómina dentro de las fechas ingresadas
                     "(FECHA_FIN_NOMINA BETWEEN ? AND ?))";                   // Rango de la nómina dentro de las fechas ingresadas

        pst = con.prepareStatement(sql);
        pst.setString(1, fechaInicioStr);
        pst.setString(2, fechaFinStr);
        pst.setString(3, fechaInicioStr);
        pst.setString(4, fechaFinStr);
        pst.setString(5, fechaInicioStr);
        pst.setString(6, fechaFinStr);
      

        rs = pst.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null, "Las fechas seleccionadas se solapan con una nómina existente.");
            return true; // Si hay solapamiento, retornar verdadero
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al verificar solapamiento de fechas: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return false; // Si no hay solapamiento, retornar falso
}

public void calcularNomina() {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        // Obtener las fechas de inicio y fin de los JDateChooser
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicioStr = sdf.format(fechaInicioNom.getDate());
        String fechaFinStr = sdf.format(fechaFinNom.getDate());

        // Convertir las fechas a LocalDate para validarlas
        LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
        LocalDate fechaFin = LocalDate.parse(fechaFinStr);

        // Validar que la diferencia entre las fechas sea entre 15 y 31 días
        long diasDeDiferencia = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (diasDeDiferencia < 15) {
            JOptionPane.showMessageDialog(null, "El período de la nómina debe ser de al menos 15 días.");
            return; // Salir si la validación falla
        }
        if (diasDeDiferencia > 31) {
            JOptionPane.showMessageDialog(null, "El período de la nómina no puede exceder los 31 días.");
            return; // Salir si la validación falla
        }

        // Validar si las fechas se solapan con otras nóminas
        if (validarFechasSolapadas(fechaInicio, fechaFin)) {
            return; // Salir si la validación de solapamiento falla
        }

        // Continuar con el cálculo de la nómina si las validaciones pasan
        con = ConexionBD.obtenerConexion();

        String sql = "SELECT e.ID, e.NOMBRE_COMPLETO, e.SALARIO AS SALARIO_BASE, " +
                     "COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END) AS DIAS_TRABAJADOS, " +
                     "COUNT(CASE WHEN a.ESTADO = 'Ausente' THEN 1 END) AS AUSENCIAS, " +
                     "ROUND(SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 " +
                     "THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END), 2) AS HORAS_EXTRAS, " +
                     "ROUND((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END), 2) AS SUELDO_NETO, " +
                     "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.04, 2) AS IVSS, " +
                     "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS FAOV, " +
                     "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS INCES, " +
                     "ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) - " +
                     "(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.05) + " +
                     "SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 " +
                     "THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END), 2) AS SUELDO_FINAL " +
                     "FROM empleados e " +
                     "LEFT JOIN asistencias a ON e.ID = a.ID_EMPLEADO AND a.FECHA BETWEEN ? AND ? " +
                     "GROUP BY e.ID, e.NOMBRE_COMPLETO, e.SALARIO";

        pst = con.prepareStatement(sql);
        pst.setString(1, fechaInicioStr);
        pst.setString(2, fechaFinStr);
        rs = pst.executeQuery();

        // Definir el modelo de la tabla con las columnas correctas
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
            "ID", "Nombre", "Salario Base", "Días Trabajados", "Ausencias", 
            "Horas Extras", "Sueldo Neto", "IVSS", "FAOV", "INCES"
        });

        // Asignar modelo a la tabla
        tablaNomina.setModel(model);

        // Variables para los totales
        double totalSalarioBase = 0;  // Nuevo total agregado
        totalSueldoNeto = 0;  // Reiniciar la variable global
        double totalDeducciones = 0;
        double totalFAOV = 0;
        double totalIVSS = 0;
        double totalInces = 0;

        // Llenar la tabla con los datos
        while (rs.next()) {
            int diasTrabajados = rs.getInt("DIAS_TRABAJADOS");
            int ausencias = rs.getInt("AUSENCIAS");
            double horasExtras = rs.getDouble("HORAS_EXTRAS");
            double sueldoNeto = rs.getDouble("SUELDO_FINAL");
            double ivss = rs.getDouble("IVSS");
            double faov = rs.getDouble("FAOV");
            double inces = rs.getDouble("INCES");
            double salarioBase = rs.getDouble("SALARIO_BASE");

            // Manejar valores NULL reemplazándolos por 0
            if (rs.wasNull()) {
                diasTrabajados = 0;
                ausencias = 0;
                horasExtras = 0.0;
                sueldoNeto = 0.0;
                ivss = 0.0;
                faov = 0.0;
                inces = 0.0;
                salarioBase = 0.0;
            }

            // Agregar datos a la tabla con formato "BS"
            model.addRow(new Object[] {
                rs.getInt("ID"),
                rs.getString("NOMBRE_COMPLETO"),
                String.format("%.2f BS", salarioBase),
                diasTrabajados,
                ausencias,
                String.format("%.2f", horasExtras),
                String.format("%.2f BS", sueldoNeto),
                String.format("%.2f BS", ivss),
                String.format("%.2f BS", faov),
                String.format("%.2f BS", inces)
            });

            // Acumular totales
            totalSalarioBase += salarioBase;  // Acumulando salarios base
            totalSueldoNeto += sueldoNeto;
            totalDeducciones += (ivss + faov + inces);
            totalFAOV += faov;
            totalIVSS += ivss;
            totalInces += inces;
        }

        // Si la tabla está vacía, mostrar un mensaje
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No se encontraron datos para el período seleccionado.");
        }

        // Actualizar labels con el formato "BS"
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




private void guardarNomina(double totalSueldoNeto) {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        con = ConexionBD.obtenerConexion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicio = sdf.format(fechaInicioNom.getDate());
        String fechaFin = sdf.format(fechaFinNom.getDate());

        // Obtener la cantidad total de empleados registrados en la base de datos
        String sqlEmpleados = "SELECT COUNT(*) AS total FROM empleados";
        pst = con.prepareStatement(sqlEmpleados);
        rs = pst.executeQuery();
        
        int totalEmpleados = 0;
        if (rs.next()) {
            totalEmpleados = rs.getInt("total");
        }

        rs.close();
        pst.close();

        // Insertar la nómina en la tabla "nominas"
        String sql = "INSERT INTO nomina (FECHA_INICIO_NOMINA, FECHA_FIN_NOMINA, TOTAL_EMPLEADOS, MONTO_TOTAL) " +
                     "VALUES (?, ?, ?, ?)";
        pst = con.prepareStatement(sql);
        pst.setString(1, fechaInicio);
        pst.setString(2, fechaFin);
        pst.setInt(3, totalEmpleados);
        pst.setDouble(4, totalSueldoNeto);

        int filasAfectadas = pst.executeUpdate();
        
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(null, "Nómina guardada correctamente.");
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo guardar la nómina.");
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al guardar la nómina: " + ex.getMessage());
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


public void historialNomina() {
    // Crear una nueva ventana (JFrame) para mostrar el historial
    JFrame historialFrame = new JFrame("Historial de Nómina");
    historialFrame.setSize(800, 400);
    historialFrame.setLocationRelativeTo(null); // Centrar la ventana
    historialFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cerrar solo la ventana de historial

    // Crear el panel para el historial
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    // Crear la tabla para mostrar los datos de la nómina
    JTable tablaHistorialNomina = new JTable();
    JScrollPane scrollPane = new JScrollPane(tablaHistorialNomina);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Crear el botón para cerrar la ventana
    JButton btnCerrar = new JButton("Cerrar");
    btnCerrar.addActionListener(e -> historialFrame.dispose()); // Al hacer clic en cerrar, se cierra la ventana
    panel.add(btnCerrar, BorderLayout.SOUTH);

    // Agregar el panel al JFrame
    historialFrame.add(panel);

    // Mostrar la ventana
    historialFrame.setVisible(true);

    // Ahora obtenemos los registros y los agregamos a la tabla
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        // Establecer la conexión
        con = ConexionBD.obtenerConexion();
        
        // Consulta para obtener los registros de la tabla nomina
        String sql = "SELECT * FROM nomina";
        pst = con.prepareStatement(sql);
        rs = pst.executeQuery();

        // Definir el modelo de la tabla con las columnas correctas
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
            "ID Nomina", "Fecha Inicio", "Fecha Fin", "Fecha Pago", 
            "Total Empleados", "Monto Total"
        });

        // Asignar el modelo de tabla
        tablaHistorialNomina.setModel(model);

        // Recorrer los registros y agregarlos a la tabla
        while (rs.next()) {
            // Recuperar los valores directamente
            String fechaInicio = rs.getString("FECHA_INICIO_NOMINA");
            String fechaFin = rs.getString("FECHA_FIN_NOMINA");
            String fechaPago = rs.getString("FECHA_PAGO_NOMINA");

            // Si alguna de las fechas es null, mostrar "N/A"
            if (fechaInicio == null) fechaInicio = "N/A";
            if (fechaFin == null) fechaFin = "N/A";
            if (fechaPago == null) fechaPago = "N/A";

            // Agregar los datos a la tabla
            model.addRow(new Object[]{
                rs.getInt("ID_NOMINA"),
                fechaInicio,
                fechaFin,
                fechaPago,
                rs.getInt("TOTAL_EMPLEADOS"),
                String.format("%.2f BS", rs.getDouble("MONTO_TOTAL"))
            });
        }

        // Verificar si la tabla está vacía
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No se encontraron registros en el historial de nómina.");
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al mostrar el historial de nómina: " + ex.getMessage());
    } finally {
        try {
            // Cerrar los recursos
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



public void calcularAportesEmpleador() {
    // Verificar si la nómina ha sido calculada previamente
    if (totalSueldoNeto == 0) {
        JOptionPane.showMessageDialog(null, "Primero debe calcular la nómina antes de calcular los aportes del empleador.");
        return;
    }

    // Inicializar los totales de los aportes
    double totalIVSS = 0;
    double totalFAOV = 0;
    double totalINCES = 0;
    
    // Sumar los aportes individuales de cada empleado
    DefaultTableModel model = (DefaultTableModel) tablaNomina.getModel();
    for (int i = 0; i < model.getRowCount(); i++) {
        double salarioBase = Double.parseDouble(model.getValueAt(i, 2).toString().replace(" BS", "").trim());
        
        // Calcular los aportes individuales por empleado
        double ivssEmpleado = salarioBase * 0.09;  // 9%
        double faovEmpleado = salarioBase * 0.02;  // 2%
        double incesEmpleado = salarioBase * 0.02; // 2%

        // Sumar a los totales
        totalIVSS += ivssEmpleado;
        totalFAOV += faovEmpleado;
        totalINCES += incesEmpleado;
    }

    // Calcular el total de los aportes
    double totalAportes = totalIVSS + totalFAOV + totalINCES;

    // Mostrar los resultados en un JDialog
    mostrarAportesDialog(totalIVSS, totalFAOV, totalINCES, totalAportes);
}


private void mostrarAportesDialog(double ivss, double faov, double inces, double total) {
    JDialog dialog = new JDialog();
    dialog.setTitle("Aportes del Empleador");
    dialog.setSize(350, 250);
    dialog.setLayout(new GridLayout(5, 1));

    JLabel ivssLabel = new JLabel(String.format("IVSS (9%%): %.2f BS", ivss));
    JLabel faovLabel = new JLabel(String.format("FAOV (2%%): %.2f BS", faov));
    JLabel incesLabel = new JLabel(String.format("INCES (2%%): %.2f BS", inces));
    JLabel totalLabel = new JLabel(String.format("Total Aportes: %.2f BS", total));

    JButton cerrarButton = new JButton("Cerrar");
    cerrarButton.addActionListener(e -> dialog.dispose());

    dialog.add(ivssLabel);
    dialog.add(faovLabel);
    dialog.add(incesLabel);
    dialog.add(totalLabel);
    dialog.add(cerrarButton);

    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
}

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
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
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
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaNomina = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(1280, 720));
        setPreferredSize(new java.awt.Dimension(1010, 400));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(1010, 864));
        jPanel1.setPreferredSize(new java.awt.Dimension(1010, 720));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Periodo de nomina"));

        jLabel6.setText("Fecha de final");

        jLabel7.setText("Fecha de inicio");

        jButton4.setText("Calcular");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton1.setText("Guardar nomina");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(26, 26, 26)
                                    .addComponent(jButton1))
                                .addComponent(fechaFinNom, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(46, Short.MAX_VALUE))
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
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 610, 160));

        jButton2.setText("Generar Recibo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 670, 120, 40));

        jButton3.setText("Generar reporte");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 670, 130, 40));

        tableTitle.setText("Empleados");
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
                                .addGap(98, 98, 98)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(faovLabel)
                                    .addComponent(ivssLabel))
                                .addGap(0, 35, Short.MAX_VALUE)))
                        .addGap(24, 24, 24))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(montoNetoL, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(montoBaseL, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deduccionesTotalesL))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(montoBaseL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(montoNetoL, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(deduccionesTotalesL))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(faovLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(46, 46, 46)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(incesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addComponent(ivssLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 50, 270, 170));

        jButton5.setText("Historial de nomina");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 670, 140, 40));

        jButton6.setText("Liquidación");
        jPanel1.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 670, 120, 40));

        jButton7.setText("Vacaciones");
        jPanel1.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 670, 110, 40));
        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 360, -1, -1));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        tablaNomina.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaNomina);

        jScrollPane2.setViewportView(jScrollPane1);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, 910, 330));

        jButton8.setText("Ver aportes de empleador");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 230, 270, 30));

        jButton9.setText("Leyenda");
        jPanel1.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 670, 120, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1088, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel deduccionesTotalesL;
    private javax.swing.JLabel faovLabel;
    private com.toedter.calendar.JDateChooser fechaFinNom;
    private com.toedter.calendar.JDateChooser fechaInicioNom;
    private javax.swing.JLabel incesLabel;
    private javax.swing.JLabel ivssLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
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
