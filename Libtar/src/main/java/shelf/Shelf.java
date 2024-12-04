package shelf;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Shelf extends JPanel {
    private JTable shelfTable;
    private DefaultTableModel tableModel;

    public Shelf() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Shelf Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Shelf Name", "Category", "Stock", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow only the last column (Actions) to be editable
                return column == 4;
            }
        };
        shelfTable = new JTable(tableModel);
        shelfTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(shelfTable);
        add(scrollPane, BorderLayout.CENTER);

        loadShelfData();

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Shelf");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton.setBackground(new Color(102, 255, 102));
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(e -> new CreateShelf(this));
        footerPanel.add(addButton);

        add(footerPanel, BorderLayout.SOUTH);

        shelfTable.getColumnModel().getColumn(4).setCellRenderer(new ActionButtonRenderer());
        shelfTable.getColumnModel().getColumn(4).setCellEditor(new ActionButtonEditor(new JCheckBox(), this));
    }

    public void loadShelfData() {
        tableModel.setRowCount(0);

        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT s.id, s.shelf_name, c.category_name, s.stock " +
                         "FROM shelves s INNER JOIN categories c ON s.category_id = c.id")) {

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("shelf_name"));
                    row.add(rs.getString("category_name"));
                    row.add(rs.getInt("stock"));
                    row.add("Edit/Delete");
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshTable() {
        loadShelfData();
    }

    public void deleteShelf(int id) throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM shelves WHERE id = ?")) {

                stmt.setInt(1, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Shelf deleted successfully!");
                refreshTable();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete shelf: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void editShelf(int id) throws Exception {
        new EditShelf(this, id);
    }

    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton editButton = new JButton("Edit");
        private final JButton deleteButton = new JButton("Delete");

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
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
        private final Shelf shelfPanel;
        private int selectedRowId;

        public ActionButtonEditor(JCheckBox checkBox, Shelf shelfPanel) {
            this.shelfPanel = shelfPanel;

            editButton.addActionListener(e -> {
                try {
                    shelfPanel.editShelf(selectedRowId);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                fireEditingStopped();
            });

            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this shelf?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        shelfPanel.deleteShelf(selectedRowId);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                fireEditingStopped();
            });

            panel.add(editButton);
            panel.add(deleteButton);
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
