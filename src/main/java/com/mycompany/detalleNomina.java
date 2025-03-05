package com.mycompany;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import javax.swing.*;
import java.awt.Desktop;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class detalleNomina {

    public static void generarDetalleNomina(int idNomina) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String fechaInicioStr = null;
        String fechaFinStr = null;
        String plantilla = "";  // Inicialización de la variable plantilla

        try {
            con = ConexionBD.obtenerConexion();

            // Consulta SQL para obtener las fechas de inicio y fin de la nómina
            String sqlFechas = "SELECT FECHA_INICIO_NOMINA, FECHA_FIN_NOMINA FROM nominas WHERE ID = ?";
            pst = con.prepareStatement(sqlFechas);
            pst.setInt(1, idNomina);
            rs = pst.executeQuery();

            if (rs.next()) {
                fechaInicioStr = rs.getString("FECHA_INICIO_NOMINA");
                fechaFinStr = rs.getString("FECHA_FIN_NOMINA");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron fechas para la nómina seleccionada.");
                return; // Si no se encuentran fechas, terminamos la ejecución
            }

            // Cargar la plantilla HTML desde el archivo recursos
            try (InputStream inputStream = detalleNomina.class.getClassLoader().getResourceAsStream("detallesNomina.html")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("Plantilla HTML no encontrada.");
                }
                plantilla = new String(inputStream.readAllBytes());  // Aquí se asigna a la variable plantilla
            }

            // Realizamos la consulta para obtener los detalles de la nómina con las fechas
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

            // Realizamos la consulta con las fechas obtenidas
            pst = con.prepareStatement(sql);
            pst.setString(1, fechaInicioStr); // Fecha de inicio de la nómina
            pst.setString(2, fechaFinStr); // Fecha de fin de la nómina
            rs = pst.executeQuery();

            // Reemplazamos las fechas en la plantilla HTML
            plantilla = plantilla.replace("{FECHA_INICIO}", fechaInicioStr)
                                 .replace("{FECHA_FIN}", fechaFinStr);

            // Variable que almacenará la tabla de detalles de la nómina
            String detallesNomina = "";

            while (rs.next()) {
                detallesNomina += "<tr>";
                detallesNomina += "<td>" + rs.getString("NOMBRE_COMPLETO") + "</td>";
                detallesNomina += "<td>" + rs.getInt("DIAS_TRABAJADOS") + "</td>";
                detallesNomina += "<td>" + rs.getInt("AUSENCIAS") + "</td>";
                detallesNomina += "<td>" + rs.getDouble("HORAS_EXTRAS") + "</td>";
                detallesNomina += "<td>" + String.format(Locale.US, "%.2f", rs.getDouble("SUELDO_NETO")) + "</td>";
                detallesNomina += "<td>" + String.format(Locale.US, "%.2f", rs.getDouble("IVSS")) + "</td>";
                detallesNomina += "<td>" + String.format(Locale.US, "%.2f", rs.getDouble("FAOV")) + "</td>";
                detallesNomina += "<td>" + String.format(Locale.US, "%.2f", rs.getDouble("INCES")) + "</td>";
                detallesNomina += "<td>" + String.format(Locale.US, "%.2f", rs.getDouble("SUELDO_FINAL")) + "</td>";
                detallesNomina += "</tr>";
            }

            // Reemplazamos los detalles de la nómina en la plantilla HTML
            plantilla = plantilla.replace("{DETALLES_NOMINA}", detallesNomina);

            // Si la plantilla tiene detalles, la guardamos en un PDF
            if (!detallesNomina.isEmpty()) {
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
    
    try {
        builder.run();
        System.out.println("Reporte de nómina generado con éxito en: " + pdfFile.getAbsolutePath());
        abrirArchivoPDF(pdfFile);
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al generar el reporte PDF: " + e.getMessage());
    }
} catch (IOException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "Error al guardar el archivo PDF: " + e.getMessage());
}

                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron registros para la nómina.");
            }

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el reporte: " + ex.getMessage());
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
