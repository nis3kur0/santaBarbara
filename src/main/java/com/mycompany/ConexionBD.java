/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Nattitor
 */
public class ConexionBD {
    
    private static final String URL = "jdbc:sqlite:santabarbara.db";
    
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    public static void probarConexion() throws SQLException {
        try (Connection conn = obtenerConexion()) {
            System.out.println("Conexión exitosa!");
        }
    }
    
}
