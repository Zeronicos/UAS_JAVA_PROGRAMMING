import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class Member extends JFrame {
    private JTextField txtName, txtStudentID, txtMajor, txtPhoneNumber, txtEmail;
    private JButton btnSubmit;
    private int editIndex = -1; // To indicate if the user is editing data
    private Connection connection;

    public Member() {
        // konek ke database
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/libtar", "kevin", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Form Input Member");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // bikin fields buat form
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
        JLabel lblStudentID = new JLabel("Student ID");
        lblStudentID.setFont(labelFont);
        add(lblStudentID, gbc);

        gbc.gridx = 1;
        txtStudentID = new JTextField(20);
        txtStudentID.setFont(fieldFont);
        add(txtStudentID, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblMajor = new JLabel("Major");
        lblMajor.setFont(labelFont);
        add(lblMajor, gbc);

        gbc.gridx = 1;
        txtMajor = new JTextField(20);
        txtMajor.setFont(fieldFont);
        add(txtMajor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblPhoneNumber = new JLabel("Phone Number");
        lblPhoneNumber.setFont(labelFont);
        add(lblPhoneNumber, gbc);

        gbc.gridx = 1;
        txtPhoneNumber = new JTextField(20);
        txtPhoneNumber.setFont(fieldFont);
        add(txtPhoneNumber, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(labelFont);
        add(lblEmail, gbc);

        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        txtEmail.setFont(fieldFont);
        add(txtEmail, gbc);

        // bikin button buat submit
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnSubmit, gbc);

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    String name = txtName.getText();
                    String studentID = txtStudentID.getText();
                    String major = txtMajor.getText();
                    String phoneNumber = txtPhoneNumber.getText();
                    String email = txtEmail.getText();

                    // update database abis di edit
                    if (editIndex == -1) {
                        addMemberToDatabase(name, studentID, major, phoneNumber, email);
                    } else {
                        // Edit member yang udah di bikin
                        updateMemberInDatabase(name, studentID, major, phoneNumber, email, editIndex);
                        editIndex = -1; // Reset after editing
                    }

                    // Reload tablenya yang abis di updte
                    dispose();
                    new MainPage(Member.this).setVisible(true);
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
        if (!txtStudentID.getText().matches("\\d{9}")) {
            JOptionPane.showMessageDialog(this, "Student ID must be exactly 9 digits!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtMajor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Major must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void addMemberToDatabase(String name, String studentID, String major, String phoneNumber, String email) {
        try {
            String query = "INSERT INTO members (name, student_id, major, phone_number, email, is_manual) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, studentID);
                stmt.setString(3, major);
                stmt.setString(4, phoneNumber);
                stmt.setString(5, email);
                stmt.setBoolean(6, true); // Marking as manually added
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Member added successfully.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding member: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMemberInDatabase(String name, String studentID, String major, String phoneNumber, String email, int id) {
        try {
            String query = "UPDATE members SET name = ?, student_id = ?, major = ?, phone_number = ?, email = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, studentID);
                stmt.setString(3, major);
                stmt.setString(4, phoneNumber);
                stmt.setString(5, email);
                stmt.setInt(6, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Member updated successfully.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating member: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Member().setVisible(true);
    }

    // MainPage class buat display member data dari database
    class MainPage extends JFrame {
        private Member memberFrame;

        public MainPage(Member memberFrame) {
            this.memberFrame = memberFrame;
            setTitle("Member List");
            setSize(700, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            String[] columnNames = {"No", "Name", "Student ID", "Detail", "Edit"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            JTable table = new JTable(model);
            table.setRowHeight(30);

            try {
                String query = "SELECT * FROM members";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("student_id"),
                            rs.getBoolean("is_manual") ? "Detail" : "",
                            rs.getBoolean("is_manual") ? "Edit" : ""
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error retrieving members: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            table.getColumn("Detail").setCellRenderer(new ButtonRenderer());
            table.getColumn("Detail").setCellEditor(new ButtonEditor(new JCheckBox(), table, false));

            table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
            table.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), table, true));

            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private JTable table;
        private boolean isEdit;

        public ButtonEditor(JCheckBox checkBox, JTable table, boolean isEdit) {
            super(checkBox);
            this.table = table;
            this.isEdit = isEdit;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
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
                if (isEdit) {
                    int memberID = (int) table.getValueAt(selectedRow, 0);
                    Member memberFrame = new Member();
                    memberFrame.editIndex = memberID;
                    memberFrame.setVisible(true);
                } else {
                    int memberID = (int) table.getValueAt(selectedRow, 0);
                    new DetailPage(memberID).setVisible(true);
                }
            }
            isPushed = false;
            return button.getText();
        }
    }

    class DetailPage extends JFrame {
        public DetailPage(int memberID) {
            setTitle("Member Detail");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new GridLayout(5, 2, 10, 10));
            Font labelFont = new Font("Arial", Font.PLAIN, 16);

            try {
                String query = "SELECT * FROM members WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, memberID);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    add(new JLabel("Name:"));
                    add(new JLabel(rs.getString("name")));

                    add(new JLabel("Student ID:"));
                    add(new JLabel(rs.getString("student_id")));

                    add(new JLabel("Major:"));
                    add(new JLabel(rs.getString("major")));

                    add(new JLabel("Phone Number:"));
                    add(new JLabel(rs.getString("phone_number")));

                    add(new JLabel("Email:"));
                    add(new JLabel(rs.getString("email")));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error retrieving member details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}