/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 *
 * @author Nattitor
 */
public class confirmarAccionConPassword {
    
    public static boolean confirmarAccion(Component parent) {
        int confirm = JOptionPane.showConfirmDialog(
            parent,
            "¿Estás seguro de que quieres hacer los cambios?",
            "Confirmar acción",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(parent, pf, "Ingresa la contraseña", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (okCxl == JOptionPane.OK_OPTION) {
                String inputPassword = new String(pf.getPassword());
                String contraseñaValida = com.mycompany.loginandsignup.Login.contraseñaValida;
                if (inputPassword.equals(contraseñaValida)) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(parent, "Contraseña incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        return false;
    }
    
}
