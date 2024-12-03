package books;

import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Book extends JPanel {
    private final DefaultTableModel tableModel;

    public Book() throws Exception {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Book Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Category", "Stock", "Shelf", "Image", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only the actions column is editable for buttons
            }
        };

        JTable bookTable = new JTable(tableModel);
        bookTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        loadBookData();

        JPanel footerPanel = getjPanel();

        add(footerPanel, BorderLayout.SOUTH);

        bookTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        bookTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), this));

        // Add custom renderer for the image column
        bookTable.getColumnModel().getColumn(5).setCellRenderer(new ImageRenderer());
    }

    private JPanel getjPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Book");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton.setBackground(new Color(102, 255, 102));
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(_ -> {
            try {
                new CreateBook(this);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        footerPanel.add(addButton);
        return footerPanel;
    }

    public void loadBookData() throws Exception {
        tableModel.setRowCount(0);

        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT b.id, b.title, c.category_name, b.books_stock, s.shelf_name, b.image " +
                                 "FROM books b " +
                                 "INNER JOIN categories c ON b.category_id = c.id " +
                                 "LEFT JOIN shelves s ON b.shelf_id = s.id")) {

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("title"));
                    row.add(rs.getString("category_name"));
                    row.add(rs.getInt("books_stock"));
                    row.add(rs.getString("shelf_name"));

                    // Load image BLOB and set it for display
                    Blob imageBlob = rs.getBlob("image");
                    byte[] imageBytes = (imageBlob != null) ? imageBlob.getBytes(1, (int) imageBlob.length()) : null;
                    row.add(imageBytes);  // Store image data (byte[]) in table

                    row.add("Edit/Delete");  // Placeholder for action buttons
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTable() throws Exception {
        loadBookData();
    }

    static class ImageRenderer extends JLabel implements TableCellRenderer {
        public ImageRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof byte[] imgData) {
                // Convert byte[] to image and display it
                ImageIcon imageIcon = new ImageIcon(imgData);
                setIcon(imageIcon);
            } else {
                setIcon(null); // No image
            }
            return this;
        }
    }

    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Edit/Delete");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private String label;
        private final Book bookPanel;

        public ButtonEditor(JCheckBox checkBox, Book bookPanel) {
            super(checkBox);
            this.bookPanel = bookPanel;
            setClickCountToStart(1);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Edit/Delete" : value.toString();
            JButton button = new JButton(label);
            button.addActionListener(_ -> {
                int bookId = (int) table.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(bookPanel, "Are you sure you want to delete this book?");
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        deleteBook(bookId);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

    // Method to delete a book from the database
    private void deleteBook(int bookId) throws Exception {
        try (Connection conn = new DatabaseConnection().getConnection()) {
            String sql = "DELETE FROM books WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, bookId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book deleted successfully.");
                refreshTable();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
