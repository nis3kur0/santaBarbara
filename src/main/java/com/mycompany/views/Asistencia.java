/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.views;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.mycompany.ConexionBD;
import javax.swing.UIManager;

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
        styles();

    }
    DefaultTableModel model;
    
    
     private void  styles () {
    
    tablaTitle.setFont( UIManager.getFont( "h1.font" ) );
    
    }

    //FUNCIONES PARA CARGAR LOS DATOS
    
    private void cargarDatosAsistenciasEnTabla() {
        String sql = "SELECT e.NOMBRE_COMPLETO, a.FECHA, a.HORA_ENTRADA, a.HORA_SALIDA, a.ESTADO, a.OBSERVACIONES "
                + "FROM asistencias a "
                + "JOIN empleados e ON a.ID_EMPLEADO = e.ID"; 
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

    
    String observaciones = observacionesText.getText().trim();
 
    if (!observaciones.isEmpty() && observaciones.contains("\n")) {
        JOptionPane.showMessageDialog(null, "Solo se permite una observación en una línea.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    if (observaciones.isEmpty()) {
        observaciones = null;
    }

    String query = "INSERT INTO asistencias (ID_EMPLEADO, FECHA, HORA_ENTRADA, ESTADO, OBSERVACIONES) VALUES (?, date('now'), ?, ?, ?)";
    
    try (Connection con = ConexionBD.obtenerConexion(); 
         PreparedStatement stmt = con.prepareStatement(query)) {

        stmt.setInt(1, idEmpleado);
        stmt.setString(2, horaActual.toString());
        stmt.setString(3, estado);
        if (observaciones != null) {
            stmt.setString(4, observaciones);
        } else {
            stmt.setNull(4, java.sql.Types.VARCHAR);
        }

        int filasInsertadas = stmt.executeUpdate();
        if (filasInsertadas > 0) {
            JOptionPane.showMessageDialog(null, "Entrada registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            jComboBox1.setSelectedIndex(0);
            actualizarTabla();
            observacionesText.setText("");  
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

        try (Connection con = ConexionBD.obtenerConexion(); PreparedStatement stmt = con.prepareStatement(sql)) {
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
        tablaTitle = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton9 = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(1010, 720));
        setMinimumSize(new java.awt.Dimension(1010, 720));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setAutoscrolls(true);
        jPanel1.setMinimumSize(new java.awt.Dimension(1010, 720));
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(47, 47, 47))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jButton7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabel2.setText("Desea añadir observaciones?");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel2))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
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
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jButton3)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton8.setText("Reporte de asistencia");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        tablaTitle.setText("ASISTENCIA DE HOY:");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Registros anteriores"));

        jToggleButton1.setText("jToggleButton1");

        jButton9.setText("jButton9");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton1))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jToggleButton1)
                .addGap(56, 56, 56)
                .addComponent(jButton9)
                .addContainerGap(151, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(583, 583, 583)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(tablaTitle)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(35, 35, 35)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 675, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28)
                .addComponent(tablaTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(106, 106, 106)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
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

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JTextArea observacionesText;
    private javax.swing.JLabel tablaTitle;
    // End of variables declaration//GEN-END:variables
}
