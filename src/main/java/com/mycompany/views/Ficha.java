/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.views;

import com.mycompany.ConexionBD;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 *
 */
public class Ficha extends javax.swing.JPanel {

    /**
     * Creates new form Ficha
     */
    
    private String fotoPath;
    private int idEmpleadoActual;
    private boolean fotoCargada = false;

    
    public Ficha() {
        initComponents();
        actualizarEstadoBotonesFoto();
        agregarFoto.setPreferredSize(new java.awt.Dimension(78, 78));
        agregarFoto.setSize(78, 78);
        limpiarCampos();
        deshabilitarCampos();
        establecerEstilos();
        agregarFoto.setContentAreaFilled(false);  // Desactivar relleno automático
        agregarFoto.setBorderPainted(false);      // Quitar borde
        agregarFoto.setOpaque(true);              // Permitir fondo personalizado
        agregarFoto.setBackground(Color.WHITE);   

    }
    
    
        private void deshabilitarCampos() {
        
            deshabilitarComponentesEnContenedor(jPanel1);

    }
        
    private void deshabilitarComponentesEnContenedor(java.awt.Container contenedor) {
        for (java.awt.Component component : contenedor.getComponents()) {
            if (component instanceof javax.swing.JTextField) {
                javax.swing.JTextField campo = (javax.swing.JTextField) component;
                campo.setEditable(false);
                campo.setFocusable(false);
            } else if (component instanceof javax.swing.JPanel) {
                deshabilitarComponentesEnContenedor((javax.swing.JPanel) component);
            }
        }
    }

    private void establecerEstilos() {
        Color colorFondo = new Color(240, 240, 240);
        establecerEstilosEnContenedor(jPanel1, colorFondo);
    }

    private void establecerEstilosEnContenedor(java.awt.Container contenedor, Color color) {
        for (java.awt.Component component : contenedor.getComponents()) {
            if (component instanceof javax.swing.JTextField) {
                component.setBackground(color);
                component.setForeground(Color.BLACK);
            } else if (component instanceof javax.swing.JPanel) {
                establecerEstilosEnContenedor((javax.swing.JPanel) component, color);
            }
        }
    }

    
        public void cargarDatosEmpleado(int idEmpleado) {
        this.idEmpleadoActual = idEmpleado;
        String sql = "SELECT * FROM empleados WHERE ID = ?";
    
        try (Connection conn = ConexionBD.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
            pstmt.setInt(1, idEmpleado);
            ResultSet rs = pstmt.executeQuery();
        
            if (rs.next()) {
                idText.setText(String.valueOf(rs.getInt("ID")));
                nombreText.setText(rs.getString("NOMBRE_COMPLETO"));
                cedulaText.setText(rs.getString("TIPO_CEDULA") + "-" + rs.getString("CEDULA"));
                sexoText.setText(rs.getString("SEXO"));
                fechaNacText.setText(rs.getString("FECHA_NACIMIENTO"));
                direccionText.setText(rs.getString("DIRECCION"));
                telefonoText.setText(rs.getString("TELEFONO"));
                tlfHabitText.setText(rs.getString("TELEFONO_HABITACION"));
                emailText.setText(rs.getString("EMAIL"));
                
                cargoText.setText(rs.getString("CARGO"));
                fechaICText.setText(rs.getString("INICIO_CONTRATO"));
                fechaFCText.setText(rs.getString("FIN_CONTRATO"));
                salarioText.setText(String.format("%,.2f", rs.getDouble("SALARIO")));
                                bancoText.setText(rs.getString("BANCO"));
                tipoCuentaText.setText(rs.getString("TIPO_CUENTA"));
                numeroCuentaText.setText(rs.getString("NUMERO_CUENTA"));
                pagoMovilText.setText(rs.getString("PAGO_MOVIL"));
                String rutaFoto = rs.getString("FOTOS");
                fotoCargada = (rutaFoto != null && !rutaFoto.isEmpty());
                           
                if (fotoCargada) {
                     cargarFotoEnBoton(rutaFoto);
                } else {
                    agregarFoto.setIcon(new ImageIcon(getClass().getResource("/agregarfoto2.png")));
                }
                }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar datos: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        actualizarEstadoBotonesFoto();
    }

private void cargarFotoEnBoton(String path) {
    try {
        File imagenFile = new File(path);
        if (!imagenFile.exists()) {
            throw new IOException("Archivo no encontrado: " + path);
        }
        
        BufferedImage originalImage = ImageIO.read(imagenFile);
        Image scaledImage = originalImage.getScaledInstance(
            agregarFoto.getWidth(), 
            agregarFoto.getHeight(), 
            Image.SCALE_SMOOTH
        );
        agregarFoto.setIcon(new ImageIcon(scaledImage));
        fotoPath = path;
        fotoCargada = true;
        actualizarEstadoBotonesFoto();
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error al cargar la foto: " + ex.getMessage());
        agregarFoto.setIcon(new ImageIcon(getClass().getResource("/agregarfoto1.png")));
        fotoCargada = false;
        actualizarEstadoBotonesFoto();
    }
}
    
    private void limpiarFotoEnBD() {
    String sql = "UPDATE empleados SET FOTOS = NULL WHERE ID = ?";
    
    try (Connection conn = ConexionBD.obtenerConexion();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, idEmpleadoActual);
        pstmt.executeUpdate();
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error al limpiar la foto en la base de datos: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void limpiarCampos() {
        limpiarComponentesEnContenedor(jPanel1);
    }
    
    private void limpiarComponentesEnContenedor(java.awt.Container contenedor) {
        for (java.awt.Component component : contenedor.getComponents()) {
            if (component instanceof javax.swing.JTextField) {
                ((javax.swing.JTextField) component).setText("");
            } else if (component instanceof javax.swing.JPanel) {
                limpiarComponentesEnContenedor((javax.swing.JPanel) component);
            }
        }
    }
    
    private void actualizarEstadoBotonesFoto() {
    agregarFoto.setVisible(true);
    cambiarFoto.setVisible(fotoCargada);
    borrarFoto.setVisible(fotoCargada);
}
    
    private void seleccionarYGuardarFoto() {
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Archivos de imagen", "jpg", "jpeg", "png", "gif");
    fileChooser.setFileFilter(filter);
    
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        try {
            String nuevaRuta = guardarImagenConIncremento(selectedFile);
            cargarFotoEnBoton(nuevaRuta);
            actualizarRutaFotoEnBD(nuevaRuta);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al procesar la imagen: " + ex.getMessage());
        }
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        nombreLabel = new javax.swing.JLabel();
        nombreText = new javax.swing.JTextField();
        cedulaLabel = new javax.swing.JLabel();
        cedulaText = new javax.swing.JTextField();
        sexoLabel = new javax.swing.JLabel();
        sexoText = new javax.swing.JTextField();
        fechaNacLabel = new javax.swing.JLabel();
        fechaNacText = new javax.swing.JTextField();
        direccionLabel = new javax.swing.JLabel();
        direccionText = new javax.swing.JTextField();
        telefonoLabel = new javax.swing.JLabel();
        telefonoText = new javax.swing.JTextField();
        tlfHabitLabel = new javax.swing.JLabel();
        tlfHabitText = new javax.swing.JTextField();
        emailLabel = new javax.swing.JLabel();
        emailText = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        idText = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        cargoLabel = new javax.swing.JLabel();
        cargoText = new javax.swing.JTextField();
        fechaICLabel = new javax.swing.JLabel();
        fechaICText = new javax.swing.JTextField();
        fechaFCLabel = new javax.swing.JLabel();
        fechaFCText = new javax.swing.JTextField();
        salarioLabel = new javax.swing.JLabel();
        salarioText = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        bancoLabel = new javax.swing.JLabel();
        bancoText = new javax.swing.JTextField();
        numeroCuentaLabel = new javax.swing.JLabel();
        tipoCuentaLabel = new javax.swing.JLabel();
        pagoMovilLabel = new javax.swing.JLabel();
        numeroCuentaText = new javax.swing.JTextField();
        tipoCuentaText = new javax.swing.JTextField();
        pagoMovilText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        agregarFoto = new javax.swing.JButton();
        borrarFoto = new javax.swing.JButton();
        cambiarFoto = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Información Personal"));

        nombreLabel.setText("Nombre:");

        nombreText.setText("jTextField1");

        cedulaLabel.setText("Cédula:");

        cedulaText.setText("jTextField1");

        sexoLabel.setText("Sexo:");

        sexoText.setText("jTextField1");

        fechaNacLabel.setText("Fecha de Nacimiento:");

        fechaNacText.setText("jTextField1");

        direccionLabel.setText("Dirección:");

        direccionText.setText("jTextField1");

        telefonoLabel.setText("Teléfono:");

        telefonoText.setText("jTextField1");

        tlfHabitLabel.setText("Teléfono de Habitación:");

        tlfHabitText.setText("jTextField1");

        emailLabel.setText("Email:");

        emailText.setText("jTextField1");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Código:", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        idText.setText("jTextField2");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(idText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(idText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(nombreLabel)
                        .addGap(18, 18, 18)
                        .addComponent(nombreText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cedulaLabel)
                            .addComponent(sexoLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cedulaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sexoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(emailLabel)
                        .addGap(18, 18, 18)
                        .addComponent(emailText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tlfHabitLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tlfHabitText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(telefonoLabel)
                        .addGap(18, 18, 18)
                        .addComponent(telefonoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(direccionLabel)
                        .addGap(18, 18, 18)
                        .addComponent(direccionText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(fechaNacLabel)
                        .addGap(18, 18, 18)
                        .addComponent(fechaNacText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 196, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nombreLabel)
                            .addComponent(nombreText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cedulaLabel)
                            .addComponent(cedulaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sexoLabel)
                    .addComponent(sexoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fechaNacLabel)
                    .addComponent(fechaNacText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(direccionLabel)
                    .addComponent(direccionText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(telefonoLabel)
                    .addComponent(telefonoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tlfHabitLabel)
                    .addComponent(tlfHabitText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLabel)
                    .addComponent(emailText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Laborales"));

        cargoLabel.setText("Cargo:");

        cargoText.setText("jTextField1");

        fechaICLabel.setText("Fecha de Inicio de Contrato:");

        fechaICText.setText("jTextField1");

        fechaFCLabel.setText("Fecha de Fin de Contrato:");

        fechaFCText.setText("jTextField2");

        salarioLabel.setText("Salario:");

        salarioText.setText("jTextField1");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(salarioLabel)
                        .addGap(18, 18, 18)
                        .addComponent(salarioText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(fechaFCLabel)
                        .addGap(18, 18, 18)
                        .addComponent(fechaFCText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(fechaICLabel)
                        .addGap(18, 18, 18)
                        .addComponent(fechaICText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cargoLabel)
                        .addGap(18, 18, 18)
                        .addComponent(cargoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(281, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cargoLabel)
                    .addComponent(cargoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fechaICLabel)
                    .addComponent(fechaICText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fechaFCLabel)
                    .addComponent(fechaFCText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(salarioLabel)
                    .addComponent(salarioText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Bancarios"));

        bancoLabel.setText("Banco:");

        bancoText.setText("jTextField1");

        numeroCuentaLabel.setText("Número de Cuenta:");

        tipoCuentaLabel.setText("Tipo de Cuenta:");

        pagoMovilLabel.setText("Pago Móvil:");

        numeroCuentaText.setText("jTextField1");

        tipoCuentaText.setText("jTextField1");

        pagoMovilText.setText("jTextField1");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(pagoMovilLabel)
                        .addGap(18, 18, 18)
                        .addComponent(pagoMovilText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(tipoCuentaLabel)
                        .addGap(18, 18, 18)
                        .addComponent(tipoCuentaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(numeroCuentaLabel)
                        .addGap(18, 18, 18)
                        .addComponent(numeroCuentaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(bancoLabel)
                        .addGap(18, 18, 18)
                        .addComponent(bancoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(326, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bancoLabel)
                    .addComponent(bancoText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeroCuentaLabel)
                    .addComponent(numeroCuentaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tipoCuentaLabel)
                    .addComponent(tipoCuentaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pagoMovilLabel)
                    .addComponent(pagoMovilText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/santabr.png"))); // NOI18N
        jLabel2.setText("Ficha del Empleado");

        agregarFoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/agregarfoto2.png"))); // NOI18N
        agregarFoto.setBorder(null);
        agregarFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarFotoActionPerformed(evt);
            }
        });

        borrarFoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete.png"))); // NOI18N
        borrarFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarFotoActionPerformed(evt);
            }
        });

        cambiarFoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cambiar.png"))); // NOI18N
        cambiarFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambiarFotoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(agregarFoto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(borrarFoto)
                    .addComponent(cambiarFoto))
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(agregarFoto))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cambiarFoto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(borrarFoto)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void agregarFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarFotoActionPerformed
        // TODO add your handling code here:
        seleccionarYGuardarFoto();
    }//GEN-LAST:event_agregarFotoActionPerformed

    private void borrarFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarFotoActionPerformed
        // TODO add your handling code here:
    int confirm = JOptionPane.showConfirmDialog(
        this, 
        "¿Eliminar definitivamente la foto del empleado?", 
        "Confirmar eliminación", 
        JOptionPane.YES_NO_OPTION
    );
    
    if (confirm == JOptionPane.YES_OPTION) {
        limpiarFotoEnBD(); // Limpia el campo FOTOS en la base de datos
        fotoCargada = false;
        agregarFoto.setIcon(new ImageIcon(getClass().getResource("/agregarfoto2.png")));
        // Al borrar la foto, habilitamos el botón agregarFoto nuevamente para poder agregar otra imagen
        agregarFoto.setEnabled(true);
        actualizarEstadoBotonesFoto();
    }             
    }//GEN-LAST:event_borrarFotoActionPerformed

    private void cambiarFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambiarFotoActionPerformed
        // TODO add your handling code here:
            if (fotoCargada) {
        int opcion = JOptionPane.showConfirmDialog(
            this, 
            "¿Está seguro que desea cambiar la foto?", 
            "Confirmar cambio", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (opcion == JOptionPane.YES_OPTION) {
            seleccionarYGuardarFoto();
        }
    }
    }//GEN-LAST:event_cambiarFotoActionPerformed

private String guardarImagenConIncremento(File origen) throws IOException {
    // Usar directorio dentro del proyecto para mejor portabilidad
    File directorio = new File(System.getProperty("user.dir") + File.separator + "efotos");
    if (!directorio.exists()) {
        directorio.mkdirs();
    }
    
    int numero = 1;
    while(new File(directorio, "e_" + String.format("%02d", numero) + ".png").exists()) {
        numero++;
    }
    
    String nombreArchivo = "e_" + String.format("%02d", numero) + ".png";
    File destino = new File(directorio, nombreArchivo);
    
    BufferedImage originalImage = ImageIO.read(origen);
    BufferedImage resizedImage = new BufferedImage(
        agregarFoto.getWidth(), 
        agregarFoto.getHeight(), 
        BufferedImage.TYPE_INT_ARGB);
    
    java.awt.Graphics2D g2d = resizedImage.createGraphics();
    g2d.drawImage(originalImage, 0, 0, agregarFoto.getWidth(), agregarFoto.getHeight(), null);
    g2d.dispose();
    
    ImageIO.write(resizedImage, "png", destino);
    
    return destino.getAbsolutePath(); // Usar ruta absoluta para evitar confusiones
}

private void actualizarRutaFotoEnBD(String ruta) {
    String sql = "UPDATE empleados SET FOTOS = ? WHERE ID = ?";
    
    try (Connection conn = ConexionBD.obtenerConexion();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, ruta);
        pstmt.setInt(2, idEmpleadoActual);
        pstmt.executeUpdate();
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al actualizar la foto en la base de datos: " + ex.getMessage());
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarFoto;
    private javax.swing.JLabel bancoLabel;
    private javax.swing.JTextField bancoText;
    private javax.swing.JButton borrarFoto;
    private javax.swing.JButton cambiarFoto;
    private javax.swing.JLabel cargoLabel;
    private javax.swing.JTextField cargoText;
    private javax.swing.JLabel cedulaLabel;
    private javax.swing.JTextField cedulaText;
    private javax.swing.JLabel direccionLabel;
    private javax.swing.JTextField direccionText;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailText;
    private javax.swing.JLabel fechaFCLabel;
    private javax.swing.JTextField fechaFCText;
    private javax.swing.JLabel fechaICLabel;
    private javax.swing.JTextField fechaICText;
    private javax.swing.JLabel fechaNacLabel;
    private javax.swing.JTextField fechaNacText;
    private javax.swing.JTextField idText;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JTextField nombreText;
    private javax.swing.JLabel numeroCuentaLabel;
    private javax.swing.JTextField numeroCuentaText;
    private javax.swing.JLabel pagoMovilLabel;
    private javax.swing.JTextField pagoMovilText;
    private javax.swing.JLabel salarioLabel;
    private javax.swing.JTextField salarioText;
    private javax.swing.JLabel sexoLabel;
    private javax.swing.JTextField sexoText;
    private javax.swing.JLabel telefonoLabel;
    private javax.swing.JTextField telefonoText;
    private javax.swing.JLabel tipoCuentaLabel;
    private javax.swing.JTextField tipoCuentaText;
    private javax.swing.JLabel tlfHabitLabel;
    private javax.swing.JTextField tlfHabitText;
    // End of variables declaration//GEN-END:variables
}
