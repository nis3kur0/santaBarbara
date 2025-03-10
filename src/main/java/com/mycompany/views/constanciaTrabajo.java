package com.mycompany.views;

import com.mycompany.ConexionBD;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import javax.swing.*;
import java.awt.Desktop;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class constanciaTrabajo {

    public static void generarConstancia(int idEmpleado) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConexionBD.obtenerConexion();

            String sql = "SELECT NOMBRE_COMPLETO, CEDULA,INICIO_CONTRATO, FIN_CONTRATO, CARGO, SALARIO FROM empleados WHERE ID = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, idEmpleado);
            rs = pst.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("NOMBRE_COMPLETO");
                String cedula = rs.getString("CEDULA");
                double salario = rs.getDouble("SALARIO");
                String cargo = rs.getString("CARGO");
    String fechaInicio = rs.getString("INICIO_CONTRATO");
    String fechaFin = rs.getString("FIN_CONTRATO");
    LocalDate fechaActual = LocalDate.now();
    String fechaActualFormateada = fechaActual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));


                String plantilla;
                try (InputStream inputStream = constanciaTrabajo.class.getClassLoader().getResourceAsStream("constanciadeTrabajo.html")) {
                    if (inputStream == null) {
                        throw new FileNotFoundException("Plantilla HTML no encontrada.");
                    }
                    plantilla = new String(inputStream.readAllBytes());
                }

                 plantilla = plantilla.replace("{{NOMBRE_COMPLETO}}", nombre)
                                     .replace("{{CÉDULA}}", cedula)
                                     .replace("{{SALARIO}}", String.format(Locale.US, "%.2f BS", salario))
                        .replace("{{CARGO}}", cargo)
                         .replace("{{FECHA_INICIO}}", fechaInicio != null ? fechaInicio : "")
                         .replace("{{FECHA_FIN}}", fechaFin != null ? fechaFin : "")
                        .replace("{{FECHA_ACTUAL}}", fechaActualFormateada);
                 
                       VistaPreviaHTML.mostrarVistaPrevia(plantilla);


                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar Constancia de Trabajo");
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
                        
                        abrirArchivoPDF(pdfFile);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron datos para el empleado seleccionado.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar la constancia: " + ex.getMessage());
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
                }
            } else {
                JOptionPane.showMessageDialog(null, "El archivo PDF no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al abrir el archivo PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
