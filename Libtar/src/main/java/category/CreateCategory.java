package category;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CreateCategory extends JFrame {
    private Category categoryPanel;

    public CreateCategory(Category categoryPanel) {
        this.categoryPanel = categoryPanel;
        setTitle("Add Category");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel nameLabel = new JLabel("Category Name");
        nameLabel.setBounds(50, 30, 100, 20);

        JTextField nameField = new JTextField();
        nameField.setBounds(150, 30, 180, 25);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(150, 70, 80, 25);

        add(nameLabel);
        add(nameField);
        add(new JLabel());
        add(saveButton);

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                saveCategoryToDatabase(name, 0);
            } else {
                JOptionPane.showMessageDialog(this, "Category name cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void saveCategoryToDatabase(String categoryName, int stock) {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO categories (category_name, stock) VALUES (?, ?)")) {
                stmt.setString(1, categoryName);
                stmt.setInt(2, stock);
                stmt.executeUpdate();

                categoryPanel.loadCategoryData();

                JOptionPane.showMessageDialog(this, "Category added successfully!");
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
