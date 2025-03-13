/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class GeneradorQR {
    
    public static void generateQRForExistingEmployees() {
        String sql = "SELECT ID, NOMBRE_COMPLETO, CEDULA FROM empleados WHERE qr_code IS NULL";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("ID");
                String nombre = rs.getString("NOMBRE_COMPLETO");
                String cedula = rs.getString("CEDULA");
                updateQRInDatabase(id, nombre, cedula);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void updateQRInDatabase(int empleadoId, String nombre, String cedula) {
        String qrContent = "ID: " + empleadoId + "\nNombre: " + nombre + "\nCédula: " + cedula;
        String filePath = "qr_e/qr_" + empleadoId + ".png";
        
        try {
            byte[] qrBytes = generateQRCodeImage(qrContent, 200, 200, filePath);
            
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE empleados SET qr_code = ? WHERE ID = ?")) {
                
                pstmt.setBytes(1, qrBytes);
                pstmt.setInt(2, empleadoId);
                pstmt.executeUpdate();
            }
        } catch (WriterException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static byte[] generateQRCodeImage(String text, int width, int height, String filePath) 
            throws WriterException, IOException {
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        
        // Crear directorio si no existe
        File directory = new File("qr_e");
        if (!directory.exists()) directory.mkdirs();
        
        // Guardar archivo
        Path path = Paths.get(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        
        // Convertir a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        return baos.toByteArray();
    }
}