package books;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BookDetail extends JDialog {
    private final JLabel titleLabel;
    private final JLabel authorLabel;
    private final JLabel publisherLabel;
    private final JLabel yearLabel;
    private final JLabel stockLabel;
    private final JLabel categoryLabel;
    private final JLabel descriptionLabel;
    private final JLabel imageLabel;
    private final int bookId;

    public BookDetail(int bookId) throws Exception {
        this.bookId = bookId;

        setTitle("Book Details");
        setSize(400, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Panel utama
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(8, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize components
        titleLabel = new JLabel();
        authorLabel = new JLabel();
        publisherLabel = new JLabel();
        yearLabel = new JLabel();
        stockLabel = new JLabel();
        categoryLabel = new JLabel();
        descriptionLabel = new JLabel();
        imageLabel = new JLabel();

        mainPanel.add(new JLabel("Title:"));
        mainPanel.add(titleLabel);
        mainPanel.add(new JLabel("Author:"));
        mainPanel.add(authorLabel);
        mainPanel.add(new JLabel("Publisher:"));
        mainPanel.add(publisherLabel);
        mainPanel.add(new JLabel("Year:"));
        mainPanel.add(yearLabel);
        mainPanel.add(new JLabel("Stock:"));
        mainPanel.add(stockLabel);
        mainPanel.add(new JLabel("Category:"));
        mainPanel.add(categoryLabel);
        mainPanel.add(new JLabel("Description:"));
        mainPanel.add(descriptionLabel);

        // Add image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(new JLabel("Image:"), BorderLayout.NORTH);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Footer panel for buttons
        JPanel footerPanel = getjPanel();

        add(mainPanel, BorderLayout.CENTER);
        add(imagePanel, BorderLayout.NORTH);
        add(footerPanel, BorderLayout.SOUTH);

        loadBookDetails();

        setVisible(true);
    }

    private JPanel getjPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        closeButton.addActionListener(_ -> dispose());
        editButton.addActionListener(_ -> {
            try {
                new CreateBook(null); // Here you can pass the book panel if necessary
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening edit book dialog: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(_ -> {
            try {
                deleteBook();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        footerPanel.add(editButton);
        footerPanel.add(deleteButton);
        footerPanel.add(closeButton);
        return footerPanel;
    }

    private void loadBookDetails() throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT b.title, b.author, b.publisher, b.year, b.books_stock, c.category_name, b.description, b.image " +
                                 "FROM books b " +
                                 "INNER JOIN categories c ON b.category_id = c.id " +
                                 "WHERE b.id = ?")) {
                stmt.setInt(1, bookId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    titleLabel.setText(rs.getString("title"));
                    authorLabel.setText(rs.getString("author"));
                    publisherLabel.setText(rs.getString("publisher"));
                    yearLabel.setText(rs.getString("year"));
                    stockLabel.setText(String.valueOf(rs.getInt("books_stock")));
                    categoryLabel.setText(rs.getString("category_name"));
                    descriptionLabel.setText("<html>" + rs.getString("description") + "</html>");

                    Blob imageBlob = rs.getBlob("image");
                    if (imageBlob != null) {
                        byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                        ImageIcon icon = new ImageIcon(imageBytes);
                        imageLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
                    } else {
                        imageLabel.setText("No Image Available");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Book not found", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load book details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() throws Exception {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Delete Book", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseConnection dbConnection = new DatabaseConnection();
                try (Connection conn = dbConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE id = ?")) {
                    stmt.setInt(1, bookId);
                    int rowsDeleted = stmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                        dispose(); // Close the dialog
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete the book.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
