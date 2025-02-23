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
public class RoundedPanel extends JPanel {

    protected int arc;
    private Color borderColor;

    public RoundedPanel(int arc) {
        super();
        this.arc = arc;
        // Por defecto, el borde se pinta con el mismo color que el fondo
        this.borderColor = getBackground();
        setOpaque(false);
    }
    
    // Permite configurar el color del borde si se desea un color distinto
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Creamos una copia del Graphics para evitar efectos secundarios
        Graphics2D g2 = (Graphics2D) g.create();
        // Habilitamos el antialiasing para suavizar los bordes
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pintamos el fondo redondeado con el color de fondo del panel
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        // Dibujamos el borde redondeado usando el color definido (por defecto, el mismo que el fondo)
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);

        g2.dispose();
        // Se llama al método de la superclase para pintar los componentes hijos, si existiesen
        super.paintComponent(g);
    }
}
