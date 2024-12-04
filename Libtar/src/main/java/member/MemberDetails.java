package member;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class MemberDetail extends JDialog {
    private JTextField nameField;
    private JTextField studentIdField;
    private JTextField majorField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton updateButton;
    private JButton deleteButton;
    private int memberId;
    private Member memberPanel;

    public MemberDetail(Frame parent, int memberId, Member memberPanel) throws Exception {
        super(parent, "Member Detail", true);
        this.memberId = memberId;
        this.memberPanel = memberPanel;

        // Set dialog properties
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setSize(500, 350);
        setLocationRelativeTo(parent);

        // GridBagConstraints untuk mengatur tata letak komponen
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 10, 5, 10); 

        // Komponen form untuk detail member
        addFormField("Name:", nameField = new JTextField(20), 0, constraints);
        addFormField("Student ID:", studentIdField = new JTextField(20), 1, constraints);
        addFormField("Major:", majorField = new JTextField(20), 2, constraints);
        addFormField("Email:", emailField = new JTextField(20), 3, constraints);
        addFormField("Phone Number:", phoneField = new JTextField(20), 4, constraints);

        // Tombol Update dan Delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, constraints);

        // Load data member
        loadMemberData();

        // Action listeners untuk tombol
        updateButton.addActionListener(e -> {
            try {
                updateMember();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                deleteMember();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setVisible(true);
    }

    private void addFormField(String labelText, JTextField textField, int gridY, GridBagConstraints constraints) {
        constraints.gridx = 0;
        constraints.gridy = gridY;
        constraints.anchor = GridBagConstraints.EAST; 
        JLabel label = new JLabel(labelText);
        add(label, constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        add(textField, constraints);
    }


    private void loadMemberData() throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT name, student_id, major, email, phone_number FROM members WHERE id = ?")) {

                pstmt.setInt(1, memberId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        nameField.setText(rs.getString("name"));
                        studentIdField.setText(rs.getString("student_id"));
                        majorField.setText(rs.getString("major"));
                        emailField.setText(rs.getString("email"));
                        phoneField.setText(rs.getString("phone_number"));
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load member data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMember() throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE members SET name = ?, student_id = ?, major = ?, email = ?, phone_number = ? WHERE id = ?")) {

                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, studentIdField.getText());
                pstmt.setString(3, majorField.getText());
                pstmt.setString(4, emailField.getText());
                pstmt.setString(5, phoneField.getText());
                pstmt.setInt(6, memberId);

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Member updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                memberPanel.refreshTable();

                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update member: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMember() throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "DELETE FROM members WHERE id = ?")) {

                pstmt.setInt(1, memberId);

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Member deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                memberPanel.refreshTable();

                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete member: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
