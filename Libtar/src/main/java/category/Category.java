package category;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.DefaultTableModel;
import javax.swing.AbstractCellEditor;


public class Category extends JPanel {
    private JTable categoryTable;
    private DefaultTableModel tableModel;

    public Category() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Category Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        tableModel = new DefaultTableModel(new String[]{"ID", "Category Name", "Stock", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(30);

        categoryTable.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        categoryTable.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);

        loadCategoryData();

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Category");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton.setBackground(new Color(102, 255, 102));
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(e -> new CreateCategory(this));
        footerPanel.add(addButton);

        add(footerPanel, BorderLayout.SOUTH);
    }

    public void loadCategoryData() {
        tableModel.setRowCount(0); // Clear data table

        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, category_name, stock FROM categories")) {

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("category_name"));
                    row.add(rs.getInt("stock"));
                    row.add("Edit/Delete"); // Placeholder untuk tombol aksi
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCategory(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseConnection dbConnection = new DatabaseConnection();
                try (Connection conn = dbConnection.getConnection();  // Gunakan koneksi dari dbConnection
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM categories WHERE id = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    loadCategoryData(); // Refresh table
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to delete category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ActionButtonRenderer class
    private static class ActionButtonRenderer extends JPanel implements TableCellRenderer {
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

    // ActionButtonEditor class
    private class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private final JButton editButton = new JButton("Edit");
        private final JButton deleteButton = new JButton("Delete");

        private Category categoryPanel;

        public ActionButtonEditor(JCheckBox checkBox, Category categoryPanel) {
            this.categoryPanel = categoryPanel;

            panel.add(editButton);
            panel.add(deleteButton);

            editButton.setBackground(new Color(0, 255, 4));
            editButton.setForeground(Color.WHITE);
            editButton.addActionListener(e -> {
                int row = categoryTable.getSelectedRow();
                int id = (int) categoryTable.getValueAt(row, 0);
                new EditCategory(id, categoryPanel);
                stopCellEditing();
            });

            deleteButton.setBackground(new Color(255, 0, 0));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.addActionListener(e -> {
                int row = categoryTable.getSelectedRow();
                int id = (int) categoryTable.getValueAt(row, 0);
                deleteCategory(id);
                stopCellEditing();
            });
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

    public void refreshTable() {
        loadCategoryData();
    }
}
