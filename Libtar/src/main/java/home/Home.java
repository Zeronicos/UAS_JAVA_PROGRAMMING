package home;

import books.Book;
import category.Category;
import layout.Header;
import layout.Sidebar;
import dashboard.Dashboard;
import member.Member;
import shelf.Shelf;
import staff.Staff;

import javax.swing.*;
import java.awt.*;

public class Home extends JFrame {

    public Home() throws Exception {
        setTitle("Library UNTAR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        mainPanel.add(new Dashboard(), "dashboard");
        mainPanel.add(new Staff(), "staff");
        mainPanel.add(new Shelf(), "shelf");
        mainPanel.add(new Member(), "member");
        mainPanel.add(new Category(), "category");
        mainPanel.add(new Book(), "book");

        Sidebar sidebar = new Sidebar(cardLayout, mainPanel);
        add(sidebar, BorderLayout.WEST);

        Header header = new Header();
        add(header, BorderLayout.NORTH);

        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new Home();
    }
}
