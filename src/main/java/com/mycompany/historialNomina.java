package com.mycompany;

import com.mycompany.ConexionBD;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import javax.swing.*;
import java.awt.Desktop;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class historialNomina {

    public static void generarReporteNomina() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConexionBD.obtenerConexion();

           
            String sql = "SELECT ID_NOMINA, FECHA_INICIO_NOMINA, FECHA_FIN_NOMINA, FECHA_PAGO_NOMINA, TOTAL_EMPLEADOS, MONTO_TOTAL FROM nomina";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            
            String plantilla;
            try (InputStream inputStream = historialNomina.class.getClassLoader().getResourceAsStream("historialNomina.html")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("Plantilla HTML no encontrada.");
                }
                plantilla = new String(inputStream.readAllBytes());
            }

            boolean hayDatos = false;
            StringBuilder filas = new StringBuilder();

 
            while (rs.next()) {
                hayDatos = true;
                int id = rs.getInt("ID_NOMINA");
                String fechaInicio = rs.getString("FECHA_INICIO_NOMINA");
                String fechaFin = rs.getString("FECHA_FIN_NOMINA");
                String fechaPago = rs.getString("FECHA_PAGO_NOMINA");
                int totalEmpleados = rs.getInt("TOTAL_EMPLEADOS");
                double montoTotal = rs.getDouble("MONTO_TOTAL");

                String fila = "<tr>"
                        + "<td>" + id + "</td>"
                        + "<td>" + fechaInicio + "</td>"
                        + "<td>" + fechaFin + "</td>"
                        + "<td>" + fechaPago + "</td>"
                        + "<td>" + totalEmpleados + "</td>"
                        + "<td>" + String.format(Locale.US, "%.2f BS", montoTotal) + "</td>"
                        + "</tr>";

             
                filas.append(fila);
            }

            
            if (hayDatos) {
                plantilla = plantilla.replace("{{filas}}", filas.toString());
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron datos en la tabla de nóminas.");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Reporte de Nómina");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fileChooser.getSelectedFile();
                if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
                    pdfFile = new File(pdfFile.getParentFile(), pdfFile.getName() + ".pdf");
                }

                try (OutputStream os = new FileOutputStream(pdfFile)) {
                    PdfRendererBuilder builder = new PdfRendererBuilder();
                    builder.withHtmlContent(plantilla, null);
                    builder.toStream(os);
                    builder.run();

                    System.out.println("Reporte de nómina generado con éxito en: " + pdfFile.getAbsolutePath());
                    abrirArchivoPDF(pdfFile);
                }
            } else {
                System.out.println("El usuario canceló la operación.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el reporte de nómina: " + ex.getMessage());
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

    public static void abrirArchivoPDF(File pdfFile) {
        try {
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    String pdfPath = pdfFile.toURI().toURL().toString();
                    Desktop.getDesktop().browse(new java.net.URI(pdfPath));
                    System.out.println("El archivo se ha abierto en el navegador.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "El archivo PDF no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | java.net.URISyntaxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al abrir el archivo PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
