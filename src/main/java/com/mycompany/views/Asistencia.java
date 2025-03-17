/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalTime;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.mycompany.ConexionBD;
import javax.swing.JDialog;
import javax.swing.UIManager;
import com.mycompany.RoundedPanel;
import com.mycompany.VerAsistencias;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *PENDIENTE A RE DISEÑO Y RECONSTRUCCION
 *
 */
public class Asistencia extends javax.swing.JPanel {

    /**
     * Creates new form Asistencia
     */
    public Asistencia() {
        initComponents();
        model = (DefaultTableModel) this.jTable1.getModel();
        cargarDatosAsistenciasEnTabla();
        styles();
        
        java.time.LocalDate fechaActual = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaFormateada = fechaActual.format(formatter);

        fechaLabel.setText(fechaFormateada);
        
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);
        jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(0);        

    }
    DefaultTableModel model;
    
    
     private void  styles () {
    
    tablaTitle.setFont( UIManager.getFont( "h1.font" ) );
    

    }

    //FUNCIONES PARA CARGAR LOS DATOS
    
   private void cargarDatosAsistenciasEnTabla() {
    String fechaActual = java.time.LocalDate.now().toString();

    String sql = "SELECT e.NOMBRE_COMPLETO, a.FECHA, a.HORA_ENTRADA, a.HORA_SALIDA, a.ESTADO, a.OBSERVACIONES "
               + "FROM asistencias a "
               + "JOIN empleados e ON a.ID_EMPLEADO = e.ID "
               + "WHERE a.FECHA = ?";

    try (Connection conn = ConexionBD.obtenerConexion(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, fechaActual);

        try (ResultSet rs = pstmt.executeQuery()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            model.setRowCount(0);  
            model.setColumnCount(0);

            model.addColumn("Nombre Empleado");
            model.addColumn("Fecha");
            model.addColumn("Hora de Entrada");
            model.addColumn("Hora de Salida");
            model.addColumn("Estado");
            model.addColumn("Observaciones");

    while (rs.next()) {
        Object[] rowData = new Object[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            Object value = rs.getObject(i);
            if (i == 3 || i == 4) { // Columnas HORA_ENTRADA y HORA_SALIDA
                if (value == null) {
                    rowData[i - 1] = null;
                    continue;
                }
                LocalTime time = null;
                if (value instanceof java.sql.Time) {
                    time = ((java.sql.Time) value).toLocalTime();
                } else if (value instanceof String) {
                    try {
                        time = LocalTime.parse((String) value);
                    } catch (Exception e) {
                    }
                }
                if (time != null) {
                    rowData[i - 1] = String.format("%02d:%02d", time.getHour(), time.getMinute());
                } else {
                    rowData[i - 1] = value.toString();
                }
            } else {
                rowData[i - 1] = value;
            }
        }
        model.addRow(rowData);
    }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar las asistencias: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


  private void actualizarTabla() {
    String fechaActual = java.time.LocalDate.now().toString();

    String query = "SELECT e.NOMBRE_COMPLETO, a.FECHA, a.HORA_ENTRADA, a.HORA_SALIDA, a.ESTADO, a.OBSERVACIONES "
                 + "FROM asistencias a "
                 + "JOIN empleados e ON a.ID_EMPLEADO = e.ID "
                 + "WHERE a.FECHA = ? "
                 + "ORDER BY a.HORA_ENTRADA DESC";

    try (Connection con = ConexionBD.obtenerConexion(); 
         PreparedStatement stmt = con.prepareStatement(query)) {

        stmt.setString(1, fechaActual);

        try (ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

           

    while (rs.next()) {
        Object[] rowData = new Object[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            Object value = rs.getObject(i);
            if (i == 3 || i == 4) {
                if (value == null) {
                    rowData[i - 1] = null;
                    continue;
                }
                LocalTime time = null;
                if (value instanceof java.sql.Time) {
                    time = ((java.sql.Time) value).toLocalTime();
                } else if (value instanceof String) {
                    try {
                        time = LocalTime.parse((String) value);
                    } catch (Exception e) {
                    }
                }
                if (time != null) {
                    rowData[i - 1] = String.format("%02d:%02d", time.getHour(), time.getMinute());
                } else {
                    rowData[i - 1] = value.toString();
                }
            } else {
                rowData[i - 1] = value;
            }
        }
        model.addRow(rowData);
    }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar la tabla: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    
   //FIN//
    
    
     public void abrirVentanaAsistencia() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        VerAsistencias verAsistencias = new VerAsistencias(frame);
        verAsistencias.setVisible(true);
    }

    //VERIFICACIONES
    private boolean verificarRegistroExistente(int idEmpleado) {
        String query = "SELECT COUNT(*) FROM asistencias WHERE ID_EMPLEADO = ? AND FECHA = date('now') AND HORA_SALIDA IS NULL";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, idEmpleado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar registro existente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private boolean verificarSalidaRegistrada(int idEmpleado) {
        String sql = "SELECT 1 FROM asistencias WHERE ID_EMPLEADO = ? AND FECHA = date('now') AND HORA_SALIDA IS NOT NULL";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idEmpleado);

            ResultSet rs = pstmt.executeQuery();
            return rs.next(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar el registro de salida: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
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
        jPanel4 = new javax.swing.JPanel();
        reporteAsistencias = new javax.swing.JButton();
        historialAsistencias = new javax.swing.JButton();
        jPanel2 = new RoundedPanel(20);
        vacacionesButton = new javax.swing.JButton();
        incapacidadButton = new javax.swing.JButton();
        permisoButton = new javax.swing.JButton();
        fechaLabel = new javax.swing.JLabel();
        jPanel3 = new RoundedPanel(20);
        tablaTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setBackground(new java.awt.Color(248, 247, 247));
        setMaximumSize(new java.awt.Dimension(1010, 720));
        setMinimumSize(new java.awt.Dimension(1010, 720));

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));
        jPanel1.setAutoscrolls(true);
        jPanel1.setMinimumSize(new java.awt.Dimension(1010, 720));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Más acciones"));
        jPanel4.setToolTipText("");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 165, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 206, Short.MAX_VALUE)
        );

        reporteAsistencias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/reporte.png"))); // NOI18N
        reporteAsistencias.setText("Reporte de Asistencias");
        reporteAsistencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reporteAsistenciasActionPerformed(evt);
            }
        });

        historialAsistencias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/historial.png"))); // NOI18N
        historialAsistencias.setText("Historial de Asistencias");
        historialAsistencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historialAsistenciasActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        vacacionesButton.setBackground(new java.awt.Color(239, 246, 255));
        vacacionesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vacaciones.png"))); // NOI18N
        vacacionesButton.setText("Vacaciones");
        vacacionesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vacacionesButtonActionPerformed(evt);
            }
        });

        incapacidadButton.setBackground(new java.awt.Color(240, 253, 244));
        incapacidadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/incapacidad1.png"))); // NOI18N
        incapacidadButton.setText("Incapacidad");

        permisoButton.setBackground(new java.awt.Color(250, 245, 255));
        permisoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/permiso1.png"))); // NOI18N
        permisoButton.setText("Permiso");
        permisoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                permisoButtonActionPerformed(evt);
            }
        });

        fechaLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        fechaLabel.setForeground(new java.awt.Color(102, 102, 102));
        fechaLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/calendario.png"))); // NOI18N
        fechaLabel.setText("jLabel1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(fechaLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 396, Short.MAX_VALUE)
                .addComponent(vacacionesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(incapacidadButton)
                .addGap(18, 18, 18)
                .addComponent(permisoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(incapacidadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(permisoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vacacionesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fechaLabel))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tablaTitle.setText("ASISTENCIA DE HOY:");

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setShowGrid(true);
        jTable1.setSurrendersFocusOnKeystroke(true);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablaTitle)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 983, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 15, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tablaTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(316, 316, 316)
                .addComponent(reporteAsistencias)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(historialAsistencias)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reporteAsistencias)
                    .addComponent(historialAsistencias))
                .addGap(179, 179, 179))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void permisoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_permisoButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_permisoButtonActionPerformed

    private void vacacionesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vacacionesButtonActionPerformed
        // TODO add your handling code here:
        Vacaciones vacacionesPanel = new Vacaciones();
        JDialog dialog = new JDialog();
        dialog.setTitle("Registrar Vacaciones");
        dialog.setModal(true);
        dialog.getContentPane().add(vacacionesPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    
 cargarDatosAsistenciasEnTabla();
        
    }//GEN-LAST:event_vacacionesButtonActionPerformed

    private void reporteAsistenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reporteAsistenciasActionPerformed
     
    }//GEN-LAST:event_reporteAsistenciasActionPerformed

    private void historialAsistenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historialAsistenciasActionPerformed
abrirVentanaAsistencia();

        //
        // TODO add your handling code here:
    }//GEN-LAST:event_historialAsistenciasActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fechaLabel;
    private javax.swing.JButton historialAsistencias;
    private javax.swing.JButton incapacidadButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton permisoButton;
    private javax.swing.JButton reporteAsistencias;
    private javax.swing.JLabel tablaTitle;
    private javax.swing.JButton vacacionesButton;
    // End of variables declaration//GEN-END:variables
}
