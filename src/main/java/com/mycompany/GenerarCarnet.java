/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import com.mycompany.views.VistaPreviaHTML;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author gabo
 */
public class GenerarCarnet {
    
     public static void generarCarnetEmpleado(int idEmpleado) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConexionBD.obtenerConexion();

            String sql = "SELECT e.ID, e.NOMBRE_COMPLETO, e.CEDULA, e.TIPO_CEDULA, e.TELEFONO, e.CARGO "
                    + "FROM empleados e WHERE e.ID = ?";

            pst = con.prepareStatement(sql);
            pst.setInt(1, idEmpleado);
            rs = pst.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("ID");
                String nombre = rs.getString("NOMBRE_COMPLETO");
                String cedula = rs.getString("CEDULA");
                String tipoCedula = rs.getString("TIPO_CEDULA");
                String telefono = rs.getString("TELEFONO");
                String cargo = rs.getString("CARGO");
                
                String plantilla;
                try (InputStream inputStream = fichaEmpleado.class.getClassLoader().getResourceAsStream("fichaEmpleado.html")) {
                    if (inputStream == null) {
                        throw new FileNotFoundException("Plantilla HTML no encontrada.");
                    }
                    plantilla = new String(inputStream.readAllBytes());
                }

                plantilla = plantilla.replace("{{codigoEmpleado}}", String.valueOf(idEmpleado))
                                             

                        .replace("{{ID_EMPLEADO}}", String.valueOf(id))
        .replace("{{NOMBRE_COMPLETO}}", (nombre != null ? nombre : "No disponible"))
        .replace("{{TIPO_CEDULA}}", (tipoCedula != null ? tipoCedula : "No disponible"))
        .replace("{{CEDULA}}", (cedula != null ? cedula : "No disponible"))
      
        .replace("{{TELEFONO}}", (telefono != null ? telefono : "No disponible"))
        
        .replace("{{CARGO}}", (cargo != null ? cargo : "No disponible"));
       
VistaPreviaHTML.mostrarVistaPrevia(plantilla);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar Ficha de Empleado");
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
                        System.out.println("Ficha de empleado generada con éxito en: " + pdfFile.getAbsolutePath());
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
            JOptionPane.showMessageDialog(null, "Error al generar la ficha: " + ex.getMessage());
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
