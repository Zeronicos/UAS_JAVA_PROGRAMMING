package shelf;

import javax.swing.*;
import java.awt.*;

public class Shelf extends JPanel {
    public Shelf() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Selamat Datang Shelf", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.CENTER);
    }
}
