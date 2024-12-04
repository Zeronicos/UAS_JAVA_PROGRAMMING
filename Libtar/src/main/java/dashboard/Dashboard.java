package dashboard;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Dashboard extends JPanel {
    private DefaultTableModel tableModel;
    private JTable borrowerTable;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;

    private JLabel borrowedCountLabel, returnedCountLabel;
    private JLabel borrowedCountValue, returnedCountValue;

    private DatabaseConnection dbConnection;
    private Connection conn;
    private Timer timer;

    public Dashboard() throws Exception {
        setLayout(new BorderLayout());

        dbConnection = new DatabaseConnection();
        conn = dbConnection.getConnection();

        JPanel cardPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        borrowedCountLabel = new JLabel("Total Borrowed");
        returnedCountLabel = new JLabel("Total Returned");

        borrowedCountValue = new JLabel("0");
        returnedCountValue = new JLabel("0");

        cardPanel.add(createCard(borrowedCountLabel, borrowedCountValue, new ImageIcon("icons/borrow.png")));
        cardPanel.add(createCard(returnedCountLabel, returnedCountValue, new ImageIcon("icons/return.png")));

        add(cardPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        filterComboBox = new JComboBox<>(new String[]{"All", "Borrowed", "Returned"});
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> loadBorrowerData());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(filterComboBox);
        searchPanel.add(searchButton);

        middlePanel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Member Name", "Book Title", "Borrow Date", "Return Date", "Status"}, 0);
        borrowerTable = new JTable(tableModel);
        middlePanel.add(new JScrollPane(borrowerTable), BorderLayout.CENTER);

        add(middlePanel, BorderLayout.CENTER);

        try {
            loadDashboardData();
            loadBorrowerData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading dashboard data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDashboardData();
            }
        }, 0, 5000);
    }

    private JPanel createCard(JLabel titleLabel, JLabel valueLabel, ImageIcon icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(230, 230, 250));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(230, 230, 250));
        titlePanel.add(titleLabel);

        JLabel iconLabel = new JLabel(icon);
        titlePanel.add(iconLabel);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadDashboardData() throws SQLException {
        String borrowedQuery = "SELECT COUNT(*) FROM borrowings WHERE status = 'borrowed'";
        String returnedQuery = "SELECT COUNT(*) FROM borrowings WHERE status = 'returned'";

        try {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(borrowedQuery)) {
                if (rs.next()) {
                    borrowedCountValue.setText(String.valueOf(rs.getInt(1)));
                }
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(returnedQuery)) {
                if (rs.next()) {
                    returnedCountValue.setText(String.valueOf(rs.getInt(1)));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error loading dashboard data: " + e.getMessage(), e);
        }
    }

    private void loadBorrowerData() {
        String filter = filterComboBox.getSelectedItem().toString();
        String search = searchField.getText().trim();
        StringBuilder query = new StringBuilder(
                "SELECT b.id, m.name, bo.title, b.borrow_date, b.return_date, b.status " +
                        "FROM borrowings b " +
                        "JOIN books bo ON b.book_id = bo.id " +
                        "JOIN members m ON b.member_id = m.id"
        );

        boolean hasCondition = false;
        if (!filter.equals("All")) {
            query.append(" WHERE b.status = '").append(filter.toLowerCase()).append("'");
            hasCondition = true;
        }

        if (!search.isEmpty()) {
            if (hasCondition) {
                query.append(" AND ");
            } else {
                query.append(" WHERE ");
            }
            query.append("(m.name LIKE '%").append(search).append("%' OR bo.title LIKE '%").append(search).append("%')");
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query.toString())) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("title"));
                row.add(rs.getDate("borrow_date"));
                row.add(rs.getDate("return_date"));
                row.add(rs.getString("status"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading borrower data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateDashboardData() {
        try {
            loadDashboardData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating dashboard data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void borrowBook(int memberId, int bookId) throws SQLException {
        String sql = "INSERT INTO borrowings (member_id, book_id, borrow_date, status) VALUES (?, ?, NOW(), 'borrowed')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
            loadBorrowerData();
        }
    }

    public void returnBook(int borrowingId) throws SQLException {
        String returnDate = new java.sql.Date(System.currentTimeMillis()).toString();
        String sql = "UPDATE borrowings SET return_date = ?, status = 'returned' WHERE id = ?";
        String returnSql = "INSERT INTO returns (borrowing_id, return_date) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement returnPstmt = conn.prepareStatement(returnSql)) {

            pstmt.setString(1, returnDate);
            pstmt.setInt(2, borrowingId);
            pstmt.executeUpdate();

            returnPstmt.setInt(1, borrowingId);
            returnPstmt.setString(2, returnDate);
            returnPstmt.executeUpdate();

            loadBorrowerData();
        }
    }
}
