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

public class fichaEmpleado {

    public static void generarFichaEmpleado(int idEmpleado) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = ConexionBD.obtenerConexion();

            String sql = "SELECT e.ID, e.NOMBRE_COMPLETO, e.CEDULA, e.TIPO_CEDULA, e.SEXO, e.FECHA_NACIMIENTO, "
                    + "e.DIRECCION, e.TELEFONO, e.TELEFONO_HABITACION, e.EMAIL, e.CARGO, e.INICIO_CONTRATO, "
                    + "e.FIN_CONTRATO, e.SALARIO, e.BANCO, e.NUMERO_CUENTA, e.TIPO_CUENTA, e.PAGO_MOVIL "
                    + "FROM empleados e WHERE e.ID = ?";

            pst = con.prepareStatement(sql);
            pst.setInt(1, idEmpleado);
            rs = pst.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("ID");
                String nombre = rs.getString("NOMBRE_COMPLETO");
                String cedula = rs.getString("CEDULA");
                String tipoCedula = rs.getString("TIPO_CEDULA");
                String sexo = rs.getString("SEXO");
                String fechaNacimiento = rs.getString("FECHA_NACIMIENTO");
                String direccion = rs.getString("DIRECCION");
                String telefono = rs.getString("TELEFONO");
                String telefonoHabitacion = rs.getString("TELEFONO_HABITACION");
                String email = rs.getString("EMAIL");
                String cargo = rs.getString("CARGO");
                String inicioContrato = rs.getString("INICIO_CONTRATO");
                String finContrato = rs.getString("FIN_CONTRATO");
                double salario = rs.getDouble("SALARIO");
                String banco = rs.getString("BANCO");
                String numeroCuenta = rs.getString("NUMERO_CUENTA");
                String tipoCuenta = rs.getString("TIPO_CUENTA");
                String pagoMovil = rs.getString("PAGO_MOVIL");

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
        .replace("{{SEXO}}", (sexo != null ? sexo : "No disponible"))
        .replace("{{FECHA_NACIMIENTO}}", (fechaNacimiento != null ? fechaNacimiento : "No disponible"))
        .replace("{{DIRECCION}}", (direccion != null ? direccion : "No disponible"))
        .replace("{{TELEFONO}}", (telefono != null ? telefono : "No disponible"))
        .replace("{{TELEFONO_HABITACION}}", (telefonoHabitacion != null ? telefonoHabitacion : "No disponible"))
        .replace("{{EMAIL}}", (email != null ? email : "No disponible"))
        .replace("{{CARGO}}", (cargo != null ? cargo : "No disponible"))
        .replace("{{INICIO_CONTRATO}}", (inicioContrato != null ? inicioContrato : "No disponible"))
        .replace("{{FIN_CONTRATO}}", (finContrato != null ? finContrato : "No disponible"))
        .replace("{{SALARIO}}", String.format(Locale.US, "%.2f BS", salario))
        .replace("{{BANCO}}", (banco != null ? banco : "No disponible"))
        .replace("{{NUMERO_CUENTA}}", (numeroCuenta != null ? numeroCuenta : "No disponible"))
        .replace("{{TIPO_CUENTA}}", (tipoCuenta != null ? tipoCuenta : "No disponible"))
        .replace("{{PAGO_MOVIL}}", (pagoMovil != null ? pagoMovil : "No disponible"));

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
