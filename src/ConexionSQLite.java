/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author henri
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLite {
    private Connection connection;

    public ConexionSQLite() {
        // Constructor vacío
    }

    // Método para conectar a la base de datos SQLite
    public boolean conectar() {
        String url = "jdbc:sqlite:C:/db/BD_Proyecto";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Conexión establecida a la base de datos en: " + url);

            // Verificar si la conexión es exitosa obteniendo alguna información de la base de datos
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

            // Mostrar las tablas existentes en la base de datos
            StringBuilder tablas = new StringBuilder("Tablas en la base de datos:\n");
            while (rs.next()) {
                tablas.append(rs.getString("name")).append("\n");
            }
            System.out.println(tablas.toString());

            return true; // Conexión exitosa
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            return false; // Error en la conexión
        } finally {
            // Cerrar ResultSet y Statement en el bloque finally
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    // Método para cerrar la conexión a la base de datos
    public void desconectar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Desconectado exitosamente");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    // Método para obtener la conexión
    public Connection getConnection() {
        return connection;
    }
}
