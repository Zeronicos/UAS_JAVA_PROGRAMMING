package authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DatabaseConnection;

public class User {
    public boolean validateUser(String username, String password) {
        boolean isValid = false;

        try {
            DatabaseConnection database = new DatabaseConnection();
            Connection conn = database.getConnection();

            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                isValid = true;
            }

            rs.close();
            stmt.close();
            database.closeConnection();
        } catch (Exception e) {
            System.err.println("Error during login validation: " + e.getMessage());
        }

        return isValid;
    }
}
