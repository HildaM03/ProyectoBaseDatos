/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author henri
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InterfazBD extends JFrame {
    private ConexionSQLite conexionSQLite;
    private JLabel lblEstadoConexion;
    private JComboBox<String> comboTablas;
    private JButton btnVerDatos, btnInsertar, btnEliminar, btnActualizar;

    public InterfazBD() {
        conexionSQLite = new ConexionSQLite(); // Inicializar la clase de conexión

        // Configuración de la ventana principal
        setTitle("Conexión a Base de Datos SQLite");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centrar la ventana

        // Crear la etiqueta para mostrar el estado de la conexión
        lblEstadoConexion = new JLabel("Estado de la conexión: Desconectado", SwingConstants.CENTER);
        lblEstadoConexion.setForeground(Color.RED);

        // Crear un panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(5, 1, 10, 10));  // Cambié de 4 a 5 filas para incluir los nuevos botones

        // Crear los botones
        JButton btnConectar = new JButton("Conectar a la Base de Datos");
        JButton btnDesconectar = new JButton("Desconectar de la Base de Datos");
        JButton btnCrearTabla = new JButton("Crear Tabla");
        JButton btnCrearRelacion = new JButton("Crear Relaciones");

        // Crear un panel para los botones de gestión de datos
        JPanel panelGestionDatos = new JPanel();
        panelGestionDatos.setLayout(new GridLayout(5, 1, 10, 10));  // Ajustado para incluir todos los botones

        // Crear el combo box para seleccionar tablas
        comboTablas = new JComboBox<>();
        comboTablas.setEnabled(false);

        btnVerDatos = new JButton("Ver Datos");
        btnInsertar = new JButton("Insertar Datos");
        btnEliminar = new JButton("Eliminar Datos");
        btnActualizar = new JButton("Actualizar Datos");

        // Estilo de los botones
        configurarBotones(btnConectar, new Color(76, 175, 80), Color.WHITE);
        configurarBotones(btnDesconectar, new Color(255, 82, 82), Color.WHITE);
        configurarBotones(btnCrearTabla, new Color(33, 150, 243), Color.WHITE);
        configurarBotones(btnCrearRelacion, new Color(255, 193, 7), Color.WHITE);
        configurarBotones(btnVerDatos, new Color(0, 150, 136), Color.WHITE);
        configurarBotones(btnInsertar, new Color(0, 188, 212), Color.WHITE);
        configurarBotones(btnEliminar, new Color(239, 83, 80), Color.WHITE);
        configurarBotones(btnActualizar, new Color(156, 39, 176), Color.WHITE);

        // Añadir los botones al panel
        panelBotones.add(btnConectar);
        panelBotones.add(btnDesconectar);
        panelBotones.add(btnCrearTabla);
        panelBotones.add(btnCrearRelacion);

        // Añadir comboBox y botones de gestión de datos al panel
        panelGestionDatos.add(comboTablas);
        panelGestionDatos.add(btnVerDatos);
        panelGestionDatos.add(btnInsertar);
        panelGestionDatos.add(btnEliminar);
        panelGestionDatos.add(btnActualizar);

        // Añadir paneles a la ventana principal
        getContentPane().add(panelBotones, BorderLayout.WEST);
        getContentPane().add(panelGestionDatos, BorderLayout.CENTER);
        getContentPane().add(lblEstadoConexion, BorderLayout.SOUTH);

        // Asignar acciones a los botones
        btnConectar.addActionListener(e -> conectar());
        btnDesconectar.addActionListener(e -> desconectar());
        btnCrearTabla.addActionListener(e -> crearTabla());
        btnCrearRelacion.addActionListener(e -> crearRelacion());
        btnVerDatos.addActionListener(e -> verDatos());
        btnInsertar.addActionListener(e -> insertarDatos());
        btnEliminar.addActionListener(e -> eliminarDatos());
        btnActualizar.addActionListener(e -> actualizarDatos());
    }

    private void configurarBotones(JButton boton, Color colorFondo, Color colorTexto) {
        boton.setBackground(colorFondo);
        boton.setForeground(colorTexto);
        boton.setFocusPainted(false);
    }

    // Método para conectar a la base de datos SQLite
    private void conectar() {
        if (conexionSQLite.conectar()) {
            lblEstadoConexion.setText("Conectado exitosamente");
            lblEstadoConexion.setForeground(Color.GREEN); // Cambiar el color a verde
            actualizarComboTablas(); // Actualizar el comboBox con las tablas disponibles
            comboTablas.setEnabled(true);
        } else {
            lblEstadoConexion.setText("Error al conectar");
            lblEstadoConexion.setForeground(Color.RED); // Cambiar el color a rojo
        }
    }

    // Método para cerrar la conexión a la base de datos
    private void desconectar() {
        conexionSQLite.desconectar();
        lblEstadoConexion.setText("Desconectado exitosamente");
        lblEstadoConexion.setForeground(Color.RED); // Cambiar el color a rojo
        comboTablas.setEnabled(false);
    }

    // Método para crear una tabla en la base de datos
    private void crearTabla() {
        String nombreTabla = JOptionPane.showInputDialog(this, "Ingrese el nombre de la tabla:");
        if (nombreTabla == null || nombreTabla.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la tabla no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String columnas = JOptionPane.showInputDialog(this, "Ingrese las columnas de la tabla (nombre tipo, nombre tipo, ...):");
        if (columnas == null || columnas.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar al menos una columna.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS " + nombreTabla + " (" + columnas + ");";

        try {
            Connection connection = conexionSQLite.getConnection();
            if (connection != null) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Tabla creada exitosamente.");
                actualizarComboTablas(); // Actualizar el comboBox con las nuevas tablas
            } else {
                JOptionPane.showMessageDialog(this, "No hay conexión con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al crear la tabla: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }

    // Método para obtener listas de tablas
    private void actualizarComboTablas() {
        List<String> tablas = obtenerTablas();
        comboTablas.removeAllItems();
        for (String tabla : tablas) {
            comboTablas.addItem(tabla);
        }
    }

    // Método para obtener una lista de nombres de tablas
    private List<String> obtenerTablas() {
        List<String> tablas = new ArrayList<>();
        try {
            Connection connection = conexionSQLite.getConnection();
            if (connection != null) {
                ResultSet rs = connection.getMetaData().getTables(null, null, "%", null);
                while (rs.next()) {
                    tablas.add(rs.getString("TABLE_NAME"));
                }
                rs.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener las tablas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error al obtener las tablas: " + e.getMessage());
        }
        return tablas;
    }

   private void crearRelacion() {
    String tabla1 = JOptionPane.showInputDialog(this, "Ingrese el nombre de la primera tabla:");
    String columna1 = JOptionPane.showInputDialog(this, "Ingrese el nombre de la columna en la primera tabla:");

    String tabla2 = JOptionPane.showInputDialog(this, "Ingrese el nombre de la segunda tabla:");
    String columna2 = JOptionPane.showInputDialog(this, "Ingrese el nombre de la columna en la segunda tabla:");

    if (tabla1 == null || columna1 == null || tabla2 == null || columna2 == null ||
        tabla1.trim().isEmpty() || columna1.trim().isEmpty() || tabla2.trim().isEmpty() || columna2.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Todos los campos deben ser completados.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Encapsular nombres de tablas y columnas en comillas dobles
    String sql = "ALTER TABLE \"" + tabla2 + "\" ADD CONSTRAINT fk_" + tabla2 + "_" + columna2 +
                 " FOREIGN KEY (\"" + columna2 + "\") REFERENCES \"" + tabla1 + "\" (\"" + columna1 + "\");";

    try {
        Connection connection = conexionSQLite.getConnection();
        if (connection != null) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            JOptionPane.showMessageDialog(this, "Relación creada exitosamente.");
        } else {
            JOptionPane.showMessageDialog(this, "No hay conexión con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al crear la relación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        System.out.println("Error al crear la relación: " + e.getMessage());
    }
}


   private void verDatos() {
    String tabla = (String) comboTablas.getSelectedItem();
    if (tabla == null) {
        JOptionPane.showMessageDialog(this, "Seleccione una tabla.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String sql = "SELECT * FROM " + tabla + ";";
    try {
        Connection connection = conexionSQLite.getConnection();
        if (connection != null) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Obtener la metainformación de los resultados
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Listas para almacenar los datos de la tabla
            List<String[]> datos = new ArrayList<>();
            String[] columnas = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnas[i - 1] = metaData.getColumnName(i);
            }

            // Obtener los datos de cada fila
            while (rs.next()) {
                String[] fila = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    fila[i - 1] = rs.getString(i);
                }
                datos.add(fila);
            }

            // Crear la JTable con los datos obtenidos
            JTable tablaDatos = new JTable(datos.toArray(new String[0][0]), columnas);
            JScrollPane scrollPane = new JScrollPane(tablaDatos);

            // Mostrar la tabla en un JOptionPane
            JOptionPane.showMessageDialog(this, scrollPane, "Datos de la Tabla " + tabla, JOptionPane.INFORMATION_MESSAGE);
            rs.close();
        } else {
            JOptionPane.showMessageDialog(this, "No hay conexión con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al ver los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        System.out.println("Error al ver los datos: " + e.getMessage());
    }
}


    // Método auxiliar para construir el modelo de tabla para JTable
    private static javax.swing.table.TableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Nombres de las columnas
        List<String> columnNames = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        // Datos de las filas
        List<List<Object>> data = new ArrayList<>();
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }

        return new javax.swing.table.DefaultTableModel(data.toArray(new Object[0][]), columnNames.toArray());
    }

    // Métodos para insertar, eliminar y actualizar datos
    private void insertarDatos() {
        String tabla = (String) comboTablas.getSelectedItem();
        if (tabla == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una tabla.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String columnas = JOptionPane.showInputDialog(this, "Ingrese las columnas de la tabla (columna1, columna2, ...):");
        String valores = JOptionPane.showInputDialog(this, "Ingrese los valores a insertar (valor1, valor2, ...):");

        if (columnas == null || valores == null || columnas.trim().isEmpty() || valores.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Las columnas y valores no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO " + tabla + " (" + columnas + ") VALUES (" + valores + ");";

        try {
            Connection connection = conexionSQLite.getConnection();
            if (connection != null) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Datos insertados exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No hay conexión con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al insertar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error al insertar los datos: " + e.getMessage());
        }
    }

    private void eliminarDatos() {
        String tabla = (String) comboTablas.getSelectedItem();
        if (tabla == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una tabla.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String condicion = JOptionPane.showInputDialog(this, "Ingrese la condición para eliminar los datos (ej: columna = valor):");

        if (condicion == null || condicion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La condición no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "DELETE FROM " + tabla + " WHERE " + condicion + ";";

        try {
            Connection connection = conexionSQLite.getConnection();
            if (connection != null) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Datos eliminados exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No hay conexión con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error al eliminar los datos: " + e.getMessage());
        }
    }

    private void actualizarDatos() {
        String tabla = (String) comboTablas.getSelectedItem();
        if (tabla == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una tabla.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String columna = JOptionPane.showInputDialog(this, "Ingrese el nombre de la columna a actualizar:");
        String valor = JOptionPane.showInputDialog(this, "Ingrese el nuevo valor:");
        String condicion = JOptionPane.showInputDialog(this, "Ingrese la condición para actualizar los datos (ej: columna = valor):");

        if (columna == null || valor == null || condicion == null ||
            columna.trim().isEmpty() || valor.trim().isEmpty() || condicion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos deben ser completados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE " + tabla + " SET " + columna + " = " + valor + " WHERE " + condicion + ";";

        try {
            Connection connection = conexionSQLite.getConnection();
            if (connection != null) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(this, "Datos actualizados exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No hay conexión con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error al actualizar los datos: " + e.getMessage());
        }
    }

    // Clase principal para ejecutar la interfaz
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazBD interfazBD = new InterfazBD();
            interfazBD.setVisible(true);
        });
    }
}


