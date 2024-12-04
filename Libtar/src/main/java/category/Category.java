package category;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Category extends JPanel {
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public Category() throws Exception {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Category Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            try {
                String keyword = searchField.getText().trim();
                loadCategoryData(keyword);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to search: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Category Name", "Stock", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        loadCategoryData(null);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Category");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton.addActionListener(e -> new CreateCategory(this));
        footerPanel.add(addButton);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        categoryTable.getColumnModel().getColumn(3).setCellRenderer(new ActionButtonRenderer());
        categoryTable.getColumnModel().getColumn(3).setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        add(mainPanel, BorderLayout.CENTER);
    }

    public void loadCategoryData(String keyword) throws Exception {
        tableModel.setRowCount(0);

        String query = "SELECT id, category_name, stock FROM categories";

        if (keyword != null && !keyword.isEmpty()) {
            query += " WHERE category_name LIKE ?";
        }

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (keyword != null && !keyword.isEmpty()) {
                stmt.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("category_name"));
                row.add(rs.getInt("stock"));
                row.add("Edit/Delete");
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton editButton = new JButton("Edit");
        private final JButton deleteButton = new JButton("Delete");

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton.setBackground(new Color(102, 178, 255));
            editButton.setForeground(Color.WHITE);
            deleteButton.setBackground(new Color(255, 102, 102));
            deleteButton.setForeground(Color.WHITE);
            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private final JButton editButton = new JButton("Edit");
        private final JButton deleteButton = new JButton("Delete");

        private Category categoryPanel;

        public ActionButtonEditor(JCheckBox checkBox, Category categoryPanel) {
            this.categoryPanel = categoryPanel;

            editButton.setBackground(new Color(102, 178, 255));
            editButton.setForeground(Color.WHITE);
            editButton.addActionListener(e -> {
                int row = categoryTable.getSelectedRow();
                int id = (int) categoryTable.getValueAt(row, 0);
                new EditCategory(id, categoryPanel);
                stopCellEditing();
            });

            deleteButton.setBackground(new Color(255, 102, 102));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.addActionListener(e -> {
                int row = categoryTable.getSelectedRow();
                int id = (int) categoryTable.getValueAt(row, 0);
                deleteCategory(id);
                stopCellEditing();
            });

            panel.add(editButton);
            panel.add(deleteButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private void deleteCategory(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseConnection dbConnection = new DatabaseConnection();
                try (Connection conn = dbConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM categories WHERE id = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    loadCategoryData(null);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
