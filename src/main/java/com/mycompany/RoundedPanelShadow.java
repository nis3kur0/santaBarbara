/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import java.awt.*;

public class RoundedPanelShadow extends RoundedPanel {
    
    private int shadowHeight = 3; 
    private Color shadowColor = new Color(0, 0, 0, 30); 

    public RoundedPanelShadow(int arc) {
        super(arc);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width, height - shadowHeight, arc, arc);

       
        g2.setColor(getBackground());
        g2.drawRoundRect(0, 0, width - 1, height - shadowHeight - 1, arc, arc);

     
        g2.setColor(shadowColor);
       
        g2.fillRoundRect(0, height - shadowHeight, width, shadowHeight, arc, arc);

        g2.dispose();
    }
}