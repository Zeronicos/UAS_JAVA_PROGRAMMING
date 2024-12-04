package staff;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateStaff extends JFrame {
    private JTextField nameField;
    private JTextField phoneNumberField;
    private JButton saveButton;
    private DatabaseConnection dbConnection;
    private Connection conn;

    public CreateStaff() {
        setTitle("Add New Staff");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            dbConnection = new DatabaseConnection();
            conn = dbConnection.getConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Phone Number:"));
        phoneNumberField = new JTextField();
        formPanel.add(phoneNumberField);

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveStaff());
        formPanel.add(new JLabel());
        formPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
    }

    private void saveStaff() {
        String name = nameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();

        if (name.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO staffs (name, phone_number) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phoneNumber);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Staff added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            nameField.setText("");
            phoneNumberField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving staff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
