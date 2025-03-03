package com.mycompany;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import javax.swing.*;
import java.awt.Desktop;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class recibodePago {

    public static void generarRecibo(int idEmpleado, String fechaInicio, String fechaFin) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConexionBD.obtenerConexion();

            String sql = "SELECT e.ID, e.NOMBRE_COMPLETO, e.CEDULA, e.SALARIO AS SALARIO_BASE, "
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
                    + "WHERE e.ID = ? "
                    + "GROUP BY e.ID, e.NOMBRE_COMPLETO, e.SALARIO";

            pst = con.prepareStatement(sql);
            pst.setString(1, fechaInicio);
            pst.setString(2, fechaFin);
            pst.setInt(3, idEmpleado);
            rs = pst.executeQuery();

            if (rs.next()) {
                // Obtener los datos del empleado
                String nombre = rs.getString("NOMBRE_COMPLETO");
                double salarioBase = rs.getDouble("SALARIO_BASE");
                int diasTrabajados = rs.getInt("DIAS_TRABAJADOS");
                int ausencias = rs.getInt("AUSENCIAS");
                double horasExtras = rs.getDouble("HORAS_EXTRAS");
                double sueldoNeto = rs.getDouble("SUELDO_NETO");
                double ivss = rs.getDouble("IVSS");
                double faov = rs.getDouble("FAOV");
                double inces = rs.getDouble("INCES");
                double sueldoFinal = rs.getDouble("SUELDO_FINAL");
                double totalDeducciones = ivss + faov + inces;
                double salarioBaseDiario = salarioBase / 30;
                int cedula = rs.getInt("CEDULA");
                String tipoCedula = "V";

                // Cargar plantilla HTML
                String plantilla;
                try (InputStream inputStream = recibodePago.class.getClassLoader().getResourceAsStream("recibodepago.html")) {
                    if (inputStream == null) {
                        throw new FileNotFoundException("Plantilla HTML no encontrada.");
                    }
                    plantilla = new String(inputStream.readAllBytes());
                }

                // Reemplazar los valores en la plantilla HTML
               plantilla = plantilla.replace("{{nombre}}", nombre)
                     .replace("{{fechaInicio}}", fechaInicio)
                     .replace("{{fechaFin}}", fechaFin)
                     .replace("{{salarioBase}}", String.format(Locale.US, "%.2f BS", salarioBase))
                     .replace("{{diasTrabajados}}", String.valueOf(diasTrabajados))
                     .replace("{{ausencias}}", String.valueOf(ausencias))
                     .replace("{{horasExtras}}", String.format(Locale.US, "%.2f BS", horasExtras))
                     .replace("{{ivss}}", String.format(Locale.US, "%.2f BS", ivss))
                     .replace("{{faov}}", String.format(Locale.US, "%.2f BS", faov))
                     .replace("{{inces}}", String.format(Locale.US, "%.2f BS", inces))
                     .replace("{{sueldoNeto}}", String.format(Locale.US, "%.2f BS", sueldoNeto))
                     .replace("{{sueldoFinal}}", String.format(Locale.US, "%.2f BS", sueldoFinal))
                     .replace("{{totalDeducciones}}", String.format(Locale.US, "%.2f BS", totalDeducciones))
                     .replace("{{tipoCedula}}", tipoCedula)  // Se cambia el formateo numérico por un simple reemplazo de cadena
                     .replace("{{salarioBaseDia}}", String.format(Locale.US, "%.2f BS", salarioBaseDiario))
                     .replace("{{cedula}}", String.valueOf(cedula));

                // Elegir la ubicación para guardar el archivo PDF
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar Recibo de Pago");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));
                int userSelection = fileChooser.showSaveDialog(null);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File pdfFile = fileChooser.getSelectedFile();
                    if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
                        pdfFile = new File(pdfFile.getParentFile(), pdfFile.getName() + ".pdf");
                    }

                    // Convertir HTML a PDF
                    try (OutputStream os = new FileOutputStream(pdfFile)) {
                        PdfRendererBuilder builder = new PdfRendererBuilder();
                        builder.withHtmlContent(plantilla, null);
                        builder.toStream(os); // Definir el flujo de salida
                        builder.run();  // Generar el PDF

                        System.out.println("Recibo de pago generado con éxito en: " + pdfFile.getAbsolutePath());
                        abrirArchivoPDF(pdfFile);
                    }
                } else {
                    System.out.println("El usuario canceló la operación.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron datos para el empleado seleccionado.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el recibo: " + ex.getMessage());
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

    public static void abrirArchivoPDF(File pdfFile) {
        try {
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);  // Abrir el archivo PDF generado
                } else {
                    // Si el Desktop no está soportado, intentamos abrir el PDF en el navegador predeterminado
                    String pdfPath = pdfFile.toURI().toURL().toString(); // Convertir archivo a URL
                    Desktop.getDesktop().browse(new java.net.URI(pdfPath));  // Intentar abrir en el navegador
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
