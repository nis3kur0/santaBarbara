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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class EscanearQR extends JFrame implements Runnable, ThreadFactory {

    public  Executor executor = Executors.newSingleThreadExecutor(this);
    private Webcam webcam = null;
    private WebcamPanel webcamPanel = null;

    public EscanearQR() {
        // Configurar la ventana principal
        setTitle("Lector de QR con Webcam");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cierra solo esta ventana
        setLayout(new BorderLayout());

        // Inicializar la cámara
        initWebcam();

        // Agregar el panel de la cámara a la ventana
        add(webcamPanel, BorderLayout.CENTER);

        // Ajustar el tamaño de la ventana y hacerla visible
        pack();
        setLocationRelativeTo(null);
        
 addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            if (webcam != null) {
                webcam.close(); // Cerrar la cámara
            }
        }
    });// Centrar la ventana en la pantalla
    }

    private void initWebcam() {
        // Obtener la cámara predeterminada
        webcam = Webcam.getDefault();
        if (webcam == null) {
            JOptionPane.showMessageDialog(this, "No se encontró una cámara.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // No cerrar la aplicación, solo salir del método
        }

        // Configurar el tamaño de la cámara
        webcam.setViewSize(WebcamResolution.QVGA.getSize());

        // Crear el panel de la cámara
        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setPreferredSize(WebcamResolution.QVGA.getSize());
        webcamPanel.setFPSDisplayed(true);
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(100); // Esperar 100 ms entre cada escaneo
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

                // Convertir la imagen a un formato que ZXing pueda procesar
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                try {
                    // Decodificar el código QR
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                    // No se encontró un código QR en la imagen
                }
            }

            if (result != null) {
                // Mostrar el resultado en un JDialog
                mostrarResultadoEnDialog(result.getText());
            }

        } while (webcam != null && webcam.isOpen());
    }

    private void mostrarResultadoEnDialog(String texto) {
        // Crear un JDialog para mostrar el resultado
        JDialog dialog = new JDialog(this, "Resultado del Escaneo", true);
        dialog.setLayout(new BorderLayout());

        // Crear un JTextArea para mostrar el texto
        JTextArea txtResultado = new JTextArea(texto);
        txtResultado.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtResultado);

        // Agregar el JTextArea al JDialog
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Configurar el JDialog
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this); // Centrar el diálogo respecto a la ventana principal
        dialog.setVisible(true);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "webcam-runner");
        t.setDaemon(true); // Hacer el hilo en segundo plano
        return t;
    }
}
