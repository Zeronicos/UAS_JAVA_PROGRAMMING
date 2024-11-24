package layout;

import javax.swing.*;
import java.awt.*;

public class Header extends JPanel {
    public Header() {
        // Set header properties
        setBackground(new Color(47, 53, 66));
        setPreferredSize(new Dimension(0, 40));
        setLayout(new BorderLayout());

        JButton profileButton = new JButton("Hello, Admin");
        profileButton.setFont(new Font("Arial", Font.PLAIN, 14));
        profileButton.setFocusPainted(false);
        profileButton.setBackground(new Color(47, 53, 66));
        profileButton.setForeground(Color.WHITE);
        profileButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        add(profileButton, BorderLayout.EAST);
    }
}
