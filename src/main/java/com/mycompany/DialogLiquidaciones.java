package com.mycompany;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DialogLiquidaciones extends JDialog {
    private JComboBox<EmpleadoComboItem> comboEmpleados;
    private JComboBox<String> comboTipoLiquidacion;
    private JDateChooser dcFechaLiquidacion;
    private JButton btnCalcular;
    private JButton btnAplicar;
    private JButton btnCancelar;

    private JTextField txtPrestaciones;
    private JTextField txtVacaciones;
    private JTextField txtUtilidades;
    private JTextField txtTotal;
    private JTextField txtDiasTrabajados;
    private JTextField txtPeriodoTrabajado;
    private JTextArea txtObservaciones;

    private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public DialogLiquidaciones(JFrame parent) {
        super(parent, "Liquidación de Empleado", true);
        initComponents();
        setSize(650, 600);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de selección
        JPanel panelSeleccion = new JPanel(new GridLayout(4, 2, 10, 10));

        // Combo Empleados
        panelSeleccion.add(new JLabel("Empleado:"));
        comboEmpleados = new JComboBox<>();
        cargarEmpleadosActivos();
        panelSeleccion.add(comboEmpleados);

        // Combo Tipo Liquidación
        panelSeleccion.add(new JLabel("Tipo de Liquidación:"));
        comboTipoLiquidacion = new JComboBox<>(new String[]{
                "Renuncia",
                "Despido",
                "Jubilación"
        });
        panelSeleccion.add(comboTipoLiquidacion);

        // Fecha Liquidación
        panelSeleccion.add(new JLabel("Fecha de Liquidación:"));
        dcFechaLiquidacion = new JDateChooser();
        dcFechaLiquidacion.setDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        panelSeleccion.add(dcFechaLiquidacion);

        panelPrincipal.add(panelSeleccion, BorderLayout.NORTH);

        // Panel de resultados
        JPanel panelResultados = new JPanel(new GridLayout(7, 2, 10, 10));
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados del Cálculo"));

        panelResultados.add(new JLabel("Periodo Trabajado:"));
        txtPeriodoTrabajado = new JTextField();
        txtPeriodoTrabajado.setEditable(false);
        panelResultados.add(txtPeriodoTrabajado);

        panelResultados.add(new JLabel("Días Trabajados (Reales):"));
        txtDiasTrabajados = new JTextField();
        txtDiasTrabajados.setEditable(false);
        panelResultados.add(txtDiasTrabajados);

        panelResultados.add(new JLabel("Prestaciones Sociales:"));
        txtPrestaciones = new JTextField();
        txtPrestaciones.setEditable(false);
        panelResultados.add(txtPrestaciones);

        panelResultados.add(new JLabel("Vacaciones no disfrutadas:"));
        txtVacaciones = new JTextField();
        txtVacaciones.setEditable(false);
        panelResultados.add(txtVacaciones);

        panelResultados.add(new JLabel("Utilidades Proporcionales:"));
        txtUtilidades = new JTextField();
        txtUtilidades.setEditable(false);
        panelResultados.add(txtUtilidades);

        panelResultados.add(new JLabel("Total Liquidación:"));
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        panelResultados.add(txtTotal);

        panelResultados.add(new JLabel("Observaciones:"));
        txtObservaciones = new JTextArea(3, 20);
        JScrollPane scroll = new JScrollPane(txtObservaciones);
        panelResultados.add(scroll);

        panelPrincipal.add(panelResultados, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnCalcular = new JButton("Calcular Liquidación");
        btnCalcular.addActionListener(e -> calcularLiquidacion());
        panelBotones.add(btnCalcular);

        btnAplicar = new JButton("Aplicar Liquidación");
        btnAplicar.addActionListener(e -> {
            if (confirmarAccionConPassword.confirmarAccion(this)) {
                aplicarLiquidacion();
            }
        });
        btnAplicar.setEnabled(false);
        panelBotones.add(btnAplicar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarEmpleadosActivos() {
        try (Connection con = ConexionBD.obtenerConexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT ID, NOMBRE_COMPLETO FROM empleados WHERE ESTADO = 'Activo'")) {

            comboEmpleados.removeAllItems();
            comboEmpleados.addItem(new EmpleadoComboItem(-1, "Seleccione un empleado"));

            while (rs.next()) {
                comboEmpleados.addItem(new EmpleadoComboItem(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE_COMPLETO")
                ));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calcularLiquidacion() {
        EmpleadoComboItem empleado = (EmpleadoComboItem) comboEmpleados.getSelectedItem();
        if (empleado == null || empleado.getId() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipoLiquidacion = (String) comboTipoLiquidacion.getSelectedItem();
        if (tipoLiquidacion == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de liquidación", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = ConexionBD.obtenerConexion()) {
            // 1. Obtener datos básicos del empleado
            double salario = 0;
            LocalDate fechaIngreso = null;

            try (PreparedStatement pstmt = con.prepareStatement(
                    "SELECT SALARIO, INICIO_CONTRATO FROM empleados WHERE ID = ?")) {
                pstmt.setInt(1, empleado.getId());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    salario = rs.getDouble("SALARIO");
                    String fechaIngresoStr = rs.getString("INICIO_CONTRATO");
                    fechaIngreso = fechaIngresoStr != null ? LocalDate.parse(fechaIngresoStr, DB_DATE_FORMATTER) : null;
                }
            }

            if (fechaIngreso == null) {
                throw new SQLException("No se pudo obtener la fecha de ingreso del empleado");
            }

            LocalDate fechaLiquidacion = dcFechaLiquidacion.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            // 2. Calcular días trabajados REALES (basados en asistencias)
            int diasTrabajadosReales = calcularDiasTrabajadosReales(con, empleado.getId(), fechaIngreso, fechaLiquidacion);

            // 3. Calcular meses trabajados para prestaciones y vacaciones
            long mesesTrabajados = ChronoUnit.MONTHS.between(
                    fechaIngreso.withDayOfMonth(1),
                    fechaLiquidacion.withDayOfMonth(1));

            // 4. Calcular salario diario
            double salarioDiario = salario / 30;

            // 5. Calcular conceptos de liquidación
            double prestaciones = calcularPrestaciones((int) mesesTrabajados, salarioDiario);
            double vacaciones = mesesTrabajados * 1.5 * salarioDiario;
            double utilidades = (diasTrabajadosReales / 360.0) * 15 * salarioDiario;
            double indemnizacion = tipoLiquidacion.equals("Despido") ? 
                    calcularIndemnizacion((int) mesesTrabajados, salario) : 0;

            // 6. Mostrar resultados
            txtPeriodoTrabajado.setText(String.format("%s - %s", 
                    fechaIngreso.format(DISPLAY_DATE_FORMATTER), 
                    fechaLiquidacion.format(DISPLAY_DATE_FORMATTER)));
            txtDiasTrabajados.setText(String.valueOf(diasTrabajadosReales));
            txtPrestaciones.setText(String.format("Bs. %,.2f", prestaciones));
            txtVacaciones.setText(String.format("Bs. %,.2f", vacaciones));
            txtUtilidades.setText(String.format("Bs. %,.2f", utilidades));

            double total = prestaciones + vacaciones + utilidades + indemnizacion;
            txtTotal.setText(String.format("Bs. %,.2f", total));

            btnAplicar.setEnabled(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al calcular liquidación: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calcularDiasTrabajadosReales(Connection con, int idEmpleado, LocalDate fechaIngreso, LocalDate fechaLiquidacion) throws SQLException {
        String fechaInicioStr = fechaIngreso.format(DB_DATE_FORMATTER);
        String fechaFinStr = fechaLiquidacion.format(DB_DATE_FORMATTER);

        String sql = "SELECT COUNT(*) AS dias_presentes " +
                     "FROM asistencias " +
                     "WHERE ID_EMPLEADO = ? AND " +
                     "DATE(FECHA) BETWEEN DATE(?) AND DATE(?) " +
                     "AND ESTADO = 'Presente'";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idEmpleado);
            pstmt.setString(2, fechaInicioStr);
            pstmt.setString(3, fechaFinStr);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("dias_presentes") : 0;
        }
    }

    private double calcularPrestaciones(int mesesTrabajados, double salarioDiario) {
        if (mesesTrabajados <= 3) {
            return mesesTrabajados * 5 * salarioDiario;
        } else {
            return (15 + (mesesTrabajados - 3) * 2) * salarioDiario;
        }
    }

    private double calcularIndemnizacion(int mesesTrabajados, double salarioMensual) {
        if (mesesTrabajados < 3) {
            return 7 * (salarioMensual / 30);
        } else {
            return (mesesTrabajados / 12.0) * salarioMensual;
        }
    }

    private void aplicarLiquidacion() {
        EmpleadoComboItem empleado = (EmpleadoComboItem) comboEmpleados.getSelectedItem();
        if (empleado == null || empleado.getId() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);

            LocalDate fechaLiquidacion = dcFechaLiquidacion.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            String fechaLiquidacionStr = fechaLiquidacion.format(DB_DATE_FORMATTER);
            LocalDate fechaIngreso = null;

            try (PreparedStatement pstmtFechaIngreso = con.prepareStatement(
                    "SELECT INICIO_CONTRATO FROM empleados WHERE ID = ?")) {
                pstmtFechaIngreso.setInt(1, empleado.getId());
                ResultSet rsFechaIngreso = pstmtFechaIngreso.executeQuery();
                if (rsFechaIngreso.next()) {
                    String fechaIngresoStr = rsFechaIngreso.getString("INICIO_CONTRATO");
                    fechaIngreso = fechaIngresoStr != null ? LocalDate.parse(fechaIngresoStr, DB_DATE_FORMATTER) : null;
                }
            }

            if (fechaIngreso == null) {
                throw new SQLException("No se pudo obtener la fecha de ingreso del empleado");
            }

            // 1. Insertar registro de liquidación
            try (PreparedStatement pstmt = con.prepareStatement(
                    "INSERT INTO liquidaciones (id_empleado, fecha_liquidacion, motivo, " +
                            "dias_trabajados, prestaciones, vacaciones, utilidades, total_liquidacion, observaciones) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

                pstmt.setInt(1, empleado.getId());
                pstmt.setString(2, fechaLiquidacionStr);
                pstmt.setString(3, (String) comboTipoLiquidacion.getSelectedItem());
                pstmt.setInt(4, Integer.parseInt(txtDiasTrabajados.getText()));
                pstmt.setDouble(5, Double.parseDouble(txtPrestaciones.getText().replace("Bs. ", "").replace(",", "")));
                pstmt.setDouble(6, Double.parseDouble(txtVacaciones.getText().replace("Bs. ", "").replace(",", "")));
                pstmt.setDouble(7, Double.parseDouble(txtUtilidades.getText().replace("Bs. ", "").replace(",", "")));
                pstmt.setDouble(8, Double.parseDouble(txtTotal.getText().replace("Bs. ", "").replace(",", "")));
                pstmt.setString(9, txtObservaciones.getText());

                pstmt.executeUpdate();
            }

            // 2. Actualizar estado del empleado
            try (PreparedStatement pstmtEmpleado = con.prepareStatement(
                    "UPDATE empleados SET ESTADO = 'Inactivo' WHERE ID = ?")) {
                pstmtEmpleado.setInt(1, empleado.getId());
                pstmtEmpleado.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "Liquidación aplicada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error al aplicar liquidación: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class EmpleadoComboItem {
        private int id;
        private String nombre;

        public EmpleadoComboItem(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}
