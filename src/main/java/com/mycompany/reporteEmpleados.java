package com.mycompany;

import java.awt.Desktop;
import javax.swing.*;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class reporteEmpleados {

    public static void generarReporte() {
        // Declarar el formateador de fechas
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID, NOMBRE_COMPLETO, TIPO_CEDULA, CEDULA, FECHA_NACIMIENTO, SEXO, TELEFONO, TELEFONO_HABITACION, EMAIL, DIRECCION, CARGO, SALARIO, INICIO_CONTRATO, FIN_CONTRATO, BANCO, TIPO_CUENTA, NUMERO_CUENTA, PAGO_MOVIL FROM empleados")) {

            StringBuilder filas = new StringBuilder();
            while (rs.next()) {
                // Obtener las fechas como cadenas
                String fechaNacimientoStr = rs.getString("FECHA_NACIMIENTO");
                String inicioContratoStr = rs.getString("INICIO_CONTRATO");
                String finContratoStr = rs.getString("FIN_CONTRATO");

                // Formatear las fechas
                String fechaNacimientoFormatted = "";
                String inicioContratoFormatted = "";
                String finContratoFormatted = "";

                try {
                    if (fechaNacimientoStr != null) {
                        Date fechaNacimiento = new SimpleDateFormat("yyyy-MM-dd").parse(fechaNacimientoStr);
                        fechaNacimientoFormatted = dateFormat.format(fechaNacimiento);
                    }
                    if (inicioContratoStr != null) {
                        Date inicioContrato = new SimpleDateFormat("yyyy-MM-dd").parse(inicioContratoStr);
                        inicioContratoFormatted = dateFormat.format(inicioContrato);
                    }
                    if (finContratoStr != null) {
                        Date finContrato = new SimpleDateFormat("yyyy-MM-dd").parse(finContratoStr);
                        finContratoFormatted = dateFormat.format(finContrato);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Construir la fila de la tabla
                filas.append("<tr>")
                     .append("<td>").append(rs.getInt("ID")).append("</td>")
                     .append("<td>").append(rs.getString("NOMBRE_COMPLETO")).append("</td>")
                     .append("<td>").append(rs.getString("TIPO_CEDULA")).append("</td>")
                     .append("<td>").append(rs.getInt("CEDULA")).append("</td>")
                     .append("<td>").append(fechaNacimientoFormatted).append("</td>")
                     .append("<td>").append(rs.getString("SEXO")).append("</td>")
                     .append("<td>").append(rs.getInt("TELEFONO")).append("</td>")
                     .append("<td>").append(rs.getInt("TELEFONO_HABITACION")).append("</td>")
                     .append("<td>").append(rs.getString("EMAIL")).append("</td>")
                     .append("<td>").append(rs.getString("DIRECCION")).append("</td>")
                     .append("<td>").append(rs.getString("CARGO")).append("</td>")
                     .append("<td>").append(rs.getDouble("SALARIO")).append("</td>")
                     .append("<td>").append(inicioContratoFormatted).append("</td>")
                     .append("<td>").append(finContratoFormatted).append("</td>")
                     .append("<td>").append(rs.getString("BANCO")).append("</td>")
                     .append("<td>").append(rs.getString("TIPO_CUENTA")).append("</td>")
                     .append("<td>").append(rs.getInt("NUMERO_CUENTA")).append("</td>")
                     .append("<td>").append(rs.getString("PAGO_MOVIL")).append("</td>")
                     .append("</tr>");
            }

            // Cargar la plantilla HTML desde recursos
            String plantilla;
            try (InputStream inputStream = reporteEmpleados.class.getClassLoader().getResourceAsStream("reporteEmpleados.html")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("Plantilla HTML no encontrada.");
                }
                plantilla = new String(inputStream.readAllBytes());
            }

            // Reemplazar el marcador de posición con las filas generadas
            plantilla = plantilla.replace("{{rows}}", filas.toString());

            // Permitir al usuario elegir la ubicación para guardar el archivo HTML
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Reporte HTML");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos HTML (*.html)", "html"));
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File htmlFile = fileChooser.getSelectedFile();
                if (!htmlFile.getName().toLowerCase().endsWith(".html")) {
                    htmlFile = new File(htmlFile.getParentFile(), htmlFile.getName() + ".html");
                }

                // Guardar el HTML en el archivo seleccionado
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile))) {
                    writer.write(plantilla);
                }

                System.out.println("Reporte HTML generado con éxito en: " + htmlFile.getAbsolutePath());

                // Abrir el archivo HTML en el navegador predeterminado
                abrirArchivoHTML(htmlFile);
            } else {
                System.out.println("El usuario canceló la operación.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para abrir el archivo HTML en el navegador predeterminado
    public static void abrirArchivoHTML(File htmlFile) {
        try {
            if (htmlFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(htmlFile.toURI());
                } else {
                    JOptionPane.showMessageDialog(null, "No se puede abrir el archivo HTML. El navegador no está disponible.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "El archivo HTML no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al abrir el archivo HTML: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
