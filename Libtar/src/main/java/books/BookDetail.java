package books;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BookDetail extends JDialog {
    private final JTextField titleField;
    private final JTextField authorField;
    private final JComboBox<String> categoryComboBox;
    private final JTextField stockField;
    private final JTextField publisherField;
    private final JTextField yearField;
    private final JLabel bookImageLabel;

    private final int bookId;
    private final Book parentPanel;

    private static final String QUERY_LOAD_BOOK_DETAILS = "SELECT b.title, b.author, c.category_name, b.books_stock, b.publisher, b.year, b.image, c.id AS category_id " +
            "FROM books b INNER JOIN categories c ON b.category_id = c.id WHERE b.id = ?";
    private static final String QUERY_UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, books_stock = ?, publisher = ?, year = ?, category_id = ? WHERE id = ?";
    private static final String QUERY_DELETE_BOOK = "DELETE FROM books WHERE id = ?";
    private static final String QUERY_LOAD_CATEGORIES = "SELECT id, category_name FROM categories";

    public BookDetail(Book parentPanel, int bookId) throws Exception {
        this.parentPanel = parentPanel;
        this.bookId = bookId;

        setTitle("Book Details");
        setSize(400, 600);
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

        bookImageLabel = new JLabel("No Image");
        mainPanel.add(new JLabel("Image:"));
        mainPanel.add(bookImageLabel);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton deleteButton = new JButton("Delete");

        saveButton.addActionListener(e -> {
            try {
                updateBookDetails();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to update book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            try {
                deleteBook();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadCategories();
        loadBookDetails();
        setVisible(true);
    }

    private void loadCategories() {
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_LOAD_CATEGORIES);
             ResultSet rs = stmt.executeQuery()) {

            categoryComboBox.removeAllItems();

            while (rs.next()) {
                categoryComboBox.addItem(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadBookDetails() throws Exception {
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_LOAD_BOOK_DETAILS)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                titleField.setText(rs.getString("title"));
                authorField.setText(rs.getString("author"));
                categoryComboBox.setSelectedItem(rs.getString("category_name"));
                stockField.setText(String.valueOf(rs.getInt("books_stock")));
                publisherField.setText(rs.getString("publisher"));
                yearField.setText(String.valueOf(rs.getInt("year")));

                // Set image
                byte[] imageBytes = rs.getBytes("image");
                if (imageBytes != null) {
                    ImageIcon bookIcon = new ImageIcon(imageBytes);
                    bookImageLabel.setIcon(new ImageIcon(bookIcon.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH)));
                } else {
                    bookImageLabel.setText("No Image");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load book details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBookDetails() throws Exception {
        if (titleField.getText().trim().isEmpty() || authorField.getText().trim().isEmpty() ||
                stockField.getText().trim().isEmpty() || publisherField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int stock = Integer.parseInt(stockField.getText().trim());
            int year = Integer.parseInt(yearField.getText().trim());

            if (stock < 0 || year < 0) {
                JOptionPane.showMessageDialog(this, "Stock and Year must be positive numbers.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stock and Year must be valid numbers.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        int categoryId = getCategoryId(selectedCategory);

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_UPDATE_BOOK)) {

            stmt.setString(1, titleField.getText().trim());
            stmt.setString(2, authorField.getText().trim());
            stmt.setInt(3, Integer.parseInt(stockField.getText().trim()));
            stmt.setString(4, publisherField.getText().trim());
            stmt.setInt(5, Integer.parseInt(yearField.getText().trim()));
            stmt.setInt(6, categoryId);
            stmt.setInt(7, bookId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Book updated successfully!");
                parentPanel.refreshTable();
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void deleteBook() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = new DatabaseConnection().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(QUERY_DELETE_BOOK)) {

                stmt.setInt(1, bookId);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                    parentPanel.refreshTable();
                    dispose();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
