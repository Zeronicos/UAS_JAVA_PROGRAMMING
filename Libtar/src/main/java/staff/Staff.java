package staff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class Staff extends JFrame {
    private JTextField txtName, txtPhoneNumber, txtEmail;
    private JButton btnSubmit;
    private int editIndex = -1; // To indicate if the user is editing data
    private Connection connection;

    public Staff() {
        //konek ke database
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/libtar", "kevin", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Form Input Staff");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);


        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblName = new JLabel("Name");
        lblName.setFont(labelFont);
        add(lblName, gbc);

        gbc.gridx = 1;
        txtName = new JTextField(20);
        txtName.setFont(fieldFont);
        add(txtName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblPhoneNumber = new JLabel("Phone Number");
        lblPhoneNumber.setFont(labelFont);
        add(lblPhoneNumber, gbc);

        gbc.gridx = 1;
        txtPhoneNumber = new JTextField(20);
        txtPhoneNumber.setFont(fieldFont);
        add(txtPhoneNumber, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(labelFont);
        add(lblEmail, gbc);

        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        txtEmail.setFont(fieldFont);
        add(txtEmail, gbc);

        // button buat submit
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnSubmit, gbc);

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    String name = txtName.getText();
                    String phoneNumber = txtPhoneNumber.getText();
                    String email = txtEmail.getText();

                    // update databse abis di edit
                    if (editIndex == -1) {
                        // Add new staff
                        addStaffToDatabase(name, phoneNumber, email);
                    } else {
                        // edit staff
                        updateStaffInDatabase(name, phoneNumber, email, editIndex);
                        editIndex = -1; // Reset after editing
                    }


                    dispose();
                    new MainPage(Staff.this, false).setVisible(true); // Open MainPage
                }
            }
        });
    }

    private boolean validateInputs() {
        // Validasi logika input
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!txtPhoneNumber.getText().matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Phone Number must contain only digits!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void addStaffToDatabase(String name, String phoneNumber, String email) {
        try {
            String query = "INSERT INTO staff (name, phone_number, email) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, phoneNumber);
                stmt.setString(3, email);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff added successfully.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding staff: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStaffInDatabase(String name, String phoneNumber, String email, int id) {
        try {
            String query = "UPDATE staff SET name = ?, phone_number = ?, email = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, phoneNumber);
                stmt.setString(3, email);
                stmt.setInt(4, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff updated successfully.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating staff: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStaffFromDatabase(int id) {
        try {
            String query = "DELETE FROM staff WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Staff deleted successfully.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting staff: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Staff().setVisible(true);
    }

    // dispay data staff dari database
    class MainPage extends JFrame {
        private Staff staffFrame;

        public MainPage(Staff staffFrame, boolean isStaff) {
            this.staffFrame = staffFrame;
            setTitle("Staff List");
            setSize(700, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            String[] columnNames = {"No", "Name", "Phone Number", "Email", "Action"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            JTable table = new JTable(model);
            table.setRowHeight(30);

            try {
                String query = "SELECT * FROM staff";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("phone_number"),
                            rs.getString("email"),
                            "Delete"
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error retrieving staff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            table.getColumn("Action").setCellRenderer(new ButtonRenderer());
            table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), table, true));

            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }


        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private JTable table;
        private boolean isDelete;

        public ButtonEditor(JCheckBox checkBox, JTable table, boolean isDelete) {
            super(checkBox);
            this.table = table;
            this.isDelete = isDelete;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }


        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText((value == null) ? "" : value.toString());
            isPushed = true;
            return button;
        }


        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.getSelectedRow();
                int staffID = (int) table.getValueAt(selectedRow, 0);
                if (isDelete) {
                    deleteStaffFromDatabase(staffID);
                }
            }
            isPushed = false;
            return button.getText();
        }
    }
}
