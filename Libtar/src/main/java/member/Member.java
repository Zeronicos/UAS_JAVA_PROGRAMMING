package member;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Member extends JPanel {
    private DefaultTableModel tableModel;
    private JTable memberTable;
    private JTextField searchField;
    private JButton searchButton;

    public Member() throws Exception {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

      
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

       
        JLabel headerLabel = new JLabel("Member Management", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        headerPanel.add(headerLabel, BorderLayout.WEST);

        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText();
            try {
                searchMembers(searchText);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH); // Add header panel to the top

     
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Student ID", "Major", "Phone", "Email", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Action column is editable
            }
        };

      
        memberTable = new JTable(tableModel);
        memberTable.setRowHeight(30);
        memberTable.getColumnModel().getColumn(6).setCellRenderer((TableCellRenderer) new ButtonRenderer());
        memberTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane tableScrollPane = new JScrollPane(memberTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the table

        add(tableScrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        JButton addButton = new JButton("Add Member");
        addButton.addActionListener(e -> {
            try {
                new CreateMember((Frame) SwingUtilities.getWindowAncestor(this), this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        footerPanel.add(addButton);
        add(footerPanel, BorderLayout.SOUTH);

        loadMemberData();
    }

    private void loadMemberData() throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, name, student_id, major, phone_number, email FROM members")) {

                tableModel.setRowCount(0); 
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("student_id"));
                    row.add(rs.getString("major"));
                    row.add(rs.getString("phone_number"));
                    row.add(rs.getString("email"));
                    row.add("Detail");
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load members: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchMembers(String searchText) throws Exception {
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT id, name, student_id, major, phone_number, email FROM members " +
                                 "WHERE name LIKE ? OR student_id LIKE ? OR major LIKE ? OR phone_number LIKE ? OR email LIKE ?")) {

                String searchPattern = "%" + searchText + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
                pstmt.setString(4, searchPattern);
                pstmt.setString(5, searchPattern);

                try (ResultSet rs = pstmt.executeQuery()) {
                    tableModel.setRowCount(0);
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getInt("id"));
                        row.add(rs.getString("name"));
                        row.add(rs.getString("student_id"));
                        row.add(rs.getString("major"));
                        row.add(rs.getString("phone_number"));
                        row.add(rs.getString("email"));
                        row.add("Detail");
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to search members: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTable() {
        try {
            loadMemberData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Detail"); 
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private String label;
        private final Member memberPanel;

        public ButtonEditor(JCheckBox checkBox, Member memberPanel) {
            super(checkBox);
            this.memberPanel = memberPanel;
            setClickCountToStart(1);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Detail" : value.toString(); 
            JButton button = new JButton(label);
            button.addActionListener(e -> {
                int memberId = (int) table.getValueAt(row, 0); 
                try {
                    new MemberDetail((Frame) SwingUtilities.getWindowAncestor(this.memberPanel), memberId, this.memberPanel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error opening member detail: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }
}
