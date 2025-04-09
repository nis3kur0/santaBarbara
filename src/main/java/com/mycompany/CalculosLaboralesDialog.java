/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Date;

public class CalculosLaboralesDialog extends JDialog {
    private JComboBox<String> seleccionarEmpleado;
    private JButton btnVacaciones, btnLiquidacion, btnPrestaciones;
    
    // Colores patrios de Venezuela
    private final Color ROJO_VENEZOLANO = new Color(206, 17, 38);
    private final Color AZUL_VENEZOLANO = new Color(0, 35, 149);
    private final Color BLANCO = Color.WHITE;
    
    public CalculosLaboralesDialog(JFrame parent) {
        super(parent, "Cálculos Laborales - Venezuela", true);
        setSize(550, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BLANCO);
        
        initUI();
        cargarEmpleadosEnComboBox();
    }
    
    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(AZUL_VENEZOLANO);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("CÁLCULOS LABORALES");
        titleLabel.setForeground(BLANCO);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // Panel central con controles
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BLANCO);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Combo de empleados
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblEmpleado = new JLabel("Seleccione empleado:");
        lblEmpleado.setFont(new Font("SansSerif", Font.BOLD, 14));
        centerPanel.add(lblEmpleado, gbc);
        
        gbc.gridy = 1;
        seleccionarEmpleado = new JComboBox<>();
        seleccionarEmpleado.setPreferredSize(new Dimension(350, 35));
        seleccionarEmpleado.setFont(new Font("SansSerif", Font.PLAIN, 14));
        centerPanel.add(seleccionarEmpleado, gbc);
        
        // Botones con colores patrios
        gbc.gridy = 2;
        btnVacaciones = createStyledButton("CALCULAR VACACIONES", ROJO_VENEZOLANO);
        btnVacaciones.addActionListener(e -> calcularVacaciones());
        centerPanel.add(btnVacaciones, gbc);
        
        gbc.gridy = 3;
        btnLiquidacion = createStyledButton("CALCULAR LIQUIDACIÓN", AZUL_VENEZOLANO);
        btnLiquidacion.addActionListener(e -> calcularLiquidacion());
        centerPanel.add(btnLiquidacion, gbc);
        
        gbc.gridy = 4;
        btnPrestaciones = createStyledButton("CALCULAR PRESTACIONES SOCIALES", ROJO_VENEZOLANO);
        btnPrestaciones.addActionListener(e -> calcularPrestaciones());
        centerPanel.add(btnPrestaciones, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BLANCO);
        JButton btnCerrar = createStyledButton("CERRAR", AZUL_VENEZOLANO);
        btnCerrar.addActionListener(e -> dispose());
        bottomPanel.add(btnCerrar);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(BLANCO);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(350, 40));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 0.1f), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }
    
    private void cargarEmpleadosEnComboBox() {
        seleccionarEmpleado.removeAllItems();
        seleccionarEmpleado.addItem("Selecciona un empleado");

        String query = "SELECT NOMBRE_COMPLETO FROM empleados WHERE ESTADO = 'Activo'";
        try (Connection con = ConexionBD.obtenerConexion(); 
             java.sql.Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String nombreEmpleado = rs.getString("NOMBRE_COMPLETO");
                seleccionarEmpleado.addItem(nombreEmpleado);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar empleados: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int obtenerIdEmpleadoPorNombre(String nombre) {
        String sql = "SELECT ID FROM empleados WHERE NOMBRE_COMPLETO = ?";
        try (Connection conn = ConexionBD.obtenerConexion(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al obtener el ID del empleado: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
    
    private void calcularVacaciones() {
        if (seleccionarEmpleado.getSelectedIndex() <= 0) {
            mostrarAdvertencia("Seleccione un empleado");
            return;
        }
        
        try {
            String nombreEmpleado = (String) seleccionarEmpleado.getSelectedItem();
            int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);
            
            if(idEmpleado == -1) {
                throw new SQLException("No se pudo obtener el ID del empleado");
            }
            
            double salario = obtenerSalario(idEmpleado);
            int diasTrabajados = obtenerDiasTrabajados(idEmpleado);
            int mesesTrabajados = obtenerMesesTrabajados(idEmpleado);
            
            double diasBaseVacaciones = 15; 
            double diasAdicionales = Math.floor(mesesTrabajados / 12.0); 
            double diasVacaciones = Math.min(diasBaseVacaciones + diasAdicionales, 30); 
            
            double bonoVacacional = (salario / 30) * diasVacaciones;
            double totalVacaciones = salario + bonoVacacional;
            
            String mensaje = "<html><div style='text-align:center;'>" +
                "<h3 style='color:#CE1126;'>CÁLCULO DE VACACIONES</h3>" +
                "<p><b>Empleado:</b> " + nombreEmpleado + "</p>" +
                "<p><b>Salario mensual:</b> " + formatCurrency(salario) + "</p>" +
                "<p><b>Meses trabajados:</b> " + mesesTrabajados + "</p>" +
                "<p><b>Días de vacaciones:</b> " + String.format("%.0f", diasVacaciones) + " días</p>" +
                "<p><b>Bono vacacional:</b> " + formatCurrency(bonoVacacional) + "</p>" +
                "<hr><p style='font-size:16px;'><b>TOTAL A RECIBIR:</b> " + formatCurrency(totalVacaciones) + "</p>" +
                "</div></html>";
            
            mostrarResultado("Resultado - Vacaciones", mensaje);
                
        } catch (SQLException e) {
            manejarError(e, "Error al calcular vacaciones");
        }
    }
    
    private void calcularLiquidacion() {
        if (seleccionarEmpleado.getSelectedIndex() <= 0) {
            mostrarAdvertencia("Seleccione un empleado");
            return;
        }
        
        try {
            String nombreEmpleado = (String) seleccionarEmpleado.getSelectedItem();
            int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);
            
            if(idEmpleado == -1) {
                throw new SQLException("No se pudo obtener el ID del empleado");
            }
            
            double salario = obtenerSalario(idEmpleado);
            int mesesTrabajados = obtenerMesesTrabajados(idEmpleado);
            
            double prestaciones = (salario * mesesTrabajados) / 12;
            double antiguedad = (salario * (mesesTrabajados / 12.0)) * 0.5; 
            double totalLiquidacion = prestaciones + antiguedad;
            
            String mensaje = "<html><div style='text-align:center;'>" +
                "<h3 style='color:#002395;'>CÁLCULO DE LIQUIDACIÓN</h3>" +
                "<p><b>Empleado:</b> " + nombreEmpleado + "</p>" +
                "<p><b>Salario mensual:</b> " + formatCurrency(salario) + "</p>" +
                "<p><b>Meses trabajados:</b> " + mesesTrabajados + "</p>" +
                "<p><b>Prestaciones:</b> " + formatCurrency(prestaciones) + "</p>" +
                "<p><b>Antigüedad:</b> " + formatCurrency(antiguedad) + "</p>" +
                "<hr><p style='font-size:16px;'><b>TOTAL LIQUIDACIÓN:</b> " + formatCurrency(totalLiquidacion) + "</p>" +
                "</div></html>";
            
            mostrarResultado("Resultado - Liquidación", mensaje);
                
        } catch (SQLException e) {
            manejarError(e, "Error al calcular liquidación");
        }
    }
    
    private void calcularPrestaciones() {
        if (seleccionarEmpleado.getSelectedIndex() <= 0) {
            mostrarAdvertencia("Seleccione un empleado");
            return;
        }
        
        try {
            String nombreEmpleado = (String) seleccionarEmpleado.getSelectedItem();
            int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);
            
            if(idEmpleado == -1) {
                throw new SQLException("No se pudo obtener el ID del empleado");
            }
            
            double salario = obtenerSalario(idEmpleado);
            int mesesTrabajados = obtenerMesesTrabajados(idEmpleado);
            
          
            double prestaciones = (salario * mesesTrabajados) / 12;
            double intereses = prestaciones * 0.1; 
            double totalPrestaciones = prestaciones + intereses;
            
            String mensaje = "<html><div style='text-align:center;'>" +
                "<h3 style='color:#CE1126;'>CÁLCULO DE PRESTACIONES SOCIALES</h3>" +
                "<p><b>Empleado:</b> " + nombreEmpleado + "</p>" +
                "<p><b>Salario mensual:</b> " + formatCurrency(salario) + "</p>" +
                "<p><b>Meses trabajados:</b> " + mesesTrabajados + "</p>" +
                "<p><b>Prestaciones acumuladas:</b> " + formatCurrency(prestaciones) + "</p>" +
                "<p><b>Intereses (10%):</b> " + formatCurrency(intereses) + "</p>" +
                "<hr><p style='font-size:16px;'><b>TOTAL PRESTACIONES:</b> " + formatCurrency(totalPrestaciones) + "</p>" +
                "</div></html>";
            
            mostrarResultado("Resultado - Prestaciones Sociales", mensaje);
                
        } catch (SQLException e) {
            manejarError(e, "Error al calcular prestaciones");
        }
    }
    
    private double obtenerSalario(int idEmpleado) throws SQLException {
        String query = "SELECT SALARIO FROM empleados WHERE ID = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, idEmpleado);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("SALARIO");
            }
        }
        throw new SQLException("No se encontró el salario para el empleado");
    }
    
    private int obtenerDiasTrabajados(int idEmpleado) throws SQLException {
        String query = "SELECT COUNT(*) as dias FROM asistencias WHERE ID_EMPLEADO = ? AND ESTADO = 'Presente'";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, idEmpleado);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("dias");
            }
        }
        return 0;
    }
    
    private int obtenerMesesTrabajados(int idEmpleado) throws SQLException {
        String query = "SELECT INICIO_CONTRATO FROM empleados WHERE ID = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, idEmpleado);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Date inicio = rs.getDate("INICIO_CONTRATO");
                long diff = new Date().getTime() - inicio.getTime();
                return (int) (diff / (1000L * 60 * 60 * 24 * 30)); // Aproximación a meses
            }
        }
        return 0;
    }
    
    private void mostrarResultado(String titulo, String mensaje) {
        JLabel label = new JLabel(mensaje);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(this, label, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
    
    private String formatCurrency(double amount) {
        DecimalFormat df = new DecimalFormat("Bs. #,##0.00");
        return df.format(amount);
    }
    
    private void manejarError(Exception e, String mensaje) {
        JOptionPane.showMessageDialog(this, 
            mensaje + ": " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
