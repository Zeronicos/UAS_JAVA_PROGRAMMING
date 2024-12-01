package category;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditCategory extends JFrame {
    private Category categoryPanel;

    public EditCategory(int categoryId, Category categoryPanel) {
        this.categoryPanel = categoryPanel;

        setTitle("Edit Category");
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
        add(new JLabel()); // Placeholder kosong
        add(saveButton);

        loadCategoryData(categoryId, nameField);

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                updateCategoryInDatabase(categoryId, name);
            } else {
                JOptionPane.showMessageDialog(this, "Category name cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadCategoryData(int categoryId, JTextField nameField) {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT category_name FROM categories WHERE id = ?")) {
                stmt.setInt(1, categoryId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String categoryName = rs.getString("category_name");
                    nameField.setText(categoryName);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load category data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCategoryInDatabase(int categoryId, String categoryName) {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE categories SET category_name = ? WHERE id = ?")) {
                stmt.setString(1, categoryName);
                stmt.setInt(2, categoryId);
                stmt.executeUpdate();

                categoryPanel.loadCategoryData();

                JOptionPane.showMessageDialog(this, "Category updated successfully!");
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to update category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
