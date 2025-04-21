package com.mycompany.reportes;

import com.mycompany.ConexionBD;
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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class detallesNomina extends javax.swing.JPanel implements Printable {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public detallesNomina() {
        initComponents();
        configurarTabla();
    }

    private void configurarTabla() {
        jTable1.setModel(model);
        model.setColumnIdentifiers(new Object[]{
            "Empleado", "Días Trabajados", "Ausencias", "Horas Extras",
            "Sueldo Neto", "IVSS", "FAOV", "INCES", "Sueldo Final"
        });
    }

  public void cargarDatosNomina(int idNomina, String fechaInicioStr, String fechaFinStr) {
        // Establecer fechas en los labels
        fechaInicio.setText(fechaInicioStr);
        fechaFin.setText(fechaFinStr);

        // Limpiar tabla
        model.setRowCount(0);

        // Obtener datos de la base de datos usando la consulta original
        String sqlDetalle = "SELECT " +
                                "   e.NOMBRE_COMPLETO, " +
                                "   COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END) AS DIAS_TRABAJADOS, " +
                                "   COUNT(CASE WHEN a.ESTADO = 'Ausente' THEN 1 END) AS AUSENCIAS, " +
                                "   ROUND(SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 " +
                                "   THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END), 2) AS HORAS_EXTRAS, " +
                                "   ROUND((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END), 2) AS SUELDO_NETO, " +
                                "   ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.04, 2) AS IVSS, " +
                                "   ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS FAOV, " +
                                "   ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.01, 2) AS INCES, " +
                                "   ROUND(((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) - " +
                                "   (((e.SALARIO / 30) * COUNT(CASE WHEN a.ESTADO = 'Presente' THEN 1 END)) * 0.06) + " +
                                "   SUM(CASE WHEN (strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 > 8 " +
                                "   THEN ((strftime('%s', a.HORA_SALIDA) - strftime('%s', a.HORA_ENTRADA)) / 3600 - 8) * (e.SALARIO / 30 / 8 * 1.5) ELSE 0 END), 2) AS SUELDO_FINAL, " +
                                "   e.SALARIO " + // Necesitamos el salario individual para mostrarlo
                                "FROM empleados e " +
                                "LEFT JOIN asistencias a ON e.ID = a.ID_EMPLEADO AND a.FECHA BETWEEN ? AND ? " +
                                "GROUP BY e.NOMBRE_COMPLETO, e.SALARIO";

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sqlDetalle)) {

            pstmt.setString(1, fechaInicioStr);
            pstmt.setString(2, fechaFinStr);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("NOMBRE_COMPLETO"),
                    rs.getInt("DIAS_TRABAJADOS"),
                    rs.getInt("AUSENCIAS"),
                    String.format("%.2f", rs.getDouble("HORAS_EXTRAS")),
                    String.format("%.2f", rs.getDouble("SALARIO")), // Mostrar el salario del empleado
                    String.format("%.2f", rs.getDouble("IVSS")),
                    String.format("%.2f", rs.getDouble("FAOV")),
                    String.format("%.2f", rs.getDouble("INCES")),
                    String.format("%.2f", rs.getDouble("SUELDO_FINAL"))
                };
                model.addRow(row);
            }

            // Calcular totales y mostrarlos en los labels
      

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        pf.setOrientation(PageFormat.LANDSCAPE);

        double scaleX = pf.getImageableWidth() / this.getWidth();
        double scaleY = pf.getImageableHeight() / this.getHeight();
        double scale = Math.min(scaleX, scaleY) * 0.95;
        g2d.scale(scale, scale);

        this.printAll(g2d);
        return PAGE_EXISTS;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        fechaInicio = new javax.swing.JLabel();
        fechaFin = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Reporte perteneciente a la Comercializadora Santa Barbara®");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Dirección: Sector Delicias Nuevas, Calle Principal de la Chile, parroquia Ambrosio");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("TLF: 0410-0000000");

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logo.jpg"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setText("REPORTE DE NÓMINA");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("Fecha de Inicio:");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("Fecha de Fin:");

        fechaInicio.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        fechaInicio.setText("XX-XX-XXXX");

        fechaFin.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        fechaFin.setText("XX-XX-XXXX");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("XXXXXXXXX");

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

        jLabel11.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Reporte perteneciente a la Comercializadora Santa Barbara®");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Dirección: Sector Delicias Nuevas, Calle Principal de la Chile, parroquia Ambrosio");

        jLabel13.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("TLF: 0410-0000000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(218, 218, 218)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58))
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(268, 268, 268))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(312, 312, 312)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fechaFin))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fechaInicio))
                                    .addComponent(jLabel2)))
                            .addComponent(jLabel6)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 994, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(fechaInicio)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(fechaFin))
                .addGap(59, 59, 59)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fechaFin;
    private javax.swing.JLabel fechaInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
