/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.views;

import com.mycompany.ConexionBD;
import com.mycompany.RoundedPanelShadow;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.mycompany.loginandsignup.Login;
import com.mycompany.confirmarAccionConPassword;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
/**
 *
 * 
 */
public class Configuracion extends javax.swing.JPanel {
    
    private javax.swing.JButton activeButton;
    private final java.awt.Color activeColor = new java.awt.Color(255, 0, 0);

    /**
     * Creates new form Configuracion
     */
    public Configuracion() {
        initComponents();
        
        configurePasswordField(contraActualField, "Tu contraseña actual");
        configurePasswordField(nuevaContraField, "Nueva contraseña");
        configurePasswordField(confContraField, "Confirmar nueva contraseña");
        
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0)); 
        
        horarioButton.setPreferredSize(new java.awt.Dimension(97, 41));
        horarioButton.setMinimumSize(new java.awt.Dimension(97, 41));
        horarioButton.setMaximumSize(new java.awt.Dimension(97, 41));

        seguridadButton.setPreferredSize(new java.awt.Dimension(97, 41));
        seguridadButton.setMinimumSize(new java.awt.Dimension(97, 41));
        seguridadButton.setMaximumSize(new java.awt.Dimension(97, 41));
        
        setActiveButtonStyle(horarioButton);
        seguridadButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 3, 0));
        
        jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(200, 200, 200)),
            jPanel4.getBorder()
        ));

        guardarButton1.addActionListener(this::guardarActionPerformed);
        cancelarButton1.addActionListener(this::cancelarActionPerformed);
        
        jSpinner1.setModel(new HourSpinnerModel());
        jSpinner2.setModel(new HourSpinnerModel());
    
        JSpinner.DateEditor editor1 = new JSpinner.DateEditor(jSpinner1, "HH:mm");
        JSpinner.DateEditor editor2 = new JSpinner.DateEditor(jSpinner2, "HH:mm");
    
        jSpinner1.setEditor(editor1);
        jSpinner2.setEditor(editor2);
    
        ((JSpinner.DefaultEditor) jSpinner1.getEditor()).getTextField().setFormatterFactory(
            new DefaultFormatterFactory(new DateFormatter(
            new SimpleDateFormat("HH:mm")
            ))
        );
    
        ((JSpinner.DefaultEditor) jSpinner2.getEditor()).getTextField().setFormatterFactory(
            new DefaultFormatterFactory(new DateFormatter(
                new SimpleDateFormat("HH:mm")
            ))
        );
        
        cargarHorariosPorDefecto();
    }

    private void setActiveButtonStyle(javax.swing.JButton button) {

    if (activeButton != null) {
        activeButton.setForeground(java.awt.Color.BLACK);
        activeButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 3, 0));

        if (activeButton == horarioButton) {
            activeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/reloj.png")));
        } else if (activeButton == seguridadButton) {
            activeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/llave.png")));
        }
    }
    
    button.setForeground(activeColor);
    button.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, activeColor));
    // Cambiar ícono según el botón activo
    if (button == horarioButton) {
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/relojr.png")));
    } else if (button == seguridadButton) {
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/llaver.png")));
    }
    activeButton = button;
}
    
    private void configurePasswordField(JPasswordField field, String placeholder) {
    field.setText(placeholder);
    field.setForeground(new Color(102, 102, 102));
    field.setEchoChar((char) 0); // Mostrar placeholder como texto plano

    field.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (String.valueOf(field.getPassword()).equals(placeholder)) {
                field.setText("");
                field.setForeground(Color.BLACK);
                field.setEchoChar('•'); // Carácter de contraseña
            }
        }

        public void focusLost(java.awt.event.FocusEvent evt) {
            if (field.getPassword().length == 0) {
                field.setEchoChar((char) 0);
                field.setText(placeholder);
                field.setForeground(new Color(102, 102, 102));
            }
        }
    });

    // Listener para cambiar color al modificar texto
    field.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) { updateColor(); }
        public void removeUpdate(DocumentEvent e) { updateColor(); }
        public void changedUpdate(DocumentEvent e) { updateColor(); }

        private void updateColor() {
            if (!String.valueOf(field.getPassword()).isEmpty() && 
                !String.valueOf(field.getPassword()).equals(placeholder)) {
                field.setForeground(Color.BLACK);
            }
        }
    });
}
    
    private void togglePasswordVisibility(JPasswordField field, JButton button) {
    if (field.getEchoChar() == '•') {
        field.setEchoChar((char) 0);
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/visible_icon.png")));
    } else {
        field.setEchoChar('•');
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contraseña.png")));
    }
}
    
    private void cancelarActionPerformed(java.awt.event.ActionEvent evt) {                                         
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "¿Estás seguro de que deseas descartar los cambios?",
        "Confirmar cancelación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );
    
    if (confirm == JOptionPane.YES_OPTION) {
        resetPasswordFields();
    }
}                                        

private void guardarActionPerformed(java.awt.event.ActionEvent evt) {                                        
    if (confirmarAccionConPassword.confirmarAccion(this)) {
        String nuevaContra = new String(nuevaContraField.getPassword());
        String confirmacion = new String(confContraField.getPassword());
        String contraActual = new String(contraActualField.getPassword());
        
        // Validar campos
        if (!validarCampos(contraActual, nuevaContra, confirmacion)) {
            return;
        }
        
        // Generar nuevo hash
        Login login = new Login();
        String nuevoHash = login.hashPassword(nuevaContra);
        
        // Actualizar y guardar
        Login.guardarContraseña(nuevoHash);
        Login.contraseñaValida = nuevoHash;
        
        JOptionPane.showMessageDialog(this, 
            "Contraseña actualizada exitosamente", 
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);
        
        resetPasswordFields();
    }
}

private boolean validarCampos(String actual, String nueva, String confirmacion) {
    if (actual.isEmpty() || nueva.isEmpty() || confirmacion.isEmpty()) {
        mostrarError("Todos los campos son obligatorios");
        return false;
    }
    
    if (!nueva.equals(confirmacion)) {
        mostrarError("Las nuevas contraseñas no coinciden");
        return false;
    }
    
    if (nueva.length() < 8) {
        mostrarError("La contraseña debe tener al menos 8 caracteres");
        return false;
    }
    
    // Validar contraseña actual
    Login login = new Login();
    if (!login.hashPassword(actual).equals(Login.contraseñaValida)) {
        mostrarError("La contraseña actual es incorrecta");
        return false;
    }
    
    return true;
}

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, 
          mensaje, 
          "Error", 
          JOptionPane.ERROR_MESSAGE);
    }                                     

    private void resetPasswordFields() {
        contraActualField.setText("Tu contraseña actual");
        nuevaContraField.setText("Nueva contraseña");
        confContraField.setText("Confirmar nueva contraseña");
    
        Arrays.fill(contraActualField.getPassword(), '\0');
        Arrays.fill(nuevaContraField.getPassword(), '\0');
        Arrays.fill(confContraField.getPassword(), '\0');
    
        contraActualField.setForeground(new Color(102, 102, 102));
        nuevaContraField.setForeground(new Color(102, 102, 102));
        confContraField.setForeground(new Color(102, 102, 102));
    
        contraActualField.setEchoChar((char) 0);
        nuevaContraField.setEchoChar((char) 0);
        confContraField.setEchoChar((char) 0);
}
    


public class HourSpinnerModel extends SpinnerDateModel {
    
    public HourSpinnerModel() {
        super(new Date(), null, null, Calendar.HOUR_OF_DAY);
        ajustarHoraActual();
    }
    
    private void ajustarHoraActual() {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) getValue());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        setValue(cal.getTime());
    }

    @Override
    public Object getNextValue() {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) super.getNextValue());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    @Override
    public Object getPreviousValue() {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) super.getPreviousValue());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
}

private void guardarHorarios() {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    
    try {
        String horaEntradaStr = sdf.format(jSpinner1.getValue());
        String horaSalidaStr = sdf.format(jSpinner2.getValue());
        
        String sql = "INSERT OR REPLACE INTO config_horarios (id, hora_entrada, hora_salida) VALUES (1, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, horaEntradaStr);
            pstmt.setString(2, horaSalidaStr);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error guardando horarios: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Formato de hora inválido: Use HH:mm", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private boolean validarHorarios(Date entrada, Date salida) {
    if (salida.before(entrada)) {
        JOptionPane.showMessageDialog(this, 
            "La hora de salida debe ser posterior a la de entrada", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    return true;
}

private void cargarHorariosPorDefecto() {
    String sql = "SELECT hora_entrada, hora_salida FROM config_horarios WHERE id = 1";
    try (Connection conn = ConexionBD.obtenerConexion();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            // Leer como String en lugar de Time
            String horaEntradaStr = rs.getString("hora_entrada");
            String horaSalidaStr = rs.getString("hora_salida");
            
            // Formateador para convertir String a Date
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            
            // Establecer valores en los spinners
            jSpinner1.setValue(sdf.parse(horaEntradaStr));
            jSpinner2.setValue(sdf.parse(horaSalidaStr));
            
        } else {
            // Insertar valores por defecto si no existen
            insertarHorariosPorDefecto();
            cargarHorariosPorDefecto(); // Recargar después de insertar
        }
    } catch (SQLException | ParseException e) {
        JOptionPane.showMessageDialog(this, "Error cargando horarios: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void insertarHorariosPorDefecto() {
    String sql = "INSERT INTO config_horarios (id, hora_entrada, hora_salida) VALUES (1, '08:00', '17:00')";
    
    try (Connection conn = ConexionBD.obtenerConexion();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.executeUpdate();
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error creando horarios iniciales: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new RoundedPanelShadow(20); ;
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        horarioButton = new javax.swing.JButton();
        seguridadButton = new javax.swing.JButton();
        panelcambiante = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        guardarButton = guardarButton = new javax.swing.JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Radio de 20px
                super.paintComponent(g);
            }

            @Override
            public boolean contains(int x, int y) {
                return new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20).contains(x, y);
            }
        };
        cancelarButton = cancelarButton = new javax.swing.JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo redondeado
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Borde negro
                g2.setColor(Color.GRAY); // Color del borde
                g2.setStroke(new BasicStroke(1)); // Grosor del borde (2px)
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // Dibuja el borde

                super.paintComponent(g);
            }

            @Override
            public boolean contains(int x, int y) {
                return new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20).contains(x, y);
            }
        };
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        guardarButton1 = guardarButton1 = new javax.swing.JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Radio de 20px
                super.paintComponent(g);
            }

            @Override
            public boolean contains(int x, int y) {
                return new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20).contains(x, y);
            }
        };
        cancelarButton1 = cancelarButton1 = new javax.swing.JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo redondeado
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Borde negro
                g2.setColor(Color.GRAY); // Color del borde
                g2.setStroke(new BasicStroke(1)); // Grosor del borde (2px)
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // Dibuja el borde

                super.paintComponent(g);
            }

            @Override
            public boolean contains(int x, int y) {
                return new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20).contains(x, y);
            }
        };
        ver1 = new javax.swing.JButton();
        ver2 = new javax.swing.JButton();
        ver3 = new javax.swing.JButton();
        contraActualField = new javax.swing.JPasswordField();
        nuevaContraField = new javax.swing.JPasswordField();
        confContraField = new javax.swing.JPasswordField();

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(java.awt.Color.red);

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 22)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Configuración del Sistema");

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Gestiona tus preferencias y privacidad");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        horarioButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/reloj.png"))); // NOI18N
        horarioButton.setText("Horario");
        horarioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                horarioButtonActionPerformed(evt);
            }
        });

        seguridadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/llave.png"))); // NOI18N
        seguridadButton.setText("Seguridad");
        seguridadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seguridadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(horarioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seguridadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(horarioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(seguridadButton, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelcambiante.setBackground(new java.awt.Color(255, 255, 255));
        panelcambiante.setLayout(new java.awt.CardLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Hora de entrada");

        jSpinner1.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1742232376374L), null, null, java.util.Calendar.HOUR_OF_DAY));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Hora de salida");

        jSpinner2.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1742232376374L), null, null, java.util.Calendar.HOUR_OF_DAY));

        guardarButton.setBackground(java.awt.Color.red);
        guardarButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        guardarButton.setForeground(new java.awt.Color(255, 255, 255));
        guardarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/savebl.png"))); // NOI18N
        guardarButton.setText(" Guardar Cambios");
        guardarButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        guardarButton.setContentAreaFilled(false);
        guardarButton.setOpaque(false);
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });

        cancelarButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        cancelarButton.setText("Cancelar");
        cancelarButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        cancelarButton.setContentAreaFilled(false);
        cancelarButton.setOpaque(false);
        cancelarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(cancelarButton)
                        .addGap(41, 41, 41)
                        .addComponent(guardarButton))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 855, Short.MAX_VALUE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guardarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(85, Short.MAX_VALUE))
        );

        panelcambiante.add(jPanel5, "card2");

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Contraseña Actual");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Nueva Contraseña");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Confirmar Contraseña");

        guardarButton1.setBackground(java.awt.Color.red);
        guardarButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        guardarButton1.setForeground(new java.awt.Color(255, 255, 255));
        guardarButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/savebl.png"))); // NOI18N
        guardarButton1.setText(" Guardar Cambios");
        guardarButton1.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        guardarButton1.setContentAreaFilled(false);
        guardarButton1.setOpaque(false);
        guardarButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButton1ActionPerformed(evt);
            }
        });

        cancelarButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelarButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        cancelarButton1.setText("Cancelar");
        cancelarButton1.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        cancelarButton1.setContentAreaFilled(false);
        cancelarButton1.setOpaque(false);

        ver1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contraseña.png"))); // NOI18N
        ver1.addActionListener(e -> togglePasswordVisibility(contraActualField, ver1));

        ver2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contraseña.png"))); // NOI18N
        ver2.addActionListener(e -> togglePasswordVisibility(nuevaContraField, ver2));

        ver3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contraseña.png"))); // NOI18N
        ver3.addActionListener(e -> togglePasswordVisibility(confContraField, ver3));

        contraActualField.setText("Tu contraseña actual");

        nuevaContraField.setText("jPasswordField1");

        confContraField.setText("jPasswordField1");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap(542, Short.MAX_VALUE)
                        .addComponent(cancelarButton1)
                        .addGap(41, 41, 41)
                        .addComponent(guardarButton1))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(contraActualField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ver1))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(confContraField)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ver3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(nuevaContraField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ver2))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(42, 42, 42))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ver1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(contraActualField))
                .addGap(29, 29, 29)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nuevaContraField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ver2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(confContraField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ver3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guardarButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelarButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        panelcambiante.add(jPanel6, "card3");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelcambiante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelcambiante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(204, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void seguridadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seguridadButtonActionPerformed
        // TODO add your handling code here:
        java.awt.CardLayout cl = (java.awt.CardLayout) panelcambiante.getLayout();
        cl.show(panelcambiante, "card3");
        setActiveButtonStyle(seguridadButton);
    }//GEN-LAST:event_seguridadButtonActionPerformed

    private void horarioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_horarioButtonActionPerformed
        // TODO add your handling code here:
        java.awt.CardLayout cl = (java.awt.CardLayout) panelcambiante.getLayout();
        cl.show(panelcambiante, "card2");
        setActiveButtonStyle(horarioButton);
    }//GEN-LAST:event_horarioButtonActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        // TODO add your handling code here:
    if (confirmarAccionConPassword.confirmarAccion(this)) {
        try {
            guardarHorarios();
            JOptionPane.showMessageDialog(
                this, 
                "Horarios actualizados exitosamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE
            );
            cargarHorariosPorDefecto(); // Actualizar con los nuevos valores
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this, 
                "Error al guardar horarios: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void cancelarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarButtonActionPerformed
        // TODO add your handling code here:
            int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de que deseas descartar los cambios?",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            cargarHorariosPorDefecto();
        }                                             
    
    }//GEN-LAST:event_cancelarButtonActionPerformed

    private void guardarButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_guardarButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelarButton;
    private javax.swing.JButton cancelarButton1;
    private javax.swing.JPasswordField confContraField;
    private javax.swing.JPasswordField contraActualField;
    private javax.swing.JButton guardarButton;
    private javax.swing.JButton guardarButton1;
    private javax.swing.JButton horarioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JPasswordField nuevaContraField;
    private javax.swing.JPanel panelcambiante;
    private javax.swing.JButton seguridadButton;
    private javax.swing.JButton ver1;
    private javax.swing.JButton ver2;
    private javax.swing.JButton ver3;
    // End of variables declaration//GEN-END:variables
}
