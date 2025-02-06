/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.santabarbara;

import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.loginandsignup.*;
import com.mycompany.views.*;
import java.awt.*;  
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author gabo
 */
public class Dashborad extends javax.swing.JFrame {

    /**
     * Creates new form Dashborad
     */
    public Dashborad() {
        initComponents();
        scaleIcons();
        initContent();
        Styles();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIcon();
          
    }
    
private void scaleIcons() {
    // Lista de botones y sus respectivos iconos
    Object[][] iconos = {
        {inicioBtn, homeIcn, "/home.png"},
        {empleBtn, nominaIcn, "/nomina.png"},
        {asistBtn, asistIcn, "/asistencia.png"},
        {configBtn, confIcn, "/conf.png"},
        {salirBtn, salirIcn, "/salir.png"}
    };

    for (Object[] icono : iconos) {
        JButton boton = (JButton) icono[0];
        JLabel etiqueta = (JLabel) icono[1];
        String ruta = (String) icono[2];

        ImageIcon originalIcon = new ImageIcon(getClass().getResource(ruta));
        Image img = originalIcon.getImage();

        int btnHeight = boton.getHeight();
        int btnWidth = btnHeight;

        Image resizedImg = img.getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
        etiqueta.setIcon(new ImageIcon(resizedImg));
    }
}
    private void setIcon() {
    java.net.URL imgURL = getClass().getResource("/logo.jpg");
    setIconImage(new ImageIcon(imgURL).getImage());

    }

    
 private void Styles() {
        // Configurar fondo
        
        inicioBtn.putClientProperty( "JButton.buttonType", "roundRect" );
        empleBtn.putClientProperty( "JButton.buttonType", "roundRect" );
        asistBtn.putClientProperty( "JButton.buttonType", "roundRect" );
        salirBtn.putClientProperty( "JButton.buttonType", "roundRect" );
        configBtn.putClientProperty( "JButton.buttonType", "roundRect" );


    }


    private void ShowJPanel(JPanel p, String panelName) {
        content.add(p, panelName);
        CardLayout layout = (CardLayout) content.getLayout();
        layout.show(content, panelName);
        content.revalidate();
        content.repaint();
    }

    public void initContent() {
        Inicio p1 = new Inicio();
        content.add(p1, "Inicio");
        CardLayout layout = (CardLayout) content.getLayout();
        layout.show(content, "Inicio");
        content.revalidate();
        content.repaint();
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
        jTable1 = new javax.swing.JTable();
        background = new javax.swing.JPanel();
        menu = new javax.swing.JPanel();
        salirIcn = new javax.swing.JLabel();
        confIcn = new javax.swing.JLabel();
        asistIcn = new javax.swing.JLabel();
        nominaIcn = new javax.swing.JLabel();
        homeIcn = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        inicioBtn = new javax.swing.JButton();
        empleBtn = new javax.swing.JButton();
        asistBtn = new javax.swing.JButton();
        salirBtn = new javax.swing.JButton();
        configBtn = new javax.swing.JButton();
        content = new javax.swing.JPanel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        background.setBackground(new java.awt.Color(255, 255, 255));

        menu.setBackground(java.awt.Color.red);
        menu.setPreferredSize(new java.awt.Dimension(270, 640));
        menu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        salirIcn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/conf.png"))); // NOI18N
        salirIcn.setText("jLabel1");
        salirIcn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        menu.add(salirIcn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 590, 40, 40));

        confIcn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/conf.png"))); // NOI18N
        confIcn.setText("jLabel1");
        confIcn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        menu.add(confIcn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 50, 50));

        asistIcn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asistencia.png"))); // NOI18N
        asistIcn.setText("jLabel1");
        asistIcn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        menu.add(asistIcn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 50, 50));

        nominaIcn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nomina.png"))); // NOI18N
        nominaIcn.setText("jLabel1");
        nominaIcn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        menu.add(nominaIcn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 50, 50));

        homeIcn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home.png"))); // NOI18N
        homeIcn.setText("jLabel1");
        homeIcn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        menu.add(homeIcn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 50, 50));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/account.png"))); // NOI18N
        menu.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, -1, -1));

        jLabel3.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("ADMIN");
        menu.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 70, 40));
        menu.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 190, 130, 20));

        inicioBtn.setBackground(new java.awt.Color(255, 102, 102));
        inicioBtn.setText("Inicio");
        inicioBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inicioBtnActionPerformed(evt);
            }
        });
        menu.add(inicioBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 270, 50));

        empleBtn.setBackground(new java.awt.Color(255, 102, 102));
        empleBtn.setText("Empleados");
        empleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                empleBtnActionPerformed(evt);
            }
        });
        menu.add(empleBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 270, 50));

        asistBtn.setBackground(new java.awt.Color(255, 102, 102));
        asistBtn.setText("Asistencia");
        asistBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asistBtnActionPerformed(evt);
            }
        });
        menu.add(asistBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 340, 270, 50));

        salirBtn.setBackground(new java.awt.Color(255, 102, 102));
        salirBtn.setText("Salir");
        salirBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salirBtnActionPerformed(evt);
            }
        });
        menu.add(salirBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 600, 270, 40));

        configBtn.setBackground(new java.awt.Color(255, 102, 102));
        configBtn.setText("Nomina");
        menu.add(configBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 400, 270, 50));

        content.setRequestFocusEnabled(false);
        content.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(background);
        background.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundLayout.createSequentialGroup()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, 919, Short.MAX_VALUE))
        );
        backgroundLayout.setVerticalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(content, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inicioBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inicioBtnActionPerformed
        Inicio inicioPanel = new Inicio();
        ShowJPanel(inicioPanel, "Inicio");        // TODO add your handling code here:
    }//GEN-LAST:event_inicioBtnActionPerformed

    private void asistBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asistBtnActionPerformed
        Asistencia asistenciaPanel = new Asistencia();
        ShowJPanel(asistenciaPanel, "Asistencia");        // TODO add your handling code here:
    }//GEN-LAST:event_asistBtnActionPerformed

    private void empleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_empleBtnActionPerformed
        Empleados empleadosPanel = new Empleados();
        ShowJPanel(empleadosPanel, "Empleados");        // TODO add your handling code here:
    }//GEN-LAST:event_empleBtnActionPerformed

    private void salirBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salirBtnActionPerformed
    int respuesta = JOptionPane.showConfirmDialog(
        null, 
        "¿Estás seguro de que quieres salir?", 
        "Confirmar salida", 
        JOptionPane.YES_NO_OPTION, 
        JOptionPane.QUESTION_MESSAGE
    );

    if (respuesta == JOptionPane.YES_OPTION) {
        System.exit(0);
    }      
    }//GEN-LAST:event_salirBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override

            public void run() {

                Login login = new Login();
                login.setVisible(true);

                if (login.isLoginSuccessful()) {
                    new Dashborad().setVisible(true);
                    login.dispose(); // Cerrar la ventana del login
                } else {
                    System.exit(0);
                }
            }
        });
    }
//private void setDate() {
//    LocalDate now = LocalDate.now();
//    int year = now.getYear();
//    int day = now.getDayOfMonth();
//    int month = now.getMonthValue();
//    String[] meses = {
//        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
//        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
//    };
//    textDate.setText("Hoy es " + day + " de " + meses[month - 1] + " de " + year);
//}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton asistBtn;
    private javax.swing.JLabel asistIcn;
    private javax.swing.JPanel background;
    private javax.swing.JLabel confIcn;
    private javax.swing.JButton configBtn;
    private javax.swing.JPanel content;
    private javax.swing.JButton empleBtn;
    private javax.swing.JLabel homeIcn;
    private javax.swing.JButton inicioBtn;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel menu;
    private javax.swing.JLabel nominaIcn;
    private javax.swing.JButton salirBtn;
    private javax.swing.JLabel salirIcn;
    // End of variables declaration//GEN-END:variables
}
