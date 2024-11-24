package layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Sidebar extends JPanel {
    private static final Color SIDEBAR_BG = new Color(47, 53, 66);
    private static final Color HOVER_COLOR = new Color(57, 63, 76);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final int BUTTON_HEIGHT = 60;
    private static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 16);

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public Sidebar(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SIDEBAR_BG);
        setPreferredSize(new Dimension(220, 0));
        setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel label = new JLabel("LIBRARY UNTAR");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(10, 0, 20, 0));
        add(label);

        addMenuButton("dashboard", "dashboard");
        addMenuButton("Staff", "staff");
        addMenuButton("shelf", "shelf");
        addMenuButton("Member", "member");
        addMenuButton("category", "category");
        addMenuButton("Book", "book");

        add(Box.createVerticalGlue());
        addMenuButton("Logout", "logout");
    }

    private void addMenuButton(String text, String targetCard) {
        JButton button = new JButton(text);
        styleButton(button);

        button.addActionListener((ActionEvent e) -> {
            if (targetCard.equals("logout")) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            } else {
                cardLayout.show(mainPanel, targetCard);
            }
        });

        add(button);
        add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setForeground(TEXT_COLOR);
        button.setBackground(SIDEBAR_BG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(0, 20, 0, 0));
    }
}
