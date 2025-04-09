
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.reportes;

import com.mycompany.ConexionBD;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;



public class historialNomina extends javax.swing.JPanel implements Printable {

    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public historialNomina() {
        initComponents();
        cargarDatosEnTabla();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        fechaLabel.setText(sdf.format(new Date()));
    }

    private void cargarDatosEnTabla() {
        String sql = "SELECT ID_NOMINA, FECHA_INICIO_NOMINA, FECHA_FIN_NOMINA, FECHA_PAGO_NOMINA, TOTAL_EMPLEADOS, MONTO_TOTAL FROM nomina";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            String[] columnNames = {
                "ID Nómina", 
                "Fecha Inicio", 
                "Fecha Fin", 
                "Fecha Pago",
                "Total Empleados", 
                "Monto Total"
            };

            model.setRowCount(0);
            model.setColumnIdentifiers(columnNames);

            while (rs.next()) {
                Object[] rowData = new Object[columnNames.length];
                rowData[0] = rs.getInt("ID_NOMINA");
                
                // Formatear fechas
                rowData[1] = rs.getDate("FECHA_INICIO_NOMINA") != null ? 
                    new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("FECHA_INICIO_NOMINA")) : "N/A";
                rowData[2] = rs.getDate("FECHA_FIN_NOMINA") != null ? 
                    new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("FECHA_FIN_NOMINA")) : "N/A";
                rowData[3] = rs.getDate("FECHA_PAGO_NOMINA") != null ? 
                    new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("FECHA_PAGO_NOMINA")) : "N/A";
                
                rowData[4] = rs.getInt("TOTAL_EMPLEADOS");
                rowData[5] = String.format("%,.2f BS", rs.getDouble("MONTO_TOTAL"));
                
                model.addRow(rowData);
            }

            jTable1.setModel(model);

            // Ajustar anchos de columnas
            int[] columnWidths = {80, 90, 90, 90, 100, 100};
            for (int i = 0; i < columnWidths.length; i++) {
                TableColumn column = jTable1.getColumnModel().getColumn(i);
                column.setPreferredWidth(columnWidths[i]);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar los datos: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // Configurar orientación horizontal para mejor visualización
        pf.setOrientation(PageFormat.LANDSCAPE);

        // Escalar el contenido para que quepa en la hoja
        double panelWidth = this.getWidth();
        double panelHeight = this.getHeight();
        double printableWidth = pf.getImageableWidth();
        double printableHeight = pf.getImageableHeight();

        double scaleX = printableWidth / panelWidth;
        double scaleY = printableHeight / panelHeight;
        double scale = Math.min(scaleX, scaleY); // Mantener proporción

        g2d.scale(scale, scale);

        // Dibujar título
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String title = "Historial de Nómina - " + fechaLabel.getText();
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (int)((panelWidth - titleWidth)/2), 20);

        // Dibujar la tabla desplazada hacia abajo
        g2d.translate(0, 30);
        jTable1.print(g2d);

        return PAGE_EXISTS;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        fechaLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logo.jpg"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("HISTORIAL DE NÓMINAS");

        jTable1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
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
        jScrollPane1.setViewportView(jTable1);

        fechaLabel.setText("jLabel3");

        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setText("FECHA DE CONSULTA");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(183, 183, 183)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 808, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fechaLabel)))))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fechaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(235, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fechaLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
