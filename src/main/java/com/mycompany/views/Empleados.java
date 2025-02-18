/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.views;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.mycompany.ConexionBD;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 *
 * 
 */
public class Empleados extends javax.swing.JPanel {

    /**
     * Creates new form Empleados
     */
    public Empleados() {
        initComponents();
        Styles();
        model = (DefaultTableModel) this.jTable1.getModel();
        cargarDatosEnTabla();
        agregarBtn.setIcon(new ImageIcon(getClass().getResource("/agregar.png"))); 
        agregarBtn.setHorizontalTextPosition(SwingConstants.RIGHT); 
        agregarBtn.setHorizontalAlignment(SwingConstants.CENTER); 
        agregarBtn.setMargin(new Insets(0, 0, 0, 20)); // 
        editarBtn.setIcon(new ImageIcon(getClass().getResource("/editar.png"))); 
        editarBtn.setHorizontalTextPosition(SwingConstants.RIGHT); 
        editarBtn.setHorizontalAlignment(SwingConstants.CENTER); 
        editarBtn.setMargin(new Insets(0, 0, 0, 20)); //
        eliminarBtn.setIcon(new ImageIcon(getClass().getResource("/eliminar.png"))); 
        eliminarBtn.setHorizontalTextPosition(SwingConstants.RIGHT); 
        eliminarBtn.setHorizontalAlignment(SwingConstants.CENTER); 
        eliminarBtn.setMargin(new Insets(0, 0, 0, 20)); //
        limpiarBtn.setIcon(new ImageIcon(getClass().getResource("/calcular.png"))); 
        limpiarBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        limpiarBtn.setHorizontalAlignment(SwingConstants.CENTER);
        limpiarBtn.setMargin(new Insets(0, 0, 0, 20)); //

    }
 private void Styles() {
      

    //ESTILOS DEL LOOK AND FEEL


        agregarBtn.putClientProperty("JButton.buttonType", "roundRect");
        eliminarBtn.putClientProperty("JButton.buttonType", "roundRect");
        editarBtn.putClientProperty("JButton.buttonType", "roundRect");
        limpiarBtn.putClientProperty("JButton.buttonType", "roundRect");
        tablaEmpleadosLabel.setFont( UIManager.getFont( "h1.font" ) );

    }
 DefaultTableModel model;
    String url = "jdbc:sqlite:santabarbara.db";
    Connection connect;

    //FUNCIONES PARA ACTUALIZAR Y CARGAR DATOS
    private void cargarDatosEnTabla() {
        String sql = "SELECT * FROM empleados";

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            model.setRowCount(0);
            model.setColumnCount(0);

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCamposEmpleado() {
        textNombre.setText("");
        textCedula.setText("");
        dateCumpleaños.setDate(null);
        sexoBox.setSelectedIndex(0);
        textTelefono.setText("");
        textTlfhab.setText("");
        textEmail.setText("");
        textDireccion.setText("");
        textCargo.setText("");
        textSalario.setText("");
        dateInicio.setDate(null);
        dateFinal.setDate(null);
        bancoBox.setSelectedIndex(0);
        tipoCuentaBox.setSelectedIndex(0);
        textNumeroCuenta.setText("");
    }

    //FUNCION PARA REALIZAR ACCIONES
    public void agregarEmpleado() {

        String nombre = textNombre.getText().trim();
        String cedulaTexto = textCedula.getText().trim();
        String sexo = (String) sexoBox.getSelectedItem();
        String telefono = textTelefono.getText().trim();
        String telefonoHabitacion = textTlfhab.getText().trim();
        String email = textEmail.getText().trim();
        String direccion = textDireccion.getText().trim();
        String cargo = textCargo.getText().trim();
        String salarioTexto = textSalario.getText().trim();
        String banco = (String) bancoBox.getSelectedItem();
        String tipoCuenta = (String) tipoCuentaBox.getSelectedItem();
        String numeroCuenta = textNumeroCuenta.getText().trim();

      
        int cedula;
        double salario = 0.0;
        try {
            cedula = Integer.parseInt(cedulaTexto);
            if (!salarioTexto.isEmpty()) {
                salario = Double.parseDouble(salarioTexto);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Cédula y salario deben ser valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaNacimiento;

        if (dateCumpleaños.getDate() != null) {
            fechaNacimiento = sdf.format(dateCumpleaños.getDate());
        } else {
            fechaNacimiento = "2000-01-01"; 
        }
        String inicioContrato;
        if (dateInicio.getDate() != null) {
            inicioContrato = sdf.format(dateInicio.getDate());
        } else {
            inicioContrato = "2025-01-01"; 
        }

        String finContrato;
        if (dateFinal.getDate() != null) {
            finContrato = sdf.format(dateFinal.getDate());
        } else {
            finContrato = "9999-12-31";
        }

     
        nombre = nombre.isEmpty() ? "Nombre por Defecto" : nombre;
        sexo = sexo.isEmpty() ? "Masculino" : sexo;
        telefono = telefono.isEmpty() ? "000-000-0000" : telefono;
        telefonoHabitacion = telefonoHabitacion.isEmpty() ? "000-000-0000" : telefonoHabitacion;
        email = email.isEmpty() ? "no_disponible@dominio.com" : email;
        direccion = direccion.isEmpty() ? "Dirección no especificada" : direccion;
        cargo = cargo.isEmpty() ? "Empleado" : cargo;
        salarioTexto = salarioTexto.isEmpty() ? "1000.0" : salarioTexto;
        inicioContrato = inicioContrato.isEmpty() ? "2025-01-01" : inicioContrato;
        finContrato = finContrato.isEmpty() ? "9999-12-31" : finContrato;
        banco = banco.isEmpty() ? "Banesco" : banco;
        tipoCuenta = tipoCuenta.isEmpty() ? "Ahorro" : tipoCuenta;
        numeroCuenta = numeroCuenta.isEmpty() ? "0000000000000000" : numeroCuenta;

        String sql = "INSERT INTO empleados (NOMBRE_COMPLETO, CEDULA, FECHA_NACIMIENTO, SEXO, TELEFONO, TELEFONO_HABITACION, EMAIL, DIRECCION, CARGO, SALARIO, INICIO_CONTRATO, FIN_CONTRATO, BANCO, TIPO_CUENTA, NUMERO_CUENTA) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, cedula);
            pstmt.setString(3, fechaNacimiento);
            pstmt.setString(4, sexo);
            pstmt.setString(5, telefono);
            pstmt.setString(6, telefonoHabitacion);
            pstmt.setString(7, email);
            pstmt.setString(8, direccion);
            pstmt.setString(9, cargo);
            pstmt.setDouble(10, salario);
            pstmt.setString(11, inicioContrato);
            pstmt.setString(12, finContrato);
            pstmt.setString(13, banco);
            pstmt.setString(14, tipoCuenta);
            pstmt.setString(15, numeroCuenta);

            int filasInsertadas = pstmt.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null, "Empleado registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                cargarDatosEnTabla();
                limpiarCamposEmpleado();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: empleados.CEDULA")) {
                JOptionPane.showMessageDialog(null, "Ya existe un empleado con esta cédula.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al agregar el empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jCalendarDemo1 = new com.toedter.calendar.demo.JCalendarDemo();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        textNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        textCedula = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        textDireccion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        textTelefono = new javax.swing.JTextField();
        textEmail = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        textTlfhab = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        dateCumpleaños = new com.toedter.calendar.JDateChooser();
        sexoBox = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        textCargo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        textSalario = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        dateInicio = new com.toedter.calendar.JDateChooser();
        dateFinal = new com.toedter.calendar.JDateChooser();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        textNumeroCuenta = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        bancoBox = new javax.swing.JComboBox<>();
        tipoCuentaBox = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        editarBtn = new javax.swing.JButton();
        agregarBtn = new javax.swing.JButton();
        eliminarBtn = new javax.swing.JButton();
        limpiarBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        tablaEmpleadosLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        javax.swing.GroupLayout jCalendarDemo1Layout = new javax.swing.GroupLayout(jCalendarDemo1.getContentPane());
        jCalendarDemo1.getContentPane().setLayout(jCalendarDemo1Layout);
        jCalendarDemo1Layout.setHorizontalGroup(
            jCalendarDemo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jCalendarDemo1Layout.setVerticalGroup(
            jCalendarDemo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setMinimumSize(new java.awt.Dimension(1000, 720));
        setPreferredSize(new java.awt.Dimension(1000, 720));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(1920, 1080));
        jPanel2.setMinimumSize(new java.awt.Dimension(1080, 720));
        jPanel2.setName(""); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Datos basicos"));

        jLabel2.setText("Nombre y apedillo");

        textNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textNombreActionPerformed(evt);
            }
        });

        jLabel3.setText("Cedula");

        textCedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textCedulaActionPerformed(evt);
            }
        });

        jLabel5.setText("Direccion");

        textDireccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textDireccionActionPerformed(evt);
            }
        });

        jLabel10.setText("Telefono");

        textTelefono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textTelefonoActionPerformed(evt);
            }
        });

        textEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textEmailActionPerformed(evt);
            }
        });

        jLabel11.setText("Email");

        jLabel12.setText("Sexo");

        textTlfhab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textTlfhabActionPerformed(evt);
            }
        });

        jLabel13.setText("Telefono de habitacion");

        jLabel16.setText("Fecha de nacimiento");

        sexoBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecciona", "Masculino", "Femenino", "Otro" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addGap(172, 181, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(sexoBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(92, 92, 92)
                                .addComponent(jLabel11))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(152, 152, 152)
                                .addComponent(jLabel5))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(140, 140, 140))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(textNombre)
                                        .addGap(27, 27, 27)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(textEmail)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(textCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(textTlfhab))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(textTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textDireccion)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel16)
                                            .addComponent(dateCumpleaños, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel13)))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textTlfhab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sexoBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateCumpleaños, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Datos laborales"));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        textCargo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textCargoActionPerformed(evt);
            }
        });
        jPanel4.add(textCargo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 184, -1));

        jLabel4.setText("Cargo");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 35, -1, -1));
        jPanel4.add(textSalario, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 184, -1));

        jLabel6.setText("Salario");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        jLabel9.setText("Fecha fin de contrato");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, -1));

        jLabel8.setText("Fecha inicio de contrato");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, -1, -1));
        jPanel4.add(dateInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 190, -1));
        jPanel4.add(dateFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 190, -1));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Datos bancarios"));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setText("Banco");
        jPanel5.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jLabel14.setText("Tipo de cuenta");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        textNumeroCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textNumeroCuentaActionPerformed(evt);
            }
        });
        jPanel5.add(textNumeroCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 184, -1));

        jLabel15.setText("Numero de cuenta");
        jPanel5.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, -1));

        bancoBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar banco", "Banco Activo", "Banco Caroní", "Banco de Venezuela", "Banco del Tesoro", "Banco Exterior", "Banco Mercantil", "Banco Nacional de Credito", "Bancamiga Banco Microfinanciero C.A", "Bancaribe", "Bancrecer", "Banesco", "BBVA Provincial", "Delsur Banco Universal" }));
        jPanel5.add(bancoBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 190, -1));

        tipoCuentaBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Corriente", "Ahorro" }));
        jPanel5.add(tipoCuentaBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 190, -1));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Acciones"));
        jPanel3.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        editarBtn.setBackground(java.awt.SystemColor.activeCaption);
        editarBtn.setText("Editar");
        editarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarBtnActionPerformed(evt);
            }
        });
        jPanel3.add(editarBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 130, 25));

        agregarBtn.setBackground(java.awt.SystemColor.activeCaption);
        agregarBtn.setText("Agregar");
        agregarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarBtnActionPerformed(evt);
            }
        });
        jPanel3.add(agregarBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 55, 130, -1));

        eliminarBtn.setBackground(java.awt.SystemColor.activeCaption);
        eliminarBtn.setText("Eliminar");
        eliminarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarBtnActionPerformed(evt);
            }
        });
        jPanel3.add(eliminarBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 130, -1));

        limpiarBtn.setText("Limpiar campos");
        limpiarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarBtnActionPerformed(evt);
            }
        });
        jPanel3.add(limpiarBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 130, -1));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
        );

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imprimir1.png"))); // NOI18N
        jButton1.setText("Reporte de personal");

        tablaEmpleadosLabel.setText("Tabla de empleados");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

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
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.setGridColor(new java.awt.Color(204, 204, 204));
        jTable1.setName("Empleados"); // NOI18N
        jTable1.setSelectionBackground(new java.awt.Color(255, 51, 51));
        jTable1.setShowGrid(true);
        jScrollPane2.setViewportView(jTable1);

        jScrollPane1.setViewportView(jScrollPane2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(tablaEmpleadosLabel))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 927, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(150, 150, 150))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(tablaEmpleadosLabel)
                .addGap(27, 27, 27)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1029, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void textCargoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textCargoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textCargoActionPerformed

    private void textNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textNombreActionPerformed

    private void textTelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textTelefonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textTelefonoActionPerformed

    private void textDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textDireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textDireccionActionPerformed

    private void textCedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textCedulaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textCedulaActionPerformed

    private void textEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textEmailActionPerformed

    private void textNumeroCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textNumeroCuentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textNumeroCuentaActionPerformed

    private void textTlfhabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textTlfhabActionPerformed
       // TODO add your handling code here:
    }//GEN-LAST:event_textTlfhabActionPerformed
                                          

    private void editarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editarBtnActionPerformed

    private void eliminarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarBtnActionPerformed

        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una fila para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) jTable1.getValueAt(selectedRow, 0);

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este registro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

    
        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement("DELETE FROM empleados WHERE ID = ?")) {

            pstmt.setInt(1, id);
            int filasEliminadas = pstmt.executeUpdate();

            if (filasEliminadas > 0) {

                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Registro eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar el registro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_eliminarBtnActionPerformed

    private void limpiarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarBtnActionPerformed

        limpiarCamposEmpleado();
    }//GEN-LAST:event_limpiarBtnActionPerformed

    private void agregarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarBtnActionPerformed

       agregarEmpleado();
    }//GEN-LAST:event_agregarBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarBtn;
    private javax.swing.JComboBox<String> bancoBox;
    private com.toedter.calendar.JDateChooser dateCumpleaños;
    private com.toedter.calendar.JDateChooser dateFinal;
    private com.toedter.calendar.JDateChooser dateInicio;
    private javax.swing.JButton editarBtn;
    private javax.swing.JButton eliminarBtn;
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.demo.JCalendarDemo jCalendarDemo1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton limpiarBtn;
    private javax.swing.JComboBox<String> sexoBox;
    private javax.swing.JLabel tablaEmpleadosLabel;
    private javax.swing.JTextField textCargo;
    private javax.swing.JTextField textCedula;
    private javax.swing.JTextField textDireccion;
    private javax.swing.JTextField textEmail;
    private javax.swing.JTextField textNombre;
    private javax.swing.JTextField textNumeroCuenta;
    private javax.swing.JTextField textSalario;
    private javax.swing.JTextField textTelefono;
    private javax.swing.JTextField textTlfhab;
    private javax.swing.JComboBox<String> tipoCuentaBox;
    // End of variables declaration//GEN-END:variables
}
