package com.mycompany;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    
    private static String URL; // La URL será dinámica
    
    static {
        // Inicializa la base de datos al cargar la clase
        try {
            inicializarBD();
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }
    
    private static void inicializarBD() throws IOException {
        // Directorio destino: "user.home/santabarbara-data"
        String userHome = System.getProperty("user.home");
        Path appDir = Paths.get(userHome, "santabarbara-data");
        Files.createDirectories(appDir); // Crea el directorio si no existe
        
        Path dbPath = appDir.resolve("santabarbara.db");
        
        // Copia la DB desde recursos solo si no existe
        if (!Files.exists(dbPath)) {
            InputStream inputStream = ConexionBD.class.getResourceAsStream("/santabarbara.db");
            Files.copy(inputStream, dbPath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Asigna la URL de conexión
        URL = "jdbc:sqlite:" + dbPath.toString();
    }
    
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    public static void probarConexion() throws SQLException {
        try (Connection conn = obtenerConexion()) {
            System.out.println("Conexión exitosa! BD en: " + URL);
        }
    }
    
}