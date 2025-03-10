/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.views;

/**
 *
 * @author gabo
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VistaPreviaHTML {

    public static void mostrarVistaPrevia(String htmlContent) {
        JDialog previewDialog = new JDialog();
        previewDialog.setTitle("Vista Previa del Documento");
        previewDialog.setSize(800, 600);
        previewDialog.setLocationRelativeTo(null);

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText(htmlContent);
        editorPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        previewDialog.add(scrollPane);

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previewDialog.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAceptar);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);

        previewDialog.setModal(true);
        previewDialog.setVisible(true);
    }
}
