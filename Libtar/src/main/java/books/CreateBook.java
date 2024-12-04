package books;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CreateBook extends JDialog {
    private final JTextField titleField;
    private final JTextField authorField;
    private final JComboBox<String> categoryComboBox;
    private final JTextField stockField;
    private final JTextField publisherField;
    private final JTextField yearField;
    private final JTextField shelfField;

    private final Book parentPanel;

    public CreateBook(Book parentPanel) throws Exception {
        this.parentPanel = parentPanel;

        setTitle("Add New Book");
        setSize(400, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        mainPanel.add(titleField);

        mainPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        mainPanel.add(authorField);

        mainPanel.add(new JLabel("Category:"));
        categoryComboBox = new JComboBox<>();
        mainPanel.add(categoryComboBox);

        mainPanel.add(new JLabel("Stock:"));
        stockField = new JTextField();
        mainPanel.add(stockField);

        mainPanel.add(new JLabel("Publisher:"));
        publisherField = new JTextField();
        mainPanel.add(publisherField);

        mainPanel.add(new JLabel("Year:"));
        yearField = new JTextField();
        mainPanel.add(yearField);

        mainPanel.add(new JLabel("Shelf ID:"));
        shelfField = new JTextField();
        mainPanel.add(shelfField);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                saveBook();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to add book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadCategories();
        setVisible(true);
    }

    private void loadCategories() throws Exception {
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, category_name FROM categories");
             ResultSet rs = stmt.executeQuery()) {

            categoryComboBox.removeAllItems();

            while (rs.next()) {
                categoryComboBox.addItem(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBook() throws SQLException {
        String query = "INSERT INTO books (title, author, category_id, books_stock, publisher, year, shelf_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, titleField.getText().trim());
            stmt.setString(2, authorField.getText().trim());

            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            int categoryId = getCategoryId(selectedCategory);
            stmt.setInt(3, categoryId);

            stmt.setInt(4, Integer.parseInt(stockField.getText().trim()));
            stmt.setString(5, publisherField.getText().trim());
            stmt.setInt(6, Integer.parseInt(yearField.getText().trim()));
            stmt.setInt(7, Integer.parseInt(shelfField.getText().trim()));

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                parentPanel.refreshTable();
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getCategoryId(String categoryName) throws Exception {
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM categories WHERE category_name = ?")) {

            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to get category ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
}
