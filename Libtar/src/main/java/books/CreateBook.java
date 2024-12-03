package books;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class CreateBook extends JDialog {
    private JTextField titleField, authorField, publisherField, yearField, stockField;
    private JComboBox<String> categoryComboBox;
    private JTextArea descriptionArea;
    private JButton saveButton, cancelButton, chooseImageButton;
    private File selectedImageFile;

    public CreateBook(Book bookPanel) throws Exception {
        setTitle("Create Book");
        setLayout(new GridLayout(9, 2, 10, 10));
        setSize(400, 350);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField();
        JLabel authorLabel = new JLabel("Author:");
        authorField = new JTextField();
        JLabel publisherLabel = new JLabel("Publisher:");
        publisherField = new JTextField();
        JLabel yearLabel = new JLabel("Year:");
        yearField = new JTextField();
        JLabel stockLabel = new JLabel("Stock:");
        stockField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        categoryComboBox = new JComboBox<>();
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionArea = new JTextArea();

        JLabel imageLabel = new JLabel("Image:");
        chooseImageButton = new JButton("Choose Image");
        chooseImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(CreateBook.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedImageFile = fileChooser.getSelectedFile();
                }
            }
        });

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> {
            try {
                saveBook(bookPanel);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Layout
        add(titleLabel);
        add(titleField);
        add(authorLabel);
        add(authorField);
        add(publisherLabel);
        add(publisherField);
        add(yearLabel);
        add(yearField);
        add(stockLabel);
        add(stockField);
        add(categoryLabel);
        add(categoryComboBox);
        add(descriptionLabel);
        add(new JScrollPane(descriptionArea));
        add(imageLabel);
        add(chooseImageButton);
        add(saveButton);
        add(cancelButton);

        // Load categories when the dialog opens
        loadCategories();

        setVisible(true);
    }

    private void loadCategories() throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, category_name FROM categories")) {

                while (rs.next()) {
                    categoryComboBox.addItem(rs.getString("category_name"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBook(Book bookPanel) throws Exception {
        String title = titleField.getText();
        String author = authorField.getText();
        String publisher = publisherField.getText();
        String year = yearField.getText();
        String stock = stockField.getText();
        String description = descriptionArea.getText();
        int categoryId = categoryComboBox.getSelectedIndex() + 1; // Assuming you load categories starting from 1

        if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || stock.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection()) {
                String sql = "INSERT INTO books (title, author, publisher, year, books_stock, category_id, description, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, title);
                    stmt.setString(2, author);
                    stmt.setString(3, publisher);
                    stmt.setString(4, year);
                    stmt.setInt(5, Integer.parseInt(stock));
                    stmt.setInt(6, categoryId);
                    stmt.setString(7, description);

                    if (selectedImageFile != null && selectedImageFile.exists()) {
                        try (FileInputStream fis = new FileInputStream(selectedImageFile)) {
                            stmt.setBinaryStream(8, fis, (int) selectedImageFile.length());
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(this, "Error reading the image file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        stmt.setNull(8, Types.BLOB); // No image selected
                    }

                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book added successfully!");
                    bookPanel.refreshTable();
                    dispose();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to save book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
