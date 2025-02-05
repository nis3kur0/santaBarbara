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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.mycompany.ConexionBD;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

/**
 *
 * @author gabo
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

    }
    DefaultTableModel model;

    
    //ESTILOS DEL LOOK AND FEEL
    
     private void Styles() {

        agregarBtn.putClientProperty("JButton.buttonType", "roundRect");
        eliminarBtn.putClientProperty("JButton.buttonType", "roundRect");
        editarBtn.putClientProperty("JButton.buttonType", "roundRect");
        conectarBtn.putClientProperty("JButton.buttonType", "roundRect");
        limpiarBtn.putClientProperty("JButton.buttonType", "roundRect");


    }
    
    
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
    textCumpleaños.setText("");
    textSexo.setText("");
    textTelefono.setText("");
    textTlfhab.setText("");
    textEmail.setText("");
    textDireccion.setText("");
    textCargo.setText("");
    textSalario.setText("");
    textContratoInicio.setText("");
    textContratoFinal.setText("");
    textBanco.setText("");
    textTipoCuenta.setText("");
    textNumeroCuenta.setText("");
}


   
    
   //FUNCION PARA REALIZAR ACCIONES
    
   public void agregarEmpleado() {
    
    String nombre = textNombre.getText().trim();
    String cedulaTexto = textCedula.getText().trim();
    String fechaNacimiento = textCumpleaños.getText().trim();
    String sexo = textSexo.getText().trim();
    String telefono = textTelefono.getText().trim();
    String telefonoHabitacion = textTlfhab.getText().trim();
    String email = textEmail.getText().trim();
    String direccion = textDireccion.getText().trim();
    String cargo = textCargo.getText().trim();
    String salarioTexto = textSalario.getText().trim();
    String inicioContrato = textContratoInicio.getText().trim();
    String finContrato = textContratoFinal.getText().trim();
    String banco = textBanco.getText().trim();
    String tipoCuenta = textTipoCuenta.getText().trim();
    String numeroCuenta = textNumeroCuenta.getText().trim();

    // Validaciones básicas


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

    // Asignar valores predeterminados si los campos son vacíos
   nombre = nombre.isEmpty() ? "Nombre por Defecto" : nombre;
    cedulaTexto = cedulaTexto.isEmpty() ? "00000000" : cedulaTexto;
    fechaNacimiento = fechaNacimiento.isEmpty() ? "2000-01-01" : fechaNacimiento;
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
    // Validar formato de las fechas
    if (!isFechaValida(inicioContrato) || !isFechaValida(finContrato)) {
        JOptionPane.showMessageDialog(null, "Las fechas deben estar en el formato AÑO-MES-DÍA (YYYY-MM-DD).", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Insertar en la base de datos
    String sql = "INSERT INTO empleados (NOMBRE_COMPLETO, CEDULA, FECHA_NACIMIENTO, SEXO, TELEFONO, TELEFONO_HABITACION, EMAIL, DIRECCION, CARGO, SALARIO, INICIO_CONTRATO, FIN_CONTRATO, BANCO, TIPO_CUENTA, NUMERO_CUENTA) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = ConexionBD.obtenerConexion();
         PreparedStatement pstmt = con.prepareStatement(sql)) {

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

// Método para validar el formato de fecha YYYY-MM-DD
private boolean isFechaValida(String fecha) {
    try {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        formatoFecha.setLenient(false);
        formatoFecha.parse(fecha);
        return true;
    } catch (ParseException e) {
        return false;
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

        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
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
        textSexo = new javax.swing.JTextField();
        textTlfhab = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        textCumpleaños = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        textCargo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        textSalario = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        textContratoInicio = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        textContratoFinal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        textBanco = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        textTipoCuenta = new javax.swing.JTextField();
        textNumeroCuenta = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        editarBtn = new javax.swing.JButton();
        agregarBtn = new javax.swing.JButton();
        eliminarBtn = new javax.swing.JButton();
        conectarBtn = new javax.swing.JButton();
        limpiarBtn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(1440, 900));
        setPreferredSize(new java.awt.Dimension(1440, 900));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(1920, 1080));
        jPanel2.setMinimumSize(new java.awt.Dimension(1440, 900));
        jPanel2.setName(""); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(0, 0));

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
        jTable1.setGridColor(new java.awt.Color(204, 204, 204));
        jTable1.setName("Empleados"); // NOI18N
        jTable1.setSelectionBackground(new java.awt.Color(255, 51, 51));
        jTable1.setShowGrid(true);
        jScrollPane2.setViewportView(jTable1);

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

        textSexo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textSexoActionPerformed(evt);
            }
        });

        textTlfhab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textTlfhabActionPerformed(evt);
            }
        });

        jLabel13.setText("Telefono de habitacion");

        jLabel16.setText("Fecha de nacimiento");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(textNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textEmail))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(textCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(textTlfhab)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel16)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(textDireccion)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(92, 92, 92)
                                .addComponent(jLabel11))
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(165, 165, 165)
                                .addComponent(jLabel5))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(textTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textCumpleaños, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
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
                    .addComponent(textEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel12))
                    .addComponent(jLabel5))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textCumpleaños, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        textContratoInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textContratoInicioActionPerformed(evt);
            }
        });
        jPanel4.add(textContratoInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 184, -1));

        jLabel9.setText("Fecha fin de contrato");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        textContratoFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textContratoFinalActionPerformed(evt);
            }
        });
        jPanel4.add(textContratoFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 184, -1));

        jLabel8.setText("Fecha inicio de contrato");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, -1));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Datos bancarios"));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setText("Banco");
        jPanel5.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        textBanco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textBancoActionPerformed(evt);
            }
        });
        jPanel5.add(textBanco, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 184, -1));

        jLabel14.setText("Tipo de cuenta");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        textTipoCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textTipoCuentaActionPerformed(evt);
            }
        });
        jPanel5.add(textTipoCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 184, -1));

        textNumeroCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textNumeroCuentaActionPerformed(evt);
            }
        });
        jPanel5.add(textNumeroCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 184, -1));

        jLabel15.setText("Numero de cuenta");
        jPanel5.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        jLabel1.setText("Pago movil");
        jPanel5.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, -1));

        jCheckBox1.setText("Sí");
        jPanel5.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, -1, -1));

        jCheckBox2.setText("No");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });
        jPanel5.add(jCheckBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 230, -1, -1));

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
        jPanel3.add(editarBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 96, 169, 25));

        agregarBtn.setBackground(java.awt.SystemColor.activeCaption);
        agregarBtn.setText("Agregar");
        agregarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarBtnActionPerformed(evt);
            }
        });
        jPanel3.add(agregarBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 55, 169, -1));

        eliminarBtn.setBackground(java.awt.SystemColor.activeCaption);
        eliminarBtn.setText("Eliminar");
        eliminarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarBtnActionPerformed(evt);
            }
        });
        jPanel3.add(eliminarBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 139, 169, -1));

        conectarBtn.setText("Conectar");
        conectarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conectarBtnActionPerformed(evt);
            }
        });
        jPanel3.add(conectarBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, -1, -1));

        limpiarBtn.setText("Limpiar campos");
        limpiarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarBtnActionPerformed(evt);
            }
        });
        jPanel3.add(limpiarBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 160, 20));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(57, 57, 57)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
        );

        jButton1.setText("Imprimir lista");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1440, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
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

    private void textContratoFinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textContratoFinalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textContratoFinalActionPerformed

    private void textEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textEmailActionPerformed

    private void textSexoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textSexoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textSexoActionPerformed

    private void textNumeroCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textNumeroCuentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textNumeroCuentaActionPerformed

    private void textTlfhabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textTlfhabActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textTlfhabActionPerformed

    private void textBancoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textBancoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textBancoActionPerformed

    private void textContratoInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textContratoInicioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textContratoInicioActionPerformed

    private void textTipoCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textTipoCuentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textTipoCuentaActionPerformed

    private void conectarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conectarBtnActionPerformed
        // TODO add your handling code here:
        model.setRowCount(0);
        ResultSet resul = null;

        try {
            Connection conn = ConexionBD.obtenerConexion();

            if (conn != null) {
                JOptionPane.showMessageDialog(null, "Conectado");
            }
        } catch (Exception x) {
            JOptionPane.showMessageDialog(null, x.getMessage().toString());

        }
    }//GEN-LAST:event_conectarBtnActionPerformed

    private void agregarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarBtnActionPerformed

        agregarEmpleado();
     
    }

// Método para generar un nuevo ID (puedes implementar tu propia lógica)
    private int obtenerNuevoID() throws SQLException {
        String sql = "SELECT MAX(ID) FROM empleados";
        try (Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1; 
            } else {
                return 1; 
            }
        }
    }//GEN-LAST:event_agregarBtnActionPerformed

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

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void limpiarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarBtnActionPerformed

        limpiarCamposEmpleado();
    }//GEN-LAST:event_limpiarBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarBtn;
    private javax.swing.JButton conectarBtn;
    private javax.swing.JButton editarBtn;
    private javax.swing.JButton eliminarBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton limpiarBtn;
    private javax.swing.JTextField textBanco;
    private javax.swing.JTextField textCargo;
    private javax.swing.JTextField textCedula;
    private javax.swing.JTextField textContratoFinal;
    private javax.swing.JTextField textContratoInicio;
    private javax.swing.JTextField textCumpleaños;
    private javax.swing.JTextField textDireccion;
    private javax.swing.JTextField textEmail;
    private javax.swing.JTextField textNombre;
    private javax.swing.JTextField textNumeroCuenta;
    private javax.swing.JTextField textSalario;
    private javax.swing.JTextField textSexo;
    private javax.swing.JTextField textTelefono;
    private javax.swing.JTextField textTipoCuenta;
    private javax.swing.JTextField textTlfhab;
    // End of variables declaration//GEN-END:variables
}
