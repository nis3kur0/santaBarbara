/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import java.awt.*;
import javax.swing.JPanel;

/**
 *
 * @author Nattitor
 */
public class RoundedPanelShadow extends RoundedPanel {
    
    private int shadowHeight = 3; // Desplazamiento de la sombra
    private Color shadowColor = new Color(0, 0, 0, 30); // Color y opacidad de la sombra

    public RoundedPanelShadow(int arc) {
        super(arc);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Convertir a Graphics2D para mejorar la calidad del renderizado.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Primero, se dibuja el panel (fondo redondeado)
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width, height - shadowHeight, arc, arc);

        // Dibujar el borde del panel (opcional)
        g2.setColor(getBackground());
        g2.drawRoundRect(0, 0, width - 1, height - shadowHeight - 1, arc, arc);

        // Dibuja la sombra solo en la parte inferior.
        // Usamos fillRoundRect para que la sombra también tenga esquinas redondeadas en la parte inferior.
        g2.setColor(shadowColor);
        // Calculamos el rectángulo de la sombra: se dibuja desde (0, height - shadowHeight)
        // hasta el ancho completo y con altura = shadowHeight.
        g2.fillRoundRect(0, height - shadowHeight, width, shadowHeight, arc, arc);

        g2.dispose();
    }
}