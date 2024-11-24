package dashboard;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JPanel {
    public Dashboard() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Selamat Datang Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.CENTER);
    }
}
