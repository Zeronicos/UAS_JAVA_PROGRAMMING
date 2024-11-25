package member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Member extends JFrame {
    private JTextField txtName, txtBirthPlace, txtAddress;
    private JComboBox<String> cmbGender, cmbDay, cmbMonth, cmbYear;
    private JButton btnSubmit;
    private ArrayList<MemberData> memberList;

    public Member() {
        memberList = new ArrayList<>();

        setTitle("Form Input Member");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // pake GridBagLayout biar layout fleksibel
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spasi antar elemen
        gbc.anchor = GridBagConstraints.WEST;

        // Font label dan field
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // Nama
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblName = new JLabel("Nama");
        lblName.setFont(labelFont);
        add(lblName, gbc);

        gbc.gridx = 1;
        txtName = new JTextField(20);
        txtName.setFont(fieldFont);
        add(txtName, gbc);

        // Kota Tempat Lahir
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblBirthPlace = new JLabel("Kota Tempat Lahir");
        lblBirthPlace.setFont(labelFont);
        add(lblBirthPlace, gbc);

        gbc.gridx = 1;
        txtBirthPlace = new JTextField(20);
        txtBirthPlace.setFont(fieldFont);
        add(txtBirthPlace, gbc);

        // Tanggal Lahir
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblBirthDate = new JLabel("Tanggal Lahir");
        lblBirthDate.setFont(labelFont);
        add(lblBirthDate, gbc);

        // Panel buat ComboBox hari, bulan, dan tahun
        gbc.gridx = 1;
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Hari
        cmbDay = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            cmbDay.addItem(String.format("%02d", i)); // Format dua digit
        }
        datePanel.add(cmbDay);

        // Bulan
        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        cmbMonth = new JComboBox<>(months);
        datePanel.add(cmbMonth);

        // Tahun
        cmbYear = new JComboBox<>();
        for (int i = 1900; i <= 2024; i++) {
            cmbYear.addItem(String.valueOf(i));
        }
        datePanel.add(cmbYear);

        add(datePanel, gbc);

        // Jenis Kelamin
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblGender = new JLabel("Jenis Kelamin");
        lblGender.setFont(labelFont);
        add(lblGender, gbc);

        gbc.gridx = 1;
        cmbGender = new JComboBox<>(new String[]{"Pria", "Wanita"});
        cmbGender.setFont(fieldFont);
        add(cmbGender, gbc);

        // Alamat
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblAddress = new JLabel("Alamat");
        lblAddress.setFont(labelFont);
        add(lblAddress, gbc);

        gbc.gridx = 1;
        txtAddress = new JTextField(20);
        txtAddress.setFont(fieldFont);
        add(txtAddress, gbc);

        // Tombol Submit
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnSubmit, gbc);

        // Action buat tombol submit
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    String name = txtName.getText();
                    String birthPlace = txtBirthPlace.getText();
                    String day = (String) cmbDay.getSelectedItem();
                    String month = (String) cmbMonth.getSelectedItem();
                    String year = (String) cmbYear.getSelectedItem();
                    String gender = (String) cmbGender.getSelectedItem();
                    String address = txtAddress.getText();

                    String birthDate = day + " " + month + " " + year;

                    // nyimpen data buat list
                    MemberData member = new MemberData(name, birthPlace, birthDate, gender, address);
                    memberList.add(member);

                    // nampilin data ke page baru
                    new DataPage(memberList).setVisible(true);
                    dispose();
                }
            }
        });
    }

    // buat validasi input
    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtBirthPlace.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kota Tempat Lahir harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (cmbDay.getSelectedIndex() == -1 || cmbMonth.getSelectedIndex() == -1 || cmbYear.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Tanggal Lahir harus lengkap!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (cmbGender.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Jenis Kelamin harus dipilih!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtAddress.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alamat harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        new Member().setVisible(true);
    }

    // data member
    class MemberData {
        private String name;
        private String birthPlace;
        private String birthDate;
        private String gender;
        private String address;

        public MemberData(String name, String birthPlace, String birthDate, String gender, String address) {
            this.name = name;
            this.birthPlace = birthPlace;
            this.birthDate = birthDate;
            this.gender = gender;
            this.address = address;
        }

        public String getName() { return name; }
        public String getBirthPlace() { return birthPlace; }
        public String getBirthDate() { return birthDate; }
        public String getGender() { return gender; }
        public String getAddress() { return address; }
    }

    // Halaman baru isinya data member
    class DataPage extends JFrame {
        public DataPage(ArrayList<MemberData> memberList) {
            setTitle("Data Member");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;

            Font labelFont = new Font("Arial", Font.PLAIN, 16);

            int row = 0;
            for (MemberData member : memberList) {
                // Nama
                gbc.gridx = 0;
                gbc.gridy = row++;
                JLabel lblName = new JLabel("Nama:");
                lblName.setFont(labelFont);
                add(lblName, gbc);

                gbc.gridx = 1;
                JLabel lblNameValue = new JLabel(member.getName());
                lblNameValue.setFont(labelFont);
                add(lblNameValue, gbc);

                row++; // Baris kosong

                // Kota Tempat Lahir
                gbc.gridx = 0;
                gbc.gridy = row++;
                JLabel lblBirthPlace = new JLabel("Kota Tempat Lahir:");
                lblBirthPlace.setFont(labelFont);
                add(lblBirthPlace, gbc);

                gbc.gridx = 1;
                JLabel lblBirthPlaceValue = new JLabel(member.getBirthPlace());
                lblBirthPlaceValue.setFont(labelFont);
                add(lblBirthPlaceValue, gbc);

                row++; // Baris kosong

                // Tanggal Lahir
                gbc.gridx = 0;
                gbc.gridy = row++;
                JLabel lblBirthDate = new JLabel("Tanggal Lahir:");
                lblBirthDate.setFont(labelFont);
                add(lblBirthDate, gbc);

                gbc.gridx = 1;
                JLabel lblBirthDateValue = new JLabel(member.getBirthDate());
                lblBirthDateValue.setFont(labelFont);
                add(lblBirthDateValue, gbc);

                row++; // Baris kosong

                // Jenis Kelamin
                gbc.gridx = 0;
                gbc.gridy = row++;
                JLabel lblGender = new JLabel("Jenis Kelamin:");
                lblGender.setFont(labelFont);
                add(lblGender, gbc);

                gbc.gridx = 1;
                JLabel lblGenderValue = new JLabel(member.getGender());
                lblGenderValue.setFont(labelFont);
                add(lblGenderValue, gbc);

                row++; // Baris kosong

                // Alamat
                gbc.gridx = 0;
                gbc.gridy = row++;
                JLabel lblAddress = new JLabel("Alamat:");
                lblAddress.setFont(labelFont);
                add(lblAddress, gbc);

                gbc.gridx = 1;
                JLabel lblAddressValue = new JLabel(member.getAddress());
                lblAddressValue.setFont(labelFont);
                add(lblAddressValue, gbc);

                row += 2;
            }
        }
    }
}
