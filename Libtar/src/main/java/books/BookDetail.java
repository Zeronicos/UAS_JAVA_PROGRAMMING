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

        // Panel untuk menampilkan gambar buku (setengah halaman atas)
        lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        JPanel panelImage = new JPanel();
        panelImage.setLayout(new BorderLayout());
        panelImage.setPreferredSize(new Dimension(500, 300));
        panelImage.add(lblImage, BorderLayout.CENTER);
        add(panelImage, BorderLayout.NORTH);

        // Panel untuk judul buku
        JPanel panelTitle = new JPanel();
        panelTitle.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align text to left
        panelTitle.setBorder(new TitledBorder("Title"));
        lblTitle = new JLabel("Nowhere to Go"); // Judul Buku
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panelTitle.add(lblTitle);

        // Panel untuk Author
        JPanel panelAuthor = new JPanel();
        panelAuthor.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align text to left
        panelAuthor.setBorder(new TitledBorder("Author"));
        JLabel lblAuthor = new JLabel("Antony Gasing"); // Nama Author
        lblAuthor.setFont(new Font("Arial", Font.PLAIN, 16));
        panelAuthor.add(lblAuthor);

        // Panel untuk Category
        JPanel panelCategory = new JPanel();
        panelCategory.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align text to left
        panelCategory.setBorder(new TitledBorder("Category"));
        JLabel lblCategory = new JLabel("Drama, Adventure, Psychology"); // Kategori Buku
        lblCategory.setFont(new Font("Arial", Font.PLAIN, 16));
        panelCategory.add(lblCategory);

        // Panel bawah untuk Author dan Category
        JPanel panelAuthorCategory = new JPanel();
        panelAuthorCategory.setLayout(new BoxLayout(panelAuthorCategory, BoxLayout.Y_AXIS));
        panelAuthorCategory.add(panelAuthor); // Add Author panel
        panelAuthorCategory.add(panelCategory); // Add Category panel

        // Panel untuk Deskripsi Buku
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
        txtDescription.setFont(new Font("Arial", Font.PLAIN, 18)); // Set larger font size
        txtDescription.setEditable(false); // Disable editing
        panelDescription.add(txtDescription);

        // Tombol untuk membuka halaman baru, di bawah kanan panel deskripsi
        btnAboutBook = new JButton("Tentang Buku Ini");
        btnAboutBook.addActionListener(e -> openAboutBookPage());

        // Set layout untuk panel deskripsi dan tombol
        panelDescription.setLayout(new BorderLayout());
        panelDescription.add(txtDescription, BorderLayout.CENTER);
        JPanel panelButton = new JPanel();
        panelButton.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Posisikan tombol ke kanan
        panelButton.add(btnAboutBook);
        panelDescription.add(panelButton, BorderLayout.SOUTH);

        // Panel utama untuk menggabungkan semua panel
        JPanel panelTabs = new JPanel();
        panelTabs.setLayout(new BoxLayout(panelTabs, BoxLayout.Y_AXIS));
        panelTabs.add(panelTitle);                // Tambahkan panel judul
        panelTabs.add(panelAuthorCategory);       // Tambahkan panel Author dan Category
        panelTabs.add(panelDescription);          // Tambahkan panel Deskripsi

        add(panelTabs, BorderLayout.CENTER);

        setVisible(true);
    }

    private void openAboutBookPage() {
        // Membuka halaman baru (JFrame penuh) dengan informasi lebih lanjut tentang buku
        JFrame aboutPage = new JFrame("About This Book");
        aboutPage.setSize(400, 500);
        aboutPage.setLocationRelativeTo(this); // Menempatkan halaman baru di tengah layar
        aboutPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Menutup halaman baru saat ditutup

        // Panel pertama: Tentang Buku Ini
        JPanel panelAboutBook = new JPanel();
        panelAboutBook.setLayout(new BorderLayout());
        panelAboutBook.setBorder(new TitledBorder("Tentang Buku Ini"));

        // Menggunakan JLabel untuk menampilkan deskripsi buku
        JLabel lblAboutBook = new JLabel("<html>This book dives into the lives of individuals facing the hardships of life, " +
                "exploring themes of drama, adventure, and psychology. It's a journey through personal struggles " +
                "and the quest for meaning, presenting a profound narrative on human resilience and the complexity " +
                "of the human mind.</html>");
        lblAboutBook.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font lebih kecil (18)
        lblAboutBook.setVerticalAlignment(SwingConstants.TOP); // Align the text to top for better layout

        // Menambahkan padding agar tidak terlalu rapat dengan border
        lblAboutBook.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelAboutBook.add(lblAboutBook, BorderLayout.CENTER);

        // Panel kedua: Detail Buku
        JPanel panelBookDetails = new JPanel();
        panelBookDetails.setLayout(new BoxLayout(panelBookDetails, BoxLayout.Y_AXIS)); // Vertikal Layout
        panelBookDetails.setBorder(new TitledBorder("Detail Buku"));

        // Informasi buku
        JLabel lblLanguage = new JLabel("Language : Indonesia");
        JLabel lblAuthor = new JLabel("Author : Antony Gasing");
        JLabel lblPublisher = new JLabel("Publisher : Man United");
        JLabel lblPublishedOn = new JLabel("Published on : 16-9-2024");
        JLabel lblPages = new JLabel("Pages : 165 pages");
        JLabel lblGenres = new JLabel("Genres : Drama, Adventure, Psychology");

        // Mengatur font sedikit lebih kecil pada setiap JLabel (font size 20)
        Font smallerFont = new Font("Arial", Font.PLAIN, 20); // Font lebih kecil (20)
        lblLanguage.setFont(smallerFont);
        lblAuthor.setFont(smallerFont);
        lblPublisher.setFont(smallerFont);
        lblPublishedOn.setFont(smallerFont);
        lblPages.setFont(smallerFont);
        lblGenres.setFont(smallerFont);

        // Menambahkan padding (spasi kosong) antara setiap label
        lblLanguage.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));  // Spasi bawah
        lblAuthor.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Spasi atas & bawah
        lblPublisher.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Spasi atas & bawah
        lblPublishedOn.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Spasi atas & bawah
        lblPages.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Spasi atas & bawah
        lblGenres.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));  // Spasi atas

        // Menambahkan komponen ke panelBookDetails
        panelBookDetails.add(lblLanguage);
        panelBookDetails.add(lblAuthor);
        panelBookDetails.add(lblPublisher);
        panelBookDetails.add(lblPublishedOn);
        panelBookDetails.add(lblPages);
        panelBookDetails.add(lblGenres);

        // Panel untuk tombol "Back" di bagian bawah
        JPanel panelBackButton = new JPanel();
        panelBackButton.setLayout(new BorderLayout());

        // Tombol Back
        JButton btnBack = new JButton("Back to Main Page");
        btnBack.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font untuk tombol
        btnBack.addActionListener(e -> {
            // Fungsi tombol untuk kembali ke halaman utama atau menutup jendela
            aboutPage.dispose(); // Menutup halaman ini dan kembali ke halaman utama
        });

        // Menambahkan tombol ke panelBackButton
        panelBackButton.add(btnBack, BorderLayout.EAST); // Posisi kanan bawah

        // Menambahkan panelAboutBook, panelBookDetails dan panelBackButton ke aboutPage
        aboutPage.setLayout(new BorderLayout());
        aboutPage.add(panelAboutBook, BorderLayout.NORTH);
        aboutPage.add(panelBookDetails, BorderLayout.CENTER);
        aboutPage.add(panelBackButton, BorderLayout.SOUTH); // Menambahkan panelBackButton di bawah

        aboutPage.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookDetail());
    }
}