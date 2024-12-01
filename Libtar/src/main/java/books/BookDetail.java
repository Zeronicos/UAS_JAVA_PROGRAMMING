package books;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class BookDetail extends JFrame {

    private JLabel lblImage;
    private JLabel lblTitle, lblAuthor, lblCategory;
    private JTextArea txtDescription;
    private JButton btnAboutBook;

    public BookDetail() {
        setTitle("Library App - Book Details");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        JPanel panelImage = new JPanel();
        panelImage.setLayout(new BorderLayout());
        panelImage.setPreferredSize(new Dimension(500, 300));
        panelImage.add(lblImage, BorderLayout.CENTER);
        add(panelImage, BorderLayout.NORTH);

        JPanel panelTitle = new JPanel();
        panelTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelTitle.setBorder(new TitledBorder("Title"));
        lblTitle = new JLabel("Nowhere to Go");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panelTitle.add(lblTitle);

        JPanel panelAuthor = new JPanel();
        panelAuthor.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelAuthor.setBorder(new TitledBorder("Author"));
        JLabel lblAuthor = new JLabel("Antony Gasing");
        lblAuthor.setFont(new Font("Arial", Font.PLAIN, 16));
        panelAuthor.add(lblAuthor);

        JPanel panelCategory = new JPanel();
        panelCategory.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelCategory.setBorder(new TitledBorder("category"));
        JLabel lblCategory = new JLabel("Drama, Adventure, Psychology");
        lblCategory.setFont(new Font("Arial", Font.PLAIN, 16));
        panelCategory.add(lblCategory);

        JPanel panelAuthorCategory = new JPanel();
        panelAuthorCategory.setLayout(new BoxLayout(panelAuthorCategory, BoxLayout.Y_AXIS));
        panelAuthorCategory.add(panelAuthor);
        panelAuthorCategory.add(panelCategory);

        JPanel panelDescription = new JPanel();
        panelDescription.setLayout(new FlowLayout());
        panelDescription.setBorder(new TitledBorder("Description"));
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setText("This book dives into the lives of individuals facing the hardships of life, " +
                "exploring themes of drama, adventure, and psychology. It's a journey through personal struggles " +
                "and the quest for meaning, presenting a profound narrative on human resilience and the complexity " +
                "of the human mind.");
        txtDescription.setFont(new Font("Arial", Font.PLAIN, 18));
        txtDescription.setEditable(false);
        panelDescription.add(txtDescription);

        btnAboutBook = new JButton("Tentang Buku Ini");
        btnAboutBook.addActionListener(e -> openAboutBookPage());

        panelDescription.setLayout(new BorderLayout());
        panelDescription.add(txtDescription, BorderLayout.CENTER);
        JPanel panelButton = new JPanel();
        panelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelButton.add(btnAboutBook);
        panelDescription.add(panelButton, BorderLayout.SOUTH);

        JPanel panelTabs = new JPanel();
        panelTabs.setLayout(new BoxLayout(panelTabs, BoxLayout.Y_AXIS));
        panelTabs.add(panelTitle);
        panelTabs.add(panelAuthorCategory);
        panelTabs.add(panelDescription);

        add(panelTabs, BorderLayout.CENTER);

        setVisible(true);
    }

    private void openAboutBookPage() {
        JFrame aboutPage = new JFrame("About This Book");
        aboutPage.setSize(400, 500);
        aboutPage.setLocationRelativeTo(this);
        aboutPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelAboutBook = new JPanel();
        panelAboutBook.setLayout(new BorderLayout());
        panelAboutBook.setBorder(new TitledBorder("Tentang Buku Ini"));

        JLabel lblAboutBook = new JLabel("<html>This book dives into the lives of individuals facing the hardships of life, " +
                "exploring themes of drama, adventure, and psychology. It's a journey through personal struggles " +
                "and the quest for meaning, presenting a profound narrative on human resilience and the complexity " +
                "of the human mind.</html>");
        lblAboutBook.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font lebih kecil (18)
        lblAboutBook.setVerticalAlignment(SwingConstants.TOP); // Align the text to top for better layout

        lblAboutBook.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelAboutBook.add(lblAboutBook, BorderLayout.CENTER);

        JPanel panelBookDetails = new JPanel();
        panelBookDetails.setLayout(new BoxLayout(panelBookDetails, BoxLayout.Y_AXIS)); // Vertikal Layout
        panelBookDetails.setBorder(new TitledBorder("Detail Buku"));

        JLabel lblLanguage = new JLabel("Language : Indonesia");
        JLabel lblAuthor = new JLabel("Author : Antony Gasing");
        JLabel lblPublisher = new JLabel("Publisher : Man United");
        JLabel lblPublishedOn = new JLabel("Published on : 16-9-2024");
        JLabel lblPages = new JLabel("Pages : 165 pages");
        JLabel lblGenres = new JLabel("Genres : Drama, Adventure, Psychology");

        Font smallerFont = new Font("Arial", Font.PLAIN, 20);
        lblLanguage.setFont(smallerFont);
        lblAuthor.setFont(smallerFont);
        lblPublisher.setFont(smallerFont);
        lblPublishedOn.setFont(smallerFont);
        lblPages.setFont(smallerFont);
        lblGenres.setFont(smallerFont);

        lblLanguage.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));  // Spasi bawah
        lblAuthor.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Spasi atas & bawah
        lblPublisher.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Spasi atas & bawah
        lblPublishedOn.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Spasi atas & bawah
        lblPages.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Spasi atas & bawah
        lblGenres.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));  // Spasi atas

        panelBookDetails.add(lblLanguage);
        panelBookDetails.add(lblAuthor);
        panelBookDetails.add(lblPublisher);
        panelBookDetails.add(lblPublishedOn);
        panelBookDetails.add(lblPages);
        panelBookDetails.add(lblGenres);

        JPanel panelBackButton = new JPanel();
        panelBackButton.setLayout(new BorderLayout());

        JButton btnBack = new JButton("Back to Main Page");
        btnBack.setFont(new Font("Arial", Font.PLAIN, 16));
        btnBack.addActionListener(e -> {
            aboutPage.dispose();
        });

        panelBackButton.add(btnBack, BorderLayout.EAST);

        aboutPage.setLayout(new BorderLayout());
        aboutPage.add(panelAboutBook, BorderLayout.NORTH);
        aboutPage.add(panelBookDetails, BorderLayout.CENTER);
        aboutPage.add(panelBackButton, BorderLayout.SOUTH);

        aboutPage.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookDetail());
    }
}