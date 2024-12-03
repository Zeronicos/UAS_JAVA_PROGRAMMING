package shelf;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateShelf extends JDialog {
    private JComboBox<String> categoryComboBox;
    private JTextField shelfNameField;
    private Shelf shelfPanel;

    public CreateShelf(Shelf shelfPanel) {
        this.shelfPanel = shelfPanel;

        setTitle("Create Shelf");
        setLayout(new GridLayout(3, 2, 10, 10));
        setSize(400, 200);
        setLocationRelativeTo(null);

        JLabel shelfNameLabel = new JLabel("Shelf Name:");
        shelfNameField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        categoryComboBox = new JComboBox<>();

        try {
            loadCategories();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                saveShelf();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        add(shelfNameLabel);
        add(shelfNameField);
        add(categoryLabel);
        add(categoryComboBox);
        add(saveButton);
        add(cancelButton);

        setVisible(true);
    }

    private void loadCategories() throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT id, category_name FROM categories WHERE id NOT IN (SELECT category_id FROM shelves)");
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    categoryComboBox.addItem(rs.getInt("id") + " - " + rs.getString("category_name"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveShelf() throws Exception {
        String shelfName = shelfNameField.getText();
        String selectedCategory = (String) categoryComboBox.getSelectedItem();

        if (shelfName.isEmpty() || selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int categoryId = Integer.parseInt(selectedCategory.split(" - ")[0]);

        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO shelves (shelf_name, category_id, stock) VALUES (?, ?, 0)")) {

                stmt.setString(1, shelfName);
                stmt.setInt(2, categoryId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Shelf added successfully!");
                shelfPanel.refreshTable();
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to save shelf: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
