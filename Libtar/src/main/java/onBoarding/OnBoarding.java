package onBoarding;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OnBoarding{
    private JFrame frame;

    public OnBoarding() {

        frame = new JFrame("Library Untar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 360);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/assets/logo_untar.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(150, 180, Image.SCALE_SMOOTH);
        JLabel labelImage = new JLabel(new ImageIcon(scaledImage));
        labelImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelText = new JLabel("Welcome to Library Untar", SwingConstants.CENTER);
        labelText.setFont(new Font("Arial", Font.BOLD, 18));
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelText.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton startButton = new JButton("Start");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        mainPanel.add(labelImage);
        mainPanel.add(labelText);
        mainPanel.add(startButton);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
