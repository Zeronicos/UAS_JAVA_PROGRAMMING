package books;

import javax.swing.*;
import java.awt.*;

public class Book extends JPanel {
    public Book() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Selamat Datang Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.CENTER);
    }
}
