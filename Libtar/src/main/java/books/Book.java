package books;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Book extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public Book() throws Exception {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Book Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            try {
                String keyword = searchField.getText().trim();
                loadBookData(keyword);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to search: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Category", "Stock", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        loadBookData(null);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Book");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton.addActionListener(e -> {
            try {
                new CreateBook(this);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        footerPanel.add(addButton);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        bookTable.getColumnModel().getColumn(4).setCellRenderer(new ActionButtonRenderer());
        bookTable.getColumnModel().getColumn(4).setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        add(mainPanel, BorderLayout.CENTER);
    }

    public void loadBookData(String keyword) throws Exception {
        tableModel.setRowCount(0);

        String query = "SELECT b.id, b.title, c.category_name, b.books_stock " +
                "FROM books b INNER JOIN categories c ON b.category_id = c.id";

        if (keyword != null && !keyword.isEmpty()) {
            query += " WHERE b.title LIKE ? OR c.category_name LIKE ?";
        }

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (keyword != null && !keyword.isEmpty()) {
                stmt.setString(1, "%" + keyword + "%");
                stmt.setString(2, "%" + keyword + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("category_name"));
                row.add(rs.getInt("books_stock"));
                row.add("Details");
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showBookDetails(int id) throws Exception {
        new BookDetail(this, id);
    }

    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton detailsButton = new JButton("Details");

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(detailsButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    public void refreshTable() {
        try {
            loadBookData(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to refresh table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private final JButton detailsButton = new JButton("Details");
        private final Book bookPanel;
        private int selectedRowId;

        public ActionButtonEditor(JCheckBox checkBox, Book bookPanel) {
            this.bookPanel = bookPanel;

            detailsButton.addActionListener(e -> {
                try {
                    bookPanel.showBookDetails(selectedRowId);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                fireEditingStopped();
            });

            panel.add(detailsButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRowId = (int) table.getValueAt(row, 0);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
}
