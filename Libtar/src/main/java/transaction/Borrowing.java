package transaction;

import database.DatabaseConnection;
import dashboard.Dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Borrowing extends JPanel {
    private DefaultTableModel tableModel;
    private JTable borrowingTable;
    private JButton borrowButton;
    private JTextField memberIdField;
    private JTextField bookIdField;

    private Dashboard dashboard;

    public Borrowing() throws Exception {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adding padding

        JLabel tableLabel = new JLabel("Borrowing Records", JLabel.CENTER);
        tableLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tableLabel.setPreferredSize(new Dimension(0, 30)); // Height of the label
        mainPanel.add(tableLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Member ID", "Book ID", "Borrow Date", "Return Date", "Status"}, 0);
        borrowingTable = new JTable(tableModel);

        JScrollPane tableScroll = new JScrollPane(borrowingTable);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        loadBorrowingData();

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        memberIdField = new JTextField(10);
        bookIdField = new JTextField(10);
        borrowButton = new JButton("Borrow Book");

        borrowButton.addActionListener(e -> {
            try {
                borrowBook(Integer.parseInt(memberIdField.getText()), Integer.parseInt(bookIdField.getText()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(new JLabel("Member ID:"));
        inputPanel.add(memberIdField);
        inputPanel.add(new JLabel("Book ID:"));
        inputPanel.add(bookIdField);
        inputPanel.add(borrowButton);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadBorrowingData() throws Exception {
        DatabaseConnection dbConnection = new DatabaseConnection();
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM borrowings")) {
            tableModel.setRowCount(0); // Clear table
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getInt("member_id"));
                row.add(rs.getInt("book_id"));
                row.add(rs.getDate("borrow_date"));
                row.add(rs.getDate("return_date"));
                row.add(rs.getString("status"));
                tableModel.addRow(row);
            }
        }
    }

    private void borrowBook(int memberId, int bookId) throws Exception {
        DatabaseConnection dbConnection = new DatabaseConnection();
        String sql = "INSERT INTO borrowings (member_id, book_id, borrow_date, status) VALUES (?, ?, NOW(), 'borrowed')";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
            loadBorrowingData();

            if (dashboard != null) {
                dashboard.updateDashboardData();
            }
        }
    }

}
