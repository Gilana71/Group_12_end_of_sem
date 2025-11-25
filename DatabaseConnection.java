// Used to connect the mysql database to java in order to import data.
// Added a test to confirm the Database was connected.
package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    // The connection details for the mysql database
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/group_project_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Gil_sql%123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Testing the connection
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Database connection successful");
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
        }
    }
}

