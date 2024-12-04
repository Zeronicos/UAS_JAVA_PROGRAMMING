package transaction;

import database.DatabaseConnection;
import dashboard.Dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Return extends JPanel {
    private DefaultTableModel tableModel;
    private JTable returnTable;
    private JButton returnButton;
    private JTextField borrowingIdField;

    private Dashboard dashboard;

    public Return() throws Exception {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tableLabel = new JLabel("Return Records", JLabel.CENTER);
        tableLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tableLabel.setPreferredSize(new Dimension(0, 30));
        mainPanel.add(tableLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Borrowing ID", "Return Date", "Fine"}, 0);
        returnTable = new JTable(tableModel);

        JScrollPane tableScroll = new JScrollPane(returnTable);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        loadReturnData();

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        borrowingIdField = new JTextField(10);
        returnButton = new JButton("Return Book");

        returnButton.addActionListener(e -> {
            try {
                returnBook(Integer.parseInt(borrowingIdField.getText()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(new JLabel("Borrowing ID:"));
        inputPanel.add(borrowingIdField);
        inputPanel.add(returnButton);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadReturnData() throws Exception {
        DatabaseConnection dbConnection = new DatabaseConnection();
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM returns")) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getInt("borrowing_id"));
                row.add(rs.getDate("return_date"));
                row.add(rs.getBigDecimal("fine"));
                tableModel.addRow(row);
            }
        }
    }

    private void returnBook(int borrowingId) throws Exception {
        String returnDate = new java.sql.Date(System.currentTimeMillis()).toString();
        String sql = "UPDATE borrowings SET return_date = ?, status = 'returned' WHERE id = ?";
        String returnSql = "INSERT INTO returns (borrowing_id, return_date) VALUES (?, ?)";

        DatabaseConnection dbConnection = new DatabaseConnection();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement returnPstmt = conn.prepareStatement(returnSql)) {

            pstmt.setString(1, returnDate);
            pstmt.setInt(2, borrowingId);
            pstmt.executeUpdate();

            returnPstmt.setInt(1, borrowingId);
            returnPstmt.setString(2, returnDate);
            returnPstmt.executeUpdate();

            loadReturnData();

            if (dashboard != null) {
                dashboard.updateDashboardData();
            }
        }
    }

}
