/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.views;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.mycompany.ConexionBD;

/**
 *
 * @author gabo
 */
public class Asistencia extends javax.swing.JPanel {

    /**
     * Creates new form Asistencia
     */
    public Asistencia() {
        initComponents();
        model = (DefaultTableModel) this.jTable1.getModel();
        cargarEmpleadosEnComboBox();
        cargarDatosAsistenciasEnTabla();

    }
    DefaultTableModel model;

    //FUNCIONES PARA CARGAR LOS DATOS
    
    private void cargarDatosAsistenciasEnTabla() {
        String sql = "SELECT e.NOMBRE_COMPLETO, a.FECHA, a.HORA_ENTRADA, a.HORA_SALIDA, a.ESTADO, a.OBSERVACIONES "
                + "FROM asistencias a "
                + "JOIN empleados e ON a.ID_EMPLEADO = e.ID"; // Consulta para obtener todas las asistencias

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

          
            try (ResultSet rs = pstmt.executeQuery()) {

                
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

       
                model.setRowCount(0); 
                model.setColumnCount(0); 

            
                model.addColumn("Nombre Empleado");
                model.addColumn("Fecha");
                model.addColumn("Hora de Entrada");
                model.addColumn("Hora de Salida");
                model.addColumn("Estado");
                model.addColumn("Observaciones");

             
                while (rs.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = rs.getObject(i);
                    }
                    model.addRow(rowData); 
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las asistencias: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTabla() {
        String query = "SELECT a.ID_ASISTENCIA, e.NOMBRE_COMPLETO, a.FECHA, a.HORA_ENTRADA, a.HORA_SALIDA, a.ESTADO, a.OBSERVACIONES "
                + "FROM asistencias a "
                + "JOIN empleados e ON a.ID_EMPLEADO = e.ID "
                + "ORDER BY a.FECHA DESC, a.HORA_ENTRADA DESC";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement stmt = con.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

        
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
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
            JOptionPane.showMessageDialog(null, "Error al actualizar la tabla: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarEmpleadosEnComboBox() {

        jComboBox1.removeAllItems();
        jComboBox1.addItem("Selecciona un empleado");
        String query = "SELECT NOMBRE_COMPLETO FROM empleados";

        try (Connection con = ConexionBD.obtenerConexion(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String nombreEmpleado = rs.getString("NOMBRE_COMPLETO");
                jComboBox1.addItem(nombreEmpleado);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar empleados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int obtenerIdEmpleadoPorNombre(String nombre) {
        String sql = "SELECT ID FROM empleados WHERE NOMBRE_COMPLETO = ?";

        try (Connection conn = ConexionBD.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener el ID del empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
    
   //FIN//

    //VERIFICACIONES
    private boolean verificarRegistroExistente(int idEmpleado) {
        String query = "SELECT COUNT(*) FROM asistencias WHERE ID_EMPLEADO = ? AND FECHA = date('now') AND HORA_SALIDA IS NULL";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, idEmpleado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Devuelve true si hay registros abiertos
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar registro existente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private boolean verificarSalidaRegistrada(int idEmpleado) {
        String sql = "SELECT 1 FROM asistencias WHERE ID_EMPLEADO = ? AND FECHA = date('now') AND HORA_SALIDA IS NOT NULL";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idEmpleado);

            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Si ya existe una salida registrada, retorna true
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar el registro de salida: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
      //FIN//

    //FUNCIONES PARA ACCIONES
    
    public void registrarEntrada() {
        String nombreEmpleado = (String) jComboBox1.getSelectedItem();

        if (nombreEmpleado == null || nombreEmpleado.equals("Selecciona un empleado")) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un empleado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);

        if (idEmpleado == -1) {
            JOptionPane.showMessageDialog(null, "No se pudo encontrar el ID del empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (verificarRegistroExistente(idEmpleado)) {
            JOptionPane.showMessageDialog(null, "Ya existe un registro de entrada para este empleado en el día de hoy.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalTime horaActual = LocalTime.now();

        LocalTime horaDeEntrada = LocalTime.of(8, 0); 

        String estado = horaActual.isAfter(horaDeEntrada) ? "Tarde" : "Presente";

        String query = "INSERT INTO asistencias (ID_EMPLEADO, FECHA, HORA_ENTRADA, ESTADO) VALUES (?, date('now'), ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, idEmpleado);
            stmt.setString(2, horaActual.toString()); // Guardar la hora de entrada
            stmt.setString(3, estado); // Guardar el estado (Presente o Tarde)

            int filasInsertadas = stmt.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null, "Entrada registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                jComboBox1.setSelectedIndex(0);

                actualizarTabla();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar la entrada: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void registrarSalida() {
        String nombreEmpleado = (String) jComboBox1.getSelectedItem();

        if (nombreEmpleado == null || nombreEmpleado.equals("Selecciona un empleado")) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un empleado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);

        if (idEmpleado == -1) {
            JOptionPane.showMessageDialog(null, "No se pudo encontrar el ID del empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

     
        if (!verificarRegistroExistente(idEmpleado)) {
            JOptionPane.showMessageDialog(null, "No se puede registrar la salida sin haber registrado previamente la entrada.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

       
        if (verificarSalidaRegistrada(idEmpleado)) {
            JOptionPane.showMessageDialog(null, "Ya se ha registrado la salida para este empleado en el día de hoy.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        String horaSalida = LocalTime.now().toString();

        
        String sql = "UPDATE asistencias SET HORA_SALIDA = ?, ESTADO = 'Presente' WHERE ID_EMPLEADO = ? AND FECHA = date('now') AND HORA_SALIDA IS NULL";

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, horaSalida); // Hora de salida
            stmt.setInt(2, idEmpleado); // ID del empleado

            int filasActualizadas = stmt.executeUpdate();
            if (filasActualizadas > 0) {
                JOptionPane.showMessageDialog(null, "Salida registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                jComboBox1.setSelectedIndex(0);

                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar la salida. Asegúrese de que haya una entrada registrada previamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar la salida: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void registrarAusencia() {
      
        String nombreEmpleado = (String) jComboBox1.getSelectedItem();

        if (nombreEmpleado == null || nombreEmpleado.equals("Selecciona un empleado")) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un empleado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEmpleado = obtenerIdEmpleadoPorNombre(nombreEmpleado);

        if (idEmpleado == -1) {
            JOptionPane.showMessageDialog(null, "No se pudo encontrar el ID del empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

    
        if (verificarRegistroExistente(idEmpleado)) {
            JOptionPane.showMessageDialog(null, "Ya existe un registro de asistencia para este empleado en el día de hoy.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        String sql = "INSERT INTO asistencias (ID_EMPLEADO, FECHA, HORA_ENTRADA, HORA_SALIDA, ESTADO) VALUES (?, date('now'), '00:00:00', '00:00:00', 'Ausente')";

        try (Connection con = DriverManager.getConnection(url); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idEmpleado);

            int filasInsertadas = stmt.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null, "Ausencia registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

              
                jComboBox1.setSelectedIndex(0);

               
                actualizarTabla();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar la ausencia: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
      //FIN//

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        observacionesText = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(1440, 900));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setAutoscrolls(true);
        jPanel1.setMinimumSize(new java.awt.Dimension(1440, 900));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Entrada/Salida"));

        jButton1.setText("Registrar entrada");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Registrar salida");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Seleccionar empleado");

        jButton7.setText("Registrar ausencia");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(47, 47, 47))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(33, 33, 33)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addContainerGap())
        );

        jTable1.setAutoCreateRowSorter(true);
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
        jTable1.setShowGrid(true);
        jTable1.setSurrendersFocusOnKeystroke(true);
        jScrollPane1.setViewportView(jTable1);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Observaciones"));

        observacionesText.setColumns(20);
        observacionesText.setRows(5);
        jScrollPane2.setViewportView(observacionesText);

        jLabel2.setText("?Desea añadir observaciones?");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel2)))
                .addContainerGap(93, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Más acciones"));
        jPanel4.setToolTipText("");

        jButton3.setText("Vacaciones");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Incapacidad");

        jButton5.setText("Permiso");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Editar un registro");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButton6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton8.setText("Reporte de asistencia");

        jPanel3.setBackground(new java.awt.Color(255, 51, 51));

        jLabel3.setFont(new java.awt.Font("Microsoft YaHei UI Light", 0, 12)); // NOI18N
        jLabel3.setText("Registrar entrada y salida de los empleados");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(127, 127, 127)
                .addComponent(jLabel3)
                .addContainerGap(135, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1192, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(474, 474, 474)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1440, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 974, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        registrarAusencia();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        registrarEntrada();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        registrarSalida();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea observacionesText;
    // End of variables declaration//GEN-END:variables
}
