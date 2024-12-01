package layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Header extends JPanel {
    public Header() {
        setBackground(new Color(47, 53, 66));
        setPreferredSize(new Dimension(0, 60));
        setLayout(new BorderLayout());

        JLabel label = new JLabel("LIBRARY UNTAR");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(20, 20, 20, 0));
        add(label);

        JButton profileButton = new JButton("Hello, Admin");
        profileButton.setFont(new Font("Arial", Font.PLAIN, 14));
        profileButton.setFocusPainted(false);
        profileButton.setBackground(new Color(47, 53, 66));
        profileButton.setForeground(Color.WHITE);
        profileButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        add(profileButton, BorderLayout.EAST);
    }
}
