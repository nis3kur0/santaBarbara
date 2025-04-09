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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    // Formateador para mostrar fechas
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public historialNomina() {
        initComponents();
        cargarDatosEnTabla();
        fechaLabel.setText(DATE_FORMAT.format(new java.util.Date()));
    }

    private void cargarDatosEnTabla() {
        String sql = "SELECT ID_NOMINA, FECHA_INICIO_NOMINA, FECHA_FIN_NOMINA, FECHA_PAGO_NOMINA, "
                   + "TOTAL_EMPLEADOS, MONTO_TOTAL FROM nomina";

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
                
                rowData[1] = parseDateTime(rs, "FECHA_INICIO_NOMINA");
                rowData[2] = parseDateTime(rs, "FECHA_FIN_NOMINA");
                rowData[3] = parseDateTime(rs, "FECHA_PAGO_NOMINA");
                
                rowData[4] = rs.getInt("TOTAL_EMPLEADOS");
                
                rowData[5] = String.format("%.2f", rs.getDouble("MONTO_TOTAL"));
                
                model.addRow(rowData);
            }

            jTable1.setModel(model);

            int[] columnWidths = {80, 120, 120, 120, 100, 100};
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

    private String parseDateTime(ResultSet rs, String columnName) throws SQLException {
        try {
            java.sql.Date date = rs.getDate(columnName);
            if (date != null) {
                return DATE_FORMAT.format(date);
            }
            
            Timestamp timestamp = rs.getTimestamp(columnName);
            if (timestamp != null) {
                return DATE_FORMAT.format(new java.util.Date(timestamp.getTime()));
            }
            
            return "N/A";
        } catch (SQLException e) {
            return rs.getString(columnName);
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

    double panelWidth = this.getWidth();
    double panelHeight = this.getHeight();
    double printableWidth = pf.getImageableWidth();
    double printableHeight = pf.getImageableHeight();

    double scaleX = printableWidth / panelWidth;
    double scaleY = printableHeight / panelHeight;
    double scale = Math.min(scaleX, scaleY) * 0.95; 

    g2d.scale(scale, scale);

    g2d.setColor(getBackground());
    g2d.fillRect(0, 0, (int)panelWidth, (int)panelHeight);

    java.awt.geom.AffineTransform originalTransform = g2d.getTransform();

    if (logoLabel != null) { 
        int logoX = 20;
        int logoY = 20;
        g2d.translate(logoX, logoY);
        logoLabel.printAll(g2d);
        g2d.setTransform(originalTransform);
    }

    g2d.setFont(new Font("Arial", Font.BOLD, 18));
    String title = "HISTORIAL DE NÓMINA";
    int titleWidth = g2d.getFontMetrics().stringWidth(title);
    int titleX = (int)((panelWidth - titleWidth) / 2);
    int titleY = 50; 
    g2d.drawString(title, titleX, titleY);

    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
    String fecha = "Fecha del reporte: " + fechaLabel.getText();
    int fechaWidth = g2d.getFontMetrics().stringWidth(fecha);
    int fechaX = (int)(panelWidth - fechaWidth - 20); 
    g2d.drawString(fecha, fechaX, titleY);

    int tableY = titleY + 40; // Espacio después del título
    g2d.translate(0, tableY);
    jTable1.print(g2d);
    g2d.setTransform(originalTransform);

    // 5. Imprimir pie de página (opcional)
    g2d.setFont(new Font("Arial", Font.ITALIC, 10));
    String footer = "© " + java.time.Year.now().getValue() + " - SANTA BARBARA";
    int footerWidth = g2d.getFontMetrics().stringWidth(footer);
    int footerX = (int)((panelWidth - footerWidth) / 2);
    int footerY = (int)(panelHeight - 20);
    g2d.drawString(footer, footerX, footerY);

    return PAGE_EXISTS;
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        fechaLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logo.jpg"))); // NOI18N

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

        fechaLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
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
                        .addComponent(logoLabel)
                        .addGap(415, 415, 415)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1236, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(fechaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(logoLabel))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fechaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fechaLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel logoLabel;
    // End of variables declaration//GEN-END:variables
}
