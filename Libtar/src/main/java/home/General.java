package home;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class LibrarySearchApp {

    public static class General extends JPanel {
        private JTextField searchField;
        private JComboBox<String> categoryComboBox;
        private JPanel bookPanel;

        public General() {
            setLayout(new BorderLayout());

            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

            searchPanel.add(new JLabel("Search:"));
            searchField = new JTextField(20);
            searchPanel.add(searchField);

            categoryComboBox = new JComboBox<>();
            try {
                loadCategories();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            searchPanel.add(new JLabel("Category:"));
            searchPanel.add(categoryComboBox);

            JButton searchButton = new JButton("Search");
            searchButton.addActionListener(e -> {
                try {
                    searchBooks();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error searching books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            searchPanel.add(searchButton);

            add(searchPanel, BorderLayout.NORTH);

            bookPanel = new JPanel();
            bookPanel.setLayout(new GridLayout(0, 4, 10, 10));
            JScrollPane scrollPane = new JScrollPane(bookPanel);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void loadCategories() throws SQLException {
            categoryComboBox.addItem("All");  
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT id, category_name FROM categories");
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    categoryComboBox.addItem(rs.getString("category_name"));
                }
            }
        }

        private void searchBooks() throws SQLException {
            String searchQuery = searchField.getText().trim();
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            Integer categoryId = getCategoryId(selectedCategory);

            String query = "SELECT b.id, b.title, b.author, c.category_name, b.books_stock, b.publisher, b.year " +
                    "FROM books b INNER JOIN categories c ON b.category_id = c.id WHERE 1=1";

            if (!searchQuery.isEmpty()) {
                query += " AND (b.title LIKE ? OR b.author LIKE ?)";
            }
          
            if (categoryId != null && !selectedCategory.equals("All")) {
                query += " AND c.id = ?";
            }

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                int paramIndex = 1;
                if (!searchQuery.isEmpty()) {
                    stmt.setString(paramIndex++, "%" + searchQuery + "%");
                    stmt.setString(paramIndex++, "%" + searchQuery + "%");
                }
                if (categoryId != null && !selectedCategory.equals("All")) {
                    stmt.setInt(paramIndex++, categoryId);
                }

                ResultSet rs = stmt.executeQuery();
                List<JPanel> bookCards = new ArrayList<>();
                while (rs.next()) {
                    JPanel card = createBookCard(rs);
                    bookCards.add(card);
                }

                bookPanel.removeAll();
                for (JPanel card : bookCards) {
                    bookPanel.add(card);
                }
                bookPanel.revalidate();
                bookPanel.repaint();
            }
        }

        private Integer getCategoryId(String categoryName) throws SQLException {
            if (categoryName == null || categoryName.isEmpty() || categoryName.equals("All")) {
                return null;
            }

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT id FROM categories WHERE category_name = ?")) {

                stmt.setString(1, categoryName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
            return null;
        }

        private JPanel createBookCard(ResultSet rs) throws SQLException {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            card.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            card.setPreferredSize(new Dimension(250, 350));
            card.setMinimumSize(new Dimension(250, 350));
            card.setMaximumSize(new Dimension(250, 350));

            String title = rs.getString("title");
            String author = rs.getString("author");
            String category = rs.getString("category_name");
            int stock = rs.getInt("books_stock");
            String publisher = rs.getString("publisher");
            int year = rs.getInt("year");

            JLabel titleLabel = new JLabel("Title: " + title);
            JLabel authorLabel = new JLabel("Author: " + author);
            JLabel categoryLabel = new JLabel("Category: " + category);
            JLabel stockLabel = new JLabel("Stock: " + stock);
            JLabel publisherLabel = new JLabel("Publisher: " + publisher);
            JLabel yearLabel = new JLabel("Year: " + year);

            card.add(titleLabel);
            card.add(authorLabel);
            card.add(categoryLabel);
            card.add(stockLabel);
            card.add(publisherLabel);
            card.add(yearLabel);

            card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

            return card;
        }


        private Connection getConnection() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/libtar";
            String user = "nicoth";
            String password = "sansanc09";  // Update your password
            return DriverManager.getConnection(url, user, password);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Library Book Search");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            General generalPanel = null;
            try {
                generalPanel = new General();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error loading General panel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return;
            }
            frame.add(generalPanel);

            frame.setVisible(true);
        });
    }
}
