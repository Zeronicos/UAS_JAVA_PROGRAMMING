package category;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditCategory extends JDialog {
    private JTextField categoryNameField;
    private int categoryId;
    private Category categoryPanel;

    public EditCategory(int categoryId, Category categoryPanel) {
        this.categoryId = categoryId;
        this.categoryPanel = categoryPanel;

        setTitle("Edit Category");
        setSize(400, 200);
        setLocationRelativeTo(categoryPanel);
        setModal(true);

        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // Only 2 rows now
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Category Name:");
        categoryNameField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(categoryNameField);

        loadCategoryData();

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                saveCategory();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        cancelButton.addActionListener(e -> dispose());

        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadCategoryData() {
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT category_name FROM categories WHERE id = ?")) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                categoryNameField.setText(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load category data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveCategory() throws Exception {
        String categoryName = categoryNameField.getText().trim();

        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            try (Connection conn = new DatabaseConnection().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE categories SET category_name = ? WHERE id = ?")) {
                stmt.setString(1, categoryName);
                stmt.setInt(2, categoryId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Category updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                categoryPanel.loadCategoryData(null);
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
