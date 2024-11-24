package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection conn;

    public DatabaseConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        this.conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/libtar", "nicoth", "sansanc09");
    }

    public boolean isConnected() {
        return (this.conn != null);
    }

    public Connection getConnection() {
        return this.conn;
    }

    public void closeConnection() {
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            DatabaseConnection database = new DatabaseConnection();

            if (database.isConnected()) {
                System.out.println("Database connection success!");
            } else {
                System.out.println("Database connection failed!");
            }

            database.closeConnection();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
