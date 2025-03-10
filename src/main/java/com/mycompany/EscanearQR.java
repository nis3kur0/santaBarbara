package com.mycompany;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class EscanearQR extends JFrame implements Runnable, ThreadFactory {

    public  Executor executor = Executors.newSingleThreadExecutor(this);
    private Webcam webcam = null;
    private WebcamPanel webcamPanel = null;

    public EscanearQR() {
        setTitle("REGISTRA TU ASISTENCIA");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout());

        initWebcam();

        add(webcamPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        
 addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            if (webcam != null) {
                webcam.close(); 
            }
        }
    });
    }

    private void initWebcam() {

        webcam = Webcam.getDefault();
        if (webcam == null) {
            JOptionPane.showMessageDialog(this, "No se encontró una cámara.", "Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }

       
        webcam.setViewSize(WebcamResolution.QVGA.getSize());

     
        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setPreferredSize(WebcamResolution.QVGA.getSize());
        webcamPanel.setFPSDisplayed(true);
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Result result = null;
            BufferedImage image = null;

            if (webcam != null && webcam.isOpen()) {
                image = webcam.getImage();
                if (image == null) {
                    continue;
                }

               
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                try {
                    
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                    
                }
            }

       if (result != null) {
    try {
        String qrData = result.getText().trim();
        int idEmpleado = Integer.parseInt(qrData);
        registrarAsistencia(idEmpleado);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "El QR escaneado no contiene un ID de empleado válido.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


        } while (webcam != null && webcam.isOpen());
    }

    private void mostrarResultadoEnDialog(String texto) {
        JDialog dialog = new JDialog(this, "Resultado del Escaneo", true);
        dialog.setLayout(new BorderLayout());

        JTextArea txtResultado = new JTextArea(texto);
        txtResultado.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtResultado);

        dialog.add(scrollPane, BorderLayout.CENTER);

        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this); 
        dialog.setVisible(true);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "webcam-runner");
        t.setDaemon(true); 
        return t;
    }
    
    
   private void registrarAsistencia(int idEmpleado) {
    String nombreCompleto = obtenerNombreEmpleadoPorId(idEmpleado);

    if (nombreCompleto == null) {
        JOptionPane.showMessageDialog(this, "Empleado no encontrado para ID: " + idEmpleado, "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    LocalTime horaActual = LocalTime.now();

    if (verificarRegistroExistente(idEmpleado)) {
        if (verificarSalidaRegistrada(idEmpleado)) {
            JOptionPane.showMessageDialog(this, "La jornada de " + nombreCompleto + " ya ha sido completada hoy.", "Información", JOptionPane.INFORMATION_MESSAGE);
        } else {
            registrarSalida(idEmpleado, horaActual, nombreCompleto);
        }
    } else {
        registrarEntrada(idEmpleado, horaActual, nombreCompleto);
    }
}

   private void registrarEntrada(int idEmpleado, LocalTime horaActual, String nombreCompleto) {
    LocalTime horaDeEntrada = LocalTime.of(8, 0);
    String estado = horaActual.isAfter(horaDeEntrada) ? "Tarde" : "Presente";

    String query = "INSERT INTO asistencias (ID_EMPLEADO, FECHA, HORA_ENTRADA, ESTADO) VALUES (?, date('now'), ?, ?)";

    try (Connection con = ConexionBD.obtenerConexion(); 
         PreparedStatement stmt = con.prepareStatement(query)) {

        stmt.setInt(1, idEmpleado);
        stmt.setString(2, horaActual.toString());
        stmt.setString(3, estado);

        int filasInsertadas = stmt.executeUpdate();
        if (filasInsertadas > 0) {
            JOptionPane.showMessageDialog(this, "Entrada registrada correctamente para " + nombreCompleto, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al registrar la entrada: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void registrarSalida(int idEmpleado, LocalTime horaActual, String nombreCompleto) {
    String query = "UPDATE asistencias SET HORA_SALIDA = ? WHERE ID_EMPLEADO = ? AND FECHA = date('now') AND HORA_SALIDA IS NULL";

    try (Connection con = ConexionBD.obtenerConexion(); 
         PreparedStatement stmt = con.prepareStatement(query)) {

        stmt.setString(1, horaActual.toString());
        stmt.setInt(2, idEmpleado);

        int filasActualizadas = stmt.executeUpdate();
        if (filasActualizadas > 0) {
            JOptionPane.showMessageDialog(this, "Salida registrada correctamente para " + nombreCompleto, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al registrar la salida: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

   
   private String obtenerNombreEmpleadoPorId(int idEmpleado) {
    String sql = "SELECT NOMBRE_COMPLETO FROM empleados WHERE ID = ?";
    try (Connection conn = ConexionBD.obtenerConexion();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idEmpleado);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("NOMBRE_COMPLETO");
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al obtener el nombre del empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return null;
}
   
   
    
    private boolean verificarRegistroExistente(int idEmpleado) {
    String query = "SELECT 1 FROM asistencias WHERE ID_EMPLEADO = ? AND FECHA = date('now')";

    try (Connection con = ConexionBD.obtenerConexion(); 
         PreparedStatement stmt = con.prepareStatement(query)) {

        stmt.setInt(1, idEmpleado);
        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al verificar registro existente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return false;
}

private boolean verificarSalidaRegistrada(int idEmpleado) {
    String query = "SELECT 1 FROM asistencias WHERE ID_EMPLEADO = ? AND FECHA = date('now') AND HORA_SALIDA IS NOT NULL";

    try (Connection con = ConexionBD.obtenerConexion(); 
         PreparedStatement stmt = con.prepareStatement(query)) {

        stmt.setInt(1, idEmpleado);
        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al verificar salida registrada: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return false;
}

   


}
