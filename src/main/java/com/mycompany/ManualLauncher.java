/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import java.awt.Desktop;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;

public class ManualLauncher {

    public static void abrirManual() {
        try {
            // Crear carpeta temporal
            Path tempDir = Files.createTempDirectory("manualSantaBarbara");

            // Copiar recursos desde el classpath a la carpeta temporal
            copiarRecurso("manual-usuario/index.html", tempDir.resolve("index.html"));
            copiarRecurso("manual-usuario/style.css", tempDir.resolve("style.css"));
            copiarDirectorio("manual-usuario/imagenes", tempDir.resolve("imagenes"));

            // Abrir el index en el navegador
            File indexFile = tempDir.resolve("index.html").toFile();
            Desktop.getDesktop().browse(indexFile.toURI());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copiarRecurso(String recurso, Path destino) throws IOException {
        try (InputStream in = ManualLauncher.class.getClassLoader().getResourceAsStream(recurso)) {
            if (in == null) {
                throw new FileNotFoundException("Recurso no encontrado: " + recurso);
            }
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void copiarDirectorio(String recursoDir, Path destinoDir) throws IOException {
        // Para que funcione desde el JAR, necesitas listar manualmente los archivos (o usar una librería externa como ClassGraph o Spring)
        // Aquí asumimos que conoces los nombres de los archivos
        String[] imagenes = {
            "login.png",
            "empleados.png",
            "asistencia.png",
            "nomina.jpg",
            "asistenciaVentana.png",
            "configuracion.png",
                "santa.jpg",
        };

        Files.createDirectories(destinoDir);

        for (String img : imagenes) {
            copiarRecurso(recursoDir + "/" + img, destinoDir.resolve(img));
        }
    }
}
