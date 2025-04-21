/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;



public class FileOpener {
    
    public static boolean openFile(File file) {
        try {
            if (!file.exists()) {
                showErrorMessage("El archivo no existe: " + file.getAbsolutePath());
                return false;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(file);
                    return true;
                }
            }

            return openWithFallback(file);
        } catch (IOException e) {
            showErrorMessage("Error al abrir el archivo: " + e.getMessage() + 
                          "\nUbicación: " + file.getAbsolutePath());
            return false;
        }
    }
    
    private static boolean openWithFallback(File file) throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("win")) {
            Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "start", "\"\"", file.getAbsolutePath()});
        } else if (osName.contains("mac")) {
            Runtime.getRuntime().exec(new String[]{"open", file.getAbsolutePath()});
        } else if (osName.contains("nix") || osName.contains("nux")) {
            Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
        } else {
            throw new IOException("Sistema operativo no soportado");
        }
        return true;
    }
    
    private static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, 
            message, 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showSuccessMessage(File file) {
        JOptionPane.showMessageDialog(null, 
            "Documento generado exitosamente:\n" + file.getAbsolutePath(), 
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
