package category;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateCategory extends JDialog {
    private JTextField categoryNameField;
    private Category categoryPanel;

    public CreateCategory(Category categoryPanel) {
        this.categoryPanel = categoryPanel;

        setTitle("Create Category");
        setSize(400, 200);
        setLocationRelativeTo(categoryPanel);
        setModal(true);

        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Category Name:");
        categoryNameField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(categoryNameField);

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

    private void saveCategory() throws Exception {
        String categoryName = categoryNameField.getText().trim();

        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            try (Connection conn = new DatabaseConnection().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO categories (category_name) VALUES (?)")) {
                stmt.setString(1, categoryName);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Category created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                categoryPanel.loadCategoryData(null);
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to create category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
