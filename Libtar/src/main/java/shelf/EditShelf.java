package shelf;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditShelf extends JDialog {
    private JTextField shelfNameField;
    private JComboBox<String> categoryComboBox;
    private Shelf shelfPanel;
    private int shelfId;

    public EditShelf(Shelf shelfPanel, int shelfId) throws Exception {
        this.shelfPanel = shelfPanel;
        this.shelfId = shelfId;

        setTitle("Edit Shelf");
        setLayout(new GridLayout(3, 2, 10, 10));
        setSize(400, 200);
        setLocationRelativeTo(null);

        JLabel shelfNameLabel = new JLabel("Shelf Name:");
        shelfNameField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        categoryComboBox = new JComboBox<>();

        loadShelfDetails();
        loadCategories();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> updateShelf());
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

    private void loadShelfDetails() {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT shelf_name, category_id FROM shelves WHERE id = ?")) {
                stmt.setInt(1, shelfId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        shelfNameField.setText(rs.getString("shelf_name"));
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load shelf details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCategories() throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT id, category_name FROM categories");
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int categoryId = rs.getInt("id");
                    String categoryName = rs.getString("category_name");
                    categoryComboBox.addItem(categoryId + " - " + categoryName);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateShelf() {
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
                         "UPDATE shelves SET shelf_name = ?, category_id = ? WHERE id = ?")) {

                stmt.setString(1, shelfName);
                stmt.setInt(2, categoryId);
                stmt.setInt(3, shelfId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Shelf updated successfully!");
                shelfPanel.refreshTable();
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update shelf: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
