package member;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateMember extends JDialog {
    private JTextField nameField;
    private JTextField studentIdField;
    private JTextField majorField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton addButton;
    private Member memberPanel;

    public CreateMember(Frame parent, Member memberPanel) throws Exception {
        super(parent, "Create Member", true);
        this.memberPanel = memberPanel;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setSize(400, 400);
        setLocationRelativeTo(parent);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 10, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.EAST;
        JLabel nameLabel = new JLabel("Name:");
        add(nameLabel, constraints);

        constraints.gridx = 1;
        nameField = new JTextField(20);
        add(nameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        JLabel studentIdLabel = new JLabel("Student ID:");
        add(studentIdLabel, constraints);

        constraints.gridx = 1;
        studentIdField = new JTextField(20);
        add(studentIdField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        JLabel majorLabel = new JLabel("Major:");
        add(majorLabel, constraints);

        constraints.gridx = 1;
        majorField = new JTextField(20);
        add(majorField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        JLabel emailLabel = new JLabel("Email:");
        add(emailLabel, constraints);

        constraints.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        JLabel phoneLabel = new JLabel("Phone Number:");
        add(phoneLabel, constraints);

        constraints.gridx = 1;
        phoneField = new JTextField(20);
        add(phoneField, constraints);
        
        addButton = new JButton("Add");

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(20, 10, 10, 10);
        add(addButton, constraints);

        addButton.addActionListener(e -> {
            try {
                addMember();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setVisible(true);
    }

    private void addMember() throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO members (name, student_id, major, email, phone_number) VALUES (?, ?, ?, ?, ?)")) {

                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, studentIdField.getText());
                pstmt.setString(3, majorField.getText());
                pstmt.setString(4, emailField.getText());
                pstmt.setString(5, phoneField.getText());

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Member added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                memberPanel.refreshTable();

                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add member: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
