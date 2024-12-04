package staff;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Staff extends JPanel {
    private JTable staffTable, workRecordTable;
    private DefaultTableModel staffTableModel, workRecordTableModel;
    private JComboBox<String> staffComboBox;
    private JButton checkInButton, checkOutButton, addStaffButton;
    private JLabel titleLabel;

    private DatabaseConnection dbConnection;
    private Connection conn;

    public Staff() throws Exception {
        setLayout(new BorderLayout(10, 10)); 
        setBackground(Color.WHITE);

        dbConnection = new DatabaseConnection();
        conn = dbConnection.getConnection();

        JPanel headerPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Staff List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        addStaffButton = new JButton("Add Staff");
        addStaffButton.addActionListener(e -> openCreateStaff());
        headerPanel.add(addStaffButton, BorderLayout.EAST);

        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));

        staffTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone Number"}, 0);
        staffTable = new JTable(staffTableModel);
        staffTable.setRowHeight(30); 
        loadStaffTableData();

        JScrollPane staffScrollPane = new JScrollPane(staffTable);
        tablePanel.add(headerPanel, BorderLayout.NORTH);
        tablePanel.add(staffScrollPane, BorderLayout.CENTER);

        JPanel staffControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        staffComboBox = new JComboBox<>();
        loadStaffData(); 

        checkInButton = new JButton("Check In");
        checkOutButton = new JButton("Check Out");

        checkInButton.addActionListener(e -> checkIn());
        checkOutButton.addActionListener(e -> checkOut());

        staffControlPanel.add(new JLabel("Select Staff:"));
        staffControlPanel.add(staffComboBox);
        staffControlPanel.add(checkInButton);
        staffControlPanel.add(checkOutButton);

        tablePanel.add(staffControlPanel, BorderLayout.SOUTH);

        JPanel workRecordPanel = new JPanel(new BorderLayout());
        workRecordTableModel = new DefaultTableModel(new String[]{"ID", "Staff ID", "Check In Time", "Check Out Time"}, 0);
        workRecordTable = new JTable(workRecordTableModel);

        JScrollPane workRecordScrollPane = new JScrollPane(workRecordTable);
        workRecordPanel.add(workRecordScrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.NORTH);
        add(workRecordPanel, BorderLayout.CENTER);

        loadWorkRecordTableData();
    }

    private void openCreateStaff() {
        JFrame createStaffFrame = new CreateStaff();
        createStaffFrame.setVisible(true);
    }

    private void loadStaffData() {
        String query = "SELECT id, name FROM staffs";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                staffComboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading staff data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStaffTableData() {
        String query = "SELECT id, name, phone_number FROM staffs";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            staffTableModel.setRowCount(0); 
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("phone_number"));

                staffTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading staff table data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkIn() {
        String selectedStaff = (String) staffComboBox.getSelectedItem();
        if (selectedStaff == null) {
            JOptionPane.showMessageDialog(this, "Please select a staff member first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int staffId = Integer.parseInt(selectedStaff.split(" - ")[0]);
        String query = "INSERT INTO work_records (staff_id, check_in_time) VALUES (?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, staffId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Staff has checked in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadWorkRecordTableData(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking in: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkOut() {
        String selectedStaff = (String) staffComboBox.getSelectedItem();
        if (selectedStaff == null) {
            JOptionPane.showMessageDialog(this, "Please select a staff member first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int staffId = Integer.parseInt(selectedStaff.split(" - ")[0]);
        String query = "UPDATE work_records SET check_out_time = NOW() WHERE staff_id = ? AND check_out_time IS NULL";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, staffId);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Staff has checked out.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadWorkRecordTableData(); // Refresh work record table
            } else {
                JOptionPane.showMessageDialog(this, "No check-in found for this staff.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking out: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadWorkRecordTableData() {
        String query = "SELECT id, staff_id, check_in_time, check_out_time FROM work_records";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            workRecordTableModel.setRowCount(0); 
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getInt("staff_id"));
                row.add(rs.getTimestamp("check_in_time"));
                row.add(rs.getTimestamp("check_out_time"));
                workRecordTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading work records: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
