package authentication;

import home.Home;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login Form");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel titleLabel = new JLabel("WELCOME", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(100, 20, 200, 30);
        add(titleLabel);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(50, 90, 100, 20);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 90, 180, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(50, 130, 100, 20);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 130, 180, 25);
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 170, 80, 25);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateLogin();
            }
        });
        add(loginButton);

        getContentPane().setBackground(Color.WHITE);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void validateLogin() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        User user = new User();
        boolean isValid = user.validateUser(username, password);

        if (isValid) {
            JOptionPane.showMessageDialog(this, "Login successfully! Welcome " + username + ".");
            this.dispose();
            new Home();
        } else {
            JOptionPane.showMessageDialog(this, "Username or password wrong", "Login failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}